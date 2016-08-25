package edu.kit.student.graphmodel.directed;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.Vertex;

/**
 * A {@link DefaultDirectedGraph} is a specific Graph which only contains
 * {@link DirectedEdge} as edges.
 * 
 * @param <V> the type of the vertices contained in this graph
 * @param <E> the type of the edges contained in this graph
 * 
 */
public class DefaultDirectedGraph<V extends Vertex, E extends DirectedEdge>
		implements DirectedGraph {

	private static DirectedGraphLayoutRegister register = new DirectedGraphLayoutRegister();
	private FastGraphAccessor fga;
	private HashMap<V, Set<E>> vertexToEdge;
	private HashMap<V, Set<E>> revVertexToEdge;
	private HashMap<V, Set<E>> vertexToSelfLoops;
	// Maybe replace with FGA
	private HashMap<Integer, V> idToVertex;

	/**
	 * Constructs a new empty {@link DefaultDirectedGraph}.
	 */
    public DefaultDirectedGraph(){
        this(new HashSet<>(), new HashSet<>());
    }

    /**
     * Constructs a new {@link DefaultDirectedGraph} with the given vertex set and edge set.
     * @param vertices of the new graph
     * @param edges of the new graph
     */
    public DefaultDirectedGraph(Set<V> vertices, Set<E> edges) {
        this.fga = new FastGraphAccessor();

        this.idToVertex = new HashMap<>();
        this.vertexToEdge = new HashMap<>();
        this.revVertexToEdge = new HashMap<>();
		this.vertexToSelfLoops = new HashMap<>();
        for (V vertex : vertices) {
            this.vertexToEdge.put(vertex, new HashSet<>());
            this.revVertexToEdge.put(vertex, new HashSet<>());
			this.vertexToSelfLoops.put(vertex, new HashSet<>());
            this.idToVertex.put(vertex.getID(), vertex);
        }
        for (E edge : edges) {
            this.addEdge(edge);
        }
    }

	/**
	 * Adds the specified edge to the set of edges.
	 * @param edge the edge to add
	 */
	public void addEdge(E edge) {
	    if (this.vertexToEdge.keySet().contains(edge.getSource()) && this.vertexToEdge.keySet().contains(edge.getTarget())) {
	        vertexToEdge.get(edge.getSource()).add(edge);
	        revVertexToEdge.get(edge.getTarget()).add(edge);

			if (edge.getSource().getID().equals(edge.getTarget().getID())) {
				this.vertexToSelfLoops.get(edge.getSource()).add(edge);
			}
	    } else {
	        throw new IllegalArgumentException("Cannot add edge to a graph without the Vertex being present");
	    }
	}

	/**
	 * Adds the specified vertex to the set of vertices.
	 * @param vertex the vertex to add
	 */
	public void addVertex(V vertex) {
		this.vertexToEdge.put(vertex, new HashSet<>());
		this.revVertexToEdge.put(vertex, new HashSet<>());
		this.vertexToSelfLoops.put(vertex, new HashSet<>());
		this.idToVertex.put(vertex.getID(), vertex);
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
	        edges.addAll(this.incomingEdgesOf(vertex));
	    }
		return edges;
	}

	@Override
	public Integer outdegreeOf(Vertex vertex) {
		return vertexToEdge.get(vertex).size();
	}

	@Override
	public Integer indegreeOf(Vertex vertex) {
		return revVertexToEdge.get(vertex).size();
	}

	@Override
	public Integer selfLoopNumberOf(Vertex vertex) {
		return vertexToSelfLoops.get(vertex).size();
	}

	@Override
	public Set<E> outgoingEdgesOf(Vertex vertex) {
		Set<E> result = new HashSet<>();
		Set<E> original = vertexToEdge.get(vertex);

		if (original != null) {
			result.addAll(original);
		}

		return result;
	}

	@Override
	public Set<E> incomingEdgesOf(Vertex vertex) {
		Set<E> result = new HashSet<>();
		Set<E> original = revVertexToEdge.get(vertex);

		if (original != null) {
			result.addAll(original);
		}

		return result;
	}

	@Override
	public Set<E> selfLoopsOf(Vertex vertex) {
		Set<E> result = new HashSet<>();
		Set<E> original = vertexToSelfLoops.get(vertex);

		if (original != null) {
			result.addAll(original);
		}

		return result;
	}

	@Override
	public Set<E> edgesOf(Vertex vertex) {
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
		for (Vertex v : getVertexSet()) {
			v.addToFastGraphAccessor(fga);
		}
		for (DirectedEdge e : getEdgeSet()) {
			e.addToFastGraphAccessor(fga);
		}
	}
	
	/**
	 * Removes the specified edge from the graph.
	 * @param edge the edge to remove
	 */
    public void removeEdge(E edge) {
		Vertex source = edge.getSource();
		Vertex target = edge.getTarget();

		if (source.getID().equals(target.getID())) {
			vertexToSelfLoops.get(source).remove(edge);
		}

		vertexToEdge.get(source).remove(edge);
		revVertexToEdge.get(target).remove(edge);
    }

    /**
     * Removes the specified vertex from the graph.
     * @param vertex the vertex to remove
     */
    public void removeVertex(V vertex) {
        if (!this.contains(vertex)) {
            throw new IllegalArgumentException("Cannot delete vertex, not present in this graph!");
        } 
        if (!(this.edgesOf(vertex).size() == 0)) {
            throw new IllegalArgumentException("Cannot delete vertex with edges!");
        }
        vertexToEdge.remove(vertex);
        revVertexToEdge.remove(vertex);
        idToVertex.remove(vertex.getID());
		vertexToSelfLoops.remove(vertex);
    }

    /**
     * Removes all specified vertices from the graph.
     * @param vertices the vertices to remove
     */
    public void removeAllVertices(Set<V> vertices) {
        for (V vertex : vertices ) {
            removeVertex(vertex);
        }
    }

    /**
     * Removes all specified edges from the graph.
     * @param edges the edges to remove
     */
    public void removeAllEdges(Set<E> edges) {
        for ( E edge : edges ) {
            removeEdge(edge);
        }
    }

    /**
     * Adds all specified edges to the graph.
     * @param edges the edges to add
     */
    public void addAllEdges(Set<E> edges) {
        for (E edge : edges) {
            this.addEdge(edge);
        }
    }

    /**
     * Adds all specified vertices to the graph.
     * @param vertices the vertices to add
     */
    public void addAllVertices(Set<V> vertices) {
        for (V vertex : vertices) {
            addVertex(vertex);
        }
    }
    
    /**
     * Returns true if the graph contains the specified vertex
     * @param vertex the vertex
     * @return true if the vertex is contained in this graph, false otherwise.
     */
    public boolean contains(Vertex vertex) {
        return this.getVertexSet().contains(vertex);
    }

	/**
	 * Returns the vertex in this graph with the specified id.
	 * @param id the id
	 * @return the vertex with the specified id
	 */
	public V getVertexById(int id) {
	    return this.idToVertex.get(id);
	}
	
	public static DirectedGraphLayoutRegister getDirectedGraphLayoutRegister()
	{
	    if (register == null) {
	        register = new DirectedGraphLayoutRegister();
	    }
	    return register;
	}
	
	public List<DirectedGraphLayoutOption> getRegisteredLayouts() {
        List<DirectedGraphLayoutOption> directedLayoutOptions = new LinkedList<>();
        if (DefaultDirectedGraph.register != null) {
            directedLayoutOptions.addAll(DefaultDirectedGraph.register.getLayoutOptions());
        }
        for (DirectedGraphLayoutOption option : directedLayoutOptions) {
            option.setGraph(this);
        }
        List<DirectedGraphLayoutOption> layoutOptions = new LinkedList<>(directedLayoutOptions);
        return layoutOptions;
	}

	@Override
	public String toString(){
		String out = "Vertices: {";
		for(Vertex v : getVertexSet()) {
			out+= v.getName() + ", ";
		}
		out = out.substring(0, out.length()-2);
		out+= "}";
		out+= '\n';
		out+= "Edges:{";
		out+= '\n';
		for(DirectedEdge e : this.getEdgeSet()){
			out+= e.getName() + "[" +e.getSource().getName() +"->"+ e.getTarget().getName()+"],";
			out+= '\n';
		}
		out=out.substring(0, out.length()-2);
		return out + "}";
	}
}
