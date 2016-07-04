package edu.kit.student.sugiyama.steps;

import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.sugiyama.RelativeLayerConstraint;
import edu.kit.student.sugiyama.graph.ILayerAssignerGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaEdge;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaVertex;

import java.util.HashSet;
import java.util.Set;

/**
 * This class takes a directed graph and assigns every vertex in it a layer.
 */
public class LayerAssigner implements ILayerAssigner {
	private DefaultDirectedGraph<SugiyamaVertex, SugiyamaEdge> DDGraph = new DefaultDirectedGraph<SugiyamaVertex, SugiyamaEdge>("", 0);
	private Set<SugiyamaVertex> graphVertices;
	private Set<SugiyamaEdge> graphEdges;

	@Override
	public void addConstraints(Set<RelativeLayerConstraint> constraints) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMaxHeight(int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMaxWidth(int width) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void assignLayers(ILayerAssignerGraph graph) {
		initialize(graph);
		Set<SugiyamaVertex> DDVertices = DDGraph.getVertexSet();
		Set<SugiyamaEdge> DDEdges = DDGraph.getEdgeSet();
		int layer = 0;

		while (!DDVertices.isEmpty()) {
			Set<SugiyamaGraph.SugiyamaVertex> currentSources = getSources(graph, DDEdges, DDVertices);

			for (SugiyamaGraph.SugiyamaVertex vertex : currentSources) {
				graph.assignToLayer(vertex, layer);
				DDVertices.remove(vertex);
				DDEdges.removeAll(graph.outgoingEdgesOf(vertex));
			}

			layer++;
		}
	}

	private Set<SugiyamaGraph.SugiyamaVertex> getSources(
			ILayerAssignerGraph graph,
			Set<SugiyamaGraph.SugiyamaEdge> edges,
			Set<SugiyamaGraph.SugiyamaVertex> vertices
	) {
		Set<SugiyamaGraph.SugiyamaVertex> result = new HashSet<>();

		for (SugiyamaGraph.SugiyamaVertex vertex : vertices) {
			Set<SugiyamaGraph.SugiyamaEdge> incomingEdges = getCorrectedIncomingEdges(graph, edges, vertex);

			if (incomingEdges.size() == 0) {
				result.add(vertex);
			}
		}

		return result;
	}

	private Set<SugiyamaGraph.SugiyamaEdge> getCorrectedIncomingEdges(
			ILayerAssignerGraph graph,
			Set<SugiyamaGraph.SugiyamaEdge> edges,
			SugiyamaGraph.SugiyamaVertex vertex
	) {
		Set<SugiyamaGraph.SugiyamaEdge> incomingEdges = graph.incomingEdgesOf(vertex);
		Set<SugiyamaEdge> tempEdges = new HashSet<SugiyamaEdge>(); //necessary in order don't to get a
		tempEdges.addAll(incomingEdges);							//concurrentModificationException 
		

		for (SugiyamaGraph.SugiyamaEdge edge : tempEdges) {
			if (!edges.contains(edge)) {
				incomingEdges.remove(edge);
			}
		}

		return incomingEdges;
	}
	
	private void initialize(ILayerAssignerGraph graph){
		this.graphVertices = graph.getVertexSet();
		this.graphEdges = graph.getEdgeSet();
		
		for(SugiyamaVertex vertex : this.graphVertices){
			DDGraph.addVertex(vertex);
		}

		for(SugiyamaEdge edge: this.graphEdges){
			DDGraph.addEdge(edge);
		}
		
	}
	

}
