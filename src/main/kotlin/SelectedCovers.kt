import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Image
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.css
import styled.styledCanvas
import styled.styledDiv

external interface SelectedCoversProps : RProps {
    /**
     * List of image urls to render
     */
    var selectedCovers: List<String>
    var width: String
    var height: String
}

// TODO: Make the images draggable

class SelectedCovers : RComponent<SelectedCoversProps, RState>() {
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
                }

                ref { canvas ->
                    if (canvas != null) {
                        val context = canvas.getContext("2d") as CanvasRenderingContext2D
                        context.clearRect(0.0, 0.0, canvas.width, canvas.height)
                        drawImages(context, props.selectedCovers.iterator())
                    }
                }
            }
        }
    }
}

fun drawImages(context: CanvasRenderingContext2D, imagesIt: Iterator<String>, x: Double = 0.0, y: Double = 0.0) {
    if (imagesIt.hasNext()) {
        val image = imagesIt.next()
        drawImage(context, image, x, y) {
            val bounds = context.canvas.getBoundingClientRect()
            var newX = x + it.width.toDouble() + 5.0
            var newY = y
            if (newX + it.width > bounds.right) {
                newX = 0.0
                newY = y + it.height.toDouble() + 5.0
            }

            if (newY < bounds.bottom) {
                drawImages(context, imagesIt, newX, newY)
            }
        }
    }
}

fun drawImage(context: CanvasRenderingContext2D, imageSrc: String, x: Double, y: Double, callback: (Image) -> Unit) {
    val img = document.createElement("img") as Image
    img.onload = {
        val bounds = context.canvas.getBoundingClientRect()
        var drawX = x
        var drawY = y
        if (x + img.width > bounds.right) {
            drawX = 0.0
            drawY = y + img.height
        }
        context.drawImage(img, drawX, drawY)
        callback(img)
    }
    img.src = imageSrc
}