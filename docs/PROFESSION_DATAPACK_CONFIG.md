---
title: 职业系统数据包配置教程(cbs)
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
# 职业系统数据包配置教程

本文档详细介绍如何通过数据包配置职业系统，包括职业树布局和职业详情页面。

## 目录

- [概述](#概述)
- [职业树布局配置](#职业树布局配置)
- [职业详情页面配置](#职业详情页面配置)
- [变量系统](#变量系统)
- [完整示例](#完整示例)
- [常见问题](#常见问题)

## 相关文档

- 📖 [指令系统教程](GUIDE_CMD_CN.md) - 包含 `/profession reload layouts` 指令说明
- 🌳 [职业树系统技术文档](PROFESSION_TREE_SYSTEM.md) - 系统架构与技术细节
- 👥 [职业配置教程](GUIDE_PROFESSION_CONFIG_CN.md) - 职业数值配置
- 🔌 [API集成指南](INTEGRATION_GUIDE.md) - 开发者API文档

---

## 概述

职业系统由两部分配置组成：

1. **职业树布局** (`tree_layout.json`) - 定义职业在树状图中的位置、图标和关系
2. **职业详情页面** (`details/{profession_id}.json`) - 定义点击职业后显示的详细信息页面

### 文件位置

```
data/classbioarsenal/profession_layouts/
├── tree_layout.json              # 职业树布局配置
└── details/                      # 详情页面配置目录
    ├── iron_swordguard.json
    ├── dual_swordsman.json
    └── ...
```

---

## 职业树布局配置

### 文件路径
`data/classbioarsenal/profession_layouts/tree_layout.json`

### 基本结构

```json
[
  {
    "professionId": "职业ID",
    "x": X坐标,
    "y": Y坐标,
    "iconItemId": "物品ID",
    "description": "简短描述",
    "children": {
      "子职业ID": {
        "parentLevel": 所需等级
      }
    }
  }
]
```

### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `professionId` | String | ✅ | 职业的唯一标识符，必须与职业定义文件名一致 |
| `x` | Number | ✅ | 职业节点在树状图中的X坐标（像素） |
| `y` | Number | ✅ | 职业节点在树状图中的Y坐标（像素） |
| `iconItemId` | String | ✅ | 显示的物品图标，格式：`namespace:item_id` |
| `description` | String | ❌ | 鼠标悬停时显示的简短描述 |
| `children` | Object | ❌ | 可升级的子职业，键为子职业ID，值为升级条件 |

### 示例

```json
[
  {
    "professionId": "iron_swordguard",
    "x": 100,
    "y": 100,
    "iconItemId": "minecraft:iron_sword",
    "description": "精通铁剑使用的守卫",
    "children": {
      "berserker": {
        "parentLevel": 25
      }
    }
  },
  {
    "professionId": "dual_swordsman",
    "x": 100,
    "y": 200,
    "iconItemId": "minecraft:iron_sword",
    "description": "追求极致速度的双剑士",
    "children": {
      "berserker": {
        "parentLevel": 35
      }
    }
  },
  {
    "professionId": "berserker",
    "x": 250,
    "y": 150,
    "iconItemId": "minecraft:netherite_axe",
    "description": "舍弃防御追求极致攻击的战士",
    "children": {}
  }
]
```

### 布局建议

- **起始职业**：建议X从100开始，Y间隔100像素
- **进阶职业**：向右偏移（X+150），根据分支调整Y坐标
- **分支结构**：多个职业可以升级到同一个进阶职业（如上例中的berserker）

---

## 职业详情页面配置

### 文件路径
`data/classbioarsenal/profession_layouts/details/{profession_id}.json`

> ⚠️ **重要**：文件名必须与 `professionId` 完全一致

### 基本结构

```json
{
  "professionId": "职业ID",
  "elements": [
    {
      "type": "元素类型",
      // 元素特定的配置...
    }
  ]
}
```

### 支持的UI元素类型

#### 1. TITLE - 标题

显示大号标题文本。

```json
{
  "type": "TITLE",
  "content": "标题内容",
  "color": "0xFFFFFFFF",
  "align": "center"
}
```

**参数**：
- `content` (String) - 文本内容，支持变量
- `color` (String) - 颜色，格式：`0xAARRGGBB`
- `align` (String) - 对齐方式：`left`、`center`、`right`

---

#### 2. TEXT - 普通文本

显示常规文本，支持自动换行。

```json
{
  "type": "TEXT",
  "content": "文本内容",
  "color": "0xFFCCCCCC",
  "align": "center",
  "wrapWidth": 300
}
```

**参数**：
- `content` (String) - 文本内容，支持变量
- `color` (String) - 颜色
- `align` (String) - 对齐方式
- `wrapWidth` (Number) - 换行宽度（像素），默认300

---

#### 3. SECTION_HEADER - 分节标题

带装饰的分节标题（如 `--- 标题 ---`）。

```json
{
  "type": "SECTION_HEADER",
  "content": "分节标题",
  "color": "0xFFFFFF00",
  "decorator": "---"
}
```

**参数**：
- `content` (String) - 标题内容
- `color` (String) - 颜色
- `decorator` (String) - 装饰符号，默认 `---`

---

#### 4. ATTRIBUTE_LIST - 属性列表

自动显示职业的所有属性加成。

```json
{
  "type": "ATTRIBUTE_LIST",
  "color": "0xFFCCCCCC",
  "align": "center"
}
```

**参数**：
- `color` (String) - 文本颜色
- `align` (String) - 对齐方式

**显示格式**：`属性名: +数值/级`

---

#### 5. INFO_LIST - 信息列表

显示自定义的项目列表，每项前带 `•` 符号。

```json
{
  "type": "INFO_LIST",
  "items": [
    "推荐装备：重型护甲",
    "特点：高生存能力",
    "可升级至狂战士（Lv.25）"
  ],
  "color": "0xFFAAAAFF",
  "align": "center",
  "wrapWidth": 300
}
```

**参数**：
- `items` (Array) - 列表项数组，每项支持变量
- `color` (String) - 文本颜色
- `align` (String) - 对齐方式
- `wrapWidth` (Number) - 换行宽度

---

#### 6. SPACER - 垂直间距

添加垂直空白间距。

```json
{
  "type": "SPACER",
  "height": 10
}
```

**参数**：
- `height` (Number) - 间距高度（像素）

---

#### 7. DIVIDER - 分隔线

绘制水平分隔线。

```json
{
  "type": "DIVIDER",
  "color": "0x88FFFFFF",
  "wrapWidth": 250,
  "height": 8
}
```

**参数**：
- `color` (String) - 线条颜色（支持半透明）
- `wrapWidth` (Number) - 线条宽度
- `height` (Number) - 分隔线后的间距

---

#### 8. ICON - 物品图标

显示物品图标（16x16）。

```json
{
  "type": "ICON",
  "itemId": "minecraft:diamond_sword"
}
```

**参数**：
- `itemId` (String) - 物品ID，格式：`namespace:item_id`

---

## 按钮配置

详情页面支持自定义"选择职业"和"返回"两个按钮的样式和位置。

### 按钮配置结构

在根对象中添加 `selectButton` 和 `backButton` 字段：

```json
{
  "professionId": "iron_swordguard",
  "selectButton": {
    "text": "选择职业",
    "x": 0,
    "y": -40,
    "width": 100,
    "height": 20,
    "enabled": true
  },
  "backButton": {
    "text": "返回",
    "x": 0,
    "y": -65,
    "width": 100,
    "height": 20,
    "enabled": true
  },
  "elements": [...]
}
```

### 按钮参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `text` | String | "按钮" | 按钮显示的文本 |
| `x` | Number | 0 | X坐标偏移（相对于屏幕中心，正值向右）|
| `y` | Number | -40 | Y坐标偏移（相对于屏幕底部，负值向上）|
| `width` | Number | 100 | 按钮宽度（像素）|
| `height` | Number | 20 | 按钮高度（像素）|
| `enabled` | Boolean | true | 是否可点击（selectButton会根据职业解锁状态自动调整）|

### 坐标系统说明

- **X坐标**：0表示屏幕水平中心，正值向右，负值向左
- **Y坐标**：0表示屏幕底部，负值向上，正值向下（会超出屏幕）

**示例**：
- `x: 0, y: -40` - 屏幕底部上方40像素，水平居中
- `x: -60, y: -40` - 屏幕底部上方40像素，向左偏移60像素
- `x: 60, y: -40` - 屏幕底部上方40像素，向右偏移60像素

### 按钮布局建议

**标准布局**（两按钮垂直排列）：
```json
"selectButton": {
  "text": "选择职业",
  "x": 0,
  "y": -40,
  "width": 100,
  "height": 20
},
"backButton": {
  "text": "返回",
  "x": 0,
  "y": -65,
  "width": 100,
  "height": 20
}
```

**水平布局**（两按钮并排）：
```json
"selectButton": {
  "text": "选择",
  "x": -60,
  "y": -40,
  "width": 80,
  "height": 20
},
"backButton": {
  "text": "返回",
  "x": 60,
  "y": -40,
  "width": 80,
  "height": 20
}
```

### 注意事项

1. 如果不配置按钮，将使用默认样式和位置
2. 只配置其中一个按钮也是允许的，另一个将使用默认
3. `selectButton` 的 `enabled` 属性会与职业解锁状态叠加判断
4. 按钮文本支持中文和特殊字符

---

## 变量系统

在文本内容中可以使用变量，它们会在运行时被替换为实际值。

### 内置变量

| 变量 | 说明 | 示例 |
|------|------|------|
| `{profession_name}` | 职业显示名称 | "铁剑卫" |
| `{max_level}` | 最大等级 | "30" |
| `{prerequisites}` | 前置职业信息 | "铁剑卫 (Lv.25)" 或 "无" |

### 属性变量

使用 `%属性ID%` 格式来显示属性数值。

**格式**：`%namespace.attribute_path%`

**示例**：
```json
{
  "type": "TEXT",
  "content": "每级提升 %generic.max_health% 点生命值"
}
```

如果该职业每级提升2点生命值，会显示为：
```
每级提升 2.0 点生命值
```

### 常用属性ID

- `generic.max_health` - 最大生命值
- `generic.attack_damage` - 攻击伤害
- `generic.armor` - 护甲值
- `generic.attack_speed` - 攻击速度
- `generic.movement_speed` - 移动速度

---

## 完整示例

### 示例1：标准风格（iron_swordguard）

```json
{
  "professionId": "iron_swordguard",
  "elements": [
    {
      "type": "TITLE",
      "content": "{profession_name}",
      "color": "0xFFFFFFFF",
      "align": "center"
    },
    {
      "type": "SPACER",
      "height": 5
    },
    {
      "type": "TEXT",
      "content": "精通铁剑使用的守卫，拥有极高的生命值加成（+%generic.max_health%）。",
      "color": "0xFFCCCCCC",
      "align": "center",
      "wrapWidth": 300
    },
    {
      "type": "SPACER",
      "height": 10
    },
    {
      "type": "SECTION_HEADER",
      "content": "详细信息",
      "color": "0xFFFFAA00",
      "decorator": "---"
    },
    {
      "type": "TEXT",
      "content": "铁剑卫是基础战士职业之一，专注于防御和生存能力。每级提升 %generic.max_health% 点生命值，是新手的理想选择。",
      "color": "0xFFBBBBBB",
      "align": "center",
      "wrapWidth": 300
    },
    {
      "type": "SPACER",
      "height": 15
    },
    {
      "type": "TEXT",
      "content": "最大等级: {max_level}",
      "color": "0xFFAAAAAA",
      "align": "center"
    },
    {
      "type": "SPACER",
      "height": 10
    },
    {
      "type": "TEXT",
      "content": "前置: {prerequisites}",
      "color": "0xFF55FF55",
      "align": "center"
    },
    {
      "type": "SPACER",
      "height": 15
    },
    {
      "type": "SECTION_HEADER",
      "content": "属性加成",
      "color": "0xFFFFFF00",
      "decorator": "---"
    },
    {
      "type": "ATTRIBUTE_LIST",
      "color": "0xFFCCCCCC",
      "align": "center"
    },
    {
      "type": "SPACER",
      "height": 10
    },
    {
      "type": "SECTION_HEADER",
      "content": "额外信息",
      "color": "0xFF00FFAA",
      "decorator": "---"
    },
    {
      "type": "INFO_LIST",
      "items": [
        "推荐装备：重型护甲",
        "特点：高生存能力，适合前排作战",
        "可升级至狂战士（Lv.25）"
      ],
      "color": "0xFFAAAAFF",
      "align": "center",
      "wrapWidth": 300
    }
  ]
}
```

### 示例2：创意风格（dual_swordsman）

使用不同的颜色方案和装饰符号。

```json
{
  "professionId": "dual_swordsman",
  "elements": [
    {
      "type": "TITLE",
      "content": "{profession_name}",
      "color": "0xFFFFD700",
      "align": "center"
    },
    {
      "type": "SPACER",
      "height": 8
    },
    {
      "type": "DIVIDER",
      "color": "0x88FFFFFF",
      "wrapWidth": 250,
      "height": 8
    },
    {
      "type": "SECTION_HEADER",
      "content": "职业特色",
      "color": "0xFFFF6B6B",
      "decorator": "═══"
    },
    {
      "type": "TEXT",
      "content": "双剑士以快速连击为核心战斗风格。攻击频率极高，需要良好的操作技巧。",
      "color": "0xFFDDDDDD",
      "align": "center",
      "wrapWidth": 300
    },
    {
      "type": "SPACER",
      "height": 10
    },
    {
      "type": "SECTION_HEADER",
      "content": "基础数据",
      "color": "0xFFADD8E6",
      "decorator": "···"
    },
    {
      "type": "TEXT",
      "content": "· 最大等级: {max_level}",
      "color": "0xFFFFFFFF",
      "align": "center"
    },
    {
      "type": "TEXT",
      "content": "· 前置职业: {prerequisites}",
      "color": "0xFF90EE90",
      "align": "center"
    },
    {
      "type": "SPACER",
      "height": 12
    },
    {
      "type": "SECTION_HEADER",
      "content": "属性加成",
      "color": "0xFFFFD700",
      "decorator": "---"
    },
    {
      "type": "ATTRIBUTE_LIST",
      "color": "0xFFFFA500",
      "align": "center"
    },
    {
      "type": "SPACER",
      "height": 12
    },
    {
      "type": "SECTION_HEADER",
      "content": "战斗建议",
      "color": "0xFFFF69B4",
      "decorator": "♦♦♦"
    },
    {
      "type": "INFO_LIST",
      "items": [
        "推荐装备：轻型护甲，双持武器",
        "战斗风格：高攻速，适合风筝战术",
        "进阶路线：可升级至狂战士（Lv.35）",
        "核心技巧：保持移动，利用速度优势"
      ],
      "color": "0xFFE6E6FA",
      "align": "center",
      "wrapWidth": 300
    }
  ]
}
```

---

## 颜色参考

### 基础颜色

| 颜色名 | 16进制 | 效果 |
|--------|--------|------|
| 白色 | `0xFFFFFFFF` | ![](https://via.placeholder.com/15/FFFFFF/000000?text=+) |
| 黑色 | `0xFF000000` | ![](https://via.placeholder.com/15/000000/000000?text=+) |
| 红色 | `0xFFFF0000` | ![](https://via.placeholder.com/15/FF0000/000000?text=+) |
| 绿色 | `0xFF00FF00` | ![](https://via.placeholder.com/15/00FF00/000000?text=+) |
| 蓝色 | `0xFF0000FF` | ![](https://via.placeholder.com/15/0000FF/000000?text=+) |

### 推荐配色

| 用途 | 颜色 | 16进制 |
|------|------|--------|
| 标题 | 金色 | `0xFFFFD700` |
| 正文 | 浅灰 | `0xFFCCCCCC` |
| 强调 | 橙色 | `0xFFFFA500` |
| 成功/可用 | 浅绿 | `0xFF55FF55` |
| 警告 | 黄色 | `0xFFFFFF00` |
| 特殊 | 青绿 | `0xFF00FFAA` |

### 颜色格式说明

格式：`0xAARRGGBB`
- `AA` - Alpha（透明度）：`00`完全透明，`FF`完全不透明
- `RR` - Red（红色分量）
- `GG` - Green（绿色分量）
- `BB` - Blue（蓝色分量）

**示例**：
- `0xFFFFFFFF` - 不透明白色
- `0x88FFFFFF` - 半透明白色
- `0x00000000` - 完全透明

---

## 常见问题

### Q1: 如何测试配置是否生效？

1. 修改JSON文件并保存
2. 在游戏中按 **F3 + T** 重新加载资源
3. 打开职业树，点击对应职业查看效果

### Q2: 职业显示"尚未配置详情页面"？

这表示 `details/` 目录下没有该职业对应的JSON文件。

**解决方法**：
1. 在 `details/` 目录创建 `{profession_id}.json`
2. 文件名必须与职业ID完全一致
3. 按照本文档格式编写配置

### Q3: 变量没有被替换？

**检查清单**：
- ✅ 变量格式正确（`{variable}` 或 `%attribute%`）
- ✅ 属性ID存在于职业的属性定义中
- ✅ 没有拼写错误

### Q4: 如何调整元素顺序？

元素按照 `elements` 数组中的顺序从上到下渲染。调整数组中对象的位置即可。

### Q5: 颜色不生效？

**常见问题**：
- 忘记 `0x` 前缀
- Alpha值设为 `00`（完全透明）
- 格式错误（应为8位16进制）

**正确格式**：`"color": "0xFFFFFFFF"`

### Q6: 文本超出屏幕？

调整 `wrapWidth` 参数：
```json
{
  "type": "TEXT",
  "wrapWidth": 280
}
```

建议值在 `250-320` 之间。

### Q7: 如何创建多列布局？

当前系统不支持多列布局，所有元素都是垂直排列。如需复杂布局，可以使用多个短文本元素模拟。

---

## 最佳实践

### 1. 保持一致的风格

在同一职业树中，建议使用统一的颜色方案和装饰风格。

### 2. 合理使用间距

- 标题后：5-10像素
- 分节之间：10-15像素
- 段落内：不使用或使用5像素

### 3. 文本长度控制

- 标题：不超过10个字
- 普通文本：每行建议30-40个字
- 列表项：每项不超过30个字

### 4. 颜色对比度

确保文本颜色与背景有足够对比度，推荐浅色文本（`0xFFCCCCCC`以上）。

### 5. 变量使用

在描述中使用变量使内容更动态：
```json
{
  "content": "达到 {max_level} 级后可升级至更强职业"
}
```

---

## 参考文件

- [职业树系统技术文档](PROFESSION_TREE_SYSTEM.md)
- [职业定义配置教程](../professions/README.md)
- [示例配置](../profession_layouts/details/iron_swordguard.json)

---

**更新日期**：2026-01-07  
**版本**：v2.0
