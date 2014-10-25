package org.almibe.naps.template

import freemarker.template.SimpleScalar
import freemarker.template.TemplateHashModel
import freemarker.template.TemplateModel
import freemarker.template.TemplateModelException
import org.almibe.naps.ContentGroupProcessor
import org.almibe.naps.MarkdownProcessor

/**
 * Created by alex on 10/25/14.
 */
class NapsTemplateHashModel implements TemplateHashModel {
    ContentGroupProcessor napsHandler
    def globalFragments
    def globalVariables
    Properties properties
    MarkdownProcessor markdownProcessor

    public NapsTemplateHashModel(ContentGroupProcessor handler, def globalVariables, def globalFragments, Properties properties, MarkdownProcessor markdownProcessor) {
        napsHandler = handler
        this.globalVariables = globalVariables
        this.globalFragments = globalFragments
        this.properties = properties
        this.markdownProcessor = markdownProcessor
    }

    @Override
    TemplateModel get(String key) throws TemplateModelException {
        String returnValue = '';
        if (false) {
            //TODO support computedContent?
        } else if (key == 'mainContent' && napsHandler.mainContent?.trim()) {
            returnValue = processFragment(napsHandler.mainContent)
        } else if (properties?.containsKey(key)) {
            returnValue = properties.get(key)
        } else if (napsHandler?.fragments?.containsKey(key)) {
            returnValue = processFragment(napsHandler.fragments.get(key))
        } else if (napsHandler?.variables?.containsKey(key)) {
            returnValue = napsHandler.variables[key]
        } else if (globalFragments?.containsKey(key)) {
            returnValue = processFragment(globalFragments.get(key))
        } else if (globalVariables?.containsKey(key)) {
            returnValue = globalVariables[key]
        } else {
            throw new RuntimeException("Value not found: $key")
        }
        return new SimpleScalar(returnValue)
    }

    String processFragment(String fragment) {
        return markdownProcessor.process(fragment)
    }

    @Override
    boolean isEmpty() throws TemplateModelException {
        return false
    }
}
