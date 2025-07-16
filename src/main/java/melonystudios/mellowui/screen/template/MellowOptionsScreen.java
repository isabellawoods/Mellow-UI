package melonystudios.mellowui.screen.template;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.function.Consumer;

public abstract class MellowOptionsScreen extends MellowScreen {
    private final Consumer<OptionsList> optionsAdder;
    private OptionsList list;

    public MellowOptionsScreen(Screen lastScreen, Component title, Consumer<OptionsList> optionsAdder) {
        super(lastScreen, title);
        this.optionsAdder = optionsAdder;
    }

    @Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.optionsAdder.accept(this.list);
        this.addWidget(this.list);
        this.footer();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.renderTitle(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<FormattedCharSequence> processors = tooltipAt(this.list, mouseX, mouseY);
        if (!processors.isEmpty()) this.renderTooltip(stack, processors, mouseX, mouseY);
    }
}
