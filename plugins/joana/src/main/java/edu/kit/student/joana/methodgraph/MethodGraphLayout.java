package edu.kit.student.joana.methodgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.student.graphmodel.DirectedSupplementEdgePath;
import edu.kit.student.graphmodel.EdgePath;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedEdge;
import edu.kit.student.graphmodel.directed.DefaultDirectedSupplementEdgePath;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.joana.FieldAccess;
import edu.kit.student.joana.FieldAccessGraph;
import edu.kit.student.joana.InterproceduralVertex;
import edu.kit.student.joana.InterproceduralVertex.EdgeDirection;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.parameter.Settings;
import edu.kit.student.plugin.LayoutAlgorithm;
import edu.kit.student.sugiyama.AbsoluteLayerConstraint;
import edu.kit.student.sugiyama.LayerContainsOnlyConstraint;
import edu.kit.student.sugiyama.RelativeLayerConstraint;
import edu.kit.student.sugiyama.SugiyamaLayoutAlgorithm;
import edu.kit.student.sugiyama.steps.LayerAssigner;
import edu.kit.student.util.DoublePoint;
import edu.kit.student.util.IntegerPoint;
import javafx.scene.paint.Color;


/**
 * Implements hierarchical layout with layers for {@link MethodGraph}.
 * This graph contains field access subgraphs.
 */
public class MethodGraphLayout implements LayoutAlgorithm<MethodGraph> {
	
    final Logger logger = LoggerFactory.getLogger(MethodGraphLayout.class);
	private SugiyamaLayoutAlgorithm<MethodGraph> sugiyamaLayoutAlgorithm;
	private int dummyId = -1;
	
	public MethodGraphLayout() {
		this.sugiyamaLayoutAlgorithm = new SugiyamaLayoutAlgorithm<MethodGraph>();
	}

	
	@Override
	public Settings getSettings() {
		return sugiyamaLayoutAlgorithm.getSettings();
	}

	/**
	 * Layouts a single {@link MethodGraph} with the configured settings.
	 * 
	 * @param graph The {@link MethodGraph} to layout.
	 */
	public void layout(MethodGraph graph) {
		logger.info("Graph before: Vertices: "+graph.getVertexSet().size()+", Edges: "+graph.getEdgeSet().size());
		this.layoutFieldAccessGraphs(graph);
		List<FieldAccess> collapsedFAs = graph.collapseFieldAccesses();

	    
	  //create absoluteLayerConstraints
        Set<AbsoluteLayerConstraint> absoluteLayerConstraints = new HashSet<AbsoluteLayerConstraint>();
		Set<LayerContainsOnlyConstraint> layerContainsOnlyConstraints = new HashSet<>();
        
        //create absoluteLayerConstraint for Entry vertex
        if (graph.getVertexSet().contains(graph.getEntryVertex())) {
            Set<Vertex> firstLayer = new HashSet<Vertex>();
            firstLayer.add(graph.getEntryVertex());
            absoluteLayerConstraints.add(new AbsoluteLayerConstraint(firstLayer, 0));
            layerContainsOnlyConstraints.add(new LayerContainsOnlyConstraint(firstLayer, 0));
        }
        
        //create absoluteLayerConstraint for Method Parameters
        Set<Vertex> secondLayer = new HashSet<Vertex>();
        
        //create relativeLayerConstraints
        Set<RelativeLayerConstraint> relativeLayerConstraints = new HashSet<RelativeLayerConstraint>();
        for (JoanaVertex v : graph.getVertexSet()) {
            //check if call node
            if (v.getNodeKind().equals(JoanaVertex.VertexKind.CALL)) {
                Set<Vertex> bottom = this.getParamVerticesOfCall(v, graph);
                Set<Vertex> top = new HashSet<Vertex>();
                top.add(v);
                
                relativeLayerConstraints.add(new RelativeLayerConstraint(top, bottom, true, 1));
            } else if (v.getNodeKind().equals(JoanaVertex.VertexKind.FRMI)
                    || v.getNodeKind().equals(JoanaVertex.VertexKind.FRMO)) {
                secondLayer.add(v);
            }
        }
        
        absoluteLayerConstraints.add(new AbsoluteLayerConstraint(secondLayer, 1));
		layerContainsOnlyConstraints.add(new LayerContainsOnlyConstraint(secondLayer, 1));
        
        LayerAssigner assigner = new LayerAssigner();
        assigner.addRelativeConstraints(relativeLayerConstraints);
        assigner.addAbsoluteConstraints(absoluteLayerConstraints);
		assigner.addLayerContainsOnlyConstraints(layerContainsOnlyConstraints);
	    
        sugiyamaLayoutAlgorithm.setLayerAssigner(assigner);
		sugiyamaLayoutAlgorithm.layout(graph);
		
		logger.info("Graph after: Vertices: "+graph.getVertexSet().size()+", Edges: "+graph.getEdgeSet().size());
		expandFieldAccesses(graph, collapsedFAs);
//		System.out.println("now drawing field access edges new !!!!!!!!!!!!!!!!!!!!!!");
		collapsedFAs.forEach(fa->drawEdgesNew(graph, fa));	//new version
		
		//draws interprocedural vertices of this graph. 
		drawInterproceduralVertices(graph);
	}
	
