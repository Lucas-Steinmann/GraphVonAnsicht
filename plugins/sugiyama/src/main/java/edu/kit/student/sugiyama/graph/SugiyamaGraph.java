package edu.kit.student.sugiyama.graph;

import edu.kit.student.graphmodel.*;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.util.Point;

import java.util.*;


/**
 * The SugiyamaGraph is a wrapper for a directed graph to enable easy and fast accessibility of
 * attributes and constructs needed during the computation of the hierarchical layout of a directed graph.
 * All vertices are assigned to a layer.
 * The positions of the vertices can be viewed as a grid (with varying widths per layer).
 */
public class SugiyamaGraph extends DefaultDirectedGraph<ISugiyamaVertex, ISugiyamaEdge>
		implements ICycleRemoverGraph,
		ILayerAssignerGraph,
		ICrossMinimizerGraph,
		IVertexPositionerGraph,
		IEdgeDrawerGraph
{

	private List<Edge<Vertex>> reversedEdges;
	private List<List<ISugiyamaVertex>> layers;
	private List<Integer> layerPositions;
	private Map<Vertex, Integer> vertexToLayer;
	private Set<Edge<Vertex>> brokenCycleEdges;
	private Set<Vertex> insertedVertices;
	private Graph<? extends Vertex, ? extends Edge<? extends Vertex>> graph;
	private Set<SupplementPath> supplementPaths;

	/**
	 * Constructs a new SugiyamaGraph and sets the Graph which is the underlying representation.
	 * To fulfill the invariant that all vertices are assigned to a layer, all vertices
	 * will be assigned to layer 0.
	 *
	 * @param graph the graph used as underlying representation.
	 */
	public SugiyamaGraph(DirectedGraph<? extends Vertex, ? extends DirectedEdge<? extends Vertex>> graph)  {
		super(graph.getName());
		this.graph = graph;
		this.reversedEdges = new LinkedList<Edge<Vertex>>();
		this.supplementPaths = new HashSet<>();
		layers = new LinkedList<>();
		List<ISugiyamaVertex> startingLayer = new LinkedList<>();

		Map<Integer, ISugiyamaVertex> tmpVertexMap = new HashMap<>();

		for (Vertex vertex: graph.getVertexSet()) {
			SugiyamaVertex SugiyamaVertex = new SugiyamaVertex(vertex, -1);
			startingLayer.add(SugiyamaVertex);	//fills first layer with all vertices and default layer number
			addVertex(SugiyamaVertex);	//fills vertexset with all wrapped vertices
			tmpVertexMap.put(vertex.getID(), SugiyamaVertex);
		}

		for(DirectedEdge edge: graph.getEdgeSet()){
			SugiyamaEdge SugiyamaEdge = new SugiyamaEdge(edge);
			SugiyamaEdge.setVertices(
					tmpVertexMap.get(edge.getSource().getID()),
					tmpVertexMap.get(edge.getTarget().getID())
			);
			addEdge(SugiyamaEdge);	//fills edgeset with all wrapped edges
		}

		layers.add(startingLayer);
		layerPositions = new LinkedList<>();
		layerPositions.add(0);
	}

	public SugiyamaGraph(String name, Set vertices, Set edges) {
		super(name, vertices, edges);
	}

	/**
	 * Replaces the specified edge with a path of dummy vertices of the specified length.
	 * Replaced edges are removed from the set of edges but saved for later retrieval
	 * with {@code getReplacedEdges()} or restored with {@code restoreReplacedEdges}.
	 *
	 * @param edge the edge to be replaced
	 * @param length the length of the path which replaces the edge
	 */
	private void replaceWithSupplementPath(ISugiyamaEdge edge, int length) {
		//TODO implement
	}

	public Set<SupplementPath> getSupplementPaths() {
		return supplementPaths;
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
	public void reverseEdge(ISugiyamaEdge edge) {
		removeEdge(edge);
		edge.setReversed(!edge.isReversed());
		addEdge(edge);
	}

	@Override
	public boolean isReversed(ISugiyamaEdge edge) {
		return edge.isReversed();
	}

	@Override
	public void swapVertices(ISugiyamaVertex first, ISugiyamaVertex second) {
		assert(this.getLayer(first)==this.getLayer(second)); //both vertices have to be on the same layer!
		int layerNum = this.getLayer(first);
		List<ISugiyamaVertex> layer = this.getLayer(layerNum);
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

		//		List does not support inserting at a special index in the list, just "add(obj)"
	}

	@Override
	public int getLayer(ISugiyamaVertex vertex) {
		return vertex.getLayer();
	}

	@Override
	public List<ISugiyamaVertex> getLayer(int layerNum) {
		return this.layers.get(layerNum); //TODO check if it would be wiser to change the method signature to List<V>
	}

	@Override
	public List<List<ISugiyamaVertex>> getLayers() {
		return this.layers; //TODO see getLayer()
	}
	
	@Override
	public DummyVertex createDummy(String name, String label, int layer) {
		DummyVertex dummyVertex = new DummyVertex(name, label, layer);
		this.addVertex(dummyVertex);
		return dummyVertex;
	}
	
	@Override
	public SupplementEdge createSupplementEdge(String name, String label, ISugiyamaVertex source, ISugiyamaVertex target) {
		SupplementEdge supplementEdge = new SupplementEdge(name, label, source, target);
		this.addEdge(supplementEdge);
		return supplementEdge;
	}

	public SupplementPath createSupplementPath(ISugiyamaEdge replacedEdge, List<ISugiyamaVertex> dummies) {
		SupplementPath supplementPath = new SupplementPath(replacedEdge, dummies);
		this.supplementPaths.add(supplementPath);
		this.removeEdge(replacedEdge);
		return supplementPath;
	}

	@Override
	public void addEdgeCorner(ISugiyamaEdge edge, int x, int y, int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeEdgeCorner(ISugiyamaEdge edge, int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Point> getEdgeCorners(ISugiyamaEdge edge) {
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
	public void setX(ISugiyamaVertex vertex, int x) {
		vertex.setX(x);

	}

	@Override
	public void assignToLayer(ISugiyamaVertex vertex, int layerNum) {
		int layer = Math.max(vertex.getLayer(), 0);
		this.layers.get(layer).remove(vertex);

		for (int i = layers.size() - 1; i < layerNum; i++) {
			this.layers.add(new LinkedList<>());
		}

		this.layers.get(layerNum).add(vertex);
		vertex.setLayer(layerNum);
	}

	@Override
	public Set<ISugiyamaEdge> getReversedEdges() {
		Set<ISugiyamaEdge> result = new HashSet<ISugiyamaEdge>();
		for(ISugiyamaEdge edge : this.getEdgeSet()){
			if(isReversed(edge)){
				result.add(edge);
			}
		}
		return result;
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
	public int getHeight() {
		return this.layers.size();
	}

	@Override
	public int getMaxWidth() {
		int max = 0;
		for(List<ISugiyamaVertex> layer: this.layers){
			if(layer.size()>max){
				max = layer.size();
			}
		}
		return max;
	}
	
	@Override
	public int getLayerWidth(int layerN) {
		if(layerN > this.layers.size()){
			return 0;
		}
		return this.layers.get(layerN).size();
	}

	@Override
	public Set<ISugiyamaEdge> getReplacedEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEdgepaths() {
		for (SupplementPath supplementPath : this.supplementPaths) {
			ISugiyamaEdge edge = supplementPath.replacedEdge;
			List<Point> path = edge.getPath().getNodes();

			for (ISugiyamaVertex dummy : supplementPath.getDummyVertices()) {
				path.add(new Point(dummy.getX(), dummy.getY()));
			}

			if (edge.isReversed()) {
				Collections.reverse(path);
			}
		}
	}

	@Override
	public List<LayeredGraph<ISugiyamaVertex, ISugiyamaEdge>> getSubgraphs() {
		return null;
		//TODO really null ?
	}

	

	@Override
	public int getLayerFromVertex(ISugiyamaVertex vertex) {
		return this.getLayer(vertex);
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
	public List<Graph<? extends Vertex, ? extends Edge<? extends Vertex>>> getChildGraphs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addChildGraph(Graph child) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString(){
		String out = "Vertices: {";
		for(ISugiyamaVertex v : this.getVertexSet()){
			out+= v.getName() + ", ";
		}
		out = out.substring(0, out.length()-2);
		out+= "}";
		out+= '\n';
		out+= "Edges:{";
		out+= '\n';
		for(ISugiyamaEdge e : this.getEdgeSet()){
			out+= e.getName() + "[" + e.getSource().getName() +"->"+ e.getTarget().getName()+"] ";
			if(this.isReversed(e)){
				out+="reversed";
			}
			out+=",";
			out+= '\n';
		}
		out=out.substring(0, out.length()-2);
		return out+="}";
	}
	
	/**
	 * A supplement path for connecting vertices, which are more than one layer apart.
	 * They are stored in the ISugiyamaEdge along with the substituted edge.
	 */
	public static class SupplementPath {
		private final ISugiyamaEdge replacedEdge;
		private final List<ISugiyamaVertex> dummies;

		public SupplementPath(ISugiyamaEdge replacedEdge, List<ISugiyamaVertex> dummies) {
			this.replacedEdge = replacedEdge;
			this.dummies = dummies;
		}

		/**
		 * Returns the number of vertices including source and target.
		 * @return the length of the path
		 */
		public int getLength() {
			return dummies.size();
		}

		/**
		 * Returns the list of vertices on the path sorted from source to target excluding the source and target.
		 * @return the list of vertices
		 */
		public List<ISugiyamaVertex> getDummyVertices() {
			return this.dummies;
		}

		/**
		 * Returns the edge which is substituted by this path
		 * @return the replaced edge
		 */
		public ISugiyamaEdge getReplacedEdge() {
			return this.replacedEdge;
		}
	}
	/**
	 * A supplement edge which is part of a {@link SupplementPath}.
	 */
	public static class SupplementEdge implements ISugiyamaEdge{
		
		private String name;
		private String label;
		private ISugiyamaVertex source;
		private ISugiyamaVertex target;
		
		public SupplementEdge(String name, String label) {
			this.name=name;
			this.label=label;
		}
		
		public SupplementEdge(String name, String label, ISugiyamaVertex source, ISugiyamaVertex target){
			this(name,label);
			this.source=source;
			this.target=target;
		}

		@Override
		public void setVertices(ISugiyamaVertex source, ISugiyamaVertex target) {
			this.source=source;
			this.target=target;
		}

		@Override
		public ISugiyamaVertex getSource() {
			return this.source;
		}

		@Override
		public ISugiyamaVertex getTarget() {
			return this.target;
		}

		@Override
		public List<ISugiyamaVertex> getVertices() {
			List<ISugiyamaVertex> list = new LinkedList<ISugiyamaVertex>();
			list.add(this.source);
			list.add(this.target);
			return list;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public Integer getID() {
			return -1;
			//TODO check if it is necessary to give a SupplementEdge an ID, maybe better return null instead of -1
		}

		@Override
		public String getLabel() {
			return this.label;
		}

		@Override
		public void addToFastGraphAccessor(FastGraphAccessor fga) {
			// TODO implement!!!
			
		}

		@Override
		public EdgePath getPath() {
			// TODO implement? is necessary ?
			return null;
		}

		@Override
		public void setReversed(boolean b) {
			//does not make sense to reverse a SupplementEdge
		}

		@Override
		public boolean isReversed() {
			return false;
		}

		@Override
		public List<GAnsProperty<?>> getProperties() {
			return new LinkedList<GAnsProperty<?>>();
		}
		
		@Override
		public String toString(){
			return this.name + "(" + this.source.toString() + "->" + this.target.toString() + ")";
		}
	}

	/**
	 * A supplement vertex which is part of a {@link SupplementPath}.
	 */
	public static class DummyVertex extends DefaultVertex implements ISugiyamaVertex {
		
		int layer;
		
		public DummyVertex(String name, String label, int layer) {
			super(name, label);
			this.layer = layer;
		}

		public boolean isDummy() {
			return true;
		}

		@Override
		public int getLayer() {
			return this.layer;
		}

		@Override
		public void setLayer(int layerNum) {
			this.layer=layerNum;
		}
		
		@Override
		public String toString(){
			return this.getName();
		}
	}

	/**
	 * A wrapper class for vertices used in the sugiyama framework.
	 * A ISugiyamaVertex can be a {@link DefaultVertex} or a {@link DummyVertex}
	 */
	public static class SugiyamaVertex implements Vertex, ISugiyamaVertex {
		private final Vertex vertex;
		private int layer;

		public SugiyamaVertex(Vertex vertex, int layer) {
			this.vertex = vertex;
			this.layer = layer;
		}

		public boolean isDummy() {
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
		public List<GAnsProperty<?>> getProperties() {
			return this.vertex.getProperties();
		}
		
		@Override
		public String toString(){
			return this.getName();
		}
	}

	/**
	 * A wrapper class for directed edges to implement additional functionality
	 * to apply the sugiyama layout to the SugiyamaGraph containing them.
	 */
	public static class SugiyamaEdge implements DirectedEdge<ISugiyamaVertex>, ISugiyamaEdge {
		List<Vector<Integer>> corners;
		private boolean isReversed;
		private boolean isSupplement;
		private DirectedEdge<Vertex> wrappedEdge;
		private ISugiyamaVertex source;
		private ISugiyamaVertex target;
		private SupplementPath supplementpath;

		private SugiyamaEdge(DirectedEdge edge) {
			this.wrappedEdge = edge;
			this.isReversed = false;
			this.isSupplement=false;
		}

		//private E getEdge() { return null; }
		/**
		 * Returns true, if this edge has been reversed in order to break cycles in the first step of sugiyama, false otherwise.
		 * @return
		 * 		true if this edge is reversed, false otherwise
		 */
		public boolean isReversed() {
			return this.isReversed;
		}

		/**
		 * Sets this edge to be reversed or not.
		 * @param reversed
		 * 		sets, if this edge is reversed or not
		 */
		public void setReversed(boolean reversed) {
			if (reversed != this.isReversed) {
				ISugiyamaVertex tmp = source;
				source = target;
				target = tmp;
			}

			this.isReversed = reversed;
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
		 * Returns the {@link SupplementPath} which this {@link ISugiyamaEdge} represents.
		 * @return
		 */
		private SupplementPath getSupplementPath() {
			if(this.isSupplement){
				return this.supplementpath;
			}
//			SupplementPath supplement = new SupplementPath();//TODO implement
//			return supplement;
		    return null;
		}
		
		private boolean isSupplementPath(){
			return this.isSupplement;
		}
		
		private void setSupplementPath(int length){
			//this.supplementpath=new SupplementPath(this,length);	//TODO check if it would be wiser to add a constructor to SupplementPath to
			//SupplementPath(String, String, ISugiyamaEdge edge, int length)
		}

		@Override
		public void setVertices(ISugiyamaVertex source, ISugiyamaVertex target) {
			this.source = source;
			this.target = target;
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
		public ISugiyamaVertex getSource() {
			return this.source;
		}

		@Override
		public ISugiyamaVertex getTarget() {
			return this.target;
		}

		@Override
		public List<ISugiyamaVertex> getVertices() {
			List<ISugiyamaVertex> vertices = new LinkedList<ISugiyamaVertex>();
			vertices.add(source);
			vertices.add(target);
			return vertices;
		}

		@Override
		public OrthogonalEdgePath getPath() {
			return (OrthogonalEdgePath) wrappedEdge.getPath();
			//TODO: not good !!!!!!!!!!!!!!!!!!!
			//TODO: really not good !!!!!
		}

		@Override
		public List<GAnsProperty<?>> getProperties() {
			return wrappedEdge.getProperties();
		}
		
		@Override
		public String toString(){
			return this.getName() + "(" + this.source.toString() + "->" + this.target.toString() + ")";
		}
	}
}
