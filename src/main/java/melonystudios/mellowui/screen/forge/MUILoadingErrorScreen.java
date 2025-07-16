package melonystudios.mellowui.screen.forge;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.list.LoadingMessageList;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingWarning;
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
    public MutableComponent errorHeader;
    public MutableComponent warningHeader;

    public MUILoadingErrorScreen(List<ModLoadingException> errors, List<ModLoadingWarning> warnings, @Nullable Path dumpedLocation) {
        super(new TranslatableComponent("menu.mellowui.loading_errors.title"));
        this.loadErrors = errors == null ? Lists.newArrayList() : errors;
        this.loadWarnings = warnings;
        this.dumpedLocation = dumpedLocation;
    }

    @Override
    protected void init() {
        if (this.minecraft == null) return;

        this.errorHeader = new TranslatableComponent("menu.mellowui.loading_errors.error_header").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD);
        this.warningHeader = new TranslatableComponent("menu.mellowui.loading_errors.warning_header").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD);

        this.list = new LoadingMessageList(this, this.loadErrors, this.loadWarnings);
        this.addWidget(this.list);

        // Open mods folder
        this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 50, 150, 20, new TranslatableComponent("button.mellowui.open_mods_folder"),
                button -> Util.getPlatform().openFile(FMLPaths.MODSDIR.get().toFile())));

        // Open latest.log
        this.addRenderableWidget(new Button(this.width / 2 + 5, this.height - 50, 150, 20, new TranslatableComponent("button.mellowui.open_file", String.valueOf(this.logFilePath.getFileName())),
                button -> Util.getPlatform().openFile(this.logFilePath.toFile())));

        // Quit game
        this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 25, 150, 20, new TranslatableComponent("menu.quit"),
                button -> this.minecraft.stop()));

        if (this.loadErrors.isEmpty()) {
            // Proceed to title screen
            this.addRenderableWidget(new Button(this.width / 2 + 5, this.height - 25, 150, 20, new TranslatableComponent("button.mellowui.proceed_to_title_screen"),
                    button -> this.minecraft.setScreen(new TitleScreen(true))));
        } else if (this.dumpedLocation != null) {
            // Open [log file].txt
            this.addRenderableWidget(new Button(this.width / 2 + 5, this.height - 25, 150, 20, new TranslatableComponent("button.mellowui.open_file", String.valueOf(this.dumpedLocation.getFileName())),
                    button -> Util.getPlatform().openFile(this.dumpedLocation.toFile())));
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);

        if (this.loadErrors.isEmpty()) {
            drawCenteredString(stack, this.font, this.warningHeader, this.width / 2, 5, 0xFFFFFF);
            drawCenteredString(stack, this.font, new TranslatableComponent("menu.mellowui.loading_errors.warning_desc", this.loadWarnings.size()).withStyle(ChatFormatting.YELLOW), this.width / 2, 17, 0xFFFFFF);
        } else {
            drawCenteredString(stack, this.font, this.errorHeader, this.width / 2, 5, 0xFFFFFF);
            drawCenteredString(stack, this.font, new TranslatableComponent("menu.mellowui.loading_errors.error_desc", this.loadErrors.size()).withStyle(ChatFormatting.RED), this.width / 2, 17, 0xFFFFFF);
        }

        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
