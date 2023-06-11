package com.fimo.aidentist.ui.navigation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.fimo.aidentist.databinding.FragmentChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var fAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fAuth = FirebaseAuth.getInstance()

        val user = fAuth.currentUser

        binding.layoutPassword.visibility = View.VISIBLE
        binding.layoutNewPassword.visibility = View.GONE

        binding.buttonBack.setOnClickListener {
            val actionPasswordChanged = ChangePasswordFragmentDirections.actionPasswordChanged()
            Navigation.findNavController(view).navigate(actionPasswordChanged)
        }

        binding.btnAuth.setOnClickListener {
            val password = binding.passwordEditText.text.toString().trim()

            if (password.isEmpty()) {
                binding.passwordEditText.error = "Password harus diisi"
                binding.passwordEditText.requestFocus()
                return@setOnClickListener
            }

            user?.let {
                val userCredential = EmailAuthProvider.getCredential(it.email!!, password)
                it.reauthenticate(userCredential).addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        binding.layoutPassword.visibility = View.GONE
                        binding.layoutNewPassword.visibility = View.VISIBLE

                        binding.btnUpdate.setOnClickListener { view ->
                            val newPassword = binding.newPasswordEditText.text.toString().trim()
                            val newPasswordConfirm =
                                binding.confirmNewPasswordEditText.text.toString().trim()

                            if (newPassword.isEmpty() || newPassword.length < 6) {
                                binding.newPasswordEditText.error =
                                    "Password harus lebih dari 6 karakter"
                                binding.newPasswordEditText.requestFocus()
                                return@setOnClickListener
                            }

                            if (newPassword != newPasswordConfirm) {
                                binding.confirmNewPasswordEditText.error = "Password tidak sama"
                                binding.confirmNewPasswordEditText.requestFocus()
                                return@setOnClickListener
                            }

                            user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    val actionPasswordChanged =
                                        ChangePasswordFragmentDirections.actionPasswordChanged()
                                    Navigation.findNavController(view)
                                        .navigate(actionPasswordChanged)
                                    Toast.makeText(
                                        activity,
                                        "Password berhasil diubah",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "${updateTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } else if (reauthTask.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.passwordEditText.error = "Password Salah"
                        binding.passwordEditText.requestFocus()
                    } else {
                        Toast.makeText(
                            activity,
                            "${reauthTask.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}