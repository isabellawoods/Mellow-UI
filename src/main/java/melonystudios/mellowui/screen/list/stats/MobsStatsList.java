package melonystudios.mellowui.screen.list.stats;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.backport.StatisticsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class MobsStatsList extends ObjectSelectionList<MobsStatsList.Entry> {
    private final StatisticsScreen parentScreen;

    public MobsStatsList(StatisticsScreen parentScreen, Minecraft minecraft, int width, int height, int y0, int y1, int entryWidth) {
        super(minecraft, width, height, y0, y1, entryWidth);
        this.parentScreen = parentScreen;
    }

    @Override
    public int getRowWidth() {
        return 375;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width / 2 + 140;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Entry extends ObjectSelectionList.Entry<MobsStatsList.Entry> {
        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {

        }

        @Override
        @NotNull
        public Component getNarration() {
            return new TranslatableComponent("narrator.select", "Nothing");
        }
    }
}
