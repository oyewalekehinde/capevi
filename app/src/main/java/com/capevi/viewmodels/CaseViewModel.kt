package com.capevi.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capevi.data.model.AuditTrail
import com.capevi.data.model.CaseModel
import com.capevi.shared.utils.DatabaseConstant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import serializeToMap
import javax.inject.Inject

@HiltViewModel
class CaseViewModel
    @Inject
    constructor() : ViewModel() {
        private val _cases = MutableStateFlow<List<CaseModel>>(emptyList())
        val cases: StateFlow<List<CaseModel>> = _cases
        private val _firestore = FirebaseFirestore.getInstance()
        private val _caseState = MutableLiveData<CaseState>()
        val caseState: LiveData<CaseState> = _caseState
        private val _initializedValue = MutableStateFlow<Boolean>(false)
        val initializedValue: StateFlow<Boolean> = _initializedValue
        var caseEvidenceFileList: List<String> = emptyList()

        var caseTitle by mutableStateOf("")
            private set

        var evidenceList by mutableStateOf(mutableListOf<String>())
            private set

        var caseDescription by mutableStateOf("")
            private set

        var evidence by mutableStateOf("")
            private set

        var status by mutableStateOf("")
            private set

        var time by mutableStateOf("")
            private set

        var date by mutableStateOf("")
            private set

        // Functions to update the state
        fun onCaseTitleChanged(newValue: String) {
            caseTitle = newValue
        }

        fun onCaseDescriptionChanged(newValue: String) {
            caseDescription = newValue
        }

        fun onCaseEvidenceChanged(newValue: String) {
            evidence = newValue
        }

        fun onCaseStatusChanged(newValue: String) {
            status = newValue
        }

        fun onCaseTimeChanged(newValue: String) {
            time = newValue
        }

        fun onCaseDateChanged(newValue: String) {
            date = newValue
        }

        fun resetState() {
            _caseState.value = CaseState.Initial
        }

        fun clearTextFields() {
            caseTitle = ""
            caseDescription = ""
            evidence = ""
            time = ""
            date = ""
            status = ""
        }

        fun initializeTextFields(case: CaseModel) {
            caseTitle = case.title
            caseDescription = case.description
            evidence = case.evidenceType
            time = String.format("%02d:%02d", case.loggedAt.hour, case.loggedAt.minute)
            date = "${case.loggedAt.dayOfMonth}/${case.loggedAt.month.value}/${case.loggedAt.year}"
            status = case.status ?: ""
            evidenceList = case?.evidenceList?.toMutableList() ?: mutableListOf()
        }

        fun getTotalCases() {
            _initializedValue.value = true
            _caseState.value = CaseState.LoadingCases
            try {
                _firestore
                    .collection(DatabaseConstant.CASE)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val documents =
                            querySnapshot.documents.mapNotNull { doc ->
                                doc.data?.let { gson.fromJson(gson.toJson(doc.data), CaseModel::class.java) }
                            }
                        _caseState.value = CaseState.LoadedCases(cases = documents)
                        _cases.value = documents
                    }.addOnFailureListener { exception ->

                        throw Exception(exception)
                    }
            } catch (error: Exception) {
                _caseState.value = CaseState.ErrorLoadingCases(message = error?.message ?: "something went wrong")
            }
        }

        fun createCase(caseData: Map<String, Any>) {
            _caseState.value = CaseState.Loading
            try {
                _firestore
                    .collection(DatabaseConstant.CASE)
                    .add(
                        caseData,
                    ).addOnSuccessListener { documentReference ->

                        val generatedId = documentReference.id
                        val updatedCaseMap: MutableMap<String, Any> = caseData.toMutableMap()
                        updatedCaseMap?.put(key = "id", value = generatedId)

                        _firestore
                            .collection(DatabaseConstant.CASE)
                            .document(generatedId)
                            .set(updatedCaseMap)
                            .addOnSuccessListener {
                                val auditTrail =
                                    AuditTrail(
                                        userId = updatedCaseMap.get("officerId").toString(),
                                        action = "CREATE",
                                        description = "Created a new case: ${updatedCaseMap.get("title")}",
                                        entityName = "Case",
                                        entityId = documentReference.id,
                                        newValue = updatedCaseMap.toString(), // or serialize to JSON
                                    )
                                recordAuditTrail(auditTrail)
                                _caseState.value = CaseState.Loaded("case saved")
                                _initializedValue.value = false
                            }.addOnFailureListener { e ->
                                throw e
                            }
                    }.addOnFailureListener { e ->
                        _caseState.value = CaseState.Error(e?.message ?: "something went wrong")
                    }
            } catch (e: Exception) {
                _caseState.value = CaseState.Error(e?.message ?: "something went wrong")
            }
        }

        fun updateCase(
            caseData: Map<String, Any>,
            oldCase: Map<String, Any>,
            id: String,
        ) {
            _caseState.value = CaseState.Loading
            try {
                _firestore
                    .collection(DatabaseConstant.CASE)
                    .document(id)
                    .update(
                        caseData,
                    ).addOnSuccessListener {
                        val auditTrail =
                            AuditTrail(
                                userId = caseData.get("officerId").toString(),
                                action = "UPDATE",
                                description = "Updated case:  ${caseData.get("title")}",
                                entityName = "Case",
                                entityId = id,
                                oldValue = oldCase.toString(), // or serialize to JSON
                                newValue = caseData.toString(), // or serialize to JSON
                            )
                        recordAuditTrail(auditTrail)
                        _caseState.value = CaseState.Loaded("case updated")
                        _initializedValue.value = false
                    }.addOnFailureListener { error -> throw error }
            } catch (error: Exception) {
                _caseState.value = CaseState.Error(error?.message ?: "something went wrong")
            }
        }

        fun deleteCase(
            id: String,
            case: Map<String, Any>,
        ) {
            _caseState.value = CaseState.Loading
            _firestore
                .collection(DatabaseConstant.CASE)
                .document(id)
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val auditTrail =
                            AuditTrail(
                                userId = case.get("officerId").toString(),
                                action = "DELETE",
                                description = "Deleted case: ${case.get("title")}",
                                entityName = "Case",
                                entityId = id,
                                oldValue = case.toString(), // or serialize to JSON
                            )
                        recordAuditTrail(auditTrail)
                        _caseState.value = CaseState.Loaded("case deleted")
                        _initializedValue.value = false
                    } else {
                        _caseState.value = CaseState.Error(task.exception?.message ?: "something went wrong")
                    }
                }
        }

        fun recordAuditTrail(auditTrail: AuditTrail) {
            _firestore
                .collection(DatabaseConstant.AUDITTRAIL)
                .add(auditTrail.serializeToMap())
                .addOnSuccessListener {
                    Log.d("AuditTrail", "Audit trail recorded successfully")
                }.addOnFailureListener { e ->
                    Log.e("AuditTrail", "Error recording audit trail", e)
                }
        }
    }

sealed class CaseState {
    data object Initial : CaseState()

    data object Loading : CaseState()

    data class Loaded(
        val message: String,
    ) : CaseState()

    data class Error(
        val message: String,
    ) : CaseState()

    data object LoadingCases : CaseState()

    data class LoadedCases(
        val cases: List<CaseModel>,
    ) : CaseState()

    data class ErrorLoadingCases(
        val message: String,
    ) : CaseState()
}

suspend fun uploadToFireStore(
    context: Context,
    dataBaseUrl: String,
    data: Map<String, Any>,
) {
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    try {
        firestore
            .collection(dataBaseUrl)
            .document()
            .set(
                data,
            ).await()
        Toast
            .makeText(
                context,
                "Evidence uploaded successfully",
                Toast.LENGTH_SHORT,
            ).show()
    } catch (e: Exception) {
        Toast
            .makeText(
                context,
                "Failed to save to database",
                Toast.LENGTH_SHORT,
            ).show()
    }
}
