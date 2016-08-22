package edu.kit.student.tests;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.ViewableVertex;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.joana.JoanaCollapsedVertex;
import edu.kit.student.joana.JoanaGraphModel;
import edu.kit.student.joana.JoanaGraphModelBuilder;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.JoanaVertex.VertexKind;
import edu.kit.student.joana.JoanaWorkspace;
import edu.kit.student.joana.callgraph.CallGraph;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.plugin.Importer;
import edu.kit.student.plugin.PluginManager;

public class JoanaGraphTester {
    
    private static List<JoanaGraphModel> models = new LinkedList<>();
    private static Iterator<File> lastOpenedFile;
    private static Random randomGenerator;
    private static List<File> files;
    private static final int RUNS = 20;
    private static final int MAX_V = 500;
    private static final int MAX_E = 2000;

    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        generateGraphModel();
        long time = System.nanoTime();
        System.out.println("Seed: " + time);
        randomGenerator = new Random(time);
    }
    
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void callGraphSizeTest() {
        for (JoanaGraphModel model : models) {
            int callGraphVertices = model.getCallGraph().getVertexSet().size();
            Assert.assertEquals(callGraphVertices, model.getMethodGraphs().size());
        }
    }

    @Test
    public void collapseTest() {
        JoanaGraphModel model = models.get( randomGenerator.nextInt(models.size()));
        model.getMethodGraphs().sort((x, y) -> x.getID().compareTo(y.getID()));
        MethodGraph testGraph = model.getMethodGraphs().get(randomGenerator.nextInt(model.getMethodGraphs().size()));
        Map<Integer, List<Integer>> adjList = createAdjacenceList(testGraph);
        JoanaCollapsedVertex jvertex = collapse(testGraph, 
                testGraph.getVertexSet().stream().filter((vertex) 
                -> (vertex.getNodeKind() == VertexKind.ACTI)).collect(Collectors.toSet()));
        testGraph.expand(jvertex);
        Assert.assertTrue(compareAdjList(adjList, createAdjacenceList(testGraph)));
    }
    
    private JoanaCollapsedVertex collapse(MethodGraph graph, Set<ViewableVertex> subgraph) {
        return graph.collapse(subgraph);
    }
    
    //First collapse a random number of time, then expand, then check for equality
    @Test
    public void randomSymmetricCollapseTest() {
        List<MethodGraph> mGraphs = getAllMethodGraphs(MAX_V, MAX_E);
        mGraphs.sort((x, y) -> x.getID().compareTo(y.getID()));
        for (int run = 0; run < RUNS; run++) {
            mGraphs.sort((x, y) -> x.getID().compareTo(y.getID()));
            MethodGraph randomTestGraph = mGraphs.get(randomGenerator.nextInt(mGraphs.size()));
            
            Map<Integer, List<Integer>> adjList = createAdjacenceList(randomTestGraph);
           // printAdjList(adjList);
            List<JoanaCollapsedVertex> collapsed = new LinkedList<>();
            int maxCollapse = randomGenerator.nextInt(randomTestGraph.getVertexSet().size() / 3);
            for (int i = 0; i < maxCollapse && randomTestGraph.getVertexSet().size() > 1; i++) {
                int vertexCount = randomGenerator.nextInt(randomTestGraph.getVertexSet().size() - 1);
                Set<JoanaVertex> toCollapse = randomSample(randomTestGraph.getVertexSet(), vertexCount);
                Set<ViewableVertex> subset = toCollapse.stream().collect(Collectors.toSet());
                collapsed.add(collapse(randomTestGraph, subset));

            }
            Collections.reverse(collapsed);
            for (JoanaCollapsedVertex cVertex : collapsed) {
                randomTestGraph.expand(cVertex);
                //printAdjList(createAdjacenceList(randomTestGraph));
            }
            
            //printAdjList(createAdjacenceList(randomTestGraph));
            Assert.assertTrue(compareAdjList(adjList, createAdjacenceList(randomTestGraph)));
        }
    }

    @Test
    public void randomMixedCollapseTest() {
        List<MethodGraph> mGraphs = getAllMethodGraphs(MAX_V, MAX_E);
        mGraphs.sort((x, y) -> x.getID().compareTo(y.getID()));
        for (int run = 0; run < RUNS; run++) {
            MethodGraph randomTestGraph = mGraphs.get(randomGenerator.nextInt(mGraphs.size()));

            Map<Integer, List<Integer>> adjList = createAdjacenceList(randomTestGraph);
            List<JoanaCollapsedVertex> collapsed = new LinkedList<>();
            int maxCollapse = randomGenerator.nextInt(randomTestGraph.getVertexSet().size() / 3);
            int currentlyCollapsed = 0;
            for (int i = 0; i < maxCollapse && randomTestGraph.getVertexSet().size() > 1;) {
                Double coinToss = randomGenerator.nextDouble() % 1;
                
                // Collapse
                if (coinToss < 0.5) {
                    currentlyCollapsed++;
                    i++;
                    int vertexCount = randomGenerator.nextInt(randomTestGraph.getVertexSet().size() - 1);
                    Set<JoanaVertex> toCollapse = randomSample(randomTestGraph.getVertexSet(), vertexCount);
                    Set<ViewableVertex> subset = toCollapse.stream().collect(Collectors.toSet());
                    collapsed.add(collapse(randomTestGraph, subset));
                } else {
                    if (currentlyCollapsed <= 0)
                        continue;
                    Collections.shuffle(collapsed);
                    JoanaCollapsedVertex cVertex = collapsed.get(0);
                    if (!randomTestGraph.getVertexSet().contains(cVertex))
                        continue;
                    randomTestGraph.expand(cVertex);
                    collapsed.remove(0);
                    currentlyCollapsed--;
                    //printAdjList(createAdjacenceList(randomTestGraph));
                }
            }
            while (currentlyCollapsed > 0) {
                Collections.shuffle(collapsed);
                JoanaCollapsedVertex cVertex = collapsed.get(0);
                if (!randomTestGraph.getVertexSet().contains(cVertex))
                    continue;
                randomTestGraph.expand(cVertex);
                collapsed.remove(0);
                //printAdjList(createAdjacenceList(randomTestGraph));
                currentlyCollapsed--;
            }
            //printAdjList(createAdjacenceList(randomTestGraph));
            Assert.assertTrue(compareAdjList(adjList, createAdjacenceList(randomTestGraph)));
        }
    }
    
    @Test
    public void randomAssymmetricCollapseTest() {
        List<MethodGraph> mGraphs = getAllMethodGraphs(MAX_V, MAX_E);
        mGraphs.sort((x, y) -> x.getID().compareTo(y.getID()));
        for (int run = 0; run < RUNS; run++) {
            MethodGraph randomTestGraph = mGraphs.get(randomGenerator.nextInt(mGraphs.size()));

            //System.out.println(randomTestGraph.name);
            Map<Integer, List<Integer>> adjList = createAdjacenceList(randomTestGraph);
           // printAdjList(adjList);
            List<JoanaCollapsedVertex> collapsed = new LinkedList<>();
            int maxCollapse = randomGenerator.nextInt(randomTestGraph.getVertexSet().size() / 3);
            for (int i = 0; i < maxCollapse && randomTestGraph.getVertexSet().size() > 1; i++) {
                int vertexCount = randomGenerator.nextInt(randomTestGraph.getVertexSet().size() - 1);
                Set<JoanaVertex> toCollapse = randomSample(randomTestGraph.getVertexSet(), vertexCount);
                Set<ViewableVertex> subset = toCollapse.stream().collect(Collectors.toSet());
                collapsed.add(collapse(randomTestGraph, subset));

            }
            while (!collapsed.isEmpty()) {
                Collections.shuffle(collapsed);
                JoanaCollapsedVertex cVertex = collapsed.get(0);
                if (!randomTestGraph.getVertexSet().contains(cVertex))
                    continue;
                randomTestGraph.expand(cVertex);
                collapsed.remove(0);
                //printAdjList(createAdjacenceList(randomTestGraph));
            }
            
            //printAdjList(createAdjacenceList(randomTestGraph));
            Assert.assertTrue(compareAdjList(adjList, createAdjacenceList(randomTestGraph)));
        }
    }
    
    private Map<Integer, List<Integer>> createAdjacenceList(DirectedGraph graph) {
        Map<Integer, List<Integer>> adjacenceList = new HashMap<>();
        graph.getVertexSet().forEach((vertex) 
                -> adjacenceList.put(vertex.getID(), graph.outgoingEdgesOf(vertex).stream().map((edge) 
                        -> edge.getTarget().getID()).collect(Collectors.toList())));
        return adjacenceList;
    }
    
    private void printAdjList(Map<Integer, List<Integer>> adjList) {
        List<Integer> keys = adjList.keySet().stream().collect(Collectors.toList());
        keys.sort((x, y) -> x.compareTo(y));
        for (Integer key : keys ) {
            System.out.print(key + " -> ");
            for (Integer target : adjList.get(key)) {
                System.out.print(target + ", ");
            }
            System.out.println("");
        }
    }
    
    private void printAdjList2(Map<Integer, List<Integer>> adjListA, Map<Integer, List<Integer>> adjListB) {
        List<Integer> keysA = adjListA.keySet().stream().collect(Collectors.toList());
        List<Integer> keysB = adjListB.keySet().stream().collect(Collectors.toList());
        keysA.sort((x, y) -> x.compareTo(y));
        keysB.sort((x, y) -> x.compareTo(y));
        for (Integer key : keysA ) {
            List<Integer> listA = adjListA.get(key);
            List<Integer> listB = adjListB.get(key);
            listA.sort((x, y) -> x.compareTo(y));
            listB.sort((x, y) -> x.compareTo(y));
            System.out.print(key + " -> ");
            for (Integer target : listA) {
                System.out.print(target + ", ");
            }
            System.out.println("");
            System.out.print(key + " -> ");
            for (Integer target : listB) {
                System.out.print(target + ", ");
            }
            System.out.println("");
        }
    }
    private boolean compareAdjList(Map<Integer, List<Integer>> adjListA, Map<Integer, List<Integer>> adjListB) {
        Assert.assertTrue("Vertex set has different sizes! (A :" + adjListA.keySet().size() + ", B:" + adjListB.keySet().size() + ")",
                adjListA.keySet().size() == adjListB.keySet().size());
        for (Integer source : adjListA.keySet()) {
            Assert.assertTrue("Vertex set B does not contain vertex containd in vertex set A", adjListB.keySet().contains(source));
            Assert.assertTrue("Vertex " + source + " in set A has a different size (" 
                                + adjListA.get(source).size() + ") of edges than vertex in set B (" + adjListB.get(source).size() + ")",
                    (adjListA.get(source).size() == adjListB.get(source).size()));
            for (Integer target : adjListA.get(source)) {
                Assert.assertTrue("Vertex in set A has edge not contained in set B", adjListB.get(source).contains(target));
            }
        }
        return true;
    }
    
    public static <T> Set<T> randomSample(Set<T> items, int m){
        List<T> list = items.stream().collect(Collectors.toList());
        Collections.shuffle(list);
        return list.subList(0, m).stream().collect(Collectors.toSet());
    }
    
    /**
     * Returns all methodgraphs with less than maxV vertices and less then maxE edges.
     * @param maxV the maximum number of vertices
     * @param maxE the maximum number of edges
     * @return the methodgraphs
     */
    public static List<MethodGraph> getAllMethodGraphs(int maxV, int maxE) {
        while (generateGraphModel() != null)  {}
        System.out.println("Finished");
        return models.stream().map(model -> model.getMethodGraphs())
                              .flatMap(mgraphs -> mgraphs.stream())
                              .filter(mgraph -> mgraph.getVertexSet().size() < maxV)
                              .filter(mgraph -> mgraph.getEdgeSet().size() < maxE)
                              .collect(Collectors.toList());
    }
    
    /**
     * Returns all call graphs with less than maxV vertices and less then maxE edges.
     * @param maxV the maximum number of vertices
     * @param maxE the maximum number of edges
     * @return the callgraphs
     */
    public static List<CallGraph> getAllCallGraphs(int maxV, int maxE) {
        while (generateGraphModel() != null)  {}
        return models.stream().map(model -> model.getCallGraph())
                              .filter(cgraph -> cgraph.getVertexSet().size() < maxV)
                              .filter(cgraph -> cgraph.getEdgeSet().size() < maxE)
                              .collect(Collectors.toList());
    }
    
    public static JoanaGraphModel generateGraphModel() {
        if (lastOpenedFile == null) {
            lastOpenedFile = getDirIterator();
        }
        boolean parsed = false;;
        while (lastOpenedFile.hasNext() && !parsed) {
            File file = lastOpenedFile.next();
            if (file.getName().equals("BigCG.pdg.graphml")) {
                continue;
            }
            ;
            return parse(file);
        }
        return null;
    }
    
    public static JoanaGraphModel parse(File file) {

        JoanaWorkspace ws = new JoanaWorkspace();
        for (Importer importer : PluginManager.getPluginManager().getImporter()) {
            if (importer.getSupportedFileEndings().equals("*.graphml")) {
                try {
                    System.out.println("Parsing: " + file.getName());
                    importer.importGraph(new JoanaGraphModelBuilder(ws), new FileInputStream(file));
                } catch (FileNotFoundException | ParseException e) {
                    e.printStackTrace();
                    return null;
                }
                break;
            }
        }
        models.add(ws.getGraphModel());
        return ws.getGraphModel();
    }
    
    public static Iterator<File> getDirIterator() {
        File resources = new File("plugins/joana/src/test/resources");
        files = Arrays.asList(resources.listFiles()).stream()
                                                    .filter((file)-> file.getName().endsWith(".graphml"))
                                                    .collect(Collectors.toList());
        if (files == null) {
            files = new LinkedList<>();
        }
        return files.iterator();

    }
}