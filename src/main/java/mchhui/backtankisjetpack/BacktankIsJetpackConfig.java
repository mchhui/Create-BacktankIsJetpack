package mchhui.backtankisjetpack;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class BacktankIsJetpackConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue GAS_CONSUMPTION_PER_TICK;
    public static final ForgeConfigSpec.BooleanValue IMMUNE_TO_FALL_DAMAGE;
    public static final ForgeConfigSpec.BooleanValue JETPACK_HEATS_WHEN_ACTIVE;

    /** 用于 GUI 保存时写入文件，在 ModConfig 加载时设置 */
    static volatile ModConfig commonConfigInstance;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("BacktankIsJetpack server config");

        builder.push("gameplay");

        GAS_CONSUMPTION_PER_TICK = builder
                .comment("每 tick 气体消耗量 (Gas consumption per tick)")
                .defineInRange("gasConsumptionPerTick", 0.3, 0.0, 1000.0);

        IMMUNE_TO_FALL_DAMAGE = builder
                .comment("免疫掉落伤害 (Immune to fall damage when using jetpack)")
                .define("immuneToFallDamage", false);

        JETPACK_HEATS_WHEN_ACTIVE = builder
                .comment("喷气时发热，可与意志坚定等模组联动 (Jetpack heats up when active, e.g. Tough As Nails)")
                .define("jetpackHeatsWhenActive", true);

        builder.pop();

        SPEC = builder.build();
    }

    /** 每 tick 气体消耗量 */
    public static float getGasConsumptionPerTick() {
        return GAS_CONSUMPTION_PER_TICK.get().floatValue();
    }

    /** 免疫掉落伤害 */
    public static boolean isImmuneToFallDamage() {
        return IMMUNE_TO_FALL_DAMAGE.get();
    }

    /** 喷气时发热 */
    public static boolean isJetpackHeatsWhenActive() {
        return JETPACK_HEATS_WHEN_ACTIVE.get();
    }

    /** 由配置界面调用，将当前值写回 TOML */
    public static void saveCommonConfig() {
        if (commonConfigInstance != null) {
            commonConfigInstance.save();
        }
    }
}
