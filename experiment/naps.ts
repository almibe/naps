import * as path from 'path'
import * as fs from 'fs-extra'
import * as ejs from 'ejs'
import { marked } from 'marked';
import {EOL} from 'os'

type FileConfig = {
  file: string
  layout: string
  data: any | (() => any)
}

type DirectoryConfig = {
  directory: string
  recursive: boolean
  layout: string
  data: any | (() => any)
}

type DynamicContent = {
  content: string
  outputFileName: string
}

type DynamicContentConfig = {
  source: DynamicContent | DynamicContent[] | (() => Iterator<DynamicContent>)
  layout: string
  data: any | (() => any)
}

type GeneralConfig = {
  //tbd
}

export function naps(...configs: Array<FileConfig | DirectoryConfig | DynamicContentConfig | GeneralConfig>) {
  fs.rmSync("./temp", { recursive: true, force: true })
  fs.rmSync("./dist", { recursive: true, force: true })
  fs.mkdirSync("./temp")
  fs.mkdirSync("./dist")
  configs.forEach(config => {
    if ('file' in config) {
      handleFileConfig(config)
    } else if ('directory' in config) {
      handleDirectoryConfig(config)
    } else if ('source' in config) {
      handleDynamicContentConfig(config)
    } else {
      throw new Error("Invalid configuration.")
    }
  })
}

function handleFileConfig(config: FileConfig) {
  checkDirectoryExists(config.file)
  const file = fs.readFileSync("./content/" + config.file + ".md", 'utf8')
  const [markdownInput, metadata] = readMetadata(file)
  const data = {...config.data, ...metadata}
  const markdownResult = ejs.render(markdownInput, config.data)
  data.content = marked.parse(markdownResult)
  const layout = fs.readFileSync("./templates/" + config.layout + ".ejs", 'utf8')
  const finalResult = ejs.render(layout, data)
  fs.writeFileSync('./temp/' + config.file + '.html', finalResult)
  //eventually just call handleDynamicContentConfig
}

function handleDirectoryConfig(config: DirectoryConfig) {
  fs.readdir("./content/" + config.directory, function (err, files) {
    if (err) {
      throw err
    } else {
      files.forEach(file => {
        if (file.endsWith(".md") && file != ".md") {
          const fileConfig = { file: config.directory + "/" + file.slice(0,file.length-3), layout: config.layout, data: config.data}
          handleFileConfig(fileConfig)  
        } else {
          console.log("Skipping:", file)
        }
      })
    }
  })
}

function handleDynamicContentConfig(config: DynamicContentConfig) {

}

type Metadata = {
  path: string,
  data: any
}

/**
 * Is pasted a path in the content directory and returns the final path of that file along with any
 * metadata defined in the markdown file.
 * TODO: eventually support recursion
 * @param contentDirectory 
 * @returns 
 */
export function readDirectoryMetadata(contentDirectory: string): Array<Metadata> {
  const result = new Array()
  const files = fs.readdirSync("./content/" + contentDirectory)
  files.forEach(file => {
    const originalFile = "./content/" + contentDirectory + "/" + file
    const tempPath = ("./" + contentDirectory + "/" + file)
    const finalPath = tempPath.slice(0, tempPath.length-3) + ".html"
    console.log(originalFile, tempPath, finalPath)
    console.log("CONTENT:", fs.readFileSync(originalFile, 'utf8'))
    const [content, metadata] = readMetadata(fs.readFileSync(originalFile, 'utf8'))
    console.log("METADATA:", metadata)
    result.push({path: finalPath, data: metadata})
  })
  return result
}

/**
 * This function reads metadata from the top of a markdown file and returns a map of all the metadata.
 * Example
 * ```
 * ---
 * meta: data
 * author: Alex
 * ---
 * Regular doc starts here.
 * ```
 * would return {"meta": "data", "author": "Alex"}
 * TODO: this should probably return a tuple of [string, Map<string, string>] where the first
 * value the input with the metadata trimmed.
 * @param markdown 
 */
function readMetadata(markdown: string): [string, any] {
  const dataResult = {}
  let contentResult = ""
  let lineCount = 0
  const lines = markdown.split(/\r?\n/)

  for (; lineCount < lines.length; lineCount++) {
    if (lineCount === 0 && lines[0] === "---") {
      continue
    } else if (lineCount === 0 && lines[0] !== "---") {
      lineCount++
      break //no metadata
    } else if (lineCount !== 0 && lines[lineCount] === "---") {
      lineCount++
      break //done reading metadata
    } else {
      const values = lines[lineCount].split(":")
      if (values.length === 2) {
        const key = values[0].trim()
        const value = values[1].trim()
        dataResult[key] = value
      } else {
        throw new Error("Invalid metadata line.")
      }
    }
  }
  contentResult = lines.slice(lineCount).join(EOL)

  return [contentResult, dataResult]
}

function checkDirectoryExists(file: string) {
  const dirname = path.dirname(file)
  if (dirname != ".") {
    fs.mkdirsSync("./temp/" + dirname)
  }
}
