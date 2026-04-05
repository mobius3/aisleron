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

package com.aisleron.domain.productvariant.usecase

import com.aisleron.domain.productvariant.ProductVariant
import com.aisleron.domain.productvariant.ProductVariantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Checks if a barcode is unique across all products.
 * Optionally excludes a specific variant (useful when updating an existing variant).
 */
class IsBarcodeUniqueUseCase(
    private val productVariantRepository: ProductVariantRepository
) {
    operator fun invoke(barcode: String, excludeVariantId: Int? = null): Flow<Boolean> =
        productVariantRepository.getByBarcode(barcode).map { variant ->
            // Barcode is unique if not found, or if the found variant has the same id (updating itself)
            variant?.let { excludeVariantId != null && it.id == excludeVariantId } ?: true
        }
}
