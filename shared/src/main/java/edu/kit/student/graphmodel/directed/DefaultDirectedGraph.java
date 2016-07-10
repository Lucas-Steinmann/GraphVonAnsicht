package edu.kit.student.graphmodel.directed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.kit.student.graphmodel.CollapsedVertex;
import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.Graph;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.ViewableGraph;
import edu.kit.student.graphmodel.serialize.SerializedEdge;
import edu.kit.student.graphmodel.serialize.SerializedGraph;
import edu.kit.student.graphmodel.serialize.SerializedVertex;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.util.IdGenerator;

/**
 * A {@link DefaultDirectedGraph} is a specific Graph which only contains
 * {@link DirectedEdge} as edges.
 */
public class DefaultDirectedGraph<V extends Vertex, E extends DirectedEdge<V>>
		implements DirectedGraph<V, E>, ViewableGraph<V, E> {

	private static DirectedGraphLayoutRegister register = new DirectedGraphLayoutRegister();
	private GAnsProperty<String> name;
	private Integer id;
	private FastGraphAccessor fga;
	private Set<CollapsedVertex<V, E>> collapsedVertices;
	private HashMap<V, Set<E>> vertexToEdge;
	private HashMap<V, Set<E>> revVertexToEdge;
	
	private Graph parent = null;
	private List<Graph<? extends Vertex, ? extends Edge<? extends Vertex>>> children = new ArrayList<>();

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the new graph
	 * @param id
	 *            The id of the new graph
	 */
    public DefaultDirectedGraph(String name) {
        // create Sets
        this(name, new HashSet<>(), new HashSet<>());
    }

    /**
     * Constructor
     * 
     * @param name
     *            of the new graph
     * @param id
     *            of the new graph
     * @param vertices
     *            of the new graph
     * @param edges
     *            of the new graph
     */
    public DefaultDirectedGraph(String name, Set<V> vertices, Set<E> edges) {
        this.name = new GAnsProperty<String>("graphName", name);
        this.id = IdGenerator.getInstance().createId();
        this.fga = new FastGraphAccessor();
        this.children = new ArrayList<Graph<? extends Vertex, ? extends Edge<? extends Vertex>>>();

        this.vertexToEdge = new HashMap<>();
        this.revVertexToEdge = new HashMap<>();
        for (V vertex : vertices) {
            this.vertexToEdge.put(vertex, new HashSet<>());
            this.revVertexToEdge.put(vertex, new HashSet<>());
        }
        for (E edge : edges) {
            this.addAllEdges(edges);
        }
    }

	@Override
	public String getName() {
		return name.getValue();
	}

	@Override
	public Integer getID() {
		return id;
	}

	/**
	 * Adds an edge to the edgeSet
	 * 
	 * @param edge
	 */
	public void addEdge(E edge) {
	    if (this.getVertexSet().contains(edge.getSource()) && this.getVertexSet().contains(edge.getTarget())) {
	        vertexToEdge.get(edge.getSource()).add(edge);
	        revVertexToEdge.get(edge.getTarget()).add(edge);
	    } else {
	        throw new IllegalArgumentException("Cannot add edge to a Graph without the Vertex being present");
	    }
	}

	/**
	 * Adds an vertex to the vertexSet
	 * 
	 * @param vertex
	 */
	public void addVertex(V vertex) {
		this.vertexToEdge.put(vertex, new HashSet<>());
		this.revVertexToEdge.put(vertex, new HashSet<>());
	}

	/**
	 * Returns the source of a edge of the graph.
	 * 
	 * @param edge
	 *            A edge which is contained in the graph.
	 * @return The vertex which the edge is coming from.
	 */
	public V getSource(E edge) {
		// TODO: is this method necessary? because caller already has the edge
		if (this.getEdgeSet().contains(edge)) {
			return edge.getSource();
		}
		return null;
	}

	@Override
	public Set<V> getVertexSet() {
		return new HashSet<>(vertexToEdge.keySet());
	}

	@Override
	public Set<E> getEdgeSet() {
	    Set<E> edges = new  HashSet<>();
	    for (V vertex : this.getVertexSet()) {
	        edges.addAll(this.outgoingEdgesOf(vertex));
	    }
		return edges;
	}

	@Override
	public List<LayoutOption> getRegisteredLayouts() {
        List<DirectedGraphLayoutOption> directedLayoutOptions = new LinkedList<>();
        if (DefaultDirectedGraph.register != null) {
            directedLayoutOptions.addAll(DefaultDirectedGraph.register.getLayoutOptions());
        }
        for (DirectedGraphLayoutOption option : directedLayoutOptions) {
            option.setGraph(this);
        }
        List<LayoutOption> layoutOptions = new LinkedList<>(directedLayoutOptions);
        return layoutOptions;
	}

	@Override
	public Integer outdegreeOf(V vertex) {
		return vertexToEdge.get(vertex).size();
	}

	@Override
	public Integer indegreeOf(V vertex) {
		return revVertexToEdge.get(vertex).size();
	}

	@Override
	public Set<E> outgoingEdgesOf(V vertex) {
		Set<E> result = new HashSet<>();
		Set<E> original = vertexToEdge.get(vertex);

		if (original != null) {
			result.addAll(original);
		}

		return result;
	}

	@Override
	public Set<E> incomingEdgesOf(V vertex) {
		Set<E> result = new HashSet<>();
		Set<E> original = revVertexToEdge.get(vertex);

		if (original != null) {
			result.addAll(original);
		}

		return result;
	}

	@Override
	public Set<E> edgesOf(V vertex) {
		Set<E> result = this.incomingEdgesOf(vertex);
		result.addAll(this.outgoingEdgesOf(vertex));
		return result;
	}

	@Override
	public FastGraphAccessor getFastGraphAccessor() {
		return fga;
	}

	@Override
	public void addToFastGraphAccessor(FastGraphAccessor fga) {
		for (V v : getVertexSet()) {
			v.addToFastGraphAccessor(fga);
		}
		for (E e : getEdgeSet()) {
			e.addToFastGraphAccessor(fga);
		}
	}

	@Override
	public CollapsedVertex<V, E> collapse(Set<V> subset) {
		DefaultDirectedGraph<V, E> collapsedGraph = new DefaultDirectedGraph<V, E>("");
		CollapsedVertex<V, E> collapsed = new CollapsedVertex<V, E>("", "");
		subset.forEach((v) -> collapsedGraph.addVertex(v));
		for (E edge : getEdgeSet()) {
			boolean containsSource = subset.contains(edge.getSource());
			boolean containsTarget = subset.contains(edge.getTarget());

			if (containsSource && containsTarget) {
				collapsedGraph.addEdge(edge);
				removeEdge(edge);
			} else if (containsSource && !containsTarget) {
				V removedVertex = edge.getSource();
				edge.setVertices((V) collapsed, edge.getTarget()); // TODO: K�nnte Probleme machen, CollapsedVertex ist nicht vom generic-Typ ist.
				collapsed.addModifiedEdge(edge, removedVertex);
			} else if (!containsSource && containsTarget) {
				V removedVertex = edge.getTarget();
				edge.setVertices(edge.getSource(), (V) collapsed); // TODO: K�nnte Probleme machen, CollapsedVertex ist nicht vom generic-Typ ist.
				collapsed.addModifiedEdge(edge, removedVertex);
			}
		}

		collapsed.setGraph(collapsedGraph);
		removeAllVertices(subset);
		addVertex((V) collapsed); // TODO: Koennte Probleme machen, CollapsedVertex ist nicht vom generic-Typ ist. (wirft cast error)
		this.collapsedVertices.add(collapsed);
		
		return collapsed;
	}

	@Override
	public Set<V> expand(CollapsedVertex<V, E> vertex) {
		Set<V> collapsedVertices = vertex.getGraph().getVertexSet();

		this.addAllEdges(vertex.getGraph().getEdgeSet());
		//TODO; Bug Why is an vertex removed from the edge set?
		//this.edgeSet.remove(vertex);
		this.addAllVertices(collapsedVertices);
		
		for(E edge : outgoingEdgesOf((V) vertex)) { // Sollte keine Probleme geben, da die CollapsedVertex ja aus dem Graphen genommen wird.
			edge.setVertices(vertex.getVertexForEdge(edge), edge.getTarget());
		}
		for(E edge : incomingEdgesOf((V) vertex)) {
			edge.setVertices(edge.getSource(), vertex.getVertexForEdge(edge));
		}
		
		this.collapsedVertices.remove(vertex);
		
		return collapsedVertices;
	}

	@Override
	public boolean isCollapsed(V vertex) {
		for (CollapsedVertex<V, E> collapsed : collapsedVertices) {
			if (collapsed.getGraph().getVertexSet().contains(vertex))
				return true;
		}
		
		return false;
	}
	
    protected void removeEdge(E edge) {
        for (Set<E> outgoingEdges : vertexToEdge.values()) {
            outgoingEdges.remove(edge);
        }
        for (Set<E> incomingEdges : revVertexToEdge.values()) {
            incomingEdges.remove(edge);
        }

    }

    protected void removeVertex(V vertex) {
        assert (this.outdegreeOf(vertex) == 0);
        assert (this.indegreeOf(vertex) == 0);
        vertexToEdge.remove(vertex);
        revVertexToEdge.remove(vertex);
    }

    private void removeAllVertices(Set<V> vertices) {
        for (V vertex : vertices ) {
            removeVertex(vertex);
        }
    }

    private void addAllEdges(Set<E> edges) {
        for (E edge : edges) {
            this.addEdge(edge);
        }
    }

    private void addAllVertices(Set<V> vertices) {
        for (V vertex : vertices) {
            addVertex(vertex);
        }
    }

	@Override
	public LayoutOption getDefaultLayout() {
		return null;
	}

	@Override
	public Graph getParentGraph() {
		return this.parent;
	}
	
	@Override
	public void setParentGraph(Graph parent) {
		this.parent = parent;
	}

	@Override
	public List<Graph<? extends Vertex, ? extends Edge<? extends Vertex>>> getChildGraphs() {
		return this.children;
	}
	
	@Override
	public void addChildGraph(Graph child) {
		this.children.add(child);
	}
	
	public static DirectedGraphLayoutRegister getDirectedGraphLayoutRegister()
	{
	    if (register == null) {
	        register = new DirectedGraphLayoutRegister();
	    }
	    return register;
	}
	
	@Override
	public String toString(){
		String out = "Vertices: {";
		for(V v : getVertexSet()) {
			out+= v.getName() + ", ";
		}
		out = out.substring(0, out.length()-2);
		out+= "}";
		out+= '\n';
		out+= "Edges:{";
		out+= '\n';
		for(E e : this.getEdgeSet()){
			out+= e.getName() + "[" +e.getSource().getName() +"->"+ e.getTarget().getName()+"],";
			out+= '\n';
		}
		out=out.substring(0, out.length()-2);
		return out+="}";
	}
}
