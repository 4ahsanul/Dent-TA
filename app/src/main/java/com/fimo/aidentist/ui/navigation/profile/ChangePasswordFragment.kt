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
            val password = binding.etPassword.text.toString().trim()

            if (password.isEmpty()) {
                binding.etPassword.error = "Password harus diisi"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            user?.let {
                val userCredential = EmailAuthProvider.getCredential(it.email!!, password)
                it.reauthenticate(userCredential).addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        binding.layoutPassword.visibility = View.GONE
                        binding.layoutNewPassword.visibility = View.VISIBLE

                        binding.btnUpdate.setOnClickListener { view ->
                            val newPassword = binding.etNewPassword.text.toString().trim()
                            val newPasswordConfirm =
                                binding.etNewPasswordConfirm.text.toString().trim()

                            if (newPassword.isEmpty() || newPassword.length < 6) {
                                binding.etNewPassword.error = "Password harus lebih dari 6 karakter"
                                binding.etNewPassword.requestFocus()
                                return@setOnClickListener
                            }

                            if (newPassword != newPasswordConfirm) {
                                binding.etNewPasswordConfirm.error = "Password tidak sama"
                                binding.etNewPasswordConfirm.requestFocus()
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
                        binding.etPassword.error = "Password Salah"
                        binding.etPassword.requestFocus()
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