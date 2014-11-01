package org.almibe.naps

import org.almibe.naps.maincontent.MainContent
import org.almibe.naps.template.NapsTemplateHashModel
import org.almibe.naps.template.TemplateProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class NapsTask extends DefaultTask {
    TemplateProcessor templateProcessor = new TemplateProcessor(project.file("$project.extensions.naps.templatesIn"))

    @TaskAction
    def naps() {
        for(ContentGroup contentGroup : project.extensions.naps.contentGroups) {
            process(contentGroup)
        }
    }

    def process(ContentGroup contentGroup) {
        NapsTemplateHashModel napsTemplateHashModel = new NapsTemplateHashModel(contentGroup, project.extensions.naps.globalDataModel)
        def finalTemplate = contentGroup.template?.trim() ?: project.extensions.naps.defaultTemplate
        if (contentGroup.mainContent instanceof String) {
            templateProcessor.processTemplate(finalTemplate, napsTemplateHashModel, project.file("$project.buildDir/$project.extensions.naps.siteOut/$contentGroup.mainContent.finalLocation"))
        } else {
            ((List<MainContent>)mainContent).forEach { MainContent currentMainContent ->
                templateProcessor.processTemplate(finalTemplate, napsTemplateHashModel, project.file("$project.buildDir/$project.extensions.naps.siteOut/$currentMainContent.finalLocation"))
            }
        }
    }
}
