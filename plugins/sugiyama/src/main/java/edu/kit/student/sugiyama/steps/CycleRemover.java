package edu.kit.student.sugiyama.steps;

import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.sugiyama.graph.ICycleRemoverGraph;
import edu.kit.student.sugiyama.graph.ISugiyamaEdge;
import edu.kit.student.sugiyama.graph.ISugiyamaVertex;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class takes a directed Graph G = (V, E) and removes a set of edges E_ 
 * so that the resulting Graph G' = (V, E\E_) is a DAG(Directed Acyclic Graph).
 */
public class CycleRemover implements ICycleRemover {
	private DefaultDirectedGraph<ISugiyamaVertex, ISugiyamaEdge> DDGraph;
	
	
	@Override
	public void removeCycles(ICycleRemoverGraph graph) {
		initialize(graph);

		Set<ISugiyamaEdge> DAGEdges = new HashSet<ISugiyamaEdge>();
		Set<ISugiyamaVertex> DDVertices = DDGraph.getVertexSet();
		Set<ISugiyamaEdge> DDEdges = DDGraph.getEdgeSet();
		System.out.println("vertices: " + DDVertices.size());
		System.out.println("edges: " + DDEdges.size());

		while(!DDVertices.isEmpty()) {
			ISugiyamaVertex vertex = getCurrentSink(DDVertices, DDEdges);
			

			while (vertex != null) {    //add sink vertices to the edge set in the final directed acyclic graph
				System.out.println("sink: " + vertex.getName());
				DAGEdges.addAll(getCorrectedIncomingEdges(vertex, DDEdges));
				DDVertices.remove(vertex);
				DDEdges.removeAll(DDGraph.incomingEdgesOf(vertex));
				vertex = getCurrentSink(DDVertices, DDEdges);
			}

			for (ISugiyamaVertex tmpVertex : DDVertices) {    //remove all isolated vertices
				if (isIsolated(tmpVertex, DDEdges)) {
					System.out.println("isolated: " + vertex.getName());
					DDVertices.remove(tmpVertex);
				}
			}

			vertex = getCurrentSource(DDVertices, DDEdges);

			while (vertex != null) {    //add source vertices to the edge set in the final directed acyclic graph
				System.out.println("source: " + vertex.getName());
				DAGEdges.addAll(getCorrectedOutcomingEdges(vertex, DDEdges));
				DDVertices.remove(vertex);
				DDEdges.removeAll(DDGraph.outgoingEdgesOf(vertex));
				vertex = getCurrentSource(DDVertices, DDEdges);
			}

			if (!DDVertices.isEmpty()) {
				int outInDiff = -1;
				int outSize = 0;
				int inSize = 0;
				ISugiyamaVertex highestOutInDiffVertex = null;

				for (ISugiyamaVertex tmpVertex : DDVertices) {
					outSize = getCorrectedOutcomingEdges(tmpVertex, DDEdges).size();
					inSize = getCorrectedIncomingEdges(tmpVertex, DDEdges).size();
					int vertexDiff = Math.abs(outSize - inSize);
					if (vertexDiff > outInDiff) {
						highestOutInDiffVertex = tmpVertex;
						outInDiff = vertexDiff;
					}
				}

				if (outSize < inSize) {
					System.out.println("remove outs of: " + highestOutInDiffVertex.getName());
					System.out.println(DDGraph.outgoingEdgesOf(highestOutInDiffVertex).stream().map(v -> v.getName()).collect(Collectors.joining(", ")));
					DAGEdges.addAll(getCorrectedOutcomingEdges(highestOutInDiffVertex, DDEdges));
				} else {
					System.out.println("remove ins of: " + highestOutInDiffVertex.getName());
					System.out.println(DDGraph.incomingEdgesOf(highestOutInDiffVertex).stream().map(v -> v.getName()).collect(Collectors.joining(", ")));
					DAGEdges.addAll(getCorrectedIncomingEdges(highestOutInDiffVertex, DDEdges));
				}

				DDVertices.remove(highestOutInDiffVertex);
				DDEdges.removeAll(DDGraph.outgoingEdgesOf(highestOutInDiffVertex));
				DDEdges.removeAll(DDGraph.incomingEdgesOf(highestOutInDiffVertex));
			}
		}


		System.out.println("reversed: " + DAGEdges.size());
		reverseEdges(getEdgesToTurn(DAGEdges, graph.getEdgeSet()),graph);
	}
	
