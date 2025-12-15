package melonystudios.mellowui.mixin.screen.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.text.TooltipDisplayData;
import melonystudios.mellowui.element.widget.text.StringWidget;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Objects;

import static melonystudios.mellowui.element.RenderComponents.TOOLTIP_MAX_WIDTH;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class MUIWorldListEntryMixin extends ObjectSelectionList.Entry<WorldSelectionList.WorldListEntry> {
    @Unique public final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow @Final LevelSummary summary;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private SelectWorldScreen screen;
    @Shadow @Final private ResourceLocation iconLocation;
    @Shadow @Final @Nullable private DynamicTexture icon;
    @Unique private StringWidget worldNameText;
    @Unique private StringWidget playSummaryText;
    @Unique private StringWidget infoSummaryText;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        this.createTextWidgets(width, mouseX, mouseY);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, this.icon != null ? this.iconLocation : GUITextures.MISSING_WORLD_ICON);
        RenderSystem.enableBlend();
        GuiComponent.blit(stack, left, top, 0, 0, 32, 32, 32, 32);
        RenderSystem.disableBlend();

        if (this.minecraft.options.touchscreen || isMouseOver) {
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GUITextures.WORLD_SELECTION_OVERLAY);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            GuiComponent.blit(stack, left, top, 0, 0, 32, 32, 32, 32);
            RenderSystem.disableBlend();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GUITextures.WORLD_SELECTION_ICONS);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            int mousePos = mouseX - left;
            boolean iconHovered = mousePos < 32;
            int vOffset = iconHovered ? 32 : 0;

            if (iconHovered) this.components.requestCursor(CursorTypes.POINTING_HAND);

            if (this.summary.isLocked()) {
                GuiComponent.blit(stack, left, top, 96, (float) vOffset, 32, 32, 256, 256);
                if (iconHovered) {
                    this.screen.setToolTip(this.minecraft.font.split(new TranslatableComponent("selectWorld.locked").withStyle(ChatFormatting.RED), TOOLTIP_MAX_WIDTH));
                }
            } else if (this.summary.requiresManualConversion()) {
                GuiComponent.blit(stack, left, top, 96, (float) vOffset, 32, 32, 256, 256);
                if (iconHovered) {
                    this.screen.setToolTip(this.minecraft.font.split(new TranslatableComponent("selectWorld.conversion.tooltip").withStyle(ChatFormatting.RED), TOOLTIP_MAX_WIDTH));
                }
            } else if (this.summary.markVersionInList()) {
                GuiComponent.blit(stack, left, top, 32, (float) vOffset, 32, 32, 256, 256);
                if (this.summary.askToOpenWorld()) {
                    GuiComponent.blit(stack, left, top, 96, (float) vOffset, 32, 32, 256, 256);
                    if (iconHovered) {
                        this.screen.setToolTip(ImmutableList.of(new TranslatableComponent("selectWorld.tooltip.fromNewerVersion1").withStyle(ChatFormatting.RED).getVisualOrderText(), new TranslatableComponent("selectWorld.tooltip.fromNewerVersion2").withStyle(ChatFormatting.RED).getVisualOrderText()));
                    }
                } else if (!SharedConstants.getCurrentVersion().isStable()) {
                    GuiComponent.blit(stack, left, top, 64, (float) vOffset, 32, 32, 256, 256);
                    if (iconHovered) {
                        this.screen.setToolTip(ImmutableList.of(new TranslatableComponent("selectWorld.tooltip.snapshot1").withStyle(ChatFormatting.GOLD).getVisualOrderText(), new TranslatableComponent("selectWorld.tooltip.snapshot2").withStyle(ChatFormatting.GOLD).getVisualOrderText()));
                    }
                }
            } else {
                GuiComponent.blit(stack, left, top, 0, (float) vOffset, 32, 32, 256, 256);
            }
        }
        int textX = left + 32 + 3;

        this.worldNameText.x = textX;
        this.worldNameText.y = top + 1;
        this.worldNameText.render(stack, mouseX, mouseY, partialTicks);
        this.playSummaryText.x = textX;
        this.playSummaryText.y = top + 9 + 3;
        this.playSummaryText.render(stack, mouseX, mouseY, partialTicks);
        this.infoSummaryText.x = textX;
        this.infoSummaryText.y = top + 9 + 9 + 3;
        this.infoSummaryText.render(stack, mouseX, mouseY, partialTicks);
    }

    @Unique
    @SuppressWarnings("deprecation")
    private void createTextWidgets(int width, int mouseX, int mouseY) {
        int maxWidth = width - 32 - 3;

        Component worldName = new TextComponent(this.summary.getLevelName());
        this.worldNameText = new StringWidget(worldName, this.minecraft.font).setColor(TextComponents.selectableColor(
                Objects.equals(this.list == null || this.list.getSelected() == null ? 0 :
                        this.list.getSelected().hashCode(), this.hashCode()), true)
        );
        this.worldNameText.setMaxWidth(maxWidth);
        if (this.minecraft.font.width(worldName) > maxWidth) {
            this.worldNameText.setTooltipData(new TooltipDisplayData(worldName, TOOLTIP_MAX_WIDTH, mouseX, mouseY));
        }

        Component playSummary = new TranslatableComponent("selectWorld.world_info", this.summary.getLevelId(), MellowUtils.WORLD_DATE_FORMAT.format(new Date(this.summary.getLastPlayed())));
        this.playSummaryText = new StringWidget(playSummary, this.minecraft.font).setColor(0x808080);
        this.playSummaryText.setMaxWidth(maxWidth);
        if (this.minecraft.font.width(playSummary) > maxWidth) {
            this.playSummaryText.setTooltipData(new TooltipDisplayData(playSummary, TOOLTIP_MAX_WIDTH, mouseX, mouseY));
        }

        Component infoSummary = this.summary.getInfo();
        this.infoSummaryText = new StringWidget(infoSummary, this.minecraft.font).setColor(0x808080);
        this.infoSummaryText.setMaxWidth(maxWidth);
        if (this.minecraft.font.width(infoSummary) > maxWidth) {
            this.infoSummaryText.setTooltipData(new TooltipDisplayData(infoSummary, TOOLTIP_MAX_WIDTH, mouseX, mouseY));
        }
    }
}
