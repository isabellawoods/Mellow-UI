package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.ChatOptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WithNarratorSettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = ChatOptionsScreen.class, priority = 900)
public class UpdatedChatSettingsScreen extends WithNarratorSettingsScreen {
    @Shadow
    @Final
    private static AbstractOption[] CHAT_OPTIONS;
    @Unique
    private OptionsRowList list;

    public UpdatedChatSettingsScreen(Screen lastScreen, GameSettings options, ITextComponent title, AbstractOption[] optionsList) {
        super(lastScreen, options, title, optionsList);
    }

    @Override
    protected void init() {
        if (MellowConfigs.CLIENT_CONFIGS.updateMouseSettingsMenu.get()) {
            this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
            this.list.addSmall(CHAT_OPTIONS);
            this.children.add(this.list);

            // Done button
            this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                    button -> this.minecraft.setScreen(this.lastScreen)));
        } else {
            super.init();
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (MellowConfigs.CLIENT_CONFIGS.updateMouseSettingsMenu.get()) {
            this.renderBackground(stack);
            this.list.render(stack, mouseX, mouseY, partialTicks);
            drawCenteredString(stack, this.font, new TranslationTextComponent("menu.minecraft.chat_settings.title"), this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
            for (Widget button : this.buttons) button.render(stack, mouseX, mouseY, partialTicks);
            List<IReorderingProcessor> tooltip = tooltipAt(this.list, mouseX, mouseY);
            if (tooltip != null) this.renderTooltip(stack, tooltip, mouseX, mouseY);
        } else {
            super.render(stack, mouseX, mouseY, partialTicks);
        }
    }
}
