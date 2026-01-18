---
title: 职业树配置说明文档(cbs)
tags:
  - pfc
  - ca
  - mc
  - mod
categories:
  - pfc
  - ca
date: 2026-1-12 17:12:45
---
# 职业树配置说明文档

## 概览

本配置文件定义了完整的职业树结构，包含 **18个职业**，分为 **3个层级**：

- **Tier 0（基础职业）**：5个可选起始职业
- **Tier 1（一阶进阶）**：11个进阶职业
- **Tier 2（二阶进阶）**：2个高级职业

## 职业树结构

### 剑术系 (Sword Category)

```
铁剑卫 (iron_swordguard) [Tier 0, Lv.25/30]
├── 狂战士 (berserker) [Tier 1, Lv.15]
│   └── 战神 (warlord) [Tier 2]
├── 圣骑士 (paladin) [Tier 1, Lv.15]
│   └── 神圣十字军 (divine_crusader) [Tier 2]
└── 剑舞者 (sword_dancer) [Tier 1]
```

### 双剑系 (Dual Sword Category)

```
双剑士 (dual_swordsman) [Tier 0, Lv.28/30/35]
├── 狂战士 (berserker) [Tier 1, Lv.15] ⚠️ 跨系统合流
│   └── 战神 (warlord) [Tier 2]
├── 刀剑宗师 (blade_master) [Tier 1]
└── 风暴之刃 (storm_blade) [Tier 1]
```

### 重剑系 (Heavy Sword Category)

```
重剑师 (heavy_swordmaster) [Tier 0, Lv.25/28]
├── 泰坦战士 (titan_warrior) [Tier 1]
└── 破地者 (earth_breaker) [Tier 1]
```

### 暗影系 (Shadow Category)

```
影刃学徒 (shadow_blade_apprentice) [Tier 0, Lv.25/28]
├── 影刃刺客 (shadow_assassin) [Tier 1]
└── 夜行者 (night_stalker) [Tier 1]
```

### 长棍系 (Staff Category)

```
旋棍使 (whirl_staff_adept) [Tier 0, Lv.25/30]
├── 游龙棍圣 (dragon_staff_master) [Tier 1]
└── 禅武僧 (mystic_monk) [Tier 1]
```

## 配置字段说明

### 核心字段

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| `professionId` | `string` | 职业唯一标识符 | `"iron_swordguard"` |
| `tier` | `int` | 职业层级（0=基础，1=进阶，2=高级） | `0` |
| `category` | `string` | 职业路线分类 | `"sword"` |
| `isStartingProfession` | `boolean` | 是否为可选的起始职业 | `true` |
| `x`, `y` | `int` | GUI中的坐标位置 | `50`, `100` |
| `iconItemId` | `string` | 显示图标的物品ID | `"minecraft:iron_sword"` |
| `description` | `string` | 职业描述文本 | `"精通铁剑使用的守卫"` |

### 关系字段

#### `prerequisites`（前置条件）
用于**文档说明**的明确前置条件列表：

```json
"prerequisites": [
  {
    "professionId": "iron_swordguard",
    "requiredLevel": 25
  }
]
```

#### `children`（子职业）
定义可进阶的职业及其等级要求：

```json
"children": {
  "berserker": {
    "parentLevel": 25
  },
  "paladin": {
    "parentLevel": 25
  }
}
```

## 职业选择规则

### 1. 基础职业选择（Tier 0）
- **条件**：玩家当前无职业
- **可选**：所有 `isStartingProfession: true` 的职业
- **限制**：一旦选择，无法再选择其他基础职业

### 2. 进阶职业选择（Tier 1+）
- **条件**：
  - 当前职业在目标职业的 `prerequisites` 列表中
  - 当前职业等级 ≥ 对应的 `requiredLevel`
- **分支选择**：可以在多个进阶职业中选择其一

### 3. 跨系统合流
某些职业（如 `berserker`）可以从多个不同的基础职业进阶：
- 从 `iron_swordguard` Lv.25 进阶
- 从 `dual_swordsman` Lv.35 进阶

## 示例职业配置

### 基础职业示例：铁剑卫

```json
{
  "professionId": "iron_swordguard",
  "tier": 0,
  "category": "sword",
  "isStartingProfession": true,
  "x": 50,
  "y": 100,
  "iconItemId": "minecraft:iron_sword",
  "description": "精通铁剑使用的守卫，平衡的攻守兼备",
  "prerequisites": [],
  "children": {
    "berserker": { "parentLevel": 25 },
    "paladin": { "parentLevel": 25 },
    "sword_dancer": { "parentLevel": 30 }
  }
}
```

**说明**：
- 玩家选择铁剑卫后，可以在25级时选择狂战士或圣骑士
- 或者在30级时选择剑舞者
- 这提供了3个不同的发展方向

### 进阶职业示例：狂战士

```json
{
  "professionId": "berserker",
  "tier": 1,
  "category": "sword",
  "isStartingProfession": false,
  "x": 250,
  "y": 175,
  "iconItemId": "minecraft:netherite_axe",
  "description": "狂战士 - 舍弃防御追求极致攻击的战士",
  "prerequisites": [
    {
      "professionId": "iron_swordguard",
      "requiredLevel": 25
    },
    {
      "professionId": "dual_swordsman",
      "requiredLevel": 35
    }
  ],
  "children": {
    "warlord": { "parentLevel": 15 }
  }
}
```

**说明**：
- 有两条路径可以成为狂战士
- 狂战士达到15级后可以进阶为战神（Tier 2）

## 坐标布局说明

配置文件中的 `x` 和 `y` 坐标用于GUI显示：

- **X轴**：
  - `50`：基础职业（Tier 0）
  - `250`：一阶进阶（Tier 1）
  - `450`：二阶进阶（Tier 2）
  
- **Y轴**：
  - 按职业顺序和分类排列
  - 每个职业间隔约50像素

## 职业列表总览

### Tier 0 - 基础职业（5个）
1. `iron_swordguard` - 铁剑卫
2. `dual_swordsman` - 双剑士
3. `heavy_swordmaster` - 重剑师
4. `shadow_blade_apprentice` - 影刃学徒
5. `whirl_staff_adept` - 旋棍使

### Tier 1 - 一阶进阶（11个）
1. `berserker` - 狂战士 ⭐
2. `paladin` - 圣骑士
3. `sword_dancer` - 剑舞者
4. `blade_master` - 刀剑宗师
5. `storm_blade` - 风暴之刃
6. `titan_warrior` - 泰坦战士
7. `earth_breaker` - 破地者
8. `shadow_assassin` - 影刃刺客
9. `night_stalker` - 夜行者
10. `dragon_staff_master` - 游龙棍圣
11. `mystic_monk` - 禅武僧

### Tier 2 - 二阶进阶（2个）
1. `warlord` - 战神
2. `divine_crusader` - 神圣十字军

⭐ = 跨系统职业（可从多个基础职业进阶）

## 下一步工作

> **注意**：当前仅更新了 `tree_layout.json` 配置文件。

还需要为新增的职业创建对应的职业定义文件：
- `src/main/resources/data/classbioarsenal/professions/{profession_id}.json`

每个职业定义文件应包含：
- 基础属性（`name`, `displayName`, `isNormal`, `maxLevel`, `maxExp`）
- 属性加成（`attributeBonuses`）
- 起始装备（`startingGear`）
- 等级命令（`levelCommands`，可选）

参考现有的职业文件：
- `iron_swordguard.json`
- `berserker.json`
- `dual_swordsman.json`
