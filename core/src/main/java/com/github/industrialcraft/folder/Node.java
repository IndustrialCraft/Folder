package com.github.industrialcraft.folder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Node {
    private static final AtomicInteger NAME_GENERATOR = new AtomicInteger(0);

    public String name;
    public final Node parent;
    public final NodeTexture nodeTexture;
    public HashMap<String,Animation> animations;
    private final HashSet<Node> children;
    public final Rectangle lightBoundingBox;
    public Node(Node parent) {
        this.parent = parent;
        this.animations = new HashMap<>();
        this.children = new HashSet<>();
        this.nodeTexture = new NodeTexture();
        this.name = "node #" + NAME_GENERATOR.getAndIncrement();
        this.lightBoundingBox = new Rectangle(0, 0, 0, 0);
    }
    public Animation getOrCreateAnimation(String name){
        Animation animation = this.animations.get(name);
        if(animation == null){
            animation = new Animation();
            animation.transforms.add(new Animation.TransformWithLength(new Transform(0, 0, 0, 1, 1), 1));
            animations.put(name, animation);
        }
        return animation;
    }
    public Node() {
        this(null);
    }
    public Transform getTransformedTransform(String animation, float time){
        Transform thisLerped = getOrCreateAnimation(animation).getLerpedTransformFor(time);
        if(parent == null)
            return thisLerped;
        return parent.getTransformedTransform(animation, time).transform(thisLerped);
    }
    public Set<Node> getChildren(){
        return Collections.unmodifiableSet(children);
    }
    public void addChild(Node child){
        children.add(child);
    }
    public void removeChild(Node child){
        children.remove(child);
        child.dispose();
    }
    public void draw(Transform baseTransform, SpriteBatch batch, String animation, float time){
        this.nodeTexture.draw(batch, baseTransform==null?getTransformedTransform(animation, time):baseTransform.transform(getTransformedTransform(animation, time)));
    }
    public void drawLightningBoundingBox(Transform baseTransform, ShapeRenderer shapeRenderer, String animation, float time){
        Transform finalTransform = baseTransform==null?getTransformedTransform(animation, time):baseTransform.transform(getTransformedTransform(animation, time));
        shapeRenderer.setColor(Color.RED);
        float finalHalfWidth = lightBoundingBox.width*finalTransform.size;
        float finalHalfHeight = lightBoundingBox.height*finalTransform.size;
        shapeRenderer.rect(finalTransform.x-lightBoundingBox.x-finalHalfWidth, finalTransform.y-lightBoundingBox.y-finalHalfHeight, finalHalfWidth, finalHalfHeight, 2*finalHalfWidth, 2*finalHalfHeight, 1, 1, finalTransform.rotation);
    }
    public String getRealName(){
        return name + "(" + (parent==null?"root":parent.name) + ")";
    }
    public List<Node> getChildAndSelfRecursively(){
        List<Node> set = new ArrayList<>();
        set.add(this);
        children.forEach(node -> set.addAll(node.getChildAndSelfRecursively()));
        return set;
    }
    public void drawRecursively(Transform baseTransform, SpriteBatch batch, String animation, float time){
        draw(baseTransform, batch, animation, time);
        for(Node node : children){
            node.drawRecursively(baseTransform, batch, animation, time);
        }
    }
    public void drawLightBBRecursively(Transform baseTransform, ShapeRenderer shapeRenderer, String animation, float time){
        drawLightningBoundingBox(baseTransform, shapeRenderer, animation, time);
        for(Node node : children){
            node.drawLightBBRecursively(baseTransform, shapeRenderer, animation, time);
        }
    }
    public void dispose(){
        if(this.nodeTexture.getTexture() != null)
            this.nodeTexture.getTexture().dispose();
        for(Node child : children.stream().toList()){
            removeChild(child);
        }
    }
}
