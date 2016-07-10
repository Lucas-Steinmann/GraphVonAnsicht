package edu.kit.student.util;

/**
 * This class is a standard immutable 2D Vector with integer values as it's components.
 */
public class Point implements Cloneable
{
    public final int x;
    public final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            if (x == ((Point) obj).x && y == ((Point) obj).y) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Point: X: " + x + ", Y:" + y;
    }

    @Override
    public Point clone() {
        return new Point(x, y);
    }
    
    public static Point zero() {
        return new Point(0, 0);
    }
}

