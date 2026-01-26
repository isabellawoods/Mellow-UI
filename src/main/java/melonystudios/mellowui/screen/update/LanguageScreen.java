package melonystudios.mellowui.screen.update;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.screen.backport.FontSettingsScreen;
import melonystudios.mellowui.screen.list.LanguageList;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class LanguageScreen extends OptionsSubScreen {
    private static final Component WARNING_LABEL = new TranslatableComponent("menu.mellowui.language.warning", new TranslatableComponent("options.languageWarning")).withStyle(TextComponents.descriptionStyle());
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final LanguageManager manager;
    private LanguageList list;

    // Search
    private EditBox searchBox;
    public String search = "";

    public LanguageScreen(Screen lastScreen, Options options, LanguageManager manager) {
        super(lastScreen, options, new TranslatableComponent("menu.mellowui.language.title").withStyle(TextComponents.titleStyle()));
        this.manager = manager;
    }

    public LanguageManager languageManager() {
        return this.manager;
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        // Language list
        this.list = new LanguageList(this, this.width, this.height, 36, this.height - 53, 18);
        this.addWidget(this.list);

        // Search box
        this.searchBox = new EditBox(this.font, this.width / 2 - 100, 17, 200, 15, TextComponents.searchText());
        this.searchBox.setFocus(false);
        this.searchBox.setCanLoseFocus(true);
        this.searchBox.setValue(this.search);
        this.searchBox.setResponder(value -> {
            this.search = value.trim();
            this.list.refreshList(value);
        });
        this.addWidget(this.searchBox);
        this.setInitialFocus(this.searchBox);

        // Font settings
        this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 28, 150, 20, new TranslatableComponent("button.mellowui.font_settings"),
                button -> this.minecraft.setScreen(new FontSettingsScreen(this, this.options))));

        // Done button
        this.addRenderableWidget(new Button(this.width / 2 + 4, this.height - 28, 150, 20, CommonComponents.GUI_DONE, button -> {
            LanguageList.Entry entry = this.list.getSelected();
            if (entry != null && !entry.language().getCode().equals(this.languageManager().getSelected().getCode())) {
                this.languageManager().setSelected(entry.language());
                this.options.languageCode = entry.language().getCode();
                this.minecraft.reloadResourcePacks();
                this.options.save();
            }

            this.onClose();
        }));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.searchBox.render(stack, mouseX, mouseY, partialTicks);
        this.components.renderTextBoxSuggestion(this.searchBox, this.searchBox.getMessage());
        this.components.drawTitle(this.title, this.width, 5);
        this.components.drawCenteredString(WARNING_LABEL, true, this.width / 2, this.height - 45, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
