package com.fimo.aidentist.ui.navigation.profile

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.fimo.aidentist.R
import com.fimo.aidentist.databinding.FragmentUpdateEmailBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class UpdateEmailFragment : Fragment() {
    private var _binding: FragmentUpdateEmailBinding? = null
    private val binding get() = _binding!!

    private lateinit var fAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateEmailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fAuth = FirebaseAuth.getInstance()

        val user = fAuth.currentUser

        binding.layoutPassword.visibility = View.VISIBLE
        binding.layoutEmail.visibility = View.GONE

        binding.buttonBack.setOnClickListener {
            val actionEmailUpdated = UpdateEmailFragmentDirections.actionEmailUpdated()
            Navigation.findNavController(view).navigate(actionEmailUpdated)
        }

        binding.btnAuth.setOnClickListener {
            val password = binding.etPassword.text.toString().trim()

            if (password.isEmpty()) {
                binding.etPassword.error = "Password harus diisi"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            user?.let {
                val userCredential = EmailAuthProvider.getCredential(it.email!!, password)
                it.reauthenticate(userCredential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        binding.layoutPassword.visibility = View.GONE
                        binding.layoutEmail.visibility = View.VISIBLE
                    } else if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.etPassword.error = "Password Salah"
                        binding.etPassword.requestFocus()
                    } else {
                        Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            binding.btnUpdate.setOnClickListener { view ->
                val email = binding.etEmail.text.toString().trim()

                if (email.isEmpty()) {
                    binding.etEmail.error = getString(R.string.email_warn)
                    binding.etEmail.requestFocus()
                    return@setOnClickListener
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.etEmail.error = getString(R.string.email_invalid)
                    binding.etEmail.requestFocus()
                    return@setOnClickListener
                }

                user?.let {
                    user.updateEmail(email).addOnCompleteListener {
                        if (it.isSuccessful){
                            val actionEmailUpdated = UpdateEmailFragmentDirections.actionEmailUpdated()
                            Navigation.findNavController(view).navigate(actionEmailUpdated)
                        } else {
                            Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

}