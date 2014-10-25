package org.almibe.naps

import org.almibe.naps.template.TemplateProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class NapsTask extends DefaultTask {
    TemplateProcessor templateProcessor = new TemplateProcessor(project.file("$project.extensions.naps.templatesIn"))

    @TaskAction
    def naps() {
        NapsExtension napsExtension = project.extensions.naps
        MarkdownProcessor markdownProcessor = new MarkdownProcessor(project.naps.fragmentsIn)

        for(ContentGroupProcessor contentGroup : project.extensions.naps.contentGroups) {
            contentGroup.process(templateProcessor, napsExtension, project, markdownProcessor)
        }
    }
}
