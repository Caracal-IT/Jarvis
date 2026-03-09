package com.github.caracal.jarvis.shopping.data

/**
 * Validates shopping item names against canonical naming rules.
 *
 * Brand-specific names are rejected to ensure the shopping list uses
 * generic canonical names only.
 */
object NamingValidator {

    /** Known brand name fragments that are not permitted as item names. */
    private val knownBrands: Set<String> = setOf(
        "coca-cola", "cocacola", "pepsi", "nestle", "kellogg",
        "heinz", "campbell", "dole", "del monte", "kraft", "lays", "doritos",
        "pringles", "oreo", "nabisco", "quaker", "tropicana", "minute maid",
        "gatorade", "red bull", "monster energy", "sprite", "fanta",
        "hellmann", "hellman", "mccain", "birds eye", "birdseye",
        "ben & jerry", "haagen-dazs", "dreyer", "breyers",
        "philadelphia", "velveeta", "laughing cow",
        "johnsonville", "oscar mayer", "oscar meyer",
        "tyson", "perdue", "butterball",
        "colgate", "crest", "sensodyne", "oral-b", "aquafresh",
        "dove", "axe", "old spice", "nivea", "pantene", "head & shoulders",
        "tide", "ariel", "persil", "fairy", "dawn", "ajax", "mr. clean",
        "finish", "cascade", "bounce",
        "kirkland", "costco brand"
    )

    /**
     * Validation result for an item name.
     *
     * @property isValid True if the name passes all validation rules.
     * @property errorMessage A user-facing message describing the validation failure, or null if valid.
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    /**
     * Validates the given item name.
     *
     * Rules enforced:
     * - Name must not be blank.
     * - Name must not match a known brand.
     *
     * @param name The candidate item name.
     * @return A [ValidationResult] describing the outcome.
     */
    fun validate(name: String): ValidationResult {
        val trimmed = name.trim()
        if (trimmed.isBlank()) {
            return ValidationResult(isValid = false, errorMessage = "Item name must not be empty.")
        }
        val lower = trimmed.lowercase()
        val matchedBrand = knownBrands.firstOrNull { lower.contains(it) }
        if (matchedBrand != null) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Brand-specific names are not allowed. Please use a generic name."
            )
        }
        return ValidationResult(isValid = true)
    }
}
