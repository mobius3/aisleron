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

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.aisleron.R
import com.aisleron.databinding.DialogManualBarcodeEntryBinding
import com.aisleron.domain.barcode.BarcodeValidator
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ManualBarcodeEntryDialogFragment : DialogFragment() {

    private var _binding: DialogManualBarcodeEntryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogManualBarcodeEntryBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.barcode_enter_barcode)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel) { _, _ -> dismiss() }
            .create()

        dialog.setOnShowListener {
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.isEnabled = false
            okButton.setOnClickListener {
                val barcode = binding.etBarcode.text.toString().trim()
                if (BarcodeValidator.isValidBarcode(barcode)) {
                    setFragmentResult(
                        REQUEST_KEY,
                        Bundle().apply { putString(KEY_BARCODE, barcode) }
                    )
                    dismiss()
                } else {
                    binding.tilBarcode.error = getString(R.string.barcode_invalid_format)
                }
            }
        }

        binding.etBarcode.doAfterTextChanged {
            val text = it?.toString()?.trim() ?: ""
            if (text.isNotEmpty() && !BarcodeValidator.isValidBarcode(text)) {
                binding.tilBarcode.error = getString(R.string.barcode_invalid_format)
            } else {
                binding.tilBarcode.error = null
            }
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled =
                text.isNotEmpty() && BarcodeValidator.isValidBarcode(text)
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "manualBarcodeEntryRequest"
        const val KEY_BARCODE = "manualBarcode"

        fun newInstance(): ManualBarcodeEntryDialogFragment {
            return ManualBarcodeEntryDialogFragment()
        }
    }
}
