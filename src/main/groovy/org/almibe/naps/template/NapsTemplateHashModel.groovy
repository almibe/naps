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

    public NapsTemplateHashModel(ContentGroupProcessor handler) {
        napsHandler = handler
    }

    @Override
    TemplateModel get(String key) throws TemplateModelException {
        String returnValue = '';
        if (false) {
            //TODO support computedContent
        } else if (key == 'mainContent' && napsHandler.mainContent?.trim()) {
            returnValue = napsHandler.mainContent.trim()
        } else if (false) {
            //TODO support properties files
        } else if (napsHandler.fragments.containsKey(key)) {
            //TODO complete
        } else if (napsHandler.variables.containsKey(key)) {
            returnValue = napsHandler.variables[key]
        } else if (globalFragments.containsKey(key)) {
            //TODO suppport fragments
        } else if (globalVariables.containsKey(key)) {
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