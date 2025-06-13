package melonystudios.mellowui.renderer;

public interface MainMenuMethods {
    default boolean keepsLogoThroughFade() {
        return false;
    }

    default void keepLogoThroughFade(boolean keep) {}
}
