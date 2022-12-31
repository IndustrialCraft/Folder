package com.github.industrialcraft.folder;

public class MathUtils {
    public static float lerp(float a, float b, float value){
        if(value < 0 || value > 1)
            throw new IllegalArgumentException("lerp value must be in range 0..1");
        return (a*(1-value)) + (b*value);
    }
    public static float distanceSquared(int x, int y){
        return (x*x)+(y*y);
    }
}
