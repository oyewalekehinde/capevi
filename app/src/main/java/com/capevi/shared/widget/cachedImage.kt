package com.capevi.shared.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun cachedImage(
    url: String,
    height: Dp,
    width: Dp,
    borderRadius: Dp,
) {
    val painter =
        rememberAsyncImagePainter(
            ImageRequest
                .Builder(LocalContext.current)
                .data(url)
                .crossfade(true) // Optional: Adds a fade transition when loading
                .build(),
        )

    Box(
        modifier =
            Modifier.height(height).width(width).border(borderRadius, color = Color.Transparent).clip(
                RoundedCornerShape(borderRadius),
            ),
    ) {
        Image(
            contentScale = ContentScale.Crop,
            painter = painter,
            contentDescription = null,
            modifier = Modifier.height(height).width(width).clip(RoundedCornerShape(borderRadius)), // Adjust size as needed
        )
        if (painter.state is AsyncImagePainter.State.Loading) {
//            CircularProgressIndicator(
//                modifier = Modifier.size(24.dp).align(Alignment.Center),
//                color = Color.Gray,
//            )
        }
    }
}
