package melonystudios.mellowui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.TwoStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.renderer.LogoRenderer;
import melonystudios.mellowui.renderer.SplashRenderer;
import melonystudios.mellowui.screen.backport.AccessibilityOnboardingScreen;
import melonystudios.mellowui.screen.backport.AttributionsScreen;
import melonystudios.mellowui.screen.forge.MUIModUpdateScreen;
import melonystudios.mellowui.screen.widget.IconButton;
import melonystudios.mellowui.screen.widget.ImageSetButton;
import melonystudios.mellowui.screen.widget.WidgetTextureSet;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.io.IOException;

public class MellomedleyTitleScreen extends Screen implements InterfaceMethods.MainMenuMethods {
    private final boolean fading;
    private long fadeInStart;
    private int copyrightWidth;
    private int copyrightX;
    @Nullable
    private String splash;
    private NotificationModUpdateScreen modUpdateScreen;
    private boolean keepLogoThroughFade;

    public MellomedleyTitleScreen() {
        this(false, false);
    }

    public MellomedleyTitleScreen(boolean fading) {
        this(fading, false);
    }

    public MellomedleyTitleScreen(boolean fading, boolean keepLogoThroughFade) {
        super(new TranslationTextComponent("menu.mellomedley.title"));
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
            this.keepLogoThroughFade(true);
            this.minecraft.setScreen(new AccessibilityOnboardingScreen(() -> this.minecraft.setScreen(this)));
        }

        // Demo-dependent options
        if (this.minecraft.isDemo()) this.demoMenu();
        else this.defaultMenu();

        this.copyrightWidth = this.font.width(new TranslationTextComponent("menu.minecraft.credits"));
        this.copyrightX = this.width - this.copyrightWidth - 3;
        if (this.splash == null && !MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) this.splash = this.minecraft.getSplashManager().getSplash();

        // Options
        this.addButton(new Button(10, 158, 140, 20, new TranslationTextComponent("menu.options"),
                button -> this.minecraft.setScreen(MellowUtils.options(this, this.minecraft))));

        // Mods
        int modsOffset = 0;
        Button modsButton;
        if (MellowConfigs.CLIENT_CONFIGS.mellomedleyMainMenuModButton.get() == TwoStyles.OPTION_2 && !this.minecraft.isDemo()) {
            modsOffset += 24;
            this.addButton(modsButton = new Button(10, 182, 140, 20, new TranslationTextComponent("fml.menu.mods"),
                    button -> this.minecraft.setScreen(MellowUtils.modList(this))));
            this.modUpdateScreen = MUIModUpdateScreen.create(this.minecraft.screen, modsButton, false);
        }

        // Quit Game
        this.addButton(new Button(10, 182 + modsOffset, 140, 20, new TranslationTextComponent("menu.quit"),
                button -> this.minecraft.stop()));

