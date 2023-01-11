package com.github.industrialcraft.folder;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NodeTexture {
    private TextureRegion texture;
    private Pixmap pixmap;
    private String texturePath;
    public float xOrigin;
    public float yOrigin;
    public NodeTexture() {
        this.texture = null;
        this.texturePath = null;
        this.xOrigin = 0;
        this.yOrigin = 0;
    }
    public Texture getTexture() {
        if(texture == null)
            return null;
        return texture.getTexture();
    }
    public Pixmap getPixmap() {
        return pixmap;
    }
    public void setTexture(FileHandle handle){
        if(this.texture != null) {
            this.texture.getTexture().dispose();
            this.pixmap.dispose();
        }
        this.texture = new TextureRegion(new Texture(handle));
        this.texturePath = handle.toString();
        if(!this.texture.getTexture().getTextureData().isPrepared())
            this.texture.getTexture().getTextureData().prepare();
        this.pixmap = this.getTexture().getTextureData().consumePixmap();
    }
    public void setTexture(Texture texture, String path){
        if(this.texture != null) {
            this.texture.getTexture().dispose();
            this.pixmap.dispose();
        }
        this.texture = new TextureRegion(texture);
        this.texturePath = path;
        if(!this.texture.getTexture().getTextureData().isPrepared())
            this.texture.getTexture().getTextureData().prepare();
        this.pixmap = this.getTexture().getTextureData().consumePixmap();
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
