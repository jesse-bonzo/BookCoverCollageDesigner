package openlibrary

class OpenLibraryResponse {
    var start: Long = 0
    var numFound: Long = 0
    var docs: Array<OpenLibraryDoc> = emptyArray()
}