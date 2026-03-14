# Jarvis UX Style Guide

**Version:** 1.1  
**Last Updated:** March 10, 2026  
**Status:** ACTIVE

---

## Purpose

This document defines the complete visual and interaction design system for the Jarvis application. All UI implementation must follow these guidelines to maintain a consistent, high-tech, Iron Man-inspired user experience.

For high-level feature requirements, see `docs/specs/`. This document contains only UX-specific implementation details.

---

## Theme Identity

**Jarvis** is inspired by the Iron Man universe — a futuristic, high-tech AI assistant interface with a sleek, minimalist aesthetic.

### Core Design Principles

1. **High-Tech Minimalism** — Clean, uncluttered interfaces with purposeful geometry
2. **Arc Reactor Energy** — Red and gold accents suggesting power and sophistication
3. **Holographic Precision** — Cyan highlights for secondary information and inactive states
4. **Dark Command Center** — Deep dark backgrounds for reduced eye strain and focus
5. **Frosted Glass Layers** — Semi-transparent surfaces with blur effects for depth and sophistication
6. **Responsive Clarity** — Every interaction provides immediate, clear feedback
7. **Mobile-First Controls** — Use touch-optimized controls only. Avoid standard desktop-style radio buttons; use toggle buttons or switches instead.

---

## Color Palette

### Primary Colors

| Color Name             | Hex Value | Resource Name        | Usage                                           |
|------------------------|-----------|----------------------|-------------------------------------------------|
| Iron Man Red           | `#7A0019` | `iron_man_red`       | Primary actions, active states, primary buttons |
| Iron Man Gold (Dark)   | `#F1D56D` | `iron_man_gold`      | Accent highlights, active text, active icons    |
| Iron Man Gold (Darker) | `#CFB53B` | `iron_man_gold_dark` | Pressed states, darker gold accents             |
| Cyan                   | `#00E5FF` | `iron_man_cyan`      | Secondary text, inactive states, info text      |
| Dark Tech              | `#020810` | `iron_man_dark_tech` | Primary background, dark surfaces               |
| Black                  | `#000000` | `black`              | Deepest background, window background           |

### Color Usage Guidelines

#### Backgrounds
- **Screen/Fragment background:** `@drawable/bg_jarvis` — Layered background with radial gradient and arc reactor ring elements
- **Window background:** `black` (`#000000`)
- **List item cards:** Semi-transparent with lighter bluish tint (40% opacity: `#66101820`), NO blur effect, NO rounded corners
- **Dialog backgrounds:** Lighter bluish-white tint (94% opacity: `#F01A2A3A`) with frosted glass effect (blur + rounded corners)
- **Dialog frosted glass effect:** Semi-transparent whiter background with 12dp rounded corners, strong cyan border (50% opacity)

#### Text
- **Primary text:** `iron_man_cyan` (`#00E5FF`) — Headlines, primary labels, active item names
- **Secondary text:** `iron_man_gold` (`#F1D56D`) — Descriptions, hints, inactive labels
- **Category headers:** `iron_man_gold` (`#F1D56D`) — Bold, prominent
- **Empty state text:** `iron_man_cyan` (`#00E5FF`) — Informative but not distracting

#### Interactive Elements
- **Primary buttons (default):** `iron_man_red` (`#7A0019`) background with `iron_man_gold` text
- **Primary buttons (pressed):** Darker red with `iron_man_gold` text
- **FAB (Floating Action Button):** `iron_man_red` background with white or gold icon
- **Icons (active):** `iron_man_gold` (`#F1D56D`)
- **Icons (inactive):** `iron_man_cyan` (`#00E5FF`)
- **Selection indicators:** `iron_man_red` (`#7A0019`)
- **Dividers:** `iron_man_cyan` at 20% opacity (`#3300E5FF`)

#### Status Indicators
- **Active / Selected:** `iron_man_red` (`#7A0019`)
- **Hover / Focus:** `iron_man_gold` (`#F1D56D`)
- **Inactive / Disabled:** `iron_man_cyan` at 40% opacity (`#6600E5FF`)
- **Error states:** `iron_man_red` with increased brightness for visibility

### Color Contrast Requirements

