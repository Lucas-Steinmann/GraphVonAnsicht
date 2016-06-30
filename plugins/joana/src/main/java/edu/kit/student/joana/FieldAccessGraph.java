package edu.kit.student.joana;

import edu.kit.student.graphmodel.LayeredGraph;

import java.util.LinkedList;
import java.util.List;


/**
 * A {@link JoanaGraph} which specifies a {@link FieldAccess} in a {@link JoanaGraph}.
 */
public class FieldAccessGraph extends JoanaGraph {

    public FieldAccessGraph(String name, Integer id) {
        super(name, id);
    }

    @Override
    public List<LayeredGraph<JoanaVertex, JoanaEdge>> getSubgraphs() {
        return new LinkedList<>();
    }
}
