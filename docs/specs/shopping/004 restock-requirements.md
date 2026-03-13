# Restock Requirements

## Checkbox Status Legend

- `[ ]` — Not started
- `[-]` — In progress
- `[X]` — Done (or any non-empty character except dash)

Use these checkboxes to track implementation status of each requirement.

## Objective

Define requirements for selecting baseline items from Replenish and moving them into Shopping quickly and predictably.

## Scope

This document covers Replenish-based add flows only:

- Tap/double-tap add behavior
- Replenish list grouping/sorting
- Replenish visibility sync with Shopping List
- Replenish baseline imagery and naming constraints

## Implementation Status Snapshot

| Area | Requirement | Status |
|---|---|---|
| Add Flow | Add from Replenish to Shopping | [X] |
| Add Flow | Double-tap quick add | [X] |
| Visibility Sync | Hide if already in Shopping List | [X] |
| Visibility Sync | Re-show when removed from Shopping List | [X] |
| Presentation | Group by category and sort by item name | [X] |
| Presentation | Baseline image per item with semantic match | [X] |

## Functional Requirements

### 1) Selection Experience

[X] User must be able to select one or more items from the Replenish List to add to the Shopping List.  
[X] User can double-tap a Replenish List item to quickly add it to the Shopping List.  
[X] Selection behavior must be clear, predictable, and reversible before confirmation.  
[X] Confirming selection must add chosen items to the Shopping List.

### 2) Replenish Visibility Rules

[X] Replenish List must hide items that are already in the Shopping List.  
[X] Replenish List must re-display items when they are removed from the Shopping List.

### 3) Selection Presentation

[X] Baseline items must be displayed ordered by category, then by item name.  
[X] Baseline items must be grouped by category with category names displayed as header items.  
[X] Each baseline item must display its generated image with clear contrast.  
[X] Item images must semantically match their baseline item.

### 4) Selection Outcomes

[X] Selected items must appear in the Shopping List after successful confirmation.  
[X] Duplicate-handling rules must apply when selected items are added.  
[X] Added items must follow the same naming and validation standards as manually entered items.

### 5) Canonical Item Naming

[X] Selectable item names must use canonical generic names from the Baseline Item Configuration Table.  
[X] Baseline items are defined in `002 shopping-requirements.md` Baseline Item Configuration Table.  
[X] Brand-specific names must be rejected.  
[X] Naming guidance must clearly communicate accepted canonical names.

## Non-Functional Requirements

[X] Selection interactions must be stable and responsive in normal usage.  
[X] User-facing language must remain concise and in Standard American English.  
[X] Visual treatment must align with the Jarvis / Iron Man theme as defined in `docs/style-guide/ux-style-guide.md`.

## Acceptance Criteria

Restock requirements are accepted when all are true:

[X] User can add baseline items from Replenish to Shopping using standard and quick (double-tap) flows.  
[X] Replenish visibility updates immediately based on Shopping List membership.  
[X] Grouping/sorting remains correct for all visible Replenish items.  
[X] Baseline images remain semantically accurate and readable.  
[X] Naming and duplicate rules remain consistent with Shopping List behavior.