- **Minimum contrast ratio:** 4.5:1 for normal text, 3:1 for large text (WCAG AA compliance)
- **Image backgrounds:** All baseline item images must have clear contrast between the item subject and the background
- **Text on backgrounds:** Always test text legibility on `iron_man_dark_tech` and `black`

---

## Screen Backgrounds

All screen and fragment backgrounds must use the **Jarvis background pattern** (`@drawable/bg_jarvis`), which creates the signature holographic command center aesthetic.

### bg_jarvis Composition

The Jarvis background is a layered drawable consisting of:

1. **Base layer:** Solid `iron_man_dark_tech` (`#020810`)
2. **Radial gradient glow:** Subtle cyan radial gradient from center (10-25% opacity `iron_man_cyan`)
3. **Arc Reactor rings:** Concentric circular outlines in cyan at various opacities
   - Large outer ring (300dp, 20% opacity)
   - Medium dashed ring (160dp, 60% opacity, dashed)
   - Small inner ring (200dp, 40% opacity)

### Usage Requirements

- **All Activity layouts:** Must use `android:background="@drawable/bg_jarvis"`
- **All Fragment layouts:** Must use `android:background="@drawable/bg_jarvis"`
- **Exception:** Fragments inside ViewPagers or nested containers may use transparent backgrounds to inherit parent background

### Example

```xml
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/bg_jarvis">
    
    <!-- Screen content here -->
    
</FrameLayout>
```

---

## Frosted Glass Effect

The Jarvis interface uses a **frosted glass** aesthetic **only for dialogs and modal overlays** to create a modern, high-tech look reminiscent of holographic displays.

**List items and cards DO NOT use frosted glass** — they use simple semi-transparent backgrounds with square corners.

### Frosted Glass Specifications (Dialogs Only)

| Property          | Value                                      | Usage                                    |
|-------------------|--------------------------------------------|------------------------------------------|
| Background color  | Lighter bluish-white tint at 94% opacity (`#F01A2A3A`) | Semi-transparent whiter surface |
| Blur radius       | 24dp                                       | Blur effect for depth                    |
| Border            | 1dp, `iron_man_cyan` at 50% opacity (`#8000E5FF`) | Strong visible cyan border       |
| Corner radius     | 12dp (rounded corners for dialogs)         | Softer appearance for modal surfaces     |
| Elevation         | 8dp                                        | Shadow depth on dark backgrounds         |

### Implementation Guidance

#### Android Implementation

Android does not natively support real-time blur effects efficiently. Use these alternatives:

**Option 1: Semi-transparent Background (Recommended)**
```xml
<!-- Dialog or Card Background -->
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#E6020810" />  <!-- 90% opacity iron_man_dark_tech -->
    <corners android:radius="12dp" />
    <stroke 
        android:width="1dp" 
        android:color="#4D00E5FF" />  <!-- 30% opacity iron_man_cyan -->
</shape>
```

**Option 2: Pre-blurred Background Image**
- Capture or generate a blurred version of the background
- Apply as drawable behind semi-transparent foreground

**Option 3: RenderScript Blur (API < 31) or RenderEffect (API 31+)**
- Use `android.graphics.RenderEffect.createBlurEffect()` for API 31+
- Use RenderScript intrinsic blur for older devices (with deprecation awareness)

### Frosted Glass Usage

| Component              | Uses Frosted Glass | Background Color      | Border Opacity | Corner Radius |
|------------------------|--------------------|-----------------------|----------------|---------------|
| Dialog                 | ✅ Yes             | `#F01A2A3A` (whiter)  | 50%            | 12dp          |
| Bottom Sheet           | ✅ Yes             | `#F01A2A3A` (whiter)  | 50%            | 12dp          |
| Popup Menu             | ✅ Yes             | `#D9101820` (lighter) | 50%            | 4dp           |
| List Items             | ❌ No              | `#66101820` (lighter) | 50%            | 0dp           |
| Cards                  | ❌ No              | `#66101820` (lighter) | 50%            | 0dp           |
| Overlay                | ✅ Yes             | `#B3101820` (lighter) | 50%            | 0dp           |

### Visual Hierarchy

- **Higher elevation = More prominent frosted glass effect** (higher opacity, stronger blur)
- **Lower elevation = Subtler frosted glass effect** (lower opacity, gentler blur)

