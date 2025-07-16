package melonystudios.mellowui.screen.update;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.list.MUIPackList;
import melonystudios.mellowui.screen.widget.ImageSetButton;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.PackLoadingManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MUIPackSelectionScreen extends Screen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Map<String, ResourceLocation> packIcons = Maps.newHashMap();
    private final PackLoadingManager manager;
    private final Screen lastScreen;
    private final File packDirectory;
    private MUIPackList packList;
    private Button doneButton;

    public MUIPackSelectionScreen(Screen lastScreen, ResourcePackList repository, Consumer<ResourcePackList> outputList, File packDirectory, ITextComponent title) {
        super(title);
        this.lastScreen = lastScreen;
        this.packDirectory = packDirectory;
        this.manager = new PackLoadingManager(this::populateLists, this::getPackIcon, repository, outputList);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        this.packList = new MUIPackList(this.minecraft, this.width, this.height, 32, this.height - 32, 36);
        this.children.add(this.packList);

        // Open Folder button
        this.addButton(new ImageSetButton(this.width / 2 + 104, this.height - 25, 20, 20, GUITextures.OPEN_FOLDER_SET,
                button -> Util.getPlatform().openFile(this.packDirectory), (button, stack, mouseX, mouseY) ->
                this.components.renderTooltip(this, button, new TranslationTextComponent("button.mellowui.open_pack_folder"), mouseX, mouseY),
                new TranslationTextComponent("button.mellowui.open_pack_folder")));

        // Done button
        this.doneButton = this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.packList.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        drawCenteredString(stack, this.font, new TranslationTextComponent("pack.dropInfo").withStyle(TextFormatting.GRAY), this.width / 2, 20, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void populateLists() {
        this.updateList(this.packList, this.manager.getSelected());
        this.updateList(this.packList, this.manager.getUnselected());
        this.doneButton.active = !this.packList.children().isEmpty();
    }

    private void updateList(MUIPackList packList, Stream<PackLoadingManager.IPack> packs) {
        packList.children().clear();
        packs.filter(PackLoadingManager.IPack::notHidden).forEach(pack -> packList.children().add(new MUIPackList.PackEntry(this, this.minecraft, pack, packList)));
    }

    private ResourceLocation getPackIcon(ResourcePackInfo packInfo) {
        return this.packIcons.computeIfAbsent(packInfo.getId(), string -> this.loadPackIcon(this.minecraft.getTextureManager(), packInfo));
    }

    private ResourceLocation loadPackIcon(TextureManager manager, ResourcePackInfo pack) {
        try (IResourcePack resources = pack.open(); InputStream inputStream = resources.getRootResource("pack.png")) {
            String packID = pack.getId();
            ResourceLocation packLocation = new ResourceLocation("minecraft", "pack/" + Util.sanitizeName(packID, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(packID) + "/icon");
            NativeImage nativeImage = NativeImage.read(inputStream);
            manager.register(packLocation, new DynamicTexture(nativeImage));
            return packLocation;
        } catch (FileNotFoundException ignored) {
        } catch (Exception exception) {
            MellowUI.LOGGER.warn(new TranslationTextComponent("error.mellowui.pack_icon", pack.getId()).getString(), exception);
        }

        return GUITextures.DEFAULT_PACK_ICON;
    }
}