	/**
	 * Layouts every single FieldAccessGraph.
	 * Also sets the sizes of the vertex representing this FieldAccessGraph appropriate.
	 */
	private void layoutFieldAccessGraphs(MethodGraph graph){
	    SugiyamaLayoutAlgorithm<FieldAccessGraph> fieldAccessSugAlgo = new SugiyamaLayoutAlgorithm<>();
		for(FieldAccess fa : graph.getFieldAccesses()){
			FieldAccessGraph fag = fa.getGraph();
			fieldAccessSugAlgo.layout(fag);
		}
	}
	
	
	//This method initializes everything necessary for drawing every edge in an FieldAccess new.
	//also sets the necessary dummies, supplementEdges, and paths in every layer.
	//For setting dummies: calc how much dummies per layer, position of vertices in this layer then add dummies, and random assign the dummies layers
	//maybe go over every edge that skips layer and search in every layer for a free dummy for assigning!
	/**
	 * Draws all edges contained in a FieldAccess and coming into and going out of a FieldAcces new.
	 * The coordinates of vertices stay the same.
	 */
	private void drawEdgesNew(MethodGraph graph, FieldAccess fa){
		//maybe split more functionality into private methods
		double boxYtop = fa.getY();
		double boxYbottom = fa.getY() + fa.getSize().y;
		List<JoanaVertex> faVertices = new ArrayList<>(fa.getGraph().getVertexSet());
		List<JoanaEdge> faEdges = new ArrayList<>(fa.getGraph().getEdgeSet());
		Set<DirectedEdge> newFAedges = new HashSet<>();	//new edges from outside to the next layer, not describing a path
		faVertices.sort((v1,v2)->Integer.compare(v1.getY(), v2.getY()));	//sort vertices in ascending order of y values
		Map<Integer, List<Vertex>> layerNumToVertices = new HashMap<>();
		Map<Integer, Vertex> edgeToDummy = new HashMap<>();
		Set<JoanaEdge> fromOutEdges = this.getEdgesFromOutside(graph, fa);	//edges from out in representing vertex
		List<Double> layerYvals = new ArrayList<>();
		//add y vals for layers
		layerYvals.add(boxYtop);
		for(Vertex v : faVertices){
			if(getIndex(layerYvals,(double) v.getY()) == -1){
				layerYvals.add((double) v.getY());
			}
		}
		layerYvals.add(boxYbottom);
		layerYvals.sort((d1,d2)->Double.compare(d1, d2));
		for(int i = 0; i < layerYvals.size(); i++){	//fills mapping for each layer
			layerNumToVertices.put(i, new ArrayList<>());
		}
		for(JoanaVertex v : faVertices){	//add field access vertices to mapping of layers
			int index = getIndex(layerYvals, (double)v.getY());
			assert(index != -1);
			layerNumToVertices.get(index).add(v);
		}
		//now add for every layer enough dummies and sort the lists afterwards
		for(JoanaEdge e : faEdges){	//add dummies for normal edges in this field access
			Double start = (double) Math.min(e.getSource().getY(), e.getTarget().getY());
			Double end = (double) Math.max(e.getSource().getY(), e.getTarget().getY());
			int index1 = getIndex(layerYvals, (double)start);
			int index2 = getIndex(layerYvals, (double)end);
			
			assert(index1 != -1 && index2 != -1);	//start and end have to be found in this list!!!
			for(int i = index1 + 1; i < index2; i++){
				Double layerVertexSize = (double) layerNumToVertices.get(i).get(0).getSize().y;//every vertex in the FA got a layer,edges are not from outside!
				layerNumToVertices.get(i).add(new JoanaDummyVertex("","", getDummyID(), new DoublePoint(3, layerVertexSize)));
			}
		}
		for(JoanaEdge e : fromOutEdges){	//add dummy vertices for vertices from outside on every necessary layer
			List<DoublePoint> points = e.getPath().getNodes();
//			System.out.println("box left top: "+"("+fa.getX()+","+boxYtop+")"+", box right bottom: "+"("+(fa.getX()+fa.getSize().x)+","+boxYbottom+"), ");
//			System.out.println("edge: source: "+e.getSource().getLabel()+", target: "+e.getTarget().getLabel());
//			System.out.print("path: ");
//			points.forEach(p->System.out.print("("+p.x+","+p.y+"), "));
//			System.out.print('\n');
			DoublePoint borderDummySize = new DoublePoint(2, 5);//just a simple size for dummies on first or last layer
			//cases: top of box and bottom, in and out of the box.
			Double start = null;
			Double end = null;
			DoublePoint boxCross = null;
			int layerToAddDummy = 0;
			JoanaDummyVertex tempDummy;
			Double yAddition = 0.0;	//adds this value to the new set y-coordinate of the new dummy(another value than 0 just needed on bottom of FA-box)
			//vertex boxes are not accurate so that the entry point in box is the same point as drawn by EdgeDrawer
			//in addition new dummy position is left on top, move them all half its length to the left!
			//on bottom: move them also it's length up!
			if(dEquals(points.get(points.size() - 1).y, boxYtop)){ //edge into box from top
				start = boxYtop;
				end = (double) e.getTarget().getY();
				boxCross = points.get(points.size() - 1);
				layerToAddDummy = 0;	//add to first layer
			}else if(dEquals(points.get(0).y, boxYtop)){//edge out of box from top
				start = boxYtop;
				end = (double) e.getSource().getY();
				boxCross = points.get(0);
				layerToAddDummy = 0;	//add to first layer
			}else if(dEquals(points.get(0).y, boxYbottom)){//edge out of box from bottom
				start = (double) e.getSource().getY();
				end = boxYbottom;
				boxCross = points.get(0);
				layerToAddDummy = layerYvals.size() - 1;	//add to last layer
				yAddition = - borderDummySize.y;
			}else if(dEquals(points.get(points.size() - 1).y, boxYbottom)){//edge into box from bottom
				start = (double) e.getTarget().getY();
				end = boxYbottom;
				boxCross = points.get(points.size() - 1);
				layerToAddDummy = layerYvals.size() - 1;	//add to last layer
				yAddition = - borderDummySize.y;
			}else{
				assert(false);
			}
			tempDummy = new JoanaDummyVertex("", "", getDummyID(), borderDummySize);
			tempDummy.setX((int) (boxCross.x - borderDummySize.x/2));
			tempDummy.setY((int) Math.floor(boxCross.y + yAddition));
			layerNumToVertices.get(layerToAddDummy).add(tempDummy);	//adds new dummy vertex on correct position 
			//add mapping of edge id to dummy that represents the new source or target on top or bottom of box!!!!!!
			edgeToDummy.put(e.getID(), tempDummy);
			//now add dummies on layers where layers are skipped by edges!
			int index1 = getIndex(layerYvals, (double)start);
			int index2 = getIndex(layerYvals, (double)end);
			assert(index1 != -1 && index2 != -1);	//start and end have to be found in this list!!!
			for(int i = index1 + 1; i < index2; i++){
				Double layerVertexSize = (double) layerNumToVertices.get(i).get(0).getSize().y;//every vertex in the FA got a layer,edges are not from outside!
				layerNumToVertices.get(i).add(new JoanaDummyVertex("","", getDummyID(), new DoublePoint(2, layerVertexSize)));
			}
		}
		layerNumToVertices.values().forEach(l->l.sort((v1,v2)->Integer.compare(v1.getX(), v2.getX())));//sort all layers
		
		//now set the coordinates of dummies from layer 1...(max-1)
//		System.out.println("now assigning coordinates to dummies!");
		for(int i = 1; i < layerYvals.size() - 1; i++){	//just need to add coordinates to vertices of layer 1 to (max-1));
			assignCoordinatesToDummiesOnLayer(fa, layerNumToVertices.get(i));//should work so, coordinates are written into the vertices
		}
		//Test for printing all layers
//		for(int i = 0; i < layerNumToVertices.size(); i++){
//			System.out.println("layer:"+i);
//			layerNumToVertices.get(i).forEach(v->System.out.print(v.getID()+","));
//			System.out.print('\n');
//		}
		
		//now build paths, in the correct direction of the edge. edges will be turned later, if necessary
		Set<DirectedSupplementEdgePath> paths =getRandomPaths(fa, newFAedges, fromOutEdges, layerNumToVertices, edgeToDummy, layerYvals);
		
		//now draw edges and adjust their edgepaths
		drawAndAdjustFieldAccessEdges(fa, newFAedges , paths, fromOutEdges, edgeToDummy);
	}
	
