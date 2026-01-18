---
title: ClassBioArsenal 二次开发 / 集成使用总教程(cbs)
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
# ClassBioArsenal 二次开发 / 集成使用总教程

本教程面向**将 ClassBioArsenal 作为依赖**的模组作者，
只介绍如何在 Forge 1.20.1 环境下：

- 在 `mods.toml` 中声明对本模组的依赖；
- 在 `build.gradle` 中添加本模组的开发依赖；
- 基于 Forge 1.20.1 MDK 搭建并构建你的模组工程。

---

## 1. 作为依赖引入 ClassBioArsenal

### 1.1 运行时依赖（玩家安装）

普通玩家只需将 `ClassBioArsenal-x.y.z.jar` 放入游戏的 `mods` 目录，你的模组 jar 放入同一目录即可。

你需要在自己模组的 `mods.toml` 中声明依赖关系，例如：

```toml
[[dependencies.yourmodid]]
modId="classbioarsenal"
mandatory=true
versionRange="[1.5.1,)"  # 根据实际版本号调整
ordering="AFTER"
side="BOTH"
```

确保 `modId` 写的是 `classbioarsenal`。

### 1.2 开发环境依赖（IDE 中引用 API）

如果你有 ClassBioArsenal 的开发用 jar，可以采用本地依赖方式：

1. 将 jar 放到你的工程根目录的 `libs/` 下，例如：
   
   `libs/ClassBioArsenal-1.5.1-dev.jar`

2. 在 `build.gradle` 中添加：

```gradle
dependencies {
    implementation files('libs/ClassBioArsenal-1.5.1-dev.jar')
}
```

3. 刷新 Gradle，IDE 即可识别 `site.backrer.classbioarsenal.*` 包下的类和 `api` 包中的对外 API。

### 1.3 Forge 1.20.1 模组工程快速搭建（依赖本模组）

下面是一个给“要依赖 ClassBioArsenal 的模组”准备的最小构建流程，仅保留关键步骤：

1. **获取 Forge 1.20.1 MDK**  
   从 Forge 官网下载与你目标版本匹配的 1.20.1 MDK，解压到你的工程目录。  
   例如：`your-mod-root/` 下包含 `build.gradle`、`gradle/`、`src/` 等文件夹。

2. **导入 Gradle 工程**  
   在 IntelliJ IDEA / VS Code（带 Gradle 插件）中：
   - 选择“导入 Gradle 项目”，指向刚刚解压的 MDK 根目录；
   - 等待 Gradle 同步完成。

3. **加入 ClassBioArsenal 本地依赖**  
   使用本指南 1.2 中的做法：
   - 把 `ClassBioArsenal-1.5.1-dev.jar` 放入 `your-mod-root/libs/`；
   - 在 `build.gradle` 的 `dependencies {}` 中添加：

     ```gradle
     implementation files('libs/ClassBioArsenal-1.5.1-dev.jar')
     ```

4. **生成运行配置（仅第一次）**  
   在工程根目录执行（命令行）：

   ```bash
   ./gradlew genIntellijRuns   # IntelliJ
   # 或
   ./gradlew genVSCodeRuns     # VS Code
   ```

   对于新版 MDK，如果已经自带 run 配置，可以跳过这一步。

5. **在 IDE 中运行客户端 / 服务端**  
   同步 Gradle 后，IDE 会出现 `runClient` / `runServer` 等运行配置，直接运行即可在开发环境中调试，
   同时加载 ClassBioArsenal 与你的模组。

6. **构建你的模组 jar**  
   在工程根目录执行：

   ```bash
   ./gradlew build
   ```

   构建完成后，你的模组 jar 通常位于 `build/libs/yourmodid-x.y.z.jar`。
   将它和 `ClassBioArsenal-x.y.z.jar` 一起放入游戏 `mods` 目录即可在玩家环境中使用。