        if (MellowConfigs.CLIENT_CONFIGS.mellomedleyMainMenuModButton.get() == TwoStyles.OPTION_2 || this.minecraft.isDemo()) {
            // Accessibility
            this.addButton(new ImageSetButton(58, 206 + modsOffset, 20, 20, GUITextures.ACCESSIBILITY_SET,
                    button -> this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options)), (button, stack, mouseX, mouseY) ->
                    MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("options.accessibility.title"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.accessibility")));

            // Language
            this.addButton(new ImageSetButton(82, 206 + modsOffset, 20, 20, GUITextures.LANGUAGE_SET,
                    button -> this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), (button, stack, mouseX, mouseY) ->
                    MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("options.language"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.language")));
        } else {
            // Accessibility
            this.addButton(new ImageSetButton(36, 206 + modsOffset, 20, 20, GUITextures.ACCESSIBILITY_SET,
                    button -> this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options)), (button, stack, mouseX, mouseY) ->
                    MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("options.accessibility.title"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.accessibility")));

            // Mods
            this.addButton(modsButton = new ImageSetButton(70, 206 + modsOffset, 20, 20,
                    GUITextures.MODS_SET, button -> this.minecraft.setScreen(MellowUtils.modList(this)), (button, stack, mouseX, mouseY) ->
                    MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("button.mellowui.mods.desc", ModList.get().getMods().size()), mouseX, mouseY),
                    new TranslationTextComponent("fml.menu.mods")));
            this.modUpdateScreen = MUIModUpdateScreen.create(this.minecraft.screen, modsButton, true);

            // Language
            this.addButton(new ImageSetButton(104, 206 + modsOffset, 20, 20, GUITextures.LANGUAGE_SET,
                    button -> this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), (button, stack, mouseX, mouseY) ->
                    MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("options.language"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.language")));
        }

        // Switch Style
        this.addButton(new IconButton(this.width - 20, 8, 12, 12, GUITextures.SWITCH_STYLE_SET, new TranslationTextComponent("button.mellowui.switch_style"),
                button -> WidgetTextureSet.switchTitleScreenStyle(), (button, stack, mouseX, mouseY) ->
                MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("button.mellowui.switch_style"), mouseX, mouseY)));
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
                LogManager.getLogger().warn("Failed to access demo world", exception);
            }
        }));
        resetDemoButton.active = demoWorldPresent;
    }

    private boolean demoWorldPresent() {
        try (SaveFormat.LevelSave demoWorldSource = this.minecraft.getLevelSource().createAccess("Demo_World")) {
            return demoWorldSource.getSummary() != null;
        } catch (IOException exception) {
            SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
            LogManager.getLogger().warn("Failed to read demo world data", exception);
            return false;
        }
    }

    private void confirmDemo(boolean confirmed) {
        if (confirmed) {
            try (SaveFormat.LevelSave demoWorldSource = this.minecraft.getLevelSource().createAccess("Demo_World")) {
                demoWorldSource.deleteLevel();
            } catch (IOException exception) {
                SystemToast.onWorldDeleteFailure(this.minecraft, "Demo_World");
                LogManager.getLogger().warn("Failed to delete demo world", exception);
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
        MellowUtils.renderPanorama(stack, partialTicks, this.width, this.height, this.fading ? overlayTransparency : 1);
        MellowUtils.renderBackgroundWithShaders(partialTicks);

        // Background Gradient
        RenderSystem.enableBlend();
        RenderSystem.color4f(1, 1, 1, buttonAlpha);
        this.minecraft.textureManager.bind(GUITextures.MAIN_MENU_GRADIENT);
        blit(stack, 0, 0, 0, 0, 220, this.height, 220, this.height);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableBlend();

        // Logo
        LogoRenderer.renderMellomedleyLogo(stack, 10, 16, 210, 75, buttonAlpha, this.keepLogoThroughFade);

        if ((textAlpha & 0xFC000000) != 0) {
            // Splashes
            if (!MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) SplashRenderer.mellomedleySplash(stack, this.font, this.splash, textAlpha);

            // Text
            boolean copyrightTextHovered = mouseX > this.copyrightX && mouseX < this.copyrightX + this.copyrightWidth && mouseY > this.height - 11 && mouseY < this.height;
            ITextComponent mellomedleyVersion = new TranslationTextComponent("menu.mellomedley.version.modpack");
            ITextComponent vanillaVersion = new TranslationTextComponent(this.minecraft.isDemo() ? "menu.mellomedley.version.vanilla_demo" : "menu.mellomedley.version.vanilla", SharedConstants.getCurrentVersion().getName(), ForgeVersion.getVersion());
            drawString(stack, this.font, vanillaVersion, this.width - this.font.width(vanillaVersion) - 3, this.height - 30, 0xFFFFFF | textAlpha);
            drawString(stack, this.font, mellomedleyVersion, this.width - this.font.width(mellomedleyVersion) - 3, this.height - 20, 0xFFFFFF | textAlpha);
            drawString(stack, this.font, new TranslationTextComponent("menu.minecraft.credits"), this.copyrightX, this.height - 10, MellowUtils.getSelectableTextColor(copyrightTextHovered, true) | textAlpha);
            if (copyrightTextHovered) {
                fill(stack, this.copyrightX, this.height - 2, this.copyrightX + this.copyrightWidth, this.height - 1, MellowUtils.getSelectableTextColor(true, true) | textAlpha);
                fill(stack, this.copyrightX + 1, this.height - 1, this.copyrightX + this.copyrightWidth + 1, this.height, MellowUtils.getSelectableTextShadowColor(true, true) | textAlpha);
            }

            for (Widget widget : this.buttons) widget.setAlpha(buttonAlpha);
            super.render(stack, mouseX, mouseY, partialTicks);
            if (!this.minecraft.isDemo() && this.modUpdateScreen != null) {
                ((MUIModUpdateScreen) this.modUpdateScreen).alpha = buttonAlpha;
                this.modUpdateScreen.render(stack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            if (mouseX > (double) this.copyrightX && mouseX < (double) (this.copyrightX + this.copyrightWidth) && mouseY > (double) (this.height - 10) && mouseY < (double) this.height) {
                this.minecraft.setScreen(new AttributionsScreen(this));
                this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            }
            return false;
        }
    }
}
