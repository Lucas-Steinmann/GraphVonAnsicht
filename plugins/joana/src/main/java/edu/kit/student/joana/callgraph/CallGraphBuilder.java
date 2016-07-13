package edu.kit.student.joana.callgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.joana.JoanaCompoundVertex;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaEdgeBuilder;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.joana.methodgraph.MethodGraphBuilder;

/**
 * The CallGraphBuilder implements an {@link IGraphBuilder} and builds 
 * one {@link CallGraph}.
 */
public class CallGraphBuilder implements IGraphBuilder {

    Set<MethodGraphBuilder> methodGraphBuilders = new HashSet<>();
    Set<MethodGraph> methodGraphs = new HashSet<>();
    String name;
    
    public CallGraphBuilder(String name) {
        this.name = name;
    }
    
    @Override
    public IEdgeBuilder getEdgeBuilder(String sourceId, String targetId) {
        for (MethodGraphBuilder builder : methodGraphBuilders) {
            if (builder.containsVertexWithId(sourceId) && builder.containsVertexWithId(targetId)) {
                return builder.getEdgeBuilder(sourceId, targetId);
            }
        }

        return new JoanaEdgeBuilder();
    }

    @Override
    public IVertexBuilder getVertexBuilder(String vertexId) {
        //TODO: throw exception
        return null;
    }

    @Override
    public IGraphBuilder getGraphBuilder(String graphId) {
        MethodGraphBuilder builder = new MethodGraphBuilder(graphId);
        methodGraphBuilders.add(builder);
        return builder;
    }
    
    /**
     * Builds a new CallGraph with the given information, added before this call.
     * @return the callgraph
     */
    public CallGraph build() {
        for (MethodGraphBuilder b : methodGraphBuilders) {
            methodGraphs.add(b.build());
        }
        HashMap<Integer, JoanaCompoundVertex> vertices = new HashMap<>();
        HashMap<JoanaCompoundVertex, Set<JoanaEdge>> edges = new HashMap<>();

        // Generate Callgraph
        // Generate method vertices.
        for (MethodGraph methodGraph : methodGraphs) {
            vertices.put(methodGraph.getID(), new JoanaCompoundVertex(methodGraph.getName(), methodGraph.getName(), methodGraph));
        }
        // Add call edges between vertices.
        for (MethodGraph methodGraph : methodGraphs) {
            JoanaCompoundVertex source = vertices.get(methodGraph.getID());
            edges.put(source, new HashSet<JoanaEdge>());

            // Search for method calls.
            for (JoanaEdge e : methodGraph.getEdgeSet()) {
                if (e.getEdgeKind() == edu.kit.student.joana.JoanaEdge.Kind.CL) {
                    if (vertices.containsKey(e.getName())) {
                        JoanaCompoundVertex target = vertices.get(e.getName());
                        if (edges.get(source).contains(target)) {
                            // Second call from this function. Skip.
                            continue;
                        }
                        JoanaEdge edge = new JoanaEdge(e.getName(), e.getLabel(), source, target, e.getEdgeKind());
                        edges.get(source).add(edge);
                    } 
                }
            }
        }
        HashSet<JoanaEdge> merged = new HashSet<>();
        for (Set<JoanaEdge> edgeSet : edges.values()) {
            merged.addAll(edgeSet);
            
        }
        CallGraph graph = new CallGraph(this.name, 
                new HashSet<JoanaCompoundVertex>(vertices.values()), merged);
        
        for(MethodGraph methodGraph : methodGraphs) {
        	graph.addChildGraph(methodGraph);
        	methodGraph.setParentGraph(graph);
        }

        return graph;
    }

}
