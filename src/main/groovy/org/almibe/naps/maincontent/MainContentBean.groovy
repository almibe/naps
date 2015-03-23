package org.almibe.naps.maincontent

import groovy.transform.Canonical

@Canonical
class MainContentBean implements MainContent {
    String finalLocation
    def dataModel = [:]
}
