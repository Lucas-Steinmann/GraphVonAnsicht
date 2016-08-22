package edu.kit.student.sugiyama.steps;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.sugiyama.AbsoluteLayerConstraint;
import edu.kit.student.sugiyama.LayerContainsOnlyConstraint;
import edu.kit.student.sugiyama.RelativeLayerConstraint;
import edu.kit.student.sugiyama.graph.ILayerAssignerGraph;
import edu.kit.student.sugiyama.graph.ISugiyamaEdge;
import edu.kit.student.sugiyama.graph.ISugiyamaVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
	private List<LayerContainsOnlyConstraint> layerContainsOnlyConstraints;
	private Set<ISugiyamaVertex> ignoredVertices;

    final Logger logger = LoggerFactory.getLogger(LayerAssigner.class);

	public LayerAssigner() {
		relativeConstraints = new HashSet<>();
		absoluteConstraints = new HashSet<>();
		layerContainsOnlyConstraints = new LinkedList<>();
		ignoredVertices = new HashSet<>();
	}

	@Override
    public void addRelativeConstraints(Set<RelativeLayerConstraint> constraints) {
        this.relativeConstraints.addAll(constraints);
		logger.info("relative Layer Constraint added");
		logger.info(constraints.toString());
	}

    @Override
    public void addAbsoluteConstraints(Set<AbsoluteLayerConstraint> constraints) {
        this.absoluteConstraints.addAll(constraints);
		logger.info("absolute Layer Constraint added");
		logger.info(constraints.toString());
    }

	@Override
	public void addLayerContainsOnlyConstraints(Set<LayerContainsOnlyConstraint> constraints) {
		this.layerContainsOnlyConstraints.addAll(constraints);
		logger.info("absolute Layer Constraint added");
		logger.info(constraints.toString());
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
		logger.info("LayerAssigner.assignLayers():");
		initialize(graph);
		Set<ISugiyamaVertex> DDVertices = DDGraph.getVertexSet();
		Set<ISugiyamaEdge> DDEdges = DDGraph.getEdgeSet();
		int layer = 0;

		while (!DDVertices.isEmpty()) {
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

		for (RelativeLayerConstraint currentConstraint : this.relativeConstraints) {
			int lowestLayer = currentConstraint.topSet().stream().mapToInt(vertex -> graph.getLayer(graph.getVertexByID(vertex.getID()))).min().getAsInt();
			currentConstraint.topSet().forEach(vertex -> ignoredVertices.add(graph.getVertexByID(vertex.getID())));

			for (Vertex bottomVertex : currentConstraint.bottomSet()) {
				ISugiyamaVertex wrapper = graph.getVertexByID(bottomVertex.getID());
				int distance = wrapper.getLayer() - (lowestLayer + currentConstraint.getDistance() - 1);

				if (distance <= 0) {
					pushDown(wrapper, graph, 1 - distance);
				}
			}
			ignoredVertices.clear();
		}

		layerContainsOnlyConstraints.sort(((o1, o2) -> o1.getLayer() - o2.getLayer()));

		for (LayerContainsOnlyConstraint onlyConstraint : this.layerContainsOnlyConstraints) {
			for (ISugiyamaVertex vertex : graph.getLayer(onlyConstraint.getLayer())) {
				if (!onlyConstraint.getVertices().contains(vertex.getVertex())) {
					pushDown(vertex, graph, 1);
				}
			}

			ignoredVertices.clear();
		}

		for (AbsoluteLayerConstraint absoluteConstraint : this.absoluteConstraints) {
			for (Vertex vertex : absoluteConstraint.getVertices()) {
				ISugiyamaVertex wrapper = graph.getVertexByID(vertex.getID());
				graph.assignToLayer(wrapper, absoluteConstraint.getLayer());
			}
		}

		graph.cleanUpEmtpyLayers();

		for (ISugiyamaEdge edge : graph.getEdgeSet()) {
			int sourceLayer = graph.getLayer(edge.getSource());
			int targetLayer = graph.getLayer(edge.getTarget());

			if (targetLayer < sourceLayer) {
				graph.reverseEdge(edge);
			}
		}

		//for printing the layers after layer assigning
		//graph.getLayers().forEach(iSugiyamaVertices -> logger.debug(iSugiyamaVertices.stream().map(iSugiyamaVertex -> iSugiyamaVertex.getName()).collect(Collectors.joining(", "))));
	}

	private void pushDown(ISugiyamaVertex vertex, ILayerAssignerGraph graph, int amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("amount of layers pusht must be greater than 0. Is " + amount);
		}

		int currentLayer = graph.getLayer(vertex);
		int targetLayer = currentLayer + amount;
		Set<ISugiyamaVertex> affectedVertices = graph.outgoingEdgesOf(vertex).stream().map(edge -> edge.getTarget()).collect(Collectors.toSet());
		graph.assignToLayer(vertex, targetLayer);
		ignoredVertices.add(vertex);

		for (ISugiyamaVertex affectedVertex : affectedVertices) {
			if (affectedVertex.getLayer() > targetLayer || ignoredVertices.contains(affectedVertex)) {
				continue;
			}

			pushDown(affectedVertex, graph, targetLayer - affectedVertex.getLayer() + 1);
		}
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
