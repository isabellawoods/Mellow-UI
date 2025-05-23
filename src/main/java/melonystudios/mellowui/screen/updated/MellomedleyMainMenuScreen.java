package melonystudios.mellowui.screen.updated;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.renderer.LogoRenderer;
import melonystudios.mellowui.renderer.SplashRenderer;
import melonystudios.mellowui.screen.WidgetTextureSet;
import melonystudios.mellowui.screen.forge.MUIModUpdateScreen;
import melonystudios.mellowui.screen.widget.IconButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
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
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.io.IOException;

public class MellomedleyMainMenuScreen extends Screen {
    private Button resetDemoButton;
    private Button modsButton;
    private final boolean fading;
    private long fadeInStart;
    private int copyrightWidth;
    private int copyrightX;
    @Nullable
    private String splash;
    private NotificationModUpdateScreen modUpdateScreen;

    public MellomedleyMainMenuScreen() {
        this(false);
    }

    public MellomedleyMainMenuScreen(boolean fading) {
        super(new TranslationTextComponent("menu.mellomedley.title"));
        this.fading = fading;
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
    public void init() {
        // Demo-dependent options
        if (this.minecraft.isDemo()) this.demoMenu();
        else this.defaultMenu();

        this.copyrightWidth = this.font.width(new TranslationTextComponent("menu.minecraft.credits"));
        this.copyrightX = this.width - this.copyrightWidth - 3;
        if (this.splash == null && !MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) this.splash = this.minecraft.getSplashManager().getSplash();

        // Options
        this.addButton(new Button(10, 160, 140, 20, new TranslationTextComponent("menu.options"),
                button -> this.minecraft.setScreen(new MUIOptionsScreen(this, this.minecraft.options))));

        // Mods
        this.addButton(this.modsButton = new Button(10, 190, 110, 20, new TranslationTextComponent("fml.menu.mods"),
                button -> this.minecraft.setScreen(MellowUtils.modList(this))));
        this.modsButton.active = !this.minecraft.isDemo();
        this.modUpdateScreen = MUIModUpdateScreen.create(this.minecraft.screen, this.modsButton, false);

        // Language
        this.addButton(new ImageButton(130, 190, 20, 20, 0, 106, 20,
                Button.WIDGETS_LOCATION, 256, 256, button -> this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())),
                new TranslationTextComponent("narrator.button.language")));

        // Quit Game
        this.addButton(new Button(10, 220, 110, 20, new TranslationTextComponent("menu.quit"),
                button -> this.minecraft.stop()));

        // Accessibility
        this.addButton(new ImageButton(130, 220, 20, 20, 0, 0, 20,
                GUITextures.ACCESSIBILITY_BUTTON, 32, 64, button -> this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options)),
                new TranslationTextComponent("narrator.button.accessibility")));

        // Switch Style
        this.addButton(new IconButton(this.width - 20, 8, 12, 12, GUITextures.SWITCH_STYLE_SET, new TranslationTextComponent("button.mellowui.switch_style"),
                button -> WidgetTextureSet.switchMainMenuStyle(), (button, stack, mouseX, mouseY) ->
                MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("button.mellowui.switch_style"), mouseX, mouseY)));
    }

    private void defaultMenu() {
        // Singleplayer
        this.addButton(new Button(10, 100, 140, 20, new TranslationTextComponent("menu.singleplayer"),
                button -> this.minecraft.setScreen(new WorldSelectionScreen(this))));

        boolean allowsMultiplayer = this.minecraft.allowsMultiplayer();
        Button.ITooltip multiplayerTooltip = allowsMultiplayer ? Button.NO_TOOLTIP : (button, stack, mouseX, mouseY) -> {
            if (!button.active)
                this.renderTooltip(stack, this.minecraft.font.split(new TranslationTextComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
        };

        // Multiplayer
        this.addButton(new Button(10, 130, 140, 20, new TranslationTextComponent("menu.multiplayer"), button -> {
            Screen multiplayerScreen = this.minecraft.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.minecraft.setScreen(multiplayerScreen);
        }, multiplayerTooltip)).active = allowsMultiplayer;
    }

    private void demoMenu() {
        boolean demoWorldPresent = this.demoWorldPresent();

        // Play Demo World
        this.addButton(new Button(10, 100, 140, 20, new TranslationTextComponent("menu.playdemo"),button -> {
            if (demoWorldPresent) {
                this.minecraft.loadLevel("Demo_World");
            } else {
                DynamicRegistries.Impl registries = DynamicRegistries.builtin();
                this.minecraft.createLevel("Demo_World", MinecraftServer.DEMO_SETTINGS, registries, DimensionGeneratorSettings.demoSettings(registries));
            }
        }));

        // Reset Demo World
        this.resetDemoButton = this.addButton(new Button(10, 130, 140, 20, new TranslationTextComponent("menu.resetdemo"), button -> {
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
        this.resetDemoButton.active = demoWorldPresent;
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
        /*this.minecraft.getTextureManager().bind(GUITextures.PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1, 1, 1, this.fading ? (float) MathHelper.ceil(MathHelper.clamp(overlayTransparency, 0, 1)) : 1);
        blit(stack, 0, 0, this.width, this.height, 0, 0, 16, 128, 16, 128);
        if (Minecraft.getInstance().level == null) MellowUtils.PANORAMA.render(this.minecraft.getDeltaFrameTime(), MathHelper.clamp(overlayTransparency, 0, 1));
        else this.renderBackground(stack);*/
        MellowUtils.renderPanorama(stack, this.width, this.height, this.fading ? overlayTransparency : 1);

        if ((textAlpha & 0xFC000000) != 0) {
            RenderSystem.color4f(1, 1, 1, buttonAlpha);
            RenderSystem.enableBlend();

            // Background Gradient
            this.minecraft.textureManager.bind(GUITextures.MAIN_MENU_GRADIENT);
            blit(stack, 0, 0, 0, 0, 220, this.height, 220, this.height);
            RenderSystem.color4f(1, 1, 1, 1);
            RenderSystem.disableBlend();

            LogoRenderer.renderMellomedleyLogo(stack, 10, 16, 210, 75, buttonAlpha);

            // Splashes
            /*if (this.splash != null && !MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) {
                RenderSystem.pushMatrix();
                RenderSystem.translatef(173, 80, 0);
                RenderSystem.rotatef(-20, 0, 0, 1);
                float splashSize = 1.8F - MathHelper.abs(MathHelper.sin((float) (Util.getMillis() % 1000L) / 1000 * ((float) Math.PI * 2F)) * 0.1F);
                splashSize = splashSize * 100 / (float) (this.font.width(this.splash) + 32);
                RenderSystem.scalef(splashSize, splashSize, splashSize);
                drawCenteredString(stack, this.font, this.splash, 0, -8, MellowConfigs.CLIENT_CONFIGS.melloSplashTextColor.get() | textAlpha);
                RenderSystem.popMatrix();
            }*/
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
            if (!this.minecraft.isDemo()) {
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
