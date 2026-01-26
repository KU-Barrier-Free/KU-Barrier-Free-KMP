package com.ganaljigi.kubf.core.designsystem.component

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import coil3.compose.AsyncImage
import kotlin.math.absoluteValue

@Composable
fun TransformableImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val scaledWidth by remember(imageSize, scale) { derivedStateOf { imageSize.width * scale } }
    val scaledHeight by remember(imageSize, scale) { derivedStateOf { imageSize.height * scale } }
    val imageRect by remember(
        scaledHeight,
        scaledWidth,
        offset,
        containerSize,
    ) {
        derivedStateOf {
            val cx = containerSize.width / 2f
            val cy = containerSize.height / 2f
            Rect(
                offset = Offset(
                    x = cx - scaledWidth / 2f + offset.x,
                    y = cy - scaledHeight / 2f + offset.y,
                ),
                size = Size(scaledWidth, scaledHeight),
            )
        }
    }
    Box(
        modifier = modifier
            .onSizeChanged { containerSize = it }
            .pointerInput(Unit) {
                detectTransformGestures { centroid, panChange, zoomChange, _ ->
                    if (!imageRect.contains(centroid)) return@detectTransformGestures
                    if (zoomChange != 1f) {
                        val newScale = (scale * zoomChange).coerceAtLeast(1f)
                        val currentCenter = Offset(
                            x = containerSize.width / 2f + offset.x,
                            y = containerSize.height / 2f + offset.y,
                        )
                        val relative = centroid - currentCenter
                        val scaleChange = newScale / scale
                        var newOffsetX = offset.x + relative.x * (1f - scaleChange)
                        var newOffsetY = offset.y + relative.y * (1f - scaleChange)
                        newOffsetX += panChange.x
                        newOffsetY += panChange.y
                        scale = newScale
                        val maxOffsetX =
                            ((imageSize.width * newScale - containerSize.width) / 2f).coerceAtLeast(
                                0f,
                            )
                        val maxOffsetY =
                            ((imageSize.height * newScale - containerSize.height) / 2f).coerceAtLeast(
                                0f,
                            )
                        offset = if (newScale == 1f) {
                            Offset.Zero
                        } else {
                            Offset(
                                x = newOffsetX.coerceIn(-maxOffsetX, maxOffsetX),
                                y = newOffsetY.coerceIn(-maxOffsetY, maxOffsetY),
                            )
                        }
                        return@detectTransformGestures
                    }
                    val maxOffsetX = ((scaledWidth - containerSize.width) / 2f).absoluteValue
                    val maxOffsetY =
                        ((scaledHeight - containerSize.height) / 2f).absoluteValue
                    offset = if (scale == 1f) {
                        Offset.Zero
                    } else {
                        Offset(
                            x = (offset.x + panChange.x).coerceIn(-maxOffsetX, maxOffsetX),
                            y = (offset.y + panChange.y).coerceIn(-maxOffsetY, maxOffsetY),
                        )
                    }
                }
            },
    ) {
        AsyncImage(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                )
                .onGloballyPositioned { coord -> imageSize = coord.size },
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )
    }
}
