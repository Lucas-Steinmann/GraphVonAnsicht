package edu.kit.student.sugiyama.steps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.student.graphmodel.EdgePath;
//import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.sugiyama.graph.IEdgeDrawerGraph;
import edu.kit.student.sugiyama.graph.ISugiyamaEdge;
import edu.kit.student.sugiyama.graph.ISugiyamaVertex;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SupplementPath;
import edu.kit.student.util.DoublePoint;

/**
 * This class takes a directed graph, as a {@link SugiyamaClass}.
 * It removes dummy vertices and reverses previously reversed edges.
 * Afterwards it assigns every edge points it must run through.
 * 
 * @param <G> the type of the directed graph
 * @param <V> the type of the vertices the graph contains
 * @param <E> the type of the directed edges the graph contains
 */
public class EdgeDrawer implements IEdgeDrawer {
	private IEdgeDrawerGraph graph;
	private Set<SupplementPath> paths;
	private Set<ISugiyamaVertex> graphVertices;	//all vertices of the graph, with dummy vertices
	private Set<ISugiyamaEdge> graphEdges;	//all edges of the graph, with supplement edges
	
	private Set<ISugiyamaEdge> sugiEdges; //edges that are not supplementEdges
	private Set<ISugiyamaEdge> selfLoopEdges;	//edges with same source and target vertex
	private Set<ISugiyamaEdge> sameLayerEdges;	//edges that connect two vertices of the same layer, (for now without selfloops!)
	private Set<ISugiyamaEdge> normalEdges;	//edges that are not (supplement edges, selfLoopEdges or sameLayerEdges)
	
	private Set<ISugiyamaVertex> isolatedVertices;	//isolated vertices
	private double[] spaceBetweenLayers;
	private double[] distancePerEdgeInLayer;
	
	private Set<DoublePoint> points = new HashSet<DoublePoint>();
	
	private Map<Integer,int[]> inOutDeg = new HashMap<Integer,int[]>();	//maps vertex id, to an array, containing the indegree on index 0 and outdegree on index 1
	private Map<Integer, List<List<DoublePoint>>> inOutPoints = new HashMap<Integer, List<List<DoublePoint>>>();	//maps vertex id to Lists of incoming and outgoing points
	private Map<Integer, List<List<ISugiyamaVertex>>> inOutVertices = new HashMap<Integer, List<List<ISugiyamaVertex>>>();// maps vertex id to incoming and outgoing vertices
	
    final Logger logger = LoggerFactory.getLogger(EdgeDrawer.class);

