package org.almibe.naps.maincontent

import org.gradle.api.Project

class MainContentFactory {
    Project project

    public MainContentFactory(Project project) {
        this.project = project
    }

    def md(String input) {
        println "in md $input"
    }

    def html(String input) {
        println "in html $input"
    }
}
