# Shopping Requirements Index

This folder contains high-level, end-state requirements for the Shopping feature.

## Purpose

Define what Shopping must deliver when the feature is complete.

## Active Files

- `001 readme.md`
- `002 shopping-requirements.md`
- `003 shopping-list-requirements.md`
- `004 restock-requirements.md`

## Quick Map

| File | Primary Focus |
|---|---|
| `002 shopping-requirements.md` | End-to-end Shopping capability (add/edit/remove, barcodes, scan flow, persistence) |
| `003 shopping-list-requirements.md` | Shopping List UX/behavior details (swipes, edit screen, barcode scan resolution, replenish sync) |
| `004 restock-requirements.md` | Replenish-based add flows and visibility rules |

## Boundaries

1. This folder covers Shopping requirements only.
2. Home-level requirements remain in `docs/specs/002 home-requirements.md`.
3. Standards and engineering rules remain in `docs/standards/`.
4. Visual and interaction styling remains in `docs/style-guide/ux-style-guide.md`.

## Maintenance Rule

When Shopping behavior or scope changes, update:

1. `002 shopping-requirements.md`
2. `003 shopping-list-requirements.md`
3. `004 restock-requirements.md`
4. Acceptance criteria and scope statements in this folder

## Traceability Appendix

Use this quick map to trace requirement areas to current implementation points.

| Requirement Area | Primary Implementation Points |
|---|---|
| Shopping list grouping/sorting | `app/src/main/java/com/github/caracal/jarvis/shopping/data/SharedPrefsShoppingRepository.kt` (`itemComparator`, `getShoppingList`) |
| Replenish grouping/sorting and visibility sync | `app/src/main/java/com/github/caracal/jarvis/shopping/data/SharedPrefsShoppingRepository.kt` (`getReplenishList`) |
| Shopping list rendering + swipes | `app/src/main/java/com/github/caracal/jarvis/shopping/list/ShoppingListFragment.kt`, `app/src/main/java/com/github/caracal/jarvis/shopping/list/ShoppingListSwipeCallback.kt` |
| Replenish add flows (tap/double-tap) | `app/src/main/java/com/github/caracal/jarvis/shopping/replenish/ReplenishListFragment.kt`, `app/src/main/java/com/github/caracal/jarvis/shopping/replenish/DoubleTapItemTouchListener.kt` |
| Add item flow (name + category) | `app/src/main/java/com/github/caracal/jarvis/shopping/list/AddItemDialogFragment.kt` |
| Edit flow (name + category + barcodes) | `app/src/main/java/com/github/caracal/jarvis/shopping/list/EditItemDialogFragment.kt`, `app/src/main/java/com/github/caracal/jarvis/shopping/list/BarcodeListAdapter.kt` |
| Scan flow and barcode resolution | `app/src/main/java/com/github/caracal/jarvis/shopping/list/BarcodeScannerFragment.kt`, `app/src/main/java/com/github/caracal/jarvis/shopping/list/BarcodeResultFragment.kt` |
| Shopping state and orchestration | `app/src/main/java/com/github/caracal/jarvis/shopping/ui/ShoppingViewModel.kt` |
| Baseline categories/items and IDs | `app/src/main/java/com/github/caracal/jarvis/shopping/data/BaselineData.kt` |
| Baseline item images mapping | `app/src/main/java/com/github/caracal/jarvis/shopping/replenish/BaselineImageMapper.kt` |

Note: This appendix is for traceability only and does not replace the requirement details in `002 shopping-requirements.md`, `003 shopping-list-requirements.md`, or `004 restock-requirements.md`.
