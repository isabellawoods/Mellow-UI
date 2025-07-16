package melonystudios.mellowui.mixin.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.VanillaConfigEntries;
import melonystudios.mellowui.config.option.SoundSourceOption;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.network.chat.CommonComponents;
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
    private OptionsList list;

    public UpdatedMusicAndSoundsScreen(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateMusicAndSoundsMenu.get()) {
            callback.cancel();
            this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
            this.list.addBig(new SoundSourceOption("soundCategory.master", SoundSource.MASTER));
            this.list.addSmall(this.makeSoundSliders().toArray(new Option[0]));
            this.list.addBig(Option.AUDIO_DEVICE);
            this.list.addSmall(Option.SHOW_SUBTITLES, VanillaConfigEntries.DIRECTIONAL_AUDIO);
            this.list.addSmall(VanillaConfigEntries.SHOW_MUSIC_TOAST, null);
            this.addWidget(this.list);

            // Done button
            this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE,
                    button -> this.minecraft.setScreen(this.lastScreen)));
        }
    }

    @Unique
    private List<Option> makeSoundSliders() {
        List<Option> sliders = Lists.newArrayList();

        for (SoundSource category : SoundSource.values()) {
            if (category != SoundSource.MASTER) sliders.add(new SoundSourceOption("soundCategory." + category.getName(), category));
        }
        return sliders;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateMusicAndSoundsMenu.get()) {
            callback.cancel();
            this.renderBackground(stack);
            this.list.render(stack, mouseX, mouseY, partialTicks);
            drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
            super.render(stack, mouseX, mouseY, partialTicks);
            List<FormattedCharSequence> processors = tooltipAt(this.list, mouseX, mouseY);
            if (!processors.isEmpty()) this.renderTooltip(stack, processors, mouseX, mouseY);
        }
    }
}
