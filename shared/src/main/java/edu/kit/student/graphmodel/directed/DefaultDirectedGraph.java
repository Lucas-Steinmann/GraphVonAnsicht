package edu.kit.student.graphmodel.directed;

import edu.kit.student.graphmodel.CollapsedVertex;
import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.Graph;
import edu.kit.student.graphmodel.SerializedEdge;
import edu.kit.student.graphmodel.SerializedGraph;
import edu.kit.student.graphmodel.SerializedVertex;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.ViewableGraph;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.util.IdGenerator;

import java.util.*;

/**
 * A {@link DefaultDirectedGraph} is a specific Graph which only contains
 * {@link DirectedEdge} as edges.
 */
public class DefaultDirectedGraph<V extends Vertex, E extends DirectedEdge<V>>
		implements DirectedGraph<V, E>, ViewableGraph<V, E> {

	private DirectedGraphLayoutRegister register;
	private GAnsProperty<String> name;
	private Integer id;
	private FastGraphAccessor fga;
	private Set<V> vertexSet;
	private Set<E> edgeSet;
	private Set<CollapsedVertex<V, E>> collapsedVertices;
	
	private Graph parent;
	private List<Graph> children;

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
		this.name = new GAnsProperty<String>("graphName", name);
		this.id = IdGenerator.getInstance().createId();
		this.fga = new FastGraphAccessor();
		this.children = new ArrayList<Graph>();
		this.vertexSet = new HashSet<V>();
		this.edgeSet = new HashSet<E>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param name of the new graph
	 * @param id of the new graph
	 * @param vertices of the new graph
	 * @param edges of the new graph
	 */
    public DefaultDirectedGraph(String name, Set<V> vertices, Set<E> edges) {
    	this(name);
        this.vertexSet = vertices;
        this.edgeSet = edges;
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
		this.edgeSet.add(edge);
	}

	/**
	 * Adds an vertex to the vertexSet
	 * 
	 * @param vertex
	 */
	public void addVertex(V vertex) {
		this.vertexSet.add(vertex);
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
		if (this.edgeSet.contains(edge)) {
			return edge.getSource();
		}
		return null;
	}

	@Override
	public Set<V> getVertexSet() {
		return this.vertexSet;
	}

	@Override
	public Set<E> getEdgeSet() {
		return this.edgeSet;
	}

	@Override
	public List<LayoutOption> getRegisteredLayouts() {
		return register.getLayoutOptions();
	}

	@Override
	public Integer outdegreeOf(V vertex) {
		Integer outdegree = 0;
		for (E edge : edgeSet) {
			if (edge.getSource() == vertex)
				outdegree++;
		}

		return outdegree;
	}

	@Override
	public Integer indegreeOf(V vertex) {
		Integer indegree = 0;
		for (E edge : edgeSet) {
			if (edge.getTarget() == vertex)
				indegree++;
		}

		return indegree;
	}

	@Override
	public Set<E> outgoingEdgesOf(V vertex) {
		Set<E> outgoing = new HashSet<E>();
		for (E edge : edgeSet) {
			if (edge.getSource() == vertex)
				outgoing.add(edge);
		}

		return outgoing;
	}

	@Override
	public Set<E> incomingEdgesOf(V vertex) {
		Set<E> incoming = new HashSet<E>();
		for (E edge : edgeSet) {
			if (edge.getTarget() == vertex)
				incoming.add(edge);
		}

		return incoming;
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
		for (V v : this.vertexSet) {
			v.addToFastGraphAccessor(fga);
		}
		for (E e : this.edgeSet) {
			e.addToFastGraphAccessor(fga);
		}
	}

	@Override
	public SerializedGraph serialize() {
		List<String[]> attributes = new LinkedList<>();
		SerializedGraph graph = new SerializedGraph(attributes);

		for (Vertex v : this.vertexSet) {
			SerializedVertex vertex = v.serialize();
			// TODO add to graph
		}

		for (Edge e : this.edgeSet) {
			SerializedEdge edge = e.serialize();
			// TODO add to graph
		}

		return graph;
	}

	@Override
	public CollapsedVertex<V, E> collapse(Set<V> subset) {
		DefaultDirectedGraph<V, E> collapsedGraph = new DefaultDirectedGraph<V, E>("");
		CollapsedVertex<V, E> collapsed = new CollapsedVertex<V, E>("", "", 0);
		subset.forEach((v) -> collapsedGraph.addVertex(v));
		for (E edge : edgeSet) {
			boolean containsSource = subset.contains(edge.getSource());
			boolean containsTarget = subset.contains(edge.getTarget());

			if (containsSource && containsTarget) {
				collapsedGraph.addEdge(edge);
				edgeSet.remove(edge);
			} else if (containsSource && !containsTarget) {
				V removedVertex = edge.getSource();
				edge.setVertices((V) collapsed, edge.getTarget()); // TODO: Könnte Probleme machen, CollapsedVertex ist nicht vom generic-Typ ist.
				collapsed.addModifiedEdge(edge, removedVertex);
			} else if (!containsSource && containsTarget) {
				V removedVertex = edge.getTarget();
				edge.setVertices(edge.getSource(), (V) collapsed); // TODO: Könnte Probleme machen, CollapsedVertex ist nicht vom generic-Typ ist.
				collapsed.addModifiedEdge(edge, removedVertex);
			}
		}

		collapsed.setGraph(collapsedGraph);
		this.vertexSet.removeAll(subset);
		this.vertexSet.add((V) collapsed); // TODO: Könnte Probleme machen, CollapsedVertex ist nicht vom generic-Typ ist.
		this.collapsedVertices.add(collapsed);
		
		return collapsed;
	}

	@Override
	public Set<V> expand(CollapsedVertex<V, E> vertex) {
		Set<V> collapsedVertices = vertex.getGraph().getVertexSet();

		this.edgeSet.addAll(vertex.getGraph().getEdgeSet());
		this.edgeSet.remove(vertex);
		this.vertexSet.addAll(collapsedVertices);
		
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
	public List<Graph> getChildGraphs() {
		return this.children;
	}
	
	@Override
	public void addChildGraph(Graph child) {
		this.children.add(child);
	}
}
