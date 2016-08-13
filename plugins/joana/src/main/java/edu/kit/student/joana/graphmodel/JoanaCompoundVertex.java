package edu.kit.student.joana.graphmodel;

import edu.kit.student.graphmodel.CompoundVertex;
import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Graph;
import edu.kit.student.joana.JoanaVertex;

public abstract class JoanaCompoundVertex extends JoanaVertex implements CompoundVertex {

    public JoanaCompoundVertex(String name, String label, VertexKind kind) {
        super(name, label, kind);
    }

    @Override
    public abstract Graph getGraph();

    @Override
    public abstract JoanaVertex getConnectedVertex(Edge edge);

}
