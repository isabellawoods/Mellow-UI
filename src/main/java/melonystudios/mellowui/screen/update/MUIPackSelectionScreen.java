package melonystudios.mellowui.screen.update;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.list.MUIPackList;
import melonystudios.mellowui.screen.widget.ImageSetButton;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MUIPackSelectionScreen extends Screen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Map<String, ResourceLocation> packIcons = Maps.newHashMap();
    private final PackSelectionModel manager;
    private final Screen lastScreen;
    private final File packDirectory;
    private MUIPackList packList;
    private Button doneButton;

    public MUIPackSelectionScreen(Screen lastScreen, PackRepository repository, Consumer<PackRepository> outputList, File packDirectory, Component title) {
        super(title);
        this.lastScreen = lastScreen;
        this.packDirectory = packDirectory;
        this.manager = new PackSelectionModel(this::populateLists, this::getPackIcon, repository, outputList);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        this.packList = new MUIPackList(this.minecraft, this.width, this.height, 32, this.height - 32, 36);
        this.addWidget(this.packList);

        // Open Folder button
        this.addRenderableWidget(new ImageSetButton(this.width / 2 + 104, this.height - 25, 20, 20, GUITextures.OPEN_FOLDER_SET,
                button -> Util.getPlatform().openFile(this.packDirectory), (button, stack, mouseX, mouseY) ->
                this.components.renderTooltip(this, button, new TranslatableComponent("button.mellowui.open_pack_folder"), mouseX, mouseY),
                new TranslatableComponent("button.mellowui.open_pack_folder")));

        // Done button
        this.doneButton = this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.packList.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        drawCenteredString(stack, this.font, new TranslatableComponent("pack.dropInfo").withStyle(ChatFormatting.GRAY), this.width / 2, 20, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void populateLists() {
        this.updateList(this.packList, this.manager.getSelected());
        this.updateList(this.packList, this.manager.getUnselected());
        this.doneButton.active = !this.packList.children().isEmpty();
    }

    private void updateList(MUIPackList packList, Stream<PackSelectionModel.Entry> packs) {
        packList.children().clear();
        packs.filter(PackSelectionModel.Entry::notHidden).forEach(pack -> packList.children().add(new MUIPackList.PackEntry(this, this.minecraft, pack, packList)));
    }

    private ResourceLocation getPackIcon(Pack pack) {
        return this.packIcons.computeIfAbsent(pack.getId(), string -> this.loadPackIcon(this.minecraft.getTextureManager(), pack));
    }

    private ResourceLocation loadPackIcon(TextureManager manager, Pack pack) {
        try (PackResources resources = pack.open(); InputStream inputStream = resources.getRootResource("pack.png")) {
            String packID = pack.getId();
            ResourceLocation packLocation = new ResourceLocation("minecraft", "pack/" + Util.sanitizeName(packID, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(packID) + "/icon");
            NativeImage nativeImage = NativeImage.read(inputStream);
            manager.register(packLocation, new DynamicTexture(nativeImage));
            return packLocation;
        } catch (FileNotFoundException ignored) {
        } catch (Exception exception) {
            MellowUI.LOGGER.warn(new TranslatableComponent("error.mellowui.pack_icon", pack.getId()).getString(), exception);
        }

        return GUITextures.DEFAULT_PACK_ICON;
    }
}
