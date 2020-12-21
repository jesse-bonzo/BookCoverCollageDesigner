import kotlinx.html.js.onClickFunction
import react.*
import styled.css
import styled.styledButton
import styled.styledDiv

data class BookCoverCollageDesignerState(
    var searching: Boolean = false,
    var foundCovers: List<String> = emptyList(),
    var selectedCovers: List<String> = emptyList()
) : RState

@JsExport
class BookCoverCollageDesigner : RComponent<RProps, BookCoverCollageDesignerState>() {

    init {
        state = BookCoverCollageDesignerState()
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                +BookCoverCollageDesignerStyles.mainContainer
            }
            child(CoverSearch::class) {
                attrs {
                    minSearchQueryLength = 3
                    onSearchStarted = {
                        // add a spinner?
                        setState {
                            searching = true
                        }
                    }
                    onFound = { covers ->
                        setState {
                            searching = false
                            this.foundCovers = covers.filter {
                                !state.selectedCovers.contains(it)
                            }
                        }
                    }
                }
            }

            child(CoverSelection::class) {
                attrs {
                    this.searching = state.searching
                    this.foundCovers = state.foundCovers
                    this.onLoadingError = { cover ->
                        setState {
                            foundCovers -= cover
                        }
                    }
                    this.onSelection = { cover ->
                        setState {
                            selectedCovers += cover
                            foundCovers -= cover
                        }
                    }
                }
            }

            child(SelectedCovers::class) {
                attrs {
                    selectedCovers = state.selectedCovers.sortedBy { it }
                    width = "800px"
                    height = "800px"
                }
            }

            styledButton {
                attrs {
                    onClickFunction = {
                        ImageCache.clear()
                        setState {
                            selectedCovers = emptyList()
                        }
                    }
                }
                +"Clear"
            }
        }
    }
}

