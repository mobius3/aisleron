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

package com.aisleron.domain.product.usecase

import com.aisleron.di.TestDependencyManager
import com.aisleron.domain.product.ProductRepository
import com.aisleron.domain.productvariant.ProductVariant
import com.aisleron.domain.productvariant.ProductVariantRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetProductByBarcodeUseCaseTest {
    private lateinit var dm: TestDependencyManager
    private lateinit var getProductByBarcodeUseCase: GetProductByBarcodeUseCase
    private lateinit var productVariantRepository: ProductVariantRepository

    @BeforeEach
    fun setUp() {
        dm = TestDependencyManager()
        getProductByBarcodeUseCase = dm.getUseCase()
        productVariantRepository = dm.getRepository()
    }

    @Test
    fun getProductByBarcode_ExistingBarcode_ReturnsProduct() = runTest {
        val existingProduct = dm.getRepository<ProductRepository>().getAll().first()
        val barcode = "5901234123457"
        productVariantRepository.add(
            ProductVariant(
                id = 0,
                productId = existingProduct.id,
                barcode = barcode,
                createdAt = System.currentTimeMillis()
            )
        )

        val result = getProductByBarcodeUseCase(barcode)

        assertNotNull(result)
        assertEquals(existingProduct.id, result?.id)
        assertEquals(existingProduct.name, result?.name)
    }

    @Test
    fun getProductByBarcode_NonExistentBarcode_ReturnsNull() = runTest {
        val result = getProductByBarcodeUseCase("9999999999999")

        assertNull(result)
    }

    @Test
    fun getProductByBarcode_EmptyBarcode_ReturnsNull() = runTest {
        val result = getProductByBarcodeUseCase("")

        assertNull(result)
    }
}