	//assigns every vertex with id <0(dummy vertex) in the given list that represents vertices in this layer a x- and y-coordinate
	/**
	 * Assigns every dummy in the given layer a coordinate so that they don't collide with other dummies or normal vertices.
	 * Dummies are identified by a negative id.
	 * Dummies on the first and last layer should'nt be assigned coordinates because their coordinates are set through the points an edge is coming in or 
	 * going out of a FieldAccess
	 * 
	 * @param layer all vertices contained in this layer. Contains at least one normal vertex(id >= 0) and any amount of dummies(id < 0)
	 * @param fa the FieldAccess. Necessary because of its x- and y-coordinates
	 * @return a list of vertices containing the same vertices as given in parameter layer but with every dummy assigned a x- and y-coordinate.
	 * 		also the vertices are sorted in ascending order of their x-coordinate
	 */
	private List<Vertex> assignCoordinatesToDummiesOnLayer(FieldAccess fa, List<Vertex> layer){
		//watch out that the boxes are placed correctly (coordinates are left on top!!!)
		List<Vertex> dummies = layer.stream().filter(v->v.getID() < 0).collect(Collectors.toList());
		if(dummies.isEmpty()){
			return new LinkedList<>();
		}
		int dummyCount = dummies.size();
		DoublePoint dummySize = dummies.get(0).getSize();
		List<Vertex> noDummies = layer.stream().filter(v->v.getID() >= 0).collect(Collectors.toList());
		List<Double> distancePoints = new ArrayList<>();
		distancePoints.add((double) fa.getX());
		for(Vertex v : noDummies){
			distancePoints.add((double) v.getX() - 2*dummySize.x);
			distancePoints.add(v.getX() + v.getSize().x);
		}
		distancePoints.add(fa.getX() + fa.getSize().x - 2*dummySize.x);
		Double freeSpace = 0.0;
		for(int i = 0; i < distancePoints.size(); i+=2){//count the width of total available space in this layer for positioning dummies
			freeSpace += distancePoints.get(i + 1) - distancePoints.get(i);
		}
		freeSpace-=dummySize.x * dummyCount;
		Double distBetweenDummies = freeSpace/(dummyCount + 2 * noDummies.size());//size between two dummies or dummy and left or right border
		int tempDummyCount = dummyCount;
//		System.out.println("Dummies total: " + tempDummyCount+ "; Distance points total: "+distancePoints.size());
		while(tempDummyCount > 0){//assign now every dummy vertex a x- and y-coordinate
//			System.out.println("test;D");
//			if(distancePoints.size() != 0){
//				System.out.println("dummy count: "+tempDummyCount+ "; distance points: "+distancePoints.size());
//				System.out.print("Segments: ");
//				distancePoints.forEach(d->System.out.print(d+", "));
//				System.out.print('\n');
//			}
			boolean adjustedDistancePoints = false;
			for(int i = 0; i < distancePoints.size() - 1; i+=2){//count the width of total available space in this layer for positioning dummies
				assert(distancePoints.size() >= 2);
				Double d1 = distancePoints.get(i);
				Double d2 = distancePoints.get(i+1);
				if(distancePoints.size() > 2 && (d2 - d1 < distBetweenDummies / 2 || d1 > d2)){//space too small, remove this space segment
//					System.out.println("segment to small!");
					distancePoints.remove(i+1);
					distancePoints.remove(i);
					adjustedDistancePoints = true;
				}else if(distancePoints.size() > 2 && d2 - d1 < distBetweenDummies && d2 - d1 > distBetweenDummies / 2){//space for exact one dummy in this segment
//					System.out.println("one in segment!");
					Vertex tmp = dummies.get(tempDummyCount - 1);
//					System.out.println("assigned dummy: "+tmp.getID());
					tmp.setX((int) Math.round(Math.ceil(d1 + (d2 - d1)/2 - tmp.getSize().x/2)));
					tmp.setY(noDummies.get(0).getY());
					tempDummyCount -= 1;
					distancePoints.remove(i+1);
					distancePoints.remove(i);
					adjustedDistancePoints = true;
				}
				if(adjustedDistancePoints){//some segments vanished, now calc spaces new
					freeSpace = 0.0;
					for(int j = 0; j < distancePoints.size() - 1; j+=2){
						freeSpace += distancePoints.get(j + 1) - distancePoints.get(j);
					}
					freeSpace-=dummySize.x * tempDummyCount;
					distBetweenDummies = freeSpace/(tempDummyCount + 2 * noDummies.size());
					break;
				}
			}
			if(!adjustedDistancePoints){//big segment
				if(distancePoints.size() == 2){//just one segment
//					System.out.println("first!");
					Double d1 = distancePoints.get(0);
					Vertex tmp = dummies.get(tempDummyCount - 1);
//					System.out.println("assigned dummy: "+tmp.getID());
					tmp.setX((int) Math.round(Math.ceil(d1+distBetweenDummies)));
					tmp.setY(noDummies.get(0).getY());
					tempDummyCount -= 1;
					distancePoints.remove(0);
					distancePoints.add(0,d1+distBetweenDummies+tmp.getSize().x/2);
				}else if(distancePoints.size() > 2 && distancePoints.size() % 2 == 0){//more than one segment, 
//					System.out.println("second!");
					for(int i = 0; i < distancePoints.size() - 1 && tempDummyCount > 0; i+=2){//draw one dummy in each segment and adjust distances
						Double d1 = distancePoints.get(i);
						Vertex tmp = dummies.get(tempDummyCount - 1);
//						System.out.println("assigned dummy: "+tmp.getID());
						tmp.setX((int) Math.round(Math.ceil(d1+distBetweenDummies)));
						tmp.setY(noDummies.get(0).getY());
						tempDummyCount -= 1;
						//now adjust the first point of this segment
						distancePoints.remove(i);
						distancePoints.add(i,d1+distBetweenDummies+tmp.getSize().x);
					}
//					freeSpace = 0.0;
//					for(int i = 0; i < distancePoints.size() - 1; i+=2){//added one dummy for every segment, now adjust distances!
//						freeSpace += distancePoints.get(i + 1) - distancePoints.get(i);
//					}
//					freeSpace-=dummySize.x * tempDummyCount;
//					distBetweenDummies = freeSpace/(tempDummyCount + 2 * noDummies.size());
				}
			}
			
		}
		//finally sort the layer again according to their x-values
		layer.sort((v1,v2)->Integer.compare(v1.getX(), v2.getX()));
//		System.out.println("layer: ");
//		layer.forEach(v->System.out.print(("["+v.getID()+"]("+v.getX()+","+v.getY()+")size:("+v.getSize().x+"); ")));
//		System.out.print('\n');
		//TODO: check if the following works correctly!
		for(int i =0; i<layer.size() - 1;i++){
			Vertex first = layer.get(i);
			Vertex second = layer.get(i + 1);
//			System.out.println("first: "+first.getID()+", second: "+second.getID());
			assert(first.getX() + first.getSize().x < second.getX());
		}
		return layer;
	}
	
