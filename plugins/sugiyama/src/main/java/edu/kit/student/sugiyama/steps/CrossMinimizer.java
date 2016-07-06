package edu.kit.student.sugiyama.steps;

import edu.kit.student.sugiyama.graph.ICrossMinimizerGraph;
import edu.kit.student.sugiyama.graph.ISugiyamaVertex;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class takes a Sugiyama Graph and rearranges its vertices on each layer to minimize
 * the amount of edge crossings.
 */ 
public class CrossMinimizer implements ICrossMinimizer {
	
	private Set<ISugiyamaVertex> Ivertices = new HashSet<ISugiyamaVertex>();

	@Override
	public void minimizeCrossings(ICrossMinimizerGraph graph) {
		System.out.println("graph before minimization");
		int layerCount = graph.getLayerCount();
		for (int i = 0; i < layerCount; i++) {
			System.out.println(graph.getLayer(i).stream().map(vertex -> vertex.getName()).collect(Collectors.joining(", ")));
		}
		System.out.println(" ");

		//add dummy knots
		int changes = Integer.MAX_VALUE;
		int counter = 0;
		while (changes > 0 && counter < 20) {
			changes = 0;
			//System.out.println("optimize up");
			for (int i = 1; i < layerCount; i++) {
				changes += optimizeLayer(graph, i, Direction.UP);
			}
			//System.out.println("optimize down");
			for (int i = layerCount - 2; i >= 0; i--) {
				changes += optimizeLayer(graph, i, Direction.DOWN);
			}

			counter++;
		}
		System.out.println(" ");
		System.out.println("runs = " + counter);
		System.out.println(" ");
		System.out.println("graph after minimization");
		for (int i = 0; i < layerCount; i++) {
			System.out.println(graph.getLayer(i).stream().map(vertex -> vertex.getName()).collect(Collectors.joining(", ")));
		}
	}

	private int optimizeLayer(ICrossMinimizerGraph graph, int optimizingLayer, Direction dir) {
		int changes = 0;
		List<SugiyamaGraph.SugiyamaVertex> layer = graph.getLayer(optimizingLayer);
		List<SugiyamaGraph.SugiyamaVertex> oldLayer = new LinkedList<>(layer);
		List<Integer> currentPositions = new LinkedList<>();
		List<Integer> newPositions = new LinkedList<>();
		Map<Integer, Float> barycenterMap = new HashMap<>();

		for (SugiyamaGraph.SugiyamaVertex vertex : layer) {
			int index = layer.indexOf(vertex);
			currentPositions.add(index);
			barycenterMap.put(index, getBarycenter(graph, vertex, dir));
		}

		newPositions = toSortedKeyList(barycenterMap);

		//System.out.println(graph.getLayer(optimizingLayer).stream().map(vertex -> vertex.getName()).collect(Collectors.joining(", ")));

		for (int i = 0; i < newPositions.size(); i++) {
			int currentPosition = currentPositions.get(i);
			int newPosition = newPositions.get(i);
			List<SugiyamaGraph.SugiyamaVertex> layer1 = graph.getLayer(optimizingLayer);

			if (newPosition != currentPosition) {
				SugiyamaGraph.SugiyamaVertex currentVertex = layer1.get(currentPosition);
				SugiyamaGraph.SugiyamaVertex newVertex = layer1.get(newPosition);
				currentPositions.set(newPosition, currentPosition);
				currentPositions.set(currentPosition, newPosition);
				graph.swapVertices(currentVertex, newVertex);
				changes++;
				//System.out.println(newPositions.toString());
				//System.out.println(graph.getLayer(optimizingLayer).stream().map(vertex -> vertex.getName()).collect(Collectors.joining(", ")));
			}
		}

		List<SugiyamaGraph.SugiyamaVertex> higherLayer;
		try {
			higherLayer = graph.getLayer(optimizingLayer + 1);
		} catch (Exception e) {
			higherLayer = null;
		}
		List<SugiyamaGraph.SugiyamaVertex> lowerLayer;
		try {
			lowerLayer = graph.getLayer(optimizingLayer - 1);
		} catch (Exception e) {
			lowerLayer = null;
		}

		if (
				crossingsOfLayers((SugiyamaGraph) graph, graph.getLayer(optimizingLayer), higherLayer) +
				crossingsOfLayers((SugiyamaGraph) graph, graph.getLayer(optimizingLayer), lowerLayer)
						>
						crossingsOfLayers((SugiyamaGraph) graph, oldLayer, higherLayer) +
						crossingsOfLayers((SugiyamaGraph) graph, oldLayer, lowerLayer)
				) {
			graph.getLayer(optimizingLayer).clear();
			graph.getLayer(optimizingLayer).addAll(oldLayer);
			changes = 0;
		}

		return changes;
	}
	
