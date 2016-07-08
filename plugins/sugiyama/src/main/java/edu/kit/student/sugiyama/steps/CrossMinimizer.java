package edu.kit.student.sugiyama.steps;

import edu.kit.student.sugiyama.graph.ICrossMinimizerGraph;
import edu.kit.student.sugiyama.graph.ISugiyamaEdge;
import edu.kit.student.sugiyama.graph.ISugiyamaVertex;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This class takes a Sugiyama Graph and rearranges its vertices on each layer to minimize
 * the amount of edge crossings.
 */ 
public class CrossMinimizer implements ICrossMinimizer {

	@Override
	public void minimizeCrossings(ICrossMinimizerGraph graph) {
		System.out.println(graph.getEdgeSet().size());
		//prints the name of every vertex on every layer before minimizing
		graph.getLayers().forEach(iSugiyamaVertices -> System.out.println(iSugiyamaVertices.stream().map(iSugiyamaVertex -> iSugiyamaVertex.getName()).collect(Collectors.joining(", "))));
		System.out.println("");
		int layerCount = graph.getLayerCount();

		addDummyAndEdges(graph);

		//add dummy knots
		int newCrossings = 0;
		int oldCrossings = crossings((SugiyamaGraph) graph);
		System.out.println("crossings before " + oldCrossings);
		int counter = 0;

		while (counter < 10) {
			List<List<ISugiyamaVertex>> undo = new ArrayList<>();

			for (List<ISugiyamaVertex> layer : graph.getLayers()) {
				undo.add(new ArrayList<ISugiyamaVertex>(layer));
			}

			//System.out.println("optimize up");
			for (int i = 1; i < layerCount; i++) {
				optimizeLayer(graph, i, Direction.UP);
			}
			//System.out.println("optimize down");
			for (int i = layerCount - 2; i >= 0; i--) {
				optimizeLayer(graph, i, Direction.DOWN);
			}
			counter++;

			newCrossings = crossings((SugiyamaGraph) graph);

			System.out.println("  crossings in " + counter + ". run: " + newCrossings);

			if (oldCrossings - newCrossings < 0) {
				System.out.println("revert");
				graph.getLayers().clear();
				graph.getLayers().addAll(undo);
				break;
			}

			if (newCrossings == 0 || oldCrossings - newCrossings < 0.001f * oldCrossings) {
				break;
			}

			oldCrossings = newCrossings;
		}

		System.out.println(" ");
		System.out.println("runs = " + counter);
		System.out.println(" ");
		System.out.println("crossings after " + crossings((SugiyamaGraph) graph));
		System.out.println("");
		//prints the name of every vertex on every layer after minimization
		graph.getLayers().forEach(iSugiyamaVertices -> System.out.println(iSugiyamaVertices.stream().map(iSugiyamaVertex -> iSugiyamaVertex.getName()).collect(Collectors.joining(", "))));
		System.out.println("");
		System.out.println("");
	}