	/**
	 * Builds Paths for every edge in the FieldAccess and edges coming in and going out of a FieldAccess if they skip at least one layer. If so, the edge
	 * is added to a path, dummies are added for every layer the edge skips and SupplementEdges are built that connect the source with the first dummy,
	 * the dummies with each other and the last dummy with the target vertex.
	 * Dummies on each layer are assigned randomly but every dummy just once.
	 * 
	 * 
	 * @param fa the FieldAccess
	 * @param newFAedges an empty set to add edges that replaces an edge coming into or going out of the FieldAccess, because source and target
	 * 		have to be completely in a FieldAccess and mustn't be a vertex out of the FieldAccess
	 * @param fromOutEdges edges coming from out or going into the FieldAccess
	 * @param layerNumToVertices mapping of layer number to vertices contained in this layer
	 * @param edgeToDummy mapping of edge id to the dummy that replaces the vertex of this edge being outside of the FieldAccess
	 * @param layerYvals y-values of all layers
	 * @return a set of all SupplementPath that have been built
	 */
	private Set<DirectedSupplementEdgePath> getRandomPaths(FieldAccess fa, Set<DirectedEdge> newFAedges, Set<JoanaEdge> fromOutEdges, Map<Integer, List<Vertex>> layerNumToVertices, Map<Integer, Vertex> edgeToDummy ,List<Double> layerYvals){
		double boxYtop = fa.getY();
		double boxYbottom = fa.getY() + fa.getSize().y;
		Set<JoanaEdge> faEdges = fa.getGraph().getEdgeSet();
		Set<DirectedSupplementEdgePath> paths = new HashSet<>();
		List<List<Vertex>> tempLayerNumToVertices = new ArrayList<>();
		for(int i = 0; i<layerNumToVertices.size(); i++){	//init temp layering for removing them in it
			List<Vertex> newList = new ArrayList<>();
			layerNumToVertices.get(i).stream().filter(v->v.getID() < 0).forEach(v->newList.add(v));
			tempLayerNumToVertices.add(newList);
		}
		for(JoanaEdge e : faEdges){
			Double start = (double) e.getSource().getY();
			Double end = (double) e.getTarget().getY();
			int upOrDown = start < end ? 1 : -1;
			int index1 = getIndex(layerYvals, start);
			int index2 = getIndex(layerYvals, end);
			assert(index1 != -1 && index2 != -1);	//start and end have to be found in this list!!!
			if(Math.abs(index1 - index2) > 1){	//edge describes a supplementPath
				List<DirectedEdge> supplementEdges = new ArrayList<>();
				List<Vertex> dummies = new ArrayList<>();
				for(int i = index1 + upOrDown; Math.abs(i - index2) > 0; i+=upOrDown){//adds dummies to the list
//					List<Vertex> layerDummies = layerNumToVertices.get(i).stream().filter(v->v.getID() < 0).collect(Collectors.toList());
					List<Vertex> layerDummies = tempLayerNumToVertices.get(i);
					int randomIndex = (int)Math.round(Math.floor(Math.random()*layerDummies.size()));
					dummies.add(layerDummies.get(randomIndex));
					layerDummies.remove(randomIndex);
				}
				supplementEdges.add(new DefaultDirectedEdge<Vertex>("","",e.getSource(), dummies.get(0)));
				for(int i = 0; i < dummies.size() - 1; i++){
					supplementEdges.add(new DefaultDirectedEdge<Vertex>("","",dummies.get(i),dummies.get(i + 1)));
				}
				supplementEdges.add(new DefaultDirectedEdge<Vertex>("","",dummies.get(dummies.size() - 1),e.getTarget()));
				paths.add(new DefaultDirectedSupplementEdgePath(e, dummies, supplementEdges));
			}
		}
		for(JoanaEdge e : fromOutEdges){
			List<DoublePoint> points = e.getPath().getNodes();
			Double start = null;
			Double end = null;
			int upOrDown = 0;	//+1 is down (increase layer), -1 is up(decrease layer)
			Vertex source = null;	//one of these will be a dummy instead of being a
			Vertex target = null;	//source or target because the vertex out of the fa is replaced by a dummy while layouting
			if(dEquals(points.get(points.size() - 1).y, boxYtop)){ //edge into box from top
				start = boxYtop;
				end = (double) e.getTarget().getY();
				upOrDown = 1;
				source = edgeToDummy.get(e.getID());
				target = e.getTarget();
			}else if(dEquals(points.get(0).y, boxYtop)){//edge out of box from top
				start = (double) e.getSource().getY();
				end = boxYtop;
				upOrDown = -1;
				source = e.getSource();
				target = edgeToDummy.get(e.getID());
			}else if(dEquals(points.get(0).y, boxYbottom)){//edge out of box from bottom
				start = (double) e.getSource().getY();
				end = boxYbottom;
				upOrDown = 1;
				source = e.getSource();
				target = edgeToDummy.get(e.getID());
			}else if(dEquals(points.get(points.size() - 1).y, boxYbottom)){//edge into box from bottom
				start = boxYbottom;
				end = (double) e.getTarget().getY();
				upOrDown = -1;
				source = edgeToDummy.get(e.getID());
				target = e.getTarget();
			}else{
				assert(false);
			}
			int index1 = getIndex(layerYvals, (double)start);
			int index2 = getIndex(layerYvals, (double)end);
			assert(index1 != -1 && index2 != -1);	//start and end have to be found in this list!!!
			if(Math.abs(index1 - index2) > 1){	//edge describes a supplementPath
				List<DirectedEdge> supplementEdges = new ArrayList<>();
				List<Vertex> dummies = new ArrayList<>();
				for(int i = index1 + upOrDown; Math.abs(i - index2) > 0; i+=upOrDown){//adds dummies the list
//					List<Vertex> layerDummies = layerNumToVertices.get(i).stream().filter(v->v.getID() < 0).collect(Collectors.toList());
					List<Vertex> layerDummies = tempLayerNumToVertices.get(i);
					int randomIndex = (int)Math.round(Math.floor(Math.random()*layerDummies.size()));
					dummies.add(layerDummies.get(randomIndex));
					layerDummies.remove(randomIndex);
				}
				supplementEdges.add(new DefaultDirectedEdge<Vertex>("","",source, dummies.get(0)));
				for(int i = 0; i < dummies.size() - 1; i++){
					supplementEdges.add(new DefaultDirectedEdge<Vertex>("","",dummies.get(i),dummies.get(i + 1)));
				}
				supplementEdges.add(new DefaultDirectedEdge<Vertex>("","",dummies.get(dummies.size() - 1),target));
				paths.add(new DefaultDirectedSupplementEdgePath(new DefaultDirectedEdge<Vertex>("","",source, target), dummies, supplementEdges));
			}else{//edge from outside goes direct into the first vertex (start layer +1 or last layer - 1), so just build a normal edge!
				assert(Math.abs(index1 - index2) == 1);	//should be so !
				newFAedges.add(new DefaultDirectedEdge<Vertex>("","",source,target));
			}
		}
		return paths;
	}
	
