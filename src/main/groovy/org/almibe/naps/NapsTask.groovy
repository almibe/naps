package org.almibe.naps

import org.almibe.naps.template.TemplateProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class NapsTask extends DefaultTask {
    TemplateProcessor templateProcessor = new TemplateProcessor(project.file("$project.extensions.naps.templatesIn"))

    final String outputLocation = "$project.buildDir/$project.naps.siteOut"

    @TaskAction
    def naps() {
        NapsExtension napsExtension = project.extensions.naps

        for(ContentGroupProcessor contentGroup : project.extensions.naps.contentGroups) {
            contentGroup.process(templateProcessor, napsExtension, project)
        }
    }
}
