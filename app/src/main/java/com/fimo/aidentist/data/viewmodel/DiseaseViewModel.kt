package com.fimo.aidentist.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fimo.aidentist.data.repository.DiseaseRepository

class DiseaseViewModel : ViewModel() {
    private val repository = DiseaseRepository()

    fun getDisease(uid: String): LiveData<String> {
        return repository.getDisease(uid)
    }

}