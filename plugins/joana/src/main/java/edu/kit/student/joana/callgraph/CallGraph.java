package edu.kit.student.joana.callgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.kit.student.graphmodel.CompoundVertex;
import edu.kit.student.graphmodel.SubGraph;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.ViewableVertex;
import edu.kit.student.graphmodel.action.SubGraphAction;
import edu.kit.student.graphmodel.action.VertexAction;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.joana.CallGraphVertex;
import edu.kit.student.joana.JoanaCollapsedVertex;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraph;
import edu.kit.student.joana.JoanaPlugin;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.graphmodel.DirectedOnionPath;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;

/**
 * This is a specified graph representation for the Callgraph in Joana.
 */
public class CallGraph extends JoanaGraph {

    private List<MethodGraph> methodGraphs = new LinkedList<>();
    private DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;

    private List<JoanaCollapsedVertex> collapsedVertices;
    private Map<JoanaCollapsedVertex, VertexAction> expandActions;
    private Map<JoanaEdge, DirectedOnionPath<JoanaEdge, JoanaCollapsedVertex>> onionEdges;
    
    public CallGraph(String name, Set<CallGraphVertex> vertices, Set<JoanaEdge> edges) {
        super(name, vertices.stream().collect(Collectors.toSet()), edges);
        this.graph = new DefaultDirectedGraph<>(vertices.stream().collect(Collectors.toSet()), edges);
        for (CallGraphVertex vertex : vertices) {
            methodGraphs.add(vertex.getGraph());
        }
        this.collapsedVertices = new LinkedList<>();
        this.onionEdges = new HashMap<>();
        this.expandActions = new HashMap<>();

    }

    public CallGraph(String name) {
        this(name, new HashSet<>(), new HashSet<>());
    }

    public List<MethodGraph> getMethodgraphs() {
    	return methodGraphs;
    }
   @Override
    public Integer outdegreeOf(Vertex vertex) {
        return removeFilteredEdges(graph.outgoingEdgesOf(vertex)).size();
    }

    @Override
    public Integer indegreeOf(Vertex vertex) {
        return removeFilteredEdges(graph.incomingEdgesOf(vertex)).size();
    }

    @Override
    public Integer selfLoopNumberOf(Vertex vertex) {
        return graph.selfLoopNumberOf(vertex);
    }

    @Override
    public Set<JoanaEdge> outgoingEdgesOf(Vertex vertex) {
        return removeFilteredEdges(graph.outgoingEdgesOf(vertex));
    }

    @Override
    public Set<JoanaEdge> incomingEdgesOf(Vertex vertex) {
        return removeFilteredEdges(graph.incomingEdgesOf(vertex));
    }

    @Override
    public Set<JoanaEdge> selfLoopsOf(Vertex vertex) {
        return graph.selfLoopsOf(vertex);
    }

    @Override
    public Set<JoanaVertex> getVertexSet() {
        return removeFilteredVertices(graph.getVertexSet());
    }

    @Override
    public Set<JoanaEdge> getEdgeSet() {
        return removeFilteredEdges(graph.getEdgeSet());
    }

    @Override
    public Set<JoanaEdge> edgesOf(Vertex vertex) {
        return removeFilteredEdges(graph.edgesOf(vertex));
    }

    @Override
    public List<VertexAction> getVertexActions(Vertex vertex) {
        List<VertexAction> actions = new LinkedList<>();
        if (this.collapsedVertices.contains(vertex) && this.getVertexSet().contains(vertex)) {
            actions.add(expandActions.get(vertex));
        }
        return actions;
    }

    @Override
    public List<SubGraphAction> getSubGraphActions(Set<ViewableVertex> vertices) {
        List<SubGraphAction> actions = new LinkedList<>();
        if (getVertexSet().containsAll(vertices) && 
        		vertices.size() > 1) {
            actions.add(newCollapseAction(vertices));
        }

        return actions;
    }

    @Override
    public Set<? extends SubGraph> getSubGraphs() {
        return new HashSet<>();
    }

	private VertexAction newExpandAction(CompoundVertex vertex) {
	    return new VertexAction("Expand", 
	            "Adds all vertices contained in this Summary-Vertex to the graph and removes the Summary-Vertex.") {
            
            @Override
            public void handle() {
                expand(vertex);
            }
        };
	}
	
