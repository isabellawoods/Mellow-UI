package melonystudios.mellowui.screen.backport;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.list.stats.GeneralStatsList;
import melonystudios.mellowui.screen.list.stats.ItemsStatsList;
import melonystudios.mellowui.screen.list.stats.MobsStatsList;
import melonystudios.mellowui.screen.widget.TabButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
public class StatisticsScreen extends Screen implements IProgressMeter {
    public static final ITextComponent RETRIEVING_STATISTICS = new TranslationTextComponent("multiplayer.downloadingStats");
    public final RenderComponents components = RenderComponents.INSTANCE;
    private final Screen lastScreen;
    private final StatisticsManager manager;
    private boolean isLoading = true;

    // Tabs
    private final List<TabButton> tabs = Lists.newArrayList();
    private GeneralStatsList general;
    private ItemsStatsList items;
    private MobsStatsList mobs;
    @Nullable
    private ExtendedList<?> activeList = null;

    public StatisticsScreen(Screen lastScreen, StatisticsManager manager) {
        super(new TranslationTextComponent("gui.stats"));
        this.lastScreen = lastScreen;
        this.manager = manager;
    }

    public StatisticsManager statisticsManager() {
        return this.manager;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.tabs.get(0).setSelected(true);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        this.isLoading = true;
        if (this.minecraft.getConnection() != null) {
            this.minecraft.getConnection().send(new CClientStatusPacket(CClientStatusPacket.State.REQUEST_STATS));
        }
    }

    public void createLists() {
        this.general = new GeneralStatsList(this, this.minecraft, this.width, this.height, 22, this.height - 32, 14);
        this.general.setRenderBackground(false);
        this.general.setRenderTopAndBottom(false);
        this.items = new ItemsStatsList(this, this.minecraft, this.width, this.height, 22, this.height - 32, 20);
        this.items.setRenderBackground(false);
        this.items.setRenderTopAndBottom(false);
        this.mobs = new MobsStatsList(this, this.minecraft, this.width, this.height, 22, this.height - 32, 14);
        this.mobs.setRenderBackground(false);
        this.mobs.setRenderTopAndBottom(false);
    }

    public void createButtons() {
        // Tabs
        int tabWidth = this.components.threeTabWidth(this.width);
        this.activeList = this.general;
        this.children.add(this.activeList);

        // General
        this.tabs.add(this.addButton(new TabButton(this.width / 2 - tabWidth / 2 - tabWidth, 0, tabWidth, 24, new TranslationTextComponent("stat.generalButton"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.general);
        })));

        // Items
        TabButton itemsTab;
        this.tabs.add(itemsTab = this.addButton(new TabButton(this.width / 2 - tabWidth / 2, 0, tabWidth, 24, new TranslationTextComponent("stat.itemsButton"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.items);
        }, (button, stack, mouseX, mouseY) -> {
            if (!button.active) this.components.renderTooltip(this, button, new TranslationTextComponent("menu.mellowui.statistics.no_statistics_found"), mouseX, mouseY);
        })));
        itemsTab.active = !this.items.children().isEmpty();

        // Mobs
        TabButton mobsTab;
        this.tabs.add(mobsTab = this.addButton(new TabButton(this.width / 2 + tabWidth / 2, 0, tabWidth, 24, new TranslationTextComponent("stat.mobsButton"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.mobs);
        }, (button, stack, mouseX, mouseY) -> {
            if (!button.active) this.components.renderTooltip(this, button, new TranslationTextComponent("menu.mellowui.statistics.no_statistics_found"), mouseX, mouseY);
        })));
        mobsTab.active = !this.mobs.children().isEmpty();

        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));

        this.tabs.get(0).setSelected(true);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        if (this.isLoading) {
            drawCenteredString(stack, this.font, RETRIEVING_STATISTICS, this.width / 2, this.height / 2, 0xFFFFFF);
            drawCenteredString(stack, this.font, LOADING_SYMBOLS[(int) (Util.getMillis() / 150L % (long) LOADING_SYMBOLS.length)], this.width / 2, this.height / 2 + 9 * 2, 0xFFFFFF);
        } else {
            if (!MellowConfigs.CLIENT_CONFIGS.updateListBackground.get()) {
                this.components.enableScissor(this.activeList.getLeft(), this.activeList.getTop() + 2, this.activeList.getRight(), this.activeList.getBottom());
                if (this.getActiveList() != null) this.getActiveList().render(stack, mouseX, mouseY, partialTicks);
                this.components.disableScissor();
            } else {
                this.components.renderTabHeaderBackground(0, 0, this.width, 24);
                if (this.getActiveList() != null) this.getActiveList().render(stack, mouseX, mouseY, partialTicks);
            }
            this.components.renderListSeparators(this.width, 0, this.height - 32, 22, 3, this.components.threeTabWidth(this.width));
            super.render(stack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void renderDirtBackground(int vOffset) {
        if (MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) this.components.renderMenuBackground(0, 24, this.width, this.height, vOffset);
        else super.renderDirtBackground(vOffset);
    }

    @Override
    public void onStatsUpdated() {
        if (this.isLoading) {
            this.createLists();
            this.createButtons();
            this.selectList(this.general);
            this.isLoading = false;
        }
    }

    @Nullable
    public ExtendedList<?> getActiveList() {
        return this.activeList;
    }

    private void selectList(ExtendedList<?> list) {
        this.children.remove(this.general);
        this.children.remove(this.items);
        this.children.remove(this.mobs);
        if (list != null) {
            this.children.add(0, list);
            this.activeList = list;
        }
    }

    public static String getTranslationKey(Stat<ResourceLocation> stat) {
        return "stat." + stat.getValue().toString().replace(':', '.');
    }

    public int getColumnX(int index) {
        return 115 + 40 * index;
    }

    public void blitSlot(MatrixStack stack, int x, int y, Item item) {
        this.blitSlotIcon(stack, x + 1, y + 1, 0, 0);
        RenderSystem.enableRescaleNormal();
        this.itemRenderer.renderGuiItem(item.getDefaultInstance(), x + 2, y + 2);
        RenderSystem.disableRescaleNormal();
    }

    public void blitSlotIcon(MatrixStack stack, int x, int y, int width, int height) {
        RenderSystem.color4f(1, 1, 1, 1);
        this.minecraft.getTextureManager().bind(STATS_ICON_LOCATION);
        blit(stack, x, y, this.getBlitOffset(), (float) width, (float) height, 18, 18, 128, 128);
    }
}
