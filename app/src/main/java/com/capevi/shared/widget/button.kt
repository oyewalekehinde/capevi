package com.capevi.shared.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capevi.app.R
import com.capevi.app.ui.theme.Blue

@Composable
fun customButton(
    onClick: () -> Unit = {},
    text: String,
    backGroundColor: Color = Blue[900]!!,
    borderRadius: Int = 10,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(borderRadius.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backGroundColor),
    ) {
        Text(
            text = text,
            modifier =
                Modifier
                    .fillMaxWidth(1f)
                    .padding(vertical = 10.dp),
            style =
                TextStyle(
                    color = colorResource(id = R.color.white),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    textAlign = TextAlign.Center,
                ),
        )
    }
}
