package com.capevi.app.ui.settings_composable

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.capevi.app.ui.theme.Black
import com.capevi.app.ui.theme.Blue
import com.capevi.data.model.UserModel
import com.capevi.navigation.Routes
import com.capevi.shared.widget.cachedImage
import com.capevi.shared.widget.showDialog
import com.capevi.viewmodels.AuthState
import com.capevi.viewmodels.AuthViewModel

@Composable
fun SettingsScreen(
    user: UserModel,
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        val context = LocalContext.current
        val imageUrl2 = "https://firebasestorage.googleapis.com/v0/b/kedari-user.appspot.com/o/uploads%2FprofileImage%2Fimage_picker_2765E9F7-A814-4B7F-8408-CA25909447A4-7210-00000344BEF39AA5.jpg?alt=media&token=2365ecc5-0764-48ad-afa2-8dbbe30457f0"
        var showDialog by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var showDialogValue by
            remember {
                mutableStateOf(false)
            }
        val authState = authViewModel.authState.observeAsState()
        LaunchedEffect(Unit) {
            authViewModel.resetAuthState()
        }
        LaunchedEffect(authState.value) {
            when (authState.value) {
                is AuthState.DeletingAccount -> showDialogValue = true
                is AuthState.DeletedAccount -> {
                    showDialogValue = false
                    Toast
                        .makeText(
                            context,
                            "Account deleted successfully",
                            Toast.LENGTH_SHORT,
                        ).show()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.MainScreen.route) { inclusive = true }
                    }
                }
                is AuthState.DeletingAccountError -> {
                    showDialogValue = false
                    Toast
                        .makeText(
                            context,
                            (authState.value as AuthState.DeletingAccountError).message,
                            Toast.LENGTH_SHORT,
                        ).show()
                }
                else -> Unit
            }
        }
        Box(
            modifier =
                Modifier
                    .height(215.dp)
                    .fillMaxWidth(1f),
        ) {
            Box(
                modifier =
                    Modifier
                        .height(165.dp)
                        .fillMaxWidth(1f)
                        .background(Blue[100]!!),
            ) {
                Text(
                    text = "Settings",
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            color = Black[950]!!,
                            fontWeight = FontWeight.W600,
                            textAlign = TextAlign.Center,
                        ),
                    modifier =
                        Modifier
                            .padding(start = 20.dp, top = 50.dp),
                )
            }
            Box(
                modifier =
                    Modifier
                        .padding(start = 20.dp)
                        .fillMaxHeight(1f),
                contentAlignment = Alignment.BottomStart,
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .height(100.dp)
                                .width(100.dp)
                                .background(Color.Gray.copy(alpha = 0.5f), shape = CircleShape),
                        contentAlignment = Alignment.BottomEnd,
                    ) {
                        if (user.image != null) {
                            cachedImage(
                                height = 100.dp,
                                width = 100.dp,
                                borderRadius = 100.dp,
                                url = imageUrl2,
                            )
                        } else {
                            androidx.compose.material3.Icon(
                                Icons.Filled.Person,
                                tint = Color.White,
                                modifier =
                                    Modifier
                                        .height(100.dp)
                                        .width(100.dp),
                                contentDescription = "Profile Icon",
                            )
                        }

                        Box(
                            modifier =
                                Modifier
                                    .height(32.dp)
                                    .width(32.dp)
                                    .background(Blue[900]!!, shape = CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            androidx.compose.material3.Icon(
                                Icons.Filled.PhotoCamera,
                                tint = Color.White,
                                modifier = Modifier.height(16.dp).width(16.dp),
                                contentDescription = "Camera Icon",
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            text = user.firstName + " " + user.lastName,
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 18.sp,
                                    color = Black[950]!!,
                                    fontWeight = FontWeight.W500,
                                    textAlign = TextAlign.Center,
                                ),
                        )
                        Text(
                            text = user.email,
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 14.sp,
                                    color = Black[600]!!,
                                    fontWeight = FontWeight.W400,
                                    textAlign = TextAlign.Center,
                                ),
                        )
                    }
                }
            }
        }
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            showDialog(showDialogValue)
            if (showDialog) {
                LogoutConfirmationDialog(
                    onLogout = {
                        logout(context, navController) // Perform logout
                    },
                    onDismiss = {
                        showDialog = false // Close the dialog
                    },
                )
            }
            DeleteAccountDialog(
                showDialog = showDeleteDialog,
                onConfirm = {
                    authViewModel.deleteAccount()
                },
                onDismiss = {
                    showDeleteDialog = false
                },
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth(1f).height(0.5.dp).background(Color(0xFFE7E6E6)))
            Spacer(modifier = Modifier.height(20.dp))

            settingItems("Account Edit", Icons.Default.AccountCircle)
            settingItems("Privacy Policy", Icons.Default.PrivacyTip, onPressed = { navController.navigate(Routes.PrivacyPolicy.route) })
            settingItems(
                "Terms Of Use",
                Icons.AutoMirrored.Filled.StickyNote2,
                onPressed = { navController.navigate(Routes.TermOfUse.route) },
            )
            settingItems("Contact Us", Icons.Default.ContactPage, onPressed = {
                makePhoneCall(context, "+447477932119")
            })
            settingItems("Delete Account", Icons.Default.DeleteOutline, isRed = true, onPressed = { showDeleteDialog = true })
            settingItems("Log Out", Icons.AutoMirrored.Filled.Logout, isRed = true, onPressed = { showDialog = true })
        }
    }
}

fun makePhoneCall(
    context: Context,
    phoneNumber: String,
) {
    val intent =
        Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
    if (context.checkSelfPermission(android.Manifest.permission.CALL_PHONE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        context.startActivity(intent)
    } else {
        // Handle the case where the permission is not granted
        // Request permission or show a message to the user
    }
}
