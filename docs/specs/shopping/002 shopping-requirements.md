# Shopping Feature Requirements

## Checkbox Status Legend

- `[ ]` — Not started
- `[-]` — In progress
- `[X]` — Done (or any non-empty character except dash)

Use these checkboxes to track implementation status of each requirement.

## Objective

Deliver Shopping as a user-ready feature that allows users to maintain a practical Shopping List with reliable behavior and clear interactions.

## User Goals

[ ] Quickly view Shopping items.  
[ ] Add needed items with minimal effort.  
[ ] Edit or remove items as plans change.  
[ ] Return later and find the same list state.

## Functional Requirements

### List Visibility

[ ] User must be able to view current Shopping items in a list.  
[ ] Shopping items must be displayed ordered by category, then by item name.  
[ ] Items must be grouped by category with category names displayed as header items.  
[ ] Empty states must be clear and readable.

### Item Management

[ ] Shopping List must initially be empty.  
[ ] User must be able to add a Shopping item.  
[ ] When adding a Shopping item, user must select a category from the available categories (both with baseline items and empty categories).  
[ ] User must be able to rename a Shopping item.  
[ ] User must be able to remove any item (including baseline items) from the shopping list.  
[ ] Baseline items can be removed from the shopping list but must persist in the Replenish List.  
[ ] Non-baseline items can be added and removed by the user.  
[ ] User must be able to associate multiple barcodes with a single Shopping item.  
[ ] User must be able to add, view, and remove barcodes from a Shopping item.

### Canonical Item Naming

[ ] Shopping items must use canonical generic names.  
[ ] Baseline items must be managed through the Baseline Item Configuration Table below.  
[ ] Empty categories (for custom items only) must be managed through the Empty Category Configuration Table below.  
[ ] Brand-specific names must be rejected.  
[ ] Duplicate handling must be explicit and consistent.  
[ ] Each category group must have a unique GUID identifier generated in source code.  
[ ] Each baseline item must have a unique GUID identifier generated in source code.

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

[ ] A generated image must exist for each baseline item in the configuration table.  
[ ] Each generated image must semantically match its baseline item (for example, `Cheese` must use a cheese image).  
[ ] Each generated image must have clear contrast between the item and the image background.

### Data Continuity

[ ] Shopping List must persist across app restarts and phone restarts in its current state (sorted by category, then item name).  
[ ] Replenish List must persist across app restarts and phone restarts in its current state (sorted by category, then item name).  
[ ] Existing items must remain intact after normal update cycles.  
[ ] Item order (category and name sorting) must be maintained after any restart.

## Non-Functional Requirements

[ ] Feature behavior must be consistent with app-wide standards.  
[ ] User-facing text must be clear, concise, and in Standard American English.  
[ ] Interactions must be stable and responsive in normal usage.  
[ ] Visual presentation must align with the Jarvis / Iron Man theme.

## Acceptance Criteria

Shopping feature is accepted when all are true:

[ ] User can open Shopping from Home.  
[ ] Shopping List starts empty when first opened.  
[ ] User can add items by selecting a category and entering an item name.  
[ ] Shopping items are grouped by category with category headers displayed.  
[ ] Items within each category are displayed in alphabetical order by item name.  
[ ] Replenish List is grouped by category with category headers displayed.  
[ ] Replenish List items are displayed in alphabetical order by item name within each category.  
[ ] User can add custom items to the shopping list.  
[ ] User can remove any item (including baseline items) from the shopping list.  
[ ] Baseline items can be removed from the Shopping List but remain available in the Replenish List.  
[ ] User can rename items successfully.  
[ ] User can associate multiple barcodes with each Shopping item.  
[ ] User can add, view, and remove barcodes from Shopping items.  
[ ] Shopping List persists after app restart in its current state and sorted order.  
[ ] Shopping List persists after phone restart in its current state and sorted order.  
[ ] Replenish List persists after app restart in its current state and sorted order.  
[ ] Replenish List persists after phone restart in its current state and sorted order.  
[ ] Canonical naming rules are enforced and brand-specific names are rejected.  
[ ] Duplicate handling follows documented rules.  
[ ] Baseline items are set through the Baseline Item Configuration Table.  
[ ] Each category group has a unique GUID identifier.  
[ ] Each baseline item has a unique GUID identifier.  
[ ] Each baseline item has a generated image that matches the item.  
[ ] Generated baseline item images maintain clear item-to-background contrast.  
[ ] Feature remains stable during common user flows.
