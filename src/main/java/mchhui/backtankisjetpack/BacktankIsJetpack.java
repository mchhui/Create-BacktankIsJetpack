package mchhui.backtankisjetpack;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;

@Mod("backtankisjetpack")
public class BacktankIsJetpack {

    public BacktankIsJetpack(FMLJavaModLoadingContext context) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BacktankIsJetpackConfig.SPEC, "backtankisjetpack.toml");
        context.getModEventBus().addListener(ServerHandler::onCommonSetup);
        context.getModEventBus().addListener(BacktankIsJetpack::onConfigLoaded);
    }

    private static void onConfigLoaded(ModConfigEvent.Loading event) {
        ModConfig cfg = event.getConfig();
        if ("backtankisjetpack".equals(cfg.getModId()) && cfg.getType() == ModConfig.Type.COMMON) {
            BacktankIsJetpackConfig.commonConfigInstance = cfg;
        }
    }
}
