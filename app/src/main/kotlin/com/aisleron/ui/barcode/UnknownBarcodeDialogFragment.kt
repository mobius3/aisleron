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

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aisleron.R
import com.aisleron.domain.product.Product
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class UnknownBarcodeDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: UnknownBarcodeViewModel by viewModel()

    private var barcode: String? = null
    private var onCreateNewProduct: ((barcode: String) -> Unit)? = null
    private var onBarcodeAssigned: (() -> Unit)? = null

    private lateinit var adapter: ProductSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcode = arguments?.getString(ARG_BARCODE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_unknown_barcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtBarcode = view.findViewById<TextView>(R.id.txt_barcode_value)
        val editSearch = view.findViewById<TextInputEditText>(R.id.edit_search_product)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_product_results)
        val btnAssign = view.findViewById<View>(R.id.btn_assign_barcode)
        val btnCreateNew = view.findViewById<View>(R.id.btn_create_new)

        txtBarcode.text = getString(R.string.barcode_unknown_barcode_value, barcode)

        adapter = ProductSearchAdapter { product ->
            viewModel.selectProduct(product)
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        editSearch.addTextChangedListener { text ->
            viewModel.searchProducts(text?.toString() ?: "")
        }

        btnAssign.setOnClickListener {
            val selectedProduct = viewModel.selectedProduct.value ?: return@setOnClickListener
            val bc = barcode ?: return@setOnClickListener
            viewModel.assignBarcodeToProduct(bc, selectedProduct.id)
        }

        btnCreateNew.setOnClickListener {
            val bc = barcode ?: return@setOnClickListener
            onCreateNewProduct?.invoke(bc)
            dismiss()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { products ->
                adapter.submitList(products)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedProduct.collect { product ->
                btnAssign.isEnabled = product != null
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.assignResult.collect { result ->
                if (result is UnknownBarcodeViewModel.AssignResult.Success) {
                    onBarcodeAssigned?.invoke()
                    dismiss()
                }
            }
        }

        // Load initial product list
        viewModel.searchProducts("")
    }

    fun setCallbacks(
        onCreateNewProduct: (barcode: String) -> Unit,
        onBarcodeAssigned: () -> Unit
    ) {
        this.onCreateNewProduct = onCreateNewProduct
        this.onBarcodeAssigned = onBarcodeAssigned
    }

    private class ProductSearchAdapter(
        private val onProductClick: (Product) -> Unit
    ) : RecyclerView.Adapter<ProductSearchAdapter.ViewHolder>() {

        private val products = mutableListOf<Product>()
        private var selectedId: Int? = null

        fun submitList(newProducts: List<Product>) {
            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = products.size
                override fun getNewListSize() = newProducts.size
                override fun areItemsTheSame(oldPos: Int, newPos: Int) =
                    products[oldPos].id == newProducts[newPos].id
                override fun areContentsTheSame(oldPos: Int, newPos: Int) =
                    products[oldPos] == newProducts[newPos]
            })
            products.clear()
            products.addAll(newProducts)
            diffResult.dispatchUpdatesTo(this)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = products[position]
            holder.text.text = product.name
            holder.itemView.isSelected = product.id == selectedId
            holder.itemView.setOnClickListener {
                selectedId = product.id
                onProductClick(product)
                notifyDataSetChanged()
            }
        }

        override fun getItemCount() = products.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val text: TextView = view.findViewById(android.R.id.text1)
        }
    }

    companion object {
        const val ARG_BARCODE = "arg_barcode"
        const val TAG = "UnknownBarcodeDialog"

        fun newInstance(barcode: String): UnknownBarcodeDialogFragment {
            return UnknownBarcodeDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_BARCODE, barcode)
                }
            }
        }
    }
}
