package org.almibe.naps

import groovy.json.JsonSlurper
import groovy.text.GStringTemplateEngine
import org.asciidoctor.Asciidoctor
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.util.resource.Resource
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

import java.nio.file.Files
import java.nio.file.Path

class NapsPluginExtension {
    String contentsIn = "src/naps/content/"
    String templatesIn = "src/naps/templates/"
    String siteOut = "build/naps/site/"

    String defaultTemplate = "default.html"

    String asciiDocExtension = "adoc"

    String directoryDefaultsFile = "directory.default.json"
}

class NapsPlugin implements Plugin<Project> {

    Asciidoctor asciidoctor = Asciidoctor.Factory.create()
    GStringTemplateEngine templateEngine = new GStringTemplateEngine()
    JsonSlurper jsonSlurper = new JsonSlurper()

    @Override
    void apply(Project project) {

        project.extensions.create("naps", NapsPluginExtension)

        project.task("naps", type:Copy) {
            from "${project.naps.contentsIn}"
            into "${project.naps.siteOut}"

            eachFile {
                def sourceFile = project.file("${project.naps.contentsIn}${it.sourcePath}")
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

        project.task("startDev", dependsOn: 'naps') {
            doLast {
                //TODO start static file server and show URL
                //TODO start monitoring files and represess when they are changed
                Server server = new Server()
                ServerConnector connector = new ServerConnector(server)
                connector.setPort(8090)
                server.addConnector(connector)

                ResourceHandler resourceHandler = new ResourceHandler();

                ContextHandler contextHandler = new ContextHandler()
                contextHandler.setContextPath("/")
                File outputDirectory = project.file(project.naps.siteOut)
                contextHandler.setBaseResource(Resource.newResource(outputDirectory))
                contextHandler.setHandler(resourceHandler)

                ContextHandlerCollection contexts = new ContextHandlerCollection()
                contexts.addHandler(contextHandler)
                server.setHandler(contexts)

                server.start()
                server.join()
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
