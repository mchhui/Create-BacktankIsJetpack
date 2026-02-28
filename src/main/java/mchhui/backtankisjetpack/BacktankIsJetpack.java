package mchhui.backtankisjetpack;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

@Mod(value = "backtankisjetpack", dist = {Dist.CLIENT})
public class BacktankIsJetpack {
    public BacktankIsJetpack(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, BacktankIsJetpackConfig.SPEC, "backtankisjetpack.toml");
        modEventBus.addListener(ServerHandler::onCommonSetup);
        modEventBus.addListener(BacktankIsJetpack::onConfigLoaded);
    }

    private static void onConfigLoaded(ModConfigEvent.Loading event) {
        ModConfig cfg = event.getConfig();
        if ("backtankisjetpack".equals(cfg.getModId()) && cfg.getType() == ModConfig.Type.COMMON) {
            BacktankIsJetpackConfig.commonConfigInstance = cfg;
        }
    }
}
