package com.fimo.aidentist.ui.menu.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fimo.aidentist.R
import com.fimo.aidentist.data.viewmodel.AuthViewModel
import com.fimo.aidentist.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var fAuth: FirebaseAuth
    private var viewModel: AuthViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = Firebase.auth

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(this@SignUpActivity.application)
        ).get(AuthViewModel::class.java)
        viewModel!!.userData!!.observe(this) {
            Intent(this@SignUpActivity, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        playAnimation()
        setupAction()
        setupView()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageViewSignup, View.TRANSLATION_X, -30F, 30F).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            startDelay = 500
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1F).setDuration(500)
        val emailInput =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1F).setDuration(500)
        val passwordInput =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1F).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding.buttonSignUp, View.ALPHA, 1F).setDuration(500)
        val signupTitle =
            ObjectAnimator.ofFloat(binding.tvTittleLogin, View.ALPHA, 15F).setDuration(500)
        val signUpButton = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 15F).setDuration(500)

        //Show animate alternate
        AnimatorSet().apply {
            playSequentially(
                title,
                emailInput,
                passwordInput,
                button,
                signupTitle,
                signUpButton
            )
            start()
        }
    }

    private fun setupAction() {
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.buttonSignUp.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val repass = binding.repassEditText.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEditText.error = getString(R.string.email_invalid)
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }
            if (email.isNotEmpty() && password.isNotEmpty() && repass.isNotEmpty()) {
                if (password == repass) {
                    viewModel!!.register(email, password)
                    Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT)
                        .show()
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this, getString(R.string.pass_not_same), Toast.LENGTH_SHORT)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            } else {
                Toast.makeText(this, getString(R.string.form_warn), Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
            binding.progressBar.visibility = View.GONE
        }
        setupView()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}