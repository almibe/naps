package org.almibe.naps

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.pegdown.PegDownProcessor

import java.nio.file.DirectoryStream
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class ProcessFragmentsTask extends DefaultTask {
    //TODO can this be a copy task and rewrite using eachFile method -- chimera.labs.oreilly.com/books/1234000001741/ch01.html
    PegDownProcessor pegDownProcessor = new PegDownProcessor();

    @TaskAction
    def processFragments() {
        File fragmentOut = project.file("$project.buildDir/$project.naps.fragmentsOut")
        fragmentOut.mkdirs()
        File fragmentIn = project.file("${project.file(project.naps.fragmentsIn)}")
        processDirectory(fragmentIn, fragmentOut)
    }

    def processDirectory(File source, File target) {
        target.mkdirs()
        for(File file : source.listFiles()) {
            if(file.isDirectory()) {
                String newDirectoryName = '/' + file.getName()
                processDirectory(file, new File(target.getCanonicalPath() + newDirectoryName))
            } else {
                if(file.getName().endsWith('.md')) {
                    String result = processFragment(file)
                    File resultFile = new File(target.getCanonicalPath() + "/" + file.getName())
                    resultFile.withWriter { out ->
                        out.write(result)
                    }
                }
            }
        }
    }

    def String processFragment(File fragment) {
        return pegDownProcessor.markdownToHtml(fragment.text)
    }
}