> 如果将来你使用 Maven 仓库（CurseMaven / 自建仓库等），可以把上面的 `files(...)` 替换为对应的 `implementation group:..., name:..., version:...`。
- 根据玩家当前职业调整你自己模组的数值（例如额外伤害、冷却缩减）。

### 3.2 关键方法

在代码中导入：

```java
import site.backrer.classbioarsenal.api.ProfessionAPI;
```

常用方法示例（详见 `PROFESSION_API.md`）：

- **判断玩家是否具备某职业**
  
  `boolean hasProfession(ServerPlayer player, String professionId)`

- **获取当前职业 ID 与等级**
  
  `@Nullable String getProfessionId(ServerPlayer player)`  
  `int getProfessionLevel(ServerPlayer player)`

- **为玩家添加职业经验**
  
  `void addProfessionExperience(ServerPlayer player, int amount)`

- **强制设置职业与等级**（例如在剧情阶段切职业）
  
  `void setProfession(ServerPlayer player, String professionId, int level)`

- **打开职业树与范围经验**
  
  `void openProfessionTree(Player player)`  
  `int addProfessionExperienceRadius(Player centerPlayer, int amount, int radius)`

更多进阶用法（例如读取职业配置、重新加载职业 JSON 等）请参考 `PROFESSION_API.md`。

---

## 4. 装备词条系统集成（AffixAPI）

### 4.1 常见场景

- 读取玩家武器 / 防具上的词条与等级；
- 根据某个词条值增加你自己的效果（例如触发自定义技能）；
- 在你自己的掉落或合成逻辑中，为物品绑定特定词条；
- Java 侧动态注册全新武器 / 防具词条。

### 4.2 关键方法

导入：

```java
import site.backrer.classbioarsenal.api.AffixAPI;
```

常用能力（详见 `AFFIX_API.md`）：

- **判定物品类型**
  
  - `boolean isWeapon(ItemStack stack)`  
  - `boolean isArmor(ItemStack stack)`

- **获取或修改词条等级和数值**
  
  - `int getWeaponAffixLevel(ItemStack stack, String affixId)`  
  - `double getWeaponAffixValue(ItemStack stack, String affixId)`  
  - 防具同理有对应的 Armor 版本。

- **注册新武器 / 防具 affix（Java 动态）**
  
  - `boolean registerWeaponAffix(String id, AffixConfig.AffixEntry def)`  
  - `boolean registerArmorAffix(String id, AffixConfig.AffixEntry def)`
  - **注意**：通过 Java API 注册的词条现在会**持久化**保存，即使执行 `/cba_weapon reload` 或重载数据包，这些词条也会自动重新合并到配置中，不会丢失。

- **武器 / 防具等级与经验**
  
  - `int getWeaponLevel(ItemStack stack)` / `int getWeaponXp(ItemStack stack)`  
  - `void addWeaponXp(ServerPlayer player, ItemStack stack, int amount)`  
  - `boolean setItemLevel(ItemStack stack, int level)` / `boolean setItemXp(ItemStack stack, int xp, Player player)`

- **品质与词条管理 (Reroll / CRUD)**
  
  - `boolean setItemRarity(ItemStack stack, String rarityId)`
  - `boolean setItemAffix(ItemStack stack, String affixId, int level)`  
    **注意**：此方法会无视配置中的 `item_rules`（如 `blocked_affixes`），强制将指定词条附加到物品上。
  - `boolean removeItemAffix(ItemStack stack, String affixId)`
  - `boolean rerollItem(ItemStack stack, boolean rerollRarity, boolean rerollAffix)`

更多示例与建议（包括如何在 `LivingHurtEvent` / `LivingHurtEvent` 中应用自定义 affix 逻辑），详见 `AFFIX_API.md`。

### 4.3 获取所有已注册的词条（高级）

如果你需要获取当前游戏中所有已注册的词条定义（例如用于制作图鉴或调试），而不仅仅是某个物品上的词条：

