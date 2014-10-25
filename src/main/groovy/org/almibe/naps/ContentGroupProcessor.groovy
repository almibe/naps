package org.almibe.naps

import org.almibe.naps.template.NapsTemplateHashModel
import org.almibe.naps.template.TemplateProcessor
import org.gradle.api.Project

class ContentGroupProcessor {
    String name
    def mainContent
    String template
    def variables = [:]
    def fragments = [:]

    ContentGroupProcessor(String name) {
        this.name = name
    }

    //TODO maybe move this to NapsTask?
    def process(TemplateProcessor templateProcessor, NapsExtension napsExtension, Project project, MarkdownProcessor markdownProcessor) {
        NapsTemplateHashModel napsTemplateHashModel = new NapsTemplateHashModel(this, project.naps.globalVariables, project.naps.globalFragments, null, markdownProcessor) //TODO replace last nulls with properties file and template processor
        def finalTemplate = template?.trim() ?: napsExtension.defaultTemplate
        if (mainContent instanceof String) {
            templateProcessor.processTemplate(finalTemplate, napsTemplateHashModel, project.file("$project.buildDir/$napsExtension.siteOut/$mainContent"))
        } else {
            ((List<String>)mainContent).forEach {
                templateProcessor.processTemplate(finalTemplate, napsTemplateHashModel, project.file("$project.buildDir/$napsExtension.siteOut/$mainContent"))
            }
        }
    }
}
