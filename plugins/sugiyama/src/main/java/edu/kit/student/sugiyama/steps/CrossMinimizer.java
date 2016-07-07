package edu.kit.student.sugiyama.steps;

import edu.kit.student.sugiyama.graph.ICrossMinimizerGraph;
import edu.kit.student.sugiyama.graph.ISugiyamaEdge;
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

	@Override
	public void minimizeCrossings(ICrossMinimizerGraph graph) {
		System.out.println("graph before minimization");
		int layerCount = graph.getLayerCount();
		for (int i = 0; i < layerCount; i++) {
			System.out.println(graph.getLayer(i).stream().map(vertex -> vertex.getName()).collect(Collectors.joining(", ")));
		}
		System.out.println(" ");

		addDummyVertices();

		//add dummy knots
		int newCrossings = 0;
		int oldCrossings = crossings((SugiyamaGraph) graph);
		int counter = 0;

		while (counter < 20) {
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

			if (newCrossings == 0 || oldCrossings - newCrossings < 0.001f * oldCrossings) {
				break;
			}

			oldCrossings = newCrossings;
		}

		System.out.println(" ");
		System.out.println("runs = " + counter);
		System.out.println(" ");
		System.out.println("graph after minimization");
		for (int i = 0; i < layerCount; i++) {
			System.out.println(graph.getLayer(i).stream().map(vertex -> vertex.getName()).collect(Collectors.joining(", ")));
		}
	}

	private void addDummyVertices() {
		// TODO Auto-generated method stub

	}

	private int optimizeLayer(ICrossMinimizerGraph graph, int optimizingLayer, Direction dir) {
		int changes = 1;
		List<ISugiyamaVertex> layer = graph.getLayer(optimizingLayer);
		List<ISugiyamaVertex> oldLayer = new LinkedList<>(layer);
		List<Integer> currentPositions = new LinkedList<>();
		List<Integer> newPositions = new LinkedList<>();
		List<ISugiyamaVertex> newLayer;
		Map<ISugiyamaVertex, Float> barycenterMap = new HashMap<>();

		for (ISugiyamaVertex vertex : layer) {
			barycenterMap.put(vertex, getBarycenter(graph, vertex, dir));
		}

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

		for (int i = 0; i < graph.getLayerCount() - 1; i++) {
			result += crossingsOfLayers(graph, graph.getLayer(i), graph.getLayer(i + 1));
		}

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
		for (int i = 0; i < layer1.size(); i++) {
			for (int j = i + 1; j < layer1.size(); j++) {
				result += crossingsOfVertices(graph, layer1.get(i), layer1.get(j), layer2);
			}
		}

		return result;
	}

	private static int crossingsOfVertices(SugiyamaGraph graph, ISugiyamaVertex vertex1, ISugiyamaVertex vertex2, List<ISugiyamaVertex> layer2) {
		if (vertex1.getID() == vertex2.getID()) {
			return 0;
		}

		//System.out.println(vertex1.getName() + " X  " + vertex2.getName());

		Set<ISugiyamaEdge> vertex1Edges = graph.edgesOf(vertex1);

		for (Iterator<ISugiyamaEdge> iterator = vertex1Edges.iterator(); iterator.hasNext();) {
			ISugiyamaEdge edge = iterator.next();
			if (!layer2.contains(edge.getSource()) && !layer2.contains(edge.getTarget())) {
				iterator.remove();
			}
		}

		List<ISugiyamaVertex> sources = vertex1Edges.stream().map(sugiyamaEdge -> sugiyamaEdge.getSource()).collect(Collectors.toList());
		List<ISugiyamaVertex> targets = vertex1Edges.stream().map(sugiyamaEdge -> sugiyamaEdge.getTarget()).collect(Collectors.toList());
		sources.removeIf(vertex -> vertex.getID() == vertex1.getID());
		targets.removeIf(vertex -> vertex.getID() == vertex1.getID());

		List<ISugiyamaVertex> neighbors1 = new LinkedList<>(sources);
		neighbors1.addAll(targets);

		Set<ISugiyamaEdge> vertex2Edges = graph.edgesOf(vertex2);

		for (Iterator<ISugiyamaEdge> iterator = vertex2Edges.iterator(); iterator.hasNext();) {
			ISugiyamaEdge edge = iterator.next();
			if (!layer2.contains(edge.getSource()) && !layer2.contains(edge.getTarget())) {
				iterator.remove();
			}
		}

		sources = vertex2Edges.stream().map(sugiyamaEdge -> sugiyamaEdge.getSource()).collect(Collectors.toList());
		targets = vertex2Edges.stream().map(sugiyamaEdge -> sugiyamaEdge.getTarget()).collect(Collectors.toList());
		sources.removeIf(vertex -> vertex.getID() == vertex2.getID());
		targets.removeIf(vertex -> vertex.getID() == vertex2.getID());

		List<ISugiyamaVertex> neighbors2 = new LinkedList<>(sources);
		neighbors2.addAll(targets);

		int result = 0;

		for (ISugiyamaVertex x1 : neighbors1) {
			for (ISugiyamaVertex x2 : neighbors2) {
				//System.out.println("   " + x1.getName() + " ? " + x2.getName());
				if (layer2.indexOf(x1) > layer2.indexOf(x2)) {
					//System.out.println("  hit");
					result++;
				}
			}
		}

		return result;
	}
}
