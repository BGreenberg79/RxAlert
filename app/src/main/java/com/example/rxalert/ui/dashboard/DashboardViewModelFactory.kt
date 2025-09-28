package com.example.rxalert.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rxalert.data.PrescriptionRepository

class DashboardViewModelFactory(
    private val prescriptionRepository: PrescriptionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(prescriptionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}