package com.github.caracal.jarvis.shopping.data

/**
 * Baseline configuration for all predefined shopping categories and items.
 *
 * Each category and baseline item has a stable, unique GUID identifier generated
 * in source code. Baseline items persist in the Replenish List even when removed
 * from the Shopping List.
 */
object BaselineData {

    /** All shopping categories, including those without baseline items. */
    val categories: List<ShoppingCategory> = listOf(
        ShoppingCategory(id = "a3f1c2d4-e5b6-4789-8abc-1d2e3f405061", name = "Bakery"),
        ShoppingCategory(id = "b2e4d6f8-a1c3-4e57-9b0d-2f4a6c8e0123", name = "Barbecue"),
        ShoppingCategory(id = "c1d3e5f7-b2a4-4c68-8d9e-3a5b7c9f1234", name = "Beverages"),
        ShoppingCategory(id = "d0e2f4a6-c3b5-4d79-9e0f-4b6c8d0a2345", name = "Canned Goods"),
        ShoppingCategory(id = "e9f1a3b5-d4c6-4e80-0f1a-5c7d9e1b3456", name = "Cleaning"),
        ShoppingCategory(id = "f8a0b2c4-e5d7-4f91-1a2b-6d8e0f2c4567", name = "Condiments"),
        ShoppingCategory(id = "07b1c3d5-f6e8-4002-2b3c-7e9f1a3d5678", name = "Dairy"),
        ShoppingCategory(id = "16c2d4e6-07f9-4113-3c4d-8f0a2b4e6789", name = "Frozen Foods"),
        ShoppingCategory(id = "25d3e5f7-18a0-4224-4d5e-9a1b3c5f789a", name = "Grains"),
        ShoppingCategory(id = "34e4f6a8-29b1-4335-5e6f-0b2c4d6a89ab", name = "Meat"),
        ShoppingCategory(id = "43f5a7b9-3ac2-4446-6f70-1c3d5e7b9abc", name = "Oils"),
        ShoppingCategory(id = "52a6b8c0-4bd3-4557-7a81-2d4e6f8cabed", name = "Pantry"),
        ShoppingCategory(id = "61b7c9d1-5ce4-4668-8b92-3e5f7a9dbcde", name = "Personal Care"),
        ShoppingCategory(id = "70c8d0e2-6df5-4779-9ca3-4f6a8b0ecdff", name = "Produce"),
        ShoppingCategory(id = "7fc9e1f3-7ea6-488a-0db4-5a7b9c1fde01", name = "Seafood"),
        ShoppingCategory(id = "8edad2a4-8fb7-499b-1ec5-6b8c0d2aef12", name = "Seasonings"),
        ShoppingCategory(id = "9febb3b5-90c8-40ac-2fd6-7c9d1e3bf023", name = "Snacks"),
        ShoppingCategory(id = "0afcc4c6-a1d9-41bd-3ae7-8d0e2f4c0134", name = "Supplements")
    )

