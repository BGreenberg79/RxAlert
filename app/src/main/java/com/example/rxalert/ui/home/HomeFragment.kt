package com.example.rxalert.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rxalert.data.PrescriptionRepository
import com.example.rxalert.databinding.FragmentHomeBinding
import com.example.rxalert.network.RxNormRepository

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null


    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: SearchResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prescriptionRepository = PrescriptionRepository.getInstance(requireContext())
        val rxRepository = RxNormRepository.getInstance()
        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(prescriptionRepository, rxRepository)
        )[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
         setupRecyclerView()
        setupListeners()
        observeViewModel()
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = SearchResultAdapter { drug ->
            viewModel.selectDrug(drug)
        }
        binding.searchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.searchResults.adapter = adapter
    }

    private fun setupListeners() {
        binding.searchButton.setOnClickListener {
            viewModel.search(binding.medicationSearchInput.text?.toString().orEmpty())
        }
        binding.addPrescriptionButton.setOnClickListener {
            handleAddPrescription()
        }
        binding.clearSelectionButton.setOnClickListener {
            viewModel.clearSelection()
        }
    }

       private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            adapter.submitList(results)
        }
viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.searchProgress.isVisible = isLoading
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            binding.searchMessage.text = message
            binding.searchMessage.isVisible = !message.isNullOrBlank()
        }
        viewModel.selectedDrug.observe(viewLifecycleOwner) { drug ->
            binding.addPrescriptionCard.isVisible = drug != null
            binding.clearSelectionButton.isVisible = drug != null
            if (drug != null) {
                binding.selectedDrugName.text = drug.name
                binding.selectedDrugType.text = drug.displayType
                val synonym = drug.relatedName
                binding.selectedDrugSynonym.isVisible = synonym != null
                binding.selectedDrugSynonym.text = synonym?.let { "Also listed as $it" }
                if (binding.prescriptionNameInput.text.isNullOrBlank()) {
                    binding.prescriptionNameInput.setText(drug.name)
                }
            } else {
                binding.prescriptionNameInput.text?.clear()
                binding.dosageInput.text?.clear()
                binding.timeOfDayInput.text?.clear()
                binding.timesPerDayInput.text?.clear()
                binding.quantityInput.text?.clear()
            }
        }
        viewModel.prescriptionSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                Toast.makeText(requireContext(), "Prescription saved", Toast.LENGTH_SHORT).show()
                viewModel.acknowledgeSaved()
            }
        }
    }

    private fun handleAddPrescription() {
        val selected = viewModel.selectedDrug.value ?: return

        val name = binding.prescriptionNameInput.text?.toString()?.trim().orEmpty()
        val dosage = binding.dosageInput.text?.toString()?.trim().orEmpty()
        val timeOfDay = binding.timeOfDayInput.text?.toString()?.trim().orEmpty()
        val timesPerDay = binding.timesPerDayInput.text?.toString()?.toIntOrNull()
        val quantity = binding.quantityInput.text?.toString()?.toIntOrNull()

        binding.prescriptionNameLayout.error = if (name.isEmpty()) "Required" else null
        binding.dosageLayout.error = if (dosage.isEmpty()) "Required" else null
        binding.timeOfDayLayout.error = if (timeOfDay.isEmpty()) "Required" else null
        binding.timesPerDayLayout.error = if (timesPerDay == null || timesPerDay <= 0) "Enter a number > 0" else null
        binding.quantityLayout.error = if (quantity == null || quantity <= 0) "Enter a number > 0" else null

        if (name.isEmpty() || dosage.isEmpty() || timeOfDay.isEmpty() ||
            timesPerDay == null || timesPerDay <= 0 ||
            quantity == null || quantity <= 0
        ) {
            return
        }

        viewModel.addPrescription(
            prescriptionName = name,
            selectedDrug = selected,
            dosage = dosage,
            timeOfDay = timeOfDay,
            timesPerDay = timesPerDay,
            quantityInBottle = quantity
        )
        binding.prescriptionNameLayout.error = null
        binding.dosageLayout.error = null
        binding.timeOfDayLayout.error = null
        binding.timesPerDayLayout.error = null
        binding.quantityLayout.error = null
        binding.prescriptionNameInput.text?.clear()
        binding.dosageInput.text?.clear()
        binding.timeOfDayInput.text?.clear()
        binding.timesPerDayInput.text?.clear()
        binding.quantityInput.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}