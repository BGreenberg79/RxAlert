package com.example.rxalert.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PrescriptionRepository private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val prescriptionsLiveData = MutableLiveData<List<Prescription>>()

    init {
        val stored = prefs.getString(KEY_PRESCRIPTIONS, null)
        val type = object : TypeToken<List<Prescription>>() {}.type
        val initial = if (stored.isNullOrEmpty()) {
            emptyList()
        } else {
            runCatching { gson.fromJson<List<Prescription>>(stored, type) }
                .getOrDefault(emptyList())
        }
        prescriptionsLiveData.value = normalize(initial)
    }

    fun observePrescriptions(): LiveData<List<Prescription>> = prescriptionsLiveData

    fun addPrescription(prescription: Prescription) {
        updatePrescriptions { current ->
            current + prescription
        }
    }

    fun recordDose(prescriptionId: String) {
        updatePrescriptions { current ->
            current.map { prescription ->
                if (prescription.id == prescriptionId) {
                    applyDose(prescription)
                } else {
                    maybeResetDaily(prescription)
                }
            }
        }
    }

    fun resetDailyCounts() {
        updatePrescriptions { current ->
            current.map(::maybeResetDaily)
        }
    }

    fun markRefill(prescriptionId: String) {
        updatePrescriptions { current ->
            current.map { prescription ->
                if (prescription.id == prescriptionId) {
                    maybeResetDaily(
                        prescription.copy(
                            pillsRemaining = prescription.quantityInBottle
                        )
                    )
                } else {
                    maybeResetDaily(prescription)
                }
            }
        }
    }

    private fun applyDose(prescription: Prescription): Prescription {
        val reset = maybeResetDaily(prescription)
        if (reset.pillsRemaining <= 0) return reset
        if (reset.timesPerDay <= 0) return reset
        if (reset.takenToday >= reset.timesPerDay) return reset
        return reset.copy(
            takenToday = reset.takenToday + 1,
            pillsRemaining = reset.pillsRemaining - 1
        )
    }

    private fun maybeResetDaily(prescription: Prescription): Prescription {
        val currentDay = Prescription.currentEpochDay()
        return if (currentDay > prescription.lastTakenResetEpochDay) {
            prescription.copy(
                takenToday = 0,
                lastTakenResetEpochDay = currentDay
            )
        } else {
            prescription
        }
    }

    private fun normalize(list: List<Prescription>): List<Prescription> =
        list.map(::maybeResetDaily)

    private fun updatePrescriptions(transform: (List<Prescription>) -> List<Prescription>) {
        val updated = normalize(transform(prescriptionsLiveData.value.orEmpty()))
        prescriptionsLiveData.value = updated
        prefs.edit()
            .putString(KEY_PRESCRIPTIONS, gson.toJson(updated))
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "prescription_prefs"
        private const val KEY_PRESCRIPTIONS = "prescriptions"

        @Volatile
        private var instance: PrescriptionRepository? = null

        fun getInstance(context: Context): PrescriptionRepository {
            return instance ?: synchronized(this) {
                instance ?: PrescriptionRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}