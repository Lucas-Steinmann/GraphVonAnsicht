package edu.kit.student.util;

/**
 * This class is a standard mutable 2D Vector with integer values as it's components.
 * Can be used for changing the values of it without instantiating a new IntegerPoint
 */
public class MutableIntegerPoint {

    public int x;
    public int y;

    public MutableIntegerPoint(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setXandY(int x, int y){
        this.x = x;
        this.y = y;
    }

    public IntegerPoint toImmutable(){
        return new IntegerPoint(this.x,this.y);
    }

    public static MutableIntegerPoint zero() {
        return new MutableIntegerPoint(0, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj instanceof MutableIntegerPoint) {
            if (x == ((MutableIntegerPoint) obj).x && y == ((MutableIntegerPoint) obj).y) {
                return true;
            }
        }
        return false;
    }
}
