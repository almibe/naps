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

                //TODO in background every 4 seconds walk through content + template dirs and check for recent
                //TODO   file changes
                Thread.start {
                    while(server.isRunning()) {
                        copyFiles(project)
                        this.sleep(4000)
                    }
                }
                server.join()
            }
        }
    }

    def copyFiles(Project project) {
        project.copy {
            from "${project.naps.contentsIn}"
            into "${project.naps.siteOut}"

            eachFile {
                final Long timeLastProcessed = fileLastProcessed[it.sourcePath] ?: 0
                final File sourceFile = project.file("${project.naps.contentsIn}${it.sourcePath}")
                fileLastProcessed[it.sourcePath] == System.currentTimeMillis()

                if (sourceFile.lastModified() < timeLastProcessed) { //do nothing if file hasn't been updated
                    it.exclude()
                }

                if (it.name == project.naps.directoryDefaultsFile) { //exclude directory default config files
                    it.exclude()
                }
                if (it.name.endsWith('.json')) { //exclude .json files if there is an asciidoc file with it's same name
                    if (sourceFile.toPath().resolveSibling("${it.name}.${project.naps.asciiDocExtension}") != null) {
                        it.exclude()
                    }
                }
                if (it.name.endsWith(".${project.naps.asciiDocExtension}")) {
                    if (Files.exists(sourceFile.toPath().resolveSibling(trimExtension(it.name) + ".html"))) {
                        throw new RuntimeException("${it.name} and ${trimExtension(it.name) + ".html"} can't both exist in source dir.")
                    }
                    it.exclude() //don't export this file but do create it's converted output
                    Path jsonFile = sourceFile.toPath().resolveSibling(trimExtension(it.name) + ".json")
                    Path directoryConfigFile = sourceFile.toPath().resolveSibling(project.naps.directoryDefaultsFile)
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
                        assert (resultFile.createNewFile() == true)
                        resultFile.text = template.toString()
                    } catch (Exception ex) {
                        throw new RuntimeException("Error processing template ${templateFileLocation}", ex)
                    }
                }
            }
        }
    }

    String trimExtension(String fileName) {
        if (fileName.contains('.')) {
            return fileName.substring(0, fileName.lastIndexOf('.'))
        } else {
            return fileName
        }
    }
}
