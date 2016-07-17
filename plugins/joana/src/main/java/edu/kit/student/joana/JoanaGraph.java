package edu.kit.student.joana;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.student.graphmodel.CollapsedVertex;
import edu.kit.student.graphmodel.DefaultGraphLayering;
import edu.kit.student.graphmodel.DirectedOnionPath;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.ViewableGraph;
import edu.kit.student.graphmodel.action.SubGraphAction;
import edu.kit.student.graphmodel.action.VertexAction;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.util.IdGenerator;

/**
 * An abstract superclass for all JOANA specific graphs.
 */
public abstract class JoanaGraph
    implements DirectedGraph, LayeredGraph, ViewableGraph {
    
	
    @Override
    public List<SubGraphAction> getSubGraphActions(Set<Vertex> vertices) {
        List<SubGraphAction> actions = new LinkedList<>();
        if (getVertexSet().containsAll(vertices) && 
        		vertices.size() > 1) {
            actions.add(newCollapseAction(vertices));
        }

        return actions;
    }

    @Override
    public List<VertexAction> getVertexActions(Vertex vertex) {
        List<VertexAction> actions = new LinkedList<>();
        if (this.collapsedVertices.contains(vertex) && this.getVertexSet().contains(vertex)) {
            actions.add(expandActions.get(vertex));
        }
        return actions;
    }

    private ViewableGraph parent;
    private List<ViewableGraph> children = new LinkedList<>();
    private Integer id;
    private GAnsProperty<String> name;

    private DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;
    private DefaultGraphLayering<JoanaVertex> layering;

    private List<JoanaCollapsedVertex> collapsedVertices;
    private Map<JoanaCollapsedVertex, VertexAction> expandActions;
    private Map<JoanaEdge, DirectedOnionPath<JoanaEdge, JoanaCollapsedVertex>> onionEdges;
    
    private GAnsProperty<Integer> edgeCount;
    private GAnsProperty<Integer> vertexCount;
    

    public JoanaGraph(String name, Set<JoanaVertex> vertices, Set<JoanaEdge> edges) {
        this.name = new GAnsProperty<String>("Name", name);
        this.id = IdGenerator.getInstance().createId();    
        this.graph = new DefaultDirectedGraph<>(vertices, edges);
        this.layering = new DefaultGraphLayering<>(vertices);
        this.collapsedVertices = new LinkedList<>();
        this.onionEdges = new HashMap<>();
        this.expandActions = new HashMap<>();
        this.edgeCount = new GAnsProperty<Integer>("Edge count", edges.size());
        this.vertexCount = new GAnsProperty<Integer>("Vertex count", vertices.size());
    }

    @Override
    public String getName() {
        return name.getValue();
    }

    @Override
    public Integer getID() {
        return this.id;
    }

    @Override
    public ViewableGraph getParentGraph() {
        return this.parent;
    }

    @Override
    public void setParentGraph(ViewableGraph parent) {
        this.parent = parent;
        
    }

    @Override
    public List<ViewableGraph> getChildGraphs() {
        return this.children;
    }

    @Override
    public void addChildGraph(ViewableGraph child) {
        this.children.add(child);
    }
    
    @Override
    public List<GAnsProperty<?>> getStatistics() {
    	List<GAnsProperty<?>> statistics = new LinkedList<GAnsProperty<?>>();
    	statistics.add(name);
    	statistics.add(this.vertexCount);
    	statistics.add(this.edgeCount);
    	return statistics;
    }

    public List<LayoutOption> getRegisteredLayouts() {
        return graph.getRegisteredLayouts();
    }

	@Override
	public JoanaCollapsedVertex collapse(Set<Vertex> subset) {
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
		    if (onionEdge.getSource() == vertex || onionEdge.getTarget() == vertex) {
		        System.out.println(onionEdge);
                printHierarchy();
		    }
		    onionEdges.remove(edge);
		    onionEdges.put(newEdge, onionEdge);
		    graph.removeEdge(edge);
		    graph.addEdge(newEdge);
		}
		graph.removeVertex(vertex);
		collapsedVertices.remove(vertex);
		
		return containedVertices;
    }
    private void printHierarchy() {
        for (JoanaCollapsedVertex v : this.collapsedVertices) {
            if (this.graph.contains(v)) {
                printChilds(v, 0);
            }
        }
    }
    
    private void printOnionEdges() {
        for (JoanaEdge edge : onionEdges.keySet()) {
            System.out.println(onionEdges.get(edge));
        }
    }
    
    private void printChilds(JoanaCollapsedVertex vertex, int indent) {
        for (int i = 0; i < indent; i++)
            System.out.print(" ");
        System.out.println(vertex.getID());
        for (JoanaVertex v : vertex.getGraph().getVertexSet()) {
            if (this.collapsedVertices.contains(v)) {
                printChilds((JoanaCollapsedVertex) v, indent+2);
            }
        }
    }


	private JoanaCollapsedVertex getCollapsedVertexByID(int id)  {
	    for (JoanaCollapsedVertex cVertex : collapsedVertices) {
	        if (id == cVertex.getID()) {
	            return cVertex;
	        }
	    }
	    return null;
	}

	private VertexAction newExpandAction(CollapsedVertex vertex) {
	    return new VertexAction("Expand", 
	            "Adds all vertices contained in this Summary-Vertex to the graph and removes the Summary-Vertex.") {
            
            @Override
            public void handle() {
                expand(vertex);
            }
        };
	}
	
	private SubGraphAction newCollapseAction(Set<Vertex> vertices) {
	    return new SubGraphAction("Collapse", "Collapses all vertices into one Summary-Vertex.") {
            
            @Override
            public void handle() {
                collapse(vertices);
            }
        };
	}

	@Override
    public Set<JoanaVertex> expand(CollapsedVertex vertex) {
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
	public boolean isCollapsed(Vertex vertex) {
	    if (!collapsedVertices.contains(vertex))
	        return false;
	    if (!getVertexSet().contains(vertex)) {
	        return false;
	    }
	    return true;
	}

    @Override
    public Integer outdegreeOf(Vertex vertex) {
        return graph.outdegreeOf(vertex);
    }

    @Override
    public Integer indegreeOf(Vertex vertex) {
        return graph.indegreeOf(vertex);
    }

    @Override
    public Set<JoanaEdge> outgoingEdgesOf(Vertex vertex) {
        return graph.outgoingEdgesOf(vertex);
    }

    @Override
    public Set<JoanaEdge> incomingEdgesOf(Vertex vertex) {
        return graph.incomingEdgesOf(vertex);
    }

    @Override
    public Set<JoanaVertex> getVertexSet() {
        return graph.getVertexSet();
    }

    @Override
    public Set<JoanaEdge> getEdgeSet() {
        return graph.getEdgeSet();
    }

    @Override
    public Set<JoanaEdge> edgesOf(Vertex vertex) {
        return graph.edgesOf(vertex);
    }

    @Override
    public int getLayerCount() {
        return this.layering.getLayerCount();
    }

    @Override
    public int getVertexCount(int layerNum) {
        return layering.getVertexCount(layerNum);
    }

    @Override
    public int getLayerFromVertex(Vertex vertex) {
        return layering.getLayerFromVertex(vertex);
    }

    @Override
    public List<? extends Vertex> getLayer(int layerNum) {
        return layering.getLayer(layerNum);
    }

    @Override
    public List<List<JoanaVertex>> getLayers() {
        return layering.getLayers();
    }

    @Override
    public int getHeight() {
        return layering.getHeight();
    }

    @Override
    public int getLayerWidth(int layerN) {
        return layering.getLayerWidth(layerN);
    }

    @Override
    public int getMaxWidth() {
        return layering.getMaxWidth();
    }

    @Override
    public FastGraphAccessor getFastGraphAccessor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addToFastGraphAccessor(FastGraphAccessor fga) {
        // TODO Auto-generated method stub
        
    }
}