	/**
	 * This method adds dummy vertices between the two vertices of an edge, on every layer that this edge skips.
	 * the dummy vertices are connected through supplement edges with each other and the source and target vertex of the edge.
	 * 
	 * @param graph input graph to add dummy vertices and supplement edges to
	 */
	private void addDummyAndEdges(ICrossMinimizerGraph graph) {
		System.out.println("Vertices before: "+graph.getVertexSet().size()+", Edges before: "+graph.getEdgeSet().size());
		Set<ISugiyamaVertex> vertices = graph.getVertexSet();
		Set<ISugiyamaEdge> edges = graph.getEdgeSet();
		Set<ISugiyamaEdge> newEdges = new HashSet<ISugiyamaEdge>();
		Set<ISugiyamaEdge> replacedEdges = new HashSet<ISugiyamaEdge>();
		for(ISugiyamaEdge e : edges){
			ISugiyamaVertex source = e.getSource();
			ISugiyamaVertex target = e.getTarget();
			int lowerLayer = source.getLayer();
			int upperLayer = target.getLayer();
			int diff = upperLayer - lowerLayer;
			assert(diff >= 1);	//diff must not be lower than zero
			assert(graph.getLayer(lowerLayer).contains(source));
			assert(graph.getLayer(upperLayer).contains(target));
			if(diff>1){	//need to add diff dummy vertices
				replacedEdges.add(e);		// the  distance of both vertices of this edge is greater than 1 so it must be replaced
				ISugiyamaVertex nv = null;	// through dummy vertices and supplement edges. add it here to remove it later from the original edge set.
				ISugiyamaEdge ne = null;
				int c = 0;
				for(int l = lowerLayer + 1; l <= upperLayer;l++){
					c++;
					if(l==lowerLayer+1){
						nv = graph.createDummy("d"+c+"("+source.getName()+"->"+target.getName()+")", "", lowerLayer + 1);	//first dummy vertex created
						ne = graph.createSupplementEdge(e.getName()+"("+c+")", "");	//first dummy edge created
						ne.setVertices(source, nv);	//set source and target of first dummy edge
						vertices.add(nv);	//add new vertex to vertex set
						newEdges.add(ne);	//add new edge to edge set
						//graph.getLayer(l).add(nv);	//add new edge to layer list
						((SugiyamaGraph) graph).assignToLayer(nv, l);
					}else if(l==upperLayer){
						ne = graph.createSupplementEdge(e.getName() + "(e" + c + ")", "");
						newEdges.add(ne);
						ne.setVertices(nv, target);	// ! important that nv is always the last created ISugiyamaVertex
					}else{
						ISugiyamaVertex temp = nv;	//temporary ISugiyamaVertex so that the new created vertex is always the one with the variable nv
						nv = graph.createDummy("d"+c+"("+source.getName()+"->"+target.getName()+")", "", c);
						ne = graph.createSupplementEdge(e.getName()+"("+c+")", "");
						ne.setVertices(temp, nv);
						vertices.add(nv);
						newEdges.add(ne);
						//graph.getLayer(l).add(nv);
						((SugiyamaGraph) graph).assignToLayer(nv, l);
					}
				}
			}
		}
		edges.addAll(newEdges);	//add all new generated supplement edges to the old edge list
		edges.removeAll(replacedEdges);	//remove all replaced edges from the original edge set
		System.out.println("Vertices after: "+graph.getVertexSet().size()+", Edges after: "+graph.getEdgeSet().size());
	}

	private int optimizeLayer(ICrossMinimizerGraph graph, int optimizingLayer, Direction dir) {
		int changes = 1;
		List<ISugiyamaVertex> layer = graph.getLayer(optimizingLayer);
		List<ISugiyamaVertex> newLayer;
		Map<ISugiyamaVertex, Float> barycenterMap = new HashMap<>();

		for (ISugiyamaVertex vertex : layer) {
			barycenterMap.put(vertex, getBarycenter(graph, vertex, dir));
		}

		//barycenterMap = layer.stream().parallel().collect(Collectors.toMap(vertex -> vertex, vertex -> getBarycenter(graph, vertex, dir)));

		newLayer = toSortedKeyList(barycenterMap);
		layer.clear();
		layer.addAll(newLayer);

		return changes;
	}
	
	private float getBarycenter (ICrossMinimizerGraph graph, ISugiyamaVertex vertex, Direction dir) {
		Set<ISugiyamaVertex> relevantNeighbors;
		int optimizingLayerNum = vertex.getLayer();
		int fixedLayerNum;

		if (dir == Direction.DOWN) {
			relevantNeighbors = graph.outgoingEdgesOf(vertex).stream().map((ISugiyamaEdge -> ISugiyamaEdge.getTarget())).collect(Collectors.toSet());
			fixedLayerNum = optimizingLayerNum + 1;
		} else if (dir == Direction.UP) {
			fixedLayerNum = optimizingLayerNum - 1;
			relevantNeighbors = graph.incomingEdgesOf(vertex).stream().map((ISugiyamaEdge -> ISugiyamaEdge.getSource())).collect(Collectors.toSet());
		} else {
			throw new NullPointerException();
		}

		List<ISugiyamaVertex> fixedLayer = graph.getLayer(fixedLayerNum);
		OptionalDouble optionalAvarage = relevantNeighbors.stream().mapToDouble((vertex1 -> fixedLayer.indexOf(vertex1))).average();

		if (optionalAvarage.isPresent()) {
			return (float) optionalAvarage.getAsDouble();
		} else {
			return (float) graph.getLayer(optimizingLayerNum).indexOf(vertex);
		}
	}

