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

import androidx.lifecycle.ViewModel
import com.aisleron.domain.barcode.BarcodeValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel shared by camera and manual entry scanning UIs.
 * Tracks the current scan state and prevents duplicate scans.
 */
class BarcodeScanViewModel : ViewModel() {

    private val _scanState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanState: StateFlow<ScanUiState> = _scanState.asStateFlow()

    /**
     * Called when a barcode is detected (from camera or manual entry).
     * Validates the barcode and updates state.
     * Ignores subsequent calls if already in Scanned state.
     */
    fun onBarcodeScanned(barcode: String) {
        if (_scanState.value is ScanUiState.Scanned) return

        if (barcode.isBlank() || !BarcodeValidator.isValidBarcode(barcode)) {
            _scanState.value = ScanUiState.Error("Invalid barcode format")
            return
        }

        _scanState.value = ScanUiState.Scanned(barcode)
    }

    /**
     * Reset to idle state, allowing a new scan.
     */
    fun reset() {
        _scanState.value = ScanUiState.Idle
    }

    /**
     * Get the scanned barcode value, or null if not yet scanned.
     */
    fun getBarcodeScanned(): String? {
        val state = _scanState.value
        return if (state is ScanUiState.Scanned) state.barcode else null
    }

    sealed class ScanUiState {
        data object Idle : ScanUiState()
        data class Scanned(val barcode: String) : ScanUiState()
        data class Error(val message: String) : ScanUiState()
    }
}
