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
        if(time > transforms.stream().map(e -> e.length).reduce(0f, Float::sum))
            return transforms.get(transforms.size()-1).transform;
        float currentTime = 0;
        Transform lastTransform = null;
        for(TransformWithLength part : transforms){
            if(currentTime+part.length>=time){
                float lerpTime = 1-((currentTime+part.length-time)/part.length);
                return (lastTransform==null?part.transform:lastTransform).lerp(part.transform, lerpTime);
            }
            currentTime += part.length;
            lastTransform = part.transform;
        }
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
