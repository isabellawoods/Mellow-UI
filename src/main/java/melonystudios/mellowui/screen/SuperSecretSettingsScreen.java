package melonystudios.mellowui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.list.PostEffectsList;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SuperSecretSettingsScreen extends Screen {
    private final Screen lastScreen;
    private PostEffectsList list;
    private Button doneButton;

    public SuperSecretSettingsScreen(Screen lastScreen) {
        super(new TranslationTextComponent("menu.mellowui.super_secret_settings.title"));
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
        this.children.add(this.list);

        // Done button
        this.addButton(this.doneButton = new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));

        this.list.setSelected(this.list.children().stream().filter(shader -> shader.effect().shaderIdentifier() == ShaderManager.CURRENT_EFFECT.shaderIdentifier()).findFirst().orElse(null));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> processors = tooltipAt(this.list, mouseX, mouseY);
        if (processors != null) this.renderTooltip(stack, processors, mouseX, mouseY);
    }

    @Nullable
    public static List<IReorderingProcessor> tooltipAt(PostEffectsList list, int mouseX, int mouseY) {
        Optional<PostEffectsList.Shader> shader = list.getMouseOver(mouseX, mouseY);
        if (shader.isPresent()) {
            IFormattableTextComponent component = shader.get().name().copy();
            component.append("\n").append(new TranslationTextComponent(((TranslationTextComponent) shader.get().name()).getKey() + ".desc").withStyle(TextFormatting.GRAY));
            component.append("\n").append(new StringTextComponent(shader.get().effect().assetID().toString()).withStyle(TextFormatting.DARK_GRAY));
            component.append(new TranslationTextComponent("post_effect.identifier", shader.get().effect().shaderIdentifier()).withStyle(TextFormatting.DARK_GRAY));
            return Minecraft.getInstance().font.split(component, MellowUtils.TOOLTIP_MAX_WIDTH);
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
        minecraft.getSoundManager().play(SimpleSound.forUI(sound, pitch, 1));

        if (minecraft.getLaunchedVersion().contains("melony-studios-dev")) {
            LogManager.getLogger().debug("Played sound '{}' at {} pitch", sound.getLocation(), pitch);
        }
    }
}
