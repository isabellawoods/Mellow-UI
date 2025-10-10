package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.FourStyles;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.renderer.LogoRenderer;
import melonystudios.mellowui.renderer.SplashRenderer;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.screen.MellowCustomizationScreen;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.backport.AccessibilityOnboardingScreen;
import melonystudios.mellowui.screen.backport.AttributionsScreen;
import melonystudios.mellowui.screen.MellomedleyTitleScreen;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.widget.ImageSetModButton;
import melonystudios.mellowui.widget.ModButton;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.BrandingControl;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = MainMenuScreen.class, priority = 900)
public abstract class UpdatedTitleScreen extends Screen implements InterfaceMethods.TitleScreenMethods {
    @Unique private final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow @Final private static ResourceLocation PANORAMA_OVERLAY;
    @Mutable @Shadow @Final private RenderSkybox panorama;
    @Shadow private boolean realmsNotificationsInitialized;
    @Shadow private Screen realmsNotificationsScreen;
    @Shadow @Nullable private String splash;
    @Shadow private int copyrightWidth;
    @Shadow private int copyrightX;
    @Shadow private long fadeInStart;
    @Shadow @Final private boolean fading;
    @Shadow protected abstract void createDemoMenuOptions(int y, int rowHeight);
    @Shadow protected abstract void createNormalMenuOptions(int y, int rowHeight);
    @Shadow protected abstract boolean realmsNotificationsEnabled();
    @Shadow protected abstract void realmsButtonClicked();
    @Unique public boolean keepLogoThroughFade;

