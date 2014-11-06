package org.almibe.naps.maincontent

import groovy.transform.Canonical

@Canonical
class MainContentBean implements MainContent {
    File finalLocation
    String content
    Map<String, Object> contentDataModel
}
