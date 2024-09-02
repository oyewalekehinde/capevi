package com.capevi.app.ui.case_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CaseStatus(status: String) {
    if (status.isNotEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.background(getColorForStatusContainer(status), RoundedCornerShape(12.dp)),
        ) {
            Text(
                text = status,
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 10.sp,
                        color = getColorForStatusText(status),
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.Center,
                    ),
                modifier = Modifier.padding(4.dp),
            )
        }
    }
}
