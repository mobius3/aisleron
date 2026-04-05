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

class RemoveProductVariantUseCaseTest {
    private lateinit var dm: TestDependencyManager
    private lateinit var removeProductVariantUseCase: RemoveProductVariantUseCase
    private lateinit var productVariantRepository: ProductVariantRepository

    @BeforeEach
    fun setUp() {
        dm = TestDependencyManager()
        removeProductVariantUseCase = dm.getUseCase()
        productVariantRepository = dm.getRepository()
    }

    @Test
    fun removeVariant_ExistingVariant_VariantIsRemoved() = runTest {
        val variant = ProductVariant(
            id = 0,
            productId = 1,
            barcode = "5901234123457",
            createdAt = System.currentTimeMillis()
        )
        val variantId = productVariantRepository.add(variant)

        removeProductVariantUseCase(variantId)

        val removedVariant = productVariantRepository.get(variantId)
        assertNull(removedVariant)
    }

    @Test
    fun removeVariant_ExistingVariant_BarcodeNoLongerReturnsVariant() = runTest {
        val barcode = "5901234123457"
        val variant = ProductVariant(
            id = 0,
            productId = 1,
            barcode = barcode,
            createdAt = System.currentTimeMillis()
        )
        productVariantRepository.add(variant)

        val variantBeforeRemove = productVariantRepository.getByBarcode(barcode).first()
        assertEquals(barcode, variantBeforeRemove?.barcode)

        removeProductVariantUseCase(variantBeforeRemove!!.id)

        val variantAfterRemove = productVariantRepository.getByBarcode(barcode).first()
        assertNull(variantAfterRemove)
    }

    @Test
    fun removeVariant_NonExistentVariantId_NoErrorThrown() = runTest {
        // Should not throw
        removeProductVariantUseCase(999999)
    }

    @Test
    fun removeVariant_RemovesCorrectVariant_WhenMultipleExist() = runTest {
        val barcode1 = "5901234123457"
        val barcode2 = "5901234123458"

        val variant1 = ProductVariant(
            id = 0,
            productId = 1,
            barcode = barcode1,
            createdAt = System.currentTimeMillis()
        )
        val variant2 = ProductVariant(
            id = 0,
            productId = 1,
            barcode = barcode2,
            createdAt = System.currentTimeMillis()
        )

        val id1 = productVariantRepository.add(variant1)
        val id2 = productVariantRepository.add(variant2)

        removeProductVariantUseCase(id1)

        // First variant should be removed
        assertNull(productVariantRepository.get(id1))

        // Second variant should still exist
        val remainingVariant = productVariantRepository.get(id2)
        assertEquals(barcode2, remainingVariant?.barcode)
    }
}
