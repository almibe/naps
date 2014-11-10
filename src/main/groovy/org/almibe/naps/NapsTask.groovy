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
        def finalTemplate = contentGroup.template?.trim() ?: project.extensions.naps.defaultTemplate
        if (contentGroup.mainContent instanceof MainContent) {
            NapsTemplateHashModel napsTemplateHashModel = new NapsTemplateHashModel(contentGroup.mainContent, contentGroup.groupDataModel, project.extensions.naps.globalDataModel)
            templateProcessor.processTemplate(finalTemplate, napsTemplateHashModel, getFinalLocationFile(contentGroup.mainContent.finalLocation))
        } else if (contentGroup.mainContent instanceof List<MainContent>) {
            ((List<MainContent>)contentGroup.mainContent).forEach { MainContent currentMainContent ->
                NapsTemplateHashModel napsTemplateHashModel = new NapsTemplateHashModel(currentMainContent, contentGroup.groupDataModel, project.extensions.naps.globalDataModel)
                templateProcessor.processTemplate(finalTemplate, napsTemplateHashModel, getFinalLocationFile(currentMainContent.finalLocation))
            }
        } else {
            throw new RuntimeException("MainContent must be instance of MainContent or List<MainContent> -- $contentGroup.mainContent")
        }
    }

    File getFinalLocationFile(String location) {
        project.file("$project.buildDir/$project.extensions.naps.siteOut/$location");
    }
}