	/**
	 * Finally draws all edges new. Adds all required edges and vertices to sets that are given to the sugiyamaLayoutAlgorithm.
	 * Also adjusts EdgePaths of edges coming into or going out of the FieldAccess because these edges have been replaced by an edge that is completely in the FieldAccess.
	 * Adds the new drawn edgepath to the existing one of the edges.
	 * 
	 * @param fa the FieldAccess
	 * @param newFAedges new edges to add to faedges
	 * @param paths the paths
	 * @param fromOutEdges edges from outside into or out of the FieldAccess
	 * @param edgeToDummy mapping of edge id to the dummy that replaces the vertex being outside of the FieldAccess.
	 */
	private void drawAndAdjustFieldAccessEdges(FieldAccess fa, Set<DirectedEdge> newFAedges, Set<DirectedSupplementEdgePath> paths, Set<JoanaEdge> fromOutEdges, Map<Integer, Vertex> edgeToDummy){
//		this.printGraph(fa.getGraph().getVertexSet(), fa.getGraph().getEdgeSet());
		double boxYtop = fa.getY();
		double boxYbottom = fa.getY() + fa.getSize().y;
		Set<Vertex> vertices = new HashSet<>(fa.getGraph().getVertexSet());
		Set<DirectedEdge> edges = new HashSet<>();
		edges.addAll(fa.getGraph().getEdgeSet());
		edges.addAll(newFAedges);	//edges from outside going to the next vertex in FA, not describing a path (not skipping a layer)
		for(DirectedEdge edge : newFAedges){
			vertices.add(edge.getSource());
			vertices.add(edge.getTarget());
		}
		for(DirectedSupplementEdgePath p : paths){
//			DirectedEdge e = p.getReplacedEdge();
//			System.out.println("old from replaced vertices: "+e.getID()+", source: "+e.getSource().getID()+", target: "+e.getTarget().getID());
			vertices.addAll(p.getDummyVertices());
			vertices.add(p.getReplacedEdge().getSource());
			vertices.add(p.getReplacedEdge().getTarget());
			edges.addAll(p.getSupplementEdges());
			edges.remove(p.getReplacedEdge());//should not be in here !
//			this.printGraph(p.getDummyVertices(), p.getSupplementEdges());
		}
//		System.out.println("vertices: "+vertices.size()+", edges: "+edges.size()+", paths: "+paths.size());
//		this.printGraph(vertices, edges);
		this.sugiyamaLayoutAlgorithm.drawEdgesNew(vertices, edges, paths);
		//now add the additional EdgePath to fromOutEdges
		for(JoanaEdge e : fromOutEdges){
//			System.out.println("from out id: "+e.getID()+", source: "+e.getSource().getID()+", target: "+e.getTarget().getID());
			List<DoublePoint> points = e.getPath().getNodes();
			Vertex dummy = edgeToDummy.get(e.getID());
//			System.out.println("dummy id: "+dummy.getID());
			assert(vertices.contains(dummy));
			Vertex source = null;
			Vertex target = null;
			boolean newPathInsertionAfter = false;	//tells if new path should be inserted behind or before the actual path
			if(dEquals(points.get(points.size() - 1).y, boxYtop)){ //edge into box from top
				source = dummy;
				target = e.getTarget();
				newPathInsertionAfter = true;
			}else if(dEquals(points.get(0).y, boxYtop)){//edge out of box from top
				source = e.getSource();
				target = dummy;
				newPathInsertionAfter = false;
			}else if(dEquals(points.get(0).y, boxYbottom)){//edge out of box from bottom
				source = e.getSource();
				target = dummy;
				newPathInsertionAfter = false;
			}else if(dEquals(points.get(points.size() - 1).y, boxYbottom)){//edge into box from bottom
				source = dummy;
				target = e.getTarget();
				newPathInsertionAfter = true;
			}else{
				assert(false);
			}
			for(DirectedSupplementEdgePath p : paths){	//look here for matching and finding correct drawn edges for every fromOutEdge!!!
				DirectedEdge replaced = p.getReplacedEdge();
//				System.out.println("replaced id:"+replaced.getID()+", source: "+replaced.getSource().getID()+", target: "+replaced.getTarget().getID());
				if(replaced.getSource().getID().equals(dummy.getID()) || replaced.getTarget().getID().equals(dummy.getID())){
//					System.out.println("found some!!!!!!!!!");
					DoublePoint newPoint;
					EdgePath op = e.getPath();
					EdgePath np = replaced.getPath();
					if(newPathInsertionAfter){
						newPoint = new DoublePoint(op.getNodes().get(op.getNodes().size() - 1).x, np.getNodes().get(0).y);
						op.addPoint(newPoint);
						np.getNodes().forEach(point->op.addPoint(point));
//						np.getNodes().forEach(point->e.getPath().addPoint(point));// insert new points behind
					} else{
						List<DoublePoint> newPoints = new LinkedList<>();
						newPoint = new DoublePoint(np.getNodes().get(np.getNodes().size() - 1).x, op.getNodes().get(0).y);
						newPoints.addAll(np.getNodes());
						newPoints.add(newPoint);
						newPoints.addAll(points);
						e.getPath().clear();
						newPoints.forEach(point->op.addPoint(point));
					}
				}
			}
			for(DirectedEdge de : edges){
				if(dummy.getID().equals(de.getSource().getID()) && target.getID().equals(de.getTarget().getID())
						|| dummy.getID().equals(de.getTarget().getID()) && source.getID().equals(de.getSource().getID())){
//					System.out.println("found match!");
					DoublePoint newPoint;
					EdgePath op = e.getPath();
					EdgePath np = de.getPath();
					if(newPathInsertionAfter){
						newPoint = new DoublePoint(op.getNodes().get(op.getNodes().size() - 1).x, np.getNodes().get(0).y);
						op.addPoint(newPoint);
						np.getNodes().forEach(point->op.addPoint(point));
//						np.getNodes().forEach(point->e.getPath().addPoint(point));// insert new points behind
					} else{
						List<DoublePoint> newPoints = new LinkedList<>();
						newPoint = new DoublePoint(np.getNodes().get(np.getNodes().size() - 1).x, op.getNodes().get(0).y);
						newPoints.addAll(np.getNodes());
						newPoints.add(newPoint);
						newPoints.addAll(points);
						e.getPath().clear();
						newPoints.forEach(point->op.addPoint(point));
					}
				}
			}
		}
	}
	
