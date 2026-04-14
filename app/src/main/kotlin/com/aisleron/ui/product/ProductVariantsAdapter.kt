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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aisleron.databinding.ItemProductVariantBinding

class ProductVariantsAdapter(
    private val onDeleteClick: (VariantUiModel) -> Unit
) : ListAdapter<VariantUiModel, ProductVariantsAdapter.VariantViewHolder>(VariantDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantViewHolder {
        val binding = ItemProductVariantBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VariantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VariantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VariantViewHolder(
        private val binding: ItemProductVariantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(variant: VariantUiModel) {
            binding.tvBarcode.text = variant.barcode
            binding.btnDeleteVariant.setOnClickListener {
                onDeleteClick(variant)
            }
        }
    }

    private object VariantDiffCallback : DiffUtil.ItemCallback<VariantUiModel>() {
        override fun areItemsTheSame(oldItem: VariantUiModel, newItem: VariantUiModel): Boolean =
            oldItem.barcode == newItem.barcode

        override fun areContentsTheSame(oldItem: VariantUiModel, newItem: VariantUiModel): Boolean =
            oldItem == newItem
    }
}
