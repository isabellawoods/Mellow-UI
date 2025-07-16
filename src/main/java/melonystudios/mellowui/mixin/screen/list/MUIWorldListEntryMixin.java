package melonystudios.mellowui.mixin.screen.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Date;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class MUIWorldListEntryMixin extends ObjectSelectionList.Entry<WorldSelectionList.WorldListEntry> {
    @Shadow @Final LevelSummary summary;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private SelectWorldScreen screen;
    @Shadow @Final private ResourceLocation iconLocation;
    @Shadow @Final @Nullable private DynamicTexture icon;
    @Shadow(remap = false) protected abstract void renderExperimentalWarning(PoseStack stack, int mouseX, int mouseY, int top, int left);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        String worldName = this.summary.getLevelName();
        Component worldInfo = new TranslatableComponent("selectWorld.world_info", this.summary.getLevelId(), MellowUtils.WORLD_DATE_FORMAT.format(new Date(this.summary.getLastPlayed())));

        if (StringUtils.isEmpty(worldName)) worldName = I18n.get("selectWorld.world") + " " + (index + 1);

        Component infoComponent = this.summary.getInfo();
        this.minecraft.font.drawShadow(stack, worldName, (float) (left + 32 + 3), (float) (top + 1), 0xFFFFFF);
        this.minecraft.font.drawShadow(stack, worldInfo, (float) (left + 32 + 3), (float) (top + 9 + 3), 0x808080);
        this.minecraft.font.drawShadow(stack, infoComponent, (float) (left + 32 + 3), (float) (top + 9 + 9 + 3), 0x808080);
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
            int i = mouseX - left;
            boolean hovering = i < 32;
            int i1 = hovering ? 32 : 0;

            if (this.summary.isLocked()) {
                GuiComponent.blit(stack, left, top, 96, (float) i1, 32, 32, 256, 256);
                if (hovering) {
                    this.screen.setToolTip(this.minecraft.font.split(new TranslatableComponent("selectWorld.locked").withStyle(ChatFormatting.RED), 175));
                }
            } else if (this.summary.requiresManualConversion()) {
                GuiComponent.blit(stack, left, top, 96, (float) i1, 32, 32, 256, 256);
                if (hovering) {
                    this.screen.setToolTip(this.minecraft.font.split(new TranslatableComponent("selectWorld.conversion.tooltip").withStyle(ChatFormatting.RED), 175));
                }
            } else if (this.summary.markVersionInList()) {
                GuiComponent.blit(stack, left, top, 32, (float) i1, 32, 32, 256, 256);
                if (this.summary.askToOpenWorld()) {
                    GuiComponent.blit(stack, left, top, 96, (float) i1, 32, 32, 256, 256);
                    if (hovering) {
                        this.screen.setToolTip(ImmutableList.of(new TranslatableComponent("selectWorld.tooltip.fromNewerVersion1").withStyle(ChatFormatting.RED).getVisualOrderText(), new TranslatableComponent("selectWorld.tooltip.fromNewerVersion2").withStyle(ChatFormatting.RED).getVisualOrderText()));
                    }
                } else if (!SharedConstants.getCurrentVersion().isStable()) {
                    GuiComponent.blit(stack, left, top, 64, (float) i1, 32, 32, 256, 256);
                    if (hovering) {
                        this.screen.setToolTip(ImmutableList.of(new TranslatableComponent("selectWorld.tooltip.snapshot1").withStyle(ChatFormatting.GOLD).getVisualOrderText(), new TranslatableComponent("selectWorld.tooltip.snapshot2").withStyle(ChatFormatting.GOLD).getVisualOrderText()));
                    }
                }
            } else {
                GuiComponent.blit(stack, left, top, 0, (float) i1, 32, 32, 256, 256);
            }
        }
    }
}
