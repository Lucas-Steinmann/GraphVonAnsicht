package edu.kit.student.joana;

import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.joana.JoanaEdge.Kind;

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
    
    public JoanaEdgeBuilder() { }
    
    @Override
    public void setID(String id) {
        this.name = id;
    }


    @Override
    public void newEdge(String source, String target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void addData(String keyname, String value) {
        if (keyname == "edgeKind") {
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
        // Lookup source and target.
        
        return new JoanaEdge<>(name, name, edgeKind);

    }
}
