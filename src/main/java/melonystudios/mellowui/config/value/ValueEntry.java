package melonystudios.mellowui.config.value;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class ValueEntry<T> {
    private T value;
    private final ValueType<T> type;
    private final ResourceLocation id;
    private ITextComponent translation = null;
    private ITextComponent tooltip = null;

    public ValueEntry(T value, ValueType<T> type, ResourceLocation id) {
        this.value = value;
        this.type = type;
        this.id = id;
    }

    public T value() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

    public ValueType<T> type() {
        return this.type;
    }

    public ITextComponent translation() {
        if (this.translation == null) this.translation = new TranslationTextComponent(Util.makeDescriptionId("config", this.id));
        return this.translation;
    }

    @Nullable
    public ITextComponent tooltip() {
        String translation = Util.makeDescriptionId("config", this.id) + ".tooltip";
        if (I18n.exists(translation) && this.tooltip == null) {
            this.tooltip = new TranslationTextComponent(translation);
        }
        return this.tooltip;
    }
}
