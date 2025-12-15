package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.screen.SuperSecretSettingsScreen;
import melonystudios.mellowui.util.shader.PostEffect;
import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class PostEffectsList extends ObjectSelectionList<PostEffectsList.Shader> {
    private final SuperSecretSettingsScreen parentScreen;
    private final Minecraft minecraft;
    private final boolean canSelectShaders = Panoramas.panorama().shader() == null;

    public PostEffectsList(Minecraft minecraft, SuperSecretSettingsScreen parentScreen) {
        super(minecraft, parentScreen.width, parentScreen.height, 32, parentScreen.height - 32, 16);
        this.minecraft = minecraft;
        this.parentScreen = parentScreen;
        ShaderManager.EFFECTS.stream().sorted(Comparator.comparingInt(PostEffect::shaderIdentifier)).forEach(effect -> this.addEntry(new Shader(this.parentScreen, effect)));
        this.centerScrollOn(this.children().stream().filter(shader -> shader.effect.shaderIdentifier() == ShaderManager.CURRENT_EFFECT.shaderIdentifier()).findFirst().orElse(this.children().get(0)));
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

        if (shader.effect().shaderIdentifier() == -1) {
            ShaderManager.clearPostEffect(this.minecraft);
        } else {
            ShaderManager.setPostEffect(this.minecraft, shader.effect(), true, true);
        }

        NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", shader.name).getString());
        this.parentScreen.updateButtonValidity();
    }

    public Optional<Shader> getMouseOver(int mouseX, int mouseY) {
        for (Shader shader : this.children()) {
            if (shader.isMouseOver(mouseX, mouseY)) return Optional.of(shader);
        }
        return Optional.empty();
    }

    @OnlyIn(Dist.CLIENT)
    public class Shader extends ObjectSelectionList.Entry<Shader> {
        private final SuperSecretSettingsScreen parentScreen;
        private final PostEffect effect;
        private final Component name;
        private int x;
        private int y;
        private int width;
        private int height;

        public Shader(SuperSecretSettingsScreen parentScreen, PostEffect effect) {
            this.parentScreen = parentScreen;
            this.effect = effect;
            this.name = new TranslatableComponent(Util.makeDescriptionId("post_effect", this.effect.assetID()));
        }

        public PostEffect effect() {
            return this.effect;
        }

        public Component name() {
            return this.name;
        }

        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            this.x = left;
            this.y = top;
            this.width = width;
            this.height = height;
            drawString(stack, this.parentScreen.getMinecraft().font, new TranslatableComponent("post_effect.dot", this.name())
                    .withStyle(TextComponents.selectableStyle(PostEffectsList.this.getSelected() == this, PostEffectsList.this.canSelectShaders)), left + 5, top + 2, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0 && PostEffectsList.this.canSelectShaders) {
                if (PostEffectsList.this.getSelected() != this && this.effect().shaderIdentifier() != -1) SuperSecretSettingsScreen.playRandomSound(this.parentScreen.getMinecraft());
                PostEffectsList.this.setSelected(this);
                PostEffectsList.this.setFocused(this);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + this.width) && mouseY < (double) (this.y + this.height);
        }

        @Override
        @NotNull
        public Component getNarration() {
            return new TranslatableComponent("narrator.select", this.name());
        }
    }
}
