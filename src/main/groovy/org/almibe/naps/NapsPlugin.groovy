package org.almibe.naps

import groovy.text.GStringTemplateEngine
import org.asciidoctor.Asciidoctor
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

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

    @Override
    void apply(Project project) {

        project.extensions.create("naps", NapsPluginExtension)

        project.task("naps", type:Copy) {
            from "${project.naps.contentsIn}"
            into "${project.buildDir}/${project.naps.siteOut}"

            eachFile {
                if (it.name.endsWith('.json')) { //exclude .json files if there is a .txt file with it's same name
                    if (it.file.path.resolveSibling(it.name.trim(5) + ".txt") != null) {
                        it.exclude()
                    }
                }
                if (it.name.endsWith('.txt')) {
                    if (it.file.path.resolveSibling(it.name.trim(4) + ".html") != null) {
                        throw new RuntimeException("${it.name} and ${it.name.trim(4) + ".html"} can't both exist in source dir.")
                    }
                    it.exclude() //don't export this file but do create it's converted output
                    def jsonConfig = [:] //TODO read in json file if it exists
                    def content = asciidoctor.convert(it.file.txt, [:])
                    def templateFileName = project.naps.defaultTemplate
                    project.naps.templateDefinitions.find { templateDefinition ->
                        if (templateDefinition.matches()) {
                            templateFileName = templateDefinition.templateName
                            return true
                        } else {
                            return false
                        }
                    }
                    //TODO run them through the configured groovy template
                    File templateFile = file(templateFileName)
                    def template = templateEngine.createTemplate(templateFile).make([content: content])
                    //TODO write template.toString() to the output dir as an html file
                }
            }
        }
    }
}