	private SubGraphAction newCollapseAction(Set<ViewableVertex> vertices) {
	    return new SubGraphAction("Collapse", "Collapses all vertices into one Summary-Vertex.") {
            
            @Override
            public void handle() {
                collapse(vertices);
            }
        };
	}
	public JoanaCollapsedVertex collapse(Set<ViewableVertex> subset) {
	    Set<JoanaVertex> directedSubset = new HashSet<JoanaVertex>();
	    for (Vertex v : subset) {
	        if (!graph.contains(v)) {
                throw new IllegalArgumentException("Cannot collapse vertices, not contained in this graph.");
	        } else {
	            directedSubset.add(graph.getVertexById(v.getID()));
	        }
	    }

		// Construct collapsed vertex
		DefaultDirectedGraph<JoanaVertex, JoanaEdge> collapsedGraph = new DefaultDirectedGraph<>(directedSubset, new HashSet<JoanaEdge>());
		JoanaCollapsedVertex collapsed = new JoanaCollapsedVertex("Collapsed", "Collapsed (" + collapsedGraph.getVertexSet().size() + ")",
		        collapsedGraph, new HashMap<>());
		graph.addVertex(collapsed); 
		collapsedVertices.add(collapsed);

		for (JoanaEdge edge : getEdgeSet()) {
			boolean containsSource = subset.contains(edge.getSource());
			boolean containsTarget = subset.contains(edge.getTarget());

			if (containsSource && containsTarget) {
                graph.removeEdge(edge);
				collapsedGraph.addEdge(edge);
			} else if (containsSource && !containsTarget) {

                graph.removeEdge(edge);
                JoanaEdge newEdge = new JoanaEdge(edge.getName(), edge.getLabel(), collapsed, edge.getTarget(), edge.getEdgeKind());
                graph.addEdge(newEdge);
                
                if (onionEdges.keySet().contains(edge)) {
                    DirectedOnionPath<JoanaEdge, JoanaCollapsedVertex> onionEdge = onionEdges.get(edge);
                    assert(collapsed.getGraph().getVertexSet().contains(edge.getSource()));
                    onionEdge.addAsSource(collapsed);
                    onionEdges.remove(edge);
                    onionEdges.put(newEdge, onionEdge);
                }  else {
                    onionEdges.put(newEdge, new DirectedOnionPath<>(edge));
                    onionEdges.get(newEdge).addAsSource(collapsed);
                }

			} else if (!containsSource && containsTarget) {

                graph.removeEdge(edge);
                JoanaEdge newEdge = new JoanaEdge(edge.getName(), edge.getLabel(), edge.getSource(), collapsed, edge.getEdgeKind());
                graph.addEdge(newEdge);

                if (onionEdges.keySet().contains(edge)) {
                    DirectedOnionPath<JoanaEdge, JoanaCollapsedVertex> onionEdge = onionEdges.get(edge);
                    assert(collapsed.getGraph().getVertexSet().contains(edge.getTarget()));
                    onionEdge.addAsTarget(collapsed);
                    onionEdges.remove(edge);
                    onionEdges.put(newEdge, onionEdge);
                }  else {
                    onionEdges.put(newEdge, new DirectedOnionPath<>(edge));
                    onionEdges.get(newEdge).addAsTarget(collapsed);
                }
			}
		}

		graph.removeAllVertices(directedSubset);
		expandActions.put(collapsed, newExpandAction(collapsed));
		return collapsed;
    }
	
    private Set<JoanaVertex> expand(JoanaCollapsedVertex vertex) {
        
        if (!graph.contains(vertex)) {
	        throw new IllegalArgumentException("Cannot expand vertex, not in this graph.");
        }

		Set<JoanaVertex> containedVertices = new HashSet<>(vertex.getGraph().getVertexSet());
		graph.addAllVertices(containedVertices);
		graph.addAllEdges(vertex.getGraph().getEdgeSet());


		for (JoanaEdge edge : edgesOf(vertex)) {
		    DirectedOnionPath<JoanaEdge, JoanaCollapsedVertex> onionEdge = this.onionEdges.get(edge);
		    onionEdge.removeNode(vertex);
		    JoanaVertex newSource = onionEdge.getSource() == null ? onionEdge.getEdge().getSource() : onionEdge.getSource();
		    JoanaVertex newTarget = onionEdge.getTarget() == null ? onionEdge.getEdge().getTarget() : onionEdge.getTarget();
		    JoanaEdge newEdge = new JoanaEdge(onionEdge.getName(), onionEdge.getLabel(), newSource, newTarget, onionEdge.getEdge().getEdgeKind());
		    onionEdges.remove(edge);
		    onionEdges.put(newEdge, onionEdge);
		    graph.removeEdge(edge);
		    graph.addEdge(newEdge);
		}
		graph.removeVertex(vertex);
		collapsedVertices.remove(vertex);
		
		return containedVertices;
    }

    public Set<JoanaVertex> expand(CompoundVertex vertex) {
	    if (!collapsedVertices.contains(vertex)) {
	        throw new IllegalArgumentException("Cannot expand vertex, not collapsed in this graph.");
	    }
        for (JoanaCollapsedVertex jvertex : collapsedVertices) {
            if (vertex.getID().equals(jvertex.getID())) {
                return this.expand(jvertex);
            }
        }
        assert (false);
        return null;
	}

    @Override
    public List<LayoutOption> getRegisteredLayouts() {

        // Retrieve callgraphLayouts from register
        List<CallGraphLayoutOption> callGraphLayouts = new LinkedList<>();
        if (JoanaPlugin.getCallGraphLayoutRegister().getLayoutOptions() != null) {
            callGraphLayouts.addAll(JoanaPlugin.getCallGraphLayoutRegister().getLayoutOptions());
        }
        for (CallGraphLayoutOption option : callGraphLayouts) {
            option.setGraph(this);
        }

        // Add default directed graph layouts;
        List<LayoutOption> layoutOptions = new LinkedList<>(callGraphLayouts);
        layoutOptions.addAll(super.getRegisteredLayouts());

        return layoutOptions;
    }
    
    @Override
	public LayoutOption getDefaultLayout() {
		return new CallGraphLayoutOption() {
			{
                this.setName("Call-Graph-Layout");
                this.setId("CGL");
                this.setGraph(CallGraph.this);
            }
            
            @Override
            public void chooseLayout() {
                this.setLayout(new CallGraphLayout());
            }
		};
	}

    @Override
    public List<GAnsProperty<?>> getStatistics() {
    	List<GAnsProperty<?>> statistics = super.getStatistics();
    	return statistics;
    }
}
