package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.SuperSecretSettingsScreen;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.shader.PostEffect;
import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class PostEffectsList extends ExtendedList<PostEffectsList.Shader> {
    private final SuperSecretSettingsScreen parentScreen;
    private final Minecraft minecraft;

    public PostEffectsList(Minecraft minecraft, SuperSecretSettingsScreen parentScreen) {
        super(minecraft, parentScreen.width, parentScreen.height, 32, parentScreen.height - 32, 16);
        this.minecraft = minecraft;
        this.parentScreen = parentScreen;
        ShaderManager.EFFECTS.stream().sorted(Comparator.comparingInt(PostEffect::shaderIdentifier)).forEach(effect -> this.addEntry(new Shader(this.parentScreen, effect)));
    }

    @Override
    protected boolean isFocused() {
        return this.parentScreen.getFocused() == this;
    }

    @Override
    public void setSelected(@Nullable Shader shader) {
        super.setSelected(shader);
        if (shader == null) return;

        if (shader.effect().shaderIdentifier() == -1) {
            ShaderManager.clearPostEffect(this.minecraft);
        } else {
            ShaderManager.setPostEffect(this.minecraft, shader.effect());
        }

        NarratorChatListener.INSTANCE.sayNow(new TranslationTextComponent("narrator.select", shader.name).getString());
        this.parentScreen.updateButtonValidity();
    }

    public Optional<Shader> getMouseOver(int mouseX, int mouseY) {
        for (Shader shader : this.children()) {
            if (shader.isMouseOver(mouseX, mouseY)) return Optional.of(shader);
        }
        return Optional.empty();
    }

    @OnlyIn(Dist.CLIENT)
    public class Shader extends ExtendedList.AbstractListEntry<Shader> {
        private final SuperSecretSettingsScreen parentScreen;
        private final PostEffect effect;
        private final ITextComponent name;
        private int x;
        private int y;
        private int width;
        private int height;

        public Shader(SuperSecretSettingsScreen parentScreen, PostEffect effect) {
            this.parentScreen = parentScreen;
            this.effect = effect;
            this.name = new TranslationTextComponent(Util.makeDescriptionId("post_effect", this.effect.assetID()));
        }

        public PostEffect effect() {
            return this.effect;
        }

        public ITextComponent name() {
            return this.name;
        }

        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            this.x = left;
            this.y = top;
            this.width = width;
            this.height = height;
            int color = MellowUtils.getSelectableTextColor(PostEffectsList.this.getSelected() == this, true);
            drawString(stack, this.parentScreen.getMinecraft().font, new TranslationTextComponent("post_effect.dot", this.name()), left + 5, top + 2, color);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0) {
                if (PostEffectsList.this.getSelected() != this && this.effect().shaderIdentifier() != -1) SuperSecretSettingsScreen.playRandomSound(this.parentScreen.getMinecraft());
                PostEffectsList.this.setSelected(this);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + this.width) && mouseY < (double) (this.y + this.height);
        }
    }
}
