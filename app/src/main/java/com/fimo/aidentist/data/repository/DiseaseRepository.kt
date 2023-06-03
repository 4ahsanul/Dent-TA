package com.fimo.aidentist.data.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class DiseaseRepository {
    private val diseaseLiveData = MutableLiveData<String>()
    private val db = FirebaseFirestore.getInstance()

    fun getDisease(uid: String): LiveData<String> {
        val docRef = db.collection("users").document(uid)
        docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(ContentValues.TAG, "Listen failed.", error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val disease = snapshot.getString("disease")
                diseaseLiveData.postValue(disease!!)
            }
        }
        return diseaseLiveData
    }
}