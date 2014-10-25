package org.almibe.naps

import org.almibe.naps.template.NapsTemplateHashModel
import org.almibe.naps.template.TemplateProcessor

class ContentGroupProcessor {
    String name
    def mainContent
    String template
    def variables = [:]
    def fragments = [:]

    ContentGroupProcessor(String name) {
        this.name = name
    }

    def process(TemplateProcessor templateProcessor, NapsExtension napsExtension) {
        def finalTemplate = template?.trim() ?: napsExtension.defaultTemplate
        templateProcessor.processTemplate(finalTemplate, new NapsTemplateHashModel(this), "$outputLocation/${computeFileLocation(contentGroup)}${computeFileName(contentGroup)}")
    }

}