---

## Typography

### Font Family

**Default:** System default (Roboto on Android)  
**Style:** Clean, geometric, modern sans-serif

### Text Sizes

| Style            | Size (sp) | Resource Name               | Usage                                     |
|------------------|-----------|-----------------------------|-------------------------------------------|
| Page Title       | 24        | `text_size_title`           | Fragment titles, main headings            |
| Category Header  | 18        | `text_size_category_header` | Category group headers in lists           |
| Body / Item Name | 16        | `text_size_body`            | List item names, body text, button labels |
| Tab Label        | 12        | `text_size_tab`             | Tab bar labels                            |
| Small / Caption  | 12        | `text_size_small`           | Hints, captions, metadata                 |

### Text Styling

- **Titles:** Bold, `iron_man_gold`, centered or left-aligned
- **Category headers:** Bold, `iron_man_gold`, left-aligned, uppercase optional
- **Item names:** Regular weight, `iron_man_gold`, left-aligned
- **Secondary labels:** Regular weight, `iron_man_cyan`, left-aligned
- **Empty state messages:** Regular weight, `iron_man_cyan`, centered
- **Button labels:** Medium or bold weight, `iron_man_gold`, uppercase optional

### Line Height and Letter Spacing

- **Standard line height:** 1.5x font size
- **Tight line height:** 1.2x font size (for compact lists)
- **Letter spacing:** Default (0) or slight increase (+0.5sp) for uppercase labels

---

## Spacing and Layout

### Spacing Scale

| Name        | Size (dp) | Resource Name        | Usage                                             |
|-------------|-----------|----------------------|---------------------------------------------------|
| Small       | 8         | `spacing_small`      | Tight internal padding, chip gaps                 |
| Medium      | 16        | `spacing_medium`     | Standard padding, list item padding, card padding |
| Large       | 24        | `spacing_large`      | Section separation, top/bottom screen margins     |
| FAB Padding | 80        | `fab_bottom_padding` | Bottom padding to prevent FAB overlap             |

### Component Sizing

| Component                   | Size (dp) | Resource Name                  | Usage                             |
|-----------------------------|-----------|--------------------------------|-----------------------------------|
| Tab Bar Height              | 48        | `tab_height`                   | Fixed tab selector height         |
| Menu Icon Size              | 48        | `icon_size_menu`               | Three-dot menu icons              |
| Replenish Item Height       | 84        | `replenish_item_height`        | Fixed height for replenish rows   |
| Replenish Item Image        | 52        | `replenish_item_image_size`    | Baseline item image thumbnail     |
| Barcode List Height         | 200       | `barcode_list_height`          | Fixed barcode dialog list height  |

### Layout Patterns

#### List Items
- **Layout style:** Card-based with spacing between items (not edge-to-edge)
- **Item margin:** `spacing_small` (8dp) on all sides for spacing between items
- **Corner radius:** 0dp (square corners, not rounded)
- **Background:** Semi-transparent with lighter bluish tint (40% opacity: `#66101820`)
- **NO blur effect** — Simple semi-transparent background only
- **Border:** 1dp, `iron_man_cyan` at 50% opacity (stronger visible cyan border: `#8000E5FF`)
- **Padding:** `spacing_medium` (16dp) horizontal, `spacing_small` (8dp) vertical (internal content padding)
- **Minimum height:** 48dp for touch targets
- **Elevation:** 0dp (flat appearance)
- **Dividers:** None (spacing and borders provide visual separation)

#### Category Headers
- **Padding:** `spacing_medium` (16dp) horizontal and vertical
- **Background:** Slightly lighter than `iron_man_dark_tech` (optional subtle differentiation)
- **Text:** Bold, uppercase optional, `iron_man_gold`

#### Cards
- **Corner radius:** 0dp (square corners, not rounded)
- **Background:** Semi-transparent with lighter bluish tint (40% opacity: `#66101820`)
- **NO blur effect** — Simple semi-transparent background only
- **Border:** 1dp, `iron_man_cyan` at 50% opacity (stronger visible cyan border: `#8000E5FF`)
- **Elevation:** 0dp (flat appearance)
- **Padding:** `spacing_medium` (16dp) on all sides

