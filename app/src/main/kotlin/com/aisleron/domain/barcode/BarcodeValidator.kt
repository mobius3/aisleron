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

package com.aisleron.domain.barcode

/**
 * Validates barcode formats commonly used in retail.
 *
 * Supported formats:
 * - EAN-13: 13-digit European Article Number (most common worldwide)
 * - EAN-8: 8-digit compact version for small packages
 * - UPC-A: 12-digit Universal Product Code (common in US/Canada)
 * - UPC-E: 6-digit compressed UPC for small packages
 * - Code 128: Variable length alphanumeric (industrial use)
 * - Code 39: Variable length alphanumeric (includes letters)
 */
object BarcodeValidator {

    /**
     * Validates if a barcode string is a valid format.
     * Returns true if the barcode matches any supported format.
     */
    fun isValidBarcode(barcode: String): Boolean {
        if (barcode.isBlank()) return false

        return isValidEan13(barcode) ||
                isValidEan8(barcode) ||
                isValidUpcA(barcode) ||
                isValidUpcE(barcode) ||
                isValidCode128(barcode) ||
                isValidCode39(barcode)
    }

    /**
     * Returns detailed validation result with the detected format.
     */
    fun validateBarcodeFormat(barcode: String): ValidationResult {
        if (barcode.isBlank()) {
            return ValidationResult.Invalid("Barcode cannot be empty")
        }

        return when {
            isValidEan13(barcode) -> ValidationResult.Valid(BarcodeFormat.EAN_13)
            isValidEan8(barcode) -> ValidationResult.Valid(BarcodeFormat.EAN_8)
            isValidUpcA(barcode) -> ValidationResult.Valid(BarcodeFormat.UPC_A)
            isValidUpcE(barcode) -> ValidationResult.Valid(BarcodeFormat.UPC_E)
            isValidCode128(barcode) -> ValidationResult.Valid(BarcodeFormat.CODE_128)
            isValidCode39(barcode) -> ValidationResult.Valid(BarcodeFormat.CODE_39)
            else -> ValidationResult.Invalid("Unrecognized barcode format")
        }
    }

    /**
     * EAN-13: 13 digits with check digit validation.
     * Common worldwide for retail products.
     */
    fun isValidEan13(barcode: String): Boolean {
        if (barcode.length != 13 || !barcode.all { it.isDigit() }) return false
        return validateCheckDigit(barcode)
    }

    /**
     * EAN-8: 8 digits with check digit validation.
     * Used for small packages where EAN-13 won't fit.
     */
    fun isValidEan8(barcode: String): Boolean {
        if (barcode.length != 8 || !barcode.all { it.isDigit() }) return false
        return validateCheckDigit(barcode)
    }

    /**
     * UPC-A: 12 digits with check digit validation.
     * Standard format in US and Canada.
     */
    fun isValidUpcA(barcode: String): Boolean {
        if (barcode.length != 12 || !barcode.all { it.isDigit() }) return false
        return validateCheckDigit(barcode)
    }

    /**
     * UPC-E: 6 digits (compressed UPC-A).
     * Does not include check digit validation as it's typically
     * expanded to UPC-A for validation.
     */
    fun isValidUpcE(barcode: String): Boolean {
        // UPC-E is 6 digits, all numeric
        // First digit must be 0 or 1 (system number)
        // Note: Full validation requires expanding to UPC-A
        return barcode.length == 6 &&
                barcode.all { it.isDigit() } &&
                (barcode.first() == '0' || barcode.first() == '1')
    }

    /**
     * Code 128: Variable length, supports all ASCII characters.
     * Common in logistics and inventory management.
     * We validate basic structure but don't validate check digit
     * as it requires knowing the Code Set (A, B, or C).
     */
    fun isValidCode128(barcode: String): Boolean {
        // Code 128 can contain any ASCII character
        // Minimum practical length is 1 (start + stop + check = 3 minimum encoded)
        // Maximum typically 48 characters for barcode readability
        return barcode.isNotEmpty() &&
                barcode.length <= 48 &&
                barcode.all { it.code in 32..126 } // Printable ASCII
    }

    /**
     * Code 39: Variable length, supports digits, letters, and special chars.
     * Common in automotive and defense industries.
     */
    fun isValidCode39(barcode: String): Boolean {
        // Code 39 valid characters: 0-9, A-Z, and -. $/+% SPACE
        val validChars = setOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', '-', '.', ' ', '$',
            '/', '+', '%'
        )

        return barcode.isNotEmpty() &&
                barcode.length <= 48 &&
                barcode.uppercase().all { it in validChars }
    }

    /**
     * Validates check digit for EAN/UPC formats using modulo 10 algorithm.
     * Weights alternate between 1 and 3, starting from the rightmost digit
     * (excluding the check digit itself).
     */
    private fun validateCheckDigit(barcode: String): Boolean {
        val digits = barcode.map { it.digitToInt() }
        val checkDigit = digits.last()

        val sum = digits.dropLast(1)
            .reversed()
            .mapIndexed { index, digit ->
                digit * if (index % 2 == 0) 3 else 1
            }
            .sum()

        val calculatedCheckDigit = (10 - (sum % 10)) % 10
        return calculatedCheckDigit == checkDigit
    }
}

/**
 * Supported barcode formats.
 */
enum class BarcodeFormat {
    EAN_13,
    EAN_8,
    UPC_A,
    UPC_E,
    CODE_128,
    CODE_39,
    UNKNOWN
}

/**
 * Result of barcode validation.
 */
sealed class ValidationResult {
    data class Valid(val format: BarcodeFormat) : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}
