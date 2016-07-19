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
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.joana.JoanaCollapsedVertex;
import edu.kit.student.joana.JoanaGraphModel;
import edu.kit.student.joana.JoanaGraphModelBuilder;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.JoanaVertex.Kind;
import edu.kit.student.joana.JoanaWorkspace;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.plugin.Importer;
import edu.kit.student.plugin.PluginManager;

public class JoanaGraphTester {
    
    static final int PARSECOUNT = 1;
    static List<JoanaGraphModel> models = new LinkedList<>();
    static Random randomGenerator;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        File resources = new File("plugins/joana/src/test/resources");
        List<File> files = Arrays.asList(resources.listFiles()).stream().filter((file) 
                -> file.getName().endsWith(".graphml")).collect(Collectors.toList());
        if (files == null) {
            return;
        }

        List<JoanaWorkspace> ws = new LinkedList<JoanaWorkspace>(); 
        for (int i = 0; i < files.size() && i < PARSECOUNT; i++) {
            ws.add(new JoanaWorkspace());
        }
        List<JoanaGraphModelBuilder> builders = ws.stream().map((workspace) -> workspace.getGraphModelBuilder()).collect(Collectors.toList());

        for (Importer importer : PluginManager.getPluginManager().getImporter()) {
            if (importer.getSupportedFileEndings().equals("*.graphml")) {
                Iterator<JoanaGraphModelBuilder> itBuilder = builders.iterator();

                int parsed = 0;
                for (File file : files) {
                    try {
                        System.out.println("Parsing: " + file.getName());
                        importer.importGraph(itBuilder.next(), new FileInputStream(file));
                        if (++parsed >= PARSECOUNT)
                            break;
                    } catch (FileNotFoundException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
        models = ws.stream().map((workspace) -> workspace.getGraphModel()).collect(Collectors.toList());

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
                -> (vertex.getNodeKind() == Kind.ACTI)).collect(Collectors.toSet()));
        testGraph.expand(jvertex);
        Assert.assertTrue(compareAdjList(adjList, createAdjacenceList(testGraph)));
    }
    
    private JoanaCollapsedVertex collapse(MethodGraph graph, Set<Vertex> subgraph) {
 //       System.out.println("Collapsing : ");
        List<Vertex> vertexList = subgraph.stream().collect(Collectors.toList());
 //       vertexList.sort((x, y) -> x.getID().compareTo(y.getID()));
 //       vertexList.forEach((vertex) -> System.out.print(vertex.getID() + ", "));
 //       System.out.println("");
        return graph.collapse(subgraph);
    }
    
    //First collapse a random number of time, then expand, then check for equality
    @Test
    public void randomSymmetricCollapseTest() {
        for (int run = 0; run < 20; run++) {
           // System.out.print("Run: " + run + " ");
            JoanaGraphModel model = models.get(randomGenerator.nextInt(models.size()));
            model.getMethodGraphs().sort((x, y) -> x.getID().compareTo(y.getID()));
            MethodGraph randomTestGraph = model.getMethodGraphs().get(randomGenerator.nextInt(model.getMethodGraphs().size()));
            //System.out.println(randomTestGraph.name);
            Map<Integer, List<Integer>> adjList = createAdjacenceList(randomTestGraph);
           // printAdjList(adjList);
            List<JoanaCollapsedVertex> collapsed = new LinkedList<>();
            int maxCollapse = randomGenerator.nextInt(randomTestGraph.getVertexSet().size() / 3);
            for (int i = 0; i < maxCollapse && randomTestGraph.getVertexSet().size() > 1; i++) {
                int vertexCount = randomGenerator.nextInt(randomTestGraph.getVertexSet().size() - 1);
                Set<JoanaVertex> toCollapse = randomSample(randomTestGraph.getVertexSet(), vertexCount);
                Set<Vertex> subset = toCollapse.stream().collect(Collectors.toSet());
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
        for (int run = 0; run < 20; run++) {
           // System.out.print("Run: " + run + " ");
            JoanaGraphModel model = models.get(randomGenerator.nextInt(models.size()));
            model.getMethodGraphs().sort((x, y) -> x.getID().compareTo(y.getID()));
            MethodGraph randomTestGraph = model.getMethodGraphs().get(randomGenerator.nextInt(model.getMethodGraphs().size()));
            //System.out.println(randomTestGraph.name);
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
                    Set<Vertex> subset = toCollapse.stream().collect(Collectors.toSet());
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
        for (int run = 0; run < 20; run++) {
           // System.out.print("Run: " + run + " ");
            JoanaGraphModel model = models.get(randomGenerator.nextInt(models.size()));
            model.getMethodGraphs().sort((x, y) -> x.getID().compareTo(y.getID()));
            MethodGraph randomTestGraph = model.getMethodGraphs().get(randomGenerator.nextInt(model.getMethodGraphs().size()));
            //System.out.println(randomTestGraph.name);
            Map<Integer, List<Integer>> adjList = createAdjacenceList(randomTestGraph);
           // printAdjList(adjList);
            List<JoanaCollapsedVertex> collapsed = new LinkedList<>();
            int maxCollapse = randomGenerator.nextInt(randomTestGraph.getVertexSet().size() / 3);
            for (int i = 0; i < maxCollapse && randomTestGraph.getVertexSet().size() > 1; i++) {
                int vertexCount = randomGenerator.nextInt(randomTestGraph.getVertexSet().size() - 1);
                Set<JoanaVertex> toCollapse = randomSample(randomTestGraph.getVertexSet(), vertexCount);
                Set<Vertex> subset = toCollapse.stream().collect(Collectors.toSet());
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
}