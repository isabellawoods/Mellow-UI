package melonystudios.mellowui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.TwoStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.renderer.LogoRenderer;
import melonystudios.mellowui.renderer.SplashRenderer;
import melonystudios.mellowui.screen.backport.AccessibilityOnboardingScreen;
import melonystudios.mellowui.screen.backport.AttributionsScreen;
import melonystudios.mellowui.screen.widget.ImageSetButton;
import melonystudios.mellowui.screen.widget.ImageSetModButton;
import melonystudios.mellowui.screen.widget.ModButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.versions.forge.ForgeVersion;

import javax.annotation.Nullable;
import java.io.IOException;

public class MellomedleyTitleScreen extends Screen implements InterfaceMethods.TitleScreenMethods {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final boolean fading;
    private long fadeInStart;
    private int copyrightWidth;
    private int copyrightX;
    @Nullable
    private String splash;
    private boolean keepLogoThroughFade;

    public MellomedleyTitleScreen() {
        this(false, false);
    }

    public MellomedleyTitleScreen(boolean fading, boolean keepLogoThroughFade) {
        super(new TranslatableComponent("menu.mellomedley.title"));
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

        this.copyrightWidth = this.font.width(new TranslatableComponent("menu.minecraft.credits"));
        this.copyrightX = this.width - this.copyrightWidth - 2;
        if (this.splash == null && !MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) this.splash = this.minecraft.getSplashManager().getSplash();

        // Options
        this.addRenderableWidget(new Button(10, 158, 140, 20, new TranslatableComponent("menu.options"),
                button -> this.minecraft.setScreen(MellowUtils.options(this, this.minecraft))));

        // Mods
        int modsOffset = 0;
        if (MellowConfigs.CLIENT_CONFIGS.mellomedleyMainMenuModButton.get() == TwoStyles.OPTION_2 && !this.minecraft.isDemo()) {
            modsOffset += 24;
            this.addRenderableWidget(new ModButton(10, 182, 140, 20, new TranslatableComponent("fml.menu.mods"),
                    button -> this.minecraft.setScreen(MellowUtils.modList(this))));
        }

        // Quit Game
        this.addRenderableWidget(new Button(10, 182 + modsOffset, 140, 20, new TranslatableComponent("menu.quit"),
                button -> this.minecraft.stop()));

        if (MellowConfigs.CLIENT_CONFIGS.mellomedleyMainMenuModButton.get() == TwoStyles.OPTION_2 || this.minecraft.isDemo()) {
            // Accessibility
            this.addRenderableWidget(new ImageSetButton(58, 206 + modsOffset, 20, 20, GUITextures.ACCESSIBILITY_SET,
                    button -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslatableComponent("options.accessibility.title"), mouseX, mouseY),
                    new TranslatableComponent("narrator.button.accessibility")));

            // Language
            this.addRenderableWidget(new ImageSetButton(82, 206 + modsOffset, 20, 20, GUITextures.LANGUAGE_SET,
                    button -> this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslatableComponent("options.language"), mouseX, mouseY),
                    new TranslatableComponent("narrator.button.language")));
        } else {
            // Accessibility
            this.addRenderableWidget(new ImageSetButton(36, 206 + modsOffset, 20, 20, GUITextures.ACCESSIBILITY_SET,
                    button -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslatableComponent("options.accessibility.title"), mouseX, mouseY),
                    new TranslatableComponent("narrator.button.accessibility")));

            // Mods
            this.addRenderableWidget(new ImageSetModButton(70, 206 + modsOffset, 20, 20,
                    GUITextures.MODS_SET, button -> this.minecraft.setScreen(MellowUtils.modList(this)), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslatableComponent("button.mellowui.mods.desc", ModList.get().getMods().size()), mouseX, mouseY),
                    new TranslatableComponent("fml.menu.mods")).renderOnCorner(true));

            // Language
            this.addRenderableWidget(new ImageSetButton(104, 206 + modsOffset, 20, 20, GUITextures.LANGUAGE_SET,
                    button -> this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslatableComponent("options.language"), mouseX, mouseY),
                    new TranslatableComponent("narrator.button.language")));
        }

        // Switch Style
        this.addRenderableWidget(this.components.switchStyle(button -> MellowUtils.switchTitleScreenStyle(this.minecraft), this, this.width - 20, 8));
    }

    private void defaultMenu() {
        // Singleplayer
        this.addRenderableWidget(new Button(10, 110, 140, 20, new TranslatableComponent("menu.singleplayer"),
                button -> this.minecraft.setScreen(new SelectWorldScreen(this))));

        boolean allowsMultiplayer = this.minecraft.allowsMultiplayer();
        Button.OnTooltip multiplayerTooltip = allowsMultiplayer ? Button.NO_TOOLTIP : (button, stack, mouseX, mouseY) -> {
            if (!button.active) this.renderTooltip(stack, this.minecraft.font.split(new TranslatableComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
        };

        // Multiplayer
        this.addRenderableWidget(new Button(10, 134, 140, 20, new TranslatableComponent("menu.multiplayer"), button -> {
            Screen multiplayerScreen = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
            this.minecraft.setScreen(multiplayerScreen);
        }, multiplayerTooltip)).active = allowsMultiplayer;
    }

    private void demoMenu() {
        boolean demoWorldPresent = this.demoWorldPresent();

        // Play Demo World
        this.addRenderableWidget(new Button(10, 110, 140, 20, new TranslatableComponent("menu.playdemo"),button -> {
            if (demoWorldPresent) {
                this.minecraft.loadLevel("Demo_World");
            } else {
                RegistryAccess.Frozen registries = RegistryAccess.BUILTIN.get();
                this.minecraft.createLevel("Demo_World", MinecraftServer.DEMO_SETTINGS, registries, WorldGenSettings.demoSettings(registries));
            }
        }));

        // Reset Demo World
        Button resetDemoButton = this.addRenderableWidget(new Button(10, 134, 140, 20, new TranslatableComponent("menu.resetdemo"), button -> {
            LevelStorageSource worldSource = this.minecraft.getLevelSource();

            try (LevelStorageSource.LevelStorageAccess demoWorldSource = worldSource.createAccess("Demo_World")) {
                LevelSummary summary = demoWorldSource.getSummary();
                if (summary != null) {
                    this.minecraft.setScreen(new ConfirmScreen(this::confirmDemo, new TranslatableComponent("selectWorld.deleteQuestion"), new TranslatableComponent("selectWorld.deleteWarning", summary.getLevelName()), new TranslatableComponent("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
                }
            } catch (IOException exception) {
                SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
                MellowUI.LOGGER.warn("Failed to access demo world", exception);
            }
        }));
        resetDemoButton.active = demoWorldPresent;
    }

    private boolean demoWorldPresent() {
        try (LevelStorageSource.LevelStorageAccess demoWorldSource = this.minecraft.getLevelSource().createAccess("Demo_World")) {
            return demoWorldSource.getSummary() != null;
        } catch (IOException exception) {
            SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
            MellowUI.LOGGER.warn("Failed to read demo world data", exception);
            return false;
        }
    }

    private void confirmDemo(boolean confirmed) {
        if (confirmed) {
            try (LevelStorageSource.LevelStorageAccess demoWorldSource = this.minecraft.getLevelSource().createAccess("Demo_World")) {
                demoWorldSource.deleteLevel();
            } catch (IOException exception) {
                SystemToast.onWorldDeleteFailure(this.minecraft, "Demo_World");
                MellowUI.LOGGER.warn("Failed to delete demo world", exception);
            }
        }

        this.minecraft.setScreen(this);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.fadeInStart == 0L && this.fading) this.fadeInStart = Util.getMillis();
        float fade = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000 : 1;
        float overlayTransparency = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000 : 1;
        float buttonAlpha = this.fading ? Mth.clamp(fade - 1, 0, 1) : 1;
        int textAlpha = Mth.ceil(buttonAlpha * 255) << 24;
        // Background
        this.components.renderPanorama(partialTicks, this.width, this.height, this.fading ? overlayTransparency : 1);
        this.components.renderBackgroundShaders(partialTicks);

        // Background Gradient
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, buttonAlpha);
        RenderSystem.setShaderTexture(0, GUITextures.MAIN_MENU_GRADIENT);
        blit(stack, 0, 0, 0, 0, 220, this.height, 220, this.height);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();

        // Logo
        LogoRenderer.renderMellomedleyLogo(stack, 10, 16, 210, 75, buttonAlpha, this.keepLogoThroughFade);

        if ((textAlpha & 0xFC000000) != 0) {
            // Splashes
            if (!MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) SplashRenderer.mellomedleySplash(stack, this.font, this.splash, textAlpha);

            // Text
            boolean copyrightTextHovered = mouseX > this.copyrightX && mouseX < this.copyrightX + this.copyrightWidth && mouseY > this.height - 11 && mouseY < this.height;
            Component mellomedleyVersion = new TranslatableComponent("menu.mellomedley.version.modpack", MellowConfigs.CLIENT_CONFIGS.mellomedleyVersion.get());
            Component vanillaVersion = new TranslatableComponent(this.minecraft.isDemo() ? "menu.mellomedley.version.vanilla_demo" : "menu.mellomedley.version.vanilla", SharedConstants.getCurrentVersion().getName(), ForgeVersion.getVersion());
            drawString(stack, this.font, vanillaVersion, this.width - this.font.width(vanillaVersion) - 2, this.height - 30, 0xFFFFFF | textAlpha);
            drawString(stack, this.font, mellomedleyVersion, this.width - this.font.width(mellomedleyVersion) - 2, this.height - 20, 0xFFFFFF | textAlpha);
            drawString(stack, this.font, new TranslatableComponent("menu.minecraft.credits"), this.copyrightX, this.height - 10, MellowUtils.getSelectableTextColor(copyrightTextHovered, true) | textAlpha);
            if (copyrightTextHovered) {
                fill(stack, this.copyrightX, this.height - 2, this.copyrightX + this.copyrightWidth, this.height - 1, MellowUtils.getSelectableTextColor(true, true) | textAlpha);
                fill(stack, this.copyrightX + 1, this.height - 1, this.copyrightX + this.copyrightWidth + 1, this.height, MellowUtils.getSelectableTextShadowColor(true, true) | textAlpha);
            }

            for (GuiEventListener listener : this.children()) {
                if (listener instanceof AbstractWidget) ((AbstractWidget) listener).setAlpha(buttonAlpha);
            }
            super.render(stack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            if (mouseX > (double) this.copyrightX && mouseX < (double) (this.copyrightX + this.copyrightWidth) && mouseY > (double) (this.height - 10) && mouseY < (double) this.height) {
                this.minecraft.setScreen(new AttributionsScreen(this));
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            }
            return false;
        }
    }
}
