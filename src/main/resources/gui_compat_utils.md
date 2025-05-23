# GUI Compatibility Utils
## `PanoramaClientEvents` (from Panorama)
```java
private void setRandomPanorama(@Nullable MainMenuScreen screen) {
    DynamicTexture[] textures = Config.useCustomPanorama ? getRandomPanorama() : null;
    MainMenuScreen.CUBE_MAP = (RenderSkyboxCube) (textures != null ? new RenderDynamicSkyboxCube(textures) : new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama")));
    if (screen != null) screen.panorama = new RenderSkybox(MainMenuScreen.CUBE_MAP);
}
```