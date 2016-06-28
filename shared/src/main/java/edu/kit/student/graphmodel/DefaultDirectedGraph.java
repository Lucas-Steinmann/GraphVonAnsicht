package edu.kit.student.graphmodel;

import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;

import java.util.*;

/**
 * A {@link DefaultDirectedGraph} is a specific Graph which only contains
 * {@link DirectedEdge} as edges.
 */
public class DefaultDirectedGraph<V extends Vertex, E extends DirectedEdge<V>>
		implements DirectedGraph<V, E>, ViewableGraph<V, E> {

	private DirectedGraphLayoutRegister register;
	private GAnsProperty<String> name;
	private GAnsProperty<Integer> id;
	private FastGraphAccessor fga;
	private Set<V> vertexSet;
	private Set<E> edgeSet;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the new graph
	 * @param id
	 *            The id of the new graph
	 */
	public DefaultDirectedGraph(String name, Integer id) {
		// create Sets
		this.vertexSet = new HashSet<V>();
		this.edgeSet = new HashSet<E>();
		this.name = new GAnsProperty<String>("graphName", name);
		this.id = new GAnsProperty<Integer>("graphID", id);
		this.fga = new FastGraphAccessor();
	}

	@Override
	public String getName() {
		return name.getValue();
	}

	@Override
	public Integer getID() {
		return id.getValue();
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
		SerializedGraph graph = new SerializedGraph(attributes, this.name, this.id, this.fga);

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
	public CompoundVertex collapse(Set<V> subset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<V> expand(CompoundVertex vertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCompound(Vertex vertex) {
		return false;
	}

	@Override
	public LayoutOption getDefaultLayout() {
		// TODO Auto-generated method stub
		return null;
	}
}
