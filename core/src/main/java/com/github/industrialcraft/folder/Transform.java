package com.github.industrialcraft.folder;

import mikera.matrixx.Matrix22;
import mikera.vectorz.Vector2;

public class Transform {
    public float x;
    public float y;
    public float rotation;
    public float size;
    public float opacity;
    public Transform(float x, float y, float rotation, float size, float opacity) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.size = size;
        this.opacity = opacity;
    }
    public Transform copy(){
        return new Transform(x, y, rotation, size, opacity);
    }
    public Transform transform(Transform other){
        Vector2 position = new Vector2(other.x, other.y);
        Matrix22 rotation = Matrix22.createRotationMatrix(this.rotation);
        Vector2 target = new Vector2();
        rotation.transform(position, target);
        return new Transform((float) (this.x + (target.x*this.size)), (float) (this.y + (target.y*this.size)), /*todo:check if correct*/this.rotation+other.rotation, this.size*other.size, this.opacity*other.opacity);
    }
    public Transform lerp(Transform other, float value){
        if(value < 0)
            value = 0;
        if(value > 1)
            value = 1;

        return new Transform(MathUtils.lerp(this.x, other.x, value), MathUtils.lerp(this.y, other.y, value), MathUtils.lerp(this.rotation, other.rotation, value), MathUtils.lerp(this.size, other.size, value), MathUtils.lerp(this.opacity, other.opacity, value));
    }
}
