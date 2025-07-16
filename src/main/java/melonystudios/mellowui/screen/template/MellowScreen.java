package melonystudios.mellowui.screen.template;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.widget.IconButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class MellowScreen extends Screen implements PositionHelper {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Screen lastScreen;

    public MellowScreen(Screen lastScreen, Component title) {
        super(title);
        this.lastScreen = lastScreen;
    }

    public void footer() {
        // Done button
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.footerButtonHeight(this.height), 200, 20, CommonComponents.GUI_DONE,
                button -> this.onClose()));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    public static List<FormattedCharSequence> tooltipAt(OptionsList list, int mouseX, int mouseY) {
        Optional<AbstractWidget> widget = list.getMouseOver(mouseX, mouseY);
        if (widget.isPresent() && widget.get() instanceof TooltipAccessor) {
            return ((TooltipAccessor) widget.get()).getTooltip();
        } else {
            return Lists.newArrayList();
        }
    }

    public void renderTitle(PoseStack stack) {
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
    }

    // predefined buttons

    public void switchStyle(Button.OnPress onPress, int x, int y) {
        this.addRenderableWidget(new IconButton(x, y, 12, 12, GUITextures.SWITCH_STYLE_SET, new TranslatableComponent("button.mellowui.switch_style"),
                onPress, (button, stack, mouseX, mouseY) ->
                this.components.renderTooltip(this, button, new TranslatableComponent("button.mellowui.switch_style"), mouseX, mouseY)));
    }
}
