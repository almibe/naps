package org.almibe.naps

import org.pegdown.PegDownProcessor

class MarkdownProcessor {
    final PegDownProcessor pegDownProcessor
    final String rootMarkdownLocation

    MarkdownProcessor(String rootMarkdownLocation) {
        this.pegDownProcessor = new PegDownProcessor()
        this.rootMarkdownLocation = rootMarkdownLocation
    }

    String process(String markdownFile) {
        File file = new File("$rootMarkdownLocation/$markdownFile")
        return pegDownProcessor.markdownToHtml(file.text)
    }
}
