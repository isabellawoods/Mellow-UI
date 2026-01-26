package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.text.TooltipDisplayData;
import melonystudios.mellowui.element.text.TooltipProvider;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.screen.SuperSecretSettingsScreen;
import melonystudios.mellowui.resource.posteffect.PostEffect;
import melonystudios.mellowui.util.ShaderManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class PostEffectsList extends ObjectSelectionList<PostEffectsList.Shader> implements TooltipProvider {
    private final SuperSecretSettingsScreen parentScreen;
    private final Minecraft minecraft;
    private TooltipDisplayData tooltipData;
    private final boolean canSelectShaders = Panoramas.panorama().shader() == null;

    public PostEffectsList(SuperSecretSettingsScreen parentScreen) {
        super(parentScreen.getMinecraft(), parentScreen.width, parentScreen.height, 36, parentScreen.height - 33, 18);
        this.minecraft = parentScreen.getMinecraft();
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
            stream = stream.filter(effect -> new TranslatableComponent(
                    Util.makeDescriptionId("post_effect", effect.assetID()))
                    .getString().toLowerCase(Locale.ROOT)
                    .contains(search.toLowerCase(Locale.ROOT))
            );
        }
        stream.sorted(Comparator.comparing(shader -> new TranslatableComponent(Util.makeDescriptionId("post_effect", shader.assetID())).getString()))
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
    public void setFocused(@Nullable GuiEventListener listener) {
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

        NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", shader.name()).getString());
        this.parentScreen.updateButtonValidity();
    }

    @OnlyIn(Dist.CLIENT)
    public class Shader extends ObjectSelectionList.Entry<Shader> {
        private final RenderComponents components = RenderComponents.INSTANCE;
        private final SuperSecretSettingsScreen parentScreen;
        private final PostEffect effect;
        private final Component name;

        public Shader(SuperSecretSettingsScreen parentScreen, PostEffect effect) {
            this.parentScreen = parentScreen;
            this.effect = effect;
            this.name = new TranslatableComponent(Util.makeDescriptionId("post_effect", effect.assetID()));
        }

        public PostEffect effect() {
            return this.effect;
        }

        public Component name() {
            return this.name;
        }

        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            // Render selection
            int color = TextComponents.lockableColor(PostEffectsList.this.getSelected() == this, PostEffectsList.this.canSelectShaders);
            if (PostEffectsList.this.getSelected() == this) this.components.renderListSelection(left, top, width, height, color);

            // Text
            drawString(stack, this.parentScreen.getMinecraft().font, new TranslatableComponent("post_effect.dot", this.name())
                    .withStyle(TextComponents.selectableStyle(PostEffectsList.this.getSelected() == this, PostEffectsList.this.canSelectShaders)), left + 5, top + 3, 0xFFFFFF);

            if (this.isMouseOver(mouseX, mouseY) && this.components.containsPointInScissor(mouseX, mouseY)) {
                // Tooltip
                PostEffectsList.this.setTooltipData(new TooltipDisplayData(this.getEffectTooltip(color), RenderComponents.TOOLTIP_MAX_WIDTH, mouseX, mouseY));

                // Cursor
                RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);
            }
        }

        private MutableComponent getEffectTooltip(int color) {
            MutableComponent component = this.name().copy().withStyle(TextComponents.withColor(color));

            // Description
            String description = ((TranslatableComponent) this.name()).getKey() + ".tooltip";
            if (I18n.exists(description)) {
                component.append("\n").append(new TranslatableComponent(description).withStyle(TextComponents.descriptionStyle()));
            }

            // Asset ID ("mellowui:blur")
            if (this.parentScreen.getMinecraft().options.advancedItemTooltips) {
                component.append("\n").append(new TextComponent(this.effect().assetID().toString()).withStyle(ChatFormatting.DARK_GRAY));
            }

            // Warning when effects can't be selected
            if (!PostEffectsList.this.canSelectShaders) {
                component.append("\n").append(new TranslatableComponent("post_effect.locked").withStyle(ChatFormatting.RED));
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

        @Override
        @NotNull
        public Component getNarration() {
            return new TranslatableComponent("narrator.select", this.name());
        }
    }
}
