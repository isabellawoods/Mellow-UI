package melonystudios.mellowui.mixin.screen.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.text.TooltipDisplayData;
import melonystudios.mellowui.element.widget.text.StringWidget;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.WorldSelectionList;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.*;
import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static melonystudios.mellowui.element.RenderComponents.TOOLTIP_MAX_WIDTH;

@Mixin(WorldSelectionList.Entry.class)
public abstract class MUIWorldListEntryMixin extends ExtendedList.AbstractListEntry<WorldSelectionList.Entry> {
    @Unique public final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow @Final private WorldSummary summary;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private WorldSelectionScreen screen;
    @Shadow @Final private ResourceLocation iconLocation;
    @Shadow @Final @Nullable private DynamicTexture icon;
    @Unique private StringWidget worldNameText;
    @Unique private StringWidget playSummaryText;
    @Unique private StringWidget infoSummaryText;

    @SuppressWarnings("deprecation")
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        this.createTextWidgets(width, mouseX, mouseY);

        RenderSystem.color4f(1, 1, 1, 1);
        this.minecraft.getTextureManager().bind(this.icon != null ? this.iconLocation : GUITextures.MISSING_WORLD_ICON);
        RenderSystem.enableBlend();
        AbstractGui.blit(stack, left, top, 0, 0, 32, 32, 32, 32);
        RenderSystem.disableBlend();

        if (this.minecraft.options.touchscreen || isMouseOver) {
            RenderSystem.enableBlend();
            this.minecraft.getTextureManager().bind(GUITextures.WORLD_SELECTION_OVERLAY);
            AbstractGui.blit(stack, left, top, 0, 0, 32, 32, 32, 32);
            RenderSystem.disableBlend();
            this.minecraft.getTextureManager().bind(GUITextures.WORLD_SELECTION_ICONS);
            RenderSystem.color4f(1, 1, 1, 1);
            int mousePos = mouseX - left;
            boolean iconHovered = mousePos < 32;
            int vOffset = iconHovered ? 32 : 0;

            if (iconHovered) this.components.requestCursor(CursorTypes.POINTING_HAND);

            if (this.summary.isLocked()) {
                AbstractGui.blit(stack, left, top, 96, (float) vOffset, 32, 32, 256, 256);
                if (iconHovered) {
                    this.screen.setToolTip(this.minecraft.font.split(new TranslationTextComponent("selectWorld.locked").withStyle(TextFormatting.RED), TOOLTIP_MAX_WIDTH));
                }
            } else if (this.summary.markVersionInList()) {
                AbstractGui.blit(stack, left, top, 32, (float) vOffset, 32, 32, 256, 256);
                if (this.summary.askToOpenWorld()) {
                    AbstractGui.blit(stack, left, top, 96, (float) vOffset, 32, 32, 256, 256);
                    if (iconHovered) {
                        this.screen.setToolTip(ImmutableList.of(new TranslationTextComponent("selectWorld.tooltip.fromNewerVersion1").withStyle(TextFormatting.RED).getVisualOrderText(), new TranslationTextComponent("selectWorld.tooltip.fromNewerVersion2").withStyle(TextFormatting.RED).getVisualOrderText()));
                    }
                } else if (!SharedConstants.getCurrentVersion().isStable()) {
                    AbstractGui.blit(stack, left, top, 64, (float) vOffset, 32, 32, 256, 256);
                    if (iconHovered) {
                        this.screen.setToolTip(ImmutableList.of(new TranslationTextComponent("selectWorld.tooltip.snapshot1").withStyle(TextFormatting.GOLD).getVisualOrderText(), new TranslationTextComponent("selectWorld.tooltip.snapshot2").withStyle(TextFormatting.GOLD).getVisualOrderText()));
                    }
                }
            } else {
                AbstractGui.blit(stack, left, top, 0, (float) vOffset, 32, 32, 256, 256);
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

        ITextComponent worldName = new StringTextComponent(this.summary.getLevelName());
        this.worldNameText = new StringWidget(worldName, this.minecraft.font).setColor(TextComponents.selectableColor(
                Objects.equals(this.list == null || this.list.getSelected() == null ? 0 :
                        this.list.getSelected().hashCode(), this.hashCode()), true)
        );
        this.worldNameText.setMaxWidth(maxWidth);
        if (this.minecraft.font.width(worldName) > maxWidth) {
            this.worldNameText.setTooltipData(new TooltipDisplayData(worldName, TOOLTIP_MAX_WIDTH, mouseX, mouseY));
        }

        SimpleDateFormat worldDateFormat = new SimpleDateFormat(); // "dd-MM-yyyy '('EEE') - 'HH:mm:ss"
        ITextComponent playSummary = new TranslationTextComponent("selectWorld.world_info", this.summary.getLevelId(), worldDateFormat.format(new Date(this.summary.getLastPlayed())));
        this.playSummaryText = new StringWidget(playSummary, this.minecraft.font).setColor(0x808080);
        this.playSummaryText.setMaxWidth(maxWidth);
        if (this.minecraft.font.width(playSummary) > maxWidth) {
            this.playSummaryText.setTooltipData(new TooltipDisplayData(playSummary, TOOLTIP_MAX_WIDTH, mouseX, mouseY));
        }

        ITextComponent infoSummary = this.summary.getInfo();
        this.infoSummaryText = new StringWidget(infoSummary, this.minecraft.font).setColor(0x808080);
        this.infoSummaryText.setMaxWidth(maxWidth);
        if (this.minecraft.font.width(infoSummary) > maxWidth) {
            this.infoSummaryText.setTooltipData(new TooltipDisplayData(infoSummary, TOOLTIP_MAX_WIDTH, mouseX, mouseY));
        }
    }
}
