package com.example.rxalert.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rxalert.data.PrescriptionRepository
import com.example.rxalert.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel
    private lateinit var adapter: PrescriptionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repository = PrescriptionRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(
            this,
            DashboardViewModelFactory(repository)
        )[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeViewModel()
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = PrescriptionListAdapter(
            onRecordDose = { prescription ->
                viewModel.recordDose(prescription.id)
            },
            onMarkRefill = { prescription ->
                viewModel.markRefill(prescription.id)
            }
        )
        binding.prescriptionList.layoutManager = LinearLayoutManager(requireContext())
        binding.prescriptionList.adapter = adapter
    }

        private fun observeViewModel() {
        viewModel.prescriptions.observe(viewLifecycleOwner) { prescriptions ->
            adapter.submitList(prescriptions)
            binding.emptyState.isVisible = prescriptions.isEmpty()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}