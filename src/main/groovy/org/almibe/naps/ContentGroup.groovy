package org.almibe.naps

import org.almibe.naps.maincontent.MainContent

class ContentGroup {
    String name
    MainContent mainContent
    String template
    def groupDataModel = [:]

    ContentGroup(String name) {
        this.name = name
    }
}
