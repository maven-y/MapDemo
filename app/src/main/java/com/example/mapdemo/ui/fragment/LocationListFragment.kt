package com.example.mapdemo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mapdemo.R
import com.example.mapdemo.data.Location
import com.example.mapdemo.databinding.FragmentLocationListBinding
import com.example.mapdemo.ui.adapter.LocationAdapter
import com.example.mapdemo.ui.dialog.LocationSearchDialog
import com.example.mapdemo.ui.viewmodel.LocationViewModel
import com.example.mapdemo.ui.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LocationListFragment : Fragment() {
    private var _binding: FragmentLocationListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var adapter: LocationAdapter
    private var isSorted = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeLocations()
    }

    private fun setupRecyclerView() {
        adapter = LocationAdapter(
            onDeleteClick = { location ->
                showDeleteConfirmationDialog(location)
            },
            onEditClick = { location ->
                showLocationSearchDialog(location)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@LocationListFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAddLocation.setOnClickListener {
            showLocationSearchDialog()
        }

        binding.btnSort.setOnClickListener { view ->
            showSortPopupMenu(view)
        }
    }

    private fun showSortPopupMenu(anchorView: View) {
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menuInflater.inflate(R.menu.sort_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_sort_ascending -> {
                    sortLocations(true)
                    true
                }
                R.id.action_sort_descending -> {
                    sortLocations(false)
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun sortLocations(ascending: Boolean) {
        val locations = viewModel.locations.value ?: return
        if (locations.isEmpty()) return

        val firstLocation = locations.first()
        isSorted = true
        viewModel.sortLocationsByDistance(
            refLat = firstLocation.latitude,
            refLng = firstLocation.longitude,
            ascending = ascending
        )
    }

    private fun observeLocations() {
        viewModel.locations.observe(viewLifecycleOwner) { locations ->
            if (!isSorted) {
                adapter.submitList(locations)
            }
        }

        viewModel.sortedLocations.observe(viewLifecycleOwner) { sortedLocations ->
            if (isSorted && sortedLocations.isNotEmpty()) {
                adapter.submitList(sortedLocations)
            } else if (sortedLocations.isEmpty()) {
                isSorted = false
            }
        }
    }

    private fun showDeleteConfirmationDialog(location: Location) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Location")
            .setMessage("Are you sure you want to delete ${location.name}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteLocation(location)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLocationSearchDialog(location: Location? = null) {
        LocationSearchDialog(location?.name ?: "").show(childFragmentManager, "location_search")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 