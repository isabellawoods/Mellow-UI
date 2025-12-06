package melonystudios.mellowui.mixin.forge;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;
import net.minecraftforge.fml.client.gui.widget.ModListWidget;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = ModListScreen.class, remap = false)
public class MUIModListScreenMixin extends Screen {
    @Shadow private TextFieldWidget search;
    @Shadow private ModListWidget.ModEntry selected;
    @Shadow private int listWidth;

    public MUIModListScreenMixin(ITextComponent title) {
        super(title);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;draw(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/util/IReorderingProcessor;FFI)I"), remap = true)
    public int cancelSearchSuggestion(FontRenderer font, MatrixStack stack, IReorderingProcessor text, float x, float y, int color) {
        return 0;
    }

    @Inject(method = "render", at = @At("TAIL"), remap = true)
    public void renderSearchSuggestion(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        RenderComponents.INSTANCE.renderTextBoxSuggestion(this.search, TextComponents.searchText());
        if (this.selected == null) {
            int modInfoWidth = this.width - this.listWidth - 12;
            RenderComponents.INSTANCE.drawCenteredString(new TranslationTextComponent("menu.mellowui.mods.no_mod_selected").withStyle(TextComponents.descriptionStyle()), true, modInfoWidth / 2 + this.listWidth, this.height / 2 - 5, 0xFFFFFF);
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/gui/widget/ModListWidget;setSelected(Lnet/minecraft/client/gui/widget/list/AbstractList$AbstractListEntry;)V"), remap = true)
    public void selectEntryOnlyOnce(ModListWidget modList, AbstractList.AbstractListEntry<?> entry) {
        if (modList.getSelected() != entry) {
            modList.setSelected((ModListWidget.ModEntry) entry);
            modList.setFocused(entry);
            this.setFocused(entry);
        } else {
            modList.setFocused(null);
            this.setFocused(null);
        }
    }

    @Mixin(value = ModListWidget.ModEntry.class, remap = false)
    public static abstract class MUIModEntryMixin extends ExtendedList.AbstractListEntry<ModListWidget.ModEntry> {
        @Shadow @Final private ModInfo modInfo;
        @Shadow @Final private ModListScreen parent;

        @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = true)
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks, CallbackInfo callback) {
            callback.cancel();
            ITextComponent name = new StringTextComponent(StringUtils.stripColor(this.modInfo.getDisplayName()));
            ITextComponent version = new StringTextComponent(StringUtils.stripColor(MavenVersionStringHelper.artifactVersionToString(this.modInfo.getVersion())));
            VersionChecker.CheckResult result = VersionChecker.getResult(this.modInfo);
            FontRenderer font = this.parent.getFontRenderer();
            RenderComponents components = RenderComponents.INSTANCE;

            // Text
            components.drawString(this.substring(name, font), true, left + 3, top + 2, TextComponents.selectableColor(
                    Objects.equals(this.list == null || this.list.getSelected() == null ? 0 : this.list.getSelected().hashCode(), this.hashCode()), true)
            );
            components.drawString(this.substring(version, font), true, left + 3, top + 3 + font.lineHeight, 0xCCCCCC);

            // Version checker icon
            if (result.status.shouldDraw()) {
                RenderComponents.INSTANCE.renderUpdateAvailableIcon(left + this.list.getRowWidth() - 16, top + height / 4 + 2, 1, result.status);
            }

            // Cursor
            if (this.isMouseOver(mouseX, mouseY)) RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);
        }

        @Unique
        @SuppressWarnings("deprecation")
        private IReorderingProcessor substring(ITextComponent name, FontRenderer font) {
            return LanguageMap.getInstance().getVisualOrder(ITextProperties.composite(font.substrByWidth(name, this.list.getRowWidth())));
        }
    }
}