	/**
	 * Reverses every edge from the parameter set in the original graph.
	 * @param edges edges to turn their direction in the original graph
	 */
	private void reverseEdges(Set<ISugiyamaEdge> edges, ICycleRemoverGraph originalGraph){
		for(ISugiyamaEdge edge:edges){
			originalGraph.reverseEdge(edge);
		}
	}
	
	private ISugiyamaVertex getCurrentSink(Set<ISugiyamaVertex> dDVertices, Set<ISugiyamaEdge> graphEdges){
		for(ISugiyamaVertex vertex : dDVertices){
			Set<ISugiyamaEdge> outgoingEdges = getCorrectedOutcomingEdges(vertex, graphEdges);

			if (outgoingEdges.size() == 0) {
				return vertex;
			}
		}

		return null;
	}
	
	private ISugiyamaVertex getCurrentSource(Set<ISugiyamaVertex> vertices, Set<ISugiyamaEdge> graphEdges){
		for(ISugiyamaVertex vertex : vertices){
			Set<ISugiyamaEdge> incomingEdges = getCorrectedIncomingEdges(vertex, graphEdges);

			if (incomingEdges.size() == 0) {
				return vertex;
			}
		}
		return null;
	}

	private boolean isIsolated(ISugiyamaVertex vertex, Set<ISugiyamaEdge> graphEdges) {
		Set<ISugiyamaEdge> outgoingEdges = getCorrectedOutcomingEdges(vertex, graphEdges);
		Set<ISugiyamaEdge> incomingEdges = getCorrectedIncomingEdges(vertex, graphEdges);

		if (outgoingEdges.size() + incomingEdges.size() == 0) {
			return true;
		}

		return false;
	}

	private Set<ISugiyamaEdge> getCorrectedOutcomingEdges(ISugiyamaVertex vertex, Set<ISugiyamaEdge> graphEdges) {
		return DDGraph.outgoingEdgesOf(vertex).stream().filter(edge -> graphEdges.contains(edge)).collect(Collectors.toSet());
	}

	private Set<ISugiyamaEdge> getCorrectedIncomingEdges(ISugiyamaVertex vertex, Set<ISugiyamaEdge> graphEdges) {
		return DDGraph.incomingEdgesOf(vertex).stream().filter(edge -> graphEdges.contains(edge)).collect(Collectors.toSet());
	}
	
	/**
	 * Returns the edges that are contained in the original graph and are missing in the DAGEdges that describe a maximum acyclic graph.
	 * @param DAGEdges edges in the maximum acyclic graph
	 * @return the edges that have to be turned in order to remove the cycles in the original graph
	 */
	private Set<ISugiyamaEdge> getEdgesToTurn(Set<ISugiyamaEdge> DAGEdges, Set<ISugiyamaEdge> graphEdges){
		Set<ISugiyamaEdge> result = new HashSet<ISugiyamaEdge>();
		for(ISugiyamaEdge edge: graphEdges){
			if(!DAGEdges.contains(edge)){
				result.add(edge);
			}
		}

		System.out.println("reversed: " + result.size());
		System.out.println("");
		return result;
	}
	
	/**
	 * Initializes the DDGraph and its vertices and edges. 
	 * Also initializes the vertex-set and edge-set that contain the vertices and edges of the original graph.
	 * 
	 * @param graph original graph to build a DefaultDirectedGraph from
	 */
	private void initialize(ICycleRemoverGraph graph){
		Set<ISugiyamaVertex> graphVertices;
		Set<ISugiyamaEdge> graphEdges;
		graphVertices = graph.getVertexSet();
		graphEdges = graph.getEdgeSet();
		
		Set<ISugiyamaVertex> DDVertices = new HashSet<ISugiyamaVertex>();
		Set<ISugiyamaEdge> DDEdges = new HashSet<ISugiyamaEdge>();
		
		for(ISugiyamaVertex vertex : graphVertices){
			DDVertices.add(vertex);
		}

		for(ISugiyamaEdge edge: graphEdges){
			DDEdges.add(edge);
		}
		this.DDGraph = new DefaultDirectedGraph("", DDVertices, DDEdges);
	}
	
	/**
	 * helper method for printing the DDGraph with all its vertices and edges
	 */
	@SuppressWarnings("unused")
	private void print(){
		String out="";
		out+="Vertices: ";
		for(ISugiyamaVertex v:(Set<ISugiyamaVertex>)DDGraph.getVertexSet()){
			out+=v.getName()+",";
		}
		out+="| Edges: ";
		for(ISugiyamaEdge e: (Set<ISugiyamaEdge>)DDGraph.getEdgeSet()){
			out+=e.getName()+",";
		}
		System.out.println(out);
	}
}
