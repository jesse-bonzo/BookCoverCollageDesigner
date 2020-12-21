import kotlinx.browser.document
import kotlinx.html.js.onMouseDownFunction
import kotlinx.html.js.onMouseMoveFunction
import kotlinx.html.js.onMouseOutFunction
import kotlinx.html.js.onMouseUpFunction
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Image
import react.*
import styled.css
import styled.styledCanvas
import styled.styledDiv
import kotlin.random.Random

external interface SelectedCoversProps : RProps {
    /**
     * List of image urls to render
     */
    var selectedCovers: List<String>
    var width: String
    var height: String
}

data class SelectedCoversState(
    var clickedImage: DrawableImage? = null,
    var lastX: Double? = null,
    var lastY: Double? = null
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
                        props.selectedCovers.map { ImageCache[it] }.filterNotNull().sortedBy {
                            it.imageSource
                        }.reversed().find {
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
                        val clickedX = (event.clientX - rect.left) as Number
                        val clickedY = (event.clientY - rect.top) as Number
                        state.clickedImage?.let { clickedImage ->
                            if (state.lastX == null || state.lastY == null) {
                                setState {
                                    this.lastX = clickedX.toDouble()
                                    this.lastY = clickedY.toDouble()
                                }
                            } else {
                                val deltaX = clickedX.toDouble() - state.lastX!!
                                val deltaY = clickedY.toDouble() - state.lastY!!
                                clickedImage.x += deltaX
                                clickedImage.y += deltaY
                                if (clickedImage.x < 0) {
                                    clickedImage.x = 0.0
                                }
                                if (clickedImage.x > target.width - clickedImage.width) {
                                    clickedImage.x = (target.width - clickedImage.width).toDouble()
                                }
                                if (clickedImage.y < 0) {
                                    clickedImage.y = 0.0
                                }
                                if (clickedImage.y > target.height - clickedImage.height) {
                                    clickedImage.y = (target.height - clickedImage.height).toDouble()
                                }
                                setState {
                                    this.clickedImage = clickedImage
                                    this.lastX = clickedX.toDouble()
                                    this.lastY = clickedY.toDouble()
                                }
                            }
                        }
                    }

                    onMouseUpFunction = {
                        setState {
                            this.clickedImage = null
                            this.lastX = null
                            this.lastY = null
                        }
                    }

                    onMouseOutFunction = {
                        setState {
                            this.clickedImage = null
                            this.lastX = null
                            this.lastY = null
                        }
                    }
                }

                ref { ref ->
                    (ref as? HTMLCanvasElement)?.let { canvas ->
                        val context = canvas.getContext("2d") as CanvasRenderingContext2D
                        context.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
                        props.selectedCovers.sortedBy { it }.forEach { cover ->
                            val drawableImage = ImageCache[cover]
                            if (drawableImage?.image == null) {
                                val image = document.createElement("img") as Image
                                image.onload = {
                                    val x = Random.nextInt(canvas.width - image.width).toDouble()
                                    val y = Random.nextInt(canvas.height - image.height).toDouble()
                                    context.drawImage(image, x, y)
                                    ImageCache[cover] = DrawableImage(cover, image, x, y, image.width, image.height)
                                    it
                                }
                                image.src = cover
                            } else {
                                context.drawImage(drawableImage.image!!, drawableImage.x, drawableImage.y)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class DrawableImage(
    val imageSource: String,
    var image: Image? = null,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var width: Int = 0,
    var height: Int = 0
)

/**
 * Caches Image elements to prevent flickering and performance issues
 */
object ImageCache {
    private val images: MutableMap<String, DrawableImage> = mutableMapOf()
    operator fun get(source: String) = images[source]
    operator fun set(source: String, img: DrawableImage) {
        images[source] = img
    }

    fun contains(source: String) = images.containsKey(source)
    fun clear() = images.clear()
}