package org.almibe.naps

import org.gradle.api.NamedDomainObjectContainer

class NapsExtension {
    String fragmentsIn = "src/naps/fragments"
    String fragmentsOut = "naps/fragments"
    String resourcesIn = "src/naps/resources"
    String templatesIn = "src/naps/templates"
    String siteOut = "naps/site"
    String defaultTemplate = ''
    def globalVariables = [:]
    def globalFragments = [:]

    final NamedDomainObjectContainer<NapsHandler> handlers

    NapsExtension(handlers) {
        this.handlers = handlers
    }

    def handlers(Closure closure) {
        handlers.configure(closure)
    }

    def test() {
        println("test")
    }
}

class NapsHandler {
    def name
    def mainContent
    def template
    def finalLocation
    def variables = [:]
    def fragments = [:]

    NapsHandler(String name) {
        this.name = name
    }
}