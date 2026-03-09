# Restock Requirements

## Checkbox Status Legend

- `[ ]` — Not started
- `[-]` — In progress
- `[X]` — Done (or any non-empty character except dash)

Use these checkboxes to track implementation status of each requirement.

## Objective

Define requirements for selecting items to add to the Shopping List using a simple and user-friendly selection flow.

## Scope

This document covers how users choose items from the Replenish List before adding them to the Shopping List.

## Functional Requirements

### Selection Experience

[X] User must be able to select one or more items from the Replenish List to add to the Shopping List.  
[X] Selection behavior must be clear, predictable, and reversible before confirmation.  
[X] Confirming selection must add chosen items to the Shopping List.

### Selection Presentation

[X] Baseline items must be displayed ordered by category, then by item name.  
[X] Baseline items must be grouped by category with category names displayed as header items.  
[X] Each baseline item must display its generated image with clear contrast.  
[X] Item images must semantically match their baseline item.

### Selection Outcomes

[X] Selected items must appear in the Shopping List after successful confirmation.  
[X] Duplicate-handling rules must apply when selected items are added.  
[X] Added items must follow the same naming and validation standards as manually entered items.

### Canonical Item Naming

[X] Selectable item names must use canonical generic names from the Baseline Item Configuration Table.  
[X] Baseline items are defined in `002 shopping-requirements.md` Baseline Item Configuration Table.  
[X] Brand-specific names must be rejected.  
[X] Naming guidance must clearly communicate accepted canonical names.

### Baseline Item Images

[X] Each baseline item must have a generated image.  
[X] Generated images must semantically match their baseline items.  
[X] Generated images must have clear contrast between the item and the background.

## Non-Functional Requirements

[X] Selection interactions must be stable and responsive in normal usage.  
[X] User-facing language must remain concise and in Standard American English.  
[X] Visual treatment must align with the Jarvis / Iron Man theme as defined in `docs/style-guide/ux-style-guide.md`.

## Acceptance Criteria

Restock requirements are accepted when all are true:

[X] User can select and confirm items to add from the Replenish List.  
[X] Items are grouped by category with category headers displayed.  
[X] Items within each category are displayed in alphabetical order by item name.  
[X] Each baseline item displays its generated image with good contrast.  
[X] Confirmed items are added correctly to the Shopping List.  
[X] Canonical naming rules are enforced and brand-specific names are rejected.  
[X] Duplicate handling is consistent with Shopping List behavior.  
[X] Selection flow remains stable in common user scenarios.
