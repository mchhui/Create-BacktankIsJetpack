package mchhui.backtankisjetpack.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.config.ModConfig;
import mchhui.backtankisjetpack.BacktankIsJetpackConfig;

@OnlyIn(Dist.CLIENT)
public class BacktankIsJetpackConfigScreen extends Screen {

    private final Screen parent;
    private EditBox gasConsumptionEdit;
    private Checkbox immuneToFallDamageCheck;
    private Checkbox jetpackHeatsCheck;
    private static final int MAX_FIELD_WIDTH = 320;

    public BacktankIsJetpackConfigScreen(Screen parent) {
        super(Component.translatable("config.backtankisjetpack.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int pad = 8;
        int rowH = 24;
        int y = 40;
        int maxWidth = MAX_FIELD_WIDTH;

        gasConsumptionEdit = new EditBox(this.font, this.width / 2 - maxWidth / 2, y, maxWidth, 20,
                Component.translatable("config.backtankisjetpack.gasConsumption"));
        gasConsumptionEdit.setValue(String.valueOf(BacktankIsJetpackConfig.GAS_CONSUMPTION_PER_TICK.get()));
        gasConsumptionEdit.setHint(Component.literal("0.0 - 1000.0"));
        addRenderableWidget(gasConsumptionEdit);
        y += rowH + pad;

        immuneToFallDamageCheck = new Checkbox(this.width / 2 - maxWidth / 2, y, maxWidth, 20,
                Component.translatable("config.backtankisjetpack.immuneToFallDamage"),
                BacktankIsJetpackConfig.IMMUNE_TO_FALL_DAMAGE.get());
        addRenderableWidget(immuneToFallDamageCheck);
        y += rowH + pad;

        jetpackHeatsCheck = new Checkbox(this.width / 2 - maxWidth / 2, y, maxWidth, 20,
                Component.translatable("config.backtankisjetpack.jetpackHeatsWhenActive"),
                BacktankIsJetpackConfig.JETPACK_HEATS_WHEN_ACTIVE.get());
        addRenderableWidget(jetpackHeatsCheck);
        y += rowH + pad * 2;

        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> saveAndClose())
                .bounds(this.width / 2 - 100, y, 98, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .bounds(this.width / 2 + 2, y, 98, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderDirtBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int left = this.width / 2 - MAX_FIELD_WIDTH / 2;
        guiGraphics.drawString(this.font, Component.translatable("config.backtankisjetpack.gasConsumption"), left, 28, 0xAAAAAA, false);
        guiGraphics.drawString(this.font, Component.translatable("config.backtankisjetpack.gasConsumptionHint"), left, 40 + 20 + 4, 0x666666, false);
    }

    private void saveAndClose() {
        try {
            double gas = Double.parseDouble(gasConsumptionEdit.getValue().trim());
            gas = Math.max(0, Math.min(1000, gas));
            BacktankIsJetpackConfig.GAS_CONSUMPTION_PER_TICK.set(gas);
        } catch (NumberFormatException ignored) {
            // keep current
        }
        BacktankIsJetpackConfig.IMMUNE_TO_FALL_DAMAGE.set(immuneToFallDamageCheck.selected());
        BacktankIsJetpackConfig.JETPACK_HEATS_WHEN_ACTIVE.set(jetpackHeatsCheck.selected());
        BacktankIsJetpackConfig.saveCommonConfig();
        onClose();
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }
}