	@Override
	public void drawEdges(IEdgeDrawerGraph graph) {
		logger.info("EdgeDrawer.drawEdges():");
		if(graph.getEdgeSet() == null || graph.getEdgeSet().isEmpty() || graph.getVertexSet() == null || graph.getVertexSet().isEmpty()){
			return;
		}
		initialize(graph);	//initializes the graph and it's sets, needed in the whole class!
		sortLayers();	//sorts the vertices in every layer in ascending order of their X-coordinate
		fillInOutDeg();	//fills mapping of vertex to degrees
		fillInOutPoints();	//fills mapping of vertex to points where edges come in or are going out
		fillInOutVertices();	//fills mapping of vertex to incoming and outgoing vertices
		sortInOutVertices();	//sorts the lists of in- and out vertices in ascending order of their X-coordinate
		calcSpaceBetweenLayers();	//fills the array that contains the space between two layers
		calcDistancePerEdgeInLayer();//fills the array that contains the distance between two kinking edges. (if all edges are kinking, the distance between two adjacency edges is constant)
//		test();
		
		this.normalEdges.forEach(edge->this.drawNormalEdge(edge));
		this.sameLayerEdges.forEach(edge->this.drawSameLayerEdge(edge));
		this.selfLoopEdges.forEach(loop->this.drawSelfLoop(loop));
		this.paths.forEach(path->this.drawSupplementPath(path));
		this.graphEdges.stream().filter(edge->edge.isReversed()).forEach(edge->this.reverseEdgePath(edge));	//reverses edge paths from reversed edges
		for(SupplementPath p : this.paths){	//reverses edgepath of replaced edges in supplement paths
			ISugiyamaEdge e = p.getReplacedEdge();
			if(e.isReversed()){
				this.reverseEdgePath(e);
			}
		}
		
//		testEdgePaths();
	}
	
	
//	private void test(){
//		//layer of source layer must be lower than target vertex
//		logger.debug("isolated vertices: " + this.isolatedVertices.size());
//		for(ISugiyamaEdge e : this.graphEdges.stream().filter(edge->!this.selfLoopEdges.contains(edge)).collect(Collectors.toList())){
//			assert(e.getSource().getLayer() < e.getTarget().getLayer());
//		}
//		for(ISugiyamaEdge e : this.selfLoopEdges){
//			logger.debug("loop: "+e.getSource().getID());
//			assert(e.getSource().getLayer() == e.getTarget().getLayer());
//		}
//		for(SupplementPath p: this.paths){
//			assert(p.getReplacedEdge().getSource().getLayer() < p.getReplacedEdge().getTarget().getLayer());
//		}
//		
//		//checks if there are vertices overlapping in an layer
//		for(List<ISugiyamaVertex> list : this.graph.getLayers()){
//			for(int i =0; i<list.size() - 1;i++){
//				ISugiyamaVertex first = list.get(i);
//				ISugiyamaVertex second = list.get(i + 1);
//				assert(first.getX() + first.getSize().getKey() < second.getX());
//			}
//		}
//		
//		//prints vertices with coordinates on every layer
//		for(List<ISugiyamaVertex> list : this.graph.getLayers()){
//			for(ISugiyamaVertex v : list){
//				System.out.print("["+v.getID()+"]("+v.getX()+","+v.getY()+") ");
//			}
//			System.out.print('\n');
//		}
//		
//		//prints map inOutDeg
//		for(int i : this.inOutDeg.keySet()){
//			int[] degs = this.inOutDeg.get(i);
//			System.out.println("ID: "+i+"; vals: "+degs[0]+","+degs[1]);
//		}
//		
//		//tests the order of the supplement edges in supplement paths
//		for(SupplementPath p : this.paths){
//			for(ISugiyamaEdge e : this.getEdgesFromPath(p)){
//				System.out.print("("+e.getSource().getX()+","+e.getSource().getY()+")->("+e.getTarget().getX()+","+e.getTarget().getY()+")");
//			}
//			System.out.print('\n');
//		}
//	}
	
	//tests every edge if its edgepath describes an orthogonal edge
//	private void testEdgePaths(){
//		Set<DoublePoint> points = new HashSet<DoublePoint>();
//		for(ISugiyamaEdge e : this.graphEdges.stream().filter(edge -> !this.selfLoopEdges.contains(edge)).collect(Collectors.toList())){
//			List<DoublePoint> l = e.getPath().getNodes();
//			
//			for(DoublePoint p : l){
//				assert(points.add(p));
//			}
//			
//			DoublePoint first = l.get(0);
//			DoublePoint second;
//			for(int i = 1; i < l.size(); i++){
//				second = l.get(i);
//				assert(dEquals(first.x, second.x) ^ dEquals(first.y,second.y));
//				first=second;
//			}
//		}
//	}

	/**
	 * Sorts the vertices in every layer in ascending order of their X-coordinate.
	 */
	private void sortLayers(){
//		this.graph.getLayers().forEach(list->list.stream().sorted((v1,v2)->Double.compare(v1.getX(),v2.getX())));
		this.graph.getLayers().forEach(list->list.sort((v1,v2)->Double.compare(v1.getX(),v2.getX())));
	}
	
