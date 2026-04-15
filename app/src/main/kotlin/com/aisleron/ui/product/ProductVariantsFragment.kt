/*
 * Copyright (C) 2025-2026 aisleron.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aisleron.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aisleron.R
import com.aisleron.databinding.FragmentProductVariantsBinding
import com.aisleron.ui.barcode.CameraBarcodeScannerFragment
import com.aisleron.ui.barcode.ManualBarcodeEntryDialogFragment
import kotlinx.coroutines.launch

class ProductVariantsFragment : Fragment() {

    private val viewModel: ProductVariantsViewModel by lazy {
        ViewModelProvider(requireParentFragment())[ProductViewModel::class.java]
    }

    private var _binding: FragmentProductVariantsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductVariantsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductVariantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductVariantsAdapter { variant ->
            viewModel.removeVariant(variant.id)
        }

        binding.rvVariants.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVariants.adapter = adapter

        binding.btnScanBarcode.setOnClickListener {
            findNavController().navigate(R.id.action_nav_add_product_to_nav_barcode_scanner)
        }

        binding.btnEnterBarcode.setOnClickListener {
            val dialog = ManualBarcodeEntryDialogFragment.newInstance()
            dialog.show(childFragmentManager, TAG_MANUAL_ENTRY)
        }

        childFragmentManager.setFragmentResultListener(
            ManualBarcodeEntryDialogFragment.REQUEST_KEY, viewLifecycleOwner
        ) { _, bundle ->
            val barcode = bundle.getString(ManualBarcodeEntryDialogFragment.KEY_BARCODE)
            if (barcode != null) {
                viewModel.addVariant(barcode)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.variants.collect { variants ->
                    adapter.submitList(variants)
                    binding.tvEmptyState.visibility =
                        if (variants.isEmpty()) View.VISIBLE else View.GONE
                    binding.rvVariants.visibility =
                        if (variants.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }

        // Observe scanner result from CameraBarcodeScannerFragment
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(CameraBarcodeScannerFragment.KEY_SCAN_RESULT)
            ?.observe(viewLifecycleOwner) { barcode ->
                if (barcode != null) {
                    viewModel.addVariant(barcode)
                    findNavController().currentBackStackEntry?.savedStateHandle
                        ?.remove<String>(CameraBarcodeScannerFragment.KEY_SCAN_RESULT)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG_MANUAL_ENTRY = "manualBarcodeEntry"
    }
}
