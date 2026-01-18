---
title: 引导(cbs)
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
# ⚔️ ClassBioArsenal (CBA) - 职业与生物军械库

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1-blue.svg)
![Forge Version](https://img.shields.io/badge/Forge-46.0%2B-orange.svg)

**ClassBioArsenal** 是一款为 Minecraft 1.20.1 开发的深度 RPG 增强模组。它通过引入**职业系统**、**动态装备词条**以及**智能实体进化机制**，将原版的生存体验转化为一场充满策略与成长的冒险。

---

## 🌟 核心特性

### 👥 1. 深度职业系统 (Profession System)
不再只是简单的“生存者”。通过 CBA，你可以选择并修炼不同的职业：
- **职业树结构**：从基础职业（如战士、射手）起步，逐步晋升为强大的进阶职业（如狂战士、双剑士）。
- **属性成长**：随着职业等级提升，玩家将获得生命值、攻击力、护甲等属性的永久加成。
- **装备限制**：特定的强力武器和防具现在需要对应的职业背景才能发挥全部威力。
- **自动化奖励**：支持在转职或升级时自动执行服务器指令，发放初始装备或阶段奖励。

### 🗡️ 2. 动态装备与词条 (Weapon & Armor System)
让每一件装备都独一无二：
- **稀有度系统**：从“普通”到“史诗”，不同品质决定了基础属性倍率和词条数量。
- **随机词条 (Affixes)**：武器可获得火焰伤害、暴击率、吸血等词条；防具可获得限伤、反甲、持续回血等效果。
- **装备升级**：通过战斗获取经验，提升装备等级。升级不仅增加基础属性，还能解锁或升级现有词条。
- **高度兼容**：默认支持原版剑、斧、三叉戟及所有防具，并支持通过数据包添加任何模组的装备。

### 👾 3. 智能实体机制 (Entity System)
世界会随着你的强大而进化：
- **动态等级计算**：怪物等级根据维度和与出生点的距离自动计算。离家越远，挑战越大！
- **生物词条**：怪物不再平庸。它们可能携带“冰冷”、“复活”、“残暴”等词条，拥有特殊攻击手段或防御机制。
- **Boss 血条**：高等级的精英怪物将拥有自定义的 Boss 血条，增强战斗仪式感。
- **额外掉落与指令**：支持根据怪物等级和词条配置额外的掉落物或触发特定的服务器事件。

---

## ⚙️ 高度自定义 (Data-Driven)
CBA 几乎所有的核心逻辑都支持通过 **数据包 (Data Pack)** 进行热加载配置：
- **职业定义**：自由创建新的职业、调整属性成长曲线。
- **词条池**：自定义武器、防具及怪物的词条属性与出现权重。
- **等级规则**：按维度定制怪物的等级增长速度。
- **外部加载**：支持从游戏目录外的指定路径强行加载数据包，方便服务器管理。

---

## 🛠️ 开发者集成 (API)
如果你是模组开发者，CBA 提供了完善的 Java API：
- **ProfessionAPI**：查询玩家职业状态、手动设置职业或经验。
- **AffixAPI**：读取或动态注册装备词条，实现自定义的战斗逻辑。
- **MechanicsAPI**：操控实体等级与标签，扩展怪物的行为。

> 详情请参考项目根目录下的 `INTEGRATION_GUIDE.md` 及各系统的 `*_API.md` 文档。

---

## ⌨️ 常用指令速查

| 指令 | 功能说明 |
| :--- | :--- |
| `/profession list` | 查看所有已注册的职业列表 |
| `/profession player info` | 查看自己当前的职业等级与经验进度 |
| `/cba_weapon reload` | 重载武器与防具系统配置 |
| `/cba_entity reload` | 重载实体系统配置 |
| `/profession player set <ID>` | [管理员] 为玩家设置特定职业 |
| `/cba_weapon affix set <ID> <Lv>` | [管理员] 为手持武器添加/修改词条 |

---

## 📦 安装要求
- **Minecraft**: 1.20.1
- **Forge**: 46.0.1 或更高版本
- **依赖**: 无（独立运行）

---

> 💡 **想要深入了解？**
> - 📖 [指令详细指南](GUIDE_CMD_CN.md)
> - 🛡️ [装备系统配置](GUIDE_WEAPON_ARMOR_CONFIG_CN.md)
> - ✨ [词缀系统文档](affix_system_documentation.md)
> - 👥 [职业系统配置](GUIDE_PROFESSION_CONFIG_CN.md)
> - 🎨 [职业详情页面自定义](PROFESSION_DATAPACK_CONFIG.md)
> - 🌳 [职业树系统技术文档](PROFESSION_TREE_SYSTEM.md)
> - 👾 [实体系统配置](GUIDE_ENTITY_SYSTEM_CONFIG_CN.md)
> - 🔌 [API集成指南](INTEGRATION_GUIDE.md)