#### Dialogs
- **Corner radius:** 12dp
- **Padding:** `spacing_large` (24dp) on all sides
- **Background:** `iron_man_dark_tech` at 90% opacity with frosted glass effect
- **Border:** 1dp, `iron_man_cyan` at 30% opacity for enhanced visibility
- **Blur radius:** 24dp for frosted glass effect
- **Button spacing:** `spacing_small` (8dp) between buttons

#### Floating Action Button (FAB)
- **Size:** 56dp (standard Material 3)
- **Position:** Bottom-right, `spacing_medium` from edges
- **Background:** `iron_man_red`
- **Icon color:** `iron_man_gold` or white
- **Elevation:** 6dp

---

## Component Specifications

### List Components

#### Shopping List Item
- **Layout:** Swipeable with foreground content and background action buttons
- **Foreground content:** Item image + item name + optional barcode count (no action buttons)
- **Background actions:** Revealed on swipe (edit on left; delete on right)
- **Container style:** Card with semi-transparent background
- **Item margin:** `spacing_small` (8dp) on all sides (space around each item)
- **Corner radius:** 0dp (square corners, not rounded)
- **Foreground background:** Semi-transparent with lighter bluish tint (40% opacity: `#66101820`)
- **NO blur effect** — Simple semi-transparent background only
- **Border:** 1dp, `iron_man_cyan` at 50% opacity (stronger visible cyan border: `#8000E5FF`)
- **Text color:** `iron_man_gold` (item name)
- **Secondary text color:** `iron_man_cyan` (barcode count)
- **Height:** Minimum 48dp, wrap content
- **Padding:** 16dp horizontal, 8dp vertical (internal content padding)
- **Elevation:** 0dp (flat appearance)
- **Interaction:** Swipe to reveal action buttons (no menu icon on item)
- **Action buttons:** 80dp width each, gold icons on colored backgrounds
- **Item image container:** 48dp circular container with visible cyan border
- **Item image style:** Full-color semantic image (no monochrome tint)
- **No dividers** (spacing and borders provide separation)

#### Replenish List Item
- **Layout:** Horizontal, image/icon on left (48dp), text in center, add button/indicator on right
- **Container style:** Card with semi-transparent background
- **Item margin:** `spacing_small` (8dp) on all sides (space around each item)
- **Corner radius:** 0dp (square corners, not rounded)
- **Background:** Semi-transparent with lighter bluish tint (40% opacity: `#66101820`)
- **NO blur effect** — Simple semi-transparent background only
- **Border:** 1dp, `iron_man_cyan` at 50% opacity (stronger visible cyan border: `#8000E5FF`)
- **Height:** 84dp fixed
- **Padding:** 16dp horizontal, 8dp vertical (internal content padding)
- **Elevation:** 0dp (flat appearance)
- **Text color:** `iron_man_gold`
- **Image/Icon container:** 48dp circular container with visible cyan border
- **Image/Icon background:** Transparent background (no 3D/glossy treatment)
- **Image/Icon padding:** 4-6dp inside circle to prevent cutoff
- **Image/Icon style:** Full-color semantic image (no monochrome cyan-only icons)
- **Image requirements:** Clear contrast between item and background, semantically correct
- **Interaction:** Tap entire row to add to Shopping List
- **No dividers** (spacing and borders provide separation)

#### Category Header
- **Layout:** Full-width header row
- **Text:** Bold, `iron_man_gold`, 18sp, uppercase optional
- **Background:** `iron_man_dark_tech` or slightly lighter variant
- **Height:** Wrap content, minimum 48dp
- **Padding:** 16dp horizontal and vertical
- **Divider:** None (visual separation through text styling)

#### Empty State
- **Layout:** Centered vertical layout
- **Icon:** Optional large icon (64dp+), `iron_man_cyan`
- **Text:** `iron_man_cyan`, centered, 16sp
- **Message:** Clear, friendly explanation ("No items yet. Tap + to add.")
- **Padding:** `spacing_large` (24dp) on all sides

### Input Components

#### Text Input Field
- **Border:** 1dp, `iron_man_cyan` (inactive), `iron_man_gold` (focused)
- **Text color:** `iron_man_gold`
- **Hint color:** `iron_man_cyan` at 60% opacity
- **Background:** Transparent or very subtle `iron_man_dark_tech`
- **Padding:** 12dp horizontal, 8dp vertical
- **Corner radius:** 4dp

