package com.github.industrialcraft.folder;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Node {
    private static final AtomicInteger NAME_GENERATOR = new AtomicInteger(0);

    public String name;
    public final Node parent;
    public final NodeTexture nodeTexture;
    public HashMap<String,Animation> animations;
    private final HashSet<Node> children;
    public Node(Node parent) {
        this.parent = parent;
        this.animations = new HashMap<>();
        this.children = new HashSet<>();
        this.nodeTexture = new NodeTexture(null);
        this.name = "node #" + NAME_GENERATOR.getAndIncrement();
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
        Transform thisLerped = animations.get(animation).getLerpedTransformFor(time);
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
    }
    public void draw(SpriteBatch batch, String animation, float time){
        this.nodeTexture.draw(batch, getTransformedTransform(animation, time));
    }
    public HashSet<Node> getChildAndSelfRecursively(){
        HashSet<Node> set = new HashSet<>();
        set.add(this);
        children.forEach(node -> set.addAll(node.getChildAndSelfRecursively()));
        return set;
    }
    public void drawRecursively(SpriteBatch batch, String animation, float time){
        draw(batch, animation, time);
        for(Node node : children){
            node.drawRecursively(batch, animation, time);
        }
    }
}
