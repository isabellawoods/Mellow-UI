package melonystudios.mellowui.screen.list.stats;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.screen.backport.StatisticsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Comparator;

@OnlyIn(Dist.CLIENT)
public class GeneralStatsList extends ExtendedList<GeneralStatsList.Entry> {
    private final StatisticsScreen parentScreen;

    public GeneralStatsList(StatisticsScreen parentScreen, Minecraft minecraft, int width, int height, int y0, int y1, int entryWidth) {
        super(minecraft, width, height, y0, y1, entryWidth);
        this.parentScreen = parentScreen;
        this.setRenderHeader(true, 2);
        this.setRenderSelection(false);
        ObjectArrayList<Stat<ResourceLocation>> customStats = new ObjectArrayList<>(Stats.CUSTOM.iterator());
        customStats.sort(Comparator.comparing(stat -> I18n.get(StatisticsScreen.getTranslationKey(stat))));
        for (Stat<ResourceLocation> stat : customStats) this.addEntry(new Entry(stat));
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

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ExtendedList.AbstractListEntry<GeneralStatsList.Entry> {
        private final Stat<ResourceLocation> stat;
        private final ITextComponent name;

        public Entry(Stat<ResourceLocation> stat) {
            this.stat = stat;
            this.name = new TranslationTextComponent(StatisticsScreen.getTranslationKey(stat));
        }

        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            // Render selection
            int color = TextComponents.selectableColor(GeneralStatsList.this.getSelected() == this, true);
            if (GeneralStatsList.this.getSelected() == this) RenderComponents.INSTANCE.renderListSelection(left, top, width, height, color);

            FontRenderer font = GeneralStatsList.this.minecraft.font;
            drawString(stack, font, this.name, left + 2, top + 1, index % 2 == 0 ? 0xFFFFFF : 0xBBBBBB);
            String value = this.stat.format(GeneralStatsList.this.parentScreen.statisticsManager().getValue(this.stat));
            drawString(stack, font, value, left + GeneralStatsList.this.getRowWidth() - 8 - font.width(value), top + 1, index % 2 == 0 ? 0xFFFFFF : 0xBBBBBB);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0) {
                GeneralStatsList.this.setSelected(this);
                return true;
            } else {
                return false;
            }
        }
    }
}
