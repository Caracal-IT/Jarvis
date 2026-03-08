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
[ ] Empty states must be clear and readable.

### Item Management

[ ] User must be able to add a Shopping item.  
[ ] User must be able to rename a Shopping item.  
[ ] User must be able to remove a Shopping item.  
[ ] User must be able to associate multiple barcodes with a single Shopping item.  
[ ] User must be able to add, view, and remove barcodes from a Shopping item.

### Canonical Item Naming

[ ] Shopping items must use canonical generic names.  
[ ] Baseline items must be managed through the configuration table below.  
[ ] Brand-specific names must be rejected.  
[ ] Duplicate handling must be explicit and consistent.

#### Baseline Item Configuration Table

| Category         | Baseline Item        |
|------------------|----------------------|
| Beverages        | Coffee               |
| Beverages        | Tea                  |
| Condiments       | Mustard              |
| Condiments       | Olive Oil            |
| Dairy            | Butter               |
| Dairy            | Cheese               |
| Dairy            | Milk                 |
| Dairy            | Yogurt               |
| Household        | Body Wash            |
| Household        | Cloths               |
| Household        | Dishwashing Liquid   |
| Household        | Fabric Softener      |
| Household        | Fire Lighters        |
| Household        | Mouthwash            |
| Household        | Soap Bar             |
| Household        | Sponges              |
| Household        | Toothpaste           |
| Household        | Washing Machine Soap |
| Household        | Wood                 |
| Pantry           | Nuts                 |
| Pantry           | Spices               |
| Pantry           | Sweeteners           |
| Pantry           | Treats               |
| Produce          | Avocados             |
| Produce          | Blueberries          |
| Produce          | Mushrooms            |
| Produce          | Strawberries         |
| Produce          | Tomatoes             |
| Produce          | Vegetables           |
| Protein          | Chicken              |
| Protein          | Eggs                 |
| Protein          | Fish                 |

### Baseline Item Images

[ ] A generated image must exist for each baseline item in the configuration table.  
[ ] Each generated image must semantically match its baseline item (for example, `Cheese` must use a cheese image).  
[ ] Each generated image must have clear contrast between the item and the image background.

### Data Continuity

[ ] Shopping List state must persist across app restarts.  
[ ] Existing items must remain intact after normal update cycles.

## Non-Functional Requirements

[ ] Feature behavior must be consistent with app-wide standards.  
[ ] User-facing text must be clear, concise, and in Standard American English.  
[ ] Interactions must be stable and responsive in normal usage.  
[ ] Visual presentation must align with the Jarvis / Iron Man theme.

## Acceptance Criteria

Shopping feature is accepted when all are true:

[ ] User can open Shopping from Home.  
[ ] Shopping items are displayed ordered by category, then by item name.  
[ ] User can add, rename, and remove items successfully.  
[ ] User can associate multiple barcodes with each Shopping item.  
[ ] User can add, view, and remove barcodes from Shopping items.  
[ ] Shopping List persists after restart.  
[ ] Canonical naming rules are enforced and brand-specific names are rejected.  
[ ] Duplicate handling follows documented rules.  
[ ] Baseline items are set through the Baseline Item Configuration Table.  
[ ] Each enabled baseline item has a generated image that matches the item.  
[ ] Generated baseline item images maintain clear item-to-background contrast.  
[ ] Feature remains stable during common user flows.
