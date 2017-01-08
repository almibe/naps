package org.almibe.naps

import org.gradle.api.Plugin
import org.gradle.api.Project

class NapsPluginExtension {
    String defaultTemplate = ""
    List<TemplateDefinition> templateDefinition = []
}

interface TemplateDefinition {
    String templateName
    boolean match(File file)
}

class NapsPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {

        project.extensions.create("naps", NapsPluginExtension)

        project.task("naps") {
            //TODO copy over all resources except .txt files process those
            //TODO convert them with asciidoctorj and run them through the configured groovy template
            //TODO have a default template and also have a way of defining template matches with a matcher function
            //TODO if none of the registered matcher functions pass then use the default
        }
    }
}
