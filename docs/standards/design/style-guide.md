# Jarvis Design Style Guide

## Overview

Jarvis uses an **"Iron Man / Arc Reactor" dark-tech aesthetic**: a near-black HUD
background, a monospace typeface throughout, and a small, disciplined accent
palette (gold, cyan, red) that maps to specific meanings rather than being used
decoratively. This guide documents that visual language as implemented today in
`app/src/main/res` and defines the rules for extending it consistently as new
screens and components are added.

This document is part of the [Standards and Guidelines](../readme.md) folder.
**AI assistants and developers MUST follow this guide** when creating or
modifying any UI (layouts, drawables, themes, styles) in the Jarvis app.

> Jarvis is built with **Android Views + Material 3 Components** (`Theme.Material3.DayNight.NoActionBar`),
> not Jetpack Compose. All guidance below is expressed in terms of XML resources
> (`themes.xml`, `colors.xml`, `dimens.xml`, `styles.xml`, drawables, layouts).

A read-only reference copy of every drawable asset (icons and backgrounds)
referenced throughout this guide is kept in [`images/`](images/), mirrored
from `app/src/main/res/drawable`. Jarvis has no raster (`.png`/`.jpg`) or SVG
art today — every visual asset is an Android vector-drawable XML file, so
that's what's mirrored here. **`app/src/main/res/drawable` is the source of
truth**; if you change an asset there, re-sync its copy in `images/` in the
same change so the two never drift.

---

## 1. Design Principles

1. **HUD, not paper.** Surfaces read as a heads-up display, not a Material
   "card on white paper." Backgrounds are dark, borders are thin glowing
   strokes, not drop shadows.
