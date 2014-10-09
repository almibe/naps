package org.almibe.naps.tasks

import freemarker.template.Configuration
import freemarker.template.DefaultObjectWrapper
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ProcessTemplatesTask extends DefaultTask {
    TemplateProcessor templateProcessor = new TemplateProcessor()

    @TaskAction
    def processTemplates() {
        println(project.extensions.naps.defaultTemplate)
        for(def handler : project.extensions.naps.handlers) {
            //templateProcessor.processTemplate(handler.template, ) //this is a mess start from scratch
        }
    }

    class TemplateProcessor {
        Configuration cfg = new Configuration();
        TemplateProcessor() {
            // Specify the data source where the template files come from. Here I set a
            // plain directory for it, but non-file-system are possible too:
            cfg.setDirectoryForTemplateLoading(project.file("$project.extensions.naps.templatesIn"));
            // Specify how templates will see the data-model. This is an advanced topic...
            // for now just use this:
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            // Set your preferred charset template files are stored in. UTF-8 is
            // a good choice in most applications:
            cfg.setDefaultEncoding("UTF-8");
            // Sets how errors will appear. Here we assume we are developing HTML pages.
            // For production systems TemplateExceptionHandler.RETHROW_HANDLER is better.
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            // At least in new projects, specify that you want the fixes that aren't
            // 100% backward compatible too (these are very low-risk changes as far as the
            // 1st and 2nd version number remains):
            cfg.setIncompatibleImprovements(new Version(2, 3, 20));  // FreeMarker 2.3.20
        }

        def processTemplate(String templateName, def dataModel, String output) {
//            Template template = cfg.getTemplate(templateName)
//            Writer writer = new OutputStreamWriter(new FileOutputStream(project.file(output)))
//            template.process(dataModel, writer)
//            writer.close()
        }
    }

}