    /** All predefined baseline items. */
    val baselineItems: List<ShoppingItem> = listOf(
        // Bakery
        ShoppingItem(
            id = "1b0dd5d7-b2ea-42ce-4bf8-9e1f3a5d1245",
            name = "Low-Carb Bread",
            categoryId = "a3f1c2d4-e5b6-4789-8abc-1d2e3f405061",
            isBaseline = true
        ),
        // Barbecue
        ShoppingItem(
            id = "2c1ee6e8-c3fb-43df-5ca9-0f2a4b6e2356",
            name = "Charcoal",
            categoryId = "b2e4d6f8-a1c3-4e57-9b0d-2f4a6c8e0123",
            isBaseline = true
        ),
        ShoppingItem(
            id = "3d2ff7f9-d40c-44e0-6dba-1a3b5c7f3467",
            name = "Fire Lighters",
            categoryId = "b2e4d6f8-a1c3-4e57-9b0d-2f4a6c8e0123",
            isBaseline = true
        ),
        ShoppingItem(
            id = "4e30a8a0-e51d-45f1-7ecb-2b4c6d8a4578",
            name = "Wood",
            categoryId = "b2e4d6f8-a1c3-4e57-9b0d-2f4a6c8e0123",
            isBaseline = true
        ),
        // Beverages
        ShoppingItem(
            id = "5f41b9b1-f62e-46a2-8fdc-3c5d7e9b5689",
            name = "Coffee",
            categoryId = "c1d3e5f7-b2a4-4c68-8d9e-3a5b7c9f1234",
            isBaseline = true
        ),
        ShoppingItem(
            id = "6a52cac2-073f-47b3-9aed-4d6e8f0c679a",
            name = "Tea",
            categoryId = "c1d3e5f7-b2a4-4c68-8d9e-3a5b7c9f1234",
            isBaseline = true
        ),
        // Cleaning
        ShoppingItem(
            id = "7b63dbd3-1840-48c4-0bfe-5e7f9a1d78ab",
            name = "Cloths",
            categoryId = "e9f1a3b5-d4c6-4e80-0f1a-5c7d9e1b3456",
            isBaseline = true
        ),
        ShoppingItem(
            id = "8c74ece4-2951-49d5-1caf-6f8a0b2e89bc",
            name = "Dishwashing Liquid",
            categoryId = "e9f1a3b5-d4c6-4e80-0f1a-5c7d9e1b3456",
            isBaseline = true
        ),
        ShoppingItem(
            id = "9d85fdf5-3a62-40e6-2db0-7a9b1c3f9acd",
            name = "Fabric Softener",
            categoryId = "e9f1a3b5-d4c6-4e80-0f1a-5c7d9e1b3456",
            isBaseline = true
        ),
        ShoppingItem(
            id = "ae960e06-4b73-41f7-3ec1-8bac2d40abde",
            name = "Sponges",
            categoryId = "e9f1a3b5-d4c6-4e80-0f1a-5c7d9e1b3456",
            isBaseline = true
        ),
        ShoppingItem(
            id = "bfa71f17-5c84-42a8-4fd2-9cbd3e51bcef",
            name = "Washing Machine Soap",
            categoryId = "e9f1a3b5-d4c6-4e80-0f1a-5c7d9e1b3456",
            isBaseline = true
        ),
        // Condiments
        ShoppingItem(
            id = "ca882028-6d95-43b9-5ae3-0dce4f62cd01",
            name = "Mustard",
            categoryId = "f8a0b2c4-e5d7-4f91-1a2b-6d8e0f2c4567",
            isBaseline = true
        ),
        // Dairy
        ShoppingItem(
            id = "db993139-7ea6-44ca-6bf4-1edf5a73de12",
            name = "Butter",
            categoryId = "07b1c3d5-f6e8-4002-2b3c-7e9f1a3d5678",
            isBaseline = true
        ),
        ShoppingItem(
            id = "eca0424a-8fb7-45db-7ca5-2fea6b84ef23",
            name = "Cheese",
            categoryId = "07b1c3d5-f6e8-4002-2b3c-7e9f1a3d5678",
            isBaseline = true
        ),
        ShoppingItem(
            id = "fdb1535b-90c8-46ec-8db6-3afb7c95f034",
            name = "Milk",
            categoryId = "07b1c3d5-f6e8-4002-2b3c-7e9f1a3d5678",
            isBaseline = true
        ),
        ShoppingItem(
            id = "0ec2646c-a1d9-47fd-9ec7-4bac8da60145",
            name = "Yogurt",
            categoryId = "07b1c3d5-f6e8-4002-2b3c-7e9f1a3d5678",
            isBaseline = true
        ),
        // Frozen Foods
        ShoppingItem(
            id = "1fd3757d-b2ea-480e-0fd8-5cbd9eb71256",
            name = "Frozen Berries",
            categoryId = "16c2d4e6-07f9-4113-3c4d-8f0a2b4e6789",
            isBaseline = true
        ),
        ShoppingItem(
            id = "2ae4868e-c3fb-491f-1ae9-6dce0fc82367",
            name = "Frozen Vegetables",
            categoryId = "16c2d4e6-07f9-4113-3c4d-8f0a2b4e6789",
            isBaseline = true
        ),
        // Meat
        ShoppingItem(
            id = "3bf5979f-d40c-4020-2bfa-7edf1ad93478",
            name = "Beef",
            categoryId = "34e4f6a8-29b1-4335-5e6f-0b2c4d6a89ab",
            isBaseline = true
        ),
        ShoppingItem(
            id = "4ca6a8a0-e51d-4131-3cab-8fea2bea4589",
            name = "Chicken",
            categoryId = "34e4f6a8-29b1-4335-5e6f-0b2c4d6a89ab",
            isBaseline = true
        ),
        ShoppingItem(
            id = "5db7b9b1-f62e-4242-4dbc-9afb3cfb569a",
            name = "Pork",
            categoryId = "34e4f6a8-29b1-4335-5e6f-0b2c4d6a89ab",
            isBaseline = true
        ),
        // Oils
        ShoppingItem(
            id = "6ec8cac2-073f-4353-5ecd-0bac4d0c67ab",
            name = "Coconut Oil",
            categoryId = "43f5a7b9-3ac2-4446-6f70-1c3d5e7b9abc",
            isBaseline = true
        ),
        ShoppingItem(
            id = "7fd9dbd3-1840-4464-6fde-1cbd5e1d78bc",
            name = "Olive Oil",
            categoryId = "43f5a7b9-3ac2-4446-6f70-1c3d5e7b9abc",
            isBaseline = true
        ),
        // Pantry
        ShoppingItem(
            id = "8aeaece4-2951-4575-7aef-2dce6f2e89cd",
            name = "Nuts",
            categoryId = "52a6b8c0-4bd3-4557-7a81-2d4e6f8cabed",
            isBaseline = true
        ),
        ShoppingItem(
            id = "9bfbfdf5-3a62-4686-8bf0-3edf7a3f9ade",
            name = "Sweeteners",
            categoryId = "52a6b8c0-4bd3-4557-7a81-2d4e6f8cabed",
            isBaseline = true
        ),
        ShoppingItem(
            id = "ac0ca0a6-4b73-4797-9ca1-4fea8b40abef",
            name = "Treats",
            categoryId = "52a6b8c0-4bd3-4557-7a81-2d4e6f8cabed",
            isBaseline = true
        ),
        // Personal Care
        ShoppingItem(
            id = "bd1db1b7-5c84-48a8-0db2-5afb9c51bc01",
            name = "Body Wash",
            categoryId = "61b7c9d1-5ce4-4668-8b92-3e5f7a9dbcde",
            isBaseline = true
        ),
        ShoppingItem(
            id = "ce2ec2c8-6d95-49b9-1ec3-6bac0d62cd12",
            name = "Mouthwash",
            categoryId = "61b7c9d1-5ce4-4668-8b92-3e5f7a9dbcde",
            isBaseline = true
        ),
        ShoppingItem(
            id = "df3fd3d9-7ea6-40ca-2fd4-7cbd1e73de23",
            name = "Soap Bar",
            categoryId = "61b7c9d1-5ce4-4668-8b92-3e5f7a9dbcde",
            isBaseline = true
        ),
        ShoppingItem(
            id = "e040e4ea-8fb7-41db-3ae5-8dce2f84ef34",
            name = "Toothpaste",
            categoryId = "61b7c9d1-5ce4-4668-8b92-3e5f7a9dbcde",
            isBaseline = true
        ),
        // Produce
        ShoppingItem(
            id = "f151f5fb-90c8-42ec-4bf6-9edf3a95f045",
            name = "Avocados",
            categoryId = "70c8d0e2-6df5-4779-9ca3-4f6a8b0ecdff",
            isBaseline = true
        ),
        ShoppingItem(
            id = "0262060c-a1d9-43fd-5ca7-0fea4ba60156",
            name = "Blueberries",
            categoryId = "70c8d0e2-6df5-4779-9ca3-4f6a8b0ecdff",
            isBaseline = true
        ),
        ShoppingItem(
            id = "1373171d-b2ea-440e-6db8-1afb5cb71267",
            name = "Mushrooms",
            categoryId = "70c8d0e2-6df5-4779-9ca3-4f6a8b0ecdff",
            isBaseline = true
        ),
        ShoppingItem(
            id = "2484282e-c3fb-451f-7ec9-2bac6dc82378",
            name = "Strawberries",
            categoryId = "70c8d0e2-6df5-4779-9ca3-4f6a8b0ecdff",
            isBaseline = true
        ),
        ShoppingItem(
            id = "3595393f-d40c-4620-8fda-3cbd7ed93489",
            name = "Tomatoes",
            categoryId = "70c8d0e2-6df5-4779-9ca3-4f6a8b0ecdff",
            isBaseline = true
        ),
        ShoppingItem(
            id = "46a64a40-e51d-4731-9aeb-4dce8fea459a",
            name = "Vegetables",
            categoryId = "70c8d0e2-6df5-4779-9ca3-4f6a8b0ecdff",
            isBaseline = true
        ),
        // Seafood
        ShoppingItem(
            id = "57b75b51-f62e-4842-0bfc-5edf9afb56ab",
            name = "Fish",
            categoryId = "7fc9e1f3-7ea6-488a-0db4-5a7b9c1fde01",
            isBaseline = true
        ),
        ShoppingItem(
            id = "68c86c62-073f-4953-1cad-6fea0bac67bc",
            name = "Shrimp",
            categoryId = "7fc9e1f3-7ea6-488a-0db4-5a7b9c1fde01",
            isBaseline = true
        ),
        // Seasonings
        ShoppingItem(
            id = "79d97d73-1840-4064-2dbe-7afb1cbd78cd",
            name = "Spices",
            categoryId = "8edad2a4-8fb7-499b-1ec5-6b8c0d2aef12",
            isBaseline = true
        ),
        // Supplements
        ShoppingItem(
            id = "8aea8e84-2951-4175-3ecf-8bac2dce89de",
            name = "Berocca Boost",
            categoryId = "0afcc4c6-a1d9-41bd-3ae7-8d0e2f4c0134",
            isBaseline = true
        )
    )

    /**
     * Finds a category by its ID.
     *
     * @param id The unique GUID of the category to find.
     * @return The matching [ShoppingCategory], or null if not found.
     */
    fun categoryById(id: String): ShoppingCategory? = categories.find { it.id == id }
}
