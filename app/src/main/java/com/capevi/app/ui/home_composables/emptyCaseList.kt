package com.capevi.app.ui.home_composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capevi.app.R
import com.capevi.app.ui.theme.Black
import com.capevi.app.ui.theme.Neutral

@Composable
fun emptyCaseWidget() {
    Image(
        painter = painterResource(id = R.drawable.empty_list_image),
        contentDescription = "Empty List",
        modifier = Modifier.height(169.dp).width(156.dp),
        contentScale = ContentScale.FillBounds,
    )
    Text(
        text = stringResource(id = R.string.noCaseYet),
        style =
            MaterialTheme.typography.bodyLarge.copy(
                fontSize = 24.sp,
                color = Black[950]!!,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center,
            ),
    )
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = stringResource(id = R.string.noCaseContent),
        style =
            MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                color = Neutral[500]!!,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
            ),
    )
}
