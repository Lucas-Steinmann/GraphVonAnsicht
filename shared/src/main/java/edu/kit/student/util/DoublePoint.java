package edu.kit.student.util;

/**
 * This class is a standard immutable 2D Vector with double values as it's components.
 */
public class DoublePoint implements Cloneable {

	public final double x;
    public final double y;
    
    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DoublePoint other = (DoublePoint) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
            return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Point: X: " + x + ", Y:" + y;
    }

    @Override
    public DoublePoint clone() {
        return new DoublePoint(x, y);
    }
    
    public static DoublePoint zero() {
        return new DoublePoint(0, 0);
    }
}
