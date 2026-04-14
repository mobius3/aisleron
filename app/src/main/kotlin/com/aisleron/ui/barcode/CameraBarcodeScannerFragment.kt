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

package com.aisleron.ui.barcode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.aisleron.R
import com.aisleron.databinding.FragmentCameraBarcodeScannerBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CameraBarcodeScannerFragment : Fragment() {

    private val viewModel: BarcodeScanViewModel by viewModel()
    private var _binding: FragmentCameraBarcodeScannerBinding? = null
    private val binding get() = _binding!!
    private var barcodeScanner: BarcodeScanner? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startCamera() else showPermissionError()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBarcodeScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeScanner = MLKitBarcodeScanner()

        binding.btnManualEntry.setOnClickListener {
            barcodeScanner?.stopScanning()
            val dialog = ManualBarcodeEntryDialogFragment.newInstance()
            dialog.show(childFragmentManager, TAG_MANUAL_ENTRY)
        }

        childFragmentManager.setFragmentResultListener(
            ManualBarcodeEntryDialogFragment.REQUEST_KEY, viewLifecycleOwner
        ) { _, bundle ->
            val barcode = bundle.getString(ManualBarcodeEntryDialogFragment.KEY_BARCODE)
            if (barcode != null) {
                viewModel.onBarcodeScanned(barcode)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.scanState.collect { state ->
                    when (state) {
                        is BarcodeScanViewModel.ScanUiState.Scanned -> handleScanResult(state.barcode)
                        is BarcodeScanViewModel.ScanUiState.Error -> viewModel.reset()
                        else -> Unit
                    }
                }
            }
        }

        if (hasCameraPermission()) startCamera() else requestCameraPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        barcodeScanner?.stopScanning()
        barcodeScanner = null
        _binding = null
    }

    private fun startCamera() {
        barcodeScanner?.startScanning(
            previewView = binding.previewView,
            lifecycleOwner = viewLifecycleOwner
        ) { result ->
            when (result) {
                is BarcodeScanResult.Success -> viewModel.onBarcodeScanned(result.barcode)
                is BarcodeScanResult.Error -> viewModel.reset()
                is BarcodeScanResult.Cancelled -> Unit
            }
        }
    }

    private fun handleScanResult(barcode: String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            KEY_SCAN_RESULT, barcode
        )
        findNavController().popBackStack()
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun showPermissionError() {
        viewModel.onBarcodeScanned("")
    }

    companion object {
        const val TAG_MANUAL_ENTRY = "manualBarcodeEntry"
        const val KEY_SCAN_RESULT = "barcodeScanResult"
    }
}