	public static <K, V extends Comparable<? super V>> List<K> toSortedKeyList( Map<K, V> map ) {
		List<K> result = new ArrayList<>();
		Stream<Map.Entry<K, V>> st = map.entrySet().stream();

		st.sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> result.add((K) e.getKey()));

		return result;
	}

	private enum Direction {
		UP, DOWN;
	}

	public static int crossings(SugiyamaGraph graph) {
		int result = 0;

		result = IntStream.range(0, graph.getLayerCount() - 1)
				.parallel()
				.map(i -> {
					return crossingsOfLayers(graph, graph.getLayer(i), graph.getLayer(i + 1));
				}).sum();

		return result;
	}

	public static int crossingsOfLayers(SugiyamaGraph graph, List<ISugiyamaVertex> layer1, List<ISugiyamaVertex> layer2) {
		if (layer1 == null || layer2 == null) {
			return 0;
		}

		if (layer1.size() <= 1) {
			return 0;
		}

		int result = 0;
		for (int i = layer1.size() - 1; --i >= 0;) {
			int k = i + 1;
			for (int j = layer1.size() - 1; --j >= k;) {
				result += crossingsOfVertices(graph, layer1.get(i), layer1.get(j), layer2);
			}
		}

		return result;
	}

	private static int crossingsOfVertices(SugiyamaGraph graph, ISugiyamaVertex vertex1, ISugiyamaVertex vertex2, List<ISugiyamaVertex> layer) {
		if (vertex1.getID() == vertex2.getID()) {
			return 0;
		}

		//System.out.println(vertex1.getName() + " X  " + vertex2.getName());

		Set<ISugiyamaEdge> vertex1Edges = graph.edgesOf(vertex1);

		vertex1Edges = vertex1Edges
				.stream()
				.parallel()
				.filter(edge -> layer.contains(edge.getSource()) || layer.contains(edge.getTarget()))
				.collect(Collectors.toSet());

		List<ISugiyamaVertex> sources = vertex1Edges.stream().parallel().map(sugiyamaEdge -> sugiyamaEdge.getSource()).filter(vertex -> vertex.getID() != vertex2.getID()).collect(Collectors.toList());
		List<ISugiyamaVertex> targets = vertex1Edges.stream().parallel().map(sugiyamaEdge -> sugiyamaEdge.getTarget()).filter(vertex -> vertex.getID() != vertex2.getID()).collect(Collectors.toList());
		//sources.removeIf(vertex -> vertex.getID() == vertex1.getID());
		//targets.removeIf(vertex -> vertex.getID() == vertex1.getID());

		List<ISugiyamaVertex> neighbors1 = sources;
		neighbors1.addAll(targets);

		Set<ISugiyamaEdge> vertex2Edges = graph.edgesOf(vertex2);

		vertex2Edges = vertex2Edges
				.stream()
				.parallel()
				.filter(edge -> layer.contains(edge.getSource()) || layer.contains(edge.getTarget()))
				.collect(Collectors.toSet());

		sources = vertex2Edges.stream().parallel().map(sugiyamaEdge -> sugiyamaEdge.getSource()).filter(vertex -> vertex.getID() != vertex2.getID()).collect(Collectors.toList());
		targets = vertex2Edges.stream().parallel().map(sugiyamaEdge -> sugiyamaEdge.getTarget()).filter(vertex -> vertex.getID() != vertex2.getID()).collect(Collectors.toList());
		//sources.removeIf(vertex -> vertex.getID() == vertex2.getID());
		//targets.removeIf(vertex -> vertex.getID() == vertex2.getID());

		List<ISugiyamaVertex> neighbors2 = sources;
		neighbors2.addAll(targets);

		int result = 0;

		for (ISugiyamaVertex x1 : neighbors1) {
			for (ISugiyamaVertex x2 : neighbors2) {
				//System.out.println("   " + x1.getName() + " ? " + x2.getName());
				if (layer.indexOf(x1) > layer.indexOf(x2)) {
					//System.out.println("  hit");
					result++;
				}
			}
		}

		//result = neighbors1.stream().parallel().mapToInt(n1 -> neighbors2.stream().mapToInt(n2 -> layer.indexOf(n1) > layer.indexOf(n2) ? 1 : 0).sum()).sum();

		return result;
	}
}
