package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.text.TooltipDisplayData;
import melonystudios.mellowui.element.text.TooltipProvider;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.screen.SuperSecretSettingsScreen;
import melonystudios.mellowui.resource.posteffect.PostEffect;
import melonystudios.mellowui.util.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class PostEffectsList extends ExtendedList<PostEffectsList.Shader> implements TooltipProvider {
    private final SuperSecretSettingsScreen parentScreen;
    private final Minecraft minecraft;
    private TooltipDisplayData tooltipData;
    private final boolean canSelectShaders = Panoramas.panorama().shader() == null;

    public PostEffectsList(Minecraft minecraft, SuperSecretSettingsScreen parentScreen) {
        super(minecraft, parentScreen.width, parentScreen.height, 32, parentScreen.height - 32, 16);
        this.minecraft = minecraft;
        this.parentScreen = parentScreen;
        this.setRenderSelection(false);
        this.refreshList(parentScreen.search);
        this.centerSelection();
    }

    public void refreshList(String search) {
        this.clearEntries();
        if (!search.isEmpty()) this.setScrollAmount(0);
        Stream<PostEffect> stream = ShaderManager.EFFECTS.stream();

        if (!StringUtils.isBlank(search)) {
            stream = stream.filter(effect -> new TranslationTextComponent(
                    Util.makeDescriptionId("post_effect", effect.assetID()))
                    .getString().toLowerCase(Locale.ROOT)
                    .contains(search.toLowerCase(Locale.ROOT))
            );
        }
        stream.sorted(Comparator.comparing(shader -> new TranslationTextComponent(Util.makeDescriptionId("post_effect", shader.assetID())).getString()))
                .forEach(effect -> this.addEntry(new Shader(this.parentScreen, effect)));

        // always select the current effect
        this.children().stream()
                .filter(shader -> shader.effect().assetID().equals(ShaderManager.CURRENT_EFFECT.assetID()))
                .findFirst().ifPresent(this::setSelected);
    }

    public void centerSelection() {
        this.children().stream()
                .filter(shader -> shader.effect().assetID().equals(ShaderManager.CURRENT_EFFECT.assetID())).findFirst()
                .ifPresent(this::centerScrollOn);
    }

    @Override
    public TooltipDisplayData tooltipData() {
        return this.tooltipData;
    }

    @Override
    public void setTooltipData(TooltipDisplayData data) {
        this.tooltipData = data;
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener listener) {
        super.setFocused(listener);
        this.parentScreen.setFocused(listener);
    }

    @Override
    protected boolean isFocused() {
        return this.parentScreen.getFocused() == this;
    }

    @Override
    public void setSelected(@Nullable Shader shader) {
        super.setSelected(shader);
        if (shader == null || !this.canSelectShaders) return;

        if (shader.effect().isDefault()) {
            ShaderManager.clearPostEffect(this.minecraft);
        } else {
            ShaderManager.setPostEffect(this.minecraft, shader.effect(), true, true);
        }

        NarratorChatListener.INSTANCE.sayNow(new TranslationTextComponent("narrator.select", shader.name()).getString());
        this.parentScreen.updateButtonValidity();
    }

    @OnlyIn(Dist.CLIENT)
    public class Shader extends ExtendedList.AbstractListEntry<Shader> {
        protected final RenderComponents components = RenderComponents.INSTANCE;
        private final SuperSecretSettingsScreen parentScreen;
        private final PostEffect effect;
        private final ITextComponent name;

        public Shader(SuperSecretSettingsScreen parentScreen, PostEffect effect) {
            this.parentScreen = parentScreen;
            this.effect = effect;
            this.name = new TranslationTextComponent(Util.makeDescriptionId("post_effect", effect.assetID()));
        }

        public PostEffect effect() {
            return this.effect;
        }

        public ITextComponent name() {
            return this.name;
        }

        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            // Render selection
            int color = TextComponents.lockableColor(PostEffectsList.this.getSelected() == this, PostEffectsList.this.canSelectShaders);
            if (PostEffectsList.this.getSelected() == this) this.components.renderListSelection(left, top, width, height, color);

            // Text
            drawString(stack, this.parentScreen.getMinecraft().font, new TranslationTextComponent("post_effect.dot", this.name())
                    .withStyle(TextComponents.withColor(color)), left + 5, top + 2, 0xFFFFFF);

            if (this.isMouseOver(mouseX, mouseY) && this.components.containsPointInScissor(mouseX, mouseY)) {
                // Tooltip
                PostEffectsList.this.setTooltipData(new TooltipDisplayData(this.getEffectTooltip(color), RenderComponents.TOOLTIP_MAX_WIDTH, mouseX, mouseY));

                // Cursor
                if (PostEffectsList.this.canSelectShaders) this.components.requestCursor(CursorTypes.POINTING_HAND);
            }
        }

        private IFormattableTextComponent getEffectTooltip(int color) {
            IFormattableTextComponent component = this.name().copy().withStyle(TextComponents.withColor(color));

            // Description
            String description = ((TranslationTextComponent) this.name()).getKey() + ".tooltip";
            if (I18n.exists(description)) {
                component.append("\n").append(new TranslationTextComponent(description).withStyle(TextComponents.descriptionStyle()));
            }

            // Asset ID ("mellowui:blur")
            if (this.parentScreen.getMinecraft().options.advancedItemTooltips) {
                component.append("\n").append(new StringTextComponent(this.effect().assetID().toString()).withStyle(TextFormatting.DARK_GRAY));
            }

            // Warning when effects can't be selected
            if (!PostEffectsList.this.canSelectShaders) {
                component.append("\n").append(new TranslationTextComponent("post_effect.locked").withStyle(TextFormatting.RED));
            }
            return component;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0 && PostEffectsList.this.canSelectShaders) {
                if (PostEffectsList.this.getSelected() != this && !this.effect().isDefault()) SuperSecretSettingsScreen.playRandomSound(this.parentScreen.getMinecraft());
                PostEffectsList.this.setSelected(this);
                PostEffectsList.this.setFocused(this);
                return true;
            } else {
                return false;
            }
        }
    }
}
