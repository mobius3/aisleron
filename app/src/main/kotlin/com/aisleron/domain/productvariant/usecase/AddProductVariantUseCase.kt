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

/**
 * Adds a new barcode variant to a product.
 * Validates the barcode format and checks for duplicates.
 */
interface AddProductVariantUseCase {
    suspend operator fun invoke(productId: Int, barcode: String): AddVariantResult
}

/**
 * Result of adding a product variant.
 */
sealed class AddVariantResult {
    data class Success(val variant: ProductVariant) : AddVariantResult()
    data class Error(val reason: AddVariantError) : AddVariantResult()
}

/**
 * Error reasons when adding a variant fails.
 */
enum class AddVariantError {
    INVALID_BARCODE_FORMAT,
    DUPLICATE_BARCODE,
    PRODUCT_NOT_FOUND
}
