package melonystudios.mellowui.screen.list.stats;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.backport.StatisticsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.entity.EntityType;
import net.minecraft.stats.Stats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class MobsStatsList extends ExtendedList<MobsStatsList.Entry> {
    private final StatisticsScreen parentScreen;

    public MobsStatsList(StatisticsScreen parentScreen, Minecraft minecraft, int width, int height, int y0, int y1, int entryWidth) {
        super(minecraft, width, height, y0, y1, entryWidth);
        this.parentScreen = parentScreen;
        this.setRenderHeader(true, 2);

        for (EntityType<?> type : ForgeRegistries.ENTITIES) {
            if (this.parentScreen.statisticsManager().getValue(Stats.ENTITY_KILLED.get(type)) > 0 || this.parentScreen.statisticsManager().getValue(Stats.ENTITY_KILLED_BY.get(type)) > 0) {
                this.addEntry(new Entry(type));
            }
        }
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
    public class Entry extends ExtendedList.AbstractListEntry<MobsStatsList.Entry> {
        private final EntityType<?> type;
        private final ITextComponent mobName;
        private final ITextComponent kills;
        private final boolean hasKills;
        private final ITextComponent killedBy;
        private final boolean wasKilledBy;

        public Entry(EntityType<?> type) {
            this.type = type;
            this.mobName = type.getDescription();
            int kills = MobsStatsList.this.parentScreen.statisticsManager().getValue(Stats.ENTITY_KILLED.get(type));
            if (kills == 0) {
                this.kills = new TranslationTextComponent("stat_type.minecraft.killed.none", this.mobName);
                this.hasKills = false;
            } else {
                this.kills = new TranslationTextComponent("stat_type.minecraft.killed", kills, this.mobName);
                this.hasKills = true;
            }

            int killedBy = MobsStatsList.this.parentScreen.statisticsManager().getValue(Stats.ENTITY_KILLED_BY.get(type));
            if (killedBy == 0) {
                this.killedBy = new TranslationTextComponent("stat_type.minecraft.killed_by.none", this.mobName);
                this.wasKilledBy = false;
            } else {
                this.killedBy = new TranslationTextComponent("stat_type.minecraft.killed_by", this.mobName, killedBy);
                this.wasKilledBy = true;
            }
        }

        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            FontRenderer font = MobsStatsList.this.parentScreen.getMinecraft().font;
            drawString(stack, font, this.mobName, left + 2, top + 1, 0xFFFFFF);
            drawString(stack, font, this.kills, left + 12, top + 10, this.hasKills ? 0xBBBBBB : 0x818181);
            drawString(stack, font, this.killedBy, left + 12, top + 19, this.wasKilledBy ? 0xBBBBBB : 0x818181);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0) {
                MobsStatsList.this.setSelected(this);
                MobsStatsList.this.setFocused(this);
                return true;
            } else {
                return false;
            }
        }
    }
}
