package edu.kit.student.sugiyama.graph;

import edu.kit.student.graphmodel.*;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.util.Point;

import java.util.*;


/**
 * The SugiyamaGraph is a wrapper for a directed graph to enable easy and fast accessibility of
 * attributes and constructs needed during the computation of the hierarchical layout of a directed graph.
 * All vertices are assigned to a layer.
 * The positions of the vertices can be viewed as a grid (with varying widths per layer).
 */
public class SugiyamaGraph
		implements ICycleRemoverGraph,
		ILayerAssignerGraph,
		ICrossMinimizerGraph,
		IVertexPositionerGraph,
		IEdgeDrawerGraph
{

	private List<Edge<Vertex>> reversedEdges;
	private List<List<SugiyamaVertex>> layers;
	private List<Integer> layerPositions;
	private Map<Vertex, Integer> vertexToLayer;
	private Set<Edge<Vertex>> brokenCycleEdges;
	private Set<Vertex> insertedVertices;
	private Graph<? extends Vertex, ? extends Edge<? extends Vertex>> graph;
	private FastGraphAccessor fga;
	private Set<SugiyamaVertex> vertexSet;
	private Set<SugiyamaEdge> edgeSet;

	/**
	 * Constructs a new SugiyamaGraph and sets the Graph which is the underlying representation.
	 * To fulfill the invariant that all vertices are assigned to a layer, all vertices
	 * will be assigned to layer 0.
	 *
	 * @param graph the graph used as underlying representation.
	 */
	public SugiyamaGraph(DirectedGraph<? extends Vertex, ? extends DirectedEdge<? extends Vertex>> graph) {
		this.vertexSet = new HashSet<SugiyamaVertex>();
		this.edgeSet = new HashSet<SugiyamaEdge>();
		this.graph = graph;
		this.fga = new FastGraphAccessor();
		this.reversedEdges = new LinkedList<Edge<Vertex>>();
		layers = new LinkedList<>();
		List<SugiyamaVertex> startingLayer = new LinkedList<>();

		Map<Integer, SugiyamaVertex> tmpVertexMap = new HashMap<>();

		for (Vertex vertex: graph.getVertexSet()) {
			SugiyamaVertex sugiyamaVertex = new SugiyamaVertex(vertex, -1);
			startingLayer.add(sugiyamaVertex);	//fills first layer with all vertices and default layer number
			vertexSet.add(sugiyamaVertex);	//fills vertexset with all wrapped vertices
			tmpVertexMap.put(vertex.getID(), sugiyamaVertex);
		}

		for(DirectedEdge edge: graph.getEdgeSet()){
			SugiyamaEdge sugiyamaEdge = new SugiyamaEdge(edge);
			sugiyamaEdge.setVertices(
					tmpVertexMap.get(edge.getSource().getID()),
					tmpVertexMap.get(edge.getTarget().getID())
			);
			edgeSet.add(sugiyamaEdge);	//fills edgeset with all wrapped edges
		}

		layers.add(startingLayer);
		layerPositions = new LinkedList<>();
		layerPositions.add(0);
	}

	/**
	 * Replaces the specified edge with a path of dummy vertices of the specified length.
	 * Replaced edges are removed from the set of edges but saved for later retrieval
	 * with {@code getReplacedEdges()} or restored with {@code restoreReplacedEdges}.
	 *
	 * @param edge the edge to be replaced
	 * @param length the length of the path which replaces the edge
	 */
	private void replaceWithSupplementPath(SugiyamaEdge edge, int length) {
		//TODO implement
	}

	@Override
	public int getLayerCount() {
		return this.layers.size();
	}

	@Override
	public int getVertexCount(int layerNum) {
		return this.layers.get(layerNum).size();
	}

	@Override
	public void reverseEdge(SugiyamaEdge edge) {
		edge.setReversed(true);
	}

	@Override
	public boolean isReversed(SugiyamaEdge edge) {
		return edge.isReversed();
	}

	@Override
	public void swapVertices(SugiyamaVertex first, SugiyamaVertex second) {
		assert(this.getLayer(first)==this.getLayer(second)); //both vertices have to be on the same layer!
		int layerNum = this.getLayer(first);
		List<SugiyamaVertex> layer = this.getLayer(layerNum);
		int pos1 = layer.indexOf(first);
		int pos2 = layer.indexOf(second);
		layer.remove(first);
		layer.remove(second);

		if (pos1 < pos2) {
			layer.add(pos1, second);
			layer.add(pos2, first);
		} else {
			layer.add(pos2, first);
			layer.add(pos1, second);
		}

		//		List des not support inserting at a special index in the list, just "add(obj)"
	}

	@Override
	public int getLayer(SugiyamaVertex vertex) {
		return vertex.getLayer();
	}

	@Override
	public List<SugiyamaVertex> getLayer(int layerNum) {
		return this.layers.get(layerNum); //TODO check if it would be wiser to change the method signature to List<V>
	}

	@Override
	public List<List<SugiyamaVertex>> getLayers() {
		return this.layers; //TODO see getLayer()
	}

	@Override
	public void addEdgeCorner(SugiyamaEdge edge, int x, int y, int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeEdgeCorner(SugiyamaEdge edge, int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Point> getEdgeCorners(SugiyamaEdge edge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLayerY(int layerNum, int y) {
		if (layerNum >= layerPositions.size()) {
			throw new IndexOutOfBoundsException();
		}

		layerPositions.set(layerNum, y);
	}

	@Override
	public void setX(SugiyamaVertex vertex, int x) {
		vertex.setX(x);

	}

	@Override
	public void assignToLayer(SugiyamaVertex vertex, int layerNum) {
		int layer = Math.max(vertex.getLayer(), 0);
		this.layers.get(layer).remove(vertex);

		for (int i = layers.size() - 1; i < layerNum; i++) {
			this.layers.add(new LinkedList<>());
		}

		this.layers.get(layerNum).add(vertex);
		vertex.setLayer(layerNum);
	}

	@Override
	public FastGraphAccessor getFastGraphAccessor() {
		return this.fga;
	}

	@Override
	public Integer outdegreeOf(SugiyamaVertex vertex) {
		Integer outdegree = 0;

		for (SugiyamaEdge edge : this.edgeSet) {
			if (edge.getSource().getID() == vertex.getID())
				outdegree++;
		}

		return outdegree;
	}


	@Override
	public Integer indegreeOf(SugiyamaVertex vertex) {
		Integer indegree = 0;

		for (SugiyamaEdge edge : this.edgeSet) {
			if (edge.getTarget().getID() == vertex.getID())
				indegree++;
		}

		return indegree;
	}

	@Override
	public Set<SugiyamaEdge> incomingEdgesOf(SugiyamaVertex vertex) {
		Set<SugiyamaEdge> incoming = new HashSet<SugiyamaEdge>();

		for (SugiyamaEdge edge : edgeSet) {
			if (edge.getTarget() == vertex)
				incoming.add(edge);
		}

		return incoming;
	}

	@Override
	public Set<SugiyamaEdge> outgoingEdgesOf(SugiyamaVertex vertex) {
		Set<SugiyamaEdge> outgoing = new HashSet<SugiyamaEdge>();

		for (SugiyamaEdge edge : edgeSet) {
			if (edge.getSource() == vertex)
				outgoing.add(edge);
		}

		return outgoing;
	}

	@Override
	public Set<SugiyamaEdge> edgesOf(SugiyamaVertex vertex) {
		Set<SugiyamaEdge> result = this.incomingEdgesOf(vertex);
		result.addAll(this.outgoingEdgesOf(vertex));
		return result;
	}

	@Override
	public Set<SugiyamaVertex> getVertexSet() {
		return this.vertexSet;
	}

	@Override
	public Set<SugiyamaEdge> getEdgeSet() {
		return this.edgeSet;
	}

	@Override
	public void addToFastGraphAccessor(FastGraphAccessor fga) {
		this.addToFastGraphAccessor(fga);
	}

	@Override
	public Set<SugiyamaEdge> getReversedEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// Is not needed in this graph.
		return graph.getName();
	}

	@Override
	public Integer getID() {
		// Is not needed in this graph.
		return graph.getID();
	}

	@Override
	public int getHeight() { return 0; }

	@Override
	public int getMaxWidth() { return 0; }

	@Override
	public Set<SugiyamaEdge> restoreAllEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<SugiyamaEdge> getReplacedEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<SugiyamaEdge> restoreReplacedEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLayerWidth(int layerN) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<LayeredGraph<SugiyamaVertex, SugiyamaEdge>> getSubgraphs() {
		return null;
	}

	/**
	 * A supplement path for connecting vertices, which are more than one layer apart.
	 * They are stored in the SugiyamaEdge along with the substituted edge.
	 */
	public static class SupplementPath extends DirectedEdge<DummyVertex>
	{
		public SupplementPath(String name, String label) {
			super(name, label);
		}

		/**
		 * Returns the number of vertices including source and target.
		 * @return the length of the path
		 */
		public int getLength() {return 0;}

		/**
		 * Returns the list of vertices on the path sorted from source to target excluding the source and target.
		 * @return the list of vertices
		 */
		public List<DummyVertex> getDummyVertices() {return null;}

		/**
		 * Returns the list of edges on the path from source to target
		 * @return the edges
		 */
		public List<SupplementEdge> getEdges() {return null;}

		/**
		 * Returns the edge which is substituted by this path
		 * @return the replaced edge
		 */
		//public E getReplacedEdge() {return null;}
	}
	/**
	 * A supplement edge which is part of a {@link SupplementPath}.
	 */
	public static class SupplementEdge extends DirectedEdge<DefaultVertex> {
		public SupplementEdge(String name, String label) {
			super(name, label);
		}
	}

	/**
	 * A supplement vertex which is part of a {@link SupplementPath}.
	 */
	public static class DummyVertex extends DefaultVertex {
		public DummyVertex(String name, String label) {
			super(name, label);
			// TODO Auto-generated constructor stub
		}

		public boolean isDummyVertex() {
			return true;
		}
	}

	/**
	 * A wrapper class for vertices used in the sugiyama framework.
	 * A SugiyamaVertex can be a {@link DefaultVertex} or a {@link DummyVertex}
	 */
	public static class SugiyamaVertex implements Vertex
	{
		private final Vertex vertex;
		private int layer;

		public SugiyamaVertex(Vertex vertex, int layer) {
			this.vertex = vertex;
			this.layer = layer;
		}

		public boolean isDummyVertex() {
			return false;
		}

		public Vertex getVertex() {
			return vertex;
		}

		public int getLayer() {
			return layer;
		}

		public void setLayer(int layer) {
			this.layer = layer;
		}

        @Override
        public String getName() {
        	return this.vertex.getName();
        }

        @Override
        public Integer getID() {
            return this.vertex.getID();
        }

        @Override
        public String getLabel() {
            return this.vertex.getLabel();
        }

        @Override
        public int getX() {
            return this.vertex.getX();
        }

        @Override
        public int getY() {
            return this.vertex.getY();
        }

        @Override
        public void setX(int x) {
            this.vertex.setX(x);
            
        }

        @Override
        public void setY(int y) {
            this.vertex.setY(y);
            
        }

        @Override
        public void addToFastGraphAccessor(FastGraphAccessor fga) {
            this.vertex.addToFastGraphAccessor(fga);
            
        }

        @Override
        public SerializedVertex serialize() {
            return this.vertex.serialize();
        }
	}

	/**
	 * A wrapper class for directed edges to implement additional functionality
	 * to apply the sugiyama layout to the SugiyamaGraph containing them.
	 */
	public static class SugiyamaEdge extends DirectedEdge<SugiyamaVertex>
	{
		List<Vector<Integer>> corners;
		private boolean reversed;
		private DirectedEdge<Vertex> wrappedEdge;
		private SugiyamaVertex source;
		private SugiyamaVertex target;

		private SugiyamaEdge(DirectedEdge<Vertex> edge) {
			super(edge.getName(), edge.getLabel());
			this.wrappedEdge = edge;
			this.reversed = false;
		}

		//private E getEdge() { return null; }
		/**
		 * Returns true, if this edge has been reversed in order to break cycles in the first step of sugiyama, false otherwise.
		 * @return
		 * 		true if this edge is reversed, false otherwise
		 */
		private boolean isReversed() {
			return this.reversed;
		}

		/**
		 * Sets this edge to be reversed or not.
		 * @param reversed
		 * 		sets, if this edge is reversed or not
		 */
		private void setReversed(boolean reversed) {
			this.reversed = reversed;
		}

		/**
		 * Returns true, if this edge was replaced by a {@link SupplementPath} that contains source and target vertices and at least one dummy vertex.
		 * @return
		 * 		True if this edge is an supplement path, false otherwise
		 */
		private boolean isReplaced() {
			return false;
		}

		/**
		 * Returns the {@link SupplementPath} which this {@link SugiyamaEdge} represents.
		 * @return
		 */
		private SupplementPath getSupplementPath() {
//			SupplementPath supplement = new SupplementPath();//TODO implement
//			return supplement;
		    return null;
		}

		@Override
		public void setVertices(SugiyamaVertex source, SugiyamaVertex target) {
			this.source = source;
			this.target = target;
			wrappedEdge.setVertices(source.getVertex(), target.getVertex());
		}


		@Override
		public String getName() {
			return wrappedEdge.getName();
		}

		@Override
		public Integer getID() {
			return wrappedEdge.getID();
		}

		@Override
		public String getLabel() {
			return wrappedEdge.getLabel();
		}

		@Override
		public void addToFastGraphAccessor(FastGraphAccessor fga) {
			wrappedEdge.addToFastGraphAccessor(fga);
		}

		@Override
		public SugiyamaVertex getSource() {
			return this.source;
		}

		@Override
		public SugiyamaVertex getTarget() {
			return this.target;
		}

		@Override
		public List<SugiyamaVertex> getVertices() {
			List<SugiyamaVertex> vertices = new LinkedList<SugiyamaVertex>();
			vertices.add(source);
			vertices.add(target);
			return vertices;
		}

		@Override
		public OrthogonalEdgePath getPath() {
			return wrappedEdge.getPath();
		}
	}

	@Override
	public int getLayerFromVertex(SugiyamaVertex vertex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SerializedGraph serialize() {
		// TODO muss weg
		return null;
	}

	@Override
	public List<LayoutOption> getRegisteredLayouts() {
		// TODO muss weg
		return null;
	}

	@Override
	public LayoutOption getDefaultLayout() {
		// TODO muss weg
		return null;
	}

	@Override
	public Graph getParentGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentGraph(Graph parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Graph> getChildGraphs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addChildGraph(Graph child) {
		// TODO Auto-generated method stub
		
	}
}
