package melonystudios.mellowui.mixin.screen.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
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

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class MUIWorldListEntryMixin extends ObjectSelectionList.Entry<WorldSelectionList.WorldListEntry> {
    @Shadow @Final LevelSummary summary;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private SelectWorldScreen screen;
    @Shadow @Final private ResourceLocation iconLocation;
    @Shadow @Final @Nullable private DynamicTexture icon;
    @Shadow(remap = false) protected abstract void renderExperimentalWarning(PoseStack stack, int mouseX, int mouseY, int top, int left);

    @SuppressWarnings("deprecation")
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        int maxWidth = width - 32 - 3;

        this.minecraft.font.drawShadow(stack, this.worldName(this.minecraft.font, maxWidth, index), (float) (left + 32 + 3), (float) (top + 1), TextComponents.selectableColor(Objects.equals(this.list == null || this.list.getSelected() == null ? 0 : this.list.getSelected().hashCode(), this.hashCode()), true));
        this.minecraft.font.drawShadow(stack, this.playSummary(this.minecraft.font, maxWidth), (float) (left + 32 + 3), (float) (top + 9 + 3), 0x808080);
        this.minecraft.font.drawShadow(stack, this.infoSummary(this.minecraft.font, maxWidth), (float) (left + 32 + 3), (float) (top + 9 + 9 + 3), 0x808080);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, this.icon != null ? this.iconLocation : GUITextures.MISSING_WORLD_ICON);
        RenderSystem.enableBlend();
        GuiComponent.blit(stack, left, top, 0, 0, 32, 32, 32, 32);
        RenderSystem.disableBlend();
        this.renderExperimentalWarning(stack, mouseX, mouseY, top, left);

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

            if (this.summary.isLocked()) {
                GuiComponent.blit(stack, left, top, 96, (float) vOffset, 32, 32, 256, 256);
                if (iconHovered) {
                    this.screen.setToolTip(this.minecraft.font.split(new TranslatableComponent("selectWorld.locked").withStyle(ChatFormatting.RED), RenderComponents.TOOLTIP_MAX_WIDTH));
                }
            } else if (this.summary.requiresManualConversion()) {
                GuiComponent.blit(stack, left, top, 96, (float) vOffset, 32, 32, 256, 256);
                if (iconHovered) {
                    this.screen.setToolTip(this.minecraft.font.split(new TranslatableComponent("selectWorld.conversion.tooltip").withStyle(ChatFormatting.RED), RenderComponents.TOOLTIP_MAX_WIDTH));
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
    }

    @Unique
    private FormattedCharSequence clipText(Font font, Component message, int width) {
        FormattedText clippedText = this.minecraft.font.substrByWidth(message, width - font.width(TextComponents.ELLIPSIS));
        return Language.getInstance().getVisualOrder(FormattedText.composite(clippedText, TextComponents.ELLIPSIS));
    }

    @Unique
    private FormattedCharSequence worldName(Font font, int width, int index) {
        Component name = this.summary.getLevelName().isEmpty() ? new TextComponent(I18n.get("selectWorld.world") + " " + index) : new TextComponent(this.summary.getLevelName());
        return font.width(name) > width ? this.clipText(font, name, width) : name.getVisualOrderText();
    }

    @Unique
    private FormattedCharSequence playSummary(Font font, int width) {
        Component summary = new TranslatableComponent("selectWorld.world_info", this.summary.getLevelId(), MellowUtils.WORLD_DATE_FORMAT.format(new Date(this.summary.getLastPlayed())));
        return font.width(summary) > width ? this.clipText(font, summary, width) : summary.getVisualOrderText();
    }

    @Unique
    private FormattedCharSequence infoSummary(Font font, int width) {
        Component summary = this.summary.getInfo();
        return font.width(summary) > width ? this.clipText(font, summary, width) : summary.getVisualOrderText();
    }
}
