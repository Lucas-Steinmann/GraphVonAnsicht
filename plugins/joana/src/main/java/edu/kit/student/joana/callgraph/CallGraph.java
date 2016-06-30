package edu.kit.student.joana.callgraph;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraph;
import edu.kit.student.joana.JoanaVertex;

import java.util.LinkedList;
import java.util.List;

/**
 * This is a specified graph representation for the Callgraph in Joana.
 */
public class CallGraph extends JoanaGraph {

    public CallGraph(String name, Integer id) {
        super(name, id);
    }

    @Override
    public List<LayeredGraph<JoanaVertex, JoanaEdge>> getSubgraphs() {
        return new LinkedList<>();
    }
}
