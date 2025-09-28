package com.example.rxalert.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rxalert.data.Prescription
import com.example.rxalert.data.PrescriptionRepository
import com.example.rxalert.data.RxDrug
import com.example.rxalert.network.RxNormRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val prescriptionRepository: PrescriptionRepository,
    private val rxNormRepository: RxNormRepository
) : ViewModel() {

     private val _searchResults = MutableLiveData<List<RxDrug>>(emptyList())
    val searchResults: LiveData<List<RxDrug>> = _searchResults

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _selectedDrug = MutableLiveData<RxDrug?>()
    val selectedDrug: LiveData<RxDrug?> = _selectedDrug

    private val _prescriptionSaved = MutableLiveData(false)
    val prescriptionSaved: LiveData<Boolean> = _prescriptionSaved

    fun search(query: String) {
        if (query.isBlank()) {
            _errorMessage.value = "Enter a medication name to search."
            _searchResults.value = emptyList()
            return
        }
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val results = rxNormRepository.searchMedications(query)
                _searchResults.value = results
                if (results.isEmpty()) {
                    _errorMessage.value = "No medications found for \"$query\"."
                }
            } catch (ex: Exception) {
                _errorMessage.value = "Unable to reach RxNorm. Please check your connection."
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectDrug(drug: RxDrug) {
        _selectedDrug.value = drug
    }

    fun clearSelection() {
        _selectedDrug.value = null
    }

    fun addPrescription(
        prescriptionName: String,
        selectedDrug: RxDrug,
        dosage: String,
        timeOfDay: String,
        timesPerDay: Int,
        quantityInBottle: Int
    ) {
        val prescription = Prescription.fresh(
            prescriptionName = prescriptionName,
            genericName = if (selectedDrug.isBrand) {
                selectedDrug.relatedName ?: selectedDrug.name
            } else {
                selectedDrug.name
            },
            brandName = if (selectedDrug.isBrand) {
                selectedDrug.name
            } else {
                selectedDrug.relatedName
            },
            dosage = dosage,
            timeOfDay = timeOfDay,
            timesPerDay = timesPerDay,
            quantityInBottle = quantityInBottle
        )
        prescriptionRepository.addPrescription(prescription)
        _prescriptionSaved.value = true
        _selectedDrug.value = null
    }

    fun acknowledgeSaved() {
        _prescriptionSaved.value = false
    }
}