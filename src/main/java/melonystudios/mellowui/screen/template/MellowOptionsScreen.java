package melonystudios.mellowui.screen.template;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.function.Consumer;

public abstract class MellowOptionsScreen extends MellowScreen {
    private final Consumer<OptionsRowList> optionsAdder;
    private OptionsRowList list;

    public MellowOptionsScreen(Screen lastScreen, ITextComponent title, Consumer<OptionsRowList> optionsAdder) {
        super(lastScreen, title);
        this.optionsAdder = optionsAdder;
    }

    @Override
    protected void init() {
        this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.optionsAdder.accept(this.list);
        this.children.add(this.list);
        this.footer();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.renderTitle(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> processors = tooltipAt(this.list, mouseX, mouseY);
        if (processors != null) this.renderTooltip(stack, processors, mouseX, mouseY);
    }
}
