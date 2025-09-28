package com.example.rxalert.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rxalert.data.RxDrug
import com.example.rxalert.databinding.ItemSearchResultBinding

class SearchResultAdapter(
    private val onDrugSelected: (RxDrug) -> Unit
) : ListAdapter<RxDrug, SearchResultAdapter.ResultViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ResultViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(drug: RxDrug) {
            binding.drugName.text = drug.name
            binding.drugType.text = "${drug.displayType} • RxCUI ${drug.rxcui}"
            val synonym = drug.relatedName
            binding.drugSynonym.isVisible = synonym != null
            binding.drugSynonym.text = synonym?.let { "Also known as $it" }
            binding.root.setOnClickListener { onDrugSelected(drug) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<RxDrug>() {
        override fun areItemsTheSame(oldItem: RxDrug, newItem: RxDrug): Boolean =
            oldItem.rxcui == newItem.rxcui

        override fun areContentsTheSame(oldItem: RxDrug, newItem: RxDrug): Boolean =
            oldItem == newItem
    }
}