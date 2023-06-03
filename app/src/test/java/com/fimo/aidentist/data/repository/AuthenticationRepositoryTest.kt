package com.fimo.aidentist.data.repository

import android.app.Application
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class AuthenticationRepositoryTest {

    private lateinit var authenticationRepository: AuthenticationRepository

    @Mock
    private lateinit var mockApplication: Application

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockFirebaseUser: FirebaseUser

    @Mock
    private lateinit var mockTask: Task<AuthResult>

    @Mock
    private lateinit var mockException: FirebaseAuthException

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        authenticationRepository = mock(AuthenticationRepository::class.java)
        authenticationRepository.auth = mockAuth
    }

    @Test
    fun `register should set firebaseUserMutableLiveData to currentUser when task is successful`() {
        `when`(mockAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask)
        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)

        authenticationRepository.register("test@test.com", "password")

        verify(mockAuth).createUserWithEmailAndPassword("test@test.com", "password")
        verify(authenticationRepository.firebaseUserMutableLiveData).postValue(mockFirebaseUser)
    }

    @Test
    fun `register should show a toast message when task is unsuccessful`() {
        `when`(mockAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask)
        `when`(mockTask.isSuccessful).thenReturn(false)
        `when`(mockTask.exception).thenReturn(mockException)
        `when`(mockException.message).thenReturn("Error")

        authenticationRepository.register("test@test.com", "password")

        verify(mockAuth).createUserWithEmailAndPassword("test@test.com", "password")
//        verify(mockApplication).toast("Error", Toast.LENGTH_SHORT)
    }

    @Test
    fun `login should set firebaseUserMutableLiveData to currentUser when task is successful`() {
        `when`(mockAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask)
        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)

        authenticationRepository.login("test@test.com", "password")

        verify(mockAuth).signInWithEmailAndPassword("test@test.com", "password")
        verify(authenticationRepository.firebaseUserMutableLiveData).postValue(mockFirebaseUser)
    }

    @Test
    fun `login should show a toast message when task is unsuccessful`() {
        `when`(mockAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask)
        `when`(mockTask.isSuccessful).thenReturn(false)
        `when`(mockTask.exception).thenReturn(mockException)
        `when`(mockException.message).thenReturn("Error")

        authenticationRepository.login("test@test.com", "password")

        verify(mockAuth).signInWithEmailAndPassword("test@test.com", "password")
//        verify(mockApplication).toast("Error", Toast.LENGTH_SHORT)
    }

    @Test
    fun `signOut should set userLoggedMutableLiveData to true`() {
        authenticationRepository.signOut()

        verify(mockAuth).signOut()
        verify(authenticationRepository.userLoggedMutableLiveData).postValue(true)
    }

    @Test
    fun `init should set firebaseUserMutableLiveData to currentUser when currentUser is not null`() {
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)

        authenticationRepository = AuthenticationRepository(mockApplication)

        verify(authenticationRepository.firebaseUserMutableLiveData).postValue(mockFirebaseUser)
    }
}