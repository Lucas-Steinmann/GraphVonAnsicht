package edu.kit.student.joana;

import edu.kit.student.graphmodel.LayeredGraph;

import java.util.LinkedList;
import java.util.List;


/**
 * A {@link JoanaGraph} which specifies a {@link FieldAccess} in a {@link JoanaGraph}.
 */
public class FieldAccessGraph extends JoanaGraph<JoanaVertex, JoanaEdge<JoanaVertex>> {

    public FieldAccessGraph(String name) {
        super(name);
    }

    @Override
    public List<LayeredGraph<JoanaVertex, JoanaEdge<JoanaVertex>>> getSubgraphs() {
        return new LinkedList<>();
    }
}
