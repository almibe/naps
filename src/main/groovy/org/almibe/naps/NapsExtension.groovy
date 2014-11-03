package org.almibe.naps

import org.almibe.naps.maincontent.MainContentFactory
import org.gradle.api.NamedDomainObjectContainer

class NapsExtension {
    String fragmentsIn = "src/naps/fragments"
    String resourcesIn = "src/naps/resources"
    String templatesIn = "src/naps/templates"
    String siteOut = "naps/site"
    String defaultTemplate = ''
    def globalDataModel = [:]

    final NamedDomainObjectContainer<ContentGroup> contentGroups
    @Delegate final MainContentFactory mainContentFactory

    NapsExtension(mainContentFactory, contentGroups) {
        this.mainContentFactory = mainContentFactory
        this.contentGroups = contentGroups
    }

    def contentGroups(Closure closure) {
        contentGroups.configure(closure)
    }
}

