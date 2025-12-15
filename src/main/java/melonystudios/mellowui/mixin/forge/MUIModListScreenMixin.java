package melonystudios.mellowui.mixin.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringUtil;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.client.gui.widget.ModListWidget;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.forgespi.language.IModInfo;
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
    @Shadow private EditBox search;
    @Shadow private ModListWidget.ModEntry selected;
    @Shadow private int listWidth;

    public MUIModListScreenMixin(Component title) {
        super(title);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/util/FormattedCharSequence;FFI)I"), remap = true)
    public int cancelSearchSuggestion(Font font, PoseStack stack, FormattedCharSequence text, float x, float y, int color) {
        return 0;
    }

    @Inject(method = "render", at = @At("TAIL"), remap = true)
    public void renderSearchSuggestion(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        RenderComponents.INSTANCE.renderTextBoxSuggestion(this.search, TextComponents.searchText());
        if (this.selected == null) {
            int modInfoWidth = this.width - this.listWidth - 12;
            RenderComponents.INSTANCE.drawCenteredString(new TranslatableComponent("menu.mellowui.mods.no_mod_selected").withStyle(TextComponents.descriptionStyle()), true, modInfoWidth / 2 + this.listWidth, this.height / 2 - 5, 0xFFFFFF);
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/gui/widget/ModListWidget;setSelected(Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;)V"), remap = true)
    public void selectEntryOnlyOnce(ModListWidget modList, AbstractSelectionList.Entry<?> entry) {
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
    public static abstract class MUIModEntryMixin extends ObjectSelectionList.Entry<ModListWidget.ModEntry> {
        @Shadow @Final private IModInfo modInfo;
        @Shadow @Final private ModListScreen parent;

        @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = true)
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks, CallbackInfo callback) {
            callback.cancel();
            Component name = new TextComponent(StringUtil.stripColor(this.modInfo.getDisplayName()));
            Component version = new TextComponent(StringUtil.stripColor(MavenVersionStringHelper.artifactVersionToString(this.modInfo.getVersion())));
            VersionChecker.CheckResult result = VersionChecker.getResult(this.modInfo);
            Font font = this.parent.getFontRenderer();
            RenderComponents components = RenderComponents.INSTANCE;

            // Text
            components.drawString(this.substring(name, font), true, left + 3, top + 2, TextComponents.selectableColor(
                    Objects.equals(this.list == null || this.list.getSelected() == null ? 0 : this.list.getSelected().hashCode(), this.hashCode()), true)
            );
            components.drawString(this.substring(version, font), true, left + 3, top + 3 + font.lineHeight, 0xCCCCCC);

            // Version checker icon
            if (result.status().shouldDraw()) {
                RenderComponents.INSTANCE.renderUpdateAvailableIcon(left + this.list.getRowWidth() - 16, top + height / 4 + 2, 1, result.status());
            }

            // Cursor
            if (this.isMouseOver(mouseX, mouseY)) RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);
        }

        @Unique
        @SuppressWarnings("deprecation")
        private FormattedCharSequence substring(Component name, Font font) {
            return Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(name, this.list.getRowWidth())));
        }
    }
}
