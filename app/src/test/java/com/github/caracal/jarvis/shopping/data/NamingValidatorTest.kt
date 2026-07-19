package com.github.caracal.jarvis.shopping.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NamingValidatorTest {

    @Test
    fun `validate rejects blank name`() {
        val result = NamingValidator.validate("   ")

        assertFalse(result.isValid)
    }

    @Test
    fun `validate rejects known brand name`() {
        val result = NamingValidator.validate("Coca-Cola")

        assertFalse(result.isValid)
    }

    @Test
    fun `validate accepts generic name`() {
        val result = NamingValidator.validate("Milk")

        assertTrue(result.isValid)
    }

    @Test
    fun `validate rejects brand name embedded in a longer string`() {
        val result = NamingValidator.validate("Pepsi Max 2L")

        assertFalse(result.isValid)
    }
}