2. **Color means something.** Gold, cyan, and red are never chosen
   decoratively — each carries a fixed meaning (see [Color System](#2-color-system)).
   If a new color is needed, define its meaning before adding it.
3. **Monospace everywhere.** All UI text uses `fontFamily="monospace"` to
   reinforce the "terminal/HUD" feel. This is a deliberate, repo-wide choice —
   do not introduce the default sans-serif font for new UI text.
4. **Restraint over decoration.** The arc-reactor rings, glows, and gradients
   are reserved for full-screen backgrounds (`bg_jarvis.xml`). Individual
   components (buttons, list rows, dialogs) stay flat and simple so the
   background motif remains the visual centerpiece.
5. **Consistency via resources, never literals.** Every color, dimension, and
   text size used in a layout **must** reference `@color/…` or `@dimen/…` —
   never a hardcoded hex value or raw `dp`/`sp` literal. (One narrow exception
   exists today for translucent overlay fills — see [§6](#6-known-exceptions--technical-debt).)

---

## 2. Color System

### 2.1 Palette (`res/values/colors.xml`)

| Token | Hex | Role |
|---|---|---|
| `iron_man_dark_tech` (alias `dark_tech`) | `#020810` | Base background — the "HUD black." Used for `windowBackground`, splash screen, and text-on-accent (e.g. button label on a gold button). |
| `black` | `#FF000000` | Reserved for the splash screen background/icon only. Do not use for general surfaces — use `dark_tech` instead. |
| `iron_man_red` | `#7A0019` | **Primary** brand color. Status bar, primary theme color, destructive/remove actions, active bottom-nav indicator. |
| `iron_man_gold` | `#F1D56D` | **Secondary** brand color. Primary text emphasis (item names, titles), primary call-to-action button fill. |
| `iron_man_gold_dark` | `#CFB53B` | Muted gold for section/category headers — distinguishes structural text from interactive/content text. |
| `iron_man_cyan` | `#00E5FF` | Accent for secondary/metadata text, ripple color, inactive bottom-nav icon, outlined-button stroke. |

### 2.2 Semantic usage rules

- **Primary content text** (item names, titles) → `iron_man_gold`.
- **Secondary / metadata text** (counts, barcodes, helper text) → `iron_man_cyan`.
- **Section / category headers** → `iron_man_gold_dark`, bold, with `letterSpacing="0.05"`.
- **Destructive actions** (remove, delete) → `iron_man_red` (icon tint) — see `btnRemoveBarcode` in `list_item_barcode.xml`.
- **Primary call-to-action buttons** (e.g. "Add") → `android:backgroundTint="@color/iron_man_gold"` with `android:textColor="@color/iron_man_dark_tech"` (dark text on light-gold fill for contrast).
- **Outlined / ghost buttons** → transparent fill, `iron_man_cyan` stroke and ripple (see `bg_button.xml`).
- **Selected/active state** (bottom nav) → `iron_man_red`; **unselected/inactive state** → `iron_man_cyan`.
- Never use raw `#RRGGBB` literals in layouts/drawables for these roles — always reference the named color. The only accepted hex literals are short-lived **alpha overlays** layered on top of a named color for glow/scrim effects (see [§6](#6-known-exceptions--technical-debt)).

### 2.3 Light/Dark mode

`values-night/themes.xml` currently mirrors `values/themes.xml` exactly — Jarvis
is intentionally **dark-only**; there is no separate light theme to design for.
If a true light variant is ever introduced, it must be proposed and reviewed
before implementation, since the entire visual language (dark HUD, glowing
strokes) assumes a dark base.

---

## 3. Typography

| Token | Size | Usage |
|---|---|---|
| `text_size_title` | 24sp | Screen/activity titles |
| `text_size_category_header` | 18sp | Category/section headers (bold, `iron_man_gold_dark`) |
| `text_size_body` | 16sp | Primary row/body text (item names, form fields) |
| `text_size_tab` | 12sp | Tab labels |
| `text_size_small` | 12sp | Secondary/metadata text, small buttons |
| `TextAppearance.Jarvis.BottomNavItem` | 10sp | Bottom navigation labels only |

Rules:
- **Font family:** `monospace` on every `TextView`, `Button`, `EditText`, etc. that renders user-facing text. This is set per-view (`android:fontFamily="monospace"`) since there is no repo-wide default font defined in the theme yet — see [§6](#6-known-exceptions--technical-debt) for the recommended fix.
- Category headers additionally use `android:textStyle="bold"` and `android:letterSpacing="0.05"` to visually separate structural headers from list content.
- Never introduce a new text size as a literal `sp` value in a layout — add a named `@dimen` in `dimens.xml` first (see [§4](#4-spacing--sizing)).

---

## 4. Spacing & Sizing

Defined in `res/values/dimens.xml`:

| Token | Value | Usage |
|---|---|---|
| `spacing_small` | 8dp | Tight gaps (icon-to-text margins, barcode row padding) |
| `spacing_medium` | 16dp | Default padding/margin for rows, dialogs, sections — the default unit |
| `spacing_large` | 24dp | Generous separation between major sections |
| `icon_size_menu` | 48dp | Overflow/menu icon buttons |
| `tab_height` | 48dp | Tab bar height |
| `replenish_item_height` | 72dp | Replenish list row height |
| `replenish_item_image_size` | 48dp | Replenish list row image |
| `barcode_list_height` | 200dp | Barcode picker list in dialogs |
| `fab_bottom_padding` | 80dp | Bottom padding to clear a FAB/bottom nav |

Rules:
- **`spacing_medium` (16dp) is the default padding** for any new screen, dialog,
  or list-row container unless there's a specific reason to deviate.
- **Never hardcode a `dp`/`sp` value in a layout.** If an existing token doesn't
  fit, add a new named `@dimen` (following the existing `snake_case` naming:
  `<purpose>_<qualifier>`, e.g. `replenish_item_height`) rather than inlining
  a literal.
- Small icon buttons that don't have a named `@dimen` yet (e.g. the `40dp`
  remove-barcode button) should be migrated to a shared token — see
  [§6](#6-known-exceptions--technical-debt).

---

## 5. Shape, Elevation & Components

### 5.1 Corner radius
- **16dp** — standalone interactive surfaces (outlined buttons, `bg_button.xml`).
- **12dp** — Material component shape overrides, e.g. `ShapeAppearance.Jarvis.SmallComponent` used for the bottom-nav active indicator.
- Use one of these two values for new rounded surfaces; don't introduce a third radius without reason.

### 5.2 Elevation & depth
- Jarvis does **not** use Material drop-shadow elevation for depth. Depth and
  emphasis come from **stroke + glow**, not shadow:
  - A translucent dark fill (`#40001015`) + a **1dp colored stroke**
    (`iron_man_cyan`) + a matching ripple color, as in `bg_button.xml`.
- When designing a new elevated/interactive surface, follow this
  fill-plus-stroke-plus-ripple pattern rather than adding `android:elevation`
  or a `CardView` shadow.

### 5.3 Buttons
- **Primary (filled) button:** `backgroundTint="@color/iron_man_gold"`,
  `textColor="@color/iron_man_dark_tech"`, monospace, `text_size_small` for
  compact inline buttons (e.g. "Add").
- **Outlined/ghost button:** `bg_button.xml` pattern — cyan stroke + cyan
  ripple + translucent dark fill, no `backgroundTint`.
- **Icon-only button:** `ImageButton` with `background="?attr/selectableItemBackgroundBorderless"`, tinted per semantic role (gold for neutral menu actions, red for destructive, cyan for secondary/informational).

### 5.4 List rows
- Horizontal `LinearLayout`, `padding="@dimen/spacing_medium"` (or
  `spacing_small` for denser rows like barcodes), `gravity="center_vertical"`.
- Primary label: gold, `text_size_body`, monospace.
- Secondary label (counts, codes): cyan, `text_size_small`, monospace,
  `visibility="gone"` by default when the data is optional/empty.
- Trailing action (menu/remove): `ImageButton`, borderless ripple.

### 5.5 Section/category headers
- Full-width `TextView`, asymmetric vertical padding (`20dp` top / `4dp`
  bottom) to create clear separation from the previous group while hugging
  the items that follow — see `list_item_category_header.xml`.
- `iron_man_gold_dark`, bold, `text_size_category_header`, `letterSpacing="0.05"`.

### 5.6 Dialogs & forms
- Vertical `LinearLayout`, `padding="@dimen/spacing_medium"`.
- Text input uses Material `TextInputLayout` / `TextInputEditText`
  (`style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"`),
  not raw `EditText`, so fields get consistent Material outline/hint behavior.
- Stack fields vertically with `layout_marginTop="@dimen/spacing_medium"`
  between them.

### 5.7 Bottom navigation
- `Widget.Jarvis.BottomNavigationView` (extends `Widget.Material3.BottomNavigationView`):
  36dp icons, no minimum height constraint, top-weighted padding (no label
  crowding the icon), 10sp label text.
- Active indicator: `Widget.Jarvis.BottomNavigationView.ActiveIndicator`,
  colored `iron_man_red`, 12dp corner radius via `ShapeAppearance.Jarvis.SmallComponent`.
- Item color state: `bottom_nav_colors.xml` — checked = red, default = cyan.

### 5.8 Backgrounds
- Full-screen chrome uses `bg_jarvis.xml`: a flat `#020810` base, a soft
  radial cyan glow, and three concentric "arc reactor" rings (two solid, one
  dashed) centered on the screen. This is the **only** place gradients/rings
  belong — don't replicate the reactor-ring motif inside individual
  components; it's a full-screen background signature, not a decorative
  widget.

### 5.9 Iconography
- Item/category icons are simple flat vector drawables named `ic_item_<name>.xml`
  (snake_case, matching the item's canonical name).
- `ic_item_placeholder.xml` is the fallback for items without a dedicated icon —
  always wire new item types through this fallback rather than leaving an
  `ImageView` empty when no icon exists yet.
- Follow existing drawable naming prefixes from
  [`kotlin-android-best-practices.md`](../kotlin-android-best-practices.md):
  `ic_` for icons, `bg_` for backgrounds, `img_` for images.

---

## 6. Known Exceptions & Technical Debt

These are documented deliberately so they're treated as *known, tracked
deviations* rather than rediscovered and copied as "the pattern":

- **Per-view `fontFamily="monospace"`**: repeated on every text-bearing view
  instead of being set once. Prefer introducing a shared
  `TextAppearance.Jarvis.Base` (or setting `android:fontFamily` on the app
  theme) next time typography is touched, so new screens don't need to repeat it.
- **Translucent hex literals** (`#40001015`, `#1A00E5FF`, `#4000E5FF`, etc. in
  `bg_button.xml` / `bg_jarvis.xml`): these are alpha-blended variants of named
  colors with no `@color` token today. Acceptable for now since they're
  glow/scrim effects rather than semantic UI colors, but if reused in a third
  place, promote them to named `@color` resources (e.g. `cyan_glow_20`).
- **Ungrouped icon-button sizes** (e.g. the `40dp` remove-barcode button vs.
  the `48dp` `icon_size_menu` token): new icon buttons should reuse
  `icon_size_menu` unless there's a documented reason for a different size;
  don't introduce a third inline literal.

Do not "fix" these opportunistically as a side effect of unrelated changes —
address them intentionally, in a change scoped to that purpose.

---

## 7. Checklist for New UI

Before submitting any new layout, drawable, or style resource, confirm:

- [ ] No hardcoded color hex values — all colors reference `@color/…` from `colors.xml`, and the choice matches the semantic table in [§2.2](#22-semantic-usage-rules).
- [ ] No hardcoded `dp`/`sp` literals — all use existing `@dimen/…` tokens, or a new token was added following the existing naming convention.
- [ ] All user-facing text uses `android:fontFamily="monospace"`.
- [ ] New rounded surfaces use `16dp` (standalone) or `12dp` (Material shape override) corner radius.
- [ ] Depth/emphasis comes from stroke + ripple + translucent fill, not `elevation`/shadow.
- [ ] Resource file names follow the prefixes and casing rules in [`kotlin-android-best-practices.md`](../kotlin-android-best-practices.md) (`ic_`, `bg_`, `fragment_`, `dialog_`, etc.) and this repo's [lowercase-hyphenated `.md` filename rule](../readme.md#markdown-file-naming-convention-non-negotiable) for any accompanying docs.
- [ ] Any new full-screen background reuses `bg_jarvis.xml` rather than inventing a second motif.
