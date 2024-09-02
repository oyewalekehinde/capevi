package com.capevi.app.ui.case_composables

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.capevi.app.R
import com.capevi.app.ui.theme.Black
import com.capevi.app.ui.theme.Neutral
import com.capevi.data.model.CaseModel
import com.capevi.encryption.EncryptionKeyManager
import com.capevi.viewmodels.AuthViewModel
import com.capevi.viewmodels.CaseState
import com.capevi.viewmodels.CaseViewModel
import serializeToMap
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun viewCase(
    case: CaseModel,
    navController: NavHostController,
    caseViewModel: CaseViewModel,
    authViewModel: AuthViewModel,
) {
    val context = LocalContext.current

    val caseState = caseViewModel.caseState.observeAsState()
    val secretKey = EncryptionKeyManager.getKey()
    val scrollState = rememberScrollState()
    val openDialog = remember { mutableStateOf(false) }
    LaunchedEffect(caseState.value) {
        when (caseState.value) {
            is CaseState.Loaded -> {
                Toast
                    .makeText(
                        context,
                        (caseState.value as CaseState.Loaded).message,
                        Toast.LENGTH_SHORT,
                    ).show()
                navController.popBackStack()
            }
            is CaseState.Error -> {
                Toast
                    .makeText(
                        context,
                        (caseState.value as CaseState.Error).message,
                        Toast.LENGTH_SHORT,
                    ).show()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                title = {
                    androidx.wear.compose.material.Text(
                        text = case.title,
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                color = Neutral[950]!!,
                                fontWeight = FontWeight.W500,
                                textAlign = TextAlign.Center,
                            ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val caseFile = caseViewModel.caseEvidenceFileList.toMutableList()
                        caseFile.clear()
                        navController
                            .popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    PopUpMenuExample(case, navController, context, openDialog, authViewModel)
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
        ) {
            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = { openDialog.value = false }, // Dismiss when touching outside the dialog
                    title = {
                        Text(text = "Confirm Deletion")
                    },
                    text = {
                        Text(text = "Are you sure you want to delete this case?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                caseViewModel.deleteCase(case.id!!, case.serializeToMap())
                                openDialog.value = false
                            },
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { openDialog.value = false },
                        ) {
                            Text("Cancel")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            textItem(stringResource(id = R.string.title), case.title)
            Spacer(modifier = Modifier.height(10.dp))
            textItem(stringResource(id = R.string.description), case.description)

            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.status),
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            color = Black[950]!!,
                            fontWeight = FontWeight.W500,
                        ),
                )
                Spacer(modifier = Modifier.height(5.dp))
                CaseStatus(status = case?.status ?: "")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.evidence),
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            color = Black[950]!!,
                            fontWeight = FontWeight.W500,
                        ),
                )
                Spacer(modifier = Modifier.height(5.dp))
                FlowRow {
                    case.evidenceList.forEach { evidence ->
                        Box(
                            modifier =
                                Modifier
                                    .padding(end = 10.dp, bottom = 10.dp),
                        ) {
                            DecryptedImageFromUrl(url = evidence, secretKey!!, navController = navController, caseViewModel = caseViewModel)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            textItem(stringResource(id = R.string.timeOfLog), case.loggedAt.format(DateTimeFormatter.ofPattern("hh:mma")).uppercase())
            Spacer(modifier = Modifier.height(10.dp))
            textItem(
                stringResource(id = R.string.dateOfLog),
                case.loggedAt.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)),
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
