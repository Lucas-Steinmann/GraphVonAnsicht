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

    private String name;
    private Map<String, String> data = new HashMap<>();

    private Set<JoanaVertexBuilder> vertexBuilders = new HashSet<>();
    private Set<String> vertexIds = new HashSet<>();
    private HashMap<String, JoanaVertex> vertices = new HashMap<>();

    private Set<JoanaEdge> edges = new HashSet<>();
    private Set<JoanaEdgeBuilder> edgeBuilders = new HashSet<>();
    
    /**
     * Constructor for {@link MethodGraphBuilder} which is created by a {@link edu.kit.student.joana.callgraph.CallGraphBuilder}.
     * 
     * @param name of the {@link MethodGraph}
     */
    public MethodGraphBuilder(String name) {
        this.name = name;
    }
    
    @Override
    public IEdgeBuilder getEdgeBuilder(String sourceId, String targetId) {
        JoanaEdgeBuilder builder = new JoanaEdgeBuilder(sourceId, targetId);
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

    /**
     * Returns true if a vertex with the specified ID is present in this builder.
     *
     * @param vertexId the ID of the vertex
     * @return true if the vertex is present, false otherwise.
     */
    public boolean containsVertexWithId(String vertexId) {
        return vertexIds.contains(vertexId);
    }

    /**
     * Returns a mapping of vertex names to vertices, after the graph was build.
     *
     * @return the vertices.
     */
    public HashMap<String, JoanaVertex> getVertexPool() {
        return this.vertices;
    }

    public Set<String> getVertexIds() {
        return this.vertexIds;
    }

    /**
     * Builds the {@link MethodGraph}, which has been described before this method is called.
     * @return the built {@link MethodGraph}
     * @throws GraphBuilderException if the {@link MethodGraph} could not be build.
     */
    public MethodGraph build() throws GraphBuilderException {
        for (JoanaVertexBuilder builder : vertexBuilders) {	
            JoanaVertex v = builder.build();
            vertices.put(v.getName(), v);
        }
        
        for (JoanaEdgeBuilder builder : edgeBuilders) {
            edges.add(builder.build(vertices));
        }

        String name = "";
        for (JoanaVertex v : vertices.values()) {
            if (v.getNodeKind() == VertexKind.ENTR)
            {
                //TODO: maybe some editing to transform the raw classnames 
                // into something more readable
                name = v.getNodeBcName();
            }
        }
        if (name.equals("")) {
            throw new GraphBuilderException(GraphBuilderException.BuilderType.GRAPH, "Found MethodGraph without ENTR Vertex.");
        }

        return new MethodGraph(new HashSet<>(vertices.values()), edges, name);
    }
}
