package edu.kit.student.joana;

import java.util.Map;

import edu.kit.student.graphmodel.builder.GraphBuilderException;
import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.joana.JoanaEdge.EdgeKind;

/**
 * The JoanaEdgeBuilder is an {@link IEdgeBuilder}, to build {@link JoanaEdge}s.
 */
public class JoanaEdgeBuilder implements IEdgeBuilder {

    private final String sourceId;
    private final String targetId;
    private EdgeKind edgeKind;

    /**
     * Constructs a new {@link JoanaEdgeBuilder} and sets sourceId and targetId IDs.
     * @param sourceId the id of the sourceId vertex
     * @param targetId the id of the targetId vertex
     */
    public JoanaEdgeBuilder(String sourceId, String targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
    }
    
    @Override
    public void setID(String id) { } // Ignore content. Name will be derived from edgeKind.

    @Override
    public void addData(String key, String value) {
        if (key.equals("edgeKind")) {
            this.edgeKind = EdgeKind.valueOf(value);
        }
    }

    /**
     * Builds a new {@link JoanaEdge} as described before this call.
     * @param vertexPool to pool of vertices, which contains this edge's endpoints
     * 
     * @return the built {@link JoanaEdge} or null if the
     */
    public JoanaEdge build(Map<String, JoanaVertex> vertexPool) throws GraphBuilderException {
        if (sourceId == null || targetId == null) {
            throw new GraphBuilderException(GraphBuilderException.BuilderType.EDGE,
                    "Found JoanaEdge missing source or target vertex.");
        }
        
        //throw error
        if (edgeKind == null) {
            throw new GraphBuilderException(GraphBuilderException.BuilderType.EDGE,
                    "Joana edge " + sourceId + "->" + targetId + " needs an edgeKind.");
        }

        JoanaVertex sourceVertex = vertexPool.get(sourceId);
        JoanaVertex targetVertex = vertexPool.get(targetId);

        if (sourceVertex != null && targetVertex != null) {
            return new JoanaEdge(edgeKind.name(), edgeKind.name(), sourceVertex, targetVertex, edgeKind);
        } else {
            throw new GraphBuilderException(GraphBuilderException.BuilderType.EDGE,
                    "Found JoanaEdge with an invalid source or target vertex.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoanaEdgeBuilder that = (JoanaEdgeBuilder) o;

        if (sourceId != null ? !sourceId.equals(that.sourceId) : that.sourceId != null) return false;
        if (targetId != null ? !targetId.equals(that.targetId) : that.targetId != null) return false;
        return edgeKind == that.edgeKind;
    }

    @Override
    public int hashCode() {
        int result = sourceId != null ? sourceId.hashCode() : 0;
        result = 31 * result + (targetId != null ? targetId.hashCode() : 0);
        result = 31 * result + (edgeKind != null ? edgeKind.hashCode() : 0);
        return result;
    }
}
