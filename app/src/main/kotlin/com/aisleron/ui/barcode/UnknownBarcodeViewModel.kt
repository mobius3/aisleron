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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aisleron.domain.product.Product
import com.aisleron.domain.product.usecase.GetAllProductsUseCase
import com.aisleron.domain.productvariant.usecase.AddProductVariantUseCase
import com.aisleron.domain.productvariant.usecase.AddVariantResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UnknownBarcodeViewModel(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val addProductVariantUseCase: AddProductVariantUseCase
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _isAssigning = MutableStateFlow(false)
    val isAssigning: StateFlow<Boolean> = _isAssigning.asStateFlow()

    private val _assignResult = MutableStateFlow<AssignResult?>(null)
    val assignResult: StateFlow<AssignResult?> = _assignResult.asStateFlow()

    fun searchProducts(query: String) {
        viewModelScope.launch {
            val allProducts = getAllProductsUseCase()
            val filtered = if (query.isBlank()) {
                allProducts
            } else {
                allProducts.filter { it.name.contains(query, ignoreCase = true) }
            }
            _searchResults.value = filtered
        }
    }

    fun selectProduct(product: Product) {
        _selectedProduct.value = product
    }

    fun clearSelection() {
        _selectedProduct.value = null
    }

    fun assignBarcodeToProduct(barcode: String, productId: Int) {
        viewModelScope.launch {
            _isAssigning.value = true
            try {
                when (val result = addProductVariantUseCase(productId, barcode)) {
                    is AddVariantResult.Success -> _assignResult.value = AssignResult.Success
                    is AddVariantResult.Error -> _assignResult.value =
                        AssignResult.Error(result.reason.name)
                }
            } catch (e: Exception) {
                _assignResult.value = AssignResult.Error(e.message ?: "Failed to assign barcode")
            } finally {
                _isAssigning.value = false
            }
        }
    }

    fun clearResult() {
        _assignResult.value = null
    }

    sealed class AssignResult {
        data object Success : AssignResult()
        data class Error(val message: String) : AssignResult()
    }
}