	private float getBarycenter (ICrossMinimizerGraph graph, SugiyamaGraph.SugiyamaVertex vertex, Direction dir) {
		Set<SugiyamaGraph.SugiyamaVertex> relevantNeighbors;
		int optimizingLayerNum = vertex.getLayer();
		int fixedLayerNum;

		if (dir == Direction.DOWN) {
			relevantNeighbors = graph.outgoingEdgesOf(vertex).stream().map((sugiyamaEdge -> sugiyamaEdge.getTarget())).collect(Collectors.toSet());
			fixedLayerNum = optimizingLayerNum + 1;
		} else if (dir == Direction.UP) {
			fixedLayerNum = optimizingLayerNum - 1;
			relevantNeighbors = graph.incomingEdgesOf(vertex).stream().map((sugiyamaEdge -> sugiyamaEdge.getSource())).collect(Collectors.toSet());
		} else {
			throw new NullPointerException();
		}

		List<SugiyamaGraph.SugiyamaVertex> fixedLayer = graph.getLayer(fixedLayerNum);
		//System.out.println(vertex.getName() + ": " + relevantNeighbors.stream().map(vertex1 -> Integer.toString(fixedLayer.indexOf(vertex1))).collect(Collectors.joining(", ")));
		OptionalDouble optionalAvarage = relevantNeighbors.stream().mapToDouble((vertex1 -> fixedLayer.indexOf(vertex1))).average();

		if (optionalAvarage.isPresent()) {
			return (float) optionalAvarage.getAsDouble();
		} else {
			return (float) graph.getLayer(optimizingLayerNum).indexOf(vertex);
		}
	}

	public static <K, V extends Comparable<? super V>> List<K> toSortedKeyList( Map<K, V> map ) {
		List<K> result = new LinkedList<>();
		Stream<Map.Entry<K, V>> st = map.entrySet().stream();

		st.sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> result.add((K) e.getKey()));

		return result;
	}

	private enum Direction {
		UP, DOWN;
	}
	
	public static int crossings(SugiyamaGraph graph) {
		int result = 0;

		for (int i = 0; i < graph.getLayerCount() - 2; i++) {
			result += crossingsOfLayers(graph, graph.getLayer(i), graph.getLayer(i + 1));
		}

		return result;
	}

	public static int crossingsOfLayers(SugiyamaGraph graph, List<SugiyamaGraph.SugiyamaVertex> layer1, List<SugiyamaGraph.SugiyamaVertex> layer2) {
		if (layer1 == null || layer2 == null) {
			return 0;
		}

		if (layer1.size() <= 1) {
			return 0;
		}

		int result = 0;
		SugiyamaGraph.SugiyamaVertex firstVertex = layer1.get(0);
		SugiyamaGraph.SugiyamaVertex previousVertex = firstVertex;
		layer1.remove(0);

		for (SugiyamaGraph.SugiyamaVertex currentVertex: layer1) {
			result += crossingsOfVertices(graph, currentVertex, previousVertex, layer2);
			previousVertex = currentVertex;
		}

		layer1.add(0, firstVertex);
		return result;
	}

	private static int crossingsOfVertices(SugiyamaGraph graph, SugiyamaGraph.SugiyamaVertex vertex1, SugiyamaGraph.SugiyamaVertex vertex2, List<SugiyamaGraph.SugiyamaVertex> layer2) {
		Set<SugiyamaGraph.SugiyamaEdge> vertex1Edges = graph.edgesOf(vertex1);

		for (Iterator<SugiyamaGraph.SugiyamaEdge> iterator = vertex1Edges.iterator(); iterator.hasNext();) {
			SugiyamaGraph.SugiyamaEdge edge = iterator.next();
			if (!layer2.contains(edge.getSource()) && !layer2.contains(edge.getTarget())) {
				iterator.remove();
			}
		}

		List<SugiyamaGraph.SugiyamaVertex> sources = vertex1Edges.stream().map(sugiyamaEdge -> sugiyamaEdge.getSource()).collect(Collectors.toList());
		List<SugiyamaGraph.SugiyamaVertex> targets = vertex1Edges.stream().map(sugiyamaEdge -> sugiyamaEdge.getTarget()).collect(Collectors.toList());
		sources.removeIf(vertex -> vertex.getID() == vertex1.getID());
		targets.removeIf(vertex -> vertex.getID() == vertex1.getID());

		List<SugiyamaGraph.SugiyamaVertex> neighbors1 = new LinkedList<>(sources);
		neighbors1.addAll(targets);

		Set<SugiyamaGraph.SugiyamaEdge> vertex2Edges = graph.edgesOf(vertex2);

		for (Iterator<SugiyamaGraph.SugiyamaEdge> iterator = vertex2Edges.iterator(); iterator.hasNext();) {
			SugiyamaGraph.SugiyamaEdge edge = iterator.next();
			if (!layer2.contains(edge.getSource()) && !layer2.contains(edge.getTarget())) {
				iterator.remove();
			}
		}

		sources = vertex2Edges.stream().map(sugiyamaEdge -> sugiyamaEdge.getSource()).collect(Collectors.toList());
		targets = vertex2Edges.stream().map(sugiyamaEdge -> sugiyamaEdge.getTarget()).collect(Collectors.toList());
		sources.removeIf(vertex -> vertex.getID() == vertex1.getID());
		targets.removeIf(vertex -> vertex.getID() == vertex1.getID());

		List<SugiyamaGraph.SugiyamaVertex> neighbors2 = new LinkedList<>(sources);
		neighbors2.addAll(targets);

		int result = 0;

		for (SugiyamaGraph.SugiyamaVertex x1 : neighbors1) {
			for (SugiyamaGraph.SugiyamaVertex x2 : neighbors2) {
				if (layer2.indexOf(x1) > layer2.indexOf(x2)) {
					result++;
				}
			}
		}

		return result;
	}
}
