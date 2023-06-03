package com.fimo.aidentist.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class ImageRepository {
    fun saveDiseaseInformation(userId: String, diseaseInformation: HashMap<String, Any>): Task<Void> {
        return FirebaseFirestore.getInstance().collection("users").document(userId)
            .set(diseaseInformation)
    }
}