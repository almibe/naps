package org.almibe.naps.tasks

import freemarker.template.Configuration
import freemarker.template.DefaultObjectWrapper
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.TemplateHashModel
import freemarker.template.TemplateModel
import freemarker.template.TemplateModelException
import freemarker.template.Version
import org.almibe.naps.NapsHandler
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import  freemarker.template.SimpleScalar

class ProcessTemplatesTask extends DefaultTask {
    TemplateProcessor templateProcessor = new TemplateProcessor()

    final String outputLocation = "$project.buildDir/$project.naps.siteOut"
    String defaultTemplate
    def globalVariables
    def globalFragments

    @TaskAction
    def processTemplates() {
        defaultTemplate = project.extensions.naps.defaultTemplate
        globalVariables = project.extensions.naps.globalVariables
        globalFragments = project.extensions.naps.globalFragments

        for(NapsHandler handler : project.extensions.naps.handlers) {
            def finalTemplate = handler.template?.trim() ?: defaultTemplate
            templateProcessor.processTemplate(finalTemplate, new NapsTemplateHashModel(handler), "$outputLocation/${computeFileLocation(handler)}${computeFileName(handler)}")
        }
    }

    /**
     *
     */
    String computeFileLocation(NapsHandler handler) {
        if(handler.finalLocation instanceof String && handler.finalLocation.trim()) {
            return "$handler.finalLocation/"
        }
    }

    /**
     * Creates the name for the file about to be output.  By default it's just the name of the main content but it can
     * be over written by setting the final name property as either a String or a closure that accepts the mainContent
     * value and returns the name.
     */
    String computeFileName(NapsHandler handler) {
        if (handler.finalName instanceof String) {
            return handler.finalName
        } else if (handler.finalName instanceof Closure) {
            return handler.finalName(handler.mainContent)
        } else {
            return handler.mainContent
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
            Template template = cfg.getTemplate(templateName)
            OutputStream os = new FileOutputStream(project.file(output))
            Writer writer = new OutputStreamWriter(os)
            template.process(dataModel, writer)
            os.close()
        }
    }

    class NapsTemplateHashModel implements TemplateHashModel {
        NapsHandler napsHandler
        public NapsTemplateHashModel(NapsHandler handler) {
            napsHandler = handler
        }

        @Override
        TemplateModel get(String key) throws TemplateModelException {
            String returnValue = '';
            if(false) {
                //TODO support computedContent
            } else if(key == 'mainContent' && napsHandler.mainContent?.trim()) {
                returnValue = napsHandler.mainContent.trim()
            } else if(false) {
                //TODO support properties files
            } else if (napsHandler.fragments.containsKey(key)) {
                //TODO complete
            } else if(napsHandler.variables.containsKey(key)) {
                returnValue = napsHandler.variables[key]
            } else if(globalFragments.containsKey(key)) {
                //TODO suppport fragments
            } else if(globalVariables.containsKey(key)) {
                returnValue = globalVariables[key]
            } else {
                throw new RuntimeException("Value not found: $key")
            }
            return new SimpleScalar(returnValue)
        }

        @Override
        boolean isEmpty() throws TemplateModelException {
            return false
        }
    }
}
