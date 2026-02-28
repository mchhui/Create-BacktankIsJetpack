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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;

public class ServerHandler {
    private static HashSet<UUID> flightPlayers = new HashSet<>();

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(ServerHandler::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(ServerHandler::onPlayerFlyableFall);
        if(ModList.get().isLoaded("toughasnails")) {
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
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.player.level().isClientSide()) {
            return;
        }
        if (event.phase == Phase.END) {
            return;
        }
        var chest = event.player.getInventory().getArmor(2);
        if (!event.player.isCreative() && !event.player.isSpectator()) {
            if (!event.player.getAbilities().mayfly) {
                if (chest != null && chest.getItem() instanceof BacktankItem item) {
                    event.player.getAbilities().mayfly = true;
                    event.player.onUpdateAbilities();
                    flightPlayers.add(event.player.getUUID());
                }
            }
        }
        if (flightPlayers.contains(event.player.getUUID())) {
            if (chest == null || chest.isEmpty() || !(chest.getItem() instanceof BacktankItem item)) {
                if (!event.player.isCreative() && !event.player.isSpectator()) {
                    event.player.getAbilities().mayfly = false;
                    event.player.getAbilities().flying = false;
                    event.player.onUpdateAbilities();
                }
                flightPlayers.remove(event.player.getUUID());
            } else {
                if (!BacktankUtil.hasAirRemaining(chest) && event.player.getAbilities().flying) {
                    event.player.getAbilities().flying = false;
                    event.player.onUpdateAbilities();
                }
                if (event.player.getAbilities().flying) {
                    if (event.player.hasEffect(MobEffects.INVISIBILITY)/*||event.player.hasEffect(MobEffectRegistry.TRUE_INVISIBILITY.get())*/) {
                        event.player.removeEffect(MobEffects.INVISIBILITY);
                        //event.player.removeEffect(MobEffectRegistry.TRUE_INVISIBILITY.get());
                    }
                    BacktankUtil.consumeAir(event.player, chest, BacktankIsJetpackConfig.getGasConsumptionPerTick());
                    event.player.fallDistance = 0;
                    if (event.player.level() instanceof ServerLevel serverLevel) {
                        Vec3 look = event.player.getLookAngle();
                        Vec3 behind = look.reverse();
                        double px = event.player.getX() + behind.x * 0.4D;
                        double py = event.player.getY() + 0.6D + behind.y * 0.4D;
                        double pz = event.player.getZ() + behind.z * 0.4D;
                        serverLevel.sendParticles(ParticleTypes.CLOUD, px, py, pz, 1, 0.02, 0.02, 0.02, 0.01);
                        if (event.player.tickCount % 10 == 0) {
                            serverLevel.playSound(null, event.player.getX(), event.player.getY(), event.player.getZ(), AllSoundEvents.STEAM.getMainEvent(), SoundSource.PLAYERS, 1F, 0.5F);
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
