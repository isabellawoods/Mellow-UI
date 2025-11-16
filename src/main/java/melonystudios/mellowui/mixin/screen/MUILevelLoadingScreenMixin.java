package melonystudios.mellowui.mixin.screen;

import melonystudios.mellowui.config.MellowConfigs;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelLoadingScreen.class)
public class MUILevelLoadingScreenMixin extends Screen {
    @Shadow
    @Final
    private StoringChunkProgressListener progressListener;

    public MUILevelLoadingScreenMixin(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        if (MellowConfigs.CLIENT_CONFIGS.worldLoadingStyle.get()) {
            this.minecraft.setScreen(new WorldLoadingScreen(this.progressListener));
        }
    }
}
