package org.almibe.naps.template

import freemarker.template.SimpleScalar
import freemarker.template.TemplateHashModel
import freemarker.template.TemplateModel
import freemarker.template.TemplateModelException
import org.almibe.naps.ContentGroupProcessor

/**
 * Created by alex on 10/25/14.
 */
class NapsTemplateHashModel implements TemplateHashModel {
    ContentGroupProcessor napsHandler
    def globalFragments
    def globalVariables
    Properties properties

    public NapsTemplateHashModel(ContentGroupProcessor handler, def globalVariables, def globalFragments, Properties properties) {
        napsHandler = handler
        this.globalVariables = globalVariables
        this.globalFragments = globalFragments
        this.properties = properties
    }

    @Override
    TemplateModel get(String key) throws TemplateModelException {
        String returnValue = '';
        if (false) {
            //TODO support computedContent
        } else if (key == 'mainContent' && napsHandler.mainContent?.trim()) {
            returnValue = napsHandler.mainContent.trim()
        } else if (properties?.containsKey(key)) {
            returnValue = properties.get(key)
        } else if (napsHandler?.fragments.containsKey(key)) {
            //TODO complete
        } else if (napsHandler?.variables.containsKey(key)) {
            returnValue = napsHandler.variables[key]
        } else if (globalFragments?.containsKey(key)) {
            //TODO suppport fragments
        } else if (globalVariables?.containsKey(key)) {
            returnValue = globalVariables[key]
        } else {
            throw new RuntimeException("Value not found: $key")
        }
        return new SimpleScalar(returnValue)
    }

    @Override
    boolean isEmpty() throws TemplateModelException {
        return false
    }
}
