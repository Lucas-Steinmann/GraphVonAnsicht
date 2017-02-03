package edu.kit.student.joana.callgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.student.graphmodel.builder.GraphBuilderException;
import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.joana.*;
import edu.kit.student.joana.JoanaEdge.EdgeKind;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.joana.methodgraph.MethodGraphBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The CallGraphBuilder implements an {@link IGraphBuilder} and builds 
 * one {@link CallGraph}.
 */
public class CallGraphBuilder implements IGraphBuilder {

    private final Logger logger = LoggerFactory.getLogger(CallGraphBuilder.class);

    private final Map<String, String> data = new HashMap<>();
    private final Map<String, MethodGraphBuilder> vertexIDToMGBuilder = new HashMap<>();
    private final Set<MethodGraphBuilder> methodGraphBuilders = new HashSet<>();
    // contains all MethodGraphBuilder whose vertices have not been added to this vertexIDToMGBuilder mapping.
    private final Set<MethodGraphBuilder> collectedMGBuilder = new HashSet<>();
    private final Set<MethodGraph> methodGraphs = new HashSet<>();
    private final Set<JoanaEdgeBuilder> callEdgeBuilders = new HashSet<>();
    private String name;

    public CallGraphBuilder(String name) {
        this.name = name;
    }

    @Override
    public IEdgeBuilder getEdgeBuilder(String sourceId, String targetId) {
        for (MethodGraphBuilder builder : collectedMGBuilder) {
            for (String vId : builder.getVertexIds()) {
                vertexIDToMGBuilder.put(vId, builder);
            }
        }
        collectedMGBuilder.clear();

        MethodGraphBuilder sourceMG = vertexIDToMGBuilder.get(sourceId);
        MethodGraphBuilder targetMG = vertexIDToMGBuilder.get(targetId);
        if (sourceMG != null && sourceMG == targetMG) {
            return sourceMG.getEdgeBuilder(sourceId, targetId);
        }
        // Found edge between two method graphs
        JoanaEdgeBuilder eBuilder = new JoanaEdgeBuilder(sourceId, targetId);
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
        collectedMGBuilder.add(builder);
        return builder;
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
     * Builds a new {@link CallGraph} with the given information, added before this call.
     * @return the {@link CallGraph}
     * @throws GraphBuilderException if the {@link CallGraph} could not be build.
     */
    public CallGraph build() throws GraphBuilderException {
        HashMap<String, JoanaVertex> vertexPool = new HashMap<>();

        long startTime = System.currentTimeMillis();

        final HashMap<String, MethodGraph> vertexIDToMG = new HashMap<>();
        for (MethodGraphBuilder b : methodGraphBuilders) {
            MethodGraph mg = b.build();
            methodGraphs.add(mg);
            vertexPool.putAll(b.getVertexPool());
            for (String vId : b.getVertexPool().keySet()) {
                vertexIDToMG.put(vId, mg);
            }
        }
        methodGraphBuilders.clear();
        vertexIDToMGBuilder.clear();

        long stopTime = System.currentTimeMillis();
        logger.info("Building MethodGraphs took " + (stopTime - startTime));
        startTime = stopTime;

        HashMap<Integer, CallGraphVertex> vertices = new HashMap<>();
        HashMap<CallGraphVertex, Set<CallGraphVertex>> connections = new HashMap<>();
        Set<JoanaEdge> edges = new HashSet<>();

        // Generate Callgraph
        // Generate CallGraphVertices for every method.
        for (MethodGraph methodGraph : methodGraphs) {
            vertices.put(methodGraph.getID(), new CallGraphVertex(methodGraph.getName(), methodGraph.getName(), methodGraph));
            connections.put(vertices.get(methodGraph.getID()), new HashSet<>());
        }
        stopTime = System.currentTimeMillis();
        logger.info("Building CallGraphVertices took " + (stopTime - startTime));
        startTime = stopTime;

        // Build the calledges in the method graphs.
        // This should be temporary. Better would be if they would really get built in the method graph builder.
        Set<JoanaEdge> callEdges = new HashSet<>();
        for (JoanaEdgeBuilder builder : callEdgeBuilders) {
            callEdges.add(builder.build(vertexPool));
        }

        stopTime = System.currentTimeMillis();
        logger.info("Building CallEdges in MethodGraphs took " + (stopTime - startTime));
        startTime = stopTime;

        //edges between two MethodGraphs. All interprocEdges can be found in callEdges, these are also edges not from the same MethodGraph
        //search in callEdges for interproceduralEdges, create a new InterproceduralVertex for both MethodGraphs the edge connects and add them to a set
        //later split this set of InterproceduralVertices and add every MethodGraph its own InterproceduralVertices from this set
        HashMap<Integer,Set<InterproceduralVertex>> mgIdToIVSet = new HashMap<>();
        //add a mapping of MethodGraph id to set of interproc vertices. (its done for all MethodGraphs)
        for(MethodGraph mg : vertexIDToMG.values()){
        	mgIdToIVSet.put(mg.getID(), new HashSet<>());
        }
        for(JoanaEdge callEdge : callEdges){
        	JoanaVertex source = callEdge.getSource();
        	JoanaVertex target = callEdge.getTarget();
        	MethodGraph sourceMG = vertexIDToMG.get(source.getName());
        	MethodGraph targetMG = vertexIDToMG.get(target.getName());
        	String sourceMGname = sourceMG.getName();
        	String targetMGname = targetMG.getName();
        	int sourceMGid = sourceMG.getID();
        	int targetMGid = targetMG.getID();
        	//adding two interproc vertices to MG of source, and target respectively
        	mgIdToIVSet.get(sourceMGid).add(new InterproceduralVertex(target,source,targetMGid,targetMGname,true,callEdge.getEdgeKind()));
        	mgIdToIVSet.get(targetMGid).add(new InterproceduralVertex(source,target,sourceMGid,sourceMGname,false,callEdge.getEdgeKind()));
        }
        stopTime = System.currentTimeMillis();
        logger.info("Building interprocedural edges took " + (stopTime - startTime));
        startTime = stopTime;
        //set the interprocedural vertices of a method graph
        for(MethodGraph mg : methodGraphs){
        	if(mgIdToIVSet.containsKey(mg.getID())){
        		mg.setInterprocVertices(mgIdToIVSet.get(mg.getID()));
        	}
        }
        stopTime = System.currentTimeMillis();
        logger.info("Setting interprocedural vertices took " + (stopTime - startTime));
        startTime = stopTime;

        //TODO: some checks if all mg's contain the right amount of IV's and the correct ones!
        
        //search for call loops
        //maybe there is a better/faster solution without searching through all edges
        for (MethodGraph methodGraph : methodGraphs) {
            for (JoanaEdge edge : methodGraph.getEdgeSet()) {
                if (edge.getEdgeKind() == JoanaEdge.EdgeKind.CL) {
                    callEdges.add(edge);
                }
            }
        }
        stopTime = System.currentTimeMillis();
        logger.info("Searching calloops vertices took " + (stopTime - startTime));
        startTime = stopTime;

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
        stopTime = System.currentTimeMillis();
        logger.info("Building joanaedges for calledges took " + (stopTime - startTime));

        return new CallGraph(this.name, new HashSet<>(vertices.values()), edges);

    }
}
