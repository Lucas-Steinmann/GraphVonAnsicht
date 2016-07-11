package edu.kit.student.joana;

import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.joana.JoanaEdge.Kind;

import java.util.Optional;
import java.util.Set;

/**
 * The JoanaEdgeBuilder is a {@link IEdgeBuilder}, specifically for building
 * {@link JoanaEdge}.
 */
public class JoanaEdgeBuilder implements IEdgeBuilder {

    boolean edgeForCallGraph;
    String source;
    String target;
    Kind edgeKind;
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
            this.edgeKind = Kind.valueOf(value);
        }
    }

    /**
     * Builds a new JoanaEdge as described before this call.
     * @return the built JoanaEdge
     */
    public JoanaEdge build(Set<JoanaVertex> vertexPool) {
        if (source == null || target == null) {
            return null;
        }

        Optional<JoanaVertex> sourceVertex = vertexPool.stream().filter(joanaVertex -> joanaVertex.getName().equals(source)).findFirst();
        Optional<JoanaVertex> targetVertex = vertexPool.stream().filter(joanaVertex -> joanaVertex.getName().equals(target)).findFirst();

        if (sourceVertex.isPresent() && targetVertex.isPresent()) {
            return new JoanaEdge(sourceVertex.get(), targetVertex.get(), edgeKind);
        }

        return null;
    }
}
