package com.fimo.aidentist.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fimo.aidentist.data.repository.AuthenticationRepository
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application): AndroidViewModel(application) {
    private val repository: AuthenticationRepository
    val userData: MutableLiveData<FirebaseUser?>?
    val loggedStatus: MutableLiveData<Boolean?>?

    fun register(email: String?, pass: String?) {
        repository.register(email, pass)
    }

    fun signIn(email: String?, pass: String?) {
        repository.login(email, pass)
    }

    fun signOut() {
        repository.signOut()
    }

    init {
        repository = AuthenticationRepository(application)
        userData = repository.firebaseUserMutableLiveData
        loggedStatus = repository.userLoggedMutableLiveData
    }
}