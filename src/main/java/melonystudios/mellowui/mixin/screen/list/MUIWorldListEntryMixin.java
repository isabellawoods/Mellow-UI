package melonystudios.mellowui.mixin.screen.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.WorldSelectionList;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IReorderingProcessor;
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
import java.util.Date;
import java.util.Objects;

@Mixin(WorldSelectionList.Entry.class)
public abstract class MUIWorldListEntryMixin extends ExtendedList.AbstractListEntry<WorldSelectionList.Entry> {
    @Shadow @Final private WorldSummary summary;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private WorldSelectionScreen screen;
    @Shadow @Final private ResourceLocation iconLocation;
    @Shadow @Final @Nullable private DynamicTexture icon;

    @SuppressWarnings("deprecation")
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        int maxWidth = width - 32 - 3;

        this.minecraft.font.drawShadow(stack, this.worldName(this.minecraft.font, maxWidth, index), (float) (left + 32 + 3), (float) (top + 1), TextComponents.selectableColor(Objects.equals(this.list == null || this.list.getSelected() == null ? 0 : this.list.getSelected().hashCode(), this.hashCode()), true));
        this.minecraft.font.drawShadow(stack, this.playSummary(this.minecraft.font, maxWidth), (float) (left + 32 + 3), (float) (top + 9 + 3), 0x808080);
        this.minecraft.font.drawShadow(stack, this.infoSummary(this.minecraft.font, maxWidth), (float) (left + 32 + 3), (float) (top + 9 + 9 + 3), 0x808080);
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

            if (this.summary.isLocked()) {
                AbstractGui.blit(stack, left, top, 96, (float) vOffset, 32, 32, 256, 256);
                if (iconHovered) {
                    this.screen.setToolTip(this.minecraft.font.split(new TranslationTextComponent("selectWorld.locked").withStyle(TextFormatting.RED), RenderComponents.TOOLTIP_MAX_WIDTH));
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
    }

    @Unique
    private IReorderingProcessor clipText(FontRenderer font, ITextComponent message, int width) {
        ITextProperties clippedText = this.minecraft.font.substrByWidth(message, width - font.width(TextComponents.ELLIPSIS));
        return LanguageMap.getInstance().getVisualOrder(ITextProperties.composite(clippedText, TextComponents.ELLIPSIS));
    }

    @Unique
    private IReorderingProcessor worldName(FontRenderer font, int width, int index) {
        ITextComponent name = this.summary.getLevelName().isEmpty() ? new StringTextComponent(I18n.get("selectWorld.world") + " " + index) : new StringTextComponent(this.summary.getLevelName());
        return font.width(name) > width ? this.clipText(font, name, width) : name.getVisualOrderText();
    }

    @Unique
    private IReorderingProcessor playSummary(FontRenderer font, int width) {
        ITextComponent summary = new TranslationTextComponent("selectWorld.world_info", this.summary.getLevelId(), MellowUtils.WORLD_DATE_FORMAT.format(new Date(this.summary.getLastPlayed())));
        return font.width(summary) > width ? this.clipText(font, summary, width) : summary.getVisualOrderText();
    }

    @Unique
    private IReorderingProcessor infoSummary(FontRenderer font, int width) {
        ITextComponent summary = this.summary.getInfo();
        return font.width(summary) > width ? this.clipText(font, summary, width) : summary.getVisualOrderText();
    }
}
