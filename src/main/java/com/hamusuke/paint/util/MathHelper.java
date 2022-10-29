package com.hamusuke.paint.util;

public class MathHelper {
    public static int clamp(int value, int min, int max) {
        return value < min ? min : Math.min(value, max);
    }
}
