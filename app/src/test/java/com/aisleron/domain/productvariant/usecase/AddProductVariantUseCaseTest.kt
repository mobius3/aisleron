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
import com.aisleron.domain.product.ProductRepository
import com.aisleron.domain.productvariant.ProductVariantRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddProductVariantUseCaseTest {
    private lateinit var dm: TestDependencyManager
    private lateinit var addProductVariantUseCase: AddProductVariantUseCase
    private lateinit var productRepository: ProductRepository
    private lateinit var productVariantRepository: ProductVariantRepository

    @BeforeEach
    fun setUp() {
        dm = TestDependencyManager()
        addProductVariantUseCase = dm.getUseCase()
        productRepository = dm.getRepository()
        productVariantRepository = dm.getRepository()
    }

    @Test
    fun addVariant_ValidBarcodeAndProduct_ReturnsSuccess() = runTest {
        val product = productRepository.getAll().first()
        val barcode = "5901234123457" // Valid EAN-13

        val result = addProductVariantUseCase(product.id, barcode)

        assertTrue(result is AddVariantResult.Success)
        val success = result as AddVariantResult.Success
        assertEquals(product.id, success.variant.productId)
        assertEquals(barcode, success.variant.barcode)
    }

    @Test
    fun addVariant_ValidBarcodeAndProduct_VariantIsPersisted() = runTest {
        val product = productRepository.getAll().first()
        val barcode = "5901234123457"

        val result = addProductVariantUseCase(product.id, barcode)
        assertTrue(result is AddVariantResult.Success)

        val savedVariant = productVariantRepository.getByBarcode(barcode).first()
        assertTrue(savedVariant != null)
        assertEquals(product.id, savedVariant?.productId)
    }

    @Test
    fun addVariant_InvalidBarcode_ReturnsInvalidBarcodeFormatError() = runTest {
        val product = productRepository.getAll().first()
        val invalidBarcode = "not-a-barcode"

        val result = addProductVariantUseCase(product.id, invalidBarcode)

        assertTrue(result is AddVariantResult.Error)
        val error = result as AddVariantResult.Error
        assertEquals(AddVariantError.INVALID_BARCODE_FORMAT, error.reason)
    }

    @Test
    fun addVariant_BarcodeTooShort_ReturnsInvalidBarcodeFormatError() = runTest {
        val product = productRepository.getAll().first()
        val shortBarcode = "12345"

        val result = addProductVariantUseCase(product.id, shortBarcode)

        assertTrue(result is AddVariantResult.Error)
        val error = result as AddVariantResult.Error
        assertEquals(AddVariantError.INVALID_BARCODE_FORMAT, error.reason)
    }

    @Test
    fun addVariant_NonExistentProduct_ReturnsProductNotFoundError() = runTest {
        val nonExistentProductId = 999999
        val barcode = "5901234123457"

        val result = addProductVariantUseCase(nonExistentProductId, barcode)

        assertTrue(result is AddVariantResult.Error)
        val error = result as AddVariantResult.Error
        assertEquals(AddVariantError.PRODUCT_NOT_FOUND, error.reason)
    }

    @Test
    fun addVariant_DuplicateBarcode_ReturnsDuplicateBarcodeError() = runTest {
        val product = productRepository.getAll().first()
        val barcode = "5901234123457"

        // Add first variant
        val firstResult = addProductVariantUseCase(product.id, barcode)
        assertTrue(firstResult is AddVariantResult.Success)

        // Try to add duplicate
        val secondResult = addProductVariantUseCase(product.id, barcode)
        assertTrue(secondResult is AddVariantResult.Error)
        val error = secondResult as AddVariantResult.Error
        assertEquals(AddVariantError.DUPLICATE_BARCODE, error.reason)
    }

    @Test
    fun addVariant_DuplicateBarcodeForDifferentProduct_ReturnsDuplicateBarcodeError() = runTest {
        val products = productRepository.getAll().take(2).toList()
        val barcode = "5901234123457"

        // Add variant for first product
        val firstResult = addProductVariantUseCase(products[0].id, barcode)
        assertTrue(firstResult is AddVariantResult.Success)

        // Try to add same barcode for second product
        val secondResult = addProductVariantUseCase(products[1].id, barcode)
        assertTrue(secondResult is AddVariantResult.Error)
        val error = secondResult as AddVariantResult.Error
        assertEquals(AddVariantError.DUPLICATE_BARCODE, error.reason)
    }

    @Test
    fun addVariant_ValidEAN8_ReturnsSuccess() = runTest {
        val product = productRepository.getAll().first()
        val ean8Barcode = "12345670" // Valid EAN-8

        val result = addProductVariantUseCase(product.id, ean8Barcode)

        assertTrue(result is AddVariantResult.Success)
    }

    @Test
    fun addVariant_ValidUPC_ReturnsSuccess() = runTest {
        val product = productRepository.getAll().first()
        val upcBarcode = "012345678905" // Valid UPC-A

        val result = addProductVariantUseCase(product.id, upcBarcode)

        assertTrue(result is AddVariantResult.Success)
    }
}
