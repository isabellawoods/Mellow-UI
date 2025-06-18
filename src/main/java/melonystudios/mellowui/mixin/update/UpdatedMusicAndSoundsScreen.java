package melonystudios.mellowui.mixin.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.VanillaConfigEntries;
import melonystudios.mellowui.config.option.SoundCategoryOption;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.OptionsSoundsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = OptionsSoundsScreen.class, priority = 990)
public class UpdatedMusicAndSoundsScreen extends SettingsScreen {
    private OptionsRowList list;

    public UpdatedMusicAndSoundsScreen(Screen lastScreen, GameSettings options, ITextComponent title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateMusicAndSoundsMenu.get()) {
            callback.cancel();
            this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
            this.list.addBig(new SoundCategoryOption("soundCategory.master", SoundCategory.MASTER));
            this.list.addSmall(this.makeSoundSliders().toArray(new AbstractOption[0]));
            this.list.addBig(VanillaConfigEntries.SOUND_DEVICE);
            this.list.addSmall(AbstractOption.SHOW_SUBTITLES, VanillaConfigEntries.DIRECTIONAL_AUDIO);
            this.list.addSmall(VanillaConfigEntries.SHOW_MUSIC_TOAST, null);
            this.children.add(this.list);

            // Done button
            this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                    button -> this.minecraft.setScreen(this.lastScreen)));
        }
    }

    @Unique
    private List<AbstractOption> makeSoundSliders() {
        List<AbstractOption> sliders = Lists.newArrayList();

        for (SoundCategory category : SoundCategory.values()) {
            if (category != SoundCategory.MASTER) sliders.add(new SoundCategoryOption("soundCategory." + category.getName(), category));
        }
        return sliders;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateMusicAndSoundsMenu.get()) {
            callback.cancel();
            this.renderBackground(stack);
            this.list.render(stack, mouseX, mouseY, partialTicks);
            drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
            super.render(stack, mouseX, mouseY, partialTicks);
            List<IReorderingProcessor> processors = tooltipAt(this.list, mouseX, mouseY);
            if (processors != null) this.renderTooltip(stack, processors, mouseX, mouseY);
        }
    }
}
