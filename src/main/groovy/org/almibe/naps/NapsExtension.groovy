package org.almibe.naps

import org.gradle.api.NamedDomainObjectContainer

class NapsExtension {
    String fragmentsIn = "src/naps/fragments"
    String resourcesIn = "src/naps/resources"
    String templatesIn = "src/naps/templates"
    String siteOut = "naps/site"
    String defaultTemplate = ''
    def globalDataModel = [:]

    final NamedDomainObjectContainer<ContentGroupProcessor> contentGroups

    NapsExtension(handlers) {
        this.contentGroups = handlers
    }

    def contentGroups(Closure closure) {
        contentGroups.configure(closure)
    }

    def test() {
        println("test")
    }
}

