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

[ ] Shopping List must initially be empty when first opened.  
[ ] User must be able to view all current Shopping items in one list.  
[ ] Shopping items must be displayed ordered by category, then by item name.  
[ ] Items must be grouped by category with category names displayed as header items.  
[ ] Empty state messaging must clearly explain that no items are currently saved.  
[ ] Populated state must remain readable and scannable.

### List Maintenance

[ ] New items must appear in the list immediately after successful add.  
[ ] Renamed items must update in the list immediately after successful edit.  
[ ] Removed items (including baseline items) must no longer appear after successful delete.  
[ ] Baseline items can be removed from the Shopping List but must persist in the Replenish List.  
[ ] Duplicate-handling rules must be applied consistently.

### Barcode Management

[ ] User must be able to associate multiple barcodes with a single Shopping item.  
[ ] User must be able to add, view, and remove barcodes from a Shopping item.  
[ ] Barcodes must persist with their associated items.

### Canonical Item Naming

[ ] List items must use canonical generic names.  
[ ] Baseline items are defined in `002 shopping-requirements.md` Baseline Item Configuration Table.  
[ ] Brand-specific names must be rejected.  
[ ] Name validation outcomes must be clear to the user.

### Replenish List

[ ] Replenish List must display all baseline items from the Baseline Item Configuration Table.  
[ ] Replenish List must be displayed ordered by category, then by item name.  
[ ] Replenish List items must be grouped by category with category names displayed as header items.  
[ ] Replenish List must persist across app restarts and phone restarts.

### Persistence

[ ] Shopping List content must be restored after app restart in its current state and sorted order.  
[ ] Shopping List content must be restored after phone restart in its current state and sorted order.  
[ ] Replenish List content must be restored after app restart in its current state and sorted order.  
[ ] Replenish List content must be restored after phone restart in its current state and sorted order.  
[ ] List integrity must be maintained during normal update cycles.

## Non-Functional Requirements

[ ] List interactions must be responsive and stable.  
[ ] Text and labels must be clear and concise.  
[ ] User-facing text must be in Standard American English.  
[ ] Presentation must align with the Jarvis / Iron Man visual standards.

## Acceptance Criteria

Shopping List requirements are accepted when all are true:

[ ] Shopping List starts empty when first opened.  
[ ] Items are grouped by category with category headers displayed.  
[ ] Items within each category are displayed in alphabetical order by item name.  
[ ] Empty and populated states are both clear.  
[ ] Add, rename, and remove operations are reflected correctly in the list.  
[ ] User can associate multiple barcodes with each item.  
[ ] Replenish List displays all baseline items grouped by category with category headers.  
[ ] Replenish List items are displayed in alphabetical order by item name within each category.  
[ ] Baseline items can be removed from Shopping List but remain in Replenish List.  
[ ] Shopping List persists after app restart in sorted order.  
[ ] Shopping List persists after phone restart in sorted order.  
[ ] Replenish List persists after app restart in sorted order.  
[ ] Replenish List persists after phone restart in sorted order.  
[ ] Canonical naming rules are enforced and brand-specific names are rejected.  
[ ] Duplicate handling remains consistent across list updates.