#### Dropdown / Spinner (Category Selector)
- **Border:** 1dp, `iron_man_cyan` (inactive), `iron_man_gold` (focused)
- **Text color:** `iron_man_gold`
- **Background:** `iron_man_dark_tech`
- **Dropdown list background:** `iron_man_dark_tech`
- **Dropdown item text:** `iron_man_gold` (unselected), `iron_man_red` (selected)
- **Padding:** 12dp horizontal, 8dp vertical
- **Corner radius:** 4dp

#### Toggle Button / Switch (Mobile Friendly Selection)
- **Requirement:** Replace standard radio buttons with toggle buttons or switches.
- **Toggle Group:** Use `MaterialButtonToggleGroup` for mutually exclusive selections.
- **Switch:** Use `SwitchMaterial` for simple on/off states.
- **Color (Active):** `iron_man_red` background with `iron_man_gold` text/thumb.
- **Color (Inactive):** `iron_man_dark_tech` background with `iron_man_cyan` text/thumb.
- **Corner Radius:** 8dp.

#### Buttons

##### Primary Button
- **Background:** `iron_man_red` (`#7A0019`)
- **Text color:** `iron_man_gold` (`#F1D56D`)
- **Text size:** 16sp
- **Text style:** Medium weight or bold, uppercase optional
- **Corner radius:** 8dp
- **Padding:** 16dp horizontal, 12dp vertical
- **Minimum width:** 88dp
- **Elevation:** 2dp

##### Secondary Button
- **Background:** Transparent or `iron_man_dark_tech`
- **Border:** 1dp, `iron_man_cyan`
- **Text color:** `iron_man_cyan`
- **Text size:** 16sp
- **Corner radius:** 8dp
- **Padding:** 16dp horizontal, 12dp vertical

##### Text Button (Tertiary)
- **Background:** Transparent
- **Text color:** `iron_man_cyan`
- **Text size:** 16sp
- **Padding:** 8dp horizontal, 8dp vertical
- **No border**

### Navigation Components

#### Tab Bar
- **Height:** 48dp
- **Background:** `iron_man_dark_tech`
- **Tab label (inactive):** `iron_man_cyan`, 12sp
- **Tab label (active):** `iron_man_gold`, 12sp, bold optional
- **Tab indicator:** `iron_man_red`, 3dp height, full tab width
- **Divider below tabs:** 1dp, `iron_man_cyan` at 20% opacity

#### Navigation Bar / Toolbar
- **Height:** 56dp (standard Material)
- **Background:** `iron_man_dark_tech`
- **Title text:** `iron_man_gold`, 24sp, bold
- **Icon color:** `iron_man_gold`
- **Elevation:** 4dp

### Dialogs and Popups

#### Dialog
- **Background:** Lighter bluish-white tint at 94% opacity with frosted glass effect (24dp blur) (`#F01A2A3A`)
- **Blur radius:** 24dp
- **Border:** 1dp, `iron_man_cyan` at 50% opacity (strong visible border: `#8000E5FF`)
- **Corner radius:** 12dp
- **Padding:** 24dp on all sides
- **Title:** Bold, `iron_man_gold`, 18sp
- **Body text:** Regular, `iron_man_cyan`, 16sp
- **Buttons:** Standard button styles (see above)
- **Button layout:** Horizontal at bottom-right, 8dp spacing between buttons
- **Elevation:** 8dp

#### Popup Menu
- **Background:** Lighter bluish tint at 85% opacity with frosted glass effect (`#D9101820`)
- **Blur radius:** 20dp
- **Border:** 1dp, `iron_man_cyan` at 50% opacity (strong visible border: `#8000E5FF`)
- **Corner radius:** 4dp
- **Item padding:** 16dp horizontal, 12dp vertical
- **Item text:** `iron_man_gold`, 16sp
- **Item hover:** `iron_man_red` background at 20% opacity
- **Divider:** 1dp, `iron_man_cyan` at 20% opacity
- **Elevation:** 8dp

