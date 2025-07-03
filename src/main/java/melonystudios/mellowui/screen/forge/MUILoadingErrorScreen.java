package melonystudios.mellowui.screen.forge;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.list.LoadingMessageList;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.client.ClientHooks;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MUILoadingErrorScreen extends Screen {
    private final Path logFilePath = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs","latest.log"));
    private final List<ModLoadingException> loadErrors;
    private final List<ModLoadingWarning> loadWarnings;
    @Nullable
    private final Path dumpedLocation;
    private LoadingMessageList list;
    public IFormattableTextComponent errorHeader;
    public IFormattableTextComponent warningHeader;

    public MUILoadingErrorScreen(List<ModLoadingException> errors, List<ModLoadingWarning> warnings, @Nullable Path dumpedLocation) {
        super(new TranslationTextComponent("menu.mellowui.loading_errors.title"));
        this.loadErrors = errors == null ? Lists.newArrayList() : errors;
        this.loadWarnings = warnings;
        this.dumpedLocation = dumpedLocation;
    }

    @Override
    protected void init() {
        if (this.minecraft == null) return;

        this.errorHeader = new TranslationTextComponent("menu.mellowui.loading_errors.error_header").withStyle(TextFormatting.RED).withStyle(TextFormatting.BOLD);
        this.warningHeader = new TranslationTextComponent("menu.mellowui.loading_errors.warning_header").withStyle(TextFormatting.YELLOW).withStyle(TextFormatting.BOLD);

        this.list = new LoadingMessageList(this, this.loadErrors, this.loadWarnings);
        this.children.add(this.list);

        // Open mods folder
        this.addButton(new Button(this.width / 2 - 155, this.height - 50, 150, 20, new TranslationTextComponent("button.mellowui.open_mods_folder"),
                button -> Util.getPlatform().openFile(FMLPaths.MODSDIR.get().toFile())));

        // Open latest.log
        this.addButton(new Button(this.width / 2 + 5, this.height - 50, 150, 20, new TranslationTextComponent("button.mellowui.open_file", this.logFilePath.getFileName()),
                button -> Util.getPlatform().openFile(this.logFilePath.toFile())));

        // Quit game
        this.addButton(new Button(this.width / 2 - 155, this.height - 25, 150, 20, new TranslationTextComponent("menu.quit"),
                button -> this.minecraft.stop()));

        if (this.loadErrors.isEmpty()) {
            // Proceed to title screen
            this.addButton(new Button(this.width / 2 + 5, this.height - 25, 150, 20, new TranslationTextComponent("button.mellowui.proceed_to_title_screen"), button -> {
                ClientHooks.logMissingTextureErrors();
                this.minecraft.setScreen(new MainMenuScreen(true));
            }));
        } else if (this.dumpedLocation != null) {
            // Open [log file].txt
            this.addButton(new Button(this.width / 2 + 5, this.height - 25, 150, 20, new TranslationTextComponent("button.mellowui.open_file", this.dumpedLocation.getFileName()),
                    button -> Util.getPlatform().openFile(this.dumpedLocation.toFile())));
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);

        if (this.loadErrors.isEmpty()) {
            drawCenteredString(stack, this.font, this.warningHeader, this.width / 2, 5, 0xFFFFFF);
            drawCenteredString(stack, this.font, new TranslationTextComponent("menu.mellowui.loading_errors.warning_desc", this.loadWarnings.size()).withStyle(TextFormatting.YELLOW), this.width / 2, 17, 0xFFFFFF);
        } else {
            drawCenteredString(stack, this.font, this.errorHeader, this.width / 2, 5, 0xFFFFFF);
            drawCenteredString(stack, this.font, new TranslationTextComponent("menu.mellowui.loading_errors.error_desc", this.loadErrors.size()).withStyle(TextFormatting.RED), this.width / 2, 17, 0xFFFFFF);
        }

        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
