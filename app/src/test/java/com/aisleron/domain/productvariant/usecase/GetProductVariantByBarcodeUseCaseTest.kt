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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetProductVariantByBarcodeUseCaseTest {
    private lateinit var dm: TestDependencyManager
    private lateinit var getVariantByBarcodeUseCase: GetProductVariantByBarcodeUseCase
    private lateinit var productVariantRepository: ProductVariantRepository

    @BeforeEach
    fun setUp() {
        dm = TestDependencyManager()
        getVariantByBarcodeUseCase = dm.getUseCase()
        productVariantRepository = dm.getRepository()
    }

    @Test
    fun getVariantByBarcode_ExistingBarcode_ReturnsVariant() = runTest {
        val barcode = "5901234123457"
        val variant = ProductVariant(
            id = 0,
            productId = 1,
            barcode = barcode,
            createdAt = System.currentTimeMillis()
        )
        productVariantRepository.add(variant)

        val result = getVariantByBarcodeUseCase(barcode).first()

        assertEquals(barcode, result?.barcode)
        assertEquals(1, result?.productId)
    }

    @Test
    fun getVariantByBarcode_NonExistentBarcode_ReturnsNull() = runTest {
        val result = getVariantByBarcodeUseCase("9999999999999").first()

        assertNull(result)
    }

    @Test
    fun getVariantByBarcode_EmptyBarcode_ReturnsNull() = runTest {
        val result = getVariantByBarcodeUseCase("").first()

        assertNull(result)
    }

    @Test
    fun getVariantByBarcode_ReturnsFlow_UpdatesOnDataChange() = runTest {
        val barcode = "5901234123457"

        // Initially no variant
        val initialResult = getVariantByBarcodeUseCase(barcode).first()
        assertNull(initialResult)

        // Add variant
        val variant = ProductVariant(
            id = 0,
            productId = 1,
            barcode = barcode,
            createdAt = System.currentTimeMillis()
        )
        productVariantRepository.add(variant)

        // Now should return the variant
        val updatedResult = getVariantByBarcodeUseCase(barcode).first()
        assertEquals(barcode, updatedResult?.barcode)
    }
}
