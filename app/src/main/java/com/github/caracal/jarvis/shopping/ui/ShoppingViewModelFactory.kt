package com.github.caracal.jarvis.shopping.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caracal.jarvis.shopping.data.ShoppingRepository

/**
 * Factory for creating [ShoppingViewModel] instances with an injected [ShoppingRepository].
 *
 * @param repository The repository instance to inject into the created ViewModel.
 */
class ShoppingViewModelFactory(private val repository: ShoppingRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingViewModel::class.java)) {
            return ShoppingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
