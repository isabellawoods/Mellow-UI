package melonystudios.mellowui.screen.update;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;

public class WorldLoadingScreen extends Screen {
    private static final long NARRATION_DELAY_MS = 2000L;
    private final StoringChunkProgressListener listener;
    private long lastNarration = -1L;
    private static final Object2IntMap<ChunkStatus> STATUS_COLORS = Util.make(new Object2IntOpenHashMap<>(), map -> {
        map.defaultReturnValue(0);
        map.put(ChunkStatus.EMPTY, 0x545454);
        map.put(ChunkStatus.STRUCTURE_STARTS, 0x999999);
        map.put(ChunkStatus.STRUCTURE_REFERENCES, 0x5F6191);
        map.put(ChunkStatus.BIOMES, 0x80B252);
        map.put(ChunkStatus.NOISE, 0xD1D1D1);
        map.put(ChunkStatus.SURFACE, 0x726809);
        map.put(ChunkStatus.CARVERS, 0x303572);
        map.put(ChunkStatus.LIQUID_CARVERS, 0x303572);
        map.put(ChunkStatus.FEATURES, 0x21C600);
        map.put(ChunkStatus.LIGHT, 0xFFE0A0);
        map.put(ChunkStatus.SPAWN, 0xF26060);
        map.put(ChunkStatus.HEIGHTMAPS, 0xEEEEEE);
        map.put(ChunkStatus.FULL, 0xFFFFFF);
    });

    public WorldLoadingScreen(StoringChunkProgressListener listener) {
        super(NarratorChatListener.NO_TITLE);
        this.listener = listener;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void removed() {
        NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.loading.done").getString());
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        int progress = Mth.clamp(this.listener.getProgress(), 0, 100);
        long currentTime = Util.getMillis();
        if (currentTime - this.lastNarration > NARRATION_DELAY_MS) {
            this.lastNarration = currentTime;
            NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.loading", progress + "%").getString());
        }

        int halfWidth = this.width / 2;
        int halfHeight = this.height / 2;
        this.renderChunkMap(stack, halfWidth, halfHeight + 10, 2, 0);
        fill(stack, halfWidth - 100, halfHeight - 49, halfWidth + 100, halfHeight - 47, 0xFF000000);
        fill(stack, halfWidth - 100, halfHeight - 49, halfWidth  - 100 + (progress * 2), halfHeight - 47, 0xFF00FF00);
        RenderComponents.INSTANCE.drawTitle(new TranslatableComponent("multiplayer.downloadingTerrain").withStyle(TextComponents.titleStyle()), this.width, halfHeight - 61);
    }

    private void renderChunkMap(PoseStack stack, int x, int y, int i2, int i3) {
        int i = i2 + i3;
        int fullDiameter = this.listener.getFullDiameter();
        int k = fullDiameter * i - i3;
        int diameter = this.listener.getDiameter();
        int i1 = diameter * i - i3;
        int j1 = x - i1 / 2;
        int k1 = y - i1 / 2;
        int l1 = k / 2 + 1;

        if (i3 != 0) {
            fill(stack, x - l1, y - l1, x - l1 + 1, y + l1, 0xFF0011FF);
            fill(stack, x + l1 - 1, y - l1, x + l1, y + l1, 0xFF0011FF);
            fill(stack, x - l1, y - l1, x + l1, y - l1 + 1, 0xFF0011FF);
            fill(stack, x - l1, y + l1 - 1, x + l1, y + l1, 0xFF0011FF);
        }

        for (int j2 = 0; j2 < diameter; ++j2) {
            for (int k2 = 0; k2 < diameter; ++k2) {
                ChunkStatus status = this.listener.getStatus(j2, k2);
                int x1 = j1 + j2 * i;
                int y1 = k1 + k2 * i;
                fill(stack, x1, y1, x1 + i2, y1 + i2, STATUS_COLORS.getInt(status) | 255 << 24);
            }
        }
    }
}
