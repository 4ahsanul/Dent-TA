package com.fimo.aidentist.data.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fimo.aidentist.data.repository.AuthenticationRepository
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

//class AuthViewModelTest {
//
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    @Mock
//    private lateinit var application: Application
//
//    @Mock
//    private lateinit var repository: AuthenticationRepository
//
//    private lateinit var viewModel: AuthViewModel
//
//    @Before
//    fun setup() {
//        MockitoAnnotations.initMocks(this)
//
//        viewModel = AuthViewModel(application, repository)
//    }
//
//    @Test
//    fun `signIn success`() {
//        val email = "testmvvm@gmail.com"
//        val password = "12345678"
//        val firebaseUser: FirebaseUser = mock(FirebaseUser::class.java)
//
//        `when`(repository.firebaseUserMutableLiveData).thenReturn(MutableLiveData<FirebaseUser?>().apply {
//            value = firebaseUser
//        })
//
//        viewModel.signIn(email, password)
//
//        verify(repository).login(email, password)
//        assertEquals(firebaseUser, viewModel.userData.value)
//    }
//
//    @Test
//    fun testSignInFailure() {
//        val email = "test@example.com"
//        val password = "password"
//
//        `when`(repository.firebaseUserMutableLiveData).thenReturn(MutableLiveData(null))
//
//        viewModel.signIn(email, password)
//
//        verify(repository).login(email, password)
//        assertEquals(null, viewModel.userData.value)
//    }
//}

