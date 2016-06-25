package edu.kit.student.graphmodel;

import java.util.List;
import java.util.Map.Entry;

import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;

import java.util.Set;

/**
 * A {@link DefaultDirectedGraph} is a specific Graph which only contains
 * {@link DirectedEdge} as edges.
 */
public class DefaultDirectedGraph<V extends Vertex, E extends DirectedEdge<V>> implements DirectedGraph<V, E>, ViewableGraph<V, E> {

	private DirectedGraphLayoutRegister register;
	private GAnsProperty<String> name;
	private GAnsProperty<Integer> id;
	private FastGraphAccessor fga;

	/**
	 * Returns the source of a edge of the graph.
	 * 
	 * @param edge
	 *            A edge which is contained in the graph.
	 * @return The vertex which the edge is coming from.
	 */
	public V getSource(E edge) {
		// TODO Auto-generated method
		return null;
	}

	@Override
	public List<LayoutOption> getRegisteredLayouts() {
		return register.getLayoutOptions();
	}


	@Override
	public Integer outdegreeOf(V vertex) {
		// TODO Auto-generated method
		return null;
	}

	@Override
	public Integer indegreeOf(V vertex) {
		// TODO Auto-generated method
		return null;
	}

	@Override
	public Set<E> outgoingEdgesOf(V vertex) {
		// TODO Auto-generated method
		return null;
	}

	@Override
	public Set<E> incomingEdgesOf(V vertex) {
		// TODO Auto-generated method
		return null;
	}

	@Override
	public String getName() {
		return name.getValue();
	}

	@Override
	public Integer getID() {
		return id.getValue();
	}

	@Override
	public Set<V> getVertexSet() {
		// TODO Auto-generated method
		return null;
	}

	@Override
	public Set<E> getEdgeSet() {
		// TODO Auto-generated method
		return null;
	}

	@Override
	public Set<E> edgesOf(V vertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FastGraphAccessor getFastGraphAccessor() {
		return null;
	}

	@Override
	public void addToFastGraphAccessor(FastGraphAccessor fga) {
		// TODO Auto-generated method stub

	}

	@Override
	public SerializedGraph serialize(List<Entry<String, String>> attributes) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LayoutOption getDefaultLayout() {
		// TODO Auto-generated method stub
		return null;
	}
}
