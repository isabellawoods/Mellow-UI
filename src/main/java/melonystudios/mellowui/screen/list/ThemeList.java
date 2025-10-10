package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.MellowCustomizationScreen;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ThemeList extends ExtendedList<ThemeList.Entry> {
    private final MellowCustomizationScreen parentScreen;
    private final Minecraft minecraft;

    public ThemeList(Minecraft minecraft, MellowCustomizationScreen parentScreen) {
        super(minecraft, parentScreen.width, parentScreen.height, 22, parentScreen.height - 32, 36);
        this.minecraft = minecraft;
        this.parentScreen = parentScreen;
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener listener) {
        super.setFocused(listener);
        this.parentScreen.setFocused(listener);
    }

    @Override
    protected boolean isFocused() {
        return this.parentScreen.getFocused() == this;
    }

    @Override
    public int getRowWidth() {
        return 280;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width / 2 + 148;
    }

    @Override
    protected void renderList(MatrixStack stack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        super.renderList(stack, x, y, mouseX, mouseY, partialTicks);
        stack.pushPose();
        stack.scale(1.5F, 1.5F, 1.5F);
        drawCenteredString(stack, this.minecraft.font, new TranslationTextComponent("menu.mellowui.customization.theme.title")
                        .withStyle(MellowUtils.withColor(MellowUtils.getSelectableTextColor(true, true)).withBold(true)),
                this.width / 3, this.height / 3 - 15, 0xFFFFFF);
        stack.popPose();

        List<IReorderingProcessor> processors = this.minecraft.font.split(new TranslationTextComponent("menu.mellowui.customization.theme.desc").withStyle(TextComponents.descriptionStyle()), this.width - 50);
        int yOffset = this.height / 2;
        for (IReorderingProcessor processor : processors) {
            this.minecraft.font.drawShadow(stack, processor, this.width / 2 - this.minecraft.font.width(processor) / 2, yOffset, 0xFFFFFF);
            yOffset += this.minecraft.font.lineHeight + 1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ExtendedList.AbstractListEntry<ThemeList.Entry> {
        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {}

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0) {
                ThemeList.this.setSelected(this);
                ThemeList.this.setFocused(this);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, item);
        }
    }
}