- **获取所有武器词条定义**：
  需访问内部配置类 `ConfigManager`：
  ```java
  import site.backrer.classbioarsenal.weapon.ConfigManager;
  import site.backrer.classbioarsenal.weapon.config.AffixConfig;
  
  List<AffixConfig.AffixEntry> allWeaponAffixes = ConfigManager.AFFIX_CONFIG.affixes;
  ```

- **获取所有防具词条定义**：
  ```java
  List<AffixConfig.AffixEntry> allArmorAffixes = ConfigManager.ARMOR_AFFIX_CONFIG.affixes;
  ```

---

## 5. 实体机制系统集成（MechanicsAPI）

实体机制系统负责：

- 非玩家实体的 **等级计算** 与属性加成；
- 实体身上的 **标签（Tag Affix）** 与行为效果；
- 基于实体等级 / 标签的 **额外掉落与指令**；
- 实体 HUD（血条上方信息）的展示与扩展。

### 5.1 运行时：实体等级与标签

导入：

```java
import site.backrer.classbioarsenal.api.MechanicsAPI;
```

常用方法（详见 `MECHANICS_API.md`）：

- **实体等级**
  
  - `int getEntityLevel(LivingEntity entity)`  
  - `void setEntityLevel(LivingEntity entity, int level, boolean refreshAttributes)`

- **实体标签（Tag Affix）**
  
  - `boolean hasEntityAffix(LivingEntity entity, String affixId)`  
  - `int getEntityAffixLevel(LivingEntity entity, String affixId)`  
  - `Map<String, Integer> getEntityAffixLevels(LivingEntity entity)`  
  - `void addOrSetEntityAffix(LivingEntity entity, String affixId, int level)`  
  - `void removeEntityAffix(LivingEntity entity, String affixId)`
  - `Map<String, EntityConfig.AffixDefinition> getAllEntityAffixDefinitions()` (获取所有已注册的实体词条定义)

例如：在 `LivingHurtEvent` 中，对攻击者身上的某个实体 affix 做额外逻辑。

### 5.2 配置级别：Level / Affix / Mob / Drops

`MechanicsAPI` 还提供了一整套等价于 `entity_system` 数据包的 Java 入口，用来：

- 读取 / 修改维度等级规则（`LevelConfig`）；
- 读取 / 注册实体 affix 定义（`AffixSettings.affixes` + `global_settings`）；
- 读取 / 设置实体专属配置（`MobConfig`）；
- 读取 / 追加额外掉落规则（`ExtraDropRule`）。

典型用法举例：

- **动态注册实体 affix（Java）**：
  
  `registerEntityAffix("yourmod:tag_shadow", minLevel, baseChance, growthPerLevel, maxLevel, params, conflicts);`

- **追加特定生物的掉落规则**：
  
  构造一个 `EntityConfig.ExtraDropRule`，通过 `addExtraDropRule("minecraft:zombie", rule, true)` 追加到该生物的掉落表中。

详细字段与数据结构对应关系见 `MECHANICS_API.md`。

### 5.3 实体 HUD 渲染的扩展

客户端 HUD 渲染位于：

- 类：`site.backrer.classbioarsenal.mechanics.client.ClientEntityEventHandler`
- 方法：`public static void renderEntityInfo(PoseStack poseStack, LivingEntity entity, float partialTick)`

你可以在自己的模组中：

- **复用默认 HUD + 追加内容**：

  ```java
  ClientEntityEventHandler.renderEntityInfo(poseStack, entity, partialTick);
  // 然后继续在当前 poseStack / buffer 上画你自己的文字 / 图标
  ```

- **关闭默认 HUD，完全自绘**：

  ```java
  ClientEntityEventHandler.setOverlayEnabled(false);
  // 在你自己的 RenderLevelStageEvent 或其他事件中，自行从 MechanicsAPI 获取数据并渲染
  ```

> 在修改 `renderEntityInfo` 内部时，如果你想在默认内容下方增加额外行，建议在 `buffer.endBatch();` 之前调用额外的 `font.drawInBatch`，这样可以复用同一渲染批次。

