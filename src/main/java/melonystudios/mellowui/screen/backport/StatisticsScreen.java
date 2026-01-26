package melonystudios.mellowui.screen.backport;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.text.TooltipProvider;
import melonystudios.mellowui.element.widget.TabButton;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.screen.list.stats.GeneralStatsList;
import melonystudios.mellowui.screen.list.stats.ItemsStatsList;
import melonystudios.mellowui.screen.list.stats.MobsStatsList;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class StatisticsScreen extends Screen implements StatsUpdateListener {
    public static final Component RETRIEVING_STATISTICS = new TranslatableComponent("multiplayer.downloadingStats");
    public final RenderComponents components = RenderComponents.INSTANCE;
    private final Screen lastScreen;
    private final StatsCounter manager;
    private boolean isLoading = true;
    @Nullable
    private EditBox textBackground;

    // Tabs
    private final List<TabButton> tabs = Lists.newArrayList();
    private String selectedTab = "general";
    private GeneralStatsList general;
    private ItemsStatsList items;
    private MobsStatsList mobs;
    @Nullable
    private ObjectSelectionList<?> activeList = null;

    // Loading
    public static final long STATISTICS_RECEIVAL_WAIT_LIMIT_MS = 2000L;
    private Button doneButton;
    private final long createdAt;
    private float textAlpha = 0;

    public StatisticsScreen(Screen lastScreen, StatsCounter manager) {
        super(new TranslatableComponent("gui.stats").withStyle(TextComponents.titleStyle()));
        this.lastScreen = lastScreen;
        this.manager = manager;
        this.createdAt = System.currentTimeMillis();
    }

    public StatsCounter statisticsManager() {
        return this.manager;
    }

    @Override
    public void tick() {
        if (System.currentTimeMillis() > this.createdAt + STATISTICS_RECEIVAL_WAIT_LIMIT_MS) {
            this.textAlpha = Mth.clamp(this.textAlpha + 0.06F, 0, 0.7F);
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        this.isLoading = true;

        // Retrieving statistics background
        this.textBackground = new EditBox(this.font, this.width / 2, this.height / 2, this.font.width(RETRIEVING_STATISTICS) + 20, 30, RETRIEVING_STATISTICS);
        this.textBackground.setMaxLength(128);
        this.textBackground.setEditable(false);
        this.textBackground.x = this.width / 2 - this.textBackground.getWidth() / 2;
        this.textBackground.y = ((this.height / 2) - 9 / 2) - 7;
        this.addWidget(this.textBackground);

        // Done button
        this.doneButton = WidgetComponents.components(this, this::addRenderableWidget).done(Alignment.CENTER);

        if (this.minecraft.getConnection() != null) {
            this.minecraft.getConnection().send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
        }
    }

    public void createLists() {
        this.general = new GeneralStatsList(this, this.minecraft, this.width, this.height, 24, this.height - 33, 14);
        this.general.setRenderBackground(false);
        this.general.setRenderTopAndBottom(false);
        this.items = new ItemsStatsList(this, this.minecraft, this.width, this.height, 24, this.height - 33, 20);
        this.items.setRenderBackground(false);
        this.items.setRenderTopAndBottom(false);
        this.mobs = new MobsStatsList(this, this.minecraft, this.width, this.height, 24, this.height - 33, 36);
        this.mobs.setRenderBackground(false);
        this.mobs.setRenderTopAndBottom(false);
    }

    public void createButtons() {
        // Tabs
        int tabWidth = this.components.threeTabWidth(this.width);
        this.activeList = this.general;
        this.addWidget(this.activeList);
        this.tabs.clear();

        // General
        this.tabs.add(this.addRenderableWidget(new TabButton(this.width / 2 - tabWidth / 2 - tabWidth, 0, tabWidth, 24, "general", new TranslatableComponent("stat.generalButton"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectedTab = ((TabButton) button).tabName();
            this.selectList(this.general);
        })));

        // Items
        TabButton itemsTab;
        this.tabs.add(itemsTab = this.addRenderableWidget(new TabButton(this.width / 2 - tabWidth / 2, 0, tabWidth, 24, "items", new TranslatableComponent("stat.itemsButton"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectedTab = ((TabButton) button).tabName();
            this.selectList(this.items);
        }, (button, stack, mouseX, mouseY) -> {
            if (!button.active) this.components.renderTooltip(this, button, new TranslatableComponent("menu.mellowui.statistics.no_statistics_found"), mouseX, mouseY);
        })));
        itemsTab.active = !this.items.children().isEmpty();

        // Mobs
        TabButton mobsTab;
        this.tabs.add(mobsTab = this.addRenderableWidget(new TabButton(this.width / 2 + tabWidth / 2, 0, tabWidth, 24, "mobs", new TranslatableComponent("stat.mobsButton"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectedTab = ((TabButton) button).tabName();
            this.selectList(this.mobs);
        }, (button, stack, mouseX, mouseY) -> {
            if (!button.active) this.components.renderTooltip(this, button, new TranslatableComponent("menu.mellowui.statistics.no_statistics_found"), mouseX, mouseY);
        })));
        mobsTab.active = !this.mobs.children().isEmpty();

        this.tabs.stream().filter(tab -> tab.tabName().equals(this.selectedTab)).findFirst().ifPresent(tab -> {
            tab.setSelected(true);
            this.selectList(this.byName(this.selectedTab));
        });
    }

    private ObjectSelectionList<?> byName(String selectedTab) {
        return switch (selectedTab) {
            case "items" -> this.items;
            case "mobs" -> this.mobs;
            default -> this.general;
        };
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        if (this.isLoading) {
            if (this.textBackground != null) this.textBackground.render(stack, mouseX, mouseY, partialTicks);
            if (this.doneButton != null) {
                this.doneButton.setAlpha(this.textAlpha);
                this.doneButton.active = this.doneButton.visible = this.textAlpha > 0F;
                this.doneButton.render(stack, mouseX, mouseY, partialTicks);
            }
            int textAlpha = Mth.ceil(this.textAlpha * 255) << 24;
            if (this.textAlpha > 0F) this.components.drawCenteredString(new TranslatableComponent("menu.mellowui.statistics.too_long").withStyle(TextComponents.descriptionStyle().withItalic(true)),
                    true, this.width / 2, this.height / 2 + 26, 0xFFFFFF | textAlpha);

            this.components.drawCenteredString(RETRIEVING_STATISTICS, true, this.width / 2, this.height / 2 - 5, 0xFFFFFF);
            this.components.drawCenteredString(new TextComponent(LOADING_SYMBOLS[(int) (Util.getMillis() / 150L % (long) LOADING_SYMBOLS.length)]).withStyle(ChatFormatting.GRAY),
                    true, this.width / 2, this.height / 2 + 4, 0xFFFFFF);
        } else {
            this.doneButton.setAlpha(1);
            this.doneButton.active = this.doneButton.visible = true;

            if (!MellowConfigs.CLIENT_CONFIGS.listBackgroundStyle.get()) {
                this.components.enableScissor(this.activeList.getLeft(), this.activeList.getTop() + 2, this.activeList.getRight(), this.activeList.getBottom());
                if (this.getActiveList() != null) this.getActiveList().render(stack, mouseX, mouseY, partialTicks);
                this.components.disableScissor();
            } else {
                this.components.renderTabHeaderBackground(0, 0, this.width, 24);
                if (this.getActiveList() != null) this.getActiveList().render(stack, mouseX, mouseY, partialTicks);
            }
            this.components.renderListSeparators(this.width, 0, this.height - 33, 24, 3, this.components.threeTabWidth(this.width));

            super.render(stack, mouseX, mouseY, partialTicks);
            if (this.getActiveList() instanceof TooltipProvider provider && provider.tooltipData() != null) provider.renderTooltip(stack, this);
        }
    }

    @Override
    public void renderDirtBackground(int vOffset) {
        if (MellowConfigs.CLIENT_CONFIGS.screenBackgroundStyle.get()) this.components.renderMenuBackground(0, this.isLoading ? 0 : 24, this.width, this.height, vOffset);
        else super.renderDirtBackground(vOffset);
    }

    @Override
    public void onStatsUpdated() {
        if (this.isLoading) {
            this.createLists();
            this.createButtons();
            this.isLoading = false;
        }
    }

    @Nullable
    public ObjectSelectionList<?> getActiveList() {
        return this.activeList;
    }

    private void selectList(ObjectSelectionList<?> list) {
        this.removeWidget(this.general);
        this.removeWidget(this.items);
        this.removeWidget(this.mobs);
        if (list != null) {
            this.addWidget(list);
            this.activeList = list;
        }
    }

    public static String getTranslationKey(Stat<ResourceLocation> stat) {
        return "stat." + stat.getValue().toString().replace(':', '.');
    }

    public static int getColumnX(int index) {
        return 74 + 40 * index;
    }

    public void blitSlot(PoseStack stack, int x, int y, Item item, boolean hovered) {
        this.blitSlotIcon(stack, x + 1, y + 1, 0, 0);
        if (hovered) {
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, GUITextures.STAT_SLOT_HIGHLIGHT_BACK);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            blit(stack, x - 2, y - 2, 0, 0, 24, 24, 24, 24);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        }
        this.itemRenderer.renderGuiItem(item.getDefaultInstance(), x + 2, y + 2);
        if (hovered) {
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, GUITextures.STAT_SLOT_HIGHLIGHT_FRONT);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            blit(stack, x - 2, y - 2, 0, 0, 24, 24, 24, 24);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        }
    }

    public void blitSlotIcon(PoseStack stack, int x, int y, int width, int height) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, STATS_ICON_LOCATION);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        blit(stack, x, y, this.getBlitOffset(), (float) width, (float) height, 18, 18, 128, 128);
        RenderSystem.disableBlend();
    }

    @Override
    public void renderTooltip(PoseStack stack, ItemStack stack1, int mouseX, int mouseY) {
        super.renderTooltip(stack, stack1, mouseX, mouseY);
    }
}
