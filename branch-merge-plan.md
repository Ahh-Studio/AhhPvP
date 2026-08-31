# 分支合并计划

基于对 5 个分支的差异分析，制定以下合并方案。**排除 Quilt**（Quilt Loader 尚未稳定，等待成熟后再考虑）。

---

## 现状分析：分支间差异有多大？

| 对比维度 | `dev` | `1.21.10-release` | `1.21.11-release` | `neoforge-...` | `quilt-...` |
|---|---|---|---|---|---|
| **平台** | Fabric | Fabric | Fabric | NeoForge | Quilt |
| **MC 版本** | 26.1.2 | 1.21.10 | 1.21.11 | 1.21.11 | 1.21.11 |
| **Java** | 25 | 21 | 21 | 21 | 21 |
| **映射** | Mojmap | Mojmap | Yarn | Parchment | — |
| **构建工具** | Fabric Loom | Fabric Loom | Fabric Loom | NeoForge moddev | Quilt Loom |
| **功能集** | 完整 | 少了防御塔等 | 同 1.21.10 | 少了防御塔/地雷/命令 | 少了防御塔/地雷/小鸡/鸡蛋 |

> **注意：** `quilt-1.21.11-release` 分支暂不纳入合并计划，等待 Quilt Loader 生态稳定后再评估。

**关键发现：** 分支之间差异很大，绝非简单的版本号不同。它们采用不同的构建工具、映射表、甚至不同的 Java 版本。`dev` 分支功能最完整，而其他分支要么是旧版本快照，要么是跨平台移植（且功能落后）。

---

## 合并范围决策

### 方案 A：只合并加载器（Fabric + NeoForge）← 推荐

将 NeoForge 代码合并到 `dev`，用多模块架构管理。版本分支保留不动。

| 合并对象 | 合并方式 |
|---|---|
| `neoforge-1.21.11-release` | 提取入口代码，适配到 `dev` 的 `common` + `neoforge` 模块 |
| `1.21.10-release` | 不合并，保留原分支 |
| `1.21.11-release` | 不合并，保留原分支 |

### 方案 B：彻底合并（所有分支 → 一个 `dev`）

将加载器差异 **和** 版本差异全部合并到一个仓库中。

| 合并对象 | 合并方式 |
|---|---|
| `neoforge-1.21.11-release` | 同上，提取到 `neoforge` 模块 |
| `1.21.10-release` | 提取版本差异代码到 `v1_21_10` 源集 |
| `1.21.11-release` | 提取版本差异代码到 `v1_21_11` 源集 |

---

### 方案 A vs 方案 B：利弊对比

| 对比维度 | 方案 A：只合并加载器 | 方案 B：合并所有分支 |
|---|---|---|
| **构建配置复杂度** | 低 — 只需处理 1 个 MC 版本，2 个加载器 | **极高** — 需要同时处理 3 个 MC 版本 + 2 个加载器 |
| **代码重复** | 版本分支间仍有重复（如 1.21.10 和 1.21.11 的差异） | 几乎消除重复，所有代码集中管理 |
| **跨版本 bug 修复** | 仍需手动 cherry-pick 到旧版本分支 | **一次性修复**，所有版本受益 |
| **新增功能跨版本** | 需为每个版本单独适配 | 一次添加，自动覆盖所有版本 |
| **Gradle 构建时间** | 正常（~30s-1min） | **显著增加**（3× MC 版本 × 2 加载器 = 6 次编译） |
| **调试难度** | 低 — 出错时容易定位 | **高** — 编译错误可能来自某个特定版本 + 加载器组合 |
| **IDE 支持** | 良好 — 标准多模块项目 | **差** — 多个 MC 版本在同一项目，IDE 经常混淆 |
| **旧版本发版** | 直接切分支即可 | 需要从 `dev` 构建特定版本，配置复杂 |
| **长期维护成本** | 低 | **高** — 每次 MC 版本更新都要改适配层 |

### 结论：推荐方案 A

**不推荐合并版本分支**，原因如下：

1. **MC 版本的 API 差异本质上是不可兼容的**——1.21.10 的某些类/方法在 26.1.2 中已经改名或移除。强行合并意味着要为每个版本写适配层（adapter/reflaction），代码量可能比独立维护还多。

2. **你的旧版本分支是"快照"性质**——制作 `1.4.5+mc1.21.10` 时，只需要在对应分支上 cherry-pick 关键修复，工作量可控。

3. **构建时间膨胀**——每个 MC 版本都需要完整的重新编译，3 个版本 × 2 个加载器 = 6 个独立的构建产物。

4. **IDE 会崩溃**——多个 MC 版本在同一个项目中，Loom 的 mappings 会互相冲突，IntelliJ 经常报错。

