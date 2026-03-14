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
- [X] Inline barcode icon present on each Shopping List row (icon-only)
- [X] Shopping List FABs swapped: Add at left (bottom|start), Scan at right (bottom|end), both icon-only
- [X] Scan overlay and acceptance rules implemented (blur/dim outside rectangle; only accept barcodes whose center is inside the rectangle)
- [X] Scan attach dialog implemented (Scan / Link existing / Add new flows) with haptic and pulse feedback on-in-frame detection

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
[X] Inline barcode icon: the icon is icon-only (no visible label) and opens the "scan / attach" dialog for that item.  
[X] Tapping the inline barcode icon opens a small "Scan / Link / Add" dialog that lets the user: 
  - Scan a barcode using the camera (launch the scanner flow).  
  - Select an existing item from a spinner and enter/confirm a barcode to link.  
  - Create a new item (name + category) and attach the barcode in one flow.  
[X] The global Scan FAB opens the scanner in list mode and follows the same post-scan resolve flow.  
[X] The scanner UI uses a centered rounded rectangle scan window; the area outside the window is dimmed and (on supported devices) blurred via a RenderEffect to focus the user on the target.  
[X] Barcode acceptance rule: the scanner will only accept a barcode if the detected barcode's bounding-box center lies inside the scan rectangle (prevents accidental off-center scans).  
[X] The scan window is responsive: default is a fraction of the smaller screen dimension (responsive to portrait/landscape) rather than a hardcoded absolute size.  
[X] When a barcode's center is inside the scan window the UI gives clear feedback: a short haptic pulse and a brief outline pulse animation around the frame before acceptance.  
[X] Devices that do not support RenderEffect will still show the dimmed overlay and the selection rules (no crash, graceful degradation).

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

## Data / Repository Behavior (implementation details)

The Shopping feature uses a SharedPreferences-backed repository (`SharedPrefsShoppingRepository`) to persist shopping and replenish data. The following behaviors are part of the canonical implementation and are considered requirements for correctness and test coverage:

- Storage: the repository persists the full item list as JSON in SharedPreferences under `shopping_prefs` using the key `shopping_list_v1`. Each item JSON object contains: id, name, category_id, is_baseline, is_on_shopping_list and a `barcodes` array.

- Sorting: item ordering uses an item comparator which sorts first by the category name (resolved via `BaselineData.categoryById(categoryId)`) then by the item name, both compared case-insensitively.

- Categories: `getCategories()` returns the canonical Baseline categories (from `BaselineData`) sorted by name.

- updateShoppingItem(...) semantics:
  - Will return `false` and refuse the update if the new name (trimmed) would duplicate another item's name in the same category (case-insensitive), preserving uniqueness per-category.
  - If the update succeeds, the item is replaced (copy) and the repository is saved to prefs.

- addShoppingItemWithBarcode(name, categoryId, barcode, isBaseline):
  - If an existing item matches the same name (case-insensitive) and category, the method merges the barcode into that item (append + distinct) and sets `isOnShoppingList = true`. The existing item's `isBaseline` flag is ORed with the provided `isBaseline`.
  - If no existing item matches, a new ShoppingItem is created with a generated UUID, the provided barcode as the single-element barcode list, `isOnShoppingList = true`, and `isBaseline` as provided.
  - The method always persists changes and returns `true` when the addition/merge completes.

- addShoppingItem(name, categoryId, isBaseline):
  - If an item with the same name and category exists, it is marked `isOnShoppingList = true` and returned (no duplicate is created).
  - Otherwise a new item is created, added to the repository, persisted, and returned.

- removeShoppingItem(itemId):
  - If the item is a baseline item, it is not deleted from storage; instead it is retained but marked `isOnShoppingList = false` so that baseline data is preserved while removing it from the active shopping list.
  - If the item is non-baseline, it is permanently removed from storage.
  - Changes are saved to SharedPreferences.

- addBarcode(itemId, barcode) / removeBarcode(itemId, barcode):
  - `addBarcode` appends the barcode to the item's barcode list only if it is not already present (deduplicated via list membership), then persists.
  - `removeBarcode` removes the barcode from the item's barcode list and persists.

- findByBarcode(barcode):
  - Returns the first ShoppingItem whose barcode list contains the given barcode (no guaranteed global uniqueness is enforced by the repository; business logic should handle duplicates as needed).

- Baseline population:
  - On initialization the repository calls `ensureBaselineItems()` which ensures every baseline item from `BaselineData.baselineItems` exists in storage (added with `isOnShoppingList = false` if missing).

These behaviors are part of the contract and must be covered by unit tests and QA scenarios (duplicate-name rejection, barcode merging, baseline vs. non-baseline remove semantics, persistence across restarts).
