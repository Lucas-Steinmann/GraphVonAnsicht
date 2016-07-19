package edu.kit.student.sugiyama.graph;

import edu.kit.student.graphmodel.*;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.util.IntegerPoint;
import javafx.scene.paint.Color;
import javafx.util.Pair;

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

	private List<Integer> layerPositions;
	private DefaultDirectedGraph<ISugiyamaVertex, ISugiyamaEdge> graph;
	private DirectedGraph wrappedGraph;
	private Set<SupplementPath> supplementPaths = new HashSet<>();
	private DefaultGraphLayering<ISugiyamaVertex> layering;

	/**
	 * Constructs a new SugiyamaGraph and sets the Graph which is the underlying representation.
	 * To fulfill the invariant that all vertices are assigned to a layer, all vertices
	 * will be assigned to layer 0.
	 *
	 * @param graph the graph used as underlying representation.
	 */
	public SugiyamaGraph(DirectedGraph graph)  {

		Map<Integer, ISugiyamaVertex> tmpVertexMap = new HashMap<>();
		HashSet<ISugiyamaVertex> vertices = new HashSet<>();
		HashSet<ISugiyamaEdge> edges = new HashSet<>();
		this.wrappedGraph = graph;

		layering = new DefaultGraphLayering<>(new HashSet<>());
		for (Vertex vertex: graph.getVertexSet()) {
			//TODO: Why -1 and not 0? Does -1 mean an illegal state? Why not just put all vertices on layer 0 at start to achieve an consistent state at all time.
			//      In the statement after this all vertices are set to the starting layer. Isn't this layer 0?
			//SugiyamaVertex sugiyamaVertex = new SugiyamaVertex(vertex, -1);
			SugiyamaVertex sugiyamaVertex = new SugiyamaVertex(vertex);
			vertices.add(sugiyamaVertex);	//fills vertexset with all wrapped vertices
			tmpVertexMap.put(vertex.getID(), sugiyamaVertex);
		}
		for(DirectedEdge edge: graph.getEdgeSet()){
			SugiyamaEdge sugiyamaEdge = new SugiyamaEdge(edge,
					tmpVertexMap.get(edge.getSource().getID()),
					tmpVertexMap.get(edge.getTarget().getID()));
			edges.add(sugiyamaEdge);	//fills edgeset with all wrapped edges
		}
		this.graph = new DefaultDirectedGraph<>(vertices, edges);
		layerPositions = new LinkedList<>();
		layerPositions.add(0);
	}

	public SugiyamaGraph(String name, Set<ISugiyamaVertex> vertices, Set<ISugiyamaEdge> edges) {
		this(new DefaultDirectedGraph<>(vertices, edges));
	}

