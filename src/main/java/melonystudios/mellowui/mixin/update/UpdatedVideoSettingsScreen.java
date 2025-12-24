package melonystudios.mellowui.mixin.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.VanillaConfigEntries;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.FullscreenResolutionOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.client.AbstractOption.*;

@Mixin(value = VideoSettingsScreen.class, priority = 900)
public class UpdatedVideoSettingsScreen extends SettingsScreen {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow
    @Final
    private static AbstractOption[] OPTIONS;
    @Shadow
    private OptionsRowList list;
    @Unique
    private static final List<AbstractOption> UPDATED_OPTIONS = Lists.newArrayList(GRAPHICS, RENDER_DISTANCE, VanillaConfigEntries.SMOOTH_LIGHTING, FRAMERATE_LIMIT, ENABLE_VSYNC, GUI_SCALE, ATTACK_INDICATOR, VanillaConfigEntries.BRIGHTNESS, RENDER_CLOUDS, USE_FULLSCREEN, PARTICLES, MIPMAP_LEVELS, ENTITY_SHADOWS, SCREEN_EFFECTS_SCALE, ENTITY_DISTANCE_SCALING, VanillaConfigEntries.FOV_EFFECTS, VanillaConfigEntries.MENU_BACKGROUND_BLURRINESS, VIEW_BOBBING);

    public UpdatedVideoSettingsScreen(Screen lastScreen, GameSettings options, ITextComponent title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.videoSettingsStyle.get() == ThreeStyles.OPTION_1) return;
        callback.cancel();
        WidgetComponents components = WidgetComponents.components(this, this::addButton);
        this.list = components.optionsList(33, this.height - 33);
        this.list.addBig(new FullscreenResolutionOption(this.minecraft.getWindow()));
        this.list.addBig(BIOME_BLEND_RADIUS);

        // I tried to make Oculus compatible... it did not work. ~ 3-7-25
        // for some reason, it injects its options directly into the end of the list during init() via a @ModifyArg annotation
        // instead of just adding it into the OPTIONS field like I expected
        for (AbstractOption option : OPTIONS) {
            if (!UPDATED_OPTIONS.contains(option) && option != AMBIENT_OCCLUSION && option != FOV_EFFECTS_SCALE && option != GAMMA) {
                UPDATED_OPTIONS.add(option);
            }
        }
        this.list.addSmall(UPDATED_OPTIONS.toArray(new AbstractOption[0]));
        this.children.add(this.list);

        // Done button
        components.done(Alignment.CENTER);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.videoSettingsStyle.get() == ThreeStyles.OPTION_1) return;
        callback.cancel();
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.components.drawTitle(this.title.copy().withStyle(TextComponents.titleStyle()), this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> tooltip = tooltipAt(this.list, mouseX, mouseY);
        if (tooltip != null) this.renderTooltip(stack, tooltip, mouseX, mouseY);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    public void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callback) {
        if (MellowConfigs.CLIENT_CONFIGS.videoSettingsStyle.get() == ThreeStyles.OPTION_1) return;
        callback.cancel();
        int guiScale = this.options.guiScale;

        if (super.mouseReleased(mouseX, mouseY, button)) {
            callback.setReturnValue(true);
        } else if (this.list.mouseReleased(mouseX, mouseY, button)) {
            if (this.options.guiScale != guiScale) this.minecraft.resizeDisplay();
            callback.setReturnValue(true);
        } else {
            callback.setReturnValue(false);
        }
    }
}
