package org.almibe.naps.maincontent

import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.pegdown.PegDownProcessor

class MainContentFactory {
    private static final PegDownProcessor markDownProcessor = new PegDownProcessor()
    private static final JsonSlurper jsonSlurper = new JsonSlurper()
    @Delegate private static final FileExtensionMapper extensionMapper = new FileExtensionMapper()

    Project project
    public MainContentFactory(Project project) {
        this.project = project
    }

    MainContent md(String file) {
        MainContent result = new MainContentBean()
        result.content = markDownProcessor.markdownToHtml(project.file("$project.extensions.naps.contentsIn/$file").text)

        File jsonFile = switchFileExtension(project.file("$project.extensions.naps.contentsIn/$file"), 'json')
        result.contentDataModel = jsonFile.exists() ? jsonSlurper.parse(jsonFile) : [:]

        result.finalLocation = switchFileExtension(file, 'html')
        return result
    }

    MainContent html(String file) {
        MainContent result = new MainContentBean()
        result.content = project.file("$project.extensions.naps.contentsIn/$file").text

        File jsonFile = switchFileExtension(project.file("$project.extensions.naps.contentsIn/$file"), 'json')
        result.contentDataModel = jsonFile.exists() ? jsonSlurper.parse(jsonFile) : [:]

        result.finalLocation = file
        return result
    }

    List<MainContentFactory> mdDir(String directory) {
        //get list of all files in directory
        //for each file call md() and add result to list
        //return list
        throw new RuntimeException("not implemented")
    }
}