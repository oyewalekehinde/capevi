package com.capevi.app.ui.case_composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.capevi.app.ui.theme.Neutral
import com.capevi.data.model.CaseModel
import com.capevi.viewmodels.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun seeAllCases(
    navController: NavHostController,
    viewModel: SharedViewModel,
    cases: List<CaseModel>,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        text = "Cases",
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
                        navController
                            .popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
        ) {
            items(
                count =
                    cases.size,
            ) { index ->
                caseItem(cases[index], navController, viewModel)
            }
        }
    }
}
