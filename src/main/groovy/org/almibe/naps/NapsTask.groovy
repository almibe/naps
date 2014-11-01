package org.almibe.naps

import org.almibe.naps.template.NapsTemplateHashModel
import org.almibe.naps.template.TemplateProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class NapsTask extends DefaultTask {
    TemplateProcessor templateProcessor = new TemplateProcessor(project.file("$project.extensions.naps.templatesIn"))

    @TaskAction
    def naps() {
        NapsExtension napsExtension = project.extensions.naps

        for(ContentGroup contentGroup : project.extensions.naps.contentGroups) {
            process(contentGroup)
        }
    }

    def process(ContentGroup contentGroup) {
        NapsTemplateHashModel napsTemplateHashModel = new NapsTemplateHashModel(this, napsExtension.globalDataModel)
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
