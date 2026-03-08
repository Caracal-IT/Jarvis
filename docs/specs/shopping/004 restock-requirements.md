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

[ ] User must be able to select one or more items from the Replenish List to add to the Shopping List.  
[ ] Selection behavior must be clear, predictable, and reversible before confirmation.  
[ ] Confirming selection must add chosen items to the Shopping List.

### Selection Presentation

[ ] Baseline items must be displayed ordered by category, then by item name.  
[ ] Each baseline item must display its generated image with clear contrast.  
[ ] Item images must semantically match their baseline item.

### Selection Outcomes

[ ] Selected items must appear in the Shopping List after successful confirmation.  
[ ] Duplicate-handling rules must apply when selected items are added.  
[ ] Added items must follow the same naming and validation standards as manually entered items.

### Canonical Item Naming

[ ] Selectable item names must use canonical generic names from the Baseline Item Configuration Table.  
[ ] Baseline items are defined in `002 shopping-requirements.md` Baseline Item Configuration Table.  
[ ] Brand-specific names must be rejected.  
[ ] Naming guidance must clearly communicate accepted canonical names.

### Baseline Item Images

[ ] Each baseline item must have a generated image.  
[ ] Generated images must semantically match their baseline items.  
[ ] Generated images must have clear contrast between the item and the background.

## Non-Functional Requirements

[ ] Selection interactions must be stable and responsive in normal usage.  
[ ] User-facing language must remain concise and in Standard American English.  
[ ] Visual treatment must align with the Jarvis / Iron Man theme.

## Acceptance Criteria

Restock requirements are accepted when all are true:

[ ] User can select and confirm items to add from the Replenish List.  
[ ] Items are displayed ordered by category, then by item name.  
[ ] Each baseline item displays its generated image with good contrast.  
[ ] Confirmed items are added correctly to the Shopping List.  
[ ] Canonical naming rules are enforced and brand-specific names are rejected.  
[ ] Duplicate handling is consistent with Shopping List behavior.  
[ ] Selection flow remains stable in common user scenarios.
