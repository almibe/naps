package org.almibe.naps.maincontent

import spock.lang.*

class FileExtensionMapperSpec extends Specification {

    @Shared
    FileExtensionMapper extensionMapper = new FileExtensionMapper()

    @Unroll
    def 'data driven test for switchFileExtension'(def input, def newExtension, def result) {
        expect:
        extensionMapper.switchFileExtension(input, newExtension) == result

        where:
        input         | newExtension || result
        'test/tmp.md' | 'json'      || 'test/tmp.json'
        '/home/homie/thingsIDontPlay.txt' | 'html' || '/home/homie/thingsIDontPlay.html'
        new File('alex/index.md') | 'html' || new File('alex/index.html')
        new File('/home/homie/thingsIDontPlay.txt') | 'html' || new File('/home/homie/thingsIDontPlay.html')
        'test.txt' | 't' || 'test.t'
        'test' | 'txt' || 'test.txt'
    }
}
