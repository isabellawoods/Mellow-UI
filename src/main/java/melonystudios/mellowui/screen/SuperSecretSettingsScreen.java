package melonystudios.mellowui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.screen.list.PostEffectsList;
import melonystudios.mellowui.util.ShaderManager;
import melonystudios.mellowui.util.debug.MUIDebuggingFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Random;

public class SuperSecretSettingsScreen extends Screen {
    private static final Marker MARKER = MarkerManager.getMarker("SuperSecretSettingsScreen");
    private final Screen lastScreen;
    private PostEffectsList list;
    private Button doneButton;

    // Search
    private EditBox searchBox;
    public String search = "";

    public SuperSecretSettingsScreen(Screen lastScreen) {
        super(new TranslatableComponent("menu.mellowui.super_secret_settings.title").withStyle(TextComponents.titleStyle()));
        this.lastScreen = lastScreen;
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.list = new PostEffectsList(this.minecraft, this);
        this.list.setSelected(this.list.children().stream().filter(shader -> shader.effect().assetID().equals(ShaderManager.CURRENT_EFFECT.assetID())).findFirst().orElse(null));
        this.addWidget(this.list);

        // Search box
        this.searchBox = new EditBox(this.font, this.width / 2 - 101, 16, 202, 14, TextComponents.searchText());
        this.searchBox.setFocus(false);
        this.searchBox.setCanLoseFocus(true);
        this.searchBox.setValue(this.search);
        this.searchBox.setResponder(value -> {
            this.search = value.trim();
            this.list.refreshList(value);
        });
        this.addWidget(this.searchBox);

        // Done button
        this.addRenderableWidget(this.doneButton = new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));

        this.list.setSelected(this.list.children().stream().filter(shader -> shader.effect().assetID().equals(ShaderManager.CURRENT_EFFECT.assetID())).findFirst().orElse(null));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.searchBox.render(stack, mouseX, mouseY, partialTicks);
        RenderComponents.INSTANCE.renderTextBoxSuggestion(this.searchBox, this.searchBox.getMessage());
        drawCenteredString(stack, this.font, this.title, this.width / 2, 5, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.list.renderTooltip(stack, this);
    }

    public void updateButtonValidity() {
        if (this.doneButton != null) this.doneButton.active = this.list.getSelected() != null;
    }

    public static void playRandomSound(Minecraft minecraft) {
        Random random = new Random();
        ResourceLocation[] allSounds = minecraft.getSoundManager().getAvailableSounds().toArray(new ResourceLocation[0]);
        ResourceLocation sound = allSounds[random.nextInt(allSounds.length)];
        float pitch = Mth.randomBetween(random, 0.01F, 2);
        minecraft.getSoundManager().play(new SimpleSoundInstance(sound, SoundSource.MASTER, MellowConfigs.CLIENT_CONFIGS.uiVolume.get().floatValue(), pitch, false, 0,
                SoundInstance.Attenuation.NONE, 0, 0, 0, true));

        if (MUIDebuggingFlags.DEBUG_LOG_SECRET_SETTINGS_SOUNDS) {
            MellowUI.LOGGER.debug(MARKER, TextComponents.translate("logger.mellowui.secret_settings_sound", "Played sound '%s' at pitch %s", sound, pitch));
        }
    }
}
