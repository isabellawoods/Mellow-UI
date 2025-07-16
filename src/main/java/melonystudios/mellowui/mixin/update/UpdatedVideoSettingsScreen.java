package melonystudios.mellowui.mixin.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.FullscreenResolutionProgressOption;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static melonystudios.mellowui.config.VanillaConfigEntries.*;
import static net.minecraft.client.Option.*;

@Mixin(value = VideoSettingsScreen.class, priority = 900)
public class UpdatedVideoSettingsScreen extends OptionsSubScreen {
    @Shadow
    @Final
    private static Option[] OPTIONS;
    @Unique
    private static final List<Option> UPDATED_OPTIONS = Lists.newArrayList(GRAPHICS, RENDER_DISTANCE, PRIORITIZE_CHUNK_UPDATES, SIMULATION_DISTANCE, SMOOTH_LIGHTING, FRAMERATE_LIMIT, ENABLE_VSYNC, VIEW_BOBBING, GUI_SCALE, ATTACK_INDICATOR, GAMMA, RENDER_CLOUDS, USE_FULLSCREEN, PARTICLES, MIPMAP_LEVELS, ENTITY_SHADOWS, SCREEN_EFFECTS_SCALE, ENTITY_DISTANCE_SCALING, FOV_EFFECTS_SCALE, AUTOSAVE_INDICATOR, MENU_BACKGROUND_BLURRINESS);
    @Unique
    private OptionsList updatedList;

    public UpdatedVideoSettingsScreen(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateVideoSettingsMenu.get() == ThreeStyles.OPTION_1) return;
        callback.cancel();
        this.updatedList = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.updatedList.addBig(new FullscreenResolutionProgressOption(this.minecraft.getWindow()));
        this.updatedList.addBig(BIOME_BLEND_RADIUS);

        // I tried to make Oculus compatible... it did not work. ~ 3-7-25
        // for some reason, it injects its options directly into the end of the list during init() via a @ModifyArg annotation
        // instead of just adding it into the OPTIONS field like I expected
        for (Option option : OPTIONS) {
            if (!UPDATED_OPTIONS.contains(option) && option != AMBIENT_OCCLUSION) UPDATED_OPTIONS.add(option);
        }
        this.updatedList.addSmall(UPDATED_OPTIONS.toArray(new Option[0]));
        this.addWidget(this.updatedList);

        // Done button
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateVideoSettingsMenu.get() == ThreeStyles.OPTION_1) return;
        callback.cancel();
        this.renderBackground(stack);
        this.updatedList.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<FormattedCharSequence> processors = tooltipAt(this.updatedList, mouseX, mouseY);
        if (!processors.isEmpty()) this.renderTooltip(stack, processors, mouseX, mouseY);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    public void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateVideoSettingsMenu.get() == ThreeStyles.OPTION_1) return;
        callback.cancel();
        int guiScale = this.options.guiScale;
        if (super.mouseReleased(mouseX, mouseY, button)) {
            callback.setReturnValue(true);
        } else if (this.updatedList.mouseReleased(mouseX, mouseY, button)) {
            if (this.options.guiScale != guiScale) this.minecraft.resizeDisplay();
            callback.setReturnValue(true);
        } else {
            callback.setReturnValue(false);
        }
    }
}
