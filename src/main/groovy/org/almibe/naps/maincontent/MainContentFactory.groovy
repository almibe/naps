package org.almibe.naps.maincontent

import org.almibe.naps.MarkdownProcessor
import org.gradle.api.Project

class MainContentFactory {
    Project project

    public MainContentFactory(Project project) {
        this.project = project
    }

    MainContent md(String file) {
        MainContent result = new MainContentBean()
        result.content = MarkdownProcessor.instance.process(project.file(root+file)) //TODO finish
        result.contentDataModel = //TODO check for properties file
        result.finalLocation = //TODO get final location
        return result
    }

    MainContent html(String file) {
        MainContent result = new MainContentBean()
        result.content = project.file(root+file).text //TODO finish getting path
        result.contentDataModel = //TODO check for properties files
        result.finalLocation = //TODO get final location
        return result
    }

    List<MainContentFactory> mdDir(String directory) {
        //get list of all files in directory
        //for each file call md() and add result to list
        //return list
    }
}
