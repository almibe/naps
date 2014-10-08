package org.almibe.naps

import org.gradle.api.NamedDomainObjectContainer

class NapsExtension {
    String fragmentsIn = "src/naps/fragments"
    String fragmentsOut = "naps/fragments"
    String resourcesIn = "src/naps/resources"
    String templatesIn = "naps/templates"
    String siteOut = "naps/site"

    String defaultTemplate = ''
    def globalVariables = [:]

    final NamedDomainObjectContainer<NapsHandler> handlers

    NapsExtension(handlers) {
        this.handlers = handlers
    }

    def handlers(Closure closure) {
        handlers.configure(closure)
    }
}

class NapsHandler {
    def name
    def template
    def mainContent
    def finalLocation

    NapsHandler(String name) {
        this.name = name
    }
}