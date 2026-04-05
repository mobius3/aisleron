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

import com.aisleron.domain.barcode.BarcodeValidator
import com.aisleron.domain.product.ProductRepository
import com.aisleron.domain.productvariant.ProductVariant
import com.aisleron.domain.productvariant.ProductVariantRepository
import kotlinx.coroutines.flow.first

class AddProductVariantUseCaseImpl(
    private val productVariantRepository: ProductVariantRepository,
    private val productRepository: ProductRepository
) : AddProductVariantUseCase {

    override suspend operator fun invoke(productId: Int, barcode: String): AddVariantResult {
        // Validate barcode format
        if (!BarcodeValidator.isValidBarcode(barcode)) {
            return AddVariantResult.Error(AddVariantError.INVALID_BARCODE_FORMAT)
        }

        // Check if product exists
        val product = productRepository.get(productId)
        if (product == null) {
            return AddVariantResult.Error(AddVariantError.PRODUCT_NOT_FOUND)
        }

        // Check for duplicate barcode
        val existingVariant = productVariantRepository.getByBarcode(barcode).first()
        if (existingVariant != null) {
            return AddVariantResult.Error(AddVariantError.DUPLICATE_BARCODE)
        }

        // Create and add the variant
        val variant = ProductVariant(
            id = 0,
            productId = productId,
            barcode = barcode,
            createdAt = System.currentTimeMillis()
        )

        val variantId = productVariantRepository.add(variant)
        return AddVariantResult.Success(variant.copy(id = variantId))
    }
}
