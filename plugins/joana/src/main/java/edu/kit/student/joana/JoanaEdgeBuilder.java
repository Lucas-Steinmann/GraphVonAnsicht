package edu.kit.student.joana;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.joana.JoanaEdge.EdgeKind;

/**
 * The JoanaEdgeBuilder is a {@link IEdgeBuilder}, specifically for building
 * {@link JoanaEdge}.
 */
public class JoanaEdgeBuilder implements IEdgeBuilder {

    private String source;
    private String target;
    private EdgeKind edgeKind;

    public JoanaEdgeBuilder() {
        source = null;
        target = null;
    }
    
    @Override
    public void setID(String id) { } // Ignore content. Name will be derived from edgeKind.


    @Override
    public void newEdge(String source, String target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void addData(String keyname, String value) {
        if (keyname.equals("edgeKind")) {
            this.edgeKind = EdgeKind.valueOf(value);
        }
    }

    /**
     * Builds a new {@link JoanaEdge} as described before this call.
     * @param vertexPool to pool of vertices, which contains this edge's endpoints
     * 
     * @return the built {@link JoanaEdge} or null if the
     */
    public JoanaEdge build(Map<String, JoanaVertex> vertexPool) {
        if (source == null || target == null) {
            return null;
        }
        
        //throw error
        if (edgeKind == null) {
            throw new IllegalArgumentException("Joana edge " + source + "->" + target + " needs an edgeKind.");
        }


        JoanaVertex sourceVertex = vertexPool.get(source);
        JoanaVertex targetVertex = vertexPool.get(target);

        if (sourceVertex != null && targetVertex != null) {
            return new JoanaEdge(edgeKind.name(), edgeKind.name(), sourceVertex, targetVertex, edgeKind);
        }

        return null;
    }
}
