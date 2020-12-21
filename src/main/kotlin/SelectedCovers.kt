import kotlinx.browser.document
import kotlinx.html.js.onMouseDownFunction
import kotlinx.html.js.onMouseMoveFunction
import kotlinx.html.js.onMouseUpFunction
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Image
import react.*
import styled.css
import styled.styledCanvas
import styled.styledDiv

external interface SelectedCoversProps : RProps {
    /**
     * List of image urls to render
     */
    var selectedCovers: List<DrawableImage>
    var width: String
    var height: String
}

data class SelectedCoversState(
    var clickedImage: DrawableImage? = null
) : RState

class SelectedCovers : RComponent<SelectedCoversProps, SelectedCoversState>() {

    init {
        state = SelectedCoversState()
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                +BookCoverCollageDesignerStyles.selectedCoversContainer
            }
            styledCanvas {
                css {
                    +BookCoverCollageDesignerStyles.selectedCoversCanvas
                }

                attrs {
                    width = props.width
                    height = props.height

                    onMouseDownFunction = { event ->
                        val event = event.asDynamic()
                        val target = event.target as HTMLCanvasElement
                        val rect = target.getBoundingClientRect()
                        val clickedX = event.clientX - rect.left
                        val clickedY = event.clientY - rect.top

                        props.selectedCovers.sortedBy {
                            it.imageSource
                        }.find {
                            clickedX >= it.x && clickedX <= it.width + it.x && clickedY >= it.y && clickedY <= it.height + it.y
                        }.let { clickedImage ->
                            console.log("clicked $clickedImage")
                            setState {
                                this.clickedImage = clickedImage
                            }
                        }
                    }

                    onMouseMoveFunction = {
                        val event = it.asDynamic()
                        val target = it.target as HTMLCanvasElement
                        val rect = target.getBoundingClientRect()
                        val clickedX = event.clientX - rect.left
                        val clickedY = event.clientY - rect.top
                        state.clickedImage?.let { clickedImage ->
                            val canvas = it.target as HTMLCanvasElement
                            val context = canvas.getContext("2d") as CanvasRenderingContext2D
                            context.clearRect(
                                clickedImage.x, clickedImage.y,
                                clickedImage.width.toDouble(), clickedImage.height.toDouble()
                            )

                            clickedImage.x = clickedX
                            clickedImage.y = clickedY
                            setState {
                                this.clickedImage = clickedImage
                            }
                        }
                    }

                    onMouseUpFunction = {
                        setState {
                            this.clickedImage = null
                        }
                    }
                }

                ref { ref ->
                    (ref as? HTMLCanvasElement)?.let { canvas ->
                        val context = canvas.getContext("2d") as CanvasRenderingContext2D
                        props.selectedCovers.sortedBy {
                            it.imageSource
                        }.forEach { drawableImage ->
                            val img = document.createElement("img") as Image
                            img.onload = {
                                context.drawImage(img, drawableImage.x, drawableImage.y)
                                drawableImage.width = img.width
                                drawableImage.height = img.height
                                it
                            }
                            img.src = drawableImage.imageSource
                        }
                    }
                }
            }
        }
    }
}

data class DrawableImage(
    val imageSource: String,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var width: Int = 0,
    var height: Int = 0
)