	/**
	 * Fills the mapping of Integer vertex-id to an Integer array.
	 * The array size is 2. In the index 0 is the indegree of this vertex, in the index 1 its outdegree.
	 */
	private void fillInOutDeg(){
		for(ISugiyamaEdge e :this.graphEdges){
			ISugiyamaVertex source = e.getSource();
			ISugiyamaVertex target = e.getTarget();
			if(!this.inOutDeg.containsKey(source.getID())){	//add source vertex to map, if not contained
				this.inOutDeg.put(source.getID(), new int[2]);
			}
			if(!this.inOutDeg.containsKey(target.getID())){	//add target vertex to map, if not contained
				this.inOutDeg.put(target.getID(), new int[2]);
			}
			this.inOutDeg.get(source.getID())[1]++;	//source vertex got one outgoing edge more
			if(!this.selfLoopEdges.contains(e)){	//if edge describes a selfloop just add an outdegree for that vertex, because this edge is drawn under this vertex
				this.inOutDeg.get(target.getID())[0]++;	//target vertex got one incoming edge more
			}
		}
		this.isolatedVertices.forEach(v->this.inOutDeg.put(v.getID(), new int[2]));
	}
	
	/**
	 * Fills mapping of Integer vertex-id to an List.
	 * This lists contains two Lists of Points. 
	 * The first list contains the Points at which edges go into the vertex, the second at which edges go out of this vertex.
	 */
	private void fillInOutPoints(){
		for(ISugiyamaVertex v : this.graphVertices){
			if(this.isolatedVertices.contains(v)){
				List<DoublePoint> inPoints = new LinkedList<DoublePoint>();
				List<DoublePoint> outPoints = new LinkedList<DoublePoint>();
				List<List<DoublePoint>> list = new ArrayList<List<DoublePoint>>(2);
				list.add(inPoints);
				list.add(outPoints);
				this.inOutPoints.put(v.getID(), null);	//an isolated vertex has no incoming or outgoing points
			}else {
				List<DoublePoint> inPoints = this.getInPoints(v);
				List<DoublePoint> outPoints = this.getOutPoints(v);
				List<List<DoublePoint>> list = new ArrayList<List<DoublePoint>>(2);
				list.add(inPoints);
				list.add(outPoints);
				this.inOutPoints.put(v.getID(),list);
			}
		}
	}
	
	/**
	 * Fills mapping of Integer vertex.id to an list that contains two lists of ISugiyamaVertex.
	 * The first list contains vertices that are going in this vertex, the second ones going out of this vertex.
	 */
	private void fillInOutVertices(){
		for(ISugiyamaEdge e : this.graphEdges){
			if(!this.inOutVertices.containsKey(e.getSource().getID())){	// add new entry for source id
				List<List<ISugiyamaVertex>> list = new LinkedList<List<ISugiyamaVertex>>();
				List<ISugiyamaVertex> inVertices = new LinkedList<ISugiyamaVertex>();
				List<ISugiyamaVertex> outVertices = new LinkedList<ISugiyamaVertex>();
				list.add(inVertices);
				list.add(outVertices);
				this.inOutVertices.put(e.getSource().getID(), list);
			}
			if(!this.inOutVertices.containsKey(e.getTarget().getID())){ // add new entry for target id
				List<List<ISugiyamaVertex>> list = new LinkedList<List<ISugiyamaVertex>>();
				List<ISugiyamaVertex> inVertices = new LinkedList<ISugiyamaVertex>();
				List<ISugiyamaVertex> outVertices = new LinkedList<ISugiyamaVertex>();
				list.add(inVertices);
				list.add(outVertices);
				this.inOutVertices.put(e.getTarget().getID(), list);
			}
			this.inOutVertices.get(e.getSource().getID()).get(1).add(e.getTarget());	//source vertex got one outgoing vertex, the target of this edge
//			logger.debug("adding to source "+e.getSource().getID()+": "+ e.getTarget().getID());
			if(!this.selfLoopEdges.contains(e)){	//at a selfloop the vertex just got an outgoing edge, no incoming, the point will be calculated later
				this.inOutVertices.get(e.getTarget().getID()).get(0).add(e.getSource());	//target vertex got one incoming vertex, the source of this edge
//				logger.debug("adding to target "+e.getTarget().getID()+ ": "+ e.getSource().getID());
			}
		}
	}
	
	/**
	 * Sorts all lists of the mapping of vertex-id to in- and out vertices in ascending order of their X-coordinate (from left to right)
	 */
	private void sortInOutVertices(){
		for(List<List<ISugiyamaVertex>> list : this.inOutVertices.values()){
			list.get(0).sort((v1,v2)->Double.compare(v1.getX(),v2.getX()));
			list.get(1).sort((v1,v2)->Double.compare(v1.getX(),v2.getX()));
		}
	}
	
