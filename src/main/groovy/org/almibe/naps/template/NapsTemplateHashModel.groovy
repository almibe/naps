package org.almibe.naps.template

import freemarker.template.SimpleScalar
import freemarker.template.TemplateHashModel
import freemarker.template.TemplateModel
import freemarker.template.TemplateModelException
import org.almibe.naps.ContentGroup
import org.almibe.naps.maincontent.MainContent

class NapsTemplateHashModel implements TemplateHashModel {
    def groupDataModel
    def globalDataModel
    MainContent mainContent

    public NapsTemplateHashModel(MainContent mainContent, def groupDataModel, def globalDataModel) {
        this.groupDataModel = groupDataModel
        this.globalDataModel = globalDataModel
        this.mainContent = mainContent
    }

    @Override
    TemplateModel get(String key) throws TemplateModelException {
        def returnValue;
        if (key == 'mainContent' && mainContent?.content.trim()) {
            returnValue = mainContent.content
        } else if (mainContent.contentDataModel?.containsKey(key)) {
            returnValue = mainContent.contentDataModel.get(key)
        } else if (groupDataModel?.containsKey(key)) {
            returnValue = groupDataModel.get(key)
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