#### Toast / Snackbar
- **Background:** `iron_man_dark_tech` with subtle border
- **Border:** 1dp, `iron_man_cyan`
- **Text color:** `iron_man_gold`
- **Text size:** 14sp
- **Corner radius:** 8dp
- **Padding:** 16dp horizontal, 12dp vertical
- **Duration:** Short (2 seconds) or Long (3.5 seconds)

---

## Icons and Graphics

### Icon Style
- **UI icons (navigation, actions):** Outlined style, 2dp stroke, `iron_man_gold` (active) or `iron_man_cyan` (inactive)
- **Item images in lists:** Full-color, clipart-style illustrations — colorful, semantic, and recognizable at small sizes
- **Item image rendering:** No tint applied — images render in their natural colors
- **Size (standard UI icon):** 24dp
- **Size (large/menu icon):** 48dp
- **Format:** Vector drawable (XML) preferred

### Baseline Item Images
- **Size:** 48dp square (in Replenish and Shopping List rows)
- **Format:** PNG or vector drawable
- **Container:** Circular container with 1dp cyan border
- **Image background:** Transparent image over bordered container
- **Image style:** Full-color semantic icon/image matching the item
- **Contrast requirement:** Clear visual separation between item subject and button/container background
- **Semantic accuracy:** Image content must match the item (e.g., beef -> cow, chicken -> chicken, cheese -> cheese)
- **Consistency:** All baseline item images must follow the same circular bordered container pattern

### Image Guidelines
- **Aspect ratio:** Square (1:1) for item thumbnails
- **Compression:** Optimize for mobile (WebP preferred, PNG acceptable)
- **Accessibility:** Include content descriptions for all images

---

## Interaction Design

### Touch Targets
- **Minimum size:** 48dp × 48dp (Material Design standard)
- **Recommended size:** 56dp × 56dp for primary actions
- **Spacing between targets:** Minimum 8dp

### Gestures
- **Tap:** Primary interaction for all buttons, list items, and selectable elements
- **Double-tap:** Add item to another list (e.g., double-tap Replenish List item to add to Shopping List)
- **Swipe right-to-left:** Reveal delete action and allow full-swipe delete
- **Swipe left-to-right:** Reveal left-side actions and allow full-swipe primary action
- **Long press:** Reserved for future context actions (currently unused)

### Feedback
- **Visual feedback:** All tapped elements must show a ripple effect or background color change
- **Ripple color:** `iron_man_gold` at 20% opacity on dark backgrounds
- **Toast messages:** Use for confirmations ("Item added to list") and error feedback
- **Loading states:** Show progress indicator with `iron_man_cyan` or `iron_man_gold` color

### Swipe Gestures

#### Shopping List Items — Swipe-to-Reveal Actions
- **Swipe right-to-left (←):** Reveals delete button on red background
  - Background: `iron_man_red`
  - Button width: 80dp
  - Icon: Trash can (`ic_menu_delete`), gold color
  - Half swipe: Revealed state stays open
  - Full swipe: Deletes item immediately

- **Swipe left-to-right (→):** Reveals left-side action buttons on transparent green background
  - Background: `swipe_action_green_transparent` (`#6600FF66`)
  - Button: Rename (80dp)
  - Icon: Edit icon (`ic_rename`), gold color
  - Half swipe: Revealed state stays open
  - Full swipe: Triggers rename/edit action

- **No in-row action buttons** — Actions are only shown on revealed background
- **Opposite-direction swipe closes** — Swiping back toward center hides actions
- **Foreground slides over background** — Content translates while action layer remains fixed
- **Snap-open threshold:** 72dp; **snap-open distance:** 160dp

#### Replenish List Items
- **Add button tap:** Add item to Shopping List (primary action)
- **Double-tap:** Add item to Shopping List (quick action shortcut)
  - Feedback: Toast message confirming addition
  - Visual: Standard ripple effect on tap
- **Replenish List filtering:** Items currently in the Shopping List are automatically hidden from the Replenish List. When an item is removed from the Shopping List it automatically reappears in the Replenish List.
  
#### Implementation Notes
- Use `ItemTouchHelper` with custom `onChildDraw` to translate foreground view
- Background contains actual button views (not canvas-only drawings)
- Partial swipe snaps open; opposite swipe snaps closed
- Category headers cannot be swiped (swipe disabled)

