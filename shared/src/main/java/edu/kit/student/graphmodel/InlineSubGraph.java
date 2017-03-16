package edu.kit.student.graphmodel;

import edu.kit.student.util.DoublePoint;
import javafx.scene.paint.Color;

public interface InlineSubGraph extends ViewableGraph {
    
    Color getBackgroundColor();

    DoublePoint getSize();
    
    double getX();

    double getY();

    void setX(double x);

    void setY(double y);
}
