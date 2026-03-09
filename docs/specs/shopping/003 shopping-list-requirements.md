# Shopping List Requirements

## Checkbox Status Legend

- `[ ]` — Not started
- `[-]` — In progress
- `[X]` — Done (or any non-empty character except dash)

Use these checkboxes to track implementation status of each requirement.

## Objective

Define requirements for presenting and maintaining the Shopping List as a clear, reliable, and user-focused list experience.

## Scope

This document covers list visibility, list updates, list persistence behavior, barcode management, and the relationship between Shopping List and Replenish List for Shopping items.

## Functional Requirements

### List Visibility

[X] Shopping List must initially be empty when first opened.  
[X] User must be able to view all current Shopping items in one list.  
[X] Shopping items must be displayed ordered by category, then by item name.  
[X] Items must be grouped by category with category names displayed as header items.  
[X] Empty state messaging must clearly explain that no items are currently saved.  
[X] Populated state must remain readable and scannable.

### List Maintenance

[X] New items must appear in the list immediately after successful add.  
[X] Renamed items must update in the list immediately after successful edit.  
[X] Removed items (including baseline items) must no longer appear after successful delete.  
[ ] User can swipe right-to-left on an item to delete it with red background indicator.  
[ ] User can swipe left-to-right on an item to reveal action options (rename, manage barcodes) with cyan background indicator.  
[X] Baseline items can be removed from the Shopping List but must persist in the Replenish List.  
[X] Duplicate-handling rules must be applied consistently.

### Barcode Management

[X] User must be able to associate multiple barcodes with a single Shopping item.  
[X] User must be able to add, view, and remove barcodes from a Shopping item.  
[X] Barcodes must persist with their associated items.

### Canonical Item Naming

[X] List items must use canonical generic names.  
[X] Baseline items are defined in `002 shopping-requirements.md` Baseline Item Configuration Table.  
[X] Brand-specific names must be rejected.  
[X] Name validation outcomes must be clear to the user.

### Replenish List

[X] Replenish List must display all baseline items from the Baseline Item Configuration Table.  
[X] Replenish List must be displayed ordered by category, then by item name.  
[X] Replenish List items must be grouped by category with category names displayed as header items.  
[X] Replenish List must persist across app restarts and phone restarts.

### Persistence

[X] Shopping List content must be restored after app restart in its current state and sorted order.  
[X] Shopping List content must be restored after phone restart in its current state and sorted order.  
[X] Replenish List content must be restored after app restart in its current state and sorted order.  
[X] Replenish List content must be restored after phone restart in its current state and sorted order.  
[X] List integrity must be maintained during normal update cycles.

## Non-Functional Requirements

[X] List interactions must be responsive and stable.  
[X] Text and labels must be clear and concise.  
[X] User-facing text must be in Standard American English.  
[X] Presentation must align with the Jarvis / Iron Man visual standards as defined in `docs/style-guide/ux-style-guide.md`.

## Acceptance Criteria

Shopping List requirements are accepted when all are true:

[X] Shopping List starts empty when first opened.  
[X] Items are grouped by category with category headers displayed.  
[X] Items within each category are displayed in alphabetical order by item name.  
[X] Empty and populated states are both clear.  
[X] Add, rename, and remove operations are reflected correctly in the list.  
[ ] Swipe right-to-left deletes items with red background indicator.  
[ ] Swipe left-to-right shows action options with cyan background indicator.  
[X] User can associate multiple barcodes with each item.  
[X] Replenish List displays all baseline items grouped by category with category headers.  
[X] Replenish List items are displayed in alphabetical order by item name within each category.  
[X] Baseline items can be removed from Shopping List but remain in Replenish List.  
[X] Shopping List persists after app restart in sorted order.  
[X] Shopping List persists after phone restart in sorted order.  
[X] Replenish List persists after app restart in sorted order.  
[X] Replenish List persists after phone restart in sorted order.  
[X] Canonical naming rules are enforced and brand-specific names are rejected.  
[X] Duplicate handling remains consistent across list updates.
