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
[X] Edit item details (name, category, barcodes) as plans change.  
[X] Scan and resolve unknown barcodes without leaving Shopping workflows.  
[X] Return later and find the same list state.

## Implementation Status Snapshot

- [X] Shopping List — Grouped by category and sorted by item name
- [X] Item Actions — Add/edit/remove with swipe actions
- [X] Edit Flow — Edit name, category, and multiple barcodes
- [X] Scan Flow — Scan from Shopping List and resolve found/not-found
- [X] Replenish Sync — Hide/show based on Shopping List membership
- [X] Persistence — Survives app restart and phone restart
- [X] Shopping List FABs swapped (Add on end, Scan on start) and icon-only

## Functional Requirements

### 1) List Visibility

[X] User must be able to view current Shopping items in a list.  
[X] Shopping items must be displayed ordered by category, then by item name.  
[X] Items must be grouped by category with category names displayed as header items.  
[X] Empty states must be clear and readable.

### 2) Item Management

[X] Shopping List must initially be empty.  
[X] User must be able to add a Shopping item.  
[X] When adding a Shopping item, user must select a category from the available categories (both with baseline items and empty categories).  
[X] User must be able to edit a Shopping item in a dedicated edit screen.  
[X] User must be able to rename a Shopping item.  
[X] User must be able to change a Shopping item category.  
[X] User must be able to remove any item (including baseline items) from the Shopping List.  
[X] User must be able to swipe right-to-left on a Shopping List item to delete it.  
[X] User must be able to swipe left-to-right on a Shopping List item to show action options (rename/edit).  
[X] Baseline items can be removed from the Shopping List but must persist in baseline source data.  
[X] Non-baseline items can be added and removed by the user.

### 3) Barcode Management and Scan Flow

[X] User must be able to associate multiple barcodes with a single Shopping item.  
[X] User must be able to add, view, and remove barcodes from a Shopping item.  
[X] User must be able to scan barcode values while editing an item and append them to that item.  
[X] Shopping List must expose a scan action for item lookup/linking via an inline barcode icon on each item and a global scan FAB.  
[X] Tapping the inline barcode icon opens the Scanner flow (icon-only, no visible text label).  
[X] Scanning from Shopping List opens a post-scan resolve screen (`BarcodeResultFragment`) where the user can link the scanned barcode to an existing item or create a new item with the barcode attached.  
[X] If scanned barcode is found, resolve screen must show which item it belongs to and preselect that item.  
[X] Resolve screen must allow selecting an existing item and linking the scanned barcode.  
[X] Resolve screen must allow adding a new item (with category selection) and attaching the scanned barcode.

### 4) Replenish Integration

[X] User must be able to double-tap a Replenish List item to add it to the Shopping List.  
[X] Replenish List must hide items that are currently in the Shopping List.  
[X] Replenish List must re-display items when they are removed from the Shopping List.

### 5) Canonical Item Naming

[X] Shopping items must use canonical generic names.  
[X] Baseline items must be managed through the Baseline Item Configuration Table below.  
[X] Empty categories (for custom items only) must be managed through the Empty Category Configuration Table below.  
[X] Brand-specific names must be rejected.  
[X] Duplicate handling must be explicit and consistent.  
[X] Each category group must have a unique GUID identifier generated in source code.  
[X] Each baseline item must have a unique GUID identifier generated in source code.

#### Baseline Item Configuration Table

| Category | Baseline Item |
| --- | --- |
| Bakery | Low-Carb Bread |
| Barbecue | Charcoal |
| Barbecue | Fire Lighters |
| Barbecue | Wood |
| Beverages | Coffee |
| Beverages | Tea |
| Cleaning | Cloths |
| Cleaning | Dishwashing Liquid |
| Cleaning | Fabric Softener |
| Cleaning | Sponges |
| Cleaning | Washing Machine Soap |
| Condiments | Mustard |
| Dairy | Butter |
| Dairy | Cheese |
| Dairy | Milk |
| Dairy | Yogurt |
| Frozen Foods | Frozen Berries |
| Frozen Foods | Frozen Vegetables |
| Meat | Beef |
| Meat | Chicken |
| Meat | Pork |
| Oils | Coconut Oil |
| Oils | Olive Oil |
| Pantry | Nuts |
| Pantry | Sweeteners |
| Pantry | Treats |
| Personal Care | Body Wash |
| Personal Care | Mouthwash |
| Personal Care | Soap Bar |
| Personal Care | Toothpaste |
| Produce | Avocados |
| Produce | Blueberries |
| Produce | Mushrooms |
| Produce | Strawberries |
| Produce | Tomatoes |
| Produce | Vegetables |
| Seafood | Fish |
| Seafood | Shrimp |
| Seasonings | Spices |
| Supplements | Berocca Boost |

### 6) Baseline Item Images

[X] A generated image must exist for each baseline item in the configuration table.  
[X] Each generated image must semantically match its baseline item (for example, `Cheese` must use a cheese image).  
[X] Each generated image must have clear contrast between the item and the image background.

### 7) Data Continuity

[X] Shopping List must persist across app restarts and phone restarts in its current state (sorted by category, then item name).  
[X] Replenish List must persist across app restarts and phone restarts in its current state (sorted by category, then item name).  
[X] Existing items must remain intact after normal update cycles.  
[X] Item order (category and name sorting) must be maintained after any restart.

## Non-Functional Requirements

[X] Feature behavior must be consistent with app-wide standards.  
[X] User-facing text must be clear, concise, and in Standard American English.  
[X] Interactions must be stable and responsive in normal usage.  
[X] Visual presentation must align with the Jarvis / Iron Man theme as defined in `docs/style-guide/ux-style-guide.md`.

## Acceptance Criteria

Shopping feature is accepted when all are true:

[X] Shopping List and Replenish List grouping/sorting rules are always correct.  
[X] Add/edit/remove flows update immediately and persist correctly.  
[X] Edit screen supports name, category, and multiple barcodes.  
[X] Swipe gestures work in both directions with correct actions and feedback.  
[X] Scan flow opens a resolve screen for found and not-found outcomes, with clear found-item context when matched.  
[X] Resolve screen supports both linking to an existing item and adding a new categorized item with the scanned barcode.  
[X] Replenish hide/show synchronization with Shopping membership is reliable.  
[X] Canonical naming, duplicate handling, and image semantics remain consistent in common user flows.
[X] Shopping-list FABs are swapped: Add at left (bottom|start), Scan at right (bottom|end), both icon-only and accessible via contentDescription.
