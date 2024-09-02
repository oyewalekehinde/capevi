package com.capevi.app.ui.settings_composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capevi.app.ui.theme.Neutral

@Composable
fun settingItems(
    title: String = "Account Info",
    icon: ImageVector = Icons.Default.ContactPage,
    isRed: Boolean = false,
    onPressed: () -> Unit = {},
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth(1f)
                .background(color = Color.White)
                .padding(bottom = 2.dp)
                .border(width = 0.5.dp, color = Neutral[400]!!)
                .clickable {
                    onPressed.invoke()
                },
    ) {
        Row(
            modifier =
                Modifier.padding(
                    vertical = 20.dp,
                    horizontal = 20.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.compose.material3.Icon(
                icon,
                tint = if (isRed) MaterialTheme.colorScheme.error else Neutral[600]!!,
                modifier =
                    Modifier
                        .height(24.dp)
                        .width(24.dp),
                contentDescription = "Profile Icon",
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 14.sp,
                color = if (isRed) MaterialTheme.colorScheme.error else Neutral[600]!!,
                fontWeight = FontWeight.W500,
            )
        }
    }
}
