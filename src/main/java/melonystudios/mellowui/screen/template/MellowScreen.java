package melonystudios.mellowui.screen.template;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.widget.IconButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class MellowScreen extends Screen implements PositionHelper {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Screen lastScreen;

    public MellowScreen(Screen lastScreen, ITextComponent title) {
        super(title);
        this.lastScreen = lastScreen;
    }

    public void footer() {
        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.footerButtonHeight(this.height), 200, 20,
                DialogTexts.GUI_DONE, button -> this.onClose()));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Nullable
    public static List<IReorderingProcessor> tooltipAt(OptionsRowList list, int mouseX, int mouseY) {
        Optional<Widget> widget = list.getMouseOver(mouseX, mouseY);
        if (widget.isPresent() && widget.get() instanceof IBidiTooltip) {
            Optional<List<IReorderingProcessor>> tooltips = ((IBidiTooltip) widget.get()).getTooltip();
            return tooltips.orElse(null);
        } else {
            return null;
        }
    }

    public void renderTitle(MatrixStack stack) {
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
    }

    // predefined buttons

    public void switchStyle(Button.IPressable whenPressed, int x, int y) {
        this.addButton(new IconButton(x, y, 12, 12, GUITextures.SWITCH_STYLE_SET, new TranslationTextComponent("button.mellowui.switch_style"),
                whenPressed, (button, stack, mouseX, mouseY) ->
                this.components.renderTooltip(this, button, new TranslationTextComponent("button.mellowui.switch_style"), mouseX, mouseY)));
    }
}
