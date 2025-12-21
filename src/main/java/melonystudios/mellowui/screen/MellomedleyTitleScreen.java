package melonystudios.mellowui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.TwoStyles;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.ImageSetButton;
import melonystudios.mellowui.element.widget.ImageSetModButton;
import melonystudios.mellowui.element.widget.ModButton;
import melonystudios.mellowui.element.widget.text.PlainTextButton;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.renderer.LogoRenderer;
import melonystudios.mellowui.renderer.SplashRenderer;
import melonystudios.mellowui.screen.backport.AccessibilityOnboardingScreen;
import melonystudios.mellowui.screen.backport.CreditsAndAttributionsScreen;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;

public class MellomedleyTitleScreen extends Screen implements InterfaceMethods.TitleScreenMethods {
    private static final Marker MARKER = MarkerManager.getMarker("MellomedleyTitleScreen");
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final boolean fading;
    private long fadeInStart;
    @Nullable
    private String splash;
    private boolean keepLogoThroughFade;

    public MellomedleyTitleScreen() {
        this(false, false);
    }

    public MellomedleyTitleScreen(boolean fading, boolean keepLogoThroughFade) {
        super(new TranslationTextComponent("menu.mellomedley.title").withStyle(TextComponents.titleStyle()));
        this.fading = fading;
        this.keepLogoThroughFade = keepLogoThroughFade;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keepsLogoThroughFade() {
        return this.keepLogoThroughFade;
    }

    @Override
    public void keepLogoThroughFade(boolean keep) {
        this.keepLogoThroughFade = keep;
    }

    @Override
    public void init() {
        // Open accessibility onboarding if it hasn't been shown.
        if (MellowConfigs.CLIENT_CONFIGS.onboardAccessibility.get()) {
            this.minecraft.setScreen(new AccessibilityOnboardingScreen(() -> this.minecraft.setScreen(this)));
            return;
        }
        LogoRenderer.rerollEasterEgg();

        // Demo-dependent options
        if (this.minecraft.isDemo()) this.demoMenu();
        else this.defaultMenu();

        int copyrightWidth = this.font.width(new TranslationTextComponent("menu.minecraft.credits"));
        if (this.splash == null && !MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) this.splash = this.minecraft.getSplashManager().getSplash();

        // Options
        this.addButton(new Button(10, 158, 140, 20, new TranslationTextComponent("menu.options"),
                button -> this.minecraft.setScreen(MellowUtils.options(this, this.minecraft))));

        // Mods
        int modsOffset = 0;
        if (MellowConfigs.CLIENT_CONFIGS.mellomedleyMainMenuModButton.get() == TwoStyles.OPTION_2 && !this.minecraft.isDemo()) {
            modsOffset += 24;
            this.addButton(new ModButton(10, 182, 140, 20, new TranslationTextComponent("fml.menu.mods"),
                    button -> this.minecraft.setScreen(MellowUtils.modList(this))));
        }

        // Quit Game
        this.addButton(new Button(10, 182 + modsOffset, 140, 20, new TranslationTextComponent("menu.quit"),
                button -> this.minecraft.stop()));

        if (MellowConfigs.CLIENT_CONFIGS.mellomedleyMainMenuModButton.get() == TwoStyles.OPTION_2 || this.minecraft.isDemo()) {
            // Accessibility
            this.addButton(new ImageSetButton(58, 206 + modsOffset, 20, 20, GUITextures.ACCESSIBILITY_SET,
                    button -> this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options)), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslationTextComponent("options.accessibility.title"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.accessibility")));

            // Language
            this.addButton(new ImageSetButton(82, 206 + modsOffset, 20, 20, GUITextures.LANGUAGE_SET,
                    button -> this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslationTextComponent("options.language"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.language")));
        } else {
            // Accessibility
            this.addButton(new ImageSetButton(36, 206 + modsOffset, 20, 20, GUITextures.ACCESSIBILITY_SET,
                    button -> this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options)), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslationTextComponent("options.accessibility.title"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.accessibility")));

            // Mods
            this.addButton(new ImageSetModButton(70, 206 + modsOffset, 20, 20,
                    GUITextures.MODS_SET, button -> this.minecraft.setScreen(MellowUtils.modList(this)), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslationTextComponent("button.mellowui.mods.tooltip", ModList.get().getMods().size()), mouseX, mouseY),
                    new TranslationTextComponent("fml.menu.mods")).renderOnCorner(true));

            // Language
            this.addButton(new ImageSetButton(104, 206 + modsOffset, 20, 20, GUITextures.LANGUAGE_SET,
                    button -> this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslationTextComponent("options.language"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.language")));
        }

        // Copyright text
        this.addButton(new PlainTextButton(this.width - copyrightWidth - 2, this.height - 10, copyrightWidth, 10,
                new TranslationTextComponent("menu.minecraft.credits"), button -> this.minecraft.setScreen(new CreditsAndAttributionsScreen(this)), this.font));

        // Switch Style
        this.addButton(this.components.switchStyle(button -> MellowUtils.switchTitleScreenStyle(this.minecraft), this.width - 21, 8));

        // Customize
        this.addButton(this.components.customize(button -> this.minecraft.setScreen(new MellowCustomizationScreen(this, this.minecraft.options)), this.width - 21, 21));
    }

    private void defaultMenu() {
        // Singleplayer
        this.addButton(new Button(10, 110, 140, 20, new TranslationTextComponent("menu.singleplayer"),
                button -> this.minecraft.setScreen(new WorldSelectionScreen(this))));

        boolean allowsMultiplayer = this.minecraft.allowsMultiplayer();
        Button.ITooltip multiplayerTooltip = allowsMultiplayer ? Button.NO_TOOLTIP : (button, stack, mouseX, mouseY) -> {
            if (!button.active) this.renderTooltip(stack, this.minecraft.font.split(new TranslationTextComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
        };

        // Multiplayer
        this.addButton(new Button(10, 134, 140, 20, new TranslationTextComponent("menu.multiplayer"), button -> {
            Screen multiplayerScreen = this.minecraft.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.minecraft.setScreen(multiplayerScreen);
        }, multiplayerTooltip)).active = allowsMultiplayer;
    }

    private void demoMenu() {
        boolean demoWorldPresent = this.demoWorldPresent();

        // Play Demo World
        this.addButton(new Button(10, 110, 140, 20, new TranslationTextComponent("menu.playdemo"),button -> {
            if (demoWorldPresent) {
                this.minecraft.loadLevel("Demo_World");
            } else {
                DynamicRegistries.Impl registries = DynamicRegistries.builtin();
                this.minecraft.createLevel("Demo_World", MinecraftServer.DEMO_SETTINGS, registries, DimensionGeneratorSettings.demoSettings(registries));
            }
        }));

        // Reset Demo World
        Button resetDemoButton = this.addButton(new Button(10, 134, 140, 20, new TranslationTextComponent("menu.resetdemo"), button -> {
            SaveFormat worldSource = this.minecraft.getLevelSource();

            try (SaveFormat.LevelSave demoWorldSource = worldSource.createAccess("Demo_World")) {
                WorldSummary summary = demoWorldSource.getSummary();
                if (summary != null) {
                    this.minecraft.setScreen(new ConfirmScreen(this::confirmDemo, new TranslationTextComponent("selectWorld.deleteQuestion"), new TranslationTextComponent("selectWorld.deleteWarning", summary.getLevelName()), new TranslationTextComponent("selectWorld.deleteButton"), DialogTexts.GUI_CANCEL));
                }
            } catch (IOException exception) {
                SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
                MellowUI.LOGGER.warn(MARKER, "Failed to access demo world", exception);
            }
        }));
        resetDemoButton.active = demoWorldPresent;
    }

    private boolean demoWorldPresent() {
        try (SaveFormat.LevelSave demoWorldSource = this.minecraft.getLevelSource().createAccess("Demo_World")) {
            return demoWorldSource.getSummary() != null;
        } catch (IOException exception) {
            SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
            MellowUI.LOGGER.warn(MARKER, "Failed to read demo world data", exception);
            return false;
        }
    }

    private void confirmDemo(boolean confirmed) {
        if (confirmed) {
            try (SaveFormat.LevelSave demoWorldSource = this.minecraft.getLevelSource().createAccess("Demo_World")) {
                demoWorldSource.deleteLevel();
            } catch (IOException exception) {
                SystemToast.onWorldDeleteFailure(this.minecraft, "Demo_World");
                MellowUI.LOGGER.warn(MARKER, "Failed to delete demo world", exception);
            }
        }

        this.minecraft.setScreen(this);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.fadeInStart == 0L && this.fading) this.fadeInStart = Util.getMillis();
        float fade = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000 : 1;
        float overlayTransparency = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000 : 1;
        float buttonAlpha = this.fading ? MathHelper.clamp(fade - 1, 0, 1) : 1;
        int textAlpha = MathHelper.ceil(buttonAlpha * 255) << 24;
        // Background
        this.components.renderPanorama(partialTicks, this.width, this.height, this.fading ? overlayTransparency : 1);
        if (MellowConfigs.CLIENT_CONFIGS.fadingBlur.get()) this.components.renderBlurredBackground(partialTicks, false);
        else this.components.renderBackgroundShaders(partialTicks);

        // Background Gradient
        RenderSystem.enableBlend();
        RenderSystem.color4f(1, 1, 1, buttonAlpha);
        this.minecraft.getTextureManager().bind(GUITextures.TITLE_SCREEN_GRADIENT_LIGHT);
        blit(stack, 0, 0, 0, 0, 220, this.height, 220, this.height);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableBlend();

        // Logo
        LogoRenderer.renderMellomedleyLogo(stack, 10, 20, 194, 75, buttonAlpha, this.keepLogoThroughFade);

        if ((textAlpha & 0xFC000000) != 0) {
            int textColor = TextComponents.selectableColor(false, true);

            // Forge's beta warning
            this.components.renderForgeBetaText(this.width, 3, textColor, textAlpha);

            // Title screen icons background
            this.components.renderTitleScreenIconsBackground(this.width - 22, 7, buttonAlpha);

            // Splashes
            if (!MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) SplashRenderer.mellomedleySplash(stack, this.font, this.splash, textAlpha);

            // Text
            ITextComponent mellomedleyVersion = new TranslationTextComponent("menu.mellomedley.version.modpack", MellowConfigs.CLIENT_CONFIGS.mellomedleyVersion.get());
            ITextComponent vanillaVersion = new TranslationTextComponent(this.minecraft.isDemo() ? "menu.mellomedley.version.vanilla_demo" : "menu.mellomedley.version.vanilla", SharedConstants.getCurrentVersion().getName(), ForgeVersion.getVersion());
            drawString(stack, this.font, mellomedleyVersion, this.width - this.font.width(mellomedleyVersion) - 2, this.height - 30, textColor | textAlpha);
            drawString(stack, this.font, vanillaVersion, this.width - this.font.width(vanillaVersion) - 2, this.height - 20, textColor | textAlpha);

            for (Widget widget : this.buttons) widget.setAlpha(buttonAlpha);
            super.render(stack, mouseX, mouseY, partialTicks);
        }
    }
}
