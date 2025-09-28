package com.example.rxalert.data

import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Represents a prescription that the user is tracking.
 */
data class Prescription(
    val id: String = UUID.randomUUID().toString(),
    val prescriptionName: String,
    val genericName: String?,
    val brandName: String?,
    val dosage: String,
    val timeOfDay: String,
    val timesPerDay: Int,
    val quantityInBottle: Int,
    val pillsRemaining: Int,
    val takenToday: Int,
    val lastTakenResetEpochDay: Long
) {
    val totalTakenFromBottle: Int
        get() = quantityInBottle - pillsRemaining

    val daysOfSupplyRemaining: Int
        get() = if (timesPerDay <= 0) Int.MAX_VALUE else pillsRemaining / timesPerDay

    companion object {
        fun fresh(
            prescriptionName: String,
            genericName: String?,
            brandName: String?,
            dosage: String,
            timeOfDay: String,
            timesPerDay: Int,
            quantityInBottle: Int
        ): Prescription {
            val todayEpochDay = currentEpochDay()
            return Prescription(
                prescriptionName = prescriptionName,
                genericName = genericName,
                brandName = brandName,
                dosage = dosage,
                timeOfDay = timeOfDay,
                timesPerDay = timesPerDay,
                quantityInBottle = quantityInBottle,
                pillsRemaining = quantityInBottle,
                takenToday = 0,
                lastTakenResetEpochDay = todayEpochDay
            )
        }

        fun currentEpochDay(): Long = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
    }
}