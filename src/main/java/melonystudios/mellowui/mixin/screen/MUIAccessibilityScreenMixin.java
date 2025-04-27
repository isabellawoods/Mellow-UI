package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigEntries;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.*;

import java.util.List;

import static net.minecraft.client.AbstractOption.*;

@Mixin(AccessibilityScreen.class)
public abstract class MUIAccessibilityScreenMixin extends SettingsScreen {
    @Mutable @Shadow @Final private static AbstractOption[] OPTIONS;
    @Shadow protected abstract void createFooter();

    @Unique
    private static final AbstractOption[] UPDATED_OPTIONS = new AbstractOption[] {NARRATOR, SHOW_SUBTITLES, AUTO_JUMP, TEXT_BACKGROUND_OPACITY, TEXT_BACKGROUND, CHAT_OPACITY, CHAT_LINE_SPACING, CHAT_DELAY, VIEW_BOBBING, TOGGLE_CROUCH, TOGGLE_SPRINT,
            SCREEN_EFFECTS_SCALE, FOV_EFFECTS_SCALE, MellowConfigEntries.MONOCHROME_LOADING_SCREEN, MellowConfigEntries.PANORAMA_SCROLL_SPEED, MellowConfigEntries.HIDE_SPLASH_TEXTS};
    @Unique
    private OptionsRowList list;

    public MUIAccessibilityScreenMixin(Screen lastScreen, GameSettings options, ITextComponent title) {
        super(lastScreen, options, title);
    }

    @Override
    protected void init() {
        OPTIONS = UPDATED_OPTIONS; // even if the option is disabled after, it will update the list (still not 100% though) ~isa 26-4-25
        if (MellowConfigs.CLIENT_CONFIGS.updateAccessibilityMenu.get()) {
            this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
            this.list.addSmall(UPDATED_OPTIONS);
            this.children.add(this.list);
            this.createFooter();
            Widget narratorButton = this.list.findOption(AbstractOption.NARRATOR);
            if (narratorButton != null) narratorButton.active = NarratorChatListener.INSTANCE.isActive();
        } else {
            super.init();
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (MellowConfigs.CLIENT_CONFIGS.updateAccessibilityMenu.get()) {
            this.renderBackground(stack);
            this.list.render(stack, mouseX, mouseY, partialTicks);
            drawCenteredString(stack, this.font, new TranslationTextComponent("menu.minecraft.accessibility_settings.title"), this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
            for (Widget button : this.buttons) button.render(stack, mouseX, mouseY, partialTicks);
            List<IReorderingProcessor> tooltip = tooltipAt(this.list, mouseX, mouseY);
            if (tooltip != null) this.renderTooltip(stack, tooltip, mouseX, mouseY);
        } else {
            super.render(stack, mouseX, mouseY, partialTicks);
        }
    }
}
