package openlibrary

import org.w3c.xhr.XMLHttpRequest

object OpenLibraryClient {
    /**
     * Returns a list of image urls for the cover of the book found when search by title
     */
    fun coverSearch(title: String, callback: (List<String>) -> (Any)) {
        val request = XMLHttpRequest()
        request.onload = {
            if (request.readyState == XMLHttpRequest.DONE && request.status >= 200 && request.status <= 300) {
                val olr = JSON.parse<OpenLibraryResponse>(request.responseText)
                callback(olr.docs.mapNotNull { doc ->
                    doc.cover_i
                }.filter { coverId ->
                    // there's something funny with how this is handled
                    // it's a Long but it can't compare with 0 unless we treat it as dynamic
                    coverId.asDynamic() > 0L
                }.map { coverId ->
                    "http://covers.openlibrary.org/b/id/${coverId}-M.jpg"
                })
            }
        }
        request.open("GET", "http://openlibrary.org/search.json?title=$title", true)
        request.send()
    }
}