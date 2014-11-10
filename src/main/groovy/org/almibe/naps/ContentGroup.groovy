package org.almibe.naps

import org.almibe.naps.maincontent.MainContent

class ContentGroup {
    String name
    def mainContent
    String template
    def groupDataModel = [:]

    ContentGroup(String name) {
        this.name = name
    }
}
