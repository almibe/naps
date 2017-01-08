package org.almibe.naps

import groovy.json.JsonSlurper
import groovy.text.GStringTemplateEngine
import org.asciidoctor.Asciidoctor
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

import java.nio.file.Path

class NapsPluginExtension {
    String contentsIn = "src/naps/contents"
    String fragmentsIn = "src/naps/fragments"
    String templatesIn = "src/naps/templates"
    String siteOut = "naps/site"

    String defaultTemplate = ""
    List<TemplateDefinition> templateDefinitions = []
}

interface TemplateDefinition {
    String templateName
    boolean match(File file)
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
            into "${project.buildDir}/${project.naps.siteOut}"

            eachFile {
                if (it.name.endsWith('.json')) { //exclude .json files if there is a .txt file with it's same name
                    if (it.file.path.resolveSibling(it.name + ".txt") != null) {
                        it.exclude()
                    }
                }
                if (it.name.endsWith('.txt')) {
                    if (it.file.path.resolveSibling(trimExtension(it.name) + ".html") != null) {
                        throw new RuntimeException("${it.name} and ${trimExtension(it.name) + ".html"} can't both exist in source dir.")
                    }
                    it.exclude() //don't export this file but do create it's converted output
                    Path jsonFile = it.file.toPath().resolveSibling(it.name + ".json")
                    def jsonConfig = jsonFile == null ? [:] : jsonSlurper.parse(jsonFile.toFile())
                    def content = asciidoctor.convert(file("${it.sourcePath}/${it.sourceName}").text, [:])
                    def templateFileName = project.naps.defaultTemplate
                    project.naps.templateDefinitions.find { templateDefinition ->
                        if (templateDefinition.matches()) {
                            templateFileName = templateDefinition.templateName
                            return true
                        } else {
                            return false
                        }
                    }
                    File templateFile = file(templateFileName)
                    jsonConfig.content = content
                    def template = templateEngine.createTemplate(templateFile).make(jsonConfig)
                    def resultFileName = trimExtension("${it.path}/${it.name}") + ".html"
                    def resultFile = file(resultFileName)
                    resultFile.text = template.toString()
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
