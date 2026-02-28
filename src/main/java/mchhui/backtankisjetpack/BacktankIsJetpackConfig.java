package mchhui.backtankisjetpack;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class BacktankIsJetpackConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue GAS_CONSUMPTION_PER_TICK;
    public static final ModConfigSpec.BooleanValue IMMUNE_TO_FALL_DAMAGE;
    public static final ModConfigSpec.BooleanValue JETPACK_HEATS_WHEN_ACTIVE;

    /** 用于 GUI 保存时写入文件，在 ModConfig 加载时设置 */
    static volatile ModConfig commonConfigInstance;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("BacktankIsJetpack server config");

        builder.push("gameplay");

        GAS_CONSUMPTION_PER_TICK = builder
                .comment("每 tick 的平均气体消耗量 (Average gas consumption per tick)")
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
    public static double getGasConsumptionPerTick() {
        return GAS_CONSUMPTION_PER_TICK.get().doubleValue();
    }

    /** 免疫掉落伤害 */
    public static boolean isImmuneToFallDamage() {
        return IMMUNE_TO_FALL_DAMAGE.get();
    }

    /** 喷气时发热 */
    public static boolean isJetpackHeatsWhenActive() {
        return JETPACK_HEATS_WHEN_ACTIVE.get();
    }
}
