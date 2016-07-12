package edu.kit.student.graphmodel.directed;

import java.util.Map;

import edu.kit.student.graphmodel.CollapsedVertex;
import edu.kit.student.graphmodel.Vertex;

public class DirectedCollapsedVertex extends CollapsedVertex {

    DirectedGraph graph;
    public DirectedCollapsedVertex(String name, String label, DirectedGraph graph,
            Map<Vertex, DirectedEdge> collapsedVertexToCutEdge) {
        super(name, label, graph, collapsedVertexToCutEdge);
        this.graph = graph;
    }

    @Override
    public DirectedGraph getGraph() {
        return this.graph;
    }
}
