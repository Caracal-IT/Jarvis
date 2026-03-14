# Shopping List Requirements

## Checkbox Status Legend

- `[ ]` — Not started
- `[-]` — In progress
- `[X]` — Done (or any non-empty character except dash)

Use these checkboxes to track implementation status of each requirement.

## Objective

Define requirements for presenting and maintaining the Shopping List as a clear, reliable, and user-focused list experience.

## Scope

This document covers:

- Shopping List visibility and ordering
- Item add/edit/remove behavior
- Edit screen behavior (name, category, multiple barcodes)
- Barcode scan behavior from Shopping List
- Replenish-to-Shopping interaction rules
- Persistence and continuity

## Implementation Status Snapshot

- [X] List Rendering — Group by category, then sort by item name
- [X] List Rendering — Clear empty state and readable populated state
- [X] Item Actions — Swipe right-to-left deletes item
- [X] Item Actions — Swipe left-to-right reveals rename/edit action
- [X] Edit Screen — Edit name and category in one screen
- [X] Edit Screen — Add/remove multiple barcodes in one screen
- [X] Scan Flow — Inline barcode icon present on each item and Scan FAB available
- [X] Scan Flow — Post-scan resolve screen opens for found and not-found barcodes
- [X] Scan Flow — Resolve screen supports link existing item and add new item with scanned barcode
- [X] Replenish Sync — Hide items already present in Shopping List
- [X] Replenish Sync — Re-show items removed from Shopping List
- [X] Persistence — Shopping/Replenish state survives app/device restart
- [X] Scan UI — Dim/blur overlay outside scan window and responsive centered rounded rectangle
- [X] Scan acceptance — Only accept barcodes whose bounding-box center is inside the scan frame
- [X] Feedback — Haptic and pulse feedback when detection is inside the scan window

## Functional Requirements

### 1) List Visibility

[X] Shopping List must initially be empty when first opened.  
[X] User must be able to view all current Shopping items in one list.  
[X] Shopping items must be displayed ordered by category, then by item name.  
[X] Items must be grouped by category with category names displayed as header items.  
[X] Empty state messaging must clearly explain that no items are currently saved.  
[X] Populated state must remain readable and scannable.

### 2) List Maintenance

[X] New items must appear in the list immediately after successful add.  
[X] Edited items must update in the list immediately after successful save.  
[X] Removed items (including baseline items) must no longer appear after successful delete.  
[X] User can swipe right-to-left on an item to delete it with red background indicator.  
[X] User can swipe left-to-right on an item to reveal action options (rename/edit) with green background indicator.  
[X] Duplicate-handling rules must be applied consistently.

### 3) Edit Screen Requirements

[X] User must be able to edit item name.  
[X] User must be able to change item category.  
[X] User must be able to add barcode values manually from the edit screen.  
[X] User must be able to remove barcode values from the edit screen.  
[X] User must be able to keep multiple barcodes linked to one item.  
[X] User must be able to scan a barcode from the edit screen and add it to the item.

### 4) Barcode Scan Flow (Shopping List Screen)

[X] Shopping List must expose a scan action button (global FAB) and an inline per-item barcode icon.  
[X] Both the inline icon and the global FAB are icon-only and use contentDescription for accessibility.  
[X] Tapping the inline barcode icon opens a "Scan / Link / Add" dialog: the dialog supports scanning, linking to an existing item (spinner), or adding a new item with the barcode pre-attached.  
[X] The scanner UI presents a centered rounded rectangle scan window; the rest of the preview is dimmed and, where supported, blurred.  
[X] The scan window size is responsive (a fraction of the screen's smaller dimension) and adapts to portrait or landscape.  
[X] The scanner will only accept detections whose bounding-box center maps inside the scan window.  
[X] When a barcode is detected inside the window users receive both visual (pulse) and haptic feedback before acceptance.  
[X] The post-scan resolve flow (found/not-found) supports linking to an existing item or creating a new categorized item with the scanned barcode.

### 5) Replenish List Sync Rules

[X] Baseline items can be removed from the Shopping List but must persist in baseline source data.  
[X] Replenish List must display baseline items not currently present in Shopping List.  
[X] Replenish List must automatically hide items added to Shopping List.  
[X] Replenish List must automatically re-show items removed from Shopping List.  
[X] Replenish List must be ordered by category, then by item name and grouped by category header.

### 6) Canonical Naming

[X] List items must use canonical generic names.  
[X] Baseline items are defined in `002 shopping-requirements.md` Baseline Item Configuration Table.  
[X] Brand-specific names must be rejected.  
[X] Name validation outcomes must be clear to the user.

### 7) Persistence

[X] Shopping List content must be restored after app restart in its current state and sorted order.  
[X] Shopping List content must be restored after phone restart in its current state and sorted order.  
[X] Replenish List content must be restored after app restart in its current state and sorted order.  
[X] Replenish List content must be restored after phone restart in its current state and sorted order.

## Non-Functional Requirements

[X] List interactions must be responsive and stable.  
[X] Text and labels must be clear and concise.  
[X] User-facing text must be in Standard American English.  
[X] Presentation must align with the Jarvis / Iron Man visual standards as defined in `docs/style-guide/ux-style-guide.md`.

## Acceptance Criteria

Shopping List requirements are accepted when all are true:

[X] Add, edit, and remove operations reflect immediately in the Shopping List.  
[X] Swipe delete and swipe edit gestures work with correct visual direction/background semantics.  
[X] Edit screen supports name change, category change, and multi-barcode management.  
[X] Shopping List scan action opens the resolve screen for found and not-found outcomes.  
[X] Resolve screen supports both "link existing item" and "add new item" outcomes with the scanned barcode attached.  
[X] Not-found flow supports both "link existing item" and "add new item" outcomes.  
[X] Replenish List hide/show rules remain synchronized with Shopping List membership.  
[X] Sorting and grouping rules remain correct after all updates and restarts.  
[X] Canonical naming and duplicate rules remain consistent across all entry points.
