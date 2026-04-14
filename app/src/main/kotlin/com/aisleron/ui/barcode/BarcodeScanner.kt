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

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner

/**
 * Interface for barcode scanning implementations.
 * Abstracts the scanning engine (ML Kit, ZXing, etc.) from the UI layer.
 */
interface BarcodeScanner {

    /**
     * Start scanning for barcodes using the provided camera preview.
     * Results are delivered via the [onResult] callback.
     *
     * @param previewView The camera preview surface.
     * @param lifecycleOwner The lifecycle owner controlling scan start/stop.
     * @param onResult Callback invoked when a barcode is detected.
     */
    fun startScanning(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        onResult: (BarcodeScanResult) -> Unit
    )

    /**
     * Stop scanning and release camera resources.
     */
    fun stopScanning()
}
