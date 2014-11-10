package org.almibe.naps

class ContentGroup {
    String name
    def mainContent
    String template
    def groupDataModel = [:]

    ContentGroup(String name) {
        this.name = name
    }
}
