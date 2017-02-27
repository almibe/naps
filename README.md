# naps

naps is a simple static site generator for Gradle written in Groovy.

## Status

Version 0.2.0 is currently in jcenter and is being used to create production websites so it can be used but expect
changes since this is a young project.

## Example

See [naps-test](https://github.com/almibe/naps-test) for an example project.

## Getting Started

Assuming that you already have some knowledge of [Gradle](https://www.gradle.org) below is the simplest `build.gradle`
file for a naps project.

```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath group: 'org.almibe', name: 'naps', version: '0.2.0'
    }
}

apply plugin: 'org.almibe.naps'
```

This project build file assumes all the defaults which is generally a best practice.  If you look at
[naps-test](https://github.com/almibe/naps-test)'s build file you'll see an example of including a `naps` section
in the build file to override values such as the name of the defaultTemplate.  See the class `NapsPluginExtension`
in the file `NapsPlugin.groovy` for all the values that can be overridden as well as their default values.

Along side of this file you will want to create the naps version of the usual gradle project structure.

```
/src/
-/naps/
--/content/
--/templates/
build.gradle
```

Under the `/src/naps/` directory you'll notice two directories `content` and `templates`.  The `content` directory
holds three types of files regular, content, and metadata files.  Content files are files that have the asciidoc
file extension (`adoc` by default).  Metadata files are files that share the same name as content files but have the
*.json file extension or are the file that represents the directory default metadata file (`directory.default.json`
by default).  And regular files are all other image, html, css, js, etc. files you want copied over to your final
website.  Each regular file or content file represents a single file in the final website directory.  The main
difference is that regular files are copied over without being changed and content files are processed to create a
final html file.

Templates are files that are processed with the `GStringTemplateEngine`.  Each asciidoc file in contents with
be processed with either default template (set in the `build.gradle` file), the template specified in the matching
.json file (specified as the value of the `template` key), or from the directory default json file.  The content of
the asciidoc is inseted in the `$content` variable and all other are set with the content from the .json file.
Templates can also reference other templates via a call such as `${templates.call('dir/innterTemplateName.html')}`.
The inner template has refence to all the variables of the outer template.

To create your website run `gradle naps` and each file in the content directory is examined.  If it's an asciidoc file
(by default *.adoc) it is process by `asciidoctorj` and the output of that process is inserted into the `${content}`
variable of the related template file (either the default template or the one specified in the related .json file).
All other files (except for related json files) are just copied over without being changed.