	/**
	 * Draws every InterproceduralVertex of this MethodGraph.
	 * There is enough space right from every vertex that is connected with InterproceduralVertices 
	 * IVs of a vertex are drawn as following: (Example of a vertex wit 3 InterproceduralVertices)
	 *  ____
	 * |	|	__	  __	__
	 * |____|  |__|  |__|  |__|  
	 * 
	 * Edges will be drawn horizontally, leaving the Vertex on the right side and going vertically into the IVs to top side.
	 * 
	 */
	private void drawInterproceduralVertices(MethodGraph mg){
		//TODO: watch out that filtering still works (need possibility for just drawing some IVs of a vertex -> look at FAs and their way!)
		//TODO: draw here the interprocedural vertices of a MethodGraph
		Map<Integer,Set<InterproceduralVertex>> idToIVs = mg.getInterprocVertices();
		Set<JoanaVertex> vertices = mg.getVertexSet();
		for(JoanaVertex v : vertices){
			if(idToIVs.containsKey(v.getID())){
				System.out.println("Vertex " + v.getName() + " : pos("+v.getX()+","+v.getY()+"), size("+v.getSize().x+","+v.getSize().y+"), LR-Margin("+v.getLeftRightMargin().x+","+v.getLeftRightMargin().y+")");
				Set<InterproceduralVertex> ivs = idToIVs.get(v.getID()); //IVs from this vertex
				HashSet<JoanaEdge> newEdges = new HashSet<JoanaEdge>();//new edges, created through InterproceduralVertices
				double edgeDist = (v.getSize().y - ivs.iterator().next().getSize().y) / (ivs.size()+1);//vertical distance of 2 edges (asserting that all IVs have the same size)
				int xVal = (int) Math.floor(v.getX()+v.getSize().x + v.getLeftRightMargin().x); //end of right margin of the vertex. Take left margin because its the same like the old right margin of this vertex
				//set the IV's position, also create an edge between it and the vertex and draw the edge (add points to its EdgePath)
				int idx = 1; //number of IV in the loop. First one has number 1! 
				for(InterproceduralVertex iv : ivs){
					xVal += iv.getLeftRightMargin().x;//starting position x of new vertex
					iv.setX(xVal);
					iv.setY((int) Math.floor(v.getY() + v.getSize().y - iv.getSize().y));//so the IV's bottom are at the same level as the vertex's bottom
					System.out.println("IV " + iv.getName()+" : pos("+iv.getX()+","+iv.getY()+"), size("+iv.getSize().x+","+iv.getSize().y+"), LR-Margin("+iv.getLeftRightMargin().x+","+iv.getLeftRightMargin().y+")");
					xVal += iv.getSize().x + iv.getLeftRightMargin().y;//set to the end of the actual IV's right margin
					JoanaEdge newEdge;
					//the IV itself represents the little dummy. IV.dummy is not needed for that!
					if(iv.getEdgeDirection() == EdgeDirection.TO){//IV is target
						newEdge = new JoanaEdge("","",iv.getConnectedVertex(),iv,iv.getEdgeKind());
					}else{
						newEdge = new JoanaEdge("","",iv,iv.getConnectedVertex(),iv.getEdgeKind());
					}
					EdgePath path = newEdge.getPath();
					path.addPoint(new DoublePoint(v.getX()+v.getSize().x,iv.getY() - idx*edgeDist));//first point: on the Vertex's right side
					path.addPoint(new DoublePoint(iv.getX() + iv.getSize().x / 2,iv.getY() - idx*edgeDist));//second point: above the middle of the IV's top
					path.addPoint(new DoublePoint(iv.getX() + iv.getSize().x / 2,iv.getY())); //third point: middle on top of the IV's top
					System.out.println("path: "+path.getNodes().get(0).toString() + " | " + path.getNodes().get(1).toString() + " | " + path.getNodes().get(2).toString());
					if(iv.getEdgeDirection() == EdgeDirection.FROM){//wrong direction: turn EdgePath
						Collections.reverse(path.getNodes());
					}
					newEdges.add(newEdge);
					idx++;
				}
				//finally add new vertices and edges to the graph's vertex- and edgeset
				mg.addInterproceduralEdges(newEdges);
			}
		}
	}
	