### Transitions and Animations
- **Duration:** 200-300ms for standard transitions
- **Easing:** Material Standard (cubic-bezier)
- **Navigation transitions:** Slide or fade between screens
- **List updates:** Subtle fade-in for new items, fade-out for removed items
- **Avoid:** Excessive animation — prioritize clarity and speed

---

## Component Library

### RecyclerView Lists

#### Standard List
- **Layout manager:** LinearLayoutManager (vertical)
- **Item style:** Card-based with spacing between items
- **Item margin:** `spacing_small` (8dp) on all sides for spacing between items
- **Item background:** Semi-transparent with lighter bluish tint (40% opacity: `#66101820`)
- **NO blur effect** — Simple semi-transparent background only
- **Item border:** 1dp, `iron_man_cyan` at 50% opacity (stronger visible cyan border: `#8000E5FF`)
- **Item corner radius:** 0dp (square corners, not rounded)
- **Item padding:** 16dp horizontal, 8dp vertical (internal content padding)
- **Minimum item height:** 48dp
- **Item elevation:** 0dp (flat appearance)
- **Background:** `@drawable/bg_jarvis` (list container background with radial gradient and arc reactor rings)
- **Scroll behavior:** Smooth scroll, standard overscroll effect
- **Dividers:** None (spacing and borders provide separation)

#### Grouped List (with Category Headers)
- **Category header:** Full-width, bold text, 18sp, `iron_man_gold`, 16dp padding
- **Items under category:** Card-based with semi-transparent background (NO blur, NO rounded corners), `spacing_small` (8dp) margin around each item
- **Item sorting:** Alphabetically by item name within each category
- **Visual separation:** Provided by card spacing and borders (no dividers needed)
- **Header background:** Solid `iron_man_dark_tech` (no transparency, no blur)

### Forms and Input

#### Add Item Dialog
- **Background:** Lighter bluish-white tint at 94% opacity with frosted glass effect (24dp blur) (`#F01A2A3A`)
- **Border:** 1dp, `iron_man_cyan` at 50% opacity (strong visible border: `#8000E5FF`)
- **Title:** "Add Item", bold, 18sp, `iron_man_gold`
- **Category selector:** Dropdown with full category list
- **Item name input:** Text field with hint "Item name"
- **Buttons:** "Cancel" (secondary) and "Add" (primary) at bottom-right
- **Padding:** 24dp on all sides
- **Validation:** Real-time or on submit, clear error messaging

#### Edit Item Dialog
- **Same as Add Item Dialog** with frosted glass effect
- **Title:** "Rename Item" or "Edit Item"
- **Pre-filled value:** Current item name
- **Buttons:** "Cancel" and "Save"

#### Barcode Management Dialog
- **Background:** Lighter bluish-white tint at 94% opacity with frosted glass effect (24dp blur) (`#F01A2A3A`)
- **Border:** 1dp, `iron_man_cyan` at 50% opacity (strong visible border: `#8000E5FF`)
- **Title:** "Manage Barcodes", bold, 18sp, `iron_man_gold`
- **Barcode list:** RecyclerView, 200dp fixed height, scrollable
- **Each barcode item:** Text (barcode value) + delete icon
- **Add barcode button:** Primary button at bottom, "Add Barcode"
- **Close button:** Secondary or text button, "Close"
- **Padding:** 24dp on all sides

### Tabs

#### Tab Bar (Two-Tab Layout)
- **Height:** 48dp
- **Background:** `iron_man_dark_tech`
- **Tab labels:** "Shopping List" and "Replenish"
- **Inactive tab text:** `iron_man_cyan`, 12sp
- **Active tab text:** `iron_man_gold`, 12sp, bold optional
- **Tab indicator:** 3dp height, `iron_man_red`, full tab width, bottom-aligned
- **Tab padding:** 16dp horizontal

### Floating Action Button (FAB)

- **Size:** 56dp (standard Material 3)
- **Background:** `iron_man_red`
- **Icon:** Plus sign or relevant action icon, `iron_man_gold` or white
- **Icon size:** 24dp
- **Position:** Bottom-right corner, 16dp from right edge, 80dp from bottom (to avoid overlap)
- **Elevation:** 6dp
- **Ripple effect:** `iron_man_gold` at 30% opacity

