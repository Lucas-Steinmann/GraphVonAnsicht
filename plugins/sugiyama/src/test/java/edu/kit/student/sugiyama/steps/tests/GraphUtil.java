package edu.kit.student.sugiyama.steps.tests;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedEdge;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Sven on 06.07.2016.
 */
public class GraphUtil {

    public static DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>> generateGraph(int vertexCount, float density, boolean isCyclic) {
        DefaultDirectedGraph graph = new DefaultDirectedGraph("randomGraph");
        density = Math.min(Math.max(density, 0f), 1f);
        int edgeCount = (int) (density * vertexCount * (vertexCount - 1));
        List<Vertex> vertices = new LinkedList<>();
        List<DirectedEdge> edges = new LinkedList<>();
        Random random = new Random();

        if (!isCyclic) {
            edgeCount /= 2;
        }

        for (int i = 0; i < vertexCount; i++) {
            DefaultVertex vertex = new DefaultVertex("v" + Integer.toString(i), "");
            vertices.add(vertex);
            graph.addVertex(vertex);

            for (int j = 0; j < i; j++) {
                if (isCyclic) {
                    DirectedEdge edge = new DefaultDirectedEdge("e(v" + Integer.toString(i) + ", v" + Integer.toString(j) + ")", "");
                    edge.setVertices(vertex, vertices.get(j));
                    edges.add(edge);
                    edge = new DefaultDirectedEdge("e(v" + Integer.toString(j) + ", v" + Integer.toString(i) + ")", "");
                    edge.setVertices(vertices.get(j), vertex);
                    edges.add(edge);
                } else {
                    DirectedEdge edge = new DefaultDirectedEdge("e(v" + Integer.toString(j) + ", v" + Integer.toString(i) + ")", "");
                    edge.setVertices(vertices.get(j), vertex);
                    edges.add(edge);
                }
            }
        }

        while (edgeCount < edges.size()) {
            int removeNumber = random.nextInt(edges.size());
            edges.remove(removeNumber);
        }

        for (DirectedEdge edge : edges) {
            graph.addEdge(edge);
        }

        return graph;
    }

    public static SugiyamaGraph generateSugiyamaGraph(int vertexCount, float density, boolean isCyclic, boolean isLayered) {
        return generateSugiyamaGraph(vertexCount, density, isCyclic, isLayered, (new Random()).nextLong());
    }

    public static SugiyamaGraph generateSugiyamaGraph(int vertexCount, float density, boolean isCyclic, boolean isLayered, long seed) {
        if (!isLayered) {
            return new SugiyamaGraph(generateGraph(vertexCount, density, isCyclic));
        }

        DefaultDirectedGraph graph = new DefaultDirectedGraph("randomGraph");
        density = Math.min(Math.max(density, 0f), 1f);
        int edgeCount = 0;
        int maxEdgeCount = 0;
        List<Integer> layerSizes = new LinkedList<>();
        int currentLayerStart = 0;
        int lastLayerStart = 0;
        List<Vertex> vertices = new LinkedList<>();
        List<DirectedEdge> edges = new LinkedList<>();
        Random random = new Random(seed);

        while (currentLayerStart < vertexCount) {
            int layerSize = Math.min(random.nextInt(vertexCount / 3) + 1, vertexCount - currentLayerStart);
            layerSizes.add(layerSize);
            currentLayerStart += layerSize;
        }

        currentLayerStart = 0;
        int currentLayer = 0;

        for (Integer layersize : layerSizes) {
            for (int i = currentLayerStart; i < currentLayerStart + layersize; i++) {
                DefaultVertex vertex = new DefaultVertex("v" + Integer.toString(i), Integer.toString(currentLayer));
                vertices.add(vertex);
                graph.addVertex(vertex);

                for (int j = lastLayerStart; j < currentLayerStart; j++) {
                    if (isCyclic) {
                        DirectedEdge edge = new DefaultDirectedEdge("e(v" + Integer.toString(i) + ", v" + Integer.toString(j) + ")", "");
                        edge.setVertices(vertex, vertices.get(j));
                        edges.add(edge);
                        edge = new DefaultDirectedEdge("e(v" + Integer.toString(j) + ", v" + Integer.toString(i) + ")", "");
                        edge.setVertices(vertices.get(j), vertex);
                        edges.add(edge);
                        maxEdgeCount += 2;
                    } else {
                        DirectedEdge edge = new DefaultDirectedEdge("e(v" + Integer.toString(j) + ", v" + Integer.toString(i) + ")", "");
                        edge.setVertices(vertices.get(j), vertex);
                        edges.add(edge);
                        maxEdgeCount += 1;
                    }
                }
            }

            lastLayerStart = currentLayerStart;
            currentLayerStart += layersize;
            currentLayer += 1;
        }

        edgeCount = (int) (density * maxEdgeCount);

        while (edgeCount < edges.size()) {
            int removeNumber = random.nextInt(edges.size());
            edges.remove(removeNumber);
        }

        for (DirectedEdge edge : edges) {
            graph.addEdge(edge);
        }

        SugiyamaGraph sugiyamaGraph = new SugiyamaGraph(graph);

        for (SugiyamaGraph.SugiyamaVertex vertex :sugiyamaGraph.getVertexSet()) {
            sugiyamaGraph.assignToLayer(vertex, Integer.parseInt(vertex.getLabel()));
        }

        return sugiyamaGraph;
    }
}
