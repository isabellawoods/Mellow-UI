package melonystudios.mellowui.mixin.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.VanillaConfigEntries;
import melonystudios.mellowui.config.option.SoundSourceOption;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = SoundOptionsScreen.class, priority = 990)
public class UpdatedMusicAndSoundsScreen extends OptionsSubScreen {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Unique
    private OptionsList list;

    public UpdatedMusicAndSoundsScreen(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.musicAndSoundsStyle.get()) return;
        callback.cancel();
        WidgetComponents components = WidgetComponents.components(this, this::addRenderableWidget);
        this.list = components.optionsList(33, this.height - 33);
        this.list.addBig(new SoundSourceOption("soundCategory.master", SoundSource.MASTER));
        this.list.addSmall(this.makeSoundSliders().toArray(new Option[0]));
        this.list.addBig(Option.AUDIO_DEVICE);
        this.list.addSmall(VanillaConfigEntries.CLOSED_CAPTIONS, VanillaConfigEntries.DIRECTIONAL_AUDIO);
        this.list.addSmall(VanillaConfigEntries.MUSIC_TOAST, null);
        this.addWidget(this.list);

        // Done button
        components.done(Alignment.CENTER);
    }

    @Unique
    private List<Option> makeSoundSliders() {
        List<Option> sliders = Lists.newArrayList();

        for (SoundSource category : SoundSource.values()) {
            if (category != SoundSource.MASTER) sliders.add(new SoundSourceOption("soundCategory." + category.getName(), category));
        }
        sliders.add(VanillaConfigEntries.UI_VOLUME);
        return sliders;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.musicAndSoundsStyle.get()) return;
        callback.cancel();
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.components.drawTitle(this.title.copy().withStyle(TextComponents.titleStyle()), this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<FormattedCharSequence> tooltip = tooltipAt(this.list, mouseX, mouseY);
        if (!tooltip.isEmpty()) this.renderTooltip(stack, tooltip, mouseX, mouseY);
    }
}
