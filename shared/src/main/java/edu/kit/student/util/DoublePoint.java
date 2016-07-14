package edu.kit.student.util;

/**
 * This class is a standard immutable 2D Vector with double values as it's components.
 */
public class DoublePoint {

	public final double x;
    public final double y;
    
    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DoublePoint) {
        	return areEqual(x, ((DoublePoint)obj).x, Math.pow(10, -6)) && areEqual(y, ((DoublePoint)obj).y, Math.pow(10, -6));
        }
        return false;
    }
    
    private boolean areEqual(double a, double b, double diff){
    	return Math.abs(a-b) < diff;
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
