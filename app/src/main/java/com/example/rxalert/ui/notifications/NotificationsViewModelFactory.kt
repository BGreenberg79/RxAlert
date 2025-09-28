package com.example.rxalert.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rxalert.data.PrescriptionRepository

class NotificationsViewModelFactory(
    private val prescriptionRepository: PrescriptionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            return NotificationsViewModel(prescriptionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}