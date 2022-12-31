package com.github.industrialcraft.folder;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.function.Function;

public class JSONSaverLoader {
    public static JsonObject toJson(Node rootNode){
        JsonObject json = new JsonObject();
        json.addProperty("name", rootNode.name);
        json.addProperty("originX", rootNode.nodeTexture.xOrigin);
        json.addProperty("originY", rootNode.nodeTexture.yOrigin);
        json.addProperty("texture", "");
        JsonObject animations = new JsonObject();
        for(var entry : rootNode.animations.entrySet()){
            JsonArray animation = new JsonArray();
            for(Animation.TransformWithLength transform : entry.getValue().transforms){
                JsonObject jsonTransform = new JsonObject();
                jsonTransform.addProperty("length", transform.length);
                jsonTransform.addProperty("x", transform.transform.x);
                jsonTransform.addProperty("y", transform.transform.y);
                jsonTransform.addProperty("rotation", transform.transform.rotation);
                jsonTransform.addProperty("size", transform.transform.size);
                jsonTransform.addProperty("opacity", transform.transform.opacity);
                animation.add(jsonTransform);
            }
            animations.add(entry.getKey(), animation);
        }
        json.add("animations", animations);
        return json;
    }
    public static Node fromJson(JsonObject jsonObject, Function<String, TextureRegion> textureResolver){
        throw new IllegalStateException("unimplemented");
    }
}
