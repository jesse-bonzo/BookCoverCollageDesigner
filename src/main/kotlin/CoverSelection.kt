import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onErrorFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.css
import styled.styledDiv
import styled.styledImg

interface CoverSelectionProps : RProps {
    var searching: Boolean
    var foundCovers: List<String>
    var onSelection: (String) -> Unit
    var onLoadingError: (String) -> Unit
}

class CoverSelection : RComponent<CoverSelectionProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                +BookCoverCollageDesignerStyles.foundCoversContainer
            }

            if (props.searching) {
                // TODO: render a spinner?
                +"Loading..."
            } else {
                props.foundCovers.forEach { imgSrc ->
                    styledImg {
                        css {
                            +BookCoverCollageDesignerStyles.foundCoverImage
                        }
                        attrs {
                            src = imgSrc
                            onErrorFunction = {
                                props.onLoadingError(imgSrc)
                            }
                            onClickFunction = {
                                props.onSelection(imgSrc)
                            }
                        }
                    }
                }
            }
        }
    }
}