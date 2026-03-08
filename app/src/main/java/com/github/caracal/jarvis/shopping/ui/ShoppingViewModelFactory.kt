package com.github.caracal.jarvis.shopping.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.caracal.jarvis.shopping.data.SharedPrefsShoppingRepository

/**
 * Factory for creating [ShoppingViewModel] instances with the correct [SharedPrefsShoppingRepository].
 *
 * @param context The context used to access SharedPreferences.
 */
class ShoppingViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingViewModel::class.java)) {
            return ShoppingViewModel(SharedPrefsShoppingRepository(context.applicationContext)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