	/**
	 * Get index of value in list.
	 * Index 0 is the first index.
	 * -1 is returned, if value is not contained in list.
	 */
	private int getIndex(List<Double> list, Double value){
		int index = 0;
		for(Double d : list){
			if(dEquals(d, value)){
				return index;
			}
			index++;
		}
		return -1;
	}
	
//	private void printGraph(Set<Vertex> vertices, Set<DirectedEdge> edges){
//		String res = "Vertices:";
//		for(Vertex v : vertices){
//			res += v.getID() +", ";
//		}
//		System.out.println(res);
//		res="Edges: ";
//		for(DirectedEdge e : edges){
//			res += e.getSource().getID()+"->"+e.getTarget().getID()+", ";
//		}
//		System.out.println(res);
//	}
//	
//	private void printGraph(List<Vertex> vertices, List<DirectedEdge> edges){
//		String res = "Vertices:";
//		for(Vertex v : vertices){
//			res += v.getY() +", ";
//		}
//		System.out.println(res);
//		res="Edges: ";
//		for(DirectedEdge e : edges){
//			res += e.getSource().getY()+"->"+e.getTarget().getY()+", ";
//		}
//		System.out.println(res);
//	}
	
	private int getDummyID(){
		int temp = this.dummyId;
		dummyId--;
		return temp;
	}
	
	
	
	
	private Set<JoanaEdge> getEdgesFromOutside(MethodGraph graph, FieldAccess fa){
		Set<JoanaEdge> fromOutEdges = new HashSet<>();
		Set<JoanaEdge> graphEdges = graph.getEdgeSet();
		Set<JoanaVertex> graphVertices = graph.getVertexSet();
		Set<JoanaVertex> faVertices = fa.getGraph().getVertexSet();
		for(JoanaEdge e : graphEdges){
			if((graphVertices.contains(e.getSource()) && faVertices.contains(e.getTarget()) && !faVertices.contains(e.getSource()))
					|| (graphVertices.contains(e.getTarget()) && faVertices.contains(e.getSource()) && !faVertices.contains(e.getTarget()))){
				fromOutEdges.add(e);
			}
		}
//		logger.debug("from out edges: "+fromOutEdges.size());
//		logger.debug("box pos: "+fa.getX()+","+fa.getY()+" size: "+fa.getSize().x+","+fa.getSize().y);
//		for(JoanaVertex v : faVertices){
//			logger.debug("vertex pos: "+v.getX()+","+v.getY()+" size: "+v.getSize().x+","+v.getSize().y);
//		}
		return fromOutEdges;
	}
	