---

## 最终方案：合并加载器到 `dev`

**分支保留策略：**
- `main` — 主分支，不动
- `dev` — 开发分支（改造目标：Fabric + NeoForge 多模块）
- `1.21.10-release` / `1.21.11-release` — 旧版本 Fabric 分支，保留不动，用于制作旧版本发布
- `neoforge-1.21.11-release` — 作为 NeoForge 代码的参考源，提取后保留
- `quilt-1.21.11-release` — 保留不动，暂不纳入

### 改造目标：构建多模块架构

**目标：** 将 NeoForge 代码合并到 `dev` 中，用 Gradle 多模块管理。

#### 最终目录结构

```
AhhPvP/
├── common/                    # 共享代码（~80% 的源码）
│   ├── build.gradle           # 仅依赖 Minecraft（无加载器）
│   └── src/main/java/
│       ├── com/aiden/pvp/
│       │   ├── blocks/        # 所有方块类（通用）
│       │   ├── blocks/entity/ # 所有方块实体
│       │   ├── entities/      # 所有实体类
│       │   ├── items/         # 所有物品类
│       │   ├── mixin/         # 通用 mixin（LivingEntity, PlayerEntity 等）
│       │   ├── util/          # 工具类（EnchantmentUtil, ExplosionImpl）
│       │   ├── screen/        # SettingsScreen, ModSliderWidget
│       │   ├── commands/      # KitCommand（通用命令）
│       │   ├── gamerules/     # ModGameRules
│       │   └── datagen/       # 数据生成器
│       └── resources/
│           ├── assets/        # 纹理、模型、语言文件
│           └── data/          # 配方、战利品表
│
├── fabric/                    # Fabric 平台入口
│   ├── build.gradle
│   └── src/main/
│       ├── java/com/aiden/pvp/
│       │   ├── PvP.java           # ModInitializer
│       │   ├── PvPClient.java     # ClientModInitializer
│       │   ├── payloads/          # 网络包（Fabric API 方式）
│       │   └── modmenu/           # ModMenu 兼容
│       ├── resources/
│       │   ├── fabric.mod.json
│       │   └── pvp.mixins.json    # Fabric 专属 mixin 配置
│       └── templates/             # 模板文件
│
├── neoforge/                  # NeoForge 平台入口
│   ├── build.gradle
│   └── src/main/
│       ├── java/com/aiden/pvp/
│       │   ├── PvP.java           # @Mod + IEventBus
│       │   ├── PvPClient.java
│       │   └── handler/           # 网络包处理
│       └── resources/
│           └── META-INF/
│               └── neoforge.mods.toml
│
├── build.gradle               # 根构建脚本
├── settings.gradle            # 包含所有子模块
└── gradle.properties          # 公共属性
```

#### 拆分逻辑

| 代码类别 | 归属模块 | 原因 |
|---|---|---|
| 方块类（ModBlocks, DefenseTowerBlock, ...） | common | 纯 Minecraft API，无加载器依赖 |
| 实体类（MurdererEntity, FireballEntity, ...） | common | 同上 |
| 物品类（ModItems, SwordItem, ...） | common | 同上 |
| Mixin（LivingEntity, PlayerEntity, ...） | common | Mixin 本身跨平台 |
| 数据生成器 | common | 仅依赖 Minecraft |
| 屏幕（SettingsScreen） | common | 仅依赖 Minecraft |
| 工具类（EnchantmentUtil, ExplosionImpl） | common | 纯逻辑 |
| 游戏规则 | common | 仅依赖 Minecraft |
| **PvP.java（主入口）** | fabric / neoforge | 各平台入口不同 |
| **PvPClient.java（客户端入口）** | fabric / neoforge | 各平台客户端入口不同 |
| **网络包处理** | fabric / neoforge | 各平台网络 API 不同 |
| **ModMenu 兼容** | fabric 专属 | 仅 Fabric 有 |
| **ConditionalItemModelPropertiesMixin** | fabric 专属 | 仅 Fabric 客户端需要 |
| **fabric.mod.json / neoforge.mods.toml** | 各自模块 | 加载器描述文件 |

#### 需要处理的差异

**差异 1：注册 API 不同**
- Fabric：`Registry.register(BuiltInRegistries.ITEM, id, item)`
- NeoForge：`DeferredRegister` + `modBus.addListener`

**方案：** 在 `common` 中提供抽象注册接口，各平台实现。

