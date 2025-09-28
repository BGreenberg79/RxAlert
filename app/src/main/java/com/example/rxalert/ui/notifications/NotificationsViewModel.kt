package com.example.rxalert.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.rxalert.data.Prescription
import com.example.rxalert.data.PrescriptionRepository

class NotificationsViewModel(
    prescriptionRepository: PrescriptionRepository
) : ViewModel() {
        val refillAlerts: LiveData<List<Prescription>> = Transformations.map(
        prescriptionRepository.observePrescriptions()
    ) { prescriptions ->
        prescriptions.filter { prescription ->
            prescription.timesPerDay > 0 && prescription.daysOfSupplyRemaining <= 7
        }.sortedBy { it.daysOfSupplyRemaining }


    }
}