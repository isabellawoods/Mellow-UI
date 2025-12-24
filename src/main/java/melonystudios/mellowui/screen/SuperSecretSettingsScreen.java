package melonystudios.mellowui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.screen.list.PostEffectsList;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.DebuggingFlags;
import melonystudios.mellowui.util.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.*;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Random;

public class SuperSecretSettingsScreen extends Screen {
    private static final Marker MARKER = MarkerManager.getMarker("SuperSecretSettingsScreen");
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Screen lastScreen;
    private PostEffectsList list;
    private Button doneButton;

    // Search
    private TextFieldWidget searchBox;
    public String search = "";

    public SuperSecretSettingsScreen(Screen lastScreen) {
        super(new TranslationTextComponent("menu.mellowui.super_secret_settings.title").withStyle(TextComponents.titleStyle()));
        this.lastScreen = lastScreen;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.list = new PostEffectsList(this);
        this.list.setSelected(this.list.children().stream()
                .filter(shader -> shader.effect().assetID().equals(ShaderManager.CURRENT_EFFECT.assetID()))
                .findFirst().orElse(null));
        this.children.add(this.list);

        // Search box
        this.searchBox = new TextFieldWidget(this.font, this.width / 2 - 100, 17, 200, 15, TextComponents.searchText());
        this.searchBox.setFocus(false);
        this.searchBox.setCanLoseFocus(true);
        this.searchBox.setValue(this.search);
        this.searchBox.setResponder(value -> {
            this.search = value.trim();
            this.list.refreshList(value);
        });
        this.addWidget(this.searchBox);

        // Done button
        this.doneButton = WidgetComponents.components(this, this::addButton).done(Alignment.CENTER);

        this.setInitialFocus(this.searchBox);
        this.list.setSelected(this.list.children().stream().filter(shader -> shader.effect().assetID().equals(ShaderManager.CURRENT_EFFECT.assetID())).findFirst().orElse(null));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.searchBox.render(stack, mouseX, mouseY, partialTicks);
        this.components.renderTextBoxSuggestion(this.searchBox, this.searchBox.getMessage());
        this.components.drawTitle(this.title, this.width, 5);
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
        float pitch = randomBetween(random, 0.01F, 2);
        minecraft.getSoundManager().play(new SimpleSound(sound, SoundCategory.MASTER, MellowConfigs.CLIENT_CONFIGS.uiVolume.get().floatValue(), pitch, false, 0,
                ISound.AttenuationType.NONE, 0, 0, 0, true));

        if (DebuggingFlags.DEBUG_LOG_SECRET_SETTINGS_SOUNDS) {
            MellowUI.LOGGER.debug(MARKER, TextComponents.translate("logger.mellowui.secret_settings_sound", "Played sound '%s' at pitch %s", sound, pitch));
        }
    }

    public static float randomBetween(Random rand, float minimum, float maximum) {
        return rand.nextFloat() * (maximum - minimum) + minimum;
    }
}
