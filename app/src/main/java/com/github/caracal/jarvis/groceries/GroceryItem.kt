package com.github.caracal.jarvis.groceries

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class GroceryItem(
    @param:StringRes val nameRes: Int,
    @param:DrawableRes val iconRes: Int,
    @param:DrawableRes val iconBgRes: Int
)
