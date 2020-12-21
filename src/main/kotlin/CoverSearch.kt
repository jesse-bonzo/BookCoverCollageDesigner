import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import openlibrary.OpenLibraryClient
import org.w3c.dom.HTMLInputElement
import react.*
import styled.css
import styled.styledButton
import styled.styledForm
import styled.styledInput

interface CoverSearchProps : RProps {
    var onSearchStarted: () -> Unit
    var onFound: (List<String>) -> Unit
    var minSearchQueryLength: Int
}

data class CoverSearchState(
    var searchQuery: String = "",
    var loading: Boolean = false
) : RState

class CoverSearch : RComponent<CoverSearchProps, CoverSearchState>() {

    init {
        state = CoverSearchState()
    }

    override fun RBuilder.render() {
        styledForm {
            attrs {
                onSubmitFunction = { event ->
                    event.preventDefault()
                    if (state.searchQuery.length > props.minSearchQueryLength) {
                        props.onSearchStarted()
                        setState {
                            loading = true
                        }
                        OpenLibraryClient.coverSearch(state.searchQuery) {
                            props.onFound(it)
                            setState {
                                loading = false
                            }
                        }
                    }
                }
            }

            styledInput(type = InputType.search) {
                css {
                    +BookCoverCollageDesignerStyles.textInput
                }
                attrs {
                    disabled = state.loading
                    onChangeFunction = { event ->
                        val query = (event.target as HTMLInputElement).value
                        if (query.length > props.minSearchQueryLength) {
                            setState {
                                searchQuery = query
                            }
                        }
                    }
                }
            }

            styledButton {
                attrs {
                    disabled = state.loading
                }
                +"Search"
            }
        }
    }
}