    public UpdatedTitleScreen(ITextComponent title) {
        super(title);
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
    public ResourceLocation getPanoramaOverlay() {
        return PANORAMA_OVERLAY;
    }

    @Inject(method = "<init>(Z)V", at = @At("TAIL"))
    public void constructor(boolean fading, CallbackInfo callback) {
        this.components.replacePanorama(this.panorama, true);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    public void init(CallbackInfo callback) {
        // Go to Mellomedley's main menu if set.
        if (MellowConfigs.CLIENT_CONFIGS.titleStyle.get() == ThreeStyles.OPTION_3) {
            this.minecraft.setScreen(new MellomedleyTitleScreen(this.fading, MellowConfigs.CLIENT_CONFIGS.onboardAccessibility.get()));
            return;
        }

        // Open accessibility onboarding if it hasn't been shown (Mellomedley's main menu opens this by itself).
        if (MellowConfigs.CLIENT_CONFIGS.onboardAccessibility.get()) {
            this.keepLogoThroughFade(true);
            this.minecraft.setScreen(new AccessibilityOnboardingScreen(() -> this.minecraft.setScreen(this)));
            return;
        }
        LogoRenderer.rerollEasterEgg();
        Panoramas.selectPanorama(Panoramas.panorama(), MellowConfigs.CLIENT_CONFIGS.selectedPanorama.get());

        if (MellowConfigs.CLIENT_CONFIGS.titleStyle.get() == ThreeStyles.OPTION_2) {
            callback.cancel();
            if (this.splash == null && !MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) this.splash = this.minecraft.getSplashManager().getSplash();

            this.copyrightWidth = this.font.width(new TranslationTextComponent("menu.minecraft.credits"));
            this.copyrightX = this.width - this.copyrightWidth - 2;
            FourStyles buttonStyle = MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get();
            int buttonsPos = buttonStyle == FourStyles.OPTION_4 && !this.minecraft.isDemo() ? this.height / 4 + 30 : this.height / 4 + 48;
            int heightOffset = this.minecraft.isDemo() ? -24 : (buttonStyle == FourStyles.OPTION_4 ? 24 : 0);

            if (this.minecraft.isDemo()) {
                this.createDemoMenuOptions(buttonsPos, 24);
            } else {
                this.createNormalMenuOptions(buttonsPos, 24);
                // Mods
                if (buttonStyle == FourStyles.OPTION_1) {
                    this.addButton(new ModButton(this.width / 2 + 2, buttonsPos + 48, 98, 20,
                            new TranslationTextComponent("fml.menu.mods"), button -> this.minecraft.setScreen(MellowUtils.modList(this))));
                } else if (buttonStyle == FourStyles.OPTION_3) {
                    this.addButton(new ModButton(this.width / 2 - 100, buttonsPos + 48, 200, 20,
                            new TranslationTextComponent("fml.menu.mods"), button -> this.minecraft.setScreen(MellowUtils.modList(this))));
                } else if (buttonStyle == FourStyles.OPTION_2) {
                    this.addButton(new ImageSetModButton(this.width / 2 + 104, buttonsPos + 48, 20, 20,
                            GUITextures.MODS_SET, button -> this.minecraft.setScreen(MellowUtils.modList(this)), (button, stack, mouseX, mouseY) ->
                            this.components.renderTooltip(this, button, new TranslationTextComponent("button.mellowui.mods.tooltip", ModList.get().getMods().size()), mouseX, mouseY),
                            new TranslationTextComponent("fml.menu.mods")).renderOnCorner(true));
                } else if (buttonStyle == FourStyles.OPTION_4) {
                    this.addButton(new ModButton(this.width / 2 - 100, buttonsPos + 72, 200, 20,
                            new TranslationTextComponent("fml.menu.mods"), button -> this.minecraft.setScreen(MellowUtils.modList(this))));
                }
            }

            // Language
            this.addButton(new ImageButton(this.width / 2 - 124, buttonsPos + 84 + heightOffset, 20, 20, 0, 106, 20,
                    Button.WIDGETS_LOCATION, 256, 256, button -> this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslationTextComponent("options.language"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.language")));

            // Options
            this.addButton(new Button(this.width / 2 - 100, buttonsPos + 84 + heightOffset, 98, 20,
                    new TranslationTextComponent("menu.options"), button -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));

            // Quit Game
            this.addButton(new Button(this.width / 2 + 2, buttonsPos + 84 + heightOffset, 98, 20,
                    new TranslationTextComponent("menu.quit"), button -> this.minecraft.stop()));

            // Accessibility Settings
            this.addButton(new ImageButton(this.width / 2 + 104, buttonsPos + 84 + heightOffset, 20, 20, 0, 0, 20,
                    GUITextures.ACCESSIBILITY_BUTTON, 32, 64, button -> this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options)), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslationTextComponent("options.accessibility.title"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.accessibility")));

            // Realms availability
            this.minecraft.setConnectedToRealms(false);
            if (this.minecraft.options.realmsNotifications && !this.realmsNotificationsInitialized) {
                RealmsBridgeScreen realmsBridge = new RealmsBridgeScreen();
                this.realmsNotificationsScreen = realmsBridge.getNotificationScreen(this);
                this.realmsNotificationsInitialized = true;
            }

            if (this.realmsNotificationsEnabled()) this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
        }

        // Switch Style
        this.addButton(this.components.switchStyle(button -> MellowUtils.switchTitleScreenStyle(this.minecraft), this.width - 20, 8));

        // Customize
        this.addButton(this.components.customize(button -> this.minecraft.setScreen(new MellowCustomizationScreen(this, this.minecraft.options)), this.width - 20, 21));
    }

    @Inject(method = "createNormalMenuOptions", at = @At("HEAD"), cancellable = true)
    private void createNormalMenuOptions(int y, int rowHeight, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.titleStyle.get() == ThreeStyles.OPTION_2) {
            callback.cancel();

            // Singleplayer
            this.addButton(new Button(this.width / 2 - 100, y, 200, 20,
                    new TranslationTextComponent("menu.singleplayer"), button -> this.minecraft.setScreen(new WorldSelectionScreen(this))));

            boolean allowsMultiplayer = this.minecraft.allowsMultiplayer();
            Button.ITooltip multiplayerTooltip = allowsMultiplayer ? Button.NO_TOOLTIP : (button, stack, mouseX, mouseY) -> {
                if (!button.active)
                    this.renderTooltip(stack, this.minecraft.font.split(new TranslationTextComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
            };

            // Multiplayer
            this.addButton(new Button(this.width / 2 - 100, y + rowHeight, 200, 20, new TranslationTextComponent("menu.multiplayer"), button -> {
                Screen multiplayerScreen = this.minecraft.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
                this.minecraft.setScreen(multiplayerScreen);
            }, multiplayerTooltip)).active = allowsMultiplayer;

            // Realms
            FourStyles buttonLocation = MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get();
            if (buttonLocation != FourStyles.OPTION_3) {
                int width = buttonLocation == FourStyles.OPTION_1 ? 98 : 200;
                this.addButton(new Button(this.width / 2 - 100, y + rowHeight * 2, width, 20,
                        new TranslationTextComponent("menu.online"), button -> this.realmsButtonClicked(), multiplayerTooltip)).active = allowsMultiplayer;
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.titleStyle.get() != ThreeStyles.OPTION_1) {
            if (MellowConfigs.CLIENT_CONFIGS.titleStyle.get() == ThreeStyles.OPTION_3) return;
            callback.cancel();
            if (this.fadeInStart == 0L && this.fading) this.fadeInStart = Util.getMillis();

            float overlayTransparency = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000 : 1;
            this.components.renderPanorama(partialTicks, this.width, this.height, this.fading ? overlayTransparency : 1);
            this.components.renderBackgroundShaders(partialTicks);
            float buttonAlpha = this.fading ? MathHelper.clamp(overlayTransparency - 1, 0, 1) : 1;
            int textAlpha = MathHelper.ceil(buttonAlpha * 255) << 24;

            RenderSystem.enableBlend();
            switch (MellowConfigs.CLIENT_CONFIGS.logoStyle.get()) {
                case OPTION_1: // Pre 1.16
                    LogoRenderer.renderPre116Logo(stack, this, this.width, buttonAlpha, 30, this.keepsLogoThroughFade());
                    break;
                case OPTION_2: // 1.16
                    LogoRenderer.render116Logo(stack, this, this.width, buttonAlpha, 30, this.keepsLogoThroughFade());
                    break;
                case OPTION_3: // 1.20 and above
                    LogoRenderer.renderUpdatedLogo(stack, this.width, buttonAlpha, this.keepsLogoThroughFade());
                    break;
                case OPTION_4: // Mellomedley's logo
                    LogoRenderer.renderMellomedleyLogo(stack, this.width / 2 - 129, 10, 258, 100, buttonAlpha, this.keepsLogoThroughFade());
                    break;
            }

            if ((textAlpha & 0xFC000000) != 0) {
                boolean copyrightTextHovered = mouseX > this.copyrightX && mouseX < this.copyrightX + this.copyrightWidth && mouseY > this.height - 10 && mouseY < this.height;
                int textColor = MellowUtils.getSelectableTextColor(false, true);

                // Forge's beta warning
                this.components.renderForgeBetaText(this.width, 3, textColor, textAlpha);

                // Title screen icons background
                this.components.renderTitleScreenIconsBackground(this.width - 21, 7, buttonAlpha);

                // Splashes
                if (!MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) {
                    if (MellowConfigs.CLIENT_CONFIGS.splashTextPosition.get()) SplashRenderer.updatedSplash(stack, this.font, this.splash, this.width, textAlpha);
                    else SplashRenderer.defaultSplash(stack, this.font, this.splash, this.width, textAlpha);
                }

                // Text
                if (!MellowConfigs.CLIENT_CONFIGS.disableBranding.get()) {
                    BrandingControl.forEachLine(true, true, (lineHeight, text) ->
                            drawString(stack, this.font, text, 2, this.height - (10 + lineHeight * (this.font.lineHeight + 1)), textColor | textAlpha));
                    BrandingControl.forEachAboveCopyrightLine((lineHeight, text) ->
                            drawString(stack, this.font, text, this.width - font.width(text), this.height - (10 + (lineHeight + 1) * (this.font.lineHeight + 1)), textColor | textAlpha));
                } else {
                    ITextComponent releaseBranding = new TranslationTextComponent("menu.minecraft." + (this.minecraft.isDemo() ? "demo" : "branding"), SharedConstants.getCurrentVersion().getName(), ModList.get().size());
                    ITextComponent snapshotBranding = new TranslationTextComponent("menu.minecraft." + (this.minecraft.isDemo() ? "demo" : "branding") + ".snapshot", SharedConstants.getCurrentVersion().getName(),
                            this.minecraft.getVersionType(), ModList.get().size());
                    ITextComponent branding = this.minecraft.getVersionType().equalsIgnoreCase("release") ? releaseBranding : snapshotBranding;

                    drawString(stack, this.font, branding, 2, this.height - 10, textColor | textAlpha);
                }

                drawString(stack, this.font, new TranslationTextComponent("menu.minecraft.credits"), this.copyrightX, this.height - 10, MellowUtils.getSelectableTextColor(copyrightTextHovered, true) | textAlpha);
                if (copyrightTextHovered) {
                    fill(stack, this.copyrightX, this.height - 2, this.copyrightX + this.copyrightWidth, this.height - 1, MellowUtils.getSelectableTextColor(true, true) | textAlpha);
                    fill(stack, this.copyrightX + 1, this.height - 1, this.copyrightX + this.copyrightWidth + 1, this.height, MellowUtils.getSelectableTextShadowColor(true, true) | textAlpha);
                }

                for (Widget widget : this.buttons) widget.setAlpha(buttonAlpha);
                super.render(stack, mouseX, mouseY, partialTicks);
                if (this.realmsNotificationsEnabled() && buttonAlpha >= 1) this.realmsNotificationsScreen.render(stack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callback) {
        if (MellowConfigs.CLIENT_CONFIGS.titleStyle.get() == ThreeStyles.OPTION_2) {
            callback.cancel();
            if (super.mouseClicked(mouseX, mouseY, button)) {
                callback.setReturnValue(true);
            } else if (this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(mouseX, mouseY, button)) {
                callback.setReturnValue(true);
            } else {
                if (mouseX > this.copyrightX && mouseX < this.copyrightX + this.copyrightWidth && mouseY > this.height - 10 && mouseY < this.height) {
                    this.minecraft.setScreen(new AttributionsScreen(this));
                    this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
                }

                callback.setReturnValue(false);
            }
        }
    }
}
