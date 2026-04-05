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

import com.aisleron.di.TestDependencyManager
import com.aisleron.domain.productvariant.ProductVariant
import com.aisleron.domain.productvariant.ProductVariantRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IsBarcodeUniqueUseCaseTest {
    private lateinit var dm: TestDependencyManager
    private lateinit var isBarcodeUniqueUseCase: IsBarcodeUniqueUseCase
    private lateinit var productVariantRepository: ProductVariantRepository

    @BeforeEach
    fun setUp() {
        dm = TestDependencyManager()
        isBarcodeUniqueUseCase = dm.getUseCase()
        productVariantRepository = dm.getRepository()
    }

    @Test
    fun isBarcodeUnique_BarcodeNotInUse_ReturnsTrue() = runTest {
        val result = isBarcodeUniqueUseCase("5901234123457").first()

        assertTrue(result)
    }

    @Test
    fun isBarcodeUnique_BarcodeInUse_ReturnsFalse() = runTest {
        val barcode = "5901234123457"
        val variant = ProductVariant(
            id = 0,
            productId = 1,
            barcode = barcode,
            createdAt = System.currentTimeMillis()
        )
        productVariantRepository.add(variant)

        val result = isBarcodeUniqueUseCase(barcode).first()

        assertFalse(result)
    }

    @Test
    fun isBarcodeUnique_BarcodeInUseWithExclusion_ReturnsTrue() = runTest {
        val barcode = "5901234123457"
        val variant = ProductVariant(
            id = 0,
            productId = 1,
            barcode = barcode,
            createdAt = System.currentTimeMillis()
        )
        val variantId = productVariantRepository.add(variant)

        // Exclude the same variant - should return true
        val result = isBarcodeUniqueUseCase(barcode, excludeVariantId = variantId).first()

        assertTrue(result)
    }

    @Test
    fun isBarcodeUnique_BarcodeInUseWithDifferentExclusion_ReturnsFalse() = runTest {
        val barcode = "5901234123457"
        val variant = ProductVariant(
            id = 0,
            productId = 1,
            barcode = barcode,
            createdAt = System.currentTimeMillis()
        )
        productVariantRepository.add(variant)

        // Exclude a different variant ID - should still return false
        val result = isBarcodeUniqueUseCase(barcode, excludeVariantId = 999).first()

        assertFalse(result)
    }

    @Test
    fun isBarcodeUnique_EmptyBarcode_ReturnsTrue() = runTest {
        val result = isBarcodeUniqueUseCase("").first()

        assertTrue(result)
    }
}
