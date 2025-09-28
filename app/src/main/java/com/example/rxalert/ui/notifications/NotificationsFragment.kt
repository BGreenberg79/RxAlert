package com.example.rxalert.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rxalert.data.PrescriptionRepository
import com.example.rxalert.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    private val binding get() = _binding!!
    private lateinit var viewModel: NotificationsViewModel
    private lateinit var adapter: RefillAlertAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repository = PrescriptionRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(
            this,
            NotificationsViewModelFactory(repository)
        )[NotificationsViewModel::class.java]

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeViewModel()
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = RefillAlertAdapter()
        binding.refillAlerts.layoutManager = LinearLayoutManager(requireContext())
        binding.refillAlerts.adapter = adapter
    }
    private fun observeViewModel() {
        viewModel.refillAlerts.observe(viewLifecycleOwner) { alerts ->
            adapter.submitList(alerts)
            binding.emptyState.isVisible = alerts.isEmpty()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}