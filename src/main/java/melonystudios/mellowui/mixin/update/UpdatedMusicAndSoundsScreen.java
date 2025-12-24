package melonystudios.mellowui.mixin.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.VanillaConfigEntries;
import melonystudios.mellowui.config.option.SoundCategoryOption;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.OptionsSoundsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
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
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Unique
    private OptionsRowList list;

    public UpdatedMusicAndSoundsScreen(Screen lastScreen, GameSettings options, ITextComponent title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.musicAndSoundsStyle.get()) return;
        callback.cancel();
        WidgetComponents components = WidgetComponents.components(this, this::addButton);
        this.list = components.optionsList(33, this.height - 33);
        this.list.addBig(new SoundCategoryOption("soundCategory.master", SoundCategory.MASTER));
        this.list.addSmall(this.makeSoundSliders().toArray(new AbstractOption[0]));
        this.list.addBig(VanillaConfigEntries.SOUND_DEVICE);
        this.list.addSmall(VanillaConfigEntries.CLOSED_CAPTIONS, VanillaConfigEntries.DIRECTIONAL_AUDIO);
        this.list.addSmall(VanillaConfigEntries.MUSIC_TOAST, null);
        this.children.add(this.list);

        // Done button
        components.done(Alignment.CENTER);
    }

    @Unique
    private List<AbstractOption> makeSoundSliders() {
        List<AbstractOption> sliders = Lists.newArrayList();

        for (SoundCategory category : SoundCategory.values()) {
            if (category != SoundCategory.MASTER) sliders.add(new SoundCategoryOption("soundCategory." + category.getName(), category));
        }
        sliders.add(VanillaConfigEntries.UI_VOLUME);
        return sliders;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.musicAndSoundsStyle.get()) return;
        callback.cancel();
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.components.drawTitle(this.title.copy().withStyle(TextComponents.titleStyle()), this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> tooltip = tooltipAt(this.list, mouseX, mouseY);
        if (tooltip != null) this.renderTooltip(stack, tooltip, mouseX, mouseY);
    }
}
