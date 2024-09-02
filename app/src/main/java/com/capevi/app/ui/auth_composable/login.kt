package com.capevi.app.ui.auth_composable

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.capevi.app.R
import com.capevi.app.ui.theme.Blue
import com.capevi.app.ui.theme.Neutral
import com.capevi.encryption.getCredentials
import com.capevi.navigation.Routes
import com.capevi.shared.utils.validateEmail
import com.capevi.shared.utils.validatePassword
import com.capevi.shared.widget.backgroundWidget
import com.capevi.shared.widget.customButton
import com.capevi.shared.widget.customTextField
import com.capevi.shared.widget.showDialog
import com.capevi.viewmodels.AuthState
import com.capevi.viewmodels.AuthViewModel
import java.util.concurrent.Executor

@Composable
fun login(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    val context = LocalContext.current

    val email =
        remember {
            mutableStateOf("")
        }
    val password =
        remember {
            mutableStateOf("")
        }
    var passwordVisible by remember { mutableStateOf(true) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val (savedEmail, savedPassword) = getCredentials(context)
    var showDialogValue by
        remember {
            mutableStateOf(false)
        }
    val activity = context as FragmentActivity
    val biometricManager = BiometricManager.from(context)
    val canAuthenticate = biometricManager.canAuthenticate()

    // Handle the result of the biometric authentication
    val biometricPrompt = remember { createBiometricPrompt(activity, authViewModel, savedEmail, savedPassword, context) }
    val promptInfo =
        remember {
            BiometricPrompt.PromptInfo
                .Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Authenticate using your fingerprint")
                .setNegativeButtonText("Cancel")
                .build()
        }
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(Unit) {
        authViewModel.resetAuthState()
    }
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Loading -> showDialogValue = true
            is AuthState.Authenticated -> {
                showDialogValue = false
                navController.navigate(Routes.MainScreen.route)
            }
            is AuthState.ForgotPasswordSuccess -> {
                showDialogValue = false
                Toast
                    .makeText(
                        context,
                        (authState.value as AuthState.ForgotPasswordSuccess).message,
                        Toast.LENGTH_SHORT,
                    ).show()
            }
            is AuthState.Error -> {
                showDialogValue = false
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
                    .padding(innerPadding),
        ) {
            showDialog(showDialogValue)
            backgroundWidget(
                title = stringResource(id = R.string.signIn),
                subTitle = stringResource(id = R.string.enterYourLoginDetails),
                navController = navController,
            )
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(50.dp))

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
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                customTextField(
                    isPassword = passwordVisible,
                    error = passwordError != null,
                    textState = password,
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
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(id = R.string.forgotPassword),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Blue[900]!!, fontSize = 12.sp),
                    modifier =
                        Modifier.fillMaxWidth(1f).clickable {
                            if (email.value.isNotEmpty()) {
                                authViewModel.forgotPassword(email.value)
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "Type in email to reset password",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }
                        },
                    textAlign = TextAlign.End,
                )
                Spacer(modifier = Modifier.height(80.dp))
                Row(modifier = Modifier.fillMaxWidth(1f)) {
                    customButton(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        enabled = emailError == null && passwordError == null,
                        onClick = {
                            if (email.value.trim().isEmpty()) {
                                emailError = "enter your email"
                                if ((password.value.trim().isEmpty())) {
                                    passwordError = "enter your password"
                                }
                            } else if (password.value.trim().isEmpty()) {
                                passwordError = "enter your password"
                            } else {
                                authViewModel.login(
                                    email.value.trim().lowercase(),
                                    password.value.trim(),
                                    context,
                                )
                            }
                        },
                        text = stringResource(id = R.string.login),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    when (canAuthenticate) {
                        BiometricManager.BIOMETRIC_SUCCESS -> {
                            // Show the button to trigger the fingerprint prompt
                            Image(
                                imageVector = ImageVector.vectorResource(id = R.drawable.biometrics),
                                contentDescription = "Biometric icon",
                                colorFilter = ColorFilter.tint(Blue[900]!!),
                                modifier =
                                    Modifier
                                        .height(55.dp)
                                        .width(100.dp)
                                        .size(100.dp)
                                        .clickable { biometricPrompt.authenticate(promptInfo) },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(id = R.string.dontHaveAnAccount),
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                color = Neutral[600]!!,
                                fontWeight = FontWeight.W500,
                                textAlign = TextAlign.Center,
                            ),
                    )
                    Text(
                        text = stringResource(id = R.string.signUp),
                        modifier =
                            Modifier
                                .clickable {
                                    navController.navigate(Routes.SignUp.route) {
                                        popUpTo(Routes.Login.route) {
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

private fun createBiometricPrompt(
    activity: FragmentActivity,
    authViewModel: AuthViewModel,
    email: String?,
    password: String?,
    context: Context,
): BiometricPrompt {
    val executor: Executor = ContextCompat.getMainExecutor(activity)
    return BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence,
            ) {
                super.onAuthenticationError(errorCode, errString)
                // Handle the error
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                if (email != null && password != null) {
                    authViewModel.login(
                        email,
                        password,
                        context,
                    )
                } else {
                    Toast
                        .makeText(
                            context,
                            "Account not found",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Handle failed authentication
            }
        },
    )
}
