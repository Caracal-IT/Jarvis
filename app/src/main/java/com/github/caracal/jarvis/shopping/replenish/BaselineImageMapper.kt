package com.github.caracal.jarvis.shopping.replenish

import com.github.caracal.jarvis.R

/**
 * Maps baseline item names to their corresponding drawable resource IDs.
 *
 * Returns a generic placeholder drawable for any item name not found in the map.
 */
object BaselineImageMapper {

    private val nameToDrawable: Map<String, Int> = mapOf(
        "Low-Carb Bread" to R.drawable.shopping_replenish_ic_item_low_carb_bread,
        "Charcoal" to R.drawable.shopping_replenish_ic_item_charcoal,
        "Fire Lighters" to R.drawable.shopping_replenish_ic_item_fire_lighters,
        "Wood" to R.drawable.shopping_replenish_ic_item_wood,
        "Coffee" to R.drawable.shopping_replenish_ic_item_coffee,
        "Tea" to R.drawable.shopping_replenish_ic_item_tea,
        "Cloths" to R.drawable.shopping_replenish_ic_item_cloths,
        "Dishwashing Liquid" to R.drawable.shopping_replenish_ic_item_dishwashing_liquid,
        "Fabric Softener" to R.drawable.shopping_replenish_ic_item_fabric_softener,
        "Sponges" to R.drawable.shopping_replenish_ic_item_sponges,
        "Washing Machine Soap" to R.drawable.shopping_replenish_ic_item_washing_machine_soap,
        "Mustard" to R.drawable.shopping_replenish_ic_item_mustard,
        "Butter" to R.drawable.shopping_replenish_ic_item_butter,
        "Cheese" to R.drawable.shopping_replenish_ic_item_cheese,
        "Milk" to R.drawable.shopping_replenish_ic_item_milk,
        "Yogurt" to R.drawable.shopping_replenish_ic_item_yogurt,
        "Frozen Berries" to R.drawable.shopping_replenish_ic_item_frozen_berries,
        "Frozen Vegetables" to R.drawable.shopping_replenish_ic_item_frozen_vegetables,
        "Beef" to R.drawable.shopping_replenish_ic_item_beef,
        "Chicken" to R.drawable.shopping_replenish_ic_item_chicken,
        "Pork" to R.drawable.shopping_replenish_ic_item_pork,
        "Coconut Oil" to R.drawable.shopping_replenish_ic_item_coconut_oil,
        "Olive Oil" to R.drawable.shopping_replenish_ic_item_olive_oil,
        "Nuts" to R.drawable.shopping_replenish_ic_item_nuts,
        "Sweeteners" to R.drawable.shopping_replenish_ic_item_sweeteners,
        "Treats" to R.drawable.shopping_replenish_ic_item_treats,
        "Body Wash" to R.drawable.shopping_replenish_ic_item_body_wash,
        "Mouthwash" to R.drawable.shopping_replenish_ic_item_mouthwash,
        "Soap Bar" to R.drawable.shopping_replenish_ic_item_soap_bar,
        "Toothpaste" to R.drawable.shopping_replenish_ic_item_toothpaste,
        "Avocados" to R.drawable.shopping_replenish_ic_item_avocados,
        "Blueberries" to R.drawable.shopping_replenish_ic_item_blueberries,
        "Mushrooms" to R.drawable.shopping_replenish_ic_item_mushrooms,
        "Strawberries" to R.drawable.shopping_replenish_ic_item_strawberries,
        "Tomatoes" to R.drawable.shopping_replenish_ic_item_tomatoes,
        "Vegetables" to R.drawable.shopping_replenish_ic_item_vegetables,
        "Fish" to R.drawable.shopping_replenish_ic_item_fish,
        "Shrimp" to R.drawable.shopping_replenish_ic_item_shrimp,
        "Spices" to R.drawable.shopping_replenish_ic_item_spices,
        "Berocca Boost" to R.drawable.shopping_replenish_ic_item_berocca_boost
    )

    /**
     * Returns the drawable resource ID for the given item name.
     *
     * @param itemName The display name of the item.
     * @return The matching drawable resource ID, or [R.drawable.shared_ic_supplies] if not found.
     */
    fun getDrawableResId(itemName: String): Int =
        nameToDrawable[itemName] ?: R.drawable.shared_ic_supplies
}
