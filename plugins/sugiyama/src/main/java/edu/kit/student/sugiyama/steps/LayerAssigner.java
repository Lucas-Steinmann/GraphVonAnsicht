package edu.kit.student.sugiyama.steps;

import edu.kit.student.sugiyama.RelativeLayerConstraint;
import edu.kit.student.sugiyama.graph.ILayerAssignerGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;

import java.util.HashSet;
import java.util.Set;

/**
 * This class takes a directed graph and assigns every vertex in it a layer.
 */
public class LayerAssigner implements ILayerAssigner {

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
		Set<SugiyamaGraph.SugiyamaVertex> vertices = graph.getVertexSet();
		Set<SugiyamaGraph.SugiyamaEdge> edges = graph.getEdgeSet();
		int layer = 0;

		while (!vertices.isEmpty()) {
			System.out.println(vertices.size());
			Set<SugiyamaGraph.SugiyamaVertex> currentSources = getSources(graph, edges, vertices);

			for (SugiyamaGraph.SugiyamaVertex vertex : currentSources) {
				vertex.setLayer(layer);
				vertices.remove(vertex);
				edges.removeAll(graph.outgoingEdgesOf(vertex));
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

		for (SugiyamaGraph.SugiyamaEdge edge : incomingEdges) {
			if (!edges.contains(edge)) {
				incomingEdges.remove(edge);
			}
		}

		return incomingEdges;
	}
	

}
