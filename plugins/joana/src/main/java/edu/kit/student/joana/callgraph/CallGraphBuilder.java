package edu.kit.student.joana.callgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.joana.CallGraphVertex;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaEdge.EdgeKind;
import edu.kit.student.joana.JoanaEdgeBuilder;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.joana.methodgraph.MethodGraphBuilder;

/**
 * The CallGraphBuilder implements an {@link IGraphBuilder} and builds 
 * one {@link CallGraph}.
 */
public class CallGraphBuilder implements IGraphBuilder {

    Set<MethodGraphBuilder> methodGraphBuilders = new HashSet<>();
    Set<MethodGraph> methodGraphs = new HashSet<>();
    Set<JoanaEdgeBuilder> callEdgeBuilders = new HashSet<>();
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
        JoanaEdgeBuilder eBuilder = new JoanaEdgeBuilder();
        callEdgeBuilders.add(eBuilder);
        return eBuilder;
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
        HashMap<Integer, CallGraphVertex> vertices = new HashMap<>();
        HashMap<CallGraphVertex, Set<CallGraphVertex>> connections = new HashMap<>();
        Set<JoanaEdge> edges = new HashSet<>();
        Set<JoanaVertex> vertexPool = new HashSet<>();

        // Generate Callgraph
        // Generate method vertices.
        for (MethodGraph methodGraph : methodGraphs) {
            vertices.put(methodGraph.getID(), new CallGraphVertex(methodGraph.getName(), methodGraph.getName(), methodGraph));
            vertexPool.addAll(methodGraph.getVertexSet());
            connections.put(vertices.get(methodGraph.getID()), new HashSet<CallGraphVertex>());
        }

        // Build the calledges in the method graphs.
        // This should be temporary. Better would be if they would really get built in the method graph builder.
        Set<JoanaEdge> callEdges = new HashSet<>();
        for (JoanaEdgeBuilder builder : callEdgeBuilders) {
            JoanaEdge edge = builder.build(vertexPool);
            callEdges.add(edge);
        }
        
        for (JoanaEdge callEdge : callEdges) {
            if (callEdge.getEdgeKind() != EdgeKind.CL)
                continue;
            int sourceID = 0;
            int targetID = 0;
            // Find which methodgraph contains the target and the source vertex for the callEdge
            for (MethodGraph methodGraph : methodGraphs) {
                if (methodGraph.getVertexSet().contains(callEdge.getSource())) {
                    sourceID = methodGraph.getID();
                }
                if (methodGraph.getVertexSet().contains(callEdge.getTarget())) {
                    targetID = methodGraph.getID();
                }
            }
            if (connections.get(vertices.get(sourceID)).contains(vertices.get(targetID))) {
                // Second call from this function. Skip.
                continue;
            }
            edges.add(new JoanaEdge(callEdge.getName(), callEdge.getLabel(), vertices.get(sourceID), vertices.get(targetID), EdgeKind.CL));
            connections.get(vertices.get(sourceID)).add(vertices.get(targetID));
        }
       // // Add call edges between vertices.
       // for (MethodGraph methodGraph : methodGraphs) {
       //     JoanaCompoundVertex source = vertices.get(methodGraph.getID());
       //     edges.put(source, new HashSet<JoanaEdge>());

       //     // Search for method calls.
       //     for (JoanaEdge e : methodGraph.getEdgeSet()) {
       //         if (e.getEdgeKind() == edu.kit.student.joana.JoanaEdge.Kind.CL) {
       //             if (vertices.containsKey(e.getID())) {
       //                 JoanaCompoundVertex target = vertices.get(e.getID());
       //                 if (edges.get(source).contains(target)) {
       //                     // Second call from this function. Skip.
       //                     continue;
       //                 }
       //                 JoanaEdge edge = new JoanaEdge(e.getName(), e.getLabel(), source, target, e.getEdgeKind());
       //                 edges.get(source).add(edge);
       //             } 
       //         }
       //     }
       // }
       // HashSet<JoanaEdge> merged = new HashSet<>();
       // for (Set<JoanaEdge> edgeSet : connections.values()) {
       //     merged.addAll(edgeSet);
       //     
       // }
        CallGraph graph = new CallGraph(this.name, 
                new HashSet<CallGraphVertex>(vertices.values()), edges);
        
        for(MethodGraph methodGraph : methodGraphs) {
        	graph.addChildGraph(methodGraph);
        	methodGraph.setParentGraph(graph);
        }

        return graph;
    }
}
