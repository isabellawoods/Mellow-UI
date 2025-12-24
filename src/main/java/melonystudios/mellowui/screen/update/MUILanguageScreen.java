package melonystudios.mellowui.screen.update;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.screen.backport.FontSettingsScreen;
import melonystudios.mellowui.screen.list.LanguageList;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.resource.VanillaResourceType;

public class MUILanguageScreen extends SettingsScreen {
    private static final ITextComponent WARNING_LABEL = new TranslationTextComponent("menu.mellowui.language.warning", new TranslationTextComponent("options.languageWarning")).withStyle(TextComponents.descriptionStyle());
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final LanguageManager manager;
    private LanguageList list;
    private Button doneButton;
    private Button fontSettingsButton;

    // Search
    private TextFieldWidget searchBox;
    public String search = "";

    public MUILanguageScreen(Screen lastScreen, GameSettings options, LanguageManager manager) {
        super(lastScreen, options, new TranslationTextComponent("menu.mellowui.language.title").withStyle(TextComponents.titleStyle()));
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
        this.searchBox = new TextFieldWidget(this.font, this.width / 2 - 100, 17, 200, 15, TextComponents.searchText());
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
        this.fontSettingsButton = this.addButton(new Button(this.width / 2 - 154, this.height - 28, 150, 20, new TranslationTextComponent("button.mellowui.font_settings"),
                button -> this.minecraft.setScreen(new FontSettingsScreen(this, this.options))));

        // Done button
        this.doneButton = this.addButton(new Button(this.width / 2 + 4, this.height - 28, 150, 20, DialogTexts.GUI_DONE, button -> {
            LanguageList.Entry entry = this.list.getSelected();
            if (entry != null && !entry.language().getCode().equals(this.languageManager().getSelected().getCode())) {
                this.languageManager().setSelected(entry.language());
                this.options.languageCode = entry.language().getCode();
                ForgeHooksClient.refreshResources(this.minecraft, VanillaResourceType.LANGUAGES);
                this.doneButton.setMessage(DialogTexts.GUI_DONE);
                this.fontSettingsButton.setMessage(new TranslationTextComponent("button.mellowui.font_settings"));
                this.options.save();
            }

            this.onClose();
        }));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.searchBox.render(stack, mouseX, mouseY, partialTicks);
        this.components.renderTextBoxSuggestion(this.searchBox, this.searchBox.getMessage());
        this.components.drawTitle(this.title, this.width, 5);
        this.components.drawCenteredString(WARNING_LABEL, true, this.width / 2, this.height - 45, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
