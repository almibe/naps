package org.almibe.naps.template

import freemarker.template.SimpleScalar
import freemarker.template.TemplateHashModel
import freemarker.template.TemplateModel
import freemarker.template.TemplateModelException
import org.almibe.naps.ContentGroup

class NapsTemplateHashModel implements TemplateHashModel {
    ContentGroup napsHandler
    def globalDataModel

    public NapsTemplateHashModel(ContentGroup handler, def globalDataModel) {
        napsHandler = handler
        this.globalDataModel = globalDataModel
    }

    @Override
    TemplateModel get(String key) throws TemplateModelException {
        def returnValue;
        if (key == 'mainContent' && napsHandler.mainContent?.content.trim()) {
            returnValue = napsHandler.mainContent.content
        } else if (napsHandler.mainContent.contentDataModel?.containsKey(key)) {
            returnValue = napsHandler.mainContent.contentDataModel.get(key)
        } else if (napsHandler?.groupDataModel?.containsKey(key)) {
            returnValue = napsHandler.groupDataModel.get(key)
        } else if (globalDataModel?.containsKey(key)) {
            returnValue = globalDataModel.get(key)
        } else {
            throw new RuntimeException("Value not found: $key")
        }
        return new SimpleScalar(getValue(returnValue))
    }

    @Override
    boolean isEmpty() throws TemplateModelException {
        return false
    }

    def getValue(def value) {
        value in Closure ? value() : value
    }
}
