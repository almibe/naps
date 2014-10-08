package org.almibe.naps

import org.almibe.naps.tasks.NapsTask
import org.almibe.naps.tasks.ProcessFragmentsTask
import org.almibe.naps.tasks.ProcessTemplatesTask;
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

public class NapsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.extensions.create("naps", NapsExtension)
        /*
         * TODO
         * http://www.gradle.org/docs/current/userguide/more_about_tasks.html#sec:up_to_date_checks
        */
        project.task("processFragments", type:ProcessFragmentsTask) {
            group = 'naps'
        }
        project.task("processTemplates", type:ProcessTemplatesTask, dependsOn:':processFragments') {
            group = 'naps'
        }
        project.task("processResources", type:Copy) {
            from("$project.naps.resourcesIn")
            into "$project.buildDir/$project.naps.siteOut"
        }
        project.task("naps", type:NapsTask, dependsOn:[':processFragments', ':processResources', ':processTemplates']) {
            group = 'naps'
        }
    }
}
