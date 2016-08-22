package edu.kit.student.graphmodel.directed;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.SubGraph;
import edu.kit.student.graphmodel.Vertex;

/**
 * A {@link DefaultDirectedGraph} is a specific Graph which only contains
 * {@link DirectedEdge} as edges.
 * 
 * @param <V> VertexKind
 * @param <E> DirectedEdgeKind
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
	 * Constructor
	 * 
	 */
    public DefaultDirectedGraph(){
        // create Sets
        this(new HashSet<>(), new HashSet<>());
    }

    /**
     * Constructor
     * 
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
	 * Adds an edge to the edgeSet
	 * 
	 * @param edge
	 */
	public void addEdge(E edge) {
//		System.out.println("adds edge: "+edge.getSource().getID()+","+edge.getTarget().getID());
		
	    if (this.vertexToEdge.keySet().contains(edge.getSource()) && this.vertexToEdge.keySet().contains(edge.getTarget())) {
	        vertexToEdge.get(edge.getSource()).add(edge);
	        revVertexToEdge.get(edge.getTarget()).add(edge);

			if (edge.getSource().getID().equals(edge.getTarget().getID())) {
				this.vertexToSelfLoops.get(edge.getSource()).add(edge);
			}
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
		this.vertexToSelfLoops.put(vertex, new HashSet<>());
		this.idToVertex.put(vertex.getID(), vertex);
	}

	/**
	 * Returns the source of a edge of the graph.
	 * 
	 * @param edge
	 *            A edge which is contained in the graph.
	 * @return The vertex which the edge is coming from.
	 */
	public Vertex getSource(DirectedEdge edge) {
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
	
    public void removeEdge(E edge) {
		Vertex source = edge.getSource();
		Vertex target = edge.getTarget();

		if (source.getID().equals(target.getID())) {
			vertexToSelfLoops.get(source).remove(edge);
			return;
		}

		vertexToEdge.get(source).remove(edge);
		revVertexToEdge.get(target).remove(edge);
    }

    public void removeVertex(V vertex) {
        if (!this.contains(vertex)) {
            throw new IllegalArgumentException("Cannot delete vertex, not present in this graph!");
        }
        assert (this.outdegreeOf(vertex) == 0);
        assert (this.indegreeOf(vertex) == 0);
        vertexToEdge.remove(vertex);
        revVertexToEdge.remove(vertex);
        idToVertex.remove(vertex.getID());
		vertexToSelfLoops.remove(vertex);
    }

    public void removeAllVertices(Set<V> vertices) {
        for (V vertex : vertices ) {
            removeVertex(vertex);
        }
    }

    public void removeAllEdges(Set<E> edges) {
        for ( E edge : edges ) {
            removeEdge(edge);
        }
    }

    public void addAllEdges(Set<E> edges) {
        for (E edge : edges) {
            this.addEdge(edge);
        }
    }

    public void addAllVertices(Set<V> vertices) {
        for (V vertex : vertices) {
            addVertex(vertex);
        }
    }
    
    public boolean contains(Vertex vertex) {
        return this.getVertexSet().contains(vertex);
    }

	public static DirectedGraphLayoutRegister getDirectedGraphLayoutRegister()
	{
	    if (register == null) {
	        register = new DirectedGraphLayoutRegister();
	    }
	    return register;
	}
	
	public V getVertexById(int id) {
	    return this.idToVertex.get(id);
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
