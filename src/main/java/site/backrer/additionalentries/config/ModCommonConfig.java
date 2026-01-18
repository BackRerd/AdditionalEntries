package site.backrer.additionalentries.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ModCommonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue ENTRY_REFORGE_SUCCESS_RATE;
    public static final ForgeConfigSpec.DoubleValue QUALITY_REFORGE_SUCCESS_RATE;

    static {
        BUILDER.push("重铸设置");

        ENTRY_REFORGE_SUCCESS_RATE = BUILDER
                .comment("词条重铸的成功率 (0.0 到 1.0)")
                .defineInRange("entryReforgeSuccessRate", 0.5, 0.0, 1.0);

        QUALITY_REFORGE_SUCCESS_RATE = BUILDER
                .comment("品质重铸的成功率 (0.0 到 1.0)")
                .defineInRange("qualityReforgeSuccessRate", 0.5, 0.0, 1.0);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}
