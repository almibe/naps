package org.almibe.naps.maincontent

import groovy.json.JsonSlurper
import org.almibe.naps.template.NapsTemplateHashModel
import org.almibe.naps.template.TemplateProcessor
import org.gradle.api.Project
import org.pegdown.PegDownProcessor

class MainContentFactory {
    private static final PegDownProcessor markDownProcessor = new PegDownProcessor()
    private static final JsonSlurper jsonSlurper = new JsonSlurper()
    @Delegate private static final FileExtensionMapper extensionMapper = new FileExtensionMapper()
    TemplateProcessor templateProcessor

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

    String template(String template, MainContent mainContent, def dataModel) {
        NapsTemplateHashModel napsTemplateHashModel = new NapsTemplateHashModel(mainContent, dataModel, [:])
        return fetchTemplateProcessor().processTemplate(template, napsTemplateHashModel)
    }

    List<MainContent> mdDir(String directory) {
        List<String> files = getAllFiles(directory, 'md', false)
        return files.collect {file -> md(file)}
    }

    List<MainContent> htmlDir(String directory) {
        List<String> files = getAllFiles(directory, 'html', false)
        return files.collect {file -> html(file) }
    }

    List<MainContent> mdDirRec(String directory) {
        List<String> files = getAllFiles(directory, 'md', true)
        return files.collect {file -> md(file)}
    }

    List<MainContent> htmlDirRec(String directory) {
        List<String> files = getAllFiles(directory, 'html', true)
        return files.collect {file -> html(file) }
    }

    List<String> getAllFiles(String directory, String extension, boolean recursive) {
        def contents = []
        def rootLocation = project.file(project.extensions.naps.contentsIn).absolutePath
        if(recursive) {
            contents = project.fileTree(dir: "$project.extensions.naps.contentsIn/$directory")
        } else {
            contents = new File("$project.extensions.naps.contentsIn/$directory").listFiles()
        }
        return contents.grep {File it -> it.isFile() && getExtension(it) == extension} .collect {File it -> (it.absolutePath - rootLocation)}
    }

    TemplateProcessor fetchTemplateProcessor() {
        if (templateProcessor != null) return templateProcessor;
        templateProcessor = new TemplateProcessor(project.file("$project.extensions.naps.templatesIn"));
        return templateProcessor
    }
}