	/**
	 * Calculates the space between two layers that are among themselves. 
	 * The calculated space depends on the point of a box of a vertex that has the highest Y-coordinate of the upper layer and the 
	 * vertex with the lowest Y-coordinate of it's box on the lower layer.
	 * The space between these layers is the difference between both points.
	 * The first entry in the array spaceBetweenLayers with index 0 describes the space between layer 0 and 1, and so on.
	 */
	private void calcSpaceBetweenLayers(){
		for(int i = 1; i < graph.getLayerCount(); i++){
			List<ISugiyamaVertex> upper = graph.getLayer(i - 1);
			List<ISugiyamaVertex> lower = graph.getLayer(i);
			double upperLowest = Integer.MIN_VALUE;	//lowest point in upper layer. (point on the bottom of the vertex-box)
			double lowerHighest = Integer.MAX_VALUE;	//highest point in lower layer. (point on top of the vertex-box)
			for(ISugiyamaVertex v : upper){
				if(v.getY() + v.getSize().y > upperLowest){	//need the bottom of this vertex box so add its height to y-value
					upperLowest = v.getY() + v.getSize().y;
				}
			}
			for(ISugiyamaVertex v : lower){
				if(v.getY() < lowerHighest){
					lowerHighest = v.getY();
				}
			}
			this.spaceBetweenLayers[i-1] = lowerHighest - upperLowest;	// coordinate (0,0) is in the corner left on the top. so a vertex further down got greater y-coord
			assert(spaceBetweenLayers[i-1]>0);	//space between layers must be greater than zero
		}
	}
	
	/**
	 * Calculates the minimum distance between two edges going horizontally above each other between two layers. 
	 * If all edges are going horizontally above each other, the distance between two neighbor edges is always the same.
	 */
	private void calcDistancePerEdgeInLayer(){
		int[] layerInOutTest = new int[this.graph.getLayerCount() - 1]; 
		for(int i = 0; i <= graph.getLayerCount() - 2; i++){
			List<ISugiyamaVertex> list = graph.getLayer(i);
//			int layerIn = 0 ;
			int layerOut = 0 ;
			for(ISugiyamaVertex v : list){
				int[] inOut = this.inOutDeg.get(v.getID());
//				layerIn +=  inOut[0];
				layerOut += inOut[1];
			}
			layerInOutTest[i] = layerOut;
//			if(i>0){
//				assert(layerInOutTest[i-1] == layerIn);
//			}
//			TODO: this assertion is no longer possible through selfLoops
			this.distancePerEdgeInLayer[i] = (this.spaceBetweenLayers[i]/(layerOut + 1.0));
		}
	}
	
	private void drawNormalEdge(ISugiyamaEdge edge){
//		logger.debug("drawing: " + edge.getSource().getID()+","+edge.getTarget().getID());
		edge.getPath().clear();	//clears edge path before setting it again
		ISugiyamaVertex source = edge.getSource();
		ISugiyamaVertex target = edge.getTarget();
		EdgePath path = edge.getPath();
		//add here the correct out point of source vertex for this edge and target vertex!
		int index = -1;
		boolean found = false;
		for(ISugiyamaVertex v : this.inOutVertices.get(source.getID()).get(1)){	//searches for the correct Point at the bottom of source
			index++;
			if(v.getID() == target.getID() && !this.points.contains(this.inOutPoints.get(source.getID()).get(1).get(index))){
				found = true;
				break;
			}
		}
//		logger.debug("source index: "+index + "| "+this.inOutPoints.get(source.getID()).get(1).get(index));
		assert(found);
		
		int pointPosition = pointsBeforeVertex(source) + index +1;	//relative Y-position of this edge if it has to kink horizontally. (multiplied by distancePerEdgeLayer)
		double edgeDistances = this.distancePerEdgeInLayer[source.getLayer()];
		double edgeKinkY = pointPosition * edgeDistances;
		DoublePoint sPoint = this.inOutPoints.get(source.getID()).get(1).get(index);
		assert(this.points.add(sPoint));	//sPoint must not be in the graph at all
		path.addPoint(sPoint);
		
		//search here for the Point incoming in the target vertex!
		index = -1;
		found = false;
		for(ISugiyamaVertex v : this.inOutVertices.get(target.getID()).get(0)){	//searches for the correct Point on top of the target
			index++;
			if(v.getID() == source.getID() && !this.points.contains(this.inOutPoints.get(target.getID()).get(0).get(index))){	//between two vertices can exist more than one edege!
				found = true;
				break;
			}
		}
//		logger.debug("target index: "+index + "| " +this.inOutPoints.get(target.getID()).get(0).get(index));
		assert(found);

//		if(!found) {
//			((JoanaEdge) edge.getWrappedEdge()).setEdgeKind(JoanaEdge.EdgeKind.DEBUG);
//		}
		
		DoublePoint tPoint = this.inOutPoints.get(target.getID()).get(0).get(index);
		
		//now draw edge between sPoint and tPoint!!!!!
		if(!dEquals(sPoint.x, tPoint.x)){	//need to kink edge
			double newY = tPoint.y - edgeKinkY;
			DoublePoint t1 = new DoublePoint(sPoint.x, newY);
			DoublePoint t2 = new DoublePoint(tPoint.x, newY);
			assert(this.points.add(t1));
			assert(this.points.add(t2));
			path.addPoint(t1);
			path.addPoint(t2);
		}
		assert(this.points.add(tPoint));	//tPoint must not be in the graph at all
		path.addPoint(tPoint);	//finally add the point where the edge goes into the target vertex
	}
	
	//draw self loops on the bottom of the vertex
	private void drawSelfLoop(ISugiyamaEdge loop){
		loop.getPath().clear();//clears edgepath before setting it again
		assert(this.selfLoopEdges.contains(loop));
		assert(loop.getSource().getID() == loop.getTarget().getID());	//just for being very sure
		ISugiyamaVertex vertex = loop.getSource();	//doesn't matter if source or target
		EdgePath path = loop.getPath();
		
		int index = -1;
		boolean found = false;
		for(ISugiyamaVertex v : this.inOutVertices.get(vertex.getID()).get(1)){	//searches for the correct Point at the bottom of source
			index++;
			if(v.getID() == vertex.getID() && !this.points.contains(this.inOutPoints.get(vertex.getID()).get(1).get(index))){ //there can be more than one self loop
				found = true;
				break;
			}
		}
		assert(found);
		
		double dist;
		DoublePoint out = this.inOutPoints.get(vertex.getID()).get(1).get(index);
		if(index + 1 < this.inOutPoints.get(vertex.getID()).get(1).size()){	//in point is not the point rightmost
			dist = (this.inOutPoints.get(vertex.getID()).get(1).get(index + 1).x - out.x) / 2;
		} else{	//he is the point rightmost
			dist = vertex.getX() + vertex.getSize().x - out.x;
		}
		
		int pointPosition = pointsBeforeVertex(vertex) + index +1;	//relative Y-position of this edge if it has to kink horizontally. (multiplied by distancePerEdgeLayer)
		double edgeDistances = this.distancePerEdgeInLayer[vertex.getLayer()];
		double edgeKinkY = pointPosition * edgeDistances;
		double newY = out.y + edgeKinkY;
		double newX = out.x + dist;
		
		path.addPoint(out);
		DoublePoint t1 = new DoublePoint(out.x, newY);
		DoublePoint t2 = new DoublePoint(newX, newY);
		DoublePoint t3 = new DoublePoint(newX, out.y);
		assert(this.points.add(out));
		assert(this.points.add(t1));
		assert(this.points.add(t2));
		assert(this.points.add(t3));
		path.addPoint(t1);
		path.addPoint(t2);
		path.addPoint(t3);

	}
	
	//draw edges that connect two vertices from the same layer
	private void drawSameLayerEdge(ISugiyamaEdge edge){
		// draw edge if the edge connects two vertices in the same layer
		//first just draw the path normal under the vertices on this layer then project the incoming point down to the bottom of the target vertex
		//not very good nor finished yet!!!!!
		edge.getPath().clear();	//clears edge path before setting it again
		ISugiyamaVertex source = edge.getSource();
		ISugiyamaVertex target = edge.getTarget();
		EdgePath path = edge.getPath();
		//add here the correct out point of source vertex for this edge and target vertex!
		int index = -1;
		boolean found = false;
		for(ISugiyamaVertex v : this.inOutVertices.get(source.getID()).get(1)){	//searches for the correct Point at the bottom of source
			index++;
			if(v.getID() == target.getID() && !this.points.contains(this.inOutPoints.get(source.getID()).get(1).get(index))){
				found = true;
				break;
			}
		}
		assert(found);
		
		int pointPosition = pointsBeforeVertex(source) + index +1;	//relative Y-position of this edge if it has to kink horizontally. (multiplied by distancePerEdgeLayer)
		double edgeDistances = this.distancePerEdgeInLayer[source.getLayer()];
		double edgeKinkY = pointPosition * edgeDistances;
		DoublePoint sPoint = this.inOutPoints.get(source.getID()).get(1).get(index);
		assert(this.points.add(sPoint));	//sPoint must not be in the graph at all
		path.addPoint(sPoint);
		
		//search here for the Point incoming in the target vertex!
		index = -1;
		found = false;
		for(ISugiyamaVertex v : this.inOutVertices.get(target.getID()).get(0)){	//searches for the correct Point on top of the target
			index++;
			if(v.getID() == source.getID() && !this.points.contains(this.inOutPoints.get(target.getID()).get(0).get(index))){	//between two vertices can exist more than one edege!
				found = true;
				break;
			}
		}
		assert(found);

		
		DoublePoint tPoint = this.inOutPoints.get(target.getID()).get(0).get(index);
		DoublePoint projected = new DoublePoint(tPoint.x, tPoint.y + target.getSize().y);	//!!!!! set project target point down on bottom of target vertex !!!!!!!
		
		//now draw edge between sPoint and projected!!!!!
		// search for the y-coordinate one layer below
		List<DoublePoint> targetOut = this.inOutPoints.get(target.getID()).get(1);
		DoublePoint p = null;
		for(int i = 0; i<targetOut.size(); i++){
			p = targetOut.get(i);
			if(dEquals(p.x, projected.x) && dEquals(p.y, projected.y)){	//point, projected from top of vertex on its bottom collided with another point on the bottom
				double dist;
				if(i + 1 < targetOut.size()){
					dist = (targetOut.get(i + 1).x - p.x) / 2.0;
				} else{
					dist = target.getX() + target.getSize().x - p.x;
				}
				projected = new DoublePoint(projected.x + dist, projected.y);
				break;
			}
		}
		
		double newY = target.getLayer() + 1 < graph.getLayerCount() ? this.graph.getLayers().get(target.getLayer() + 1).get(0).getY() - edgeKinkY : projected.y + target.getSize().y + edgeKinkY;
		DoublePoint t1 = new DoublePoint(sPoint.x, newY);
		DoublePoint t2 = new DoublePoint(projected.x, newY);
		assert(this.points.add(t1));
		assert(this.points.add(t2));
		path.addPoint(t1);
		path.addPoint(t2);
		assert(this.points.add(tPoint));	//tPoint must not be in the graph at all
		path.addPoint(projected);	//finally add the point where the edge goes into the target vertex
	}
	
	private void drawSupplementPath(SupplementPath path){
		assert(this.paths.contains(path));
		EdgePath replacedEdgePath = path.getReplacedEdge().getPath();
		replacedEdgePath.clear();
		List<ISugiyamaEdge> sEdges =  path.getSupplementEdges();
		List<DoublePoint> points = new LinkedList<DoublePoint>();
		sEdges.forEach(edge->this.drawNormalEdge(edge));	//sets edgePath for every supplement edge
		sEdges.forEach(edge->points.addAll(edge.getPath().getNodes()));	//puts together all edge paths
		points.forEach(point->replacedEdgePath.addPoint(point));
	}
	
