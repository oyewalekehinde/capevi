package com.capevi.shared.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.capevi.app.R
import com.capevi.app.ui.theme.Blue

@Composable
fun backgroundWidget(
    title: String,
    subTitle: String,
    navController: NavHostController? = null,
) {
    Box(
        modifier =
            Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth(1f)
                .background(color = Blue[900]!!),
    ) {
        Image(
            painter = painterResource(id = R.drawable.container_bg),
            contentDescription = "Onboarding 1",
            modifier =
                Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(1f),
            contentScale = ContentScale.FillBounds,
        )
        Column(modifier = Modifier.padding(start = 20.dp, top = 40.dp, end = 20.dp)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .height(40.dp)
                        .width(40.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                        ).clickable {
                            navController?.popBackStack()
                        },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White, // Set the color and opacity here
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = title,
                style =
                    TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W600,
                        color = Color.White,
                    ),
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = subTitle,
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        color = Color.White,
                    ),
            )
        }
    }
}
