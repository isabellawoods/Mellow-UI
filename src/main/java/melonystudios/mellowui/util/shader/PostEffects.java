package melonystudios.mellowui.util.shader;

import melonystudios.mellowui.MellowUI;

public class PostEffects {
    public static final PostEffect MUI_BLUR = new PostEffect(MellowUI.mellowUI("blur"), -1, "Radius");
    public static final PostEffect ANTIALIAS = new PostEffect("antialias", 0);
    public static final PostEffect ART = new PostEffect("art", 1);
    public static final PostEffect BITS = new PostEffect("bits", 2);
    public static final PostEffect BLOBS = new PostEffect("blobs", 3);
    public static final PostEffect BLOBS2 = new PostEffect("blobs2", 4);
    public static final PostEffect BLUR = new PostEffect("blur", 5, "Radius");
    public static final PostEffect BUMPY = new PostEffect("bumpy", 6);
    public static final PostEffect COLOR_CONVOLVE = new PostEffect("color_convolve", 7);
    public static final PostEffect CREEPER = new PostEffect("creeper", 8);
    public static final PostEffect DECONVERGE = new PostEffect("deconverge", 9);
    public static final PostEffect DESATURATE = new PostEffect("desaturate", 10);
    public static final PostEffect ENTITY_OUTLINE = new PostEffect("entity_outline", 11);
    public static final PostEffect FLIP = new PostEffect("flip", 12);
    public static final PostEffect FXAA = new PostEffect("fxaa", 13);
    public static final PostEffect GREEN = new PostEffect("green", 14);
    public static final PostEffect INVERT = new PostEffect("invert", 15);
    public static final PostEffect LOVE = new PostEffect(MellowUI.mellowUI("love"), 16);
    public static final PostEffect NOTCH = new PostEffect("notch", 17);
    public static final PostEffect NTSC = new PostEffect("ntsc", 18);
    public static final PostEffect OUTLINE = new PostEffect("outline", 19);
    public static final PostEffect PENCIL = new PostEffect("pencil", 20);
    public static final PostEffect PHOSPHOR = new PostEffect("phosphor", 21);
    public static final PostEffect SCAN_PINCUSHION = new PostEffect("scan_pincushion", 22);
    public static final PostEffect SOBEL = new PostEffect("sobel", 23);
    public static final PostEffect SPIDER = new PostEffect("spider", 24);
    public static final PostEffect WOBBLE = new PostEffect("wobble", 25);
}
