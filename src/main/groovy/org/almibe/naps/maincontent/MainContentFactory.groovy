package org.almibe.naps.maincontent

import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.pegdown.PegDownProcessor

class MainContentFactory {
    Project project
    private static final PegDownProcessor markDownProcessor = new PegDownProcessor()
    private static final JsonSlurper jsonSlurper = new JsonSlurper()
    public MainContentFactory(Project project) {
        this.project = project
    }

    MainContent md(String file) {
        MainContent result = new MainContentBean()
        result.content = markDownProcessor.markdownToHtml(project.file("$project.extensions.naps.contentsIn/$file").text)

        File jsonFile = switchFileExtension(project.file("$project.extensions.naps.contentsIn/$file"), 'json')
        result.contentDataModel = jsonFile.exists() ? jsonSlurper.parse(jsonFile) : [:]

        result.finalLocation = switchFileExtension(project.file("$project.buildDir/$project.extensions.naps.siteOut/$file"), 'html')
        return result
    }

    MainContent html(String file) {
        MainContent result = new MainContentBean()
        result.content = project.file("$project.extensions.naps.contentsIn/$file").text

        File jsonFile = switchFileExtension(project.file("$project.extensions.naps.contentsIn/$file"), 'json')
        result.contentDataModel = jsonFile.exists() ? jsonSlurper.parse(jsonFile) : [:]

        result.finalLocation = project.file("$project.buildDir/$project.extensions.naps.siteOut/$file")
        return result
    }

    List<MainContentFactory> mdDir(String directory) {
        //get list of all files in directory
        //for each file call md() and add result to list
        //return list
    }

    File switchFileExtension(File f, String newExtension) {
        File parent = f.parentFile
        String name = f.name
        int index = name.lastIndexOf('.')
        return index == -1 ? new File(parent, name + '.' + newExtension) : new File(parent, name.substring(0,index) + '.' + newExtension)
    }
}
