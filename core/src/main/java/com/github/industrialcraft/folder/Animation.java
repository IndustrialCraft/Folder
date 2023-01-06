package com.github.industrialcraft.folder;

import java.util.ArrayList;

public class Animation {
    public final ArrayList<TransformWithLength> transforms;
    public Animation() {
        this.transforms = new ArrayList<>();
    }
    public Transform getLerpedTransformFor(float time){
        if(time < 0)
            return transforms.get(0).transform;
        if(time >= transforms.stream().map(e -> e.length).reduce(0f, Float::sum)-transforms.get(transforms.size()-1).length)
            return transforms.get(transforms.size()-1).transform;
        float currentTime = 0;
        Transform lastTransform = null;
        float lastLength = 0;
        for(TransformWithLength part : transforms){
            if(lastTransform == null){
                lastTransform = part.transform;
                lastLength = part.length;
                continue;
            }
            if (currentTime + lastLength >= time) {
                float lerpTime = ((time) / lastLength);
                return lastTransform.lerp(part.transform, lerpTime);
            }
            time -= lastLength;
            currentTime += part.length;
            lastTransform = part.transform;
            lastLength = part.length;
        }
        if(lastTransform != null)
            return lastTransform;
        throw new IllegalStateException("something went wrong");
    }
    public static class TransformWithLength{
        public final Transform transform;
        public float length;
        public TransformWithLength(Transform transform, float length) {
            this.transform = transform;
            this.length = length;
        }
    }
}
