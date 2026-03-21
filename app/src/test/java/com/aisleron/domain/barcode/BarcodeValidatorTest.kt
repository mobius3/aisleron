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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BarcodeValidatorTest {

    // ==================== isValidBarcode Tests ====================

    @Test
    fun isValidBarcode_EmptyString_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidBarcode(""))
    }

    @Test
    fun isValidBarcode_BlankString_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidBarcode("   "))
    }

    @Test
    fun isValidBarcode_ValidEan13_ReturnsTrue() {
        // Valid EAN-13 barcode for a product
        assertTrue(BarcodeValidator.isValidBarcode("5901234123457"))
    }

    @Test
    fun isValidBarcode_ValidEan8_ReturnsTrue() {
        // Valid EAN-8 barcode
        assertTrue(BarcodeValidator.isValidBarcode("96385074"))
    }

    @Test
    fun isValidBarcode_ValidUpcA_ReturnsTrue() {
        // Valid UPC-A barcode (common US product)
        assertTrue(BarcodeValidator.isValidBarcode("036000291452"))
    }

    @Test
    fun isValidBarcode_ValidUpcE_ReturnsTrue() {
        // Valid UPC-E barcode (compressed)
        assertTrue(BarcodeValidator.isValidBarcode("012345"))
    }

    @Test
    fun isValidBarcode_ValidCode128_ReturnsTrue() {
        // Code 128 can be alphanumeric
        assertTrue(BarcodeValidator.isValidBarcode("ABC123"))
    }

    @Test
    fun isValidBarcode_ValidCode39_ReturnsTrue() {
        // Code 39 supports letters, numbers, and some special chars
        assertTrue(BarcodeValidator.isValidBarcode("CODE39"))
    }

    @Test
    fun isValidBarcode_InvalidBarcode_ReturnsFalse() {
        // String with non-printable ASCII (tab character) - not valid for any format
        assertFalse(BarcodeValidator.isValidBarcode("invalid\t123"))
    }

    // ==================== validateBarcodeFormat Tests ====================

    @Test
    fun validateBarcodeFormat_EmptyString_ReturnsInvalid() {
        val result = BarcodeValidator.validateBarcodeFormat("")
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Barcode cannot be empty", (result as ValidationResult.Invalid).reason)
    }

    @Test
    fun validateBarcodeFormat_ValidEan13_ReturnsEan13Format() {
        val result = BarcodeValidator.validateBarcodeFormat("5901234123457")
        assertTrue(result is ValidationResult.Valid)
        assertEquals(BarcodeFormat.EAN_13, (result as ValidationResult.Valid).format)
    }

    @Test
    fun validateBarcodeFormat_ValidEan8_ReturnsEan8Format() {
        val result = BarcodeValidator.validateBarcodeFormat("96385074")
        assertTrue(result is ValidationResult.Valid)
        assertEquals(BarcodeFormat.EAN_8, (result as ValidationResult.Valid).format)
    }

    @Test
    fun validateBarcodeFormat_ValidUpcA_ReturnsUpcAFormat() {
        val result = BarcodeValidator.validateBarcodeFormat("036000291452")
        assertTrue(result is ValidationResult.Valid)
        assertEquals(BarcodeFormat.UPC_A, (result as ValidationResult.Valid).format)
    }

    @Test
    fun validateBarcodeFormat_ValidUpcE_ReturnsUpcEFormat() {
        val result = BarcodeValidator.validateBarcodeFormat("012345")
        assertTrue(result is ValidationResult.Valid)
        assertEquals(BarcodeFormat.UPC_E, (result as ValidationResult.Valid).format)
    }

    @Test
    fun validateBarcodeFormat_UnknownFormat_ReturnsInvalid() {
        // String with non-printable ASCII - not valid for any format
        val result = BarcodeValidator.validateBarcodeFormat("test\tvalue")
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Unrecognized barcode format", (result as ValidationResult.Invalid).reason)
    }

    // ==================== EAN-13 Tests ====================

    @Test
    fun isValidEan13_ValidBarcode_ReturnsTrue() {
        // EAN-13 with valid check digit
        assertTrue(BarcodeValidator.isValidEan13("5901234123457"))
    }

    @Test
    fun isValidEan13_ValidBarcodeAnother_ReturnsTrue() {
        // Another valid EAN-13 (Coca-Cola can in Europe)
        assertTrue(BarcodeValidator.isValidEan13("5449000000996"))
    }

    @Test
    fun isValidEan13_WrongLength_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidEan13("123456789012")) // 12 digits
    }

    @Test
    fun isValidEan13_InvalidCheckDigit_ReturnsFalse() {
        // Same barcode but last digit changed
        assertFalse(BarcodeValidator.isValidEan13("5901234123458"))
    }

    @Test
    fun isValidEan13_ContainsLetters_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidEan13("590123412345A"))
    }

    @Test
    fun isValidEan13_Empty_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidEan13(""))
    }

    // ==================== EAN-8 Tests ====================

    @Test
    fun isValidEan8_ValidBarcode_ReturnsTrue() {
        // EAN-8 with valid check digit
        assertTrue(BarcodeValidator.isValidEan8("96385074"))
    }

    @Test
    fun isValidEan8_WrongLength_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidEan8("123456789")) // 9 digits
    }

    @Test
    fun isValidEan8_InvalidCheckDigit_ReturnsFalse() {
        // Same barcode but last digit changed
        assertFalse(BarcodeValidator.isValidEan8("96385075"))
    }

    @Test
    fun isValidEan8_ContainsLetters_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidEan8("9638507A"))
    }

    // ==================== UPC-A Tests ====================

    @Test
    fun isValidUpcA_ValidBarcode_ReturnsTrue() {
        // UPC-A with valid check digit
        assertTrue(BarcodeValidator.isValidUpcA("036000291452"))
    }

    @Test
    fun isValidUpcA_ValidBarcodeAnother_ReturnsTrue() {
        // Another valid UPC-A
        assertTrue(BarcodeValidator.isValidUpcA("012345678905"))
    }

    @Test
    fun isValidUpcA_WrongLength_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidUpcA("12345678901")) // 11 digits
    }

    @Test
    fun isValidUpcA_InvalidCheckDigit_ReturnsFalse() {
        // Same barcode but last digit changed
        assertFalse(BarcodeValidator.isValidUpcA("036000291453"))
    }

    @Test
    fun isValidUpcA_ContainsLetters_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidUpcA("03600029145A"))
    }

    // ==================== UPC-E Tests ====================

    @Test
    fun isValidUpcE_ValidBarcodeStartingWithZero_ReturnsTrue() {
        assertTrue(BarcodeValidator.isValidUpcE("012345"))
    }

    @Test
    fun isValidUpcE_ValidBarcodeStartingWithOne_ReturnsTrue() {
        assertTrue(BarcodeValidator.isValidUpcE("123456"))
    }

    @Test
    fun isValidUpcE_InvalidStartDigit_ReturnsFalse() {
        // UPC-E must start with 0 or 1
        assertFalse(BarcodeValidator.isValidUpcE("212345"))
    }

    @Test
    fun isValidUpcE_WrongLength_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidUpcE("01234")) // 5 digits
        assertFalse(BarcodeValidator.isValidUpcE("0123456")) // 7 digits
    }

    @Test
    fun isValidUpcE_ContainsLetters_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidUpcE("01234A"))
    }

    // ==================== Code 128 Tests ====================

    @Test
    fun isValidCode128_NumericOnly_ReturnsTrue() {
        assertTrue(BarcodeValidator.isValidCode128("1234567890"))
    }

    @Test
    fun isValidCode128_Alphanumeric_ReturnsTrue() {
        assertTrue(BarcodeValidator.isValidCode128("ABC123XYZ"))
    }

    @Test
    fun isValidCode128_SpecialCharacters_ReturnsTrue() {
        // Code 128 supports printable ASCII
        assertTrue(BarcodeValidator.isValidCode128("ABC-123_456!"))
    }

    @Test
    fun isValidCode128_Empty_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidCode128(""))
    }

    @Test
    fun isValidCode128_TooLong_ReturnsFalse() {
        // Max 48 characters
        val longBarcode = "A".repeat(49)
        assertFalse(BarcodeValidator.isValidCode128(longBarcode))
    }

    @Test
    fun isValidCode128_MaxLength_ReturnsTrue() {
        // Exactly 48 characters should be valid
        val maxBarcode = "A".repeat(48)
        assertTrue(BarcodeValidator.isValidCode128(maxBarcode))
    }

    @Test
    fun isValidCode128_ContainsNonPrintableAscii_ReturnsFalse() {
        // Non-printable ASCII (tab character)
        assertFalse(BarcodeValidator.isValidCode128("ABC\t123"))
    }

    // ==================== Code 39 Tests ====================

    @Test
    fun isValidCode39_NumericOnly_ReturnsTrue() {
        assertTrue(BarcodeValidator.isValidCode39("1234567890"))
    }

    @Test
    fun isValidCode39_UppercaseLetters_ReturnsTrue() {
        assertTrue(BarcodeValidator.isValidCode39("ABCDEF"))
    }

    @Test
    fun isValidCode39_LowercaseLetters_ReturnsTrue() {
        // Should be treated as uppercase
        assertTrue(BarcodeValidator.isValidCode39("abcdef"))
    }

    @Test
    fun isValidCode39_MixedCase_ReturnsTrue() {
        assertTrue(BarcodeValidator.isValidCode39("AbCdEf"))
    }

    @Test
    fun isValidCode39_WithValidSpecialChars_ReturnsTrue() {
        // Valid Code 39 special chars: -. $/+% SPACE
        assertTrue(BarcodeValidator.isValidCode39("ABC-123"))
        assertTrue(BarcodeValidator.isValidCode39("ABC 123"))
        assertTrue(BarcodeValidator.isValidCode39("ABC$123"))
        assertTrue(BarcodeValidator.isValidCode39("ABC/123"))
        assertTrue(BarcodeValidator.isValidCode39("ABC+123"))
        assertTrue(BarcodeValidator.isValidCode39("ABC%123"))
        assertTrue(BarcodeValidator.isValidCode39("ABC.123"))
    }

    @Test
    fun isValidCode39_Empty_ReturnsFalse() {
        assertFalse(BarcodeValidator.isValidCode39(""))
    }

    @Test
    fun isValidCode39_TooLong_ReturnsFalse() {
        // Max 48 characters
        val longBarcode = "A".repeat(49)
        assertFalse(BarcodeValidator.isValidCode39(longBarcode))
    }

    @Test
    fun isValidCode39_InvalidSpecialChar_ReturnsFalse() {
        // @ is not a valid Code 39 character
        assertFalse(BarcodeValidator.isValidCode39("ABC@123"))
    }

    @Test
    fun isValidCode39_ContainsUnderscore_ReturnsFalse() {
        // Underscore is not a valid Code 39 character
        assertFalse(BarcodeValidator.isValidCode39("ABC_123"))
    }

    // ==================== Check Digit Calculation Tests ====================

    @Test
    fun checkDigit_CalculatesCorrectly_Ean13() {
        // EAN-13: 5901234123457
        // Check digit calculation:
        // Sum = 5*1 + 9*3 + 0*1 + 1*3 + 2*1 + 3*3 + 4*1 + 1*3 + 2*1 + 3*3 + 4*1 + 5*3
        //     = 5 + 27 + 0 + 3 + 2 + 9 + 4 + 3 + 2 + 9 + 4 + 15 = 83
        // Check digit = (10 - (83 % 10)) % 10 = (10 - 3) % 10 = 7
        assertTrue(BarcodeValidator.isValidEan13("5901234123457"))
        assertFalse(BarcodeValidator.isValidEan13("5901234123456")) // Wrong check digit
    }

    @Test
    fun checkDigit_CalculatesCorrectly_UpcA() {
        // UPC-A: 036000291452
        // Check digit calculation:
        // Sum = 0*3 + 3*1 + 6*3 + 0*1 + 0*3 + 0*1 + 2*3 + 9*1 + 1*3 + 4*1 + 5*3
        //     = 0 + 3 + 18 + 0 + 0 + 0 + 6 + 9 + 3 + 4 + 15 = 58
        // Check digit = (10 - (58 % 10)) % 10 = (10 - 8) % 10 = 2
        assertTrue(BarcodeValidator.isValidUpcA("036000291452"))
        assertFalse(BarcodeValidator.isValidUpcA("036000291453")) // Wrong check digit
    }

    // ==================== Edge Cases ====================

    @Test
    fun isValidBarcode_RealWorldEan13Barcodes_ReturnTrue() {
        // Real EAN-13 barcodes from various products
        assertTrue(BarcodeValidator.isValidBarcode("4006381333931")) // German product
        assertTrue(BarcodeValidator.isValidBarcode("5000159484695")) // UK product
        assertTrue(BarcodeValidator.isValidBarcode("8801051111111")) // Korean product
    }

    @Test
    fun isValidBarcode_RealWorldUpcABarcodes_ReturnTrue() {
        // Real UPC-A barcodes
        assertTrue(BarcodeValidator.isValidBarcode("042100005264")) // US product
        assertTrue(BarcodeValidator.isValidBarcode("011110864213")) // Another US product
    }
}
