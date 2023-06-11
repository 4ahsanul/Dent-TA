package com.fimo.aidentist.ui.navigation.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.fimo.aidentist.R
import com.fimo.aidentist.data.viewmodel.AuthViewModel
import com.fimo.aidentist.databinding.FragmentProfileBinding
import com.fimo.aidentist.ui.menu.auth.LoginActivity
import com.fimo.aidentist.ui.navigation.history.HistoryActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var fAuth: FirebaseAuth

    private var viewModel: AuthViewModel? = null

    //Photo Camera
    private lateinit var imageUri: Uri
    private var sImage: String? = ""

    companion object {
        const val REQUEST_CAMERA = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(AuthViewModel::class.java)
        viewModel!!.loggedStatus!!.observe(this) { aBoolean ->
            if (aBoolean == true) {
                val intent = Intent(activity, LoginActivity::class.java)
                activity?.startActivity(intent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.buttonHistory.setOnClickListener {
            val intent = Intent(activity, HistoryActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.buttonLogout.setOnClickListener {
            viewModel!!.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fAuth = FirebaseAuth.getInstance()

        val user = fAuth.currentUser

        if (user != null) {
            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(binding.avatar)
            } else {
                Picasso.get()
                    .load("https://raw.githubusercontent.com/4ahsanul/SignUpSignIn/main/images/ligin%20and%20reg%20purple/5907.jpg")
                    .into(binding.avatar)
            }

            binding.etName.setText(user.displayName)
            binding.etMail.setText(user.email)

            if (user.isEmailVerified) {
                binding.icVerified.visibility = View.VISIBLE
                binding.verifMessage.visibility = View.GONE
            } else {
                binding.icUnverified.visibility = View.VISIBLE
                binding.verifMessage.visibility = View.VISIBLE
            }

            if (user.phoneNumber.isNullOrEmpty()) {
                //Make string value for this text
                binding.etPhone.setText(getString(R.string.phone_hint))
            } else {
                binding.etPhone.setText(user.phoneNumber)
            }
        }

        binding.avatar.setOnClickListener {
            intentCamera()
        }

        binding.etName.setOnFocusChangeListener { view, hasFocus ->
            binding.btnSave.visibility = if (hasFocus) View.VISIBLE else View.GONE
        }

        binding.btnSave.setOnClickListener {
            val image = when {
                ::imageUri.isInitialized -> imageUri
                user?.photoUrl == null -> Uri.parse("https://raw.githubusercontent.com/4ahsanul/Workstation/main/ic_avatar_profile_hd-removebg-preview.png?token=GHSAT0AAAAAACAVDT35HNVEL5LZNVO6427QZBM3AKA")
                else -> user.photoUrl
            }

            val name = binding.etName.text.toString().trim()

            if (name.isEmpty()) {
                binding.etName.error = getString(R.string.name_warn)
                binding.etName.requestFocus()
                return@setOnClickListener
            }

            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(image)
                .build().also {
                    user?.updateProfile(it)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                activity,
                                getString(R.string.save_profile),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                activity,
                                "${it.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }

        binding.icUnverified.setOnClickListener {
            user?.sendEmailVerification()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        activity,
                        getString(R.string.send_verification),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        activity,
                        "${it.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnInsertImage.setOnClickListener {
            insertImg()
        }

        binding.etMail.setOnClickListener {
            val actionUpdateEmail = ProfileFragmentDirections.actionUpdateEmail()
            Navigation.findNavController(it).navigate(actionUpdateEmail)
        }

        binding.changePass.setOnClickListener {
            val actionChangePassword = ProfileFragmentDirections.actionChangePassword()
            Navigation.findNavController(it).navigate(actionChangePassword)
        }
    }

    private fun insertImg() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        filePickerLauncher.launch(intent)
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val uri: Uri? = data.data
                    try {
                        val inputStream = requireContext().contentResolver.openInputStream(uri!!)
                        val imgBitmap = BitmapFactory.decodeStream(inputStream)
                        val stream = ByteArrayOutputStream()
                        imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val bytes = stream.toByteArray()
                        sImage = Base64.encodeToString(bytes, Base64.DEFAULT)
                        uploadImage(imgBitmap)
                        inputStream!!.close()
                        Toast.makeText(requireActivity(), "Image Selected", Toast.LENGTH_SHORT)
                            .show()
                    } catch (e: Exception) {
                        Toast.makeText(requireActivity(), e.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            val imgBitmap = data?.extras?.get("data") as Bitmap
            uploadImage(imgBitmap)
        }
    }

    private fun intentCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            activity?.packageManager?.let {
                intent.resolveActivity(it).also {
                    //Deprecated but still works, so why not
                    startActivityForResult(intent, REQUEST_CAMERA)
                }
            }
        }
    }

    private fun uploadImage(imgBitmap: Bitmap) {
        val byteAOS = ByteArrayOutputStream()
        val reference = FirebaseStorage.getInstance().reference.child(
            "imagesUpProfile/${FirebaseAuth.getInstance().currentUser?.uid}"
        )

        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteAOS)
        val image = byteAOS.toByteArray()
        binding.progressBar.visibility = View.VISIBLE

        reference.putBytes(image).addOnCompleteListener {
            if (it.isSuccessful) {
                reference.downloadUrl.addOnCompleteListener {
                    it.result?.let {
                        imageUri = it
                        binding.avatar.setImageBitmap(imgBitmap)
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}