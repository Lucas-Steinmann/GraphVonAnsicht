package edu.kit.student.joana.methodgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.student.graphmodel.builder.GraphBuilderException;
import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaEdgeBuilder;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.JoanaVertex.VertexKind;
import edu.kit.student.joana.JoanaVertexBuilder;

/**
 * The MethodGraphBuilder is a {@link IGraphBuilder}, specifically for building
 * {@link MethodGraph}.
 */
public class MethodGraphBuilder implements IGraphBuilder {

    String name;
    Set<JoanaVertex> vertices = new HashSet<JoanaVertex>();
    Set<String> vertexIds = new HashSet<>();
    Map<String, String> data = new HashMap<>();
    Set<JoanaEdge> edges = new HashSet<>();
    Set<JoanaVertexBuilder> vertexBuilders = new HashSet<>();
    Set<JoanaEdgeBuilder> edgeBuilders = new HashSet<>();
    
    /**
     * Constructor for methodgraphBuilder which is created by a callgraphBuilder.
     * 
     * @param name of the methodgraph
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
        return null;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public void addData(String keyname, String value) throws IllegalArgumentException {
        this.data.put(keyname, value);
    }

    public boolean containsVertexWithId(String vertexId) {
        return vertexIds.contains(vertexId);
    }

    /**
     * Builds the {@link MethodGraph}, which has been described before this method is called.
     * @return the built {@link MethodGraph}
     * @throws GraphBuilderException if the {@link MethodGraph} could not be build.
     */
    public MethodGraph build() throws GraphBuilderException {
        for (JoanaVertexBuilder builder : vertexBuilders) {	
            vertices.add(builder.build());
        }
        
        for (JoanaEdgeBuilder builder : edgeBuilders) {
            JoanaEdge edge = builder.build(vertices);
            
            //check if edge exists
            if (edge != null) {
                edges.add(edge);
            } 
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
