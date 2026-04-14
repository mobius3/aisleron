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

/**
 * Determines how a scanned barcode should be processed.
 */
enum class ScanMode {
    /** Add a barcode variant to a specific product. */
    ADD_VARIANT,
    /** Toggle product status (needed/in stock) based on current list context. */
    UPDATE_STATUS,
    /** Quick scan from outside the app (tile, widget). */
    QUICK_SCAN
}

/**
 * Result of a barcode scan operation.
 */
sealed class BarcodeScanResult {
    /** A barcode was successfully detected. */
    data class Success(val barcode: String) : BarcodeScanResult()

    /** Scanning was cancelled by the user. */
    data object Cancelled : BarcodeScanResult()

    /** An error occurred during scanning. */
    data class Error(val message: String) : BarcodeScanResult()
}
