package com.example.rxalert.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rxalert.data.Prescription
import com.example.rxalert.databinding.ItemRefillAlertBinding

class RefillAlertAdapter :
    ListAdapter<Prescription, RefillAlertAdapter.RefillViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefillViewHolder {
        val binding = ItemRefillAlertBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RefillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RefillViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RefillViewHolder(
        private val binding: ItemRefillAlertBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(prescription: Prescription) {
            binding.refillName.text = prescription.prescriptionName
            val daysRemaining = prescription.daysOfSupplyRemaining
            val supplyLabel = if (daysRemaining == 1) "1 day" else "$daysRemaining days"
            binding.refillMessage.text =
                "${prescription.pillsRemaining} pills remaining • $supplyLabel of supply"
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Prescription>() {
        override fun areItemsTheSame(oldItem: Prescription, newItem: Prescription): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Prescription, newItem: Prescription): Boolean =
            oldItem == newItem
    }
}