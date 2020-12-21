import kotlinx.css.*
import styled.StyleSheet

object BookCoverCollageDesignerStyles : StyleSheet("BookCoverCollageDesignerStyles", isStatic = true) {

    val mainContainer by css {
        padding(5.px)
    }

    val textInput by css {
        margin(vertical = 5.px)
        fontSize = 14.px
    }

    val foundCoversContainer by css {
        padding(5.px)
        width = 800.px
        height = 400.px
        overflowX = Overflow.scroll
    }

    val foundCoverImage by css {
        padding(5.px)
    }

    val selectedCoversContainer by css {
        padding(5.px)
    }

    val selectedCoversCanvas by css {
        borderStyle = BorderStyle.solid
    }
} 
