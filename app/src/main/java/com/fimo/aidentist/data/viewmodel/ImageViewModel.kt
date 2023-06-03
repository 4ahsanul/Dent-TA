package com.fimo.aidentist.data.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fimo.aidentist.data.repository.ImageRepository
import com.fimo.aidentist.helper.ImageUploadStatus
import com.fimo.aidentist.ml.Classifier
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageViewModel(private val repository: ImageRepository) : ViewModel() {
    private val _imageUploadStatus = MutableLiveData<ImageUploadStatus>()
    val imageUploadStatus: LiveData<ImageUploadStatus>
        get() = _imageUploadStatus

    fun uploadImage(bitmap: Bitmap, classifier: Classifier, user: FirebaseUser) {
        _imageUploadStatus.value = ImageUploadStatus.InProgress

        val result = classifier.recognizeImage(bitmap)

        // Create reference to the image file in Firebase Storage
        // val timestamp = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date())
        val timestamp = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault()).format(Date())
        val fileName = "${result[0].title} - $timestamp.jpg"
        // val timestamp = System.currentTimeMillis()
        // val storageRef = FirebaseStorage.getInstance()
        // .getReference("users/${user.uid}/images/${result[0].title}_$timestamp.jpg")
        val storageRef = FirebaseStorage.getInstance()
            .getReference("users/${user.uid}/images/$fileName")

        // Compress the image bitmap and upload it to Firebase Storage
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image upload success
            Log.d(TAG, "Image upload successfully")

            // Get download URL of the uploaded image
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                // Save the disease information to Firestore
                val diseaseInformation = hashMapOf<String, Any>(
                    "disease" to result[0].title,
                    "confidence" to result[0].confidence,
                    "imageUri" to uri.toString(), // Save the download URL to Firestore
                    "createdAt" to FieldValue.serverTimestamp()
                )
                repository.saveDiseaseInformation(user.uid, diseaseInformation)
                    .addOnSuccessListener {
                        Log.d(
                            TAG,
                            "Successfully saved data for user ${user.uid}"
                        )
                        _imageUploadStatus.value = ImageUploadStatus.Success
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            TAG,
                            "Error adding document for user ${user.uid}",
                            e
                        )
                        _imageUploadStatus.value = ImageUploadStatus.Failure(e)
                    }
                repository.saveDiseaseInformation(user.uid, diseaseInformation)
                    .addOnSuccessListener {
                        Log.d(
                            TAG,
                            "Successfully saved data for user ${user.uid}"
                        )
                        _imageUploadStatus.value = ImageUploadStatus.Success
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            TAG,
                            "Error adding document for user ${user.uid}",
                            e
                        )
                        _imageUploadStatus.value = ImageUploadStatus.Failure(e)
                    }
            }
        }
    }

    companion object {
        private const val TAG = "AddTaskDialog"
    }
}