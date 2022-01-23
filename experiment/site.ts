import { naps, readDirectoryMetadata } from './naps'

// import Main from "../layouts/Main.astro"
// import 'blueprint-css/dist/blueprint.css'

// const blogs = Astro.fetchContent("./blog/entries/*.md").sort((b1, b2) => {
//   if (b1.date < b2.date) {
//     return 1
//   } else if (b1.date > b2.date) {
//     return -1
//   } else {
//     return 0
//   }
// }).splice(0,5)

////////////////////////////////////////////////////////////////////////////////

// import SubPage from "../../layouts/SubPage.astro"

// const blogs = Astro.fetchContent("./entries/*.md").sort((b1, b2) => {
//   if (b1.date < b2.date) {
//     return 1
//   } else if (b1.date > b2.date) {
//     return -1
//   } else {
//     return 0
//   }
// })

function getLatestBlogs(): Array<any> {
  const blogData = readDirectoryMetadata("blog/entries")
  console.log("BLOGDATA", blogData)
  return blogData.sort((a, b) => a.data.date.localeCompare(b.data.date)).reverse()
}

naps(
  {file: "index", layout: "Main", data: { blogs: getLatestBlogs() }},
  {file: "blog/index", layout: "Main", data: {}},
  {directory: "blog/entries", layout: "Main", data: {}}
)
