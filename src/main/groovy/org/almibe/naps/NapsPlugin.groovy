package org.almibe.naps

import groovy.json.JsonSlurper
import groovy.text.GStringTemplateEngine
import org.asciidoctor.Asciidoctor
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

import java.nio.file.Files
import java.nio.file.Path

class NapsPluginExtension {
    String contentsIn = "src/naps/content/"
    String fragmentsIn = "src/naps/fragments/"
    String templatesIn = "src/naps/templates/"
    String siteOut = "build/naps/site/"

    String defaultTemplate = "default.html"
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
                if (it.name.endsWith('.json')) { //exclude .json files if there is a .txt file with it's same name
                    if (sourceFile.toPath().resolveSibling(it.name + ".txt") != null) {
                        it.exclude()
                    }
                }
                if (it.name.endsWith('.txt')) {
                    if (Files.exists(sourceFile.toPath().resolveSibling(trimExtension(it.name) + ".html"))) {
                        throw new RuntimeException("${it.name} and ${trimExtension(it.name) + ".html"} can't both exist in source dir.")
                    }
                    it.exclude() //don't export this file but do create it's converted output
                    Path jsonFile = sourceFile.toPath().resolveSibling(trimExtension(it.name) + ".json")
                    def jsonConfig = Files.exists(jsonFile) ? jsonSlurper.parse(jsonFile.toFile()) : [:]
                    def content = asciidoctor.convert(sourceFile.text, [:])
                    def templateFileName = jsonConfig.template != null ?
                            jsonConfig.template : project.naps.defaultTemplate
                    String templateFileLocation = "${project.naps.templatesIn}$templateFileName"
                    try {
                        File templateFile = project.file(templateFileLocation)
                        jsonConfig.content = content
                        jsonConfig.fragments = { String fragmentName ->
                            return project.file("${project.naps.fragmentsIn}/$fragmentName").text
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
