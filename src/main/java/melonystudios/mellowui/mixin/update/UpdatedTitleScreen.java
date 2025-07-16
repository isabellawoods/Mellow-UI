package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.renderer.LogoRenderer;
import melonystudios.mellowui.renderer.SplashRenderer;
import melonystudios.mellowui.screen.MellomedleyTitleScreen;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.backport.AccessibilityOnboardingScreen;
import melonystudios.mellowui.screen.backport.AttributionsScreen;
import melonystudios.mellowui.screen.update.TitleScreenWarning32Bit;
import melonystudios.mellowui.screen.widget.ImageSetModButton;
import melonystudios.mellowui.screen.widget.ModButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.internal.BrandingControl;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

@Mixin(value = TitleScreen.class, priority = 900)
public abstract class UpdatedTitleScreen extends Screen implements InterfaceMethods.TitleScreenMethods {
    @Unique private final RenderComponents components = RenderComponents.INSTANCE;
    @Mutable @Shadow @Final private PanoramaRenderer panorama;
    @Shadow private Screen realmsNotificationsScreen;
    @Shadow @Nullable private String splash;
    @Shadow private long fadeInStart;
    @Shadow @Final private boolean fading;
    @Shadow protected abstract void createDemoMenuOptions(int y, int rowHeight);
    @Shadow protected abstract void createNormalMenuOptions(int y, int rowHeight);
    @Shadow protected abstract boolean realmsNotificationsEnabled();
    @Shadow protected abstract void realmsButtonClicked();
    @Shadow protected abstract boolean hasRealmsSubscription();

    @Unique @Nullable private TitleScreenWarning32Bit warning32Bit;
    @Unique public boolean keepLogoThroughFade;
    @Unique private int copyrightWidth;
    @Unique private int copyrightX;

    public UpdatedTitleScreen(Component title) {
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

    @Inject(method = "<init>(Z)V", at = @At("TAIL"))
    public void constructor(boolean fading, CallbackInfo callback) {
        this.components.replacePanorama(this.panorama);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    public void init(CallbackInfo callback) {
        // Go to Mellomedley's main menu if set.
        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == ThreeStyles.OPTION_3) {
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

        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == ThreeStyles.OPTION_2) {
            callback.cancel();
            if (this.splash == null && !MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) this.splash = this.minecraft.getSplashManager().getSplash();

            this.copyrightWidth = this.font.width(new TranslatableComponent("menu.minecraft.credits"));
            this.copyrightX = this.width - this.copyrightWidth - 2;
            int buttonsPos = this.height / 4 + 48;
            int demoOffset = this.minecraft.isDemo() ? 24 : 0;

            if (this.minecraft.isDemo()) {
                this.createDemoMenuOptions(buttonsPos, 24);
            } else {
                this.createNormalMenuOptions(buttonsPos, 24);
                // Mods
                ThreeStyles buttonLocation = MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get();
                if (buttonLocation == ThreeStyles.OPTION_1) {
                    this.addRenderableWidget(new ModButton(this.width / 2 + 2, buttonsPos + 24 * 2, 98, 20,
                            new TranslatableComponent("fml.menu.mods"), button -> this.minecraft.setScreen(MellowUtils.modList(this))));
                } else if (buttonLocation == ThreeStyles.OPTION_3) {
                    this.addRenderableWidget(new ModButton(this.width / 2 - 100, buttonsPos + 24 * 2, 200, 20,
                            new TranslatableComponent("fml.menu.mods"), button -> this.minecraft.setScreen(MellowUtils.modList(this))));
                } else if (buttonLocation == ThreeStyles.OPTION_2) {
                    this.addRenderableWidget(new ImageSetModButton(this.width / 2 + 104, buttonsPos + 24 * 2, 20, 20,
                            GUITextures.MODS_SET, button -> this.minecraft.setScreen(MellowUtils.modList(this)), (button, stack, mouseX, mouseY) ->
                            this.components.renderTooltip(this, button, new TranslatableComponent("button.mellowui.mods.desc", ModList.get().getMods().size()), mouseX, mouseY),
                            new TranslatableComponent("fml.menu.mods")).renderOnCorner(true));
                }
            }

            // Language
            this.addRenderableWidget(new ImageButton(this.width / 2 - 124, buttonsPos + 84 - demoOffset, 20, 20, 0, 106, 20,
                    Button.WIDGETS_LOCATION, 256, 256, button -> this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslatableComponent("options.language"), mouseX, mouseY),
                    new TranslatableComponent("narrator.button.language")));

            // Options
            this.addRenderableWidget(new Button(this.width / 2 - 100, buttonsPos + 84 - demoOffset, 98, 20,
                    new TranslatableComponent("menu.options"), button -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));

            // Quit Game
            this.addRenderableWidget(new Button(this.width / 2 + 2, buttonsPos + 84 - demoOffset, 98, 20,
                    new TranslatableComponent("menu.quit"), button -> this.minecraft.stop()));

            // Accessibility Settings
            this.addRenderableWidget(new ImageButton(this.width / 2 + 104, buttonsPos + 84 - demoOffset, 20, 20, 0, 0, 20,
                    GUITextures.ACCESSIBILITY_BUTTON, 32, 64, button -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslatableComponent("options.accessibility.title"), mouseX, mouseY),
                    new TranslatableComponent("narrator.button.accessibility")));

            // Realms availability
            this.minecraft.setConnectedToRealms(false);
            if (this.minecraft.options.realmsNotifications && this.realmsNotificationsScreen == null) {
                this.realmsNotificationsScreen = new RealmsNotificationsScreen();
            }

            if (this.realmsNotificationsEnabled()) this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);

