package melonystudios.mellowui.screen.list.stats;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import melonystudios.mellowui.screen.backport.StatisticsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

@OnlyIn(Dist.CLIENT)
public class GeneralStatsList extends ObjectSelectionList<GeneralStatsList.Entry> {
    private final StatisticsScreen parentScreen;

    public GeneralStatsList(StatisticsScreen parentScreen, Minecraft minecraft, int width, int height, int y0, int y1, int entryWidth) {
        super(minecraft, width, height, y0, y1, entryWidth);
        this.parentScreen = parentScreen;
        ObjectArrayList<Stat<ResourceLocation>> customStats = new ObjectArrayList<>(Stats.CUSTOM.iterator());
        customStats.sort(Comparator.comparing(stat -> I18n.get(StatisticsScreen.getTranslationKey(stat))));
        for (Stat<ResourceLocation> stat : customStats) this.addEntry(new Entry(stat));
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
        return 350;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width / 2 + 140;
    }

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ObjectSelectionList.Entry<GeneralStatsList.Entry> {
        private final Stat<ResourceLocation> stat;
        private final Component name;

        public Entry(Stat<ResourceLocation> stat) {
            this.stat = stat;
            this.name = new TranslatableComponent(StatisticsScreen.getTranslationKey(stat));
        }

        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            Font font = GeneralStatsList.this.minecraft.font;
            drawString(stack, font, this.name, left + 2, top + 1, index % 2 == 0 ? 0xFFFFFF : 0xAAAAAA);
            String value = this.stat.format(GeneralStatsList.this.parentScreen.statisticsManager().getValue(this.stat));
            drawString(stack, font, value, left + 320 - font.width(value), top + 1, index % 2 == 0 ? 0xFFFFFF : 0xAAAAAA);
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

        @Override
        @NotNull
        public Component getNarration() {
            return new TranslatableComponent("narrator.select", this.name);
        }
    }
}