---

## 6. 数据包方式的扩展

如果你更偏好用 **数据驱动** 的方式扩展，而不是写 Java 代码，可以通过数据包在以下路径下添加 JSON：

- 职业：`data/classbioarsenal/professions/*.json`
- 武器词条：`data/classbioarsenal/weapon_system/affixes.json`
- 防具词条：`data/classbioarsenal/armor_system/armor_affixes.json`
- 实体机制：`data/classbioarsenal/entity_system/*.json`

这些 JSON 的字段与含义在以下文档中已经详细说明：

- 职业系统：`PROFESSION_API.md`（职业 JSON 结构说明在其中）；
- 装备词条系统：`AFFIX_API.md`（武器 / 防具 affix JSON 示例）；
- 实体机制系统：`MECHANICS_API.md`（`level_config` / `affix_config` / `entity_settings` / `drops` 示例）。

你的模组既可以：

- 直接打包自己的数据包（namespace 为你自己的 modid），利用这些 schema 扩展原系统；
- 也可以用 Java 动态注册（通过上述 API），实现和 JSON 等价的功能。

---

## 7. 典型集成示例

### 7.1 只有特定职业才能使用你的武器

思路：在你自定义武器的 `use` 或相关事件中，检查玩家职业是否符合要求，不符合则提示并阻止使用。

伪代码：

```java
if (!ProfessionAPI.hasProfession(player, "classbioarsenal:warrior")) {
    player.displayClientMessage(Component.literal("只有战士才能使用这把武器！"), true);
    return InteractionResult.FAIL;
}
```

### 7.2 根据 ClassBioArsenal 武器 affix 增强你自己的技能

在技能触发事件中：

```java
ItemStack weapon = player.getMainHandItem();
if (AffixAPI.isWeapon(weapon)) {
    int level = AffixAPI.getWeaponAffixLevel(weapon, "classbioarsenal:sharpness_like_id");
    if (level > 0) {
        float bonus = 0.1f * level; // 每级 +10% 伤害
        damage *= (1.0f + bonus);
    }
}
```

### 7.3 让你的 Boss 根据实体标签有特殊互动

在 `LivingHurtEvent` 或 `LivingAttackEvent` 中：

```java
if (event.getEntity() instanceof LivingEntity target) {
    if (MechanicsAPI.hasEntityAffix(target, "classbioarsenal:tag_desperate")) {
        // 对带有 "绝境" 标签的怪物额外处理
    }
}
```

### 7.4 为指定生物追加你自己的掉落

在服务器启动时：

```java
EntityConfig.ExtraDropRule rule = new EntityConfig.ExtraDropRule();
// 配置 rule.chance / entity_level / required_affixes / items...
MechanicsAPI.addExtraDropRule("minecraft:zombie", rule, true);
```

---

## 8. 开发注意事项与版本兼容

- **只依赖 `api` 包**：
  
  建议你的模组尽量只依赖 `site.backrer.classbioarsenal.api` 下的类，
  这些是为二次开发稳定暴露的接口。

- **内部实现可能变动**：
  
  `profession.*`、`weapon.*`、`mechanics.*` 等内部包属于实现细节，可能随版本演进更改；
  除非你确认要做深度兼容，否则尽量避免直接依赖内部类。

- **版本检查**：
  
  在 `mods.toml` 中使用合理的 `versionRange`（例如 `[1.3.2,)`），
  并在必要时在运行时检查关键 API 的存在（通过反射或简单方法调用测试）。

- **调试与日志**：
  
  善用 Minecraft `/reload` 与游戏日志，结合三个 API 文档中的调试建议，
  确认数据包 / Java 注册是否正确生效。

---

如需对某一部分（职业 / 装备 / 实体机制 / HUD）做更深入的定制，请结合本教程与对应的 `*_API.md` 文档一起阅读和实践。
