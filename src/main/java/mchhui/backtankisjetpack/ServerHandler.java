package mchhui.backtankisjetpack;

import java.util.HashSet;
import java.util.UUID;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerFlyableFallEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;

public class ServerHandler {
    private static HashSet<UUID> flightPlayers = new HashSet<>();

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(ServerHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(ServerHandler::onPlayerFlyableFall);
        if (ModList.get().isLoaded("toughasnails")) {
            compactTAN();
        }
    }

    private static void compactTAN() {
        TemperatureHelper.registerPlayerTemperatureModifier((Player paramPlayer, TemperatureLevel current) -> {
            if (BacktankIsJetpackConfig.isJetpackHeatsWhenActive()) {
                if (ServerHandler.isBacktankFlying(paramPlayer)) {
                    return current.increment(1);
                }
            }
            return current;
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        var player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        var chest = player.getInventory().getArmor(2);
        if (!player.isCreative() && !player.isSpectator()) {
            if (!player.getAbilities().mayfly) {
                if (chest != null && chest.getItem() instanceof BacktankItem item) {
                    player.getAbilities().mayfly = true;
                    player.onUpdateAbilities();
                    flightPlayers.add(player.getUUID());
                }
            }
        }
        if (flightPlayers.contains(player.getUUID())) {
            if (chest == null || chest.isEmpty() || !(chest.getItem() instanceof BacktankItem item)) {
                if (!player.isCreative() && !player.isSpectator()) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                }
                flightPlayers.remove(player.getUUID());
            } else {
                if (!BacktankUtil.hasAirRemaining(chest) && player.getAbilities().flying) {
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                }
                if (player.getAbilities().flying) {
                    if (player.hasEffect(MobEffects.INVISIBILITY)/*||player.hasEffect(MobEffectRegistry.TRUE_INVISIBILITY.get())*/) {
                        player.removeEffect(MobEffects.INVISIBILITY);
                        //player.removeEffect(MobEffectRegistry.TRUE_INVISIBILITY.get());
                    }
                    double gpt = BacktankIsJetpackConfig.getGasConsumptionPerTick();
                    if (gpt < 1) {
                        if (Math.random() < gpt) {
                            BacktankUtil.consumeAir(player, chest, 1);
                        }
                    } else {
                        BacktankUtil.consumeAir(player, chest, (int)gpt);
                    }
                    player.fallDistance = 0;
                    if (player.level() instanceof ServerLevel serverLevel) {
                        Vec3 look = player.getLookAngle();
                        Vec3 behind = look.reverse();
                        double px = player.getX() + behind.x * 0.4D;
                        double py = player.getY() + 0.6D + behind.y * 0.4D;
                        double pz = player.getZ() + behind.z * 0.4D;
                        serverLevel.sendParticles(ParticleTypes.CLOUD, px, py, pz, 1, 0.02, 0.02, 0.02, 0.01);
                        if (player.tickCount % 10 == 0) {
                            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), AllSoundEvents.STEAM.getMainEvent(), SoundSource.PLAYERS, 1F, 0.5F);
                        }
                    }
                }
            }
        }
    }

    public static boolean isBacktankFlying(Player player) {
        return flightPlayers.contains(player.getUUID()) && player.getAbilities().flying;
    }

    @SubscribeEvent
    public static void onPlayerFlyableFall(PlayerFlyableFallEvent event) {
        if (BacktankIsJetpackConfig.isImmuneToFallDamage()) {
            return;
        }
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (flightPlayers.contains(event.getEntity().getUUID())) {
            if (!event.getEntity().isCreative() && !event.getEntity().isSpectator()) {
                event.getEntity().getAbilities().mayfly = false;
                event.getEntity().getAbilities().flying = false;
                event.getEntity().onUpdateAbilities();
            }
            flightPlayers.remove(event.getEntity().getUUID());
            event.getEntity().causeFallDamage(event.getDistance(), 1, event.getEntity().damageSources().fall());
        }
    }
}
