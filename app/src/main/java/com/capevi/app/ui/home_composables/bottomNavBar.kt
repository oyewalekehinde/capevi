package com.capevi.app.ui.home_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capevi.app.R
import com.capevi.app.ui.theme.Black
import com.capevi.app.ui.theme.Blue
import com.capevi.data.model.UserModel
import com.capevi.navigation.Routes

@Composable
fun BottomNavBar(
    selectedScreen: String,
    userModel: UserModel,
    onScreenSelected: (String) -> Unit,
) {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Blue,
        elevation = 5.dp,
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.home_icon),
                    contentDescription = "Home Icon",
                    tint = if (selectedScreen == Routes.Home.route) Blue[900]!! else Black[500]!!,
                    modifier =
                        Modifier
                            .height(24.dp)
                            .width(24.dp)
                            .size(24.dp),
                )
            },
            label = {
                Text(
                    "Home",
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 10.sp,
                            color = if (selectedScreen == Routes.Home.route) Blue[900]!! else Black[500]!!,
                            fontWeight = FontWeight.W400,
                            textAlign = TextAlign.Center,
                        ),
                )
            },
            selected = selectedScreen == Routes.Home.route,
            onClick = { onScreenSelected(Routes.Home.route) },
        )
        BottomNavigationItem(
            icon = {
                Box(
                    modifier =
                        Modifier
                            .height(24.dp)
                            .width(24.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Black[100]!!)
                            .border(
                                width = 1.dp,
                                shape = CircleShape,
                                color =
                                    if (selectedScreen ==
                                        Routes.Profile.route
                                    ) {
                                        Blue[900]!!
                                    } else {
                                        Black[500]!!
                                    },
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${userModel.firstName.first()}${userModel.lastName.first()}".uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.W500,
                        color = if (selectedScreen == Routes.Profile.route) Blue[900]!! else Black[500]!!,
                        textAlign = TextAlign.Center,
                    )
                }
            },
            label = {
                Text(
                    "Settings",
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 10.sp,
                            color = if (selectedScreen == Routes.Profile.route) Blue[900]!! else Black[500]!!,
                            fontWeight = FontWeight.W400,
                            textAlign = TextAlign.Center,
                        ),
                )
            },
            selected = selectedScreen == Routes.Profile.route,
            onClick = { onScreenSelected(Routes.Profile.route) },
        )
    }
}
