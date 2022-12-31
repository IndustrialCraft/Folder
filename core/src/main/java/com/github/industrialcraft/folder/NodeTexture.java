package com.github.industrialcraft.folder;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NodeTexture {
    public TextureRegion texture;
    public float xOrigin;
    public float yOrigin;
    public NodeTexture(TextureRegion texture) {
        this.texture = texture;
        this.xOrigin = 0;
        this.yOrigin = 0;
    }
    public void draw(SpriteBatch batch, Transform transform){
        if(texture == null)
            return;
        batch.setColor(1, 1, 1, transform.opacity());
        batch.draw(texture, transform.x(), transform.y(), xOrigin, yOrigin, texture.getRegionWidth(), texture.getRegionHeight(), transform.size(), transform.size(), (float)Math.toDegrees(transform.rotation()));
    }
}
