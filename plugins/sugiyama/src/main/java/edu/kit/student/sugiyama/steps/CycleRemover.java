package edu.kit.student.sugiyama.steps;

import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.sugiyama.graph.ICycleRemoverGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaEdge;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaVertex;

import java.util.HashSet;
import java.util.Set;

/**
 * This class takes a directed Graph G = (V, E) and removes a set of edges E_ 
 * so that the resulting Graph G' = (V, E\E_) is a DAG(Directed Acyclic Graph).
 */
public class CycleRemover implements ICycleRemover {
	private DefaultDirectedGraph<SugiyamaVertex, SugiyamaEdge> DDGraph = new DefaultDirectedGraph<SugiyamaVertex, SugiyamaEdge>("", 0);
	private Set<SugiyamaVertex> graphVertices;
	private Set<SugiyamaEdge> graphEdges;
	
	/**
	 * Initializes the DDGraph and its vertices and edges. 
	 * Also initializes the vertex-set and edge-set that contain the vertices and edges of the original graph.
	 * 
	 * @param graph original graph to build a DefaultDirectedGraph from
	 */
	private void initialize(ICycleRemoverGraph graph){
		this.graphVertices = graph.getVertexSet();
		this.graphEdges = graph.getEdgeSet();
		
		for(SugiyamaVertex vertex : this.graphVertices){
			DDGraph.addVertex(vertex);
		}

		for(SugiyamaEdge edge: this.graphEdges){
			DDGraph.addEdge(edge);
		}
		
	}
	
	@Override
	public Set<SugiyamaEdge> removeCycles(ICycleRemoverGraph graph) {
		initialize(graph);

		Set<SugiyamaEdge> DAGEdges = new HashSet<SugiyamaEdge>();
		Set<SugiyamaVertex> DDVertices = DDGraph.getVertexSet();
		Set<SugiyamaEdge> DDEdges = DDGraph.getEdgeSet();

		while(!DDVertices.isEmpty()) {
			SugiyamaVertex vertex = getCurrentSink(DDVertices);

			while (vertex != null) {    //add sink vertices to the edge set in the final directed acyclic graph
				DAGEdges.addAll(DDGraph.incomingEdgesOf(vertex));
				DDVertices.remove(vertex);
				DDEdges.removeAll(DDGraph.incomingEdgesOf(vertex));
				vertex = getCurrentSink(DDVertices);
			}

			for (SugiyamaVertex tmpVertex : DDVertices) {    //remove all isolated vertices
				if (isIsolated(tmpVertex)) {
					DDVertices.remove(tmpVertex);
				}
			}

			vertex = getCurrentSource(DDVertices);

			while (vertex != null) {    //add source vertices to the edge set in the final directed acyclic graph
				DAGEdges.addAll(getCorrectedIncomingEdges(vertex));
				DDVertices.remove(vertex);
				DDEdges.removeAll(DDGraph.outgoingEdgesOf(vertex));
				vertex = getCurrentSource(DDVertices);
			}

			if (!DDVertices.isEmpty()) {
				int outInDiff = 0;
				SugiyamaVertex w = DDVertices.iterator().next();

				for (SugiyamaVertex tmpVertex : DDVertices) {
					int vertexDiff = Math.abs(getCorrectedOutcomingEdges(tmpVertex).size() - getCorrectedIncomingEdges(tmpVertex).size());
					if (vertexDiff > outInDiff) {
						w = tmpVertex;
						outInDiff = vertexDiff;
					}
				}

				DAGEdges.addAll(DDGraph.outgoingEdgesOf(w));
				DDVertices.remove(w);
				DDEdges.removeAll(DDGraph.outgoingEdgesOf(w));
				DDEdges.removeAll(DDGraph.incomingEdgesOf(w));
			}
		}

		return getRemainingEdges(DAGEdges);
	}
	
	private SugiyamaVertex getCurrentSink(Set<SugiyamaVertex> vertices){
		for(SugiyamaVertex vertex : vertices){
			Set<SugiyamaEdge> outgoingEdges = getCorrectedOutcomingEdges(vertex);

			if (outgoingEdges.size() == 0) {
				return vertex;
			}
		}

		return null;
	}
	
	private SugiyamaVertex getCurrentSource(Set<SugiyamaVertex> vertices){
		for(SugiyamaVertex vertex : vertices){
			Set<SugiyamaEdge> incomingEdges = getCorrectedIncomingEdges(vertex);

			if (incomingEdges.size() == 0) {
				return vertex;
			}
		}
		return null;
	}

	private boolean isIsolated(SugiyamaVertex vertex) {
		Set<SugiyamaEdge> outgoingEdges = getCorrectedOutcomingEdges(vertex);
		Set<SugiyamaEdge> incomingEdges = getCorrectedIncomingEdges(vertex);

		if (outgoingEdges.size() + incomingEdges.size() == 0) {
			return true;
		}

		return false;
	}

	private Set<SugiyamaEdge> getCorrectedOutcomingEdges(SugiyamaVertex vertex) {
		Set<SugiyamaEdge> outgoingEdges = DDGraph.outgoingEdgesOf(vertex);

		for (SugiyamaEdge edge : outgoingEdges) {
			if (!graphEdges.contains(edge)) {
				outgoingEdges.remove(edge);
			}
		}

		return outgoingEdges;
	}

	private Set<SugiyamaEdge> getCorrectedIncomingEdges(SugiyamaVertex vertex) {
		Set<SugiyamaEdge> incomingEdges = DDGraph.incomingEdgesOf(vertex);

		for (SugiyamaEdge edge : incomingEdges) {
			if (!graphEdges.contains(edge)) {
				incomingEdges.remove(edge);
			}
		}

		return incomingEdges;
	}
	
	private Set<SugiyamaEdge> getRemainingEdges(Set<SugiyamaEdge> DAGEdges){
		Set<SugiyamaEdge> result = new HashSet<SugiyamaEdge>();
		for(SugiyamaEdge edge:this.graphEdges){
			if(!DAGEdges.contains(edge)){
				result.add(edge);
			}
		}
		return result;
	}
}
