package edu.kit.student.sugiyama.steps;

import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.sugiyama.AbsoluteLayerConstraint;
import edu.kit.student.sugiyama.RelativeLayerConstraint;
import edu.kit.student.sugiyama.graph.ILayerAssignerGraph;
import edu.kit.student.sugiyama.graph.ISugiyamaEdge;
import edu.kit.student.sugiyama.graph.ISugiyamaVertex;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class takes a directed graph and assigns every vertex in it a layer.
 */
public class LayerAssigner implements ILayerAssigner {
	private DefaultDirectedGraph<ISugiyamaVertex, ISugiyamaEdge> DDGraph;
	private Set<ISugiyamaVertex> graphVertices;
	private Set<ISugiyamaEdge> graphEdges;
	private Set<RelativeLayerConstraint> relativeConstraints;
	private Set<AbsoluteLayerConstraint> absoluteConstraints;

    @Override
    public void addRelativeConstraints(Set<RelativeLayerConstraint> constraints) {
        this.relativeConstraints = constraints;        
    }

    @Override
    public void addAbsoluteConstraints(Set<AbsoluteLayerConstraint> constraints) {
        this.absoluteConstraints = constraints;
        
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
		System.out.println("LayerAssigner.assignLayers():");
		initialize(graph);
		Set<ISugiyamaVertex> DDVertices = DDGraph.getVertexSet();
		Set<ISugiyamaEdge> DDEdges = DDGraph.getEdgeSet();
		int layer = 0;

		while (!DDVertices.isEmpty() && layer <= 100) {
			Set<ISugiyamaVertex> currentSources = getSources(graph, DDEdges, DDVertices);

			if (currentSources.size() == 0) {
				currentSources = new HashSet<>(DDVertices);
			}

			for (ISugiyamaVertex vertex : currentSources) {
				graph.assignToLayer(vertex, layer);
				DDVertices.remove(vertex);
				DDEdges.removeAll(graph.outgoingEdgesOf(vertex));
			}

			layer++;
		}
		//for printing the layers after layer assigning
		graph.getLayers().forEach(iSugiyamaVertices -> System.out.println(iSugiyamaVertices.stream().map(iSugiyamaVertex -> iSugiyamaVertex.getName()).collect(Collectors.joining(", "))));
	}

	private Set<ISugiyamaVertex> getSources(
			ILayerAssignerGraph graph,
			Set<ISugiyamaEdge> edges,
			Set<ISugiyamaVertex> vertices
	) {
		Set<ISugiyamaVertex> result = new HashSet<>();

		for (ISugiyamaVertex vertex : vertices) {
			Set<ISugiyamaEdge> incomingEdges = getCorrectedIncomingEdges(graph, edges, vertex);

			if (incomingEdges.size() == 0) {
				result.add(vertex);
			}
		}

		return result;
	}

	private Set<ISugiyamaEdge> getCorrectedIncomingEdges(
			ILayerAssignerGraph graph,
			Set<ISugiyamaEdge> edges,
			ISugiyamaVertex vertex
	) {
		Set<ISugiyamaEdge> incomingEdges = graph.incomingEdgesOf(vertex);
		Set<ISugiyamaEdge> selfLoops = graph.selfLoopsOf(vertex);
		Set<ISugiyamaEdge> tempEdges = new HashSet<ISugiyamaEdge>(); //necessary in order don't to get a
		tempEdges.addAll(incomingEdges);							//concurrentModificationException
		

		for (ISugiyamaEdge edge : tempEdges) {
			if (!edges.contains(edge)) {
				incomingEdges.remove(edge);
			} else if (selfLoops.contains(edge)) {
				incomingEdges.remove(edge);
			}
		}

		return incomingEdges;
	}
	
	/**
	 * Initializes the DDGraph and its vertices and edges. 
	 * Also initializes the vertex-set and edge-set that contain the vertices and edges of the original graph.
	 * 
	 * @param graph original graph to build a DefaultDirectedGraph from
	 */
	private void initialize(ILayerAssignerGraph graph){
		this.graphVertices = graph.getVertexSet();
		this.graphEdges = graph.getEdgeSet();
		
		Set<ISugiyamaVertex> DDVertices = new HashSet<ISugiyamaVertex>();
		Set<ISugiyamaEdge> DDEdges = new HashSet<ISugiyamaEdge>();
		
		for(ISugiyamaVertex vertex : this.graphVertices){
			DDVertices.add(vertex);
		}

		for(ISugiyamaEdge edge: this.graphEdges){
			DDEdges.add(edge);
		}
		this.DDGraph = new DefaultDirectedGraph<>(DDVertices, DDEdges);
	}
	

}