            if (!this.minecraft.is64Bit()) {
                CompletableFuture<Boolean> subscriptionFuture = this.warning32Bit != null ? this.warning32Bit.realmsSubscriptionFuture() : CompletableFuture.supplyAsync(this::hasRealmsSubscription, Util.backgroundExecutor());
                this.warning32Bit = new TitleScreenWarning32Bit(MultiLineLabel.create(this.font, new TranslatableComponent("title.32bit.deprecation"), 350, 2), this.width / 2, buttonsPos - 36, subscriptionFuture);
            }
        }

        // Switch Style
        this.addRenderableWidget(this.components.switchStyle(button -> MellowUtils.switchTitleScreenStyle(this.minecraft), this, this.width - 20, 8));
    }

    @Inject(method = "createNormalMenuOptions", at = @At("HEAD"), cancellable = true)
    private void createNormalMenuOptions(int y, int rowHeight, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == ThreeStyles.OPTION_2) {
            callback.cancel();

            // Singleplayer
            this.addRenderableWidget(new Button(this.width / 2 - 100, y, 200, 20,
                    new TranslatableComponent("menu.singleplayer"), button -> this.minecraft.setScreen(new SelectWorldScreen(this))));

            boolean allowsMultiplayer = this.minecraft.allowsMultiplayer();
            Button.OnTooltip multiplayerTooltip = allowsMultiplayer ? Button.NO_TOOLTIP : (button, stack, mouseX, mouseY) -> {
                if (!button.active)
                    this.renderTooltip(stack, this.minecraft.font.split(new TranslatableComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
            };

            // Multiplayer
            this.addRenderableWidget(new Button(this.width / 2 - 100, y + rowHeight, 200, 20, new TranslatableComponent("menu.multiplayer"), button -> {
                Screen multiplayerScreen = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
                this.minecraft.setScreen(multiplayerScreen);
            }, multiplayerTooltip)).active = allowsMultiplayer;

            // Realms
            ThreeStyles buttonLocation = MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get();
            if (buttonLocation != ThreeStyles.OPTION_3) {
                int width = buttonLocation == ThreeStyles.OPTION_1 ? 98 : 200;
                this.addRenderableWidget(new Button(this.width / 2 - 100, y + rowHeight * 2, width, 20,
                        new TranslatableComponent("menu.online"), button -> this.realmsButtonClicked(), multiplayerTooltip)).active = allowsMultiplayer;
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() != ThreeStyles.OPTION_1) {
            if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == ThreeStyles.OPTION_3) return;
            callback.cancel();
            if (this.fadeInStart == 0L && this.fading) this.fadeInStart = Util.getMillis();

            float overlayTransparency = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000 : 1;
            this.components.renderPanorama(partialTicks, this.width, this.height, this.fading ? overlayTransparency : 1);
            this.components.renderBackgroundShaders(partialTicks);
            float buttonAlpha = this.fading ? Mth.clamp(overlayTransparency - 1, 0, 1) : 1;
            int textAlpha = Mth.ceil(buttonAlpha * 255) << 24;

            RenderSystem.enableBlend();
            switch (MellowConfigs.CLIENT_CONFIGS.logoStyle.get()) {
                case OPTION_1: // Pre 1.19
                    LogoRenderer.renderOldLogo(stack, this, this.width, buttonAlpha, this.keepsLogoThroughFade());
                    break;
                case OPTION_2: // 1.20 and above
                    LogoRenderer.renderUpdatedLogo(stack, this.width, buttonAlpha, this.keepsLogoThroughFade());
                    break;
                case OPTION_3: // Mellomedley's logo
                    LogoRenderer.renderMellomedleyLogo(stack, this.width / 2 - 129, 10, 258, 100, buttonAlpha, this.keepsLogoThroughFade());
                    break;
            }

            if ((textAlpha & 0xFC000000) != 0) {
                if (this.warning32Bit != null) {
                    this.warning32Bit.label().renderBackgroundCentered(stack, this.warning32Bit.x(), this.warning32Bit.y(), 9, 2, 0x55200000);
                    this.warning32Bit.label().renderCentered(stack, this.warning32Bit.x(), this.warning32Bit.y(), 9, 0xFFFFFF | textAlpha);
                }

                ForgeHooksClient.renderMainMenu((TitleScreen) this.minecraft.screen, stack, this.font, this.width, this.height, textAlpha);
                if (!MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) {
                    if (MellowConfigs.CLIENT_CONFIGS.splashTextPosition.get()) SplashRenderer.updatedSplash(stack, this.font, this.splash, this.width, textAlpha);
                    else SplashRenderer.defaultSplash(stack, this.font, this.splash, this.width, textAlpha);
                }

                if (!MellowConfigs.CLIENT_CONFIGS.disableBranding.get()) {
                    BrandingControl.forEachLine(true, true, (lineHeight, text) ->
                            drawString(stack, this.font, text, 2, this.height - (10 + lineHeight * (this.font.lineHeight + 1)), 0xFFFFFF | textAlpha));
                    BrandingControl.forEachAboveCopyrightLine((lineHeight, text) ->
                            drawString(stack, this.font, text, this.width - font.width(text), this.height - (10 + (lineHeight + 1) * (this.font.lineHeight + 1)), 0xFFFFFF | textAlpha));
                } else {
                    Component releaseBranding = new TranslatableComponent("menu.minecraft." + (this.minecraft.isDemo() ? "demo" : "branding"), SharedConstants.getCurrentVersion().getName(), ModList.get().size());
                    Component snapshotBranding = new TranslatableComponent("menu.minecraft." + (this.minecraft.isDemo() ? "demo" : "branding") + ".snapshot", SharedConstants.getCurrentVersion().getName(),
                            this.minecraft.getVersionType(), ModList.get().size());
                    Component branding = this.minecraft.getVersionType().equalsIgnoreCase("release") ? releaseBranding : snapshotBranding;

                    drawString(stack, this.font, branding, 2, this.height - 10, 0xFFFFFF | textAlpha);
                }
                boolean copyrightTextHovered = mouseX > this.copyrightX && mouseX < this.copyrightX + this.copyrightWidth && mouseY > this.height - 10 && mouseY < this.height;

                drawString(stack, this.font, new TranslatableComponent("menu.minecraft.credits"), this.copyrightX, this.height - 10, MellowUtils.getSelectableTextColor(copyrightTextHovered, true) | textAlpha);
                if (copyrightTextHovered) {
                    fill(stack, this.copyrightX, this.height - 2, this.copyrightX + this.copyrightWidth, this.height - 1, MellowUtils.getSelectableTextColor(true, true) | textAlpha);
                    fill(stack, this.copyrightX + 1, this.height - 1, this.copyrightX + this.copyrightWidth + 1, this.height, MellowUtils.getSelectableTextShadowColor(true, true) | textAlpha);
                }

                for (GuiEventListener listener : this.children()) {
                    if (listener instanceof AbstractWidget) ((AbstractWidget) listener).setAlpha(buttonAlpha);
                }
                super.render(stack, mouseX, mouseY, partialTicks);
                if (this.realmsNotificationsEnabled() && buttonAlpha >= 1) this.realmsNotificationsScreen.render(stack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callback) {
        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == ThreeStyles.OPTION_2) {
            callback.cancel();
            if (super.mouseClicked(mouseX, mouseY, button)) {
                callback.setReturnValue(true);
            } else if (this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(mouseX, mouseY, button)) {
                callback.setReturnValue(true);
            } else {
                if (mouseX > this.copyrightX && mouseX < this.copyrightX + this.copyrightWidth && mouseY > this.height - 10 && mouseY < this.height) {
                    this.minecraft.setScreen(new AttributionsScreen(this));
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
                }

                callback.setReturnValue(false);
            }
        }
    }
}
