package melonystudios.mellowui.mixin.screen;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.update.WorldLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.chunk.listener.TrackingChunkStatusListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldLoadProgressScreen.class)
public class MUIWorldLoadingScreenMixin extends Screen {
    @Shadow
    @Final
    private TrackingChunkStatusListener progressListener;

    public MUIWorldLoadingScreenMixin(ITextComponent title) {
        super(title);
    }

    @Override
    protected void init() {
        if (MellowConfigs.CLIENT_CONFIGS.worldLoadingStyle.get()) {
            this.minecraft.setScreen(new WorldLoadingScreen(this.progressListener));
        }
    }
}