### Empty States

- **Icon:** Large icon (64dp+), `iron_man_cyan`, centered
- **Message:** "No items yet. Tap + to add.", `iron_man_cyan`, 16sp, centered
- **Padding:** 24dp on all sides
- **Background:** `iron_man_dark_tech`

---

## Accessibility

### Content Descriptions
- All images must have meaningful content descriptions
- All icon buttons must have content descriptions
- Empty decorative images: `android:contentDescription="@null"`

### Screen Readers
- Use semantic HTML-equivalent structure (headings, lists)
- Ensure all interactive elements are focusable
- Group related content logically

### High Contrast
- All color combinations must meet WCAG AA contrast requirements (4.5:1 minimum)
- Test readability on both `iron_man_dark_tech` and `black` backgrounds

---

## Animation and Motion

### Standard Transitions
- **Duration:** 250ms
- **Easing:** Material Standard curve
- **Types:** Fade, slide, scale (subtle)

### List Animations
- **Item add:** Fade in + subtle scale (0.9 to 1.0)
- **Item remove:** Fade out
- **Category expand/collapse:** Slide + fade (if applicable in future)

### Loading Indicators
- **Style:** Circular progress indicator
- **Color:** `iron_man_cyan` or `iron_man_gold`
- **Size:** 32dp (small), 48dp (medium)
- **Position:** Centered in content area

---

## Platform Considerations

### Android Specific

#### Material 3 Theming
- Base theme: `Theme.Material3.DayNight.NoActionBar`
- Color primary: `iron_man_red`
- Color secondary: `iron_man_gold`
- Status bar color: `iron_man_red`
- Window background: `black`

#### Status Bar
- **Color:** `iron_man_red` (matches primary color)
- **Icons:** Light (white/gold) for visibility on red background

#### Navigation Bar (System)
- **Color:** `black` or `iron_man_dark_tech`
- **Icons:** Light (white/gold)

---

## Implementation Notes

### Resource File References

All colors, dimensions, and strings must reference resource files:

```xml
<!-- Example: Using color resource -->
<TextView
    android:textColor="@color/iron_man_gold"
    android:background="@color/iron_man_dark_tech" />

<!-- Example: Using dimension resource -->
<Button
    android:layout_margin="@dimen/spacing_medium"
    android:textSize="@dimen/text_size_body" />

<!-- Example: Using string resource -->
<TextView
    android:text="@string/shopping_list_empty_message" />
```

### No Hardcoded Values

❌ **Forbidden:**
```xml
<TextView
    android:textColor="#F1D56D"
    android:textSize="16sp"
    android:text="Shopping List" />
```

✅ **Required:**
```xml
<TextView
    android:textColor="@color/iron_man_gold"
    android:textSize="@dimen/text_size_body"
    android:text="@string/shopping_list_title" />
```

---

## Design Review Checklist

Before considering any UI implementation complete, verify:

- [ ] All colors reference `res/values/colors.xml`
- [ ] All text sizes reference `res/values/dimens.xml`
- [ ] All spacing values reference `res/values/dimens.xml`
- [ ] All user-facing text references `res/values/strings.xml`
- [ ] Color contrast meets WCAG AA standards (4.5:1)
- [ ] Touch targets are minimum 48dp × 48dp
- [ ] Icons use consistent style (outlined, 2dp stroke)
- [ ] Typography follows the defined scale
- [ ] Spacing follows the defined scale
- [ ] Components match the specifications in this guide
- [ ] Visual style aligns with Jarvis / Iron Man theme
- [ ] All images have content descriptions
- [ ] Interactions provide clear feedback (ripple, toast, etc.)
- [ ] Empty states are clear and helpful

---

## Version History

| Version | Date           | Changes                                        |
|---------|----------------|------------------------------------------------|
| 1.0     | March 9, 2026  | Initial style guide creation                   |
| 1.1     | March 10, 2026 | Added mobile-friendly controls requirement (switches/toggle buttons). |

---

**For high-level feature requirements, see:** `docs/specs/`  
**For coding standards, see:** `docs/standards/ai-assistant-instructions.md`  
**For Kotlin/Android practices, see:** `docs/standards/kotlin-android-best-practices.md`
