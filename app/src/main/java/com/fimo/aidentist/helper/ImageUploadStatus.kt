package com.fimo.aidentist.helper

sealed class ImageUploadStatus {
    object InProgress : ImageUploadStatus()
    object Success : ImageUploadStatus()
    data class Failure(val exception: Exception) : ImageUploadStatus()
}