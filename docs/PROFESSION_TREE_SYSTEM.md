# Profession Tree System: Technical Overview

This document provides a comprehensive technical breakdown of the Profession Tree system in the `ClassBioArsenal` mod (Forge 1.20.1). It is designed to provide an AI with the necessary context to understand, maintain, or extend the system.

## Related Documentation

- 📖 [Command Guide](GUIDE_CMD_CN.md) - Includes `/profession reload layouts` command
- 🎨 [Detail Page Configuration Tutorial](PROFESSION_DATAPACK_CONFIG.md) - How to customize profession detail pages
- 👥 [Profession Configuration Guide](GUIDE_PROFESSION_CONFIG_CN.md) - Basic profession setup
- 🔌 [API Integration Guide](INTEGRATION_GUIDE.md) - Developer API reference

---

## 1. Core Architecture

The system is split into two layers: **Core Data** (Professions) and **GUI/Advancement Logic** (Layouts).

### 1.1 Core Data Layer
- **`Profession` Class**: The base data model. Stores internal name, display name, level stats (`maxLevel`, `maxExp`, `multiplier`), and `attributeBonuses`.
- **`ProfessionManager`**: A `SimpleJsonResourceReloadListener` that loads profession definitions from `data/classbioarsenal/professions/*.json`.
- **`ModVariables`**: A Capability-based system that stores player-specific profession data: `professionName`, `professionLevel`, `currentExperience`, and a `Set<String> unlockedProfessions`.

### 1.2 GUI & Layout Layer
- **`ProfessionLayout` Class**: Stores GUI-specific data: coordinates (`x`, `y`), icon, description, and a `Map<String, Integer> children` (child ID -> required parent level).
- **`ProfessionLayoutManager`**: Loads the tree structure from a single file: `data/classbioarsenal/profession_layouts/tree_layout.json`.
- **`ProfessionTreeScreen`**: The main GUI for the tree. Handles node rendering, dragging, and highlighting.
- **`ProfessionDetailScreen`**: Displays specific profession info and handles the "Select" action.

## 2. Configuration Details

### 2.1 Profession Definition (`professions/*.json`)
```json
{
  "name": "iron_swordguard",
  "displayName": "铁剑卫",
  "isNormal": true,
  "professionLevel": 1,
  "maxLevel": 30,
  "maxExp": 100,
  "multiplier": 1.5,
  "attributeBonuses": [
    {
      "attribute": "minecraft:generic.max_health",
      "amountPerLevel": 2.0,
      "operation": "add"
    }
  ]
}
```
- `isNormal`: If true, it's a "Base Profession" selectable by players with no profession.
- `multiplier`: Used in experience and attribute scaling formulas.

### 2.2 Tree Layout (`tree_layout.json`)
```json
[
  {
    "professionId": "iron_swordguard",
    "x": 100, "y": 100,
    "iconItemId": "minecraft:iron_sword",
    "description": "High health guard. (+%generic.max_health%)",
    "children": {
      "berserker": { "parentLevel": 25 }
    }
  }
]
```
- **Descriptions**: Stored here to separate GUI text from core data. Supports `%attribute_id%` placeholders.
- **Children**: Defines the advancement path. A profession can be a child of multiple parents.

## 3. Advancement & Selection Logic

### 3.1 Selection Rules
A player can select a profession if **ANY** of these conditions are met:
1.  **Base Selection**: The profession is `isNormal: true` and the player has no current profession.
2.  **Advancement**: The player's current profession is a parent of the target in `tree_layout.json`, AND the player's current level >= `parentLevel`.
3.  **Re-selection**: The profession ID is already in the player's `unlockedProfessions` set (allows switching back to previously owned professions).

### 3.2 Validation Flow
- **Client**: `ProfessionDetailScreen` checks `canSelect` in `init()` to enable/disable the button.
- **Server**: `C2SSelectProfessionMessage` performs a full re-validation of all parents and level requirements before calling `variables.setProfession()`.

## 4. Key Logic Locations for Maintenance

- **Validation Bug Fixes**: Look at `ProfessionDetailScreen.java`, `C2SSelectProfessionMessage.java`, and `ProfessionTreeScreen.java`. Ensure loops iterate through **all** layouts to check for parent-child relationships.
- **Experience Formula**: Located in `ModVariables.PlayerVariables.addExperience` and `levelUp`.
- **Attribute Application**: Handled by `ProfessionManager.setAttributeByProfessionInPlayer`.
- **Description Parsing**: `ProfessionDetailScreen.replaceVariables` handles the regex replacement of attribute placeholders.

## 5. Customizable Detail Pages (v1.3)

The profession detail screen now supports fully customizable layouts via JSON configuration files located in `data/classbioarsenal/profession_layouts/details/<profession_id>.json`.

### 5.1 Detail Page Configuration
Each profession can have a custom detail page that defines:
- **UI Elements**: Title, text, section headers, dividers, spacers, attribute lists, and info lists
- **Buttons**: Customizable "Select" and "Back" buttons with configurable text, position, size, and enabled state

### 5.2 Button Configuration
Buttons use a coordinate system relative to the screen:
- **X coordinate**: Relative to screen center (0 = center, positive = right, negative = left)
- **Y coordinate**: Relative to screen bottom (0 = bottom, negative = up)

Example button configuration:
```json
"selectButton": {
  "text": "选择",
  "x": -60,
  "y": -35,
  "width": 90,
  "height": 20,
  "enabled": true
},
"backButton": {
  "text": "返回",
  "x": 60,
  "y": -35,
  "width": 90,
  "height": 20,
  "enabled": true
}
```

**Recommended Layout**: Use horizontal button placement (left/right) to avoid overlapping with content. Buttons should be placed at least 30 pixels from the bottom to provide adequate spacing.

### 5.3 Fallback Behavior
If no detail page configuration exists for a profession, the system automatically generates a default layout based on the profession data.

### 5.4 Hot Reload
Use `/profession reload layouts` command to reload detail page configurations without restarting the game.

## 6. Recent Changes (v1.3)
- **v1.3**: Added customizable detail page system with JSON-based UI element configuration
- **v1.3**: Implemented customizable buttons with position, size, text, and enabled state configuration
- **v1.3**: Added hot reload command for detail page layouts
- **v1.2**: Moved `description` from profession JSONs to `tree_layout.json`
- **v1.2**: Implemented `parentLevel` requirements for advanced professions
- **v1.2**: Fixed a bug where only the first parent in the layout was checked for validation
- **v1.2**: Added support for re-selecting already unlocked professions
- **v1.2**: Increased `dual_swordsman` max level to 40 to accommodate `berserker` (Lv.35) requirement
