package edu.kit.student.joana;

import java.util.Optional;
import java.util.Set;

import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.joana.JoanaEdge.EdgeKind;

/**
 * The JoanaEdgeBuilder is a {@link IEdgeBuilder}, specifically for building
 * {@link JoanaEdge}.
 */
public class JoanaEdgeBuilder implements IEdgeBuilder {

    boolean edgeForCallGraph;
    String source;
    String target;
    EdgeKind edgeKind;
    String name = "";
    
    public JoanaEdgeBuilder() {
        source = null;
        target = null;
    }
    
    @Override
    public void setID(String id) {
    	// not needed, an edge in joana is defined only by its source, targed and kind.
        this.name = id;
    }


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
     * Builds a new JoanaEdge as described before this call.
     * @param vertexPool to check if Edge is valid
     * 
     * @return the built JoanaEdge or null
     */
    public JoanaEdge build(Set<JoanaVertex> vertexPool) {
        if (source == null || target == null) {
            return null;
        }
        
        //throw error
        if (edgeKind == null) {
            throw new IllegalArgumentException("Joana edge " + source + "->" + target + " needs an edgeKind.");
        }

        Optional<JoanaVertex> sourceVertex = vertexPool.stream().filter(joanaVertex -> joanaVertex.getName().equals(source)).findFirst();
        Optional<JoanaVertex> targetVertex = vertexPool.stream().filter(joanaVertex -> joanaVertex.getName().equals(target)).findFirst();

        if (sourceVertex.isPresent() && targetVertex.isPresent()) {
            return new JoanaEdge(edgeKind.name(), edgeKind.name(), sourceVertex.get(), targetVertex.get(), edgeKind);
        }

        return null;
    }
}
