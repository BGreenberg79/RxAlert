package com.example.rxalert.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rxalert.data.Prescription
import com.example.rxalert.databinding.ItemPrescriptionBinding

class PrescriptionListAdapter(
    private val onRecordDose: (Prescription) -> Unit,
    private val onMarkRefill: (Prescription) -> Unit
) : ListAdapter<Prescription, PrescriptionListAdapter.PrescriptionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
        val binding = ItemPrescriptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PrescriptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PrescriptionViewHolder(
        private val binding: ItemPrescriptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(prescription: Prescription) {
            binding.prescriptionName.text = prescription.prescriptionName
            val details = buildString {
                prescription.brandName?.takeIf { it.isNotBlank() }?.let {
                    append("Brand: ").append(it)
                }
                prescription.genericName?.takeIf { it.isNotBlank() }?.let {
                    if (isNotEmpty()) append("  •  ")
                    append("Generic: ").append(it)
                }
            }
            binding.prescriptionDetails.isVisible = details.isNotBlank()
            binding.prescriptionDetails.text = details
            binding.dosageInfo.text = "Dosage: ${prescription.dosage}"
            binding.scheduleInfo.text = "Schedule: ${prescription.timesPerDay}x per day • ${prescription.timeOfDay}"
            binding.takenToday.text = "Taken today: ${prescription.takenToday} / ${prescription.timesPerDay}"
            binding.totalTaken.text = "Total taken from bottle: ${prescription.totalTakenFromBottle}"
            binding.pillsRemaining.text = "Pills remaining: ${prescription.pillsRemaining} of ${prescription.quantityInBottle}"

            val needsRefill = prescription.timesPerDay > 0 && prescription.daysOfSupplyRemaining <= 7
            binding.supplyWarning.isVisible = needsRefill
            if (needsRefill) {
                binding.supplyWarning.text = "Only ${prescription.daysOfSupplyRemaining} days of supply left"
            }

            val disableDose = prescription.pillsRemaining <= 0 ||
                prescription.timesPerDay <= 0 ||
                prescription.takenToday >= prescription.timesPerDay
            binding.recordDoseButton.isEnabled = !disableDose
            binding.recordDoseButton.setOnClickListener { onRecordDose(prescription) }
            binding.refillButton.setOnClickListener { onMarkRefill(prescription) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Prescription>() {
        override fun areItemsTheSame(oldItem: Prescription, newItem: Prescription): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Prescription, newItem: Prescription): Boolean =
            oldItem == newItem
    }
}