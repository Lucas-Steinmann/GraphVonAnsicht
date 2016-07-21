package edu.kit.student.joana.methodgraph;

import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaEdgeBuilder;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.JoanaVertex.VertexKind;
import edu.kit.student.joana.JoanaVertexBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * The MethodGraphBuilder is a {@link IGraphBuilder}, specifically for building
 * {@link MethodGraph}.
 */
public class MethodGraphBuilder implements IGraphBuilder {

    String name;
    Set<JoanaVertex> vertices = new HashSet<JoanaVertex>();
    Set<String> vertexIds = new HashSet<>();
    Set<JoanaEdge> edges = new HashSet<>();
    Set<JoanaVertexBuilder> vertexBuilders = new HashSet<>();
    Set<JoanaEdgeBuilder> edgeBuilders = new HashSet<>();
    
    /**
     * Constructor for methodgraphBuilder which is created by a callgraphBuilder.
     */
    public MethodGraphBuilder(String name) {
        this.name = name;
    }
    
    @Override
    public IEdgeBuilder getEdgeBuilder(String sourceId, String targetId) {
        JoanaEdgeBuilder builder = new JoanaEdgeBuilder();
        edgeBuilders.add(builder);
        return builder;
    }

    @Override
    public IVertexBuilder getVertexBuilder(String vertexId) {
        JoanaVertexBuilder builder = new JoanaVertexBuilder(vertexId);
        vertexIds.add(vertexId);
        vertexBuilders.add(builder);
        return builder;
    }

    @Override
    public IGraphBuilder getGraphBuilder(String graphId) {
        //is not allowed to happen
        //TODO: throw exception?
        return null;
    }

    public boolean containsVertexWithId(String vertexId) {
        return vertexIds.contains(vertexId);
    }

    /**
     * Builds the method graph, which has been described before this method is called.
     * @return the built methodgraph
     */
    public MethodGraph build() {
        for (JoanaVertexBuilder builder : vertexBuilders) {	
            vertices.add(builder.build());
        }
        
        for (JoanaEdgeBuilder builder : edgeBuilders) {
            edges.add(builder.build(vertices));
        }

        String name = "";
        for (JoanaVertex v : vertices) {
            if (v.getNodeKind() == VertexKind.ENTR)
            {
                //TODO: maybe some editing to transform the raw classnames 
                // into something more readable
                name = v.getNodeBcName();
            }
        }
        if (name.equals("")) {
            //TODO: throw exception
        }
        
        MethodGraph methodGraph = new MethodGraph(vertices, edges, name);
        
        return methodGraph;
    }
}
