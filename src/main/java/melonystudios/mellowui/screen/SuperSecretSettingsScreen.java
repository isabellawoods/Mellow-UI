package melonystudios.mellowui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.screen.list.PostEffectsList;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SuperSecretSettingsScreen extends Screen {
    private final Screen lastScreen;
    private PostEffectsList list;
    private Button doneButton;

    public SuperSecretSettingsScreen(Screen lastScreen) {
        super(new TranslatableComponent("menu.mellowui.super_secret_settings.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.list = new PostEffectsList(this.minecraft, this);
        this.addWidget(this.list);

        // Done button
        this.addRenderableWidget(this.doneButton = new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));

        this.list.setSelected(this.list.children().stream().filter(shader -> shader.effect().shaderIdentifier() == ShaderManager.CURRENT_EFFECT.shaderIdentifier()).findFirst().orElse(null));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<FormattedCharSequence> processors = tooltipAt(this.list, mouseX, mouseY);
        if (processors != null) this.renderTooltip(stack, processors, mouseX, mouseY);
    }

    @Nullable
    public static List<FormattedCharSequence> tooltipAt(PostEffectsList list, int mouseX, int mouseY) {
        Optional<PostEffectsList.Shader> shader = list.getMouseOver(mouseX, mouseY);
        if (shader.isPresent()) {
            MutableComponent component = shader.get().name().copy();
            component.append("\n").append(new TranslatableComponent(((TranslatableComponent) shader.get().name()).getKey() + ".desc").withStyle(ChatFormatting.GRAY));
            component.append("\n").append(new TextComponent(shader.get().effect().assetID().toString()).withStyle(ChatFormatting.DARK_GRAY));
            component.append(new TranslatableComponent("post_effect.identifier", shader.get().effect().shaderIdentifier()).withStyle(ChatFormatting.DARK_GRAY));
            return Minecraft.getInstance().font.split(component, RenderComponents.TOOLTIP_MAX_WIDTH);
        }
        return null;
    }

    public void updateButtonValidity() {
        this.doneButton.active = this.list.getSelected() != null;
    }

    public static void playRandomSound(Minecraft minecraft) {
        Random random = new Random();
        SoundEvent[] allSounds = ForgeRegistries.SOUND_EVENTS.getValues().toArray(new SoundEvent[0]);
        SoundEvent sound = allSounds[random.nextInt(allSounds.length)];
        float pitch = MellowUtils.randomBetween(random, 0.01F, 2);
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, 1));

        if (minecraft.getLaunchedVersion().contains("melony-studios-dev")) {
            MellowUI.LOGGER.debug("Played sound '{}' at {} pitch", sound.getLocation(), pitch);
        }
    }
}