```java
// common 中定义
public interface ModRegistries {
    <T> T registerItem(Identifier id, Item item);
    <T> T registerBlock(Identifier id, Block block);
    <T extends Entity> EntityType<T> registerEntity(Identifier id, EntityType.Builder<T> builder);
}

// fabric 中实现
public class FabricRegistries implements ModRegistries {
    public <T> T registerItem(Identifier id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }
}

// neoforge 中实现
public class NeoForgeRegistries implements ModRegistries {
    private final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public <T> T registerItem(Identifier id, Item item) {
        ITEMS.register(id.getPath(), () -> item);
        // ...
    }
}
```

**差异 2：网络包 API 不同**
- Fabric：`PayloadTypeRegistry` + `ServerPlayNetworking`
- NeoForge：`Channel` + `ServerPayloadHandler`

**方案：** 网络包数据类放入 `common`，注册和处理放入各平台模块。

**差异 3：主入口 + 生命周期不同**
- Fabric：`ModInitializer` + `ClientModInitializer`
- NeoForge：`@Mod` + `IEventBus`

**方案：** 各平台模块各自实现入口类，`common` 不依赖任何加载器。

**差异 4：映射表不同**
- Fabric：Mojmap（官方映射）
- NeoForge：Parchment（Mojmap + 参数名）

**方案：** 统一使用 Mojmap 编译 `common` 模块。NeoForge 的 Parchment 是 Mojmap 的超集，兼容。

#### 2.3 构建配置

**根 settings.gradle：**
```groovy
rootProject.name = "AhhPvP"
include("common")
include("fabric")
include("neoforge")
```

**common/build.gradle（核心思路）：**
```groovy
plugins {
    id("java")
    id("net.fabricmc.loom") // 仅用于获取 Minecraft 依赖
}

loom {
    skipModCleanup = true
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings(loom.officialMojangMappings()) // 统一使用 Mojmap
}
```

**fabric/build.gradle：**
```groovy
dependencies {
    implementation(project(":common"))
    // Fabric API + Fabric Loom 插件
}
```

**neoforge/build.gradle：**
```groovy
plugins {
    id("net.neoforged.moddev")
}

dependencies {
    implementation(project(":common"))
    // NeoForge 依赖
}
```

---

## 实施步骤

### 阶段一：提取 common 模块（2-3 天）

```
1. 创建 common/build.gradle，配置仅依赖 Minecraft
2. 将以下代码从 dev 复制到 common:
   - src/main/java/com/aiden/pvp/blocks/   (全部)
   - src/main/java/com/aiden/pvp/entities/  (全部)
   - src/main/java/com/aiden/pvp/items/     (全部)
   - src/main/java/com/aiden/pvp/mixin/     (通用 mixin)
   - src/main/java/com/aiden/pvp/util/      (全部)
   - src/main/java/com/aiden/pvp/screen/    (全部)
   - src/main/java/com/aiden/pvp/commands/  (KitCommand)
   - src/main/java/com/aiden/pvp/gamerules/ (全部)
   - src/main/java/com/aiden/pvp/datagen/   (全部)
   - src/main/resources/                    (assets, data)
3. 将 ModBlocks / ModItems / ModEntityTypes 中的注册调用
   改为通过抽象接口调用
4. 编译验证 common 模块
```

### 阶段二：重建 Fabric 模块（1 天）

```
1. 创建 fabric/build.gradle
2. 将 PvP.java / PvPClient.java 移入 fabric
3. 将 payloads/ 移入 fabric
4. 将 ModMenu 兼容代码移入 fabric
5. 将 ConditionalItemModelPropertiesMixin 移入 fabric
6. 配置 fabric.mod.json 和 pvp.mixins.json
7. 编译并运行测试
```

### 阶段三：移植 NeoForge 模块（2-3 天）

```
1. 创建 neoforge/build.gradle
2. 从 neoforge-1.21.11-release 分支提取:
   - PvP.java (@Mod 版本)
   - 网络包处理器
3. 适配 common 的注册接口为 DeferredRegister
4. 配置 neoforge.mods.toml
5. 编译并运行测试
```

---

## 风险与应对

| 风险 | 影响 | 应对 |
|---|---|---|
| NeoForge 和 dev 的 MC 版本不同（1.21.11 vs 26.1.2） | common 中 API 可能不兼容 | 先以 dev 为准提取 common，NeoForge 模块暂锁定旧版本 |
| 跨平台注册抽象层增加复杂度 | 代码量增加，维护成本上升 | 先做 Fabric 模块验证，再扩展 |
| 构建时间变长 | 开发体验下降 | 只在需要时构建特定模块 |

---

## 不做合并的选项

如果不想投入这么多时间，**保持现状完全可行**：

- `dev` 是主力开发分支，继续在上面开发
- 旧版本 Fabric 分支和 NeoForge 分支按需维护，需要时 cherry-pick 关键修复
- 每次发版时手动同步到其他分支
- 等未来某个版本再做架构调整