package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.screen.MellowCustomizationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ThemeList extends ObjectSelectionList<ThemeList.Entry> {
    private final MellowCustomizationScreen parentScreen;
    private final Minecraft minecraft;

    public ThemeList(Minecraft minecraft, MellowCustomizationScreen parentScreen) {
        super(minecraft, parentScreen.width, parentScreen.height, 22, parentScreen.height - 32, 36);
        this.minecraft = minecraft;
        this.parentScreen = parentScreen;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
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
    protected void renderList(PoseStack stack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        super.renderList(stack, x, y, mouseX, mouseY, partialTicks);
        stack.pushPose();
        stack.scale(1.5F, 1.5F, 1.5F);
        drawCenteredString(stack, this.minecraft.font, new TranslatableComponent("menu.mellowui.customization.theme.title").withStyle(TextComponents.selectableStyle(true, true).withBold(true)),
                this.width / 3, this.height / 3 - 15, 0xFFFFFF);
        stack.popPose();

        List<FormattedCharSequence> lines = this.minecraft.font.split(new TranslatableComponent("menu.mellowui.customization.theme.desc").withStyle(TextComponents.descriptionStyle()), this.width - 50);
        int yOffset = this.height / 2;
        for (FormattedCharSequence line : lines) {
            this.minecraft.font.drawShadow(stack, line, this.width / 2 - this.minecraft.font.width(line) / 2, yOffset, 0xFFFFFF);
            yOffset += this.minecraft.font.lineHeight + 1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ObjectSelectionList.Entry<Entry> {
        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {}

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0) {
                ThemeList.this.setSelected(this);
                ThemeList.this.setFocused(this);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, item);
        }

        @Override
        @NotNull
        public Component getNarration() {
            return TextComponent.EMPTY;
        }
    }
}
