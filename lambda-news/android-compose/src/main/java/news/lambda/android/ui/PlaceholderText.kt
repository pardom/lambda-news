package news.lambda.android.ui

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.currentTextStyle
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.geometry.Offset
import androidx.ui.geometry.shift
import androidx.ui.graphics.Color
import androidx.ui.graphics.Outline
import androidx.ui.graphics.Path
import androidx.ui.graphics.Shape
import androidx.ui.text.AnnotatedString
import androidx.ui.text.TextLayoutResult
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.Density
import androidx.ui.unit.Dp
import androidx.ui.unit.Px
import androidx.ui.unit.PxSize
import androidx.ui.unit.dp

@Composable
fun PlaceholderText(
    text: String,
    color: Color = Color.LightGray,
    shape: Shape = RoundedCornerShape(2.dp),
    modifier: Modifier = Modifier,
    style: TextStyle = currentTextStyle(),
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    Text(
        text,
        modifier = modifier + Modifier.drawBackground(
            color = color,
            shape = PaddingShape(2.dp, shape)
        ),
        style = style.copy(color = Color.Transparent),
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout
    )
}

@Composable
fun PlaceholderText(
    text: AnnotatedString,
    color: Color = Color.LightGray,
    shape: Shape = RoundedCornerShape(2.dp),
    modifier: Modifier = Modifier,
    style: TextStyle = currentTextStyle(),
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    Text(
        text,
        modifier = modifier + Modifier.drawBackground(
            color = color,
            shape = PaddingShape(2.dp, shape)
        ),
        style = style.copy(color = Color.Transparent),
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout
    )
}

private data class PaddingShape(val padding: Dp, val shape: Shape) : Shape {
    override fun createOutline(size: PxSize, density: Density): Outline {
        val twoPaddings = with(density) { (padding * 2).toPx() }
        val sizeMinusPaddings = PxSize(size.width, size.height - twoPaddings)
        val rawResult = shape.createOutline(sizeMinusPaddings, density)
        return rawResult.offset(twoPaddings / 2)
    }
}

private fun Outline.offset(size: Px): Outline {
    val offset = Offset(0F, size.value)
    return when (this) {
        is Outline.Rectangle -> Outline.Rectangle(rect.shift(offset))
        is Outline.Rounded -> Outline.Rounded(rrect.shift(offset))
        is Outline.Generic -> Outline.Generic(Path().apply {
            addPath(path)
            shift(offset)
        })
    }
}
