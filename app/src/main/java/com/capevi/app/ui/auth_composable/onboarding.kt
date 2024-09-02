package com.capevi.app.ui.auth_composable

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.capevi.app.R
import com.capevi.app.ui.theme.Blue
import com.capevi.navigation.Routes
import com.capevi.shared.widget.customButton

// class Onboarding : ComponentActivity() {
//    @OptIn(ExperimentalMaterial3Api::class)
//    private fun navigateToSignIn() {
//        val intent = Intent(this, Signin::class.java)
//        startActivity(intent)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            CapeviTheme {
//                Scaffold(
//                    modifier = Modifier.fillMaxSize(),
// //                    topBar = {
// //                        TopAppBar(
// //
// //                            title = {
// //                                Text(text = "My App")
// //                            },
// //                            colors = TopAppBarDefaults.topAppBarColors(containerColor =MaterialTheme.colorScheme.primary )
// //                        )
// //                    },
//                ) { innerPadding ->
//                    Box(
//                        modifier =
//                            Modifier
//                                .fillMaxSize()
//                                .padding(innerPadding),
//                    ) {
//                        onboardingWidget()
//                    }
//                }
//            }
//        }
//    }
// }

@Composable
fun onboarding(navController: NavHostController) {
    var currentPosition by remember {
        mutableIntStateOf(0)
    }
    val image = listOf<Int>(R.drawable.onboarding_1, R.drawable.onboarding_2)
    val permissions =
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
        )

    // State to hold the permission results
    val permissionResults = remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    // Launcher to request multiple permissions
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { results ->
            permissionResults.value = results
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = image[currentPosition]),
            contentDescription = "Onboarding 1",
            modifier =
                Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.6f),
            contentScale = ContentScale.FillBounds,
        )
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(id = R.string.capture_knowledge),
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W600,
                    textAlign = TextAlign.Center,
                ),
        )

        Text(
            text = stringResource(id = R.string.capture_knowledge_content),
            modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp),
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    color = colorResource(id = R.color.neutral800),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    textAlign = TextAlign.Center,
                ),
        )
        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            customButton(
                onClick = {
                    if (currentPosition == 1) {
                        navController.navigate(Routes.Login.route)
                    }
                    currentPosition = if (currentPosition == 0)1 else 0
                    launcher.launch(permissions.toTypedArray())
                },
                text = stringResource(id = if (currentPosition == 1) R.string.continue_string else R.string.next),
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.9f),
        ) {
            Row {
                indicator(currentPosition == 0)
                Spacer(modifier = Modifier.width(10.dp))
                indicator(currentPosition == 1)
            }

            Text(
                text = stringResource(id = if (currentPosition == 1) R.string.next else R.string.skip),
                modifier =
                    Modifier.clickable {
                        currentPosition = if (currentPosition == 0)1 else 0
                        if (currentPosition == 1) {
                            launcher.launch(permissions.toTypedArray())
                            navController.navigate(Routes.Login.route)
                        }
                    },
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        color = Blue[900]!!,
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.Center,
                    ),
            )
        }
    }
}

@Composable
fun indicator(selected: Boolean) {
    Box(
        modifier =
            Modifier
                .background(
                    color =

                        if (selected) Blue[900]!! else Blue[200]!!,
                    shape = RoundedCornerShape(40.dp),
                ).height(6.dp)
                .width(if (selected) 40.dp else 10.dp),
    )
}
