package com.capevi.app.ui.case_composables

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.capevi.app.R
import com.capevi.app.ui.theme.Black
import com.capevi.app.ui.theme.Neutral
import com.capevi.data.model.CaseModel
import com.capevi.encryption.EncryptionKeyManager
import com.capevi.encryption.saveListOfEncryptedFiles
import com.capevi.navigation.Routes
import com.capevi.shared.utils.formatDate
import com.capevi.shared.utils.formatTime
import com.capevi.shared.widget.customButton
import com.capevi.shared.widget.customTextField
import com.capevi.shared.widget.showDialog
import com.capevi.viewmodels.AuthViewModel
import com.capevi.viewmodels.CaseState
import com.capevi.viewmodels.CaseViewModel
import com.capevi.viewmodels.FileViewModel
import com.capevi.viewmodels.SharedViewModel
import com.capevi.viewmodels.UploadFileState
import kotlinx.coroutines.launch
import serializeToMap
import java.io.File
import java.time.LocalDateTime
import java.util.Calendar

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class,
)
@Composable
fun createCase(
    navController: NavHostController,
    viewModel: SharedViewModel,
    caseViewModel: CaseViewModel,
    fileViewModel: FileViewModel,
    authViewModel: AuthViewModel,
    case: CaseModel? = null,
) {
    val bottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )
    val scope = rememberCoroutineScope()
    val evidenceList =
        remember {
            mutableStateOf(listOf("Image", "Video", "Recording", "Others"))
        }
    val uploadEvidence =
        remember {
            mutableStateOf("")
        }
    val evidenceListTitle =
        remember {
            mutableStateOf("")
        }
    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
            onResult = { uri: List<Uri>? ->
                uri?.let {
                    viewModel.addToExisitingList(uri)
                }
            },
        )

    val user by authViewModel.user.collectAsState()
    val scrollState = rememberScrollState()
    val dataList by viewModel.dataList.collectAsState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val showDialogValue =
        remember {
            mutableStateOf(false)
        }
    val secretKey = EncryptionKeyManager.getKey()
    val fileUploadState = fileViewModel.uploadFileState.observeAsState()
    val caseState = caseViewModel.caseState.observeAsState()
    LaunchedEffect(Unit) {
        fileViewModel.resetFileState()
        caseViewModel.resetState()
        if (case != null) {
            case?.evidenceList?.let { viewModel.addToExisitingList(it) }
            caseViewModel.initializeTextFields(case)
        }
    }
    BackHandler {
        navController.popBackStack()
        caseViewModel.clearTextFields()
        viewModel.clearList()
    }
    LaunchedEffect(fileUploadState.value) {
        when (fileUploadState.value) {
            is UploadFileState.UploadingFile -> showDialogValue.value = true
            is UploadFileState.UploadedFile -> {
                val data =
                    CaseModel(
                        title = caseViewModel.caseTitle,
                        description = caseViewModel.caseDescription,
                        evidenceType = caseViewModel.evidence,
                        loggedAt = LocalDateTime.of(formatDate(caseViewModel.date), formatTime(caseViewModel.time)),
                        evidenceList = (fileUploadState.value as UploadFileState.UploadedFile).downLoadUrls,
                        createdAt = if (case != null) case?.createdAt!! else LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                        officerId = user?.id!!,
                        officerName = "${user?.firstName} ${user?.lastName}",
                        status = if (caseViewModel.status.isEmpty()) "Opened" else caseViewModel.status,
                    )
                if (case != null) {
                    caseViewModel.updateCase(data.serializeToMap(), case.serializeToMap(), case?.id!!)
                } else {
                    caseViewModel.createCase(data.serializeToMap())
                }
            }
            is UploadFileState.UploadedError -> {
                showDialogValue.value = false
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
    LaunchedEffect(caseState.value) {
        when (caseState.value) {
            is CaseState.Loading -> if (case != null) showDialogValue.value = true
            is CaseState.Loaded -> {
                showDialogValue.value = false
                Toast
                    .makeText(
                        context,
                        (caseState.value as CaseState.Loaded).message,
                        Toast.LENGTH_SHORT,
                    ).show()

                navController.popBackStack()
                caseViewModel.clearTextFields()
            }
            is CaseState.Error -> {
                showDialogValue.value = false
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
                    Text(
                        text = stringResource(id = if (case != null) R.string.updateCase else R.string.createACase),
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
                        navController.popBackStack()
                        caseViewModel.clearTextFields()
                        viewModel.clearList()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            scrimColor = Color.Transparent,
            sheetBackgroundColor = Color.White,
            sheetElevation = 4.dp,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetContent = {
                Box(
                    modifier =
                        Modifier
                            .padding(16.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(1f)) {
                        Box(modifier = Modifier.height(5.dp).width(60.dp).background(Color(0xFFE0E0E0), shape = RoundedCornerShape(5.dp)))
                        Spacer(modifier = Modifier.height(15.dp))

                        Text(
                            text = evidenceListTitle.value,
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 18.sp,
                                    color = Black[950]!!,
                                    fontWeight = FontWeight.W500,
                                    textAlign = TextAlign.Center,
                                ),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        evidenceList.value.forEach { str ->
                            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth(1f)) {
                                Text(
                                    str,
                                    modifier =
                                        Modifier.clickable {
//                                            uploadEvidence.value = str
//                                            caseViewModel.onCaseStatusChanged(str)

                                            if (evidenceListTitle.value == "Evidence Type") {
                                                caseViewModel.onCaseEvidenceChanged(str)
                                            } else if (evidenceListTitle.value == "Update Status") {
                                                uploadEvidence.value = ""
                                                caseViewModel.onCaseStatusChanged(str)
                                            } else {
                                                uploadEvidence.value = str
                                            }
                                            scope.launch {
                                                bottomSheetState.hide()

                                                if (uploadEvidence.value == "Camera" && caseViewModel.evidence == "Image") {
                                                    navController.navigate(Routes.CameraScreen.route + "/false")
                                                }
                                                if (uploadEvidence.value == "Gallery" && caseViewModel.evidence == "Image") {
                                                    galleryLauncher.launch("image/*")
                                                }
                                                if (uploadEvidence.value == "Camera" && caseViewModel.evidence == "Video") {
                                                    navController.navigate(Routes.CameraScreen.route + "/true")
                                                }
                                                if (uploadEvidence.value == "Gallery" && caseViewModel.evidence == "Video") {
                                                    galleryLauncher.launch("video/*")
                                                }
                                            }
                                        },
                                    style =
                                        MaterialTheme.typography.bodyLarge.copy(
                                            fontSize = 14.sp,
                                            color = Neutral[950]!!,
                                            fontWeight = FontWeight.W500,
                                            textAlign = TextAlign.Center,
                                        ),
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                        }
                    }
                }
            },
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp)
                        .fillMaxSize()
                        .verticalScroll(scrollState),
            ) {
                showDialog(showDialogValue.value)
                customTextField(
                    textValue = caseViewModel.caseTitle,
                    placeHolder = stringResource(id = R.string.enterTitle) + " " + stringResource(id = R.string.caseTitle),
                    label = stringResource(id = R.string.caseTitle),
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                    onValueChanged =
                        caseViewModel::onCaseTitleChanged,
                )
                Spacer(modifier = Modifier.height(10.dp))

                customTextField(
                    textValue = caseViewModel.caseDescription,
                    placeHolder = stringResource(id = R.string.enterTitle) + " " + stringResource(id = R.string.caseDescription),
                    label = stringResource(id = R.string.caseDescription),
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                    maxLines = 5,
                    minLines = 3,
                    onValueChanged =
                        caseViewModel::onCaseDescriptionChanged,
                )

                if (case != null) Spacer(modifier = Modifier.height(10.dp))
                if (case != null) {
                    customTextField(
                        textValue = caseViewModel.status,
                        placeHolder = stringResource(id = R.string.caseStatus),
                        label = stringResource(id = R.string.caseStatus),
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences,
                        suffixIcon = { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "ArrowDropDown") },
                        readOnly = true,
                        enabled = false,
                        onValueChanged =
                            caseViewModel::onCaseStatusChanged,
                        onTextFieldClick = {
                            keyboardController?.hide()
                            scope.launch {
                                evidenceList.value = listOf("Investigating", "Reviewed", "Resolved", "Closed")
                                evidenceListTitle.value = "Update Status"
                                bottomSheetState.show()
                            }
                        },
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                customTextField(
                    textValue = caseViewModel.evidence,
                    placeHolder = stringResource(id = R.string.enterTitle) + " " + stringResource(id = R.string.evidenceType),
                    label = stringResource(id = R.string.evidenceType),
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                    suffixIcon = { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "ArrowDropDown") },
                    readOnly = true,
                    enabled = false,
                    onValueChanged =
                        caseViewModel::onCaseEvidenceChanged,
                    onTextFieldClick = {
                        keyboardController?.hide()
                        scope.launch {
                            evidenceList.value = listOf("Image", "Video", "Recording", "Others")
                            evidenceListTitle.value = "Evidence Type"
                            bottomSheetState.show()
                        }
                    },
                )
                if (caseViewModel.evidence.isNotEmpty()) Spacer(modifier = Modifier.height(20.dp))
                if (caseViewModel.evidence.isNotEmpty()) {
                    Text(
                        stringResource(id = R.string.uploadEvidence),
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 12.sp,
                                color = Black[950]!!,
                                fontWeight = FontWeight.W500,
                                textAlign = TextAlign.Center,
                            ),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (caseViewModel.evidence.isNotEmpty()) {
                    if (dataList.isEmpty()) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth(1f)
                                    .height(141.dp)
                                    .background(Neutral[50]!!, shape = RoundedCornerShape(8.dp))
                                    .clickable {
                                        scope.launch {
                                            if (caseViewModel.evidence == "Others") {
                                                galleryLauncher.launch("*/*")
                                            } else if (caseViewModel.evidence == "Recording") {
                                                navController.navigate(Routes.Recording.route)
                                            } else {
                                                evidenceListTitle.value = "Upload From"
                                                evidenceList.value = listOf("Camera", "Gallery")
                                                bottomSheetState.show()
                                            }
                                        }
                                    },
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(id = R.drawable.file_upload),
                                contentDescription = "Camera Icon",
                                modifier =
                                    Modifier
                                        .height(32.dp)
                                        .width(32.dp)
                                        .size(36.dp),
                            )
                        }
                    } else {
//                        if (case?.evidenceList != null) {
//                            if (case.evidenceList.isNotEmpty()) {
//                                FlowRow {
//                                    case.evidenceList.forEach { evidence ->
//                                        Box(
//                                            modifier =
//                                                Modifier
//                                                    .padding(end = 10.dp, bottom = 10.dp),
//                                        ) {
//                                            DecryptedImageFromUrl(
//                                                url = evidence,
//                                                secretKey!!,
//                                                navController = navController,
//                                                caseViewModel = caseViewModel,
//                                            )
//                                        }
//                                    }
//                                    Box(
//                                        modifier =
//                                            Modifier
//                                                .height(
//                                                    100.dp,
//                                                ).width(100.dp)
//                                                .background(Neutral[50]!!, shape = RoundedCornerShape(12.dp))
//                                                .clickable {
//                                                    scope.launch {
//                                                        if (caseViewModel.evidence == "Others") {
//                                                            galleryLauncher.launch("*/*")
//                                                        } else {
//                                                            evidenceList.value = listOf("Camera", "Gallery")
//                                                            bottomSheetState.show()
//                                                        }
//                                                    }
//                                                },
//                                        contentAlignment = Alignment.Center,
//                                    ) {
//                                        Image(
//                                            imageVector = ImageVector.vectorResource(id = R.drawable.file_upload),
//                                            contentDescription = "Camera Icon",
//                                            modifier =
//                                                Modifier
//                                                    .height(32.dp)
//                                                    .width(32.dp)
//                                                    .size(36.dp),
//                                        )
//                                    }
//                                }
//                            }
//                        } else {
//
                        FlowRow {
                            dataList.forEach { image ->

                                Box(
                                    modifier =
                                        Modifier
                                            .padding(end = 8.dp, bottom = 8.dp),
                                ) {
                                    if (image is Uri) {
                                        MediaPreview(Uri.parse(image.toString()), navController)
                                    } else {
                                        DecryptedImageFromUrl(
                                            url = image as String,
                                            secretKey!!,
                                            navController = navController,
                                            caseViewModel = caseViewModel,
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier =
                                            Modifier
                                                .width(100.dp)
                                                .clickable {
                                                    viewModel.removeMedia(image)
                                                },
                                    ) {
                                        Icon(
                                            modifier =
                                                Modifier
                                                    .height(
                                                        28.dp,
                                                    ).width(28.dp)
                                                    .padding(4.dp)
                                                    .background(
                                                        color = Color.White,
                                                        shape = RoundedCornerShape(20.dp),
                                                    ),
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close",
                                        )
                                    }
                                }
                            }
                            Box(
                                modifier =
                                    Modifier
                                        .height(
                                            100.dp,
                                        ).width(100.dp)
                                        .background(Neutral[50]!!, shape = RoundedCornerShape(12.dp))
                                        .clickable {
                                            scope.launch {
                                                if (caseViewModel.evidence == "Others") {
                                                    galleryLauncher.launch("*/*")
                                                } else if (caseViewModel.evidence == "Recording") {
                                                    navController.navigate(Routes.Recording.route)
                                                } else {
                                                    evidenceListTitle.value = "Upload From"
                                                    evidenceList.value = listOf("Camera", "Gallery")
                                                    bottomSheetState.show()
                                                }
                                            }
                                        },
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.file_upload),
                                    contentDescription = "Camera Icon",
                                    modifier =
                                        Modifier
                                            .height(32.dp)
                                            .width(32.dp)
                                            .size(36.dp),
                                )
                            }
                        }
                    }
                }
                if (caseViewModel.evidence.isNotEmpty()) Spacer(modifier = Modifier.height(20.dp))
                timePicker(
                    context = LocalContext.current,
                    initialHour =
                        if (caseViewModel.time.isNotEmpty()) {
                            formatTime(
                                caseViewModel.time,
                            ).hour
                        } else {
                            Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        },
                    initialMinute =
                        if (caseViewModel.time.isNotEmpty()) {
                            formatTime(
                                caseViewModel.time,
                            ).minute
                        } else {
                            Calendar.getInstance().get(Calendar.MINUTE)
                        },
                    caseViewModel,
                ) { hour, minute ->
                    caseViewModel.onCaseTimeChanged(String.format("%02d:%02d", hour, minute))
                }
                Spacer(modifier = Modifier.height(10.dp))
                showDatePicker(context = LocalContext.current, caseViewModel)
                Spacer(modifier = Modifier.height(50.dp))

                customButton(text = stringResource(id = if (case != null) R.string.updateCase else R.string.submitCase), onClick = {
                    if (caseViewModel.caseTitle.isEmpty()) {
                        Toast.makeText(context, "enter case title", Toast.LENGTH_SHORT).show()
                    } else if (caseViewModel.caseDescription.isEmpty()) {
                        Toast.makeText(context, "enter case description", Toast.LENGTH_SHORT).show()
                    } else if (caseViewModel.evidence.isEmpty()) {
                        Toast.makeText(context, "choose evidence type", Toast.LENGTH_SHORT).show()
                    } else if (caseViewModel.time.isEmpty()) {
                        Toast.makeText(context, "choose a time", Toast.LENGTH_SHORT).show()
                    } else if (caseViewModel.date.isEmpty()) {
                        Toast.makeText(context, "choose a date", Toast.LENGTH_SHORT).show()
                    } else {
                        if (dataList.isEmpty()) {
                            val data =
                                CaseModel(
                                    title = caseViewModel.caseTitle,
                                    description = caseViewModel.caseDescription,
                                    evidenceType = caseViewModel.evidence,
                                    loggedAt = LocalDateTime.of(formatDate(caseViewModel.date), formatTime(caseViewModel.time)),
                                    evidenceList = emptyList(),
                                    createdAt = if (case != null) case?.createdAt!! else LocalDateTime.now(),
                                    updatedAt = LocalDateTime.now(),
                                    officerId = user?.id!!,
                                    officerName = "${user?.firstName} ${user?.lastName}",
                                    status = "opened",
                                )
                            if (case != null) {
                                caseViewModel.updateCase(data.serializeToMap(), case.serializeToMap(), case?.id!!)
                            } else {
                                caseViewModel.createCase(data.serializeToMap())
                            }
                        } else {
                            val encryptedFilesPath =
                                saveListOfEncryptedFiles(context, dataList as List<Uri>)
                                    .map { file ->
                                        if (file is File) file.path else file
                                    }.toList()

                            fileViewModel.uploadFile(files = encryptedFilesPath.map { e -> e.toString() }.toList())
                        }
                    }
                })
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
