package com.github.industrialcraft.folder;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CustomScreenViewport extends ScreenViewport {
    public void update (int screenWidth, int screenHeight, boolean centerCamera) {
        setScreenBounds(getScreenX(), getScreenY(), screenWidth, screenHeight);
        setWorldSize(screenWidth * getUnitsPerPixel(), screenHeight * getUnitsPerPixel());
        apply(centerCamera);
    }
}
