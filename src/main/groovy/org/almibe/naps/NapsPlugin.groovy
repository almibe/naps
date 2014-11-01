package org.almibe.naps

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

public class NapsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        def contentGroups = project.container(ContentGroup)
        project.extensions.create("naps", NapsExtension, contentGroups)

        /*
         * TODO
         * http://www.gradle.org/docs/current/userguide/more_about_tasks.html#sec:up_to_date_checks
        */
        project.task("processResources", type:Copy, group:'naps') {
            from("$project.naps.resourcesIn")
            into "$project.buildDir/$project.naps.siteOut"
        }
        project.task("naps", type:NapsTask, dependsOn:[':processResources'],
            group:'naps')
    }
}
