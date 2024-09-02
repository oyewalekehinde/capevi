package com.capevi.app.ui.case_composables

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.capevi.app.ui.theme.Black
import com.capevi.app.ui.theme.Red
import com.capevi.data.model.AuditTrail
import com.capevi.data.model.CaseModel
import com.capevi.navigation.Routes
import com.capevi.shared.utils.DatabaseConstant
import com.capevi.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import generatePdf
import gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun PopUpMenuExample(
    case: CaseModel,
    navController: NavHostController,
    context: Context,
    openDialog: MutableState<Boolean>,
    authViewModel: AuthViewModel,
) {
    var expanded by remember { mutableStateOf(false) }

    var selectedItem by remember { mutableStateOf("") }
    val user by authViewModel.user.collectAsState()
    val items = if (case.officerId == user?.id) listOf("Update", "Delete", "Generate Report") else listOf("Delete", "Generate Report")
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.Black)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 16.sp,
                                    color = if (item == "Delete") Red[700]!! else Black[950]!!,
                                    fontWeight = FontWeight.W400,
                                ),
                        )
                    },
                    onClick = {
                        selectedItem = item
                        expanded = false
                        if (selectedItem == "Delete") {
                            openDialog.value = true
//
                        } else if (selectedItem == "Generate Report") {
                            fetchAuditTrailForCase(case.id!!) { auditTrails ->
                                generatePdf(context, case, auditTrails)
                            }
                        } else {
                            val caseToJson = gson.toJson(case)
                            val encodedJson = URLEncoder.encode(caseToJson, StandardCharsets.UTF_8.toString())
                            navController.navigate(Routes.CreateCase.createRoute(encodedJson))
                        }
                    },
                )
            }
        }
    }
}

fun fetchAuditTrailForCase(
    caseId: String,
    callback: (List<AuditTrail>) -> Unit,
) {
    val db = FirebaseFirestore.getInstance()
    db
        .collection(DatabaseConstant.AUDITTRAIL)
        .whereEqualTo("entityId", caseId)
        .get()
        .addOnSuccessListener { documents ->

            val auditTrails =
                documents.mapNotNull { doc ->
                    doc.data?.let { gson.fromJson(gson.toJson(doc.data), AuditTrail::class.java) }
                }
            callback(auditTrails)
        }.addOnFailureListener { e ->

            callback(emptyList())
        }
}
