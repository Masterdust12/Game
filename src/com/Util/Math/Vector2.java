package com.Util.Math;

public class Vector2 {

    public float x = 0;
    public float y = 0;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 clone() {
        return new Vector2(x, y);
    }

    public static Vector2 zero() {
        return new Vector2(0, 0);
    }

    public String toString() {
        return (int) x + ", " + (int) y;
    }

    public String statistics() {
        return x + ", " + y;
    }

    public Vector2 add(Vector2 other) {
        this.x += other.x;
        this.y += other.y;

        return new Vector2(x, y);
    }

    public Vector2 subtract(Vector2 other) {
        this.x -= other.x;
        this.y -= other.y;

        return new Vector2(x, y);
    }

    public Vector2 scale(float scaleFactor) {
        this.x *= scaleFactor;
        this.y *= scaleFactor;

        return new Vector2(x, y);
    }

    public static Vector2 magnitudeDirection(Vector2 v1, Vector2 v2) {
        float dx = v2.x - v1.x;
        float dy = v2.y - v1.y;

        Vector2 result = new Vector2(dx, dy);

        float slope = 0;

        boolean xy = (Math.abs(dx) > Math.abs(dy));

        slope = xy ? 1 / dx : 1 / dy;

        result.scale(slope);

        float xx = result.x;
        float yy = result.y;

        result.x = Math.abs(xx) * Math.signum(dx);
        result.y = Math.abs(yy) * Math.signum(dy);

        return result;
    }

    public static Vector2 reverseMag(Vector2 v1, Vector2 v2) {
        float dx = v2.x - v1.x;
        float dy = v2.y - v1.y;

        Vector2 result = new Vector2(dx, dy);

        float slope = 0;

        boolean xy = (Math.abs(dx) < Math.abs(dy));

        slope = xy ? 1 / dx : 1 / dy;

        result.scale(slope);

        float xx = result.x;
        float yy = result.y;

        result.x = Math.abs(xx) * Math.signum(dx);
        result.y = Math.abs(yy) * Math.signum(dy);

        return result;
    }

    public static double distance(Vector2 v1, Vector2 v2) {
        double x = (v1.x - v2.x);
        double y = (v1.y - v2.y);

        x = Math.signum(x) * Math.pow(x, 2);
        y = Math.signum(y) * Math.pow(y, 2);

        double dist = x + y;

        dist = Math.signum(dist) * Math.sqrt(Math.abs(dist));

        return dist;
    }

    public static double absDistance(Vector2 v1, Vector2 v2) {
        return Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2));
    }
}
