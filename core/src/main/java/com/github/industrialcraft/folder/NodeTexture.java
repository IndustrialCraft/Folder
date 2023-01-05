package com.github.industrialcraft.folder;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NodeTexture {
    private TextureRegion texture;
    private String texturePath;
    public float xOrigin;
    public float yOrigin;
    public NodeTexture() {
        this.texture = null;
        this.texturePath = null;
        this.xOrigin = 0;
        this.yOrigin = 0;
    }
    public void setTexture(FileHandle handle){
        if(this.texture != null)
            this.texture.getTexture().dispose();
        this.texture = new TextureRegion(new Texture(handle));
        this.texturePath = handle.toString();
    }
    public String getTexturePath() {
        return texturePath;
    }
    public void draw(SpriteBatch batch, Transform transform){
        if(texture == null)
            return;
        batch.setColor(1, 1, 1, transform.opacity);
        batch.draw(texture, transform.x-xOrigin, transform.y-yOrigin, xOrigin, yOrigin, texture.getRegionWidth(), texture.getRegionHeight(), transform.size, transform.size, (float)Math.toDegrees(transform.rotation));
    }
}
