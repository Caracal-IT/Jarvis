package com.github.caracal.jarvis.groceries

import com.github.caracal.jarvis.R

/**
 * Maps a product name or category string to an appropriate icon and background drawable.
 * Provides coherent Iron Man themed icons for all grocery items.
 */
object IconGenerator {

    data class IconSet(val iconRes: Int, val bgRes: Int)

    private val categoryRules: List<Pair<List<String>, IconSet>> = listOf(
        listOf("milk", "dairy", "cream", "yogurt", "kefir", "butter") to
                IconSet(R.drawable.ic_milk, R.drawable.bg_icon_blue),
        listOf("bread", "bakery", "biscuit", "baked", "cereal", "pasta", "flour", "grain", "rice", "noodle") to
                IconSet(R.drawable.ic_bread, R.drawable.bg_icon_orange),
        listOf("apple", "fruit", "berry", "banana", "mango", "grape", "orange", "lemon", "pear", "peach", "plum", "cherry", "strawberry") to
                IconSet(R.drawable.ic_apple, R.drawable.bg_icon_red),
        listOf("egg") to
                IconSet(R.drawable.ic_egg, R.drawable.bg_icon_yellow),
        listOf("cheese") to
                IconSet(R.drawable.ic_cheese, R.drawable.bg_icon_navy),
        listOf("water", "juice", "drink", "beverage", "soda", "cola", "coffee", "tea", "wine", "beer") to
                IconSet(R.drawable.ic_water, R.drawable.bg_icon_blue),
        listOf("peanut", "nut", "almond", "cashew", "pistachio", "walnut", "seed") to
                IconSet(R.drawable.ic_peanuts, R.drawable.bg_icon_dark_green),
        listOf("vegetable", "veggie", "tomato", "potato", "carrot", "onion", "garlic", "broccoli", "spinach", "lettuce", "cucumber", "pepper", "salad") to
                IconSet(R.drawable.ic_apple, R.drawable.bg_icon_dark_green),
        listOf("meat", "beef", "chicken", "pork", "lamb", "fish", "seafood", "salmon", "tuna", "shrimp") to
                IconSet(R.drawable.ic_supplies, R.drawable.bg_icon_red),
        listOf("sauce", "oil", "vinegar", "condiment", "ketchup", "mustard", "mayo") to
                IconSet(R.drawable.ic_milk, R.drawable.bg_icon_orange),
        listOf("sweet", "chocolate", "candy", "sugar", "honey", "jam", "snack", "chip", "cookie", "cake") to
                IconSet(R.drawable.ic_supplies, R.drawable.bg_icon_yellow),
        listOf("soap", "shampoo", "clean", "detergent", "hygiene", "toothpaste") to
                IconSet(R.drawable.ic_water, R.drawable.bg_icon_white)
    )

    private val default = IconSet(R.drawable.ic_supplies, R.drawable.bg_icon_navy)

    /**
     * Resolves the best matching icon set for the given product name and category.
     * Checks name first, then category — falls back to a generic icon.
     */
    fun resolve(name: String, category: String): IconSet {
        val combined = "${name.lowercase()} ${category.lowercase()}"
        return categoryRules.firstOrNull { (keywords, _) ->
            keywords.any { keyword -> combined.contains(keyword) }
        }?.second ?: default
    }
}

