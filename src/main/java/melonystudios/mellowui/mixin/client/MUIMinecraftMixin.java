package melonystudios.mellowui.mixin.client;

import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import melonystudios.mellowui.screen.update.MUILoadingTerrainScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.io.File;
import java.net.Proxy;
import java.util.concurrent.Executor;

import static melonystudios.mellowui.util.DimensionTransitionScreenManager.getScreenFromWorld;

@Mixin(value = Minecraft.class, priority = 900)
public abstract class MUIMinecraftMixin {
    @Shadow @Final private Proxy proxy;
    @Shadow @Nullable public ClientLevel level;
    @Shadow @Final public File gameDirectory;
    @Shadow @Final private boolean allowsMultiplayer;
    @Shadow public abstract boolean isLocalServer();
    @Shadow protected abstract void updateScreenAndTick(Screen screen);
    @Shadow protected abstract void updateLevelInEngines(@Nullable ClientLevel world);

    // Re-enables the multiplayer button in a development environment.
    @Inject(method = "allowsMultiplayer", at = @At("HEAD"), cancellable = true)
    public void allowsMultiplayer(CallbackInfoReturnable<Boolean> callback) {
        if (Minecraft.getInstance().getLaunchedVersion().contains("melony-studios-dev")) {
            callback.cancel();
            callback.setReturnValue(this.allowsMultiplayer);
        }
    }

    // todo: temporarily disable this because loading screen stays on for too long ~isa 15-7-25
    @Inject(method = "setLevel", at = @At("HEAD"), cancellable = true)
    public void setLevel(ClientLevel world, CallbackInfo callback) {
        callback.cancel();
        if (this.level != null) MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(this.level));
        this.updateScreenAndTick(getScreenFromWorld(world, this.level).create(() -> false, MUILoadingTerrainScreen.Reason.OTHER));
        this.level = world;
        this.updateLevelInEngines(world);

        if (!this.isLocalServer()) {
            AuthenticationService authService = new YggdrasilAuthenticationService(this.proxy);
            MinecraftSessionService sessionService = authService.createMinecraftSessionService();
            GameProfileRepository profileRepository = authService.createProfileRepository();
            GameProfileCache profileCache = new GameProfileCache(profileRepository, new File(this.gameDirectory, MinecraftServer.USERID_CACHE_FILE.getName()));
            SkullBlockEntity.setup(profileCache, sessionService, (Executor) this);
            GameProfileCache.setUsesAuthentication(false);
        }
    }
}
