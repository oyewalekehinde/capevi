package com.capevi.app.ui.case_composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capevi.app.ui.theme.Black

@Composable
fun textItem(
    title: String,
    subTitle: String,
) {
    Column {
        Text(
            text = title,
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    color = Black[950]!!,
                    fontWeight = FontWeight.W500,
                ),
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = subTitle,
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    color = Black[600]!!,
                    fontWeight = FontWeight.W400,
                ),
        )
    }
}
