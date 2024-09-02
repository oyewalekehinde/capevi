
package com.capevi.app.ui.auth_composable

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.capevi.app.R
import com.capevi.app.ui.theme.Blue
import com.capevi.app.ui.theme.Neutral
import com.capevi.navigation.Routes
import com.capevi.shared.utils.validateEmail
import com.capevi.shared.utils.validateFullName
import com.capevi.shared.utils.validatePassword
import com.capevi.shared.widget.backgroundWidget
import com.capevi.shared.widget.customButton
import com.capevi.shared.widget.customTextField
import com.capevi.shared.widget.showDialog
import com.capevi.viewmodels.AuthState
import com.capevi.viewmodels.AuthViewModel

@Composable
fun signUp(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    val context = LocalContext.current
    val fullName =
        remember {
            mutableStateOf("")
        }
    val email =
        remember {
            mutableStateOf("")
        }
    val password =
        remember {
            mutableStateOf("")
        }
    val showDialogValue =
        remember {
            mutableStateOf(false)
        }
    var emailError by remember { mutableStateOf<String?>(null) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(true) }
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Loading -> showDialogValue.value = true
            is AuthState.Authenticated -> {
                showDialogValue.value = false
                navController.navigate(Routes.MainScreen.route) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            }
            is AuthState.Error -> {
                showDialogValue.value = false
                Toast
                    .makeText(
                        context,
                        (authState.value as AuthState.Error).message,
                        Toast.LENGTH_SHORT,
                    ).show()
            }
            else -> Unit
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            showDialog(showDialogValue.value)
            backgroundWidget(
                title = stringResource(id = R.string.signUp),
                subTitle = stringResource(id = R.string.createAnAccount),
                navController = navController,
            )
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(50.dp))
                customTextField(
                    error = fullNameError != null,
                    textState = fullName,
                    placeHolder = stringResource(id = R.string.enterYourFullName),
                    label = stringResource(id = R.string.fullName),
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    onValueChanged = {
                        fullName.value = it
                        fullNameError = validateFullName(fullName.value)
                    },
                )
                if (fullNameError != null) {
                    Text(
                        text = fullNameError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                customTextField(
                    error = emailError != null,
                    textState = email,
                    placeHolder = stringResource(id = R.string.enterYourEmail),
                    label = stringResource(id = R.string.email),
                    keyboardType = KeyboardType.Email,
                    onValueChanged = {
                        email.value = it
                        emailError = validateEmail(email.value)
                    },
                )
                if (emailError != null) {
                    Text(
                        text = emailError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                customTextField(
                    isPassword = passwordVisible,
                    textState = password,
                    error = passwordError != null,
                    placeHolder = stringResource(id = R.string.enterPassword),
                    label = stringResource(id = R.string.password),
                    keyboardType = KeyboardType.Password,
                    onValueChanged = {
                        password.value = it
                        passwordError = validatePassword(password.value)
                    },
                    suffixIcon = {
                        val image =
                            if (passwordVisible) {
                                Icons.Filled.Visibility
                            } else {
                                Icons.Filled.VisibilityOff
                            }

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                )
                if (passwordError != null) {
                    Text(
                        text = passwordError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Spacer(modifier = Modifier.height(80.dp))
                customButton(
                    enabled = emailError == null && passwordError == null && fullNameError == null,
                    onClick = {
                        if ((password.value.trim().isEmpty())) {
                            passwordError = "enter your password"
                        }
                        if ((fullName.value.trim().isEmpty())) {
                            fullNameError = "enter your full name"
                        }
                        if ((email.value.trim().isEmpty())) {
                            emailError = "enter your email"
                        }
                        if (emailError == null && passwordError == null && fullNameError == null) {
                            authViewModel.registerUser(
                                fullName.value.trim(),
                                email.value.trim().lowercase(),
                                password.value.trim(),
                            )
                        }
                    },
                    text = stringResource(id = R.string.signUp),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(id = R.string.alreadyHaveAnAccount),
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                color = Neutral[600]!!,
                                fontWeight = FontWeight.W500,
                                textAlign = TextAlign.Center,
                            ),
                    )
                    Text(
                        text = stringResource(id = R.string.login),
                        modifier =
                            Modifier
                                .clickable {
                                    navController.navigate(Routes.Login.route) {
                                        popUpTo(Routes.SignUp.route) {
                                            inclusive = true
                                        }
                                    }
                                }.padding(horizontal = 10.dp),
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                color = Blue[900]!!,
                                fontWeight = FontWeight.W500,
                                textAlign = TextAlign.Center,
                            ),
                    )
                }
            }
        }
    }
}
