package edu.kit.student.util;

/**
 * This class is a standard immutable 2D Vector with integer values as it's components.
 */
public class IntegerPoint implements Cloneable
{
    public final int x;
    public final int y;
    
    public IntegerPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj instanceof IntegerPoint) {
            if (x == ((IntegerPoint) obj).x && y == ((IntegerPoint) obj).y) {
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
    public IntegerPoint clone() {
        return new IntegerPoint(x, y);
    }
    
    public static IntegerPoint zero() {
        return new IntegerPoint(0, 0);
    }
}

