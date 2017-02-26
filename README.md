# naps

naps is a simple static site generator for Gradle written in Groovy.

## Status

Version 0.1.0 is currently in jcenter and is being used to create production websites so it can be used but expect
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
        classpath group: 'org.almibe', name: 'naps', version: '0.1.0'
    }
}

apply plugin: 'org.almibe.naps'
```

This project assumes all the defaults which is generally a best practice.  If you look at
[naps-test](https://github.com/almibe/naps-test)'s build file you'll see an example of including a `naps` section
in the build file to override values such as the name of the defaultTemplate.  See the class `NapsPluginExtension`
in the file `NapsPlugin.groovy` to see values that can be overridden as well as their default values.

Along side this file you'll want to build the naps version of the usual gradle project structure.

```
/src/
-/naps/
--/content/
--/fragments/
--/templates/
build.gradle
```

Under the `/src/naps/` directory you'll notice three directories
<dl>
  <dt><code>/content/</code></dt>
  <dd>This directory holds content fragments that can be either expressed as asciidoc.
  This folder can also contain json files with the same name as the asciidoc file that can
  provide additional values used in templates that sit outside of the main content.  The main idea is that each
  asciidoc file will produce a single final html file with the asciidoc content
  being the main content of the file with the other data also represented as used by templates.</dd>
  <dt><code>/fragments/</code></dt>
  <dd>Fragments represent small pieces of content that aren't the main content of a page but are used independently
  templates.  Not all sites will find fragments that useful.  If it ever makes sense to support template nesting with
  naps fragments will probably go away.  But for now they are useful for things like using the same content across
  multiple templates like a header or footer or sidebar.</dd>
  <dt><code>/templates/</code></dt>
  <dd>Templates are files that are processed with the <code>GStringTemplateEngine</code>.  Each asciidoc file in contents with
  be processed with either default template or the template specified in the matching .json file.  The content of
  the asciidoc is inset in the `$content` variable and all other are set with the content from the .json file.</dd>
</dl>

When you run `gradle naps` each file in the content directory is examined.  If it's an asciidoc file (by default *.adoc)
it is process by `asciidoctorj` and the output of that process is inserted into the `${content}` variable of the
related template file (either the default template or the one specified in the related .json file).  All other files
(except for related json files) are just copied over without being changed.
