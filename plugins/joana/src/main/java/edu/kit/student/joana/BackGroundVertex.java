package edu.kit.student.joana;

import edu.kit.student.util.DoublePoint;

/**
 * Plain visible vertex which is shown in background of an expanded field access.
 */
public class BackGroundVertex extends JoanaVertex {
    
    FieldAccess fieldAccess;

    public BackGroundVertex(FieldAccess fa) {
        super("", "", VertexKind.FIELDACCESS);
        fieldAccess = fa;
    }

    @Override
    public DoublePoint getSize() {
        return fieldAccess.getSize();
    }

    @Override
    public int getX() {
        return fieldAccess.getX();
    }

    @Override
    public int getY() {
        return fieldAccess.getY();
    }

}
