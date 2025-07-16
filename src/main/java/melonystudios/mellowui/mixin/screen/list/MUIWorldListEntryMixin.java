package melonystudios.mellowui.mixin.screen.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.WorldSelectionList;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.WorldSummary;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Date;

@Mixin(WorldSelectionList.Entry.class)
public abstract class MUIWorldListEntryMixin extends ExtendedList.AbstractListEntry<WorldSelectionList.Entry> {
    @Shadow @Final private WorldSummary summary;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private WorldSelectionScreen screen;
    @Shadow @Final private ResourceLocation iconLocation;
    @Shadow @Final @Nullable private DynamicTexture icon;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        String worldName = this.summary.getLevelName();
        ITextComponent worldInfo = new TranslationTextComponent("selectWorld.world_info", this.summary.getLevelId(), MellowUtils.WORLD_DATE_FORMAT.format(new Date(this.summary.getLastPlayed())));

        if (StringUtils.isEmpty(worldName)) worldName = I18n.get("selectWorld.world") + " " + (index + 1);

        ITextComponent infoComponent = this.summary.getInfo();
        this.minecraft.font.drawShadow(stack, worldName, (float) (left + 32 + 3), (float) (top + 1), 0xFFFFFF);
        this.minecraft.font.drawShadow(stack, worldInfo, (float) (left + 32 + 3), (float) (top + 9 + 3), 0x808080);
        this.minecraft.font.drawShadow(stack, infoComponent, (float) (left + 32 + 3), (float) (top + 9 + 9 + 3), 0x808080);
        GL11.glColor4f(1, 1, 1, 1);
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
            GL11.glColor4f(1, 1, 1, 1);
            int i = mouseX - left;
            boolean flag = i < 32;
            int i1 = flag ? 32 : 0;

            if (this.summary.isLocked()) {
                AbstractGui.blit(stack, left, top, 96, (float) i1, 32, 32, 256, 256);
                if (flag) {
                    this.screen.setToolTip(this.minecraft.font.split(new TranslationTextComponent("selectWorld.locked").withStyle(TextFormatting.RED), 175));
                }
            } else if (this.summary.markVersionInList()) {
                AbstractGui.blit(stack, left, top, 32, (float) i1, 32, 32, 256, 256);
                if (this.summary.askToOpenWorld()) {
                    AbstractGui.blit(stack, left, top, 96, (float) i1, 32, 32, 256, 256);
                    if (flag) {
                        this.screen.setToolTip(ImmutableList.of(new TranslationTextComponent("selectWorld.tooltip.fromNewerVersion1").withStyle(TextFormatting.RED).getVisualOrderText(), new TranslationTextComponent("selectWorld.tooltip.fromNewerVersion2").withStyle(TextFormatting.RED).getVisualOrderText()));
                    }
                } else if (!SharedConstants.getCurrentVersion().isStable()) {
                    AbstractGui.blit(stack, left, top, 64, (float) i1, 32, 32, 256, 256);
                    if (flag) {
                        this.screen.setToolTip(ImmutableList.of(new TranslationTextComponent("selectWorld.tooltip.snapshot1").withStyle(TextFormatting.GOLD).getVisualOrderText(), new TranslationTextComponent("selectWorld.tooltip.snapshot2").withStyle(TextFormatting.GOLD).getVisualOrderText()));
                    }
                }
            } else {
                AbstractGui.blit(stack, left, top, 0, (float) i1, 32, 32, 256, 256);
            }
        }
    }
}
