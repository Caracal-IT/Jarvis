# Shopping Feature Requirements

## Checkbox Status Legend

- `[ ]` — Not started
- `[-]` — In progress
- `[X]` — Done (or any non-empty character except dash)

Use these checkboxes to track implementation status of each requirement.

## Objective

Deliver Shopping as a user-ready feature that allows users to maintain a practical Shopping List with reliable behavior and clear interactions.

## User Goals

[X] Quickly view Shopping items.  
[X] Add needed items with minimal effort.  
[X] Edit or remove items as plans change.  
[X] Return later and find the same list state.

## Functional Requirements

### List Visibility

[X] User must be able to view current Shopping items in a list.  
[X] Shopping items must be displayed ordered by category, then by item name.  
[X] Items must be grouped by category with category names displayed as header items.  
[X] Empty states must be clear and readable.

### Item Management

[X] Shopping List must initially be empty.  
[X] User must be able to add a Shopping item.  
[X] When adding a Shopping item, user must select a category from the available categories (both with baseline items and empty categories).  
[X] User must be able to rename a Shopping item.  
[X] User must be able to remove any item (including baseline items) from the shopping list.  
[X] Baseline items can be removed from the shopping list but must persist in the Replenish List.  
[X] Non-baseline items can be added and removed by the user.  
[X] User must be able to associate multiple barcodes with a single Shopping item.  
[X] User must be able to add, view, and remove barcodes from a Shopping item.

### Canonical Item Naming

[X] Shopping items must use canonical generic names.  
[X] Baseline items must be managed through the Baseline Item Configuration Table below.  
[X] Empty categories (for custom items only) must be managed through the Empty Category Configuration Table below.  
[X] Brand-specific names must be rejected.  
[X] Duplicate handling must be explicit and consistent.  
[X] Each category group must have a unique GUID identifier generated in source code.  
[X] Each baseline item must have a unique GUID identifier generated in source code.

#### Baseline Item Configuration Table

| Category         | Baseline Item        |
|------------------|----------------------|
| Bakery           | Low-Carb Bread       |
| Barbecue         | Charcoal             |
| Barbecue         | Fire Lighters        |
| Barbecue         | Wood                 |
| Beverages        | Coffee               |
| Beverages        | Tea                  |
| Cleaning         | Cloths               |
| Cleaning         | Dishwashing Liquid   |
| Cleaning         | Fabric Softener      |
| Cleaning         | Sponges              |
| Cleaning         | Washing Machine Soap |
| Condiments       | Mustard              |
| Dairy            | Butter               |
| Dairy            | Cheese               |
| Dairy            | Milk                 |
| Dairy            | Yogurt               |
| Frozen Foods     | Frozen Berries       |
| Frozen Foods     | Frozen Vegetables    |
| Meat             | Beef                 |
| Meat             | Chicken              |
| Meat             | Pork                 |
| Oils             | Coconut Oil          |
| Oils             | Olive Oil            |
| Pantry           | Nuts                 |
| Pantry           | Sweeteners           |
| Pantry           | Treats               |
| Personal Care    | Body Wash            |
| Personal Care    | Mouthwash            |
| Personal Care    | Soap Bar             |
| Personal Care    | Toothpaste           |
| Produce          | Avocados             |
| Produce          | Blueberries          |
| Produce          | Mushrooms            |
| Produce          | Strawberries         |
| Produce          | Tomatoes             |
| Produce          | Vegetables           |
| Seafood          | Fish                 |
| Seafood          | Shrimp               |
| Seasonings       | Spices               |
| Supplements      | Berocca Boost        |


### Baseline Item Images

[X] A generated image must exist for each baseline item in the configuration table.  
[X] Each generated image must semantically match its baseline item (for example, `Cheese` must use a cheese image).  
[X] Each generated image must have clear contrast between the item and the image background.

### Data Continuity

[X] Shopping List must persist across app restarts and phone restarts in its current state (sorted by category, then item name).  
[X] Replenish List must persist across app restarts and phone restarts in its current state (sorted by category, then item name).  
[X] Existing items must remain intact after normal update cycles.  
[X] Item order (category and name sorting) must be maintained after any restart.

## Non-Functional Requirements

[X] Feature behavior must be consistent with app-wide standards.  
[X] User-facing text must be clear, concise, and in Standard American English.  
[X] Interactions must be stable and responsive in normal usage.  
[X] Visual presentation must align with the Jarvis / Iron Man theme.

## Acceptance Criteria

Shopping feature is accepted when all are true:

[X] User can open Shopping from Home.  
[X] Shopping List starts empty when first opened.  
[X] User can add items by selecting a category and entering an item name.  
[X] Shopping items are grouped by category with category headers displayed.  
[X] Items within each category are displayed in alphabetical order by item name.  
[X] Replenish List is grouped by category with category headers displayed.  
[X] Replenish List items are displayed in alphabetical order by item name within each category.  
[X] User can add custom items to the shopping list.  
[X] User can remove any item (including baseline items) from the shopping list.  
[X] Baseline items can be removed from the Shopping List but remain available in the Replenish List.  
[X] User can rename items successfully.  
[X] User can associate multiple barcodes with each Shopping item.  
[X] User can add, view, and remove barcodes from Shopping items.  
[X] Shopping List persists after app restart in its current state and sorted order.  
[X] Shopping List persists after phone restart in its current state and sorted order.  
[X] Replenish List persists after app restart in its current state and sorted order.  
[X] Replenish List persists after phone restart in its current state and sorted order.  
[X] Canonical naming rules are enforced and brand-specific names are rejected.  
[X] Duplicate handling follows documented rules.  
[X] Baseline items are set through the Baseline Item Configuration Table.  
[X] Each category group has a unique GUID identifier.  
[X] Each baseline item has a unique GUID identifier.  
[X] Each baseline item has a generated image that matches the item.  
[X] Generated baseline item images maintain clear item-to-background contrast.  
[X] Feature remains stable during common user flows.
