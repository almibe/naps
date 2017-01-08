package org.almibe.naps

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

class NapsPluginExtension {
    String contentsIn = "src/naps/contents"
    String fragmentsIn = "src/naps/fragments"
    String templatesIn = "src/naps/templates"
    String siteOut = "naps/site"

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

        project.task("naps", type:Copy) {
            from "${project.naps.contentsIn}"
            into "${project.buildDir}/${project.naps.siteOut}"

            eachFile {
                if (it.name.endsWith('.json')) { //exclude .json files if there is a .txt file with it's same name
                    if (it.file.path.resolveSibling(it.name.trim(5) + ".txt") != null) {
                        it.exclude()
                    }
                }
                if (it.name.endsWith('.txt')) {
                    //TODO convert them with asciidoctorj and run them through the configured groovy template
                    //TODO have a default template and also have a way of defining template matches with a matcher function
                    //TODO if none of the registered matcher functions pass then use the default
                }
            }
        }
    }
}