//	/**
//	 * Replaces the specified edge with a path of dummy vertices of the specified length.
//	 * Replaced edges are removed from the set of edges but saved for later retrieval
//	 * with {@code getReplacedEdges()} or restored with {@code restoreReplacedEdges}.
//	 *
//	 * @param edge the edge to be replaced
//	 * @param length the length of the path which replaces the edge
//	 */
//	private void replaceWithSupplementPath(ISugiyamaEdge edge, int length) {
//		//TODO implement ? or unnecessary ?! edges are yet replaced with createSupplementpath()
//	}

	public Set<SupplementPath> getSupplementPaths() {
		return supplementPaths;
	}

	@Override
	public int getLayerCount() {
		return layering.getLayerCount();
	}

	@Override
	public int getVertexCount(int layerNum) {
		return layering.getVertexCount(layerNum);
	}

	@Override
	public void reverseEdge(ISugiyamaEdge edge) {
		graph.removeEdge(edge);
		edge.reverse();
		graph.addEdge(edge);
	}

	@Override
	public boolean isReversed(ISugiyamaEdge edge) {
		return edge.isReversed();
	}

	@Override
	public void swapVertices(ISugiyamaVertex first, ISugiyamaVertex second) {
		assert (this.getLayer(first)==this.getLayer(second)); //both vertices have to be on the same layer!

		IntegerPoint tmp = layering.getPosition(first);
		layering.setPosition(first, layering.getPosition(second));
		layering.setPosition(second, tmp);
	}

	@Override
	public int getLayer(ISugiyamaVertex vertex) {
		return vertex.getLayer();
	}

	private int getLayerById(int vertexID) {
		ISugiyamaVertex v = this.getVertexByID(vertexID);
		if (v == null) {
			return -1;
		}
		return v.getLayer();
	}

	private ISugiyamaVertex getVertexByID(int vertexID) {
		for (ISugiyamaVertex vertex : this.getVertexSet()) {
			if (vertex.getID() == vertexID) {
				return vertex;
			}
		}
		return null;
	}


	@Override
	public List<ISugiyamaVertex> getLayer(int layerNum) {
		return layering.getLayer(layerNum);
	}

	@Override
	public List<List<ISugiyamaVertex>> getLayers() {
		return layering.getLayers();
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

	public SupplementPath createSupplementPath(ISugiyamaEdge replacedEdge, List<ISugiyamaVertex> dummies, List<ISugiyamaEdge> supplementEdges) {
		SupplementPath supplementPath = new SupplementPath(replacedEdge, dummies, supplementEdges);
		this.supplementPaths.add(supplementPath);
		this.removeEdge(replacedEdge);
		return supplementPath;
	}

	private void removeEdge(ISugiyamaEdge edge) {
		graph.removeEdge(edge);
	}

	private void addEdge(ISugiyamaEdge edge) {
		graph.addEdge(edge);
	}

	private void addVertex(ISugiyamaVertex edge) {
		graph.addVertex(edge);
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
		this.layering.setLayer(vertex, layerNum);
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
	public int getHeight() {
		return layering.getHeight();
	}

	@Override
	public int getMaxWidth() {
		return layering.getMaxWidth();
	}

	@Override
	public int getLayerWidth(int layerN) {
		return layering.getLayerWidth(layerN);
	}

	@Override
	public int getLayerFromVertex(Vertex vertex) {
		return getLayerById(vertex.getID());
	}

	public IntegerPoint getPosition(Vertex vertex) {
		return layering.getPosition(vertex);
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
		private final List<ISugiyamaEdge> supplementEdges;

		public SupplementPath(ISugiyamaEdge replacedEdge, List<ISugiyamaVertex> dummies, List<ISugiyamaEdge> supplementEdges) {
			this.replacedEdge = replacedEdge;
			this.dummies = dummies;
			this.supplementEdges = supplementEdges;
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
		 * Returns the list of supplement edges in the path sorted form source to target.
		 * @return the list of supplement edges
		 */
		public List<ISugiyamaEdge> getSupplementEdges(){
			return this.supplementEdges;
		}

		/**
		 * Returns the edge which is substituted by this path
		 * @return the replaced edge
		 */
		public ISugiyamaEdge getReplacedEdge() {
			return this.replacedEdge;
		}

		/**
		 * Reverses a supplement path by reversing the replaced edge and reversing the order of the dummy vertices on this path.
		 */
		public void reverse(){
			Collections.reverse(this.dummies);
			this.replacedEdge.reverse();
		}
	}
	/**
	 * A supplement edge which is part of a {@link SupplementPath}.
	 */
	public class SupplementEdge implements ISugiyamaEdge {

		private String name;
		private String label;
		private ISugiyamaVertex source;
		private ISugiyamaVertex target;
		private boolean isReversed;
		private EdgePath path;

		public SupplementEdge(String name, String label) {
			this.name=name;
			this.label=label;
			this.path = new OrthogonalEdgePath();
		}

		public SupplementEdge(String name, String label, ISugiyamaVertex source, ISugiyamaVertex target) {
			this(name,label);
			this.source=source;
			this.target=target;
			this.isReversed=false;
		}

		// a SupplementEdge is a SupplementEdge
		public boolean isSupplementEdge(){
			return true;
		}

		@Override
		public void setVertices(ISugiyamaVertex source, ISugiyamaVertex target) {
			this.source=source;
			this.target=target;
		}

		@Override
		public DirectedEdge getWrappedEdge() {
			return null;
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
			return this.path;
		}

		/**
		 * Reverses this edge.
		 */
		public void reverse() {
			ISugiyamaVertex tmp = source;
			source = target;
			target = tmp;
			this.isReversed=!this.isReversed;
		}

		@Override
		public boolean isReversed() {
			return this.isReversed;
		}

		@Override
		public List<GAnsProperty<?>> getProperties() {
			return new LinkedList<>();
		}

		@Override
		public String toString(){
			return this.name + "[S" + this.getID() + "](" + this.source.toString() + "->" + this.target.toString() + ")";
		}

		@Override
		public Color getColor() {
			// TODO necessary ?
			return null;
		}

		@Override
		public EdgeArrow getArrowHead() {
			// TODO necessary?
			return null;
		}
	}

	/**
	 * A supplement vertex which is part of a {@link SupplementPath}.
	 */
	public class DummyVertex extends DefaultVertex implements ISugiyamaVertex {

		public DummyVertex(String name, String label, int layer) {
			super(name, label);
			layering.addVertex(this, layer);
		}

		public boolean isDummy() {
			return true;
		}

		@Override
		public int getLayer() {
			return layering.getLayerFromVertex(this);
		}

		@Override
		public void setLayer(int layerNum) {
			layering.setLayer(this, layerNum);
		}

		@Override
		public String toString(){
			return this.getName() + "[D" + this.getID() + "]";
		}
	}

	/**
	 * A wrapper class for vertices used in the sugiyama framework.
	 * A ISugiyamaVertex can be a {@link DefaultVertex} or a {@link DummyVertex}
	 */
	public class SugiyamaVertex implements ISugiyamaVertex {
		private final Vertex vertex;

		public SugiyamaVertex(Vertex vertex) {
			this.vertex = vertex;
			layering.addVertex(this, 0);
		}

		public boolean isDummy() {
			return false;
		}

		public Vertex getVertex() {
			return vertex;
		}

		public int getLayer() {
			return layering.getLayerFromVertex(this);
		}

		public void setLayer(int layer) {
			layering.setLayer(this, layer);
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
			return this.getName() + "[" + this.getID() + "]";
		}

		@Override
		public Pair<Double, Double> getSize() {
			return this.vertex.getSize();
		}

		@Override
		public Color getColor() {
			return this.vertex.getColor();
		}
	}

	/**
	 * A wrapper class for directed edges to implement additional functionality
	 * to apply the sugiyama layout to the SugiyamaGraph containing them.
	 */
	public class SugiyamaEdge implements ISugiyamaEdge {
		List<Vector<Integer>> corners;
		private boolean isReversed;

		// TODO: is this really necessary?
//		private boolean isSupplement;
		private DirectedEdge wrappedEdge;
		private ISugiyamaVertex source;
		private ISugiyamaVertex target;

		private SugiyamaEdge(DirectedEdge edge, ISugiyamaVertex source, ISugiyamaVertex target) {
			this.wrappedEdge = edge;
			this.isReversed = false;
			this.source = source;
			this.target = target;
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
		 * Reverses this edge.
		 */
		public void reverse() {
			ISugiyamaVertex tmp = source;
			source = target;
			target = tmp;
			this.isReversed=!this.isReversed;
		}

		//a SugiyamaEdge is not a SupplementEdge
		public boolean isSupplementEdge(){
			return false;
		}
//		/**
//		 * Returns true, if this edge was replaced by a {@link SupplementPath} that contains source and target vertices and at least one dummy vertex.
//		 * @return
//		 * 		True if this edge is an supplement path, false otherwise
//		 */
//		private boolean isReplaced() {
//			return false;
//		}
//
//		/**
//		 * Returns the {@link SupplementPath} which this {@link ISugiyamaEdge} represents.
//		 * @return
//		 */
//		private SupplementPath getSupplementPath() {
//			if(this.isSupplement){
//				return this.supplementpath;
//			}
////			SupplementPath supplement = new SupplementPath();//TODO implement, if necessary ?
////			return supplement;
//		    return null;
//		}
//
//		private boolean isSupplementPath(){
//			return this.isSupplement;
//		}
//
//		private void setSupplementPath(int length){
//			//this.supplementpath=new SupplementPath(this,length);	//TODO check if it would be wiser to add a constructor to SupplementPath to
//			//SupplementPath(String, String, ISugiyamaEdge edge, int length)
//		}

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
		public void setVertices(ISugiyamaVertex source, ISugiyamaVertex nv) {
			this.source = source;
			this.target = nv;

		}

		@Override
		public String toString(){
			return this.getName() + "[" + this.getID() + "](" + this.source.toString() + "->" + this.target.toString() + ")";
		}

		@Override
		public Color getColor() {
			return this.wrappedEdge.getColor();
		}

		@Override
		public EdgeArrow getArrowHead() {
			return this.wrappedEdge.getArrowHead();
		}

		@Override
		public DirectedEdge getWrappedEdge() {
			return wrappedEdge;
		}
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
	public Integer selfLoopNumberOf(Vertex vertex) {
		return graph.selfLoopNumberOf(vertex);
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

	@Override
	public Set<ISugiyamaEdge> edgesOf(Vertex vertex) {
		return graph.edgesOf(vertex);
	}

	@Override
	public Set<ISugiyamaEdge> outgoingEdgesOf(Vertex vertex) {
		return graph.outgoingEdgesOf(vertex);
	}

	@Override
	public Set<ISugiyamaEdge> incomingEdgesOf(Vertex vertex) {
		return graph.incomingEdgesOf(vertex);
	}

	@Override
	public Set<ISugiyamaEdge> selfLoopsOf(Vertex vertex) {
		return graph.selfLoopsOf(vertex);
	}

	@Override
	public Set<ISugiyamaVertex> getVertexSet() {
		return graph.getVertexSet();
	}

	@Override
	public Set<ISugiyamaEdge> getEdgeSet() {
		return graph.getEdgeSet();
	}

	@Override
	public void setPositionsOnLayer(int layer, List<ISugiyamaVertex> newLayer) {
		int x = 0;
		for (ISugiyamaVertex vertex : newLayer) {
			if (!(this.layering.getLayerFromVertex(vertex) == layer)) {
				throw new IllegalArgumentException("All vertices have to be on the specified layer, when calling setPositionOnLayer");
			}
			this.layering.setPosition(vertex, new IntegerPoint(x, layer));
			x++;
		}
	}
}