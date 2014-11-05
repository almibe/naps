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
        result.content = MarkdownProcessor.instance.process(project.file("$project.extensions.naps.contentsIn/$file"))

        Properties properties = new Properties() //TODO check for properties file
        File propertiesFile = switchFileExtension(project.file("$project.extensions.naps.contentsIn/$file"), 'json')
        result.contentDataModel

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

    File switchFileExtension(File f, String newExtension) {
        File parent = f.parentFile
        String name = f.name
        int index = name.lastIndexOf('.')
        return index == -1 ? new File(parent, name + '.' + newExtension) : new File(parent, name.substring(0,index) + '.' + newExtension)
    }
}
