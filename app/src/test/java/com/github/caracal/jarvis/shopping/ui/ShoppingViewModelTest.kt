package com.github.caracal.jarvis.shopping.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ShoppingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `addShoppingItem adds a valid item to the shopping list`() {
        val viewModel = ShoppingViewModel(FakeShoppingRepository())

        val added = viewModel.addShoppingItem("Milk", "cat_1")

        assertTrue(added)
        assertEquals(1, viewModel.shoppingList.value?.size)
        assertEquals("Milk", viewModel.shoppingList.value?.first()?.name)
    }

    @Test
    fun `addShoppingItem rejects a known brand name`() {
        val viewModel = ShoppingViewModel(FakeShoppingRepository())

        val added = viewModel.addShoppingItem("Coca-Cola", "cat_1")

        assertFalse(added)
        assertNotNull(viewModel.addItemError.value)
        assertTrue(viewModel.shoppingList.value.isNullOrEmpty())
    }

    @Test
    fun `findByBarcode returns the matching item from the repository`() {
        val repository = FakeShoppingRepository()
        val viewModel = ShoppingViewModel(repository)
        viewModel.addShoppingItemWithBarcode("Milk", "cat_1", "1234567890")

        val found = viewModel.findByBarcode("1234567890")

        assertEquals("Milk", found?.name)
    }

    @Test
    fun `removeShoppingItem refreshes the shopping list`() {
        val viewModel = ShoppingViewModel(FakeShoppingRepository())
        viewModel.addShoppingItem("Milk", "cat_1")
        val itemId = viewModel.shoppingList.value?.first()?.id.orEmpty()

        viewModel.removeShoppingItem(itemId)

        assertTrue(viewModel.shoppingList.value.isNullOrEmpty())
    }
}
