package org.almibe.naps

import org.pegdown.PegDownProcessor

@Singleton
class MarkdownProcessor {
    private final PegDownProcessor pegDownProcessor
    private final String rootMarkdownLocation

    MarkdownProcessor(String rootMarkdownLocation) {
        this.pegDownProcessor = new PegDownProcessor()
        this.rootMarkdownLocation = rootMarkdownLocation
    }

    String process(String markdownFile) {
        File file = new File("$rootMarkdownLocation/$markdownFile")
        return pegDownProcessor.markdownToHtml(file.text)
    }
}
