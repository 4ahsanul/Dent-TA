package com.fimo.aidentist.ui.navigation.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fimo.aidentist.MainActivity
import com.fimo.aidentist.R
import com.fimo.aidentist.data.repository.ImageRepository
import com.fimo.aidentist.data.viewmodel.ImageViewModel
import com.fimo.aidentist.data.viewmodel.ImageViewModelFactory
import com.fimo.aidentist.databinding.ActivityCameraBinding
import com.fimo.aidentist.databinding.LayoutCameraBinding
import com.fimo.aidentist.helper.ImageUploadStatus
import com.fimo.aidentist.helper.PreferenceHelper
import com.fimo.aidentist.ml.Classifier
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var imageFile: File? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraBinding: LayoutCameraBinding

    private val mInputSize = 150
    private val mModelPath = "model.tflite"
    private val mLabelPath = "labels.txt"
    private lateinit var classifier: Classifier

    private lateinit var fAuth: FirebaseAuth
    private lateinit var sharedPref: PreferenceHelper

    private var imageUploadStatus: ImageUploadStatus = ImageUploadStatus.InProgress

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initClassifier()
        fAuth = FirebaseAuth.getInstance()
        sharedPref = PreferenceHelper(this)

        binding.check.visibility = View.GONE
        binding.retake.visibility = View.GONE
        binding.layout.visibility = View.GONE

        cameraBinding = binding.cameraView

        binding.retake.setOnClickListener {
            binding.cameraView.root.visibility = View.VISIBLE
            binding.image.visibility = View.GONE
            binding.borderView.visibility = View.VISIBLE
            binding.check.visibility = View.GONE
            binding.retake.visibility = View.GONE
            binding.layout.visibility = View.GONE
        }


        val imageRepository = ImageRepository() // Create an instance of your ImageRepository
        val imageViewModelFactory =
            ImageViewModelFactory(imageRepository) // Create an instance of your ViewModelFactory
        val imageViewModel: ImageViewModel by viewModels { imageViewModelFactory } // Use the ViewModelFactory when creating the ImageViewModel

        binding.check.setOnClickListener {
            val bitmap = ((binding.image).drawable as BitmapDrawable).bitmap
            val classifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)
            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                imageViewModel.uploadImage(bitmap, classifier, user)
                // Start Activity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                // No user is currently signed in
                Log.w(TAG, "No user is currently signed in.")
            }
        }

        imageViewModel.imageUploadStatus.observe(this) { status ->
            imageUploadStatus = status
            when (status) {
                ImageUploadStatus.InProgress -> {
                    // Show a progress indicator
                    binding.progressBar.visibility = View.VISIBLE
                    Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show()
                }
                ImageUploadStatus.Success -> {
                    // Hide the progress indicator and show a success message
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                }
                is ImageUploadStatus.Failure -> {
                    // Hide the progress indicator and show an error message
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Error uploading image: ${status.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        //Request Camera Permissions
        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        cameraBinding = binding.cameraView
        cameraBinding.cameraCaptureButton.setOnClickListener {
            takePhoto()
            binding.check.visibility = View.VISIBLE
            binding.retake.visibility = View.VISIBLE
            binding.layout.visibility = View.VISIBLE
        }

        cameraBinding.switchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector.equals(CameraSelector.DEFAULT_BACK_CAMERA)) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA

            startCamera()
        }

        cameraBinding.closeCamera.setOnClickListener { finish() }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun initClassifier() {
        classifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)
    }

    private fun takePhoto() {
        //Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        //Create time stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        //Create ouput options object which contains file + metaData
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        //Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    onImageCaptured(savedUri)
                    val msg = "Photo capture succeeded: $savedUri"
                    //Toast.makeText(this@CameraActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull().let {
            File(
                it, resources.getString(R.string.app_name)
            ).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            //Used to bind lifecycle of camera to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraBinding.viewFinder.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder()
                .build()

            //Select back camera as a default
            //val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val viewPort = ViewPort.Builder(Rational(350, 170), Surface.ROTATION_0).build()
            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                //.addUseCase(imageAnalyzer)
                .addUseCase(imageCapture!!)
                .setViewPort(viewPort)
                .build()

            try {
                //Unbind use cases before rebinding
                cameraProvider.unbindAll()

                //Bind use case to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, useCaseGroup
                )
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
    }

    companion object {
        private const val TAG = "AddTaskDialog"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private fun onImageCaptured(uri: Uri) {
        val file = File(uri.path!!)
        imageFile = file

        Glide.with(binding.image).load(file).into(binding.image)
        showImage()
    }

    private fun showImage() {
        binding.borderView.visibility = View.GONE
        binding.cameraView.root.visibility = View.GONE
        binding.image.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}