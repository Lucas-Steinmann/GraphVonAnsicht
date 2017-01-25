package edu.kit.student.graphmodel;

import edu.kit.student.util.DoublePoint;
import javafx.scene.paint.Color;

public interface InlineSubGraph extends ViewableGraph {
    
    public abstract Color getBackgroundColor();

    public abstract DoublePoint getSize();
    
    public abstract Double getX();

    public abstract Double getY();
}
