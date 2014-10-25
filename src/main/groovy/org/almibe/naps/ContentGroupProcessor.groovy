package org.almibe.naps

class ContentGroupProcessor {
    def name
    def mainContent
    def template
    def variables = [:]
    def fragments = [:]

    ContentGroupProcessor(String name) {
        this.name = name
    }
}
