package org.almibe.naps

import org.almibe.naps.template.NapsTemplateHashModel
import org.almibe.naps.template.TemplateProcessor
import org.gradle.api.Project

class ContentGroupProcessor {
    String name
    MainContent mainContent
    String template
    def groupDataModel = [:]

    ContentGroupProcessor(String name) {
        this.name = name
    }

    //TODO maybe move this to NapsTask?
    def process(TemplateProcessor templateProcessor, NapsExtension napsExtension, Project project, MarkdownProcessor markdownProcessor) {
        NapsTemplateHashModel napsTemplateHashModel = new NapsTemplateHashModel(this, project.naps.globalDataModel)
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
