package com.capevi.app.ui.home_composables

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.capevi.app.R
import com.capevi.app.ui.case_composables.caseItem
import com.capevi.app.ui.theme.Black
import com.capevi.app.ui.theme.Blue
import com.capevi.app.ui.theme.Neutral
import com.capevi.navigation.Routes
import com.capevi.network.getAddressFromLocationIQ
import com.capevi.shared.widget.cachedImage
import com.capevi.shared.widget.customButton
import com.capevi.shared.widget.shimmerEffect
import com.capevi.viewmodels.AuthViewModel
import com.capevi.viewmodels.CaseState
import com.capevi.viewmodels.CaseViewModel
import com.capevi.viewmodels.SharedViewModel
import gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun homeScreen(
    navController: NavHostController,
    caseViewModel: CaseViewModel,
    authViewModel: AuthViewModel,
    viewModel: SharedViewModel,
) {
    val imageUrl2 = "https://firebasestorage.googleapis.com/v0/b/kedari-user.appspot.com/o/uploads%2FprofileImage%2Fimage_picker_2765E9F7-A814-4B7F-8408-CA25909447A4-7210-00000344BEF39AA5.jpg?alt=media&token=2365ecc5-0764-48ad-afa2-8dbbe30457f0"
    val cases by caseViewModel.cases.collectAsState()

    val context = LocalContext.current
    val showDialogValue =
        remember {
            mutableStateOf(false)
        }
    val caseStates = caseViewModel.caseState.observeAsState()
    val scrollState = rememberScrollState()
    val user by authViewModel.user.collectAsState()
    var address by remember { mutableStateOf("Fetching address...") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    GetUserLocation { lat, lon ->
        latitude = lat
        longitude = lon
    }
    LaunchedEffect(latitude, longitude) {
        latitude?.let { lat ->
            longitude?.let { lon ->
                address = getAddressFromLocationIQ(lat, lon)
            }
        }
    }
    LaunchedEffect(Unit) {
        if (!caseViewModel.initializedValue.value) {
            caseViewModel.getTotalCases()
        }
    }
    LaunchedEffect(caseStates.value) {
        println(caseStates.value)
        when (caseStates.value) {
            is CaseState.LoadingCases -> showDialogValue.value = true
            is CaseState.LoadedCases -> {
                showDialogValue.value = false
            }
            is CaseState.ErrorLoadingCases -> {
                showDialogValue.value = false
                Toast
                    .makeText(
                        context,
                        (caseStates.value as CaseState.Error).message,
                        Toast.LENGTH_SHORT,
                    ).show()
            }
            else -> Unit
        }
    }
    Scaffold { innerPadding ->
        Box(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(20.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    cachedImage(
                        height = 50.dp,
                        width = 50.dp,
                        borderRadius = 50.dp,
                        url = imageUrl2,
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            text = "${user?.firstName} ${user?.lastName}",
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 16.sp,
                                    color = Black[950]!!,
                                    fontWeight = FontWeight.W400,
                                    textAlign = TextAlign.Center,
                                ),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = address,
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 14.sp,
                                    color = Black[600]!!,
                                    fontWeight = FontWeight.W500,
                                    textAlign = TextAlign.Center,
                                ),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier =
                        Modifier
                            .height(
                                144.dp,
                            ).fillMaxWidth(1f)
                            .background(
                                color = Blue[100]!!,
                                shape = RoundedCornerShape(16.dp),
                            ).padding(start = 20.dp, end = 20.dp, top = 20.dp),
                ) {
                    Column {
                        Box(
                            modifier =
                                Modifier
                                    .height(36.dp)
                                    .width(36.dp)
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(12.dp),
                                    ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(id = R.drawable.wallet_icon),
                                contentDescription = "Wallet Icon",
                                modifier =
                                    Modifier
                                        .height(16.dp)
                                        .width(16.dp)
                                        .size(20.dp),
                                // Adjust size as needed
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = stringResource(id = R.string.allCases),
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 14.sp,
                                    color = Neutral[950]!!,
                                    fontWeight = FontWeight.W500,
                                    textAlign = TextAlign.Center,
                                ),
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "${cases.size}",
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 24.sp,
                                    color = Neutral[950]!!,
                                    fontWeight = FontWeight.W700,
                                    textAlign = TextAlign.Center,
                                ),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(1f)) {
                    Text(
                        text = stringResource(id = R.string.recentCase),
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                color = Black[950]!!,
                                fontWeight = FontWeight.W600,
                                textAlign = TextAlign.Center,
                            ),
                    )
                    Text(
                        text = stringResource(id = R.string.seeAll),
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                color = Blue[900]!!,
                                fontWeight = FontWeight.W500,
                                textAlign = TextAlign.Center,
                            ),
                        modifier =
                            Modifier.clickable {
                                val caseTojson = gson.toJson(cases)
                                val encodedJson = URLEncoder.encode(caseTojson, StandardCharsets.UTF_8.toString())
//                                viewModel.clearList()
                                navController.navigate(Routes.SeeAllCases.route + "/$encodedJson")
                            },
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                if (showDialogValue.value) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(5) { _ ->
                            Column {
                                shimmerEffect(height = 100.dp)
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                        }
                    }
                } else {
                    if (cases.isEmpty()) {
                        emptyCaseWidget()
                    } else {
                        LazyColumn(
                            modifier =
                                Modifier
                                    .weight(1f) // This makes the list expand to fill available space
                                    .fillMaxWidth(),
                        ) {
                            items(
                                count =
                                    if (cases.size > 4) {
                                        4
                                    } else {
                                        cases.size
                                    },
                            ) { index ->
                                caseItem(cases[index], navController, viewModel)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))

                customButton(text = stringResource(id = R.string.createACase), onClick = {
                    viewModel.clearList()
                    navController.navigate(Routes.CreateCase.createRoute(null))
                })
            }
        }
    }
}
