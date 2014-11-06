package org.almibe.naps.maincontent

interface MainContent {
    String getContent()
    File getFinalLocation()
    Map<String, Object> getContentDataModel()
}
