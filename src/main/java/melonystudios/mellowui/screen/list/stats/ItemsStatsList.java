package melonystudios.mellowui.screen.list.stats;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TooltipDisplayData;
import melonystudios.mellowui.element.text.TooltipProvider;
import melonystudios.mellowui.screen.backport.StatisticsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ItemsStatsList extends ObjectSelectionList<ItemsStatsList.Entry> implements TooltipProvider {
    private final StatisticsScreen parentScreen;
    private TooltipDisplayData tooltipData;
    protected final List<StatType<Block>> blockColumns;
    protected final List<StatType<Item>> itemColumns;
    private final int[] iconOffsets = new int[] {3, 4, 1, 2, 5, 6};
    protected int headerPressed = -1;
    protected final List<Item> statItemList;
    protected final Comparator<Item> itemStatSorter = new StatComparator();
    @Nullable
    protected StatType<?> sortColumn;
    protected int sortOrder;

    public ItemsStatsList(StatisticsScreen parentScreen, Minecraft minecraft, int width, int height, int y0, int y1, int entryWidth) {
        super(minecraft, width, height, y0, y1, entryWidth);
        this.parentScreen = parentScreen;
        this.blockColumns = Lists.newArrayList();
        this.blockColumns.add(Stats.BLOCK_MINED);
        this.itemColumns = Lists.newArrayList(Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED);
        this.setRenderHeader(true, 21);
        Set<Item> items = Sets.newIdentityHashSet();

        for (Item item : ForgeRegistries.ITEMS) {
            boolean hasItem = false;

            for (StatType<Item> type : this.itemColumns) {
                if (type.contains(item) && this.parentScreen.statisticsManager().getValue(type.get(item)) > 0) {
                    hasItem = true;
                }
            }

            if (hasItem) items.add(item);
        }

        for (Block block : ForgeRegistries.BLOCKS) {
            boolean hasBlock = false;

            for (StatType<Block> type : this.blockColumns) {
                if (type.contains(block) && this.parentScreen.statisticsManager().getValue(type.get(block)) > 0) hasBlock = true;
            }

            if (hasBlock) items.add(block.asItem());
        }

        items.remove(Items.AIR);
        this.statItemList = Lists.newArrayList(items);

        for (int i = 0; i < this.statItemList.size(); ++i) this.addEntry(new Entry());
    }

    @Override
    public TooltipDisplayData tooltipData() {
        return this.tooltipData;
    }

    @Override
    public void setTooltipData(TooltipDisplayData data) {
        this.tooltipData = data;
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
    protected void renderHeader(PoseStack stack, int x, int y, Tesselator tessellator) {
        if (!this.minecraft.mouseHandler.isLeftPressed()) this.headerPressed = -1;

        for (int i = 0; i < this.iconOffsets.length; ++i) {
            this.parentScreen.blitSlotIcon(stack, x + StatisticsScreen.getColumnX(i) - 18, y + 1, 0, this.headerPressed == i ? 0 : 18);
        }

        if (this.sortColumn != null) {
            int xOffset = StatisticsScreen.getColumnX(this.getColumnIndex(this.sortColumn)) - 36;
            int width = this.sortOrder == 1 ? 2 : 1;
            this.parentScreen.blitSlotIcon(stack, x + xOffset, y + 1, 18 * width, 0);
        }

        for (int i = 0; i < this.iconOffsets.length; ++i) {
            int offset = this.headerPressed == i ? 1 : 0;
            this.parentScreen.blitSlotIcon(stack, x + StatisticsScreen.getColumnX(i) - 18 + offset, y + 1 + offset, 18 * this.iconOffsets[i], 18);
        }
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
    protected void clickedHeader(int x, int y) {
        this.headerPressed = -1;

        for (int i = 0; i < this.iconOffsets.length; ++i) {
            int position = x - StatisticsScreen.getColumnX(i);
            if (position >= -36 && position <= 0) {
                this.headerPressed = i;
                break;
            }
        }

        if (this.headerPressed >= 0) {
            this.sortByColumn(this.getColumn(this.headerPressed));
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
        }
    }

    private StatType<?> getColumn(int index) {
        return index < this.blockColumns.size() ? this.blockColumns.get(index) : this.itemColumns.get(index - this.blockColumns.size());
    }

    private int getColumnIndex(StatType<?> type) {
        int blockIndex = this.blockColumns.indexOf(type);
        if (blockIndex >= 0) {
            return blockIndex;
        } else {
            int itemIndex = this.itemColumns.indexOf(type);
            return itemIndex >= 0 ? itemIndex + this.blockColumns.size() : -1;
        }
    }

    @Override
    protected void renderDecorations(PoseStack stack, int mouseX, int mouseY) {
        if (mouseY >= this.y0 && mouseY <= this.y1) {
            Entry entry = this.getEntryAtPosition(mouseX, mouseY);
            Component text = null;
            int i = (this.width - this.getRowWidth()) / 2;
            int pos = mouseX - i;
            if (entry != null) return;

            for (int k = 0; k < this.iconOffsets.length; ++k) {
                int l = StatisticsScreen.getColumnX(k) + 1;
                if (pos >= l - 17 && pos <= l) {
                    text = this.getColumn(k).getDisplayName();
                    break;
                }
            }

            if (text != null) this.setTooltipData(new TooltipDisplayData(text, RenderComponents.TOOLTIP_MAX_WIDTH, mouseX, mouseY));
        }
    }

    protected void sortByColumn(StatType<?> type) {
        if (type != this.sortColumn) {
            this.sortColumn = type;
            this.sortOrder = -1;
        } else if (this.sortOrder == -1) {
            this.sortOrder = 1;
        } else {
            this.sortColumn = null;
            this.sortOrder = 0;
        }

        this.statItemList.sort(this.itemStatSorter);
    }

    @OnlyIn(Dist.CLIENT)
    public class StatComparator implements Comparator<Item> {
        public StatComparator() {}

        @Override
        @SuppressWarnings("unchecked")
        public int compare(Item itemA, Item itemB) {
            StatsCounter manager = ItemsStatsList.this.parentScreen.statisticsManager();
            int block;
            int item;
            if (ItemsStatsList.this.sortColumn == null) {
                block = 0;
                item = 0;
            } else if (ItemsStatsList.this.blockColumns.contains(ItemsStatsList.this.sortColumn)) {
                StatType<Block> blockStat = (StatType<Block>) ItemsStatsList.this.sortColumn;
                block = itemA instanceof BlockItem ? manager.getValue(blockStat, ((BlockItem) itemA).getBlock()) : -1;
                item = itemB instanceof BlockItem ? manager.getValue(blockStat, ((BlockItem) itemB).getBlock()) : -1;
            } else {
                StatType<Item> itemStat = (StatType<Item>) ItemsStatsList.this.sortColumn;
                block = manager.getValue(itemStat, itemA);
                item = manager.getValue(itemStat, itemB);
            }

            return block == item ? ItemsStatsList.this.sortOrder * Integer.compare(Item.getId(itemA), Item.getId(itemB)) : ItemsStatsList.this.sortOrder * Integer.compare(block, item);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ObjectSelectionList.Entry<ItemsStatsList.Entry> {
        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            Item item = ItemsStatsList.this.statItemList.get(index);
            boolean hovered = this.isHovered(mouseX, mouseY, left - 1, top - 1, 18, 18);
            ItemsStatsList.this.parentScreen.blitSlot(stack, left - 2, top - 2, item, hovered || ItemsStatsList.this.getSelected() == this);

            for (int i = 0; i < ItemsStatsList.this.blockColumns.size(); ++i) {
                Stat<Block> stat;
                if (item instanceof BlockItem) {
                    stat = ItemsStatsList.this.blockColumns.get(i).get(((BlockItem)item).getBlock());
                } else {
                    stat = null;
                }

                this.renderStat(stack, stat, left + StatisticsScreen.getColumnX(i), top, index % 2 == 0);
            }

            for (int column = 0; column < ItemsStatsList.this.itemColumns.size(); ++column) {
                this.renderStat(stack, ItemsStatsList.this.itemColumns.get(column).get(item), left + StatisticsScreen.getColumnX(column + ItemsStatsList.this.blockColumns.size()), top, index % 2 == 0);
            }

            if (hovered) {
                List<FormattedCharSequence> lines = TooltipProvider.getVisualOrder(ItemsStatsList.this.parentScreen.getTooltipFromItem(item.getDefaultInstance()));
                ItemsStatsList.this.setTooltipData(new TooltipDisplayData(lines, left + 15, top + 10));
            }
        }

        protected void renderStat(PoseStack stack, @Nullable Stat<?> stat, int x, int y, boolean odd) {
            String text = stat == null ? I18n.get("menu.mellowui.statistics.not_applicable") : stat.format(ItemsStatsList.this.parentScreen.statisticsManager().getValue(stat));
            drawString(stack, ItemsStatsList.this.parentScreen.getMinecraft().font, text, x - ItemsStatsList.this.parentScreen.getMinecraft().font.width(text), y + 5, odd ? 0xFFFFFF : 0xBBBBBB);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0) {
                ItemsStatsList.this.setSelected(this);
                return true;
            } else {
                return false;
            }
        }

        public boolean isHovered(double mouseX, double mouseY, int x, int y, int width, int height) {
            return mouseX >= (double) x && mouseY >= (double) y && mouseX < (double) (x + width) && mouseY < (double) (y + height);
        }

        @Override
        @NotNull
        public Component getNarration() {
            return TextComponent.EMPTY;
        }
    }
}
