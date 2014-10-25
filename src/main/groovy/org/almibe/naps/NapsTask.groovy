package org.almibe.naps

import org.almibe.naps.ContentGroupProcessor
import org.almibe.naps.template.TemplateProcessor
import org.almibe.naps.template.NapsTemplateHashModel
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class NapsTask extends DefaultTask {
    TemplateProcessor templateProcessor = new TemplateProcessor()

    final String outputLocation = "$project.buildDir/$project.naps.siteOut"
    String defaultTemplate
    def globalVariables
    def globalFragments

    @TaskAction
    def naps() {
        defaultTemplate = project.extensions.naps.defaultTemplate
        globalVariables = project.extensions.naps.globalVariables
        globalFragments = project.extensions.naps.globalFragments

        for(ContentGroupProcessor contentGroup : project.extensions.naps.contentGroups) {
            def finalTemplate = contentGroup.template?.trim() ?: defaultTemplate
            templateProcessor.processTemplate(finalTemplate, new NapsTemplateHashModel(contentGroup), "$outputLocation/${computeFileLocation(contentGroup)}${computeFileName(contentGroup)}")
        }
    }

    /**
     *
     */
    String computeFileLocation(ContentGroupProcessor handler) {
        if(handler.finalLocation instanceof String && handler.finalLocation.trim()) {
            return "$handler.finalLocation/"
        }
    }

    /**
     * Creates the name for the file about to be output.  By default it's just the name of the main content but it can
     * be over written by setting the final name property as either a String or a closure that accepts the mainContent
     * value and returns the name.
     */
    String computeFileName(ContentGroupProcessor handler) {
        if (handler.finalName instanceof String) {
            return handler.finalName
        } else if (handler.finalName instanceof Closure) {
            return handler.finalName(handler.mainContent)
        } else {
            return handler.mainContent
        }
    }

}
