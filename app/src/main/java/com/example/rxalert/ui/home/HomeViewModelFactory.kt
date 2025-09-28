package com.example.rxalert.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rxalert.data.PrescriptionRepository
import com.example.rxalert.network.RxNormRepository

class HomeViewModelFactory(
    private val prescriptionRepository: PrescriptionRepository,
    private val rxNormRepository: RxNormRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(prescriptionRepository, rxNormRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}