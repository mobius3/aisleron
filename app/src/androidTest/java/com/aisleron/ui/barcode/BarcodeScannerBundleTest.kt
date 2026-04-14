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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aisleron.ui.bundles.BarcodeScannerBundle
import com.aisleron.ui.bundles.Bundler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BarcodeScannerBundleTest {

    private val bundler = Bundler()

    @Test
    fun makeBarcodeScannerBundle_WithAllFields_BundleRoundTrips() {
        val bundle = bundler.makeBarcodeScannerBundle(
            productId = 42,
            scanMode = ScanMode.ADD_VARIANT,
            locationId = 7,
            filterType = "NEEDED"
        )

        val result = bundler.getBarcodeScannerBundle(bundle)

        assertEquals(42, result.productId)
        assertEquals(ScanMode.ADD_VARIANT, result.scanMode)
        assertEquals(7, result.locationId)
        assertEquals("NEEDED", result.filterType)
    }

    @Test
    fun makeBarcodeScannerBundle_Defaults_AreNull() {
        val bundle = bundler.makeBarcodeScannerBundle()

        val result = bundler.getBarcodeScannerBundle(bundle)

        assertNull(result.productId)
        assertEquals(ScanMode.ADD_VARIANT, result.scanMode)
        assertNull(result.locationId)
        assertNull(result.filterType)
    }

    @Test
    fun getBarcodeScannerBundle_NullBundle_ReturnsDefaults() {
        val result = bundler.getBarcodeScannerBundle(null)

        assertNotNull(result)
        assertNull(result.productId)
        assertEquals(ScanMode.ADD_VARIANT, result.scanMode)
    }

    @Test
    fun makeBarcodeScannerBundle_UpdateStatusMode_RoundTrips() {
        val bundle = bundler.makeBarcodeScannerBundle(
            scanMode = ScanMode.UPDATE_STATUS,
            locationId = 3,
            filterType = "IN_STOCK"
        )

        val result = bundler.getBarcodeScannerBundle(bundle)

        assertEquals(ScanMode.UPDATE_STATUS, result.scanMode)
        assertEquals(3, result.locationId)
        assertEquals("IN_STOCK", result.filterType)
    }
}