	private List<FieldAccess> expandFieldAccesses(MethodGraph graph, List<FieldAccess> fasToExpand) {

		// Fill a map from id to edge for all outgoing edges of field accesses to match them later
		// with the new edges of the graph to the specific vertices in the field access
		Map<Integer, JoanaEdge> idToEdge = new HashMap<>();
		for (FieldAccess fa : fasToExpand) {
		    for (JoanaEdge e : graph.edgesOf(fa)) {
		        idToEdge.put(e.getID(), e);
		    }
		}

		List<FieldAccess> fas = graph.expandFieldAccesses(fasToExpand);
		Set<JoanaEdge> copied = new HashSet<>();

		for (FieldAccess fa : fas) {

		    // Offset vertices contained in field access
            for(JoanaVertex v : fa.getGraph().getVertexSet()){
                v.setX(v.getX() + fa.getX() + (int)FieldAccessGraph.paddingx/2);
                v.setY(v.getY() + fa.getY() + (int)FieldAccessGraph.paddingy/2);
            }
			for(JoanaEdge e : fa.getGraph().getEdgeSet()){
				List<DoublePoint> points = e.getPath().getNodes();
				List<DoublePoint> newPoints = new LinkedList<>();
				assert(!points.isEmpty());
				points.forEach(p->newPoints.add(new DoublePoint(p.x + fa.getX() + FieldAccessGraph.paddingx/2, 
				                                                p.y + fa.getY() + FieldAccessGraph.paddingy/2)));
				points.clear();
				points.addAll(newPoints);
			}
			for (JoanaVertex v : fa.getGraph().getVertexSet()) {
			    for (JoanaEdge e : graph.edgesOf(v)) {
			        if (!copied.contains(e)) {

                        copied.add(e);
                        Set<JoanaVertex> faVertices = fa.getGraph().getVertexSet();
                        if (!faVertices.contains(e.getTarget()) || !faVertices.contains(e.getSource())) {
                            JoanaEdge matchingEdge = idToEdge.get(e.getID());
                            for (DoublePoint point : matchingEdge.getPath().getNodes()) {
                                e.getPath().addPoint(point);
                            }
                        }
			        }
			    }
			}
        }
		for (JoanaEdge e : graph.getEdgeSet()) {
		    List<DoublePoint> points = e.getPath().getNodes();
		    DoublePoint first = points.get(0);
		    Iterator<DoublePoint> it = points.iterator();
		    it.next();
		    while (it.hasNext()) {
		        DoublePoint p = it.next();
                assert (! first.equals(p));
		    }
		}
		return fas;
	}
	
	
	//private Method to get all param vertices of a joana call vertex
    private Set<Vertex> getParamVerticesOfCall(JoanaVertex call, MethodGraph graph) {
        Set<Vertex> result = new HashSet<Vertex>();
        
        for (JoanaEdge e : graph.outgoingEdgesOf(call)) {
            //check if edge is Control_dep_expr
            if (e.getEdgeKind().equals(JoanaEdge.EdgeKind.CE)) {
                //check if edge is act-in or act-out
                JoanaVertex v = e.getTarget();
                if (v.getNodeKind().equals(JoanaVertex.VertexKind.ACTI)
                        || v.getNodeKind().equals(JoanaVertex.VertexKind.ACTO)) {
                   result.add(v); 
                }
            }
        }
        return result;
    }
    
    private boolean dEquals(double a, double b){
		return Math.abs(a-b) < Math.pow(10, -6);
	}
    
    /**
     * For representing dummy vertices with fixed size and given id.
     */
    private static class JoanaDummyVertex implements Vertex{
    	private int x;
    	private int y;
    	private final int id;
    	private final String name;
    	private final String label;
    	private final DoublePoint size;

    	public JoanaDummyVertex(String name, String label, int id, DoublePoint size){
    		this.name = name;
    		this.label = label;
    		this.id = id;
    		this.size = size;
    	}
    	
		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public Integer getID() {
			return this.id;
		}

		@Override
		public String getLabel() {
			return this.label;
		}

		@Override
		public int getX() {
			return this.x;
		}

		@Override
		public int getY() {
			return this.y;
		}

		@Override
		public void setX(int x) {
			this.x = x;
			
		}

		@Override
		public void setY(int y) {
			this.y = y;
			
		}

		@Override
		public void addToFastGraphAccessor(FastGraphAccessor fga) {
			//not necessary
		}

		@Override
		public List<GAnsProperty<?>> getProperties() {
			// not necessary
			return null;
		}

		@Override
		public DoublePoint getSize() {
			return this.size;
		}
		
		@Override
		public IntegerPoint getLeftRightMargin() {
			return new IntegerPoint(2,2);
		}
		
		@Override
		public void setLeftRightMargin(IntegerPoint newMargin) {
			//don't need this here
		}

		@Override
		public Color getColor() {
			// not necessary
			return null;
		}
    }

}
