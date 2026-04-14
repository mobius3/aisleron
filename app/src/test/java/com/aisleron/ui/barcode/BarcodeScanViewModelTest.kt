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

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BarcodeScanViewModelTest {

    private lateinit var viewModel: BarcodeScanViewModel
    private val testScope = TestScope()

    @BeforeEach
    fun setUp() {
        viewModel = BarcodeScanViewModel()
    }

    @Test
    fun initialState_IsIdle() {
        assertEquals(BarcodeScanViewModel.ScanUiState.Idle, viewModel.scanState.value)
    }

    @Test
    fun onBarcodeScanned_ValidBarcode_UpdatesStateToScanned() = testScope.runTest {
        val barcode = "5901234123457" // valid EAN-13
        viewModel.onBarcodeScanned(barcode)

        val state = viewModel.scanState.value
        assert(state is BarcodeScanViewModel.ScanUiState.Scanned)
        assertEquals(barcode, (state as BarcodeScanViewModel.ScanUiState.Scanned).barcode)
    }

    @Test
    fun onBarcodeScanned_InvalidBarcode_UpdatesStateToError() = testScope.runTest {
        viewModel.onBarcodeScanned("")

        val state = viewModel.scanState.value
        assert(state is BarcodeScanViewModel.ScanUiState.Error)
    }

    @Test
    fun onBarcodeScanned_SecondScan_IgnoredWhileScanned() = testScope.runTest {
        val first = "5901234123457"
        val second = "12345670"
        viewModel.onBarcodeScanned(first)
        viewModel.onBarcodeScanned(second)

        val state = viewModel.scanState.value
        assertEquals(first, (state as BarcodeScanViewModel.ScanUiState.Scanned).barcode)
    }

    @Test
    fun reset_ReturnsToIdle() = testScope.runTest {
        viewModel.onBarcodeScanned("5901234123457")
        viewModel.reset()

        assertEquals(BarcodeScanViewModel.ScanUiState.Idle, viewModel.scanState.value)
    }

    @Test
    fun getBarcodeScanned_WhenScanned_ReturnsBarcode() = testScope.runTest {
        val barcode = "5901234123457"
        viewModel.onBarcodeScanned(barcode)

        assertEquals(barcode, viewModel.getBarcodeScanned())
    }

    @Test
    fun getBarcodeScanned_WhenNotScanned_ReturnsNull() {
        assertNull(viewModel.getBarcodeScanned())
    }
}
