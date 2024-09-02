package com.capevi.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capevi.data.model.UserModel
import com.capevi.encryption.saveCredentials
import com.capevi.shared.utils.DatabaseConstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor() : ViewModel() {
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
        private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        private val _authState = MutableLiveData<AuthState>()
        private val _user = MutableStateFlow<UserModel?>(null)
        val user: StateFlow<UserModel?> = _user
        val authState: LiveData<AuthState> = _authState

        fun checkAuthStatus() {
            if (auth.currentUser != null) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }

        fun registerUser(
            fullName: String,
            email: String,
            password: String,
        ) {
            _authState.value = AuthState.Loading

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val names = fullName.split(" ")
                    val userData: MutableMap<String, Any> =
                        mutableMapOf(
                            "id" to user?.uid!!,
                            "firstName" to names[0],
                            "lastName" to names[1],
                            "image" to "",
                            "email" to email,
                        )
                    saveUserCredentials(userData)
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
        }

        fun login(
            email: String,
            password: String,
            context: Context,
        ) {
            try {
                _authState.value = AuthState.Loading

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        saveCredentials(context, email, password)
                        firestore
                            .collection(DatabaseConstant.USERS)
                            .document(user?.uid!!)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    _user.value =
                                        document.data?.let {
                                            gson.fromJson(gson.toJson(document.data), UserModel::class.java)
                                        }

                                    _authState.value = AuthState.Authenticated
                                } else {
                                    throw Exception("something went wrong")
                                }
                            }.addOnFailureListener { error -> throw error }
                    } else {
                        _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                    }
                }
            } catch (error: Exception) {
                _authState.value = AuthState.Error(error.message ?: "Something went wrong")
            }
        }

        private fun saveUserCredentials(data: Map<String, Any>) {
            _user.value =
                UserModel(
                    data["id"].toString(),
                    data["first_name"].toString(),
                    data["image"].toString(),
                    data["last_name"].toString(),
                    data["email"].toString(),
                )
            firestore.collection(DatabaseConstant.USERS).document(auth.currentUser?.uid ?: "").set(data)
        }

        fun forgotPassword(email: String) {
            _authState.value = AuthState.Loading

            auth.sendPasswordResetEmail(email.trim()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.ForgotPasswordSuccess("Password reset email sent.")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
        }

        fun deleteAccount() {
            _authState.value = AuthState.DeletingAccount

            auth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.DeletedAccount
                } else {
                    _authState.value = AuthState.DeletingAccountError(task.exception?.message ?: "Something went wrong")
                }
            }
        }

        fun resetAuthState() {
            _authState.value = AuthState.InitialState
        }
    }

sealed class AuthState {
    object InitialState : AuthState()

    object Authenticated : AuthState()

    object Unauthenticated : AuthState()

    object Loading : AuthState()

    object DeletingAccount : AuthState()

    object DeletedAccount : AuthState()

    data class DeletingAccountError(
        val message: String,
    ) : AuthState()

    data class ForgotPasswordSuccess(
        val message: String,
    ) : AuthState()

    data class Error(
        val message: String,
    ) : AuthState()
}
