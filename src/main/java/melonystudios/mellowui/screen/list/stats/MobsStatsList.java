package melonystudios.mellowui.screen.list.stats;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.backport.StatisticsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class MobsStatsList extends ObjectSelectionList<MobsStatsList.Entry> {
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

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ObjectSelectionList.Entry<MobsStatsList.Entry> {
        private final EntityType<?> type;
        private final Component mobName;
        private final Component kills;
        private final boolean hasKills;
        private final Component killedBy;
        private final boolean wasKilledBy;

        public Entry(EntityType<?> type) {
            this.type = type;
            this.mobName = type.getDescription();
            int kills = MobsStatsList.this.parentScreen.statisticsManager().getValue(Stats.ENTITY_KILLED.get(type));
            if (kills == 0) {
                this.kills = new TranslatableComponent("stat_type.minecraft.killed.none", this.mobName);
                this.hasKills = false;
            } else {
                this.kills = new TranslatableComponent("stat_type.minecraft.killed", kills, this.mobName);
                this.hasKills = true;
            }

            int killedBy = MobsStatsList.this.parentScreen.statisticsManager().getValue(Stats.ENTITY_KILLED_BY.get(type));
            if (killedBy == 0) {
                this.killedBy = new TranslatableComponent("stat_type.minecraft.killed_by.none", this.mobName);
                this.wasKilledBy = false;
            } else {
                this.killedBy = new TranslatableComponent("stat_type.minecraft.killed_by", this.mobName, killedBy);
                this.wasKilledBy = true;
            }
        }

        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            Font font = MobsStatsList.this.parentScreen.getMinecraft().font;
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

        @Override
        @NotNull
        public Component getNarration() {
            return new TranslatableComponent("narrator.select", this.mobName);
        }
    }
}