	private int pointsBeforeVertex(ISugiyamaVertex vertex){
		int num = 0;
		for(ISugiyamaVertex v : this.graph.getLayer(vertex.getLayer())){
			if(v.getID() == vertex.getID()){
				break;
			}
			num += this.inOutDeg.get(v.getID())[1];	//#outdeg points exist in this vertex on its bottom
		}
		return num;
	}
	
	/**
	 * Returns the points on top of a vertex-box where the incoming edges go into this vertex.
	 * Points are sorted from left to right.
	 */
	private List<DoublePoint> getInPoints(ISugiyamaVertex vertex){
		List<DoublePoint> points = new LinkedList<DoublePoint>();
		double width = vertex.getSize().x;
		int x = vertex.getX();
		int y = vertex.getY();
		int inDeg = this.inOutDeg.get(vertex.getID())[0];
		for(int i = 1; i <= inDeg; i++){
			points.add(new DoublePoint(x + (i /( inDeg + 1.0)) * width, y));
		}
		return points;
	}
	
	/**
	 * Returns the points on bottom of a vertex-box where the incoming edges go into this vertex.
	 * Points are sorted from left to right.
	 */
	private List<DoublePoint> getOutPoints(ISugiyamaVertex vertex){
		List<DoublePoint> points = new LinkedList<DoublePoint>();
		double width = vertex.getSize().x;
		double height = vertex.getSize().y;
		int x = vertex.getX();
		int y = vertex.getY();
		int outDeg = this.inOutDeg.get(vertex.getID())[1];
		for(int i = 1; i <= outDeg; i++){
			points.add(new DoublePoint(x + (i / (outDeg + 1.0)) * width, y + height));
		}
		return points;
	}
	
	private boolean dEquals(double a, double b){
		return Math.abs(a-b) < Math.pow(10, -6);
	}
	
	
	/**
	 * Reverses the EdgePath of this edge.
	 */
	private void reverseEdgePath(ISugiyamaEdge edge){
		Collections.reverse(edge.getPath().getNodes());
	}
	
	/**
	 * Initializes the graph this class works with and also sets some sets that access is often necessary. 
	 * Also initializes: 
	 */
	private void initialize(IEdgeDrawerGraph graph){
		this.graph = graph;
		this.paths = graph.getSupplementPaths();
		this.graphVertices = this.graph.getVertexSet();	//all graph vertices, with dummy vertices
		this.graphEdges = this.graph.getEdgeSet();	//all graph edges, with supplement edges
		logger.debug("amount: "+graphVertices.size()+","+graphEdges.size());
		
		this.isolatedVertices = new HashSet<ISugiyamaVertex>();
		this.selfLoopEdges = new HashSet<ISugiyamaEdge>();
		this.sameLayerEdges = new HashSet<ISugiyamaEdge>();
//		this.graphVertices.stream().filter(vertex->graph.selfLoopNumberOf(vertex)>0).forEach(vertex->this.selfLoopEdges.addAll(graph.selfLoopsOf(vertex)));//fills selfloops
		this.sugiEdges = this.graphEdges.stream().filter(edge->!edge.isSupplementEdge()).collect(Collectors.toSet()); //edges that are not supplementEdges
		for(ISugiyamaEdge e : this.sugiEdges){
			if(e.getSource().equals(e.getTarget())){	//selfloop
				this.selfLoopEdges.add(e);
			}
			if(e.getSource().getLayer() == e.getTarget().getLayer() && !e.getSource().equals(e.getTarget())){	//source and target on same layer, but no selfloops!
				this.sameLayerEdges.add(e);
			}
		}
		this.normalEdges = this.sugiEdges.stream().filter(edge->!this.selfLoopEdges.contains(edge)&&!this.sameLayerEdges.contains(edge)).collect(Collectors.toSet());//edge that is not supplement edge(because sugi edge is used), selfLoopEdge or sameLayerEdge
		
		this.spaceBetweenLayers = new double[graph.getLayerCount()];
		this.distancePerEdgeInLayer = new double[graph.getLayerCount()];
		this.points.clear();	//necessary ?
		assert(sugiEdges.size() == (selfLoopEdges.size() + sameLayerEdges.size() + normalEdges.size()));
	}
}