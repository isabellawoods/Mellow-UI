package melonystudios.mellowui.screen.template;

public interface PositionHelper {
    default int footerButtonHeight(int height) {
        return height - 27;
    }
}
