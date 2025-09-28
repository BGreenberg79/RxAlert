package com.example.rxalert.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rxalert.data.Prescription
import com.example.rxalert.data.PrescriptionRepository

class DashboardViewModel(
    private val prescriptionRepository: PrescriptionRepository
) : ViewModel() {

    val prescriptions: LiveData<List<Prescription>> = prescriptionRepository.observePrescriptions()

    init {
        prescriptionRepository.resetDailyCounts()
    }

    fun recordDose(prescriptionId: String) {
        prescriptionRepository.recordDose(prescriptionId)
    }

    fun markRefill(prescriptionId: String) {
        prescriptionRepository.markRefill(prescriptionId)
    }
}