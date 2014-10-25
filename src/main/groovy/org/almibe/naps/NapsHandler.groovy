package org.almibe.naps

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
