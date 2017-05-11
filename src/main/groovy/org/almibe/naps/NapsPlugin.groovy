package org.almibe.naps

import groovy.json.JsonSlurper
import groovy.text.GStringTemplateEngine
import org.asciidoctor.Asciidoctor
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.util.resource.Resource
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.Files
import java.nio.file.Path

class NapsPluginExtension {
    String contentsIn = "src/naps/content/"
    String templatesIn = "src/naps/templates/"
    String siteOut = "build/naps/site/"
    String defaultTemplate = "default.html"
    String asciiDocExtension = "adoc"
    String directoryDefaultsFile = "directory.default.json"
    int devPort = 8090
    long devTimeout = 4000
}

class NapsPlugin implements Plugin<Project> {
    final Asciidoctor asciidoctor = Asciidoctor.Factory.create()
    final GStringTemplateEngine templateEngine = new GStringTemplateEngine()
    final JsonSlurper jsonSlurper = new JsonSlurper()
    final Map<String, Long> fileLastProcessed = new HashMap<>()

    @Override
    void apply(Project project) {

        project.extensions.create("naps", NapsPluginExtension)

        project.task("naps") {
            doLast {
                copyFiles(project)
            }
        }

        project.task("startDev", dependsOn: 'naps') {
            doLast {
                final Server server = new Server()
                final ServerConnector connector = new ServerConnector(server)
                connector.port = project.naps.devPort
                server.addConnector(connector)

                final ResourceHandler resourceHandler = new ResourceHandler();

                final ContextHandler contextHandler = new ContextHandler()
                contextHandler.contextPath = "/"
                final File outputDirectory = project.file(project.naps.siteOut)
                contextHandler.baseResource = Resource.newResource(outputDirectory)
                contextHandler.handler = resourceHandler

                server.setHandler(contextHandler)

                server.start()
                println("\n\nStarted http://localhost:${project.naps.devPort}\n\n")

                Thread.start {
                    while(server.isRunning()) {
                        println "${new Date()} - Checking for updates."
                        try {
                            copyFiles(project)
                            this.sleep(project.naps.devTimeout)
                        } catch (Exception ex) {
                            ex.printStackTrace()
                        }
                    }
                }
                server.join()
            }
        }
    }

    /**
     * loop over all normal files and check status
     * ignore all template and metadata files
     * loop over all content files and check if they need redone (check metadata and template files now) -- probably better
     */
    def copyFiles(Project project) {
        project.copy {
            from "${project.naps.contentsIn}"
            into "${project.naps.siteOut}"
            exclude "**/${project.naps.directoryDefaultsFile}"
            exclude "**/*.${project.naps.asciiDocExtension}.json"

            eachFile {
                final Long timeLastProcessed = fileLastProcessed[it.sourcePath] ?: 0
                final File sourceFile = project.file("${project.naps.contentsIn}${it.sourcePath}")
                fileLastProcessed[it.sourcePath] = System.currentTimeMillis()

                if (it.name.endsWith(".${project.naps.asciiDocExtension}")) {
                    if (Files.exists(sourceFile.toPath().resolveSibling(trimExtension(it.name) + ".html"))) {
                        throw new RuntimeException("${it.name} and ${trimExtension(it.name) + ".html"} can't both exist in source dir.")
                    }
                    it.exclude() //don't export this file but do create it's converted output if needed
                    //check if this file needs to be processed or not
                    def updateContent = false
                    //has the file itself changed?
                    updateContent = updateContent || (sourceFile.lastModified() >= timeLastProcessed)
                    //has its metadata file changed?
                    Path jsonFile = sourceFile.toPath().resolveSibling("${it.name}.json")
                    if (jsonFile.toFile().exists()) {
                        final Long jsonFileTimeLastProcessed = fileLastProcessed["${it.sourcePath}.json"] ?: 0
                        fileLastProcessed["${it.sourcePath}.json"] = System.currentTimeMillis()
                        updateContent = updateContent || (jsonFile.toFile().lastModified() >= jsonFileTimeLastProcessed)
                    }
                    //has its directory default metadata file changed?
                    Path directoryConfigFile = sourceFile.toPath().resolveSibling("${project.naps.directoryDefaultsFile}")
                    if (directoryConfigFile.toFile().exists()) {
                        final Long directoryConfigFileLastProcess = fileLastProcessed[directoryConfigFile.toFile().absolutePath] ?: 0
                        fileLastProcessed[directoryConfigFile.toFile().absolutePath] = System.currentTimeMillis()
                        updateContent = updateContent || (jsonFile.toFile().lastModified() >= directoryConfigFileLastProcess)
                    }
                    //has any template changed? (just redoing all files for now when a template changes since templates can nest -- eventually this could be done with more sophistication)
                    updateContent = updateContent || templatesUpdatedSince(project, timeLastProcessed)
                    if (!updateContent) {
                        return
                    }
                    def directoryConfig = (Files.exists(directoryConfigFile) ? jsonSlurper.parse(directoryConfigFile.toFile()) : [:])
                    def jsonConfig = directoryConfig + (Files.exists(jsonFile) ? jsonSlurper.parse(jsonFile.toFile()) : [:])
                    def content = asciidoctor.convert(sourceFile.text, [:])
                    def templateFileName = jsonConfig.template != null ?
                            jsonConfig.template : project.naps.defaultTemplate
                    String templateFileLocation = "${project.naps.templatesIn}$templateFileName"
                    try {
                        File templateFile = project.file(templateFileLocation)
                        jsonConfig.content = content
                        jsonConfig.templates = { String templateName ->
                            File innerTemplateFile = project.file("${project.naps.templatesIn}/$templateName")
                            def innerTemplate = templateEngine.createTemplate(innerTemplateFile).make(jsonConfig)
                            return innerTemplate.toString()
                        }
                        def template = templateEngine.createTemplate(templateFile).make(jsonConfig)
                        def resultFileName = trimExtension(it.sourcePath) + ".html"
                        def resultFile = project.file("${project.naps.siteOut}${resultFileName}")
                        if (!resultFile.parentFile.exists()) {
                            resultFile.parentFile.mkdirs()
                        }
                        if (!resultFile.exists()) {
                            resultFile.createNewFile()
                        }
                        resultFile.text = template.toString()
                    } catch (Exception ex) {
                        throw new RuntimeException("Error processing template ${templateFileLocation}", ex)
                    }
                }
                else if (sourceFile.lastModified() < timeLastProcessed) { //do nothing if file hasn't been updated
                    it.exclude()
                    return
                }
                println "${new Date()} - Outputting ${it.sourcePath}"
            }
        }
    }

    boolean templatesUpdatedSince(Project project, long timeMillis) {
        project.files("$project.naps.templatesIn").forEach { templateFile ->
            if (templateFile.isFile() && templateFile.lastModified() >= timeMillis) {
                return true
            }
        }
        return false
    }

    String trimExtension(String fileName) {
        if (fileName.contains('.')) {
            return fileName.substring(0, fileName.lastIndexOf('.'))
        } else {
            return fileName
        }
    }
}
