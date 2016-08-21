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
		graph.collapseFieldAccesses();

	    
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
		List<FieldAccess> expandedFas = expandFieldAccesses(graph);
		System.out.println("now drawing field access edges new !!!!!!!!!!!!!!!!!!!!!!");
//		expandedFas.forEach(fa->drawFieldAccessEdges(graph,fa));//old version
		expandedFas.forEach(fa->drawEdgesNew(graph, fa));	//new version
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
	
	//method responsible for drawing every edge from out or inside the fieldAccess new
	//calls other private methods 
	private void drawFieldAccessEdges(MethodGraph graph, FieldAccess fa){
		logger.info("FieldAccess: vertices: "+fa.getGraph().getVertexSet().size() + "; edges: "+ fa.getGraph().getEdgeSet().size());
		//!!!draw FA through layouting the complete FA with sugiyama (prob. just last step)
		//problem with giving the sugiyama a whole supplement path as they are defined just in sugiyama
		//add dummy vertices in deep copies of vertex- and edgeset of this original given methodgraph
		
		//draw here every? edge in the FieldAccess new. beginning from the point going to the top or bottom of the representing vertex
		//maybe distinguish between turned edges and not turned ones. (even 2 diff methods possible)
		Set<JoanaEdge> fromOutEdges = this.getEdgesFromOutside(graph, fa);	//edges from out in representing vertex
		Set<JoanaEdge> turnedEdges = this.getTurnedEdges(fa, fromOutEdges);	//turned edges so on top are just incoming and on bottom just outgoing
		Set<JoanaEdge> notTurned = fromOutEdges.stream().filter(e->!turnedEdges.contains(e)).collect(Collectors.toSet());	//not turned edges
		
		turnedEdges.forEach(edge->drawEdge(fa,edge));
		notTurned.forEach(edge->drawEdge(fa,edge));
		turnedEdges.forEach(edge->Collections.reverse(edge.getPath().getNodes()));
	}
	
	//This method initializes everything necessary for drawing every edge in an FieldAccess new.
	//also sets the necessary dummies, supplementEdges, and paths in every layer.
	//For setting dummies: calc how much dummies per layer, position of vertices in this layer then add dummies, and random assign the dummies layers
	//maybe go over every edge that skips layer and search in every layer for a free dummy for assigning!
	private void drawEdgesNew(MethodGraph graph, FieldAccess fa){
		//TODO: split more functionality into private methods
		double boxYtop = fa.getY();
		double boxYbottom = fa.getY() + fa.getSize().y;
		List<JoanaVertex> faVertices = new ArrayList(fa.getGraph().getVertexSet());
		List<JoanaEdge> faEdges = new ArrayList(fa.getGraph().getEdgeSet());
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
			DoublePoint borderDummySize = new DoublePoint(3, 5);//just a simple size for dummies on first or last layer
			//cases: top of box and bottom, in and out of the box.
			Double start = null;
			Double end = null;
			DoublePoint boxCross = null;
			int layerToAddDummy = -1;
			JoanaDummyVertex tempDummy;
			Double yAddition = 0.0;	//adds this value to the new set y-coordinate of the new dummy(another value than 0 just needed on bottom of FA-box)
			//TODO: vertex boxes are not accurate so that the entry point in box is the same point as drawn by EdgeDrawer
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
			tempDummy.setX((int) (boxCross.x - borderDummySize.x/2));	//TODO: do something because an int is expected in setX()
			tempDummy.setY((int) Math.floor(boxCross.y + yAddition));	//TODO: do something because an int is expected in setY(), also round it to not be underneath the FA-box bottom !
			layerNumToVertices.get(layerToAddDummy).add(tempDummy);	//adds new dummy vertex on correct position 
			//add mapping of edge id to dummy that represents the new source or target on top or bottom of box!!!!!!
			edgeToDummy.put(e.getID(), tempDummy);
			//now add dummies on layers where layers are skipped by edges!
			int index1 = getIndex(layerYvals, (double)start);
			int index2 = getIndex(layerYvals, (double)end);
			assert(index1 != -1 && index2 != -1);	//start and end have to be found in this list!!!
			for(int i = index1 + 1; i < index2; i++){
				Double layerVertexSize = (double) layerNumToVertices.get(i).get(0).getSize().y;//every vertex in the FA got a layer,edges are not from outside!
				layerNumToVertices.get(i).add(new JoanaDummyVertex("","", getDummyID(), new DoublePoint(3, layerVertexSize)));
			}
		}
		layerNumToVertices.values().forEach(l->l.sort((v1,v2)->Integer.compare(v1.getX(), v2.getX())));//sort all layers
		
		//now set the coordinates of dummies from layer 1...(max-1)
		System.out.println("now assigning coordinates to dummies!");
		for(int i = 1; i < layerYvals.size() - 1; i++){	//just need to add coordinates to vertices of layer 1 to (max-1));
			assignCoordinatesToDummiesOnLayer(fa, layerNumToVertices.get(i));//should work so, coordinates are written into the vertices
		}
		//Test for printing all layers
		for(int i = 0; i < layerNumToVertices.size(); i++){
			System.out.println("layer:"+i);
			layerNumToVertices.get(i).forEach(v->System.out.print(v.getID()+","));
			System.out.print('\n');
		}
		
		//now build paths, in the correct direction of the edge. edges will be turned later, if necessary
		Set<DirectedSupplementEdgePath> paths =getRandomPaths(fa, newFAedges, fromOutEdges, layerNumToVertices, edgeToDummy, layerYvals);
		
		//now draw edges and adjust their edgepaths
		drawAndAdjustFieldAccessEdges(fa, newFAedges , paths, fromOutEdges, edgeToDummy);
	}
	
	private void drawAndAdjustFieldAccessEdges(FieldAccess fa, Set<DirectedEdge> newFAedges, Set<DirectedSupplementEdgePath> paths, Set<JoanaEdge> fromOutEdges, Map<Integer, Vertex> edgeToDummy){
		//TODO: look whether the right vertices and edges are contained here!
//		this.printGraph(fa.getGraph().getVertexSet(), fa.getGraph().getEdgeSet());
		double boxYtop = fa.getY();
		double boxYbottom = fa.getY() + fa.getSize().y;
		Set<Vertex> vertices = new HashSet<>();
		Set<DirectedEdge> edges = new HashSet<>();
		vertices.addAll(fa.getGraph().getVertexSet());
		edges.addAll(fa.getGraph().getEdgeSet());
		edges.addAll(newFAedges);	//edges from outside going to the next vertex in FA, not describing a path (not skipping a layer)
		for(DirectedEdge edge : newFAedges){
			vertices.add(edge.getSource());
			vertices.add(edge.getTarget());
		}
		for(DirectedSupplementEdgePath p : paths){
			DirectedEdge e = p.getReplacedEdge();
			System.out.println("old from replaced vertices: "+e.getID()+", source: "+e.getSource().getID()+", target: "+e.getTarget().getID());
			vertices.addAll(p.getDummyVertices());
			vertices.add(p.getReplacedEdge().getSource());
			vertices.add(p.getReplacedEdge().getTarget());
			edges.addAll(p.getSupplementEdges());
			edges.remove(p.getReplacedEdge());//should not be in here !
//			this.printGraph(p.getDummyVertices(), p.getSupplementEdges());
		}
//		System.out.println("vertices: "+vertices.size()+", edges: "+edges.size()+", paths: "+paths.size());
		this.printGraph(vertices, edges);
		this.sugiyamaLayoutAlgorithm.drawEdgesNew(vertices, edges, paths);
		//now add the additional EdgePath to fromOutEdges
		for(JoanaEdge e : fromOutEdges){
			System.out.println("from out id: "+e.getID()+", source: "+e.getSource().getID()+", target: "+e.getTarget().getID());
			List<DoublePoint> points = e.getPath().getNodes();
			Vertex dummy = edgeToDummy.get(e.getID());
			System.out.println("dummy id: "+dummy.getID());
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
			for(DirectedSupplementEdgePath p : paths){	//TODO: look here for matching and finding correct drawn edges for every fromOutEdge!!!
				DirectedEdge replaced = p.getReplacedEdge();
//				System.out.println("replaced id:"+replaced.getID()+", source: "+replaced.getSource().getID()+", target: "+replaced.getTarget().getID());
				if(replaced.getSource().getID().equals(dummy.getID()) || replaced.getTarget().getID().equals(dummy.getID())){//TODO: think!!!!!!!!!!!!
					System.out.println("found some!!!!!!!!!");
					if(newPathInsertionAfter){
						replaced.getPath().getNodes().forEach(point->e.getPath().addPoint(point));// insert new points behind
					} else{
						List<DoublePoint> newPoints = new LinkedList<>();
						newPoints.addAll(replaced.getPath().getNodes());
						newPoints.addAll(points);
						e.getPath().clear();
						newPoints.forEach(point->e.getPath().addPoint(point));
					}
				}
			}
			for(DirectedEdge de : edges){
				if(dummy.getID().equals(de.getSource().getID()) && target.getID().equals(de.getTarget().getID())
						|| dummy.getID().equals(de.getTarget().getID()) && source.getID().equals(de.getSource().getID())){
					System.out.println("found match!");
					if(newPathInsertionAfter){
						de.getPath().getNodes().forEach(p->e.getPath().addPoint(p));// insert new points behind
					} else{
						List<DoublePoint> newPoints = new LinkedList<>();
						newPoints.addAll(de.getPath().getNodes());
						newPoints.addAll(points);
						e.getPath().clear();
						newPoints.forEach(p->e.getPath().addPoint(p));
					}
				}
			}
		}
	}
	
	//assigns every vertex with id <0(dummy vertex) in the given list that represents vertices in this layer a x- and y-coordinate
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
			distancePoints.add((double) v.getX());
			distancePoints.add(v.getX() + v.getSize().x);
		}
		distancePoints.add(fa.getX() + fa.getSize().x);
		Double freeSpace = 0.0;
		for(int i = 0; i < distancePoints.size(); i+=2){//count the width of total available space in this layer for positioning dummies
			freeSpace += distancePoints.get(i + 1) - distancePoints.get(i);
		}
		freeSpace-=dummySize.x * dummyCount;
		Double distBetweenDummies = freeSpace/(dummyCount + 2 * noDummies.size());//size between two dummies or dummy and left or right border
		int tempDummyCount = dummyCount;
		System.out.println("Dummies total: " + tempDummyCount+ "; Distance points total: "+distancePoints.size());
		while(tempDummyCount > 0){//assign now every dummy vertex a x- and y-coordinate
//			System.out.println("test;D");
			if(distancePoints.size() != 0){
				System.out.println("dummy count: "+tempDummyCount+ "; distance points: "+distancePoints.size());
				System.out.print("Segments: ");
				distancePoints.forEach(d->System.out.print(d+", "));
				System.out.print('\n');
			}
			boolean adjustedDistancePoints = false;
			for(int i = 0; i < distancePoints.size() - 1; i+=2){//count the width of total available space in this layer for positioning dummies
				assert(distancePoints.size() >= 2);
				Double d1 = distancePoints.get(i);
				Double d2 = distancePoints.get(i+1);
				if(distancePoints.size() > 2 && d2 - d1 < distBetweenDummies / 2){//space too small, remove this space segment
					System.out.println("segment to small!");
					distancePoints.remove(i+1);
					distancePoints.remove(i);
					adjustedDistancePoints = true;
				}else if(distancePoints.size() > 2 && d2 - d1 < distBetweenDummies && d2 - d1 > distBetweenDummies / 2){//space for exact one dummy in this segment
					System.out.println("one in segment!");
					Vertex tmp = dummies.get(tempDummyCount - 1);
					System.out.println("assigned dummy: "+tmp.getID());
					tmp.setX((int) Math.round(Math.ceil(d1 + (d2 - d1)/2 - tmp.getSize().x/2)));//TODO: casting problem ?
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
//				System.out.println("got here!");
				if(distancePoints.size() == 2){//just one segment
					System.out.println("first!");
					Double d1 = distancePoints.get(0);
					Double d2 = distancePoints.get(1);
					Vertex tmp = dummies.get(tempDummyCount - 1);
					System.out.println("assigned dummy: "+tmp.getID());
					tmp.setX((int) Math.round(Math.ceil(d1+distBetweenDummies)));//TODO: casting problem ?
					tmp.setY(noDummies.get(0).getY());
					tempDummyCount -= 1;
					distancePoints.remove(0);
					distancePoints.add(0,d1+distBetweenDummies+tmp.getSize().x/2);
				}else if(distancePoints.size() > 2 && distancePoints.size() % 2 == 0){//more than one segment, 
					System.out.println("second!");
					for(int i = 0; i < distancePoints.size() - 1 && tempDummyCount > 0; i+=2){//draw one dummy in each segment and adjust distances
						Double d1 = distancePoints.get(i);
						Double d2 = distancePoints.get(i+1);
						Vertex tmp = dummies.get(tempDummyCount - 1);
						System.out.println("assigned dummy: "+tmp.getID());
						tmp.setX((int) Math.round(Math.ceil(d1+distBetweenDummies)));//TODO: casting problem ?, calc right ??? maybe d1+distBetweenDummies
						//old val for x: d1 + (d2 - d1)/2 - tmp.getSize().x/2)
						tmp.setY(noDummies.get(0).getY());
						tempDummyCount -= 1;
						//now adjust the first point of this segment
						distancePoints.remove(i);
						distancePoints.add(i,d1+distBetweenDummies+tmp.getSize().x/2);//TODO: distance calc correct ?d1+distBetweenDummies+tmp.getSize/2
						//old val: d1 + distBetweenDummies
					}
					freeSpace = 0.0;
					for(int i = 0; i < distancePoints.size() - 1; i+=2){//added one dummy for every segment, now adjust distances!
						freeSpace += distancePoints.get(i + 1) - distancePoints.get(i);
					}
					freeSpace-=dummySize.x * tempDummyCount;
					distBetweenDummies = freeSpace/(tempDummyCount + 2 * noDummies.size());
				}
			}
			
		}
		//finally sort the layer again according to their x-values
		layer.sort((v1,v2)->Integer.compare(v1.getX(), v2.getX()));
		System.out.println("layer: ");
		layer.forEach(v->System.out.print(("["+v.getID()+"]("+v.getX()+","+v.getY()+")size:("+v.getSize().x+"); ")));
		System.out.print('\n');
		return layer;
	}
	
	//TODO: there are build too less paths !?
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
				supplementEdges.add(new DefaultDirectedEdge("","",e.getSource(), dummies.get(0)));
				for(int i = 0; i < dummies.size() - 1; i++){
					supplementEdges.add(new DefaultDirectedEdge("","",dummies.get(i),dummies.get(i + 1)));
				}
				supplementEdges.add(new DefaultDirectedEdge("","",dummies.get(dummies.size() - 1),e.getTarget()));
				paths.add(new DefaultDirectedSupplementEdgePath(e, dummies, supplementEdges));
			}
		}
		for(JoanaEdge e : fromOutEdges){//TODO: check if every dummy in first and last layer is used in any edge!!!!!
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
				//TODO: source is not edge.source but the point on top of fa-box!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				supplementEdges.add(new DefaultDirectedEdge("","",source, dummies.get(0)));
				for(int i = 0; i < dummies.size() - 1; i++){
					supplementEdges.add(new DefaultDirectedEdge("","",dummies.get(i),dummies.get(i + 1)));
				}
				supplementEdges.add(new DefaultDirectedEdge("","",dummies.get(dummies.size() - 1),target));
				paths.add(new DefaultDirectedSupplementEdgePath(new DefaultDirectedEdge("","",source, target), dummies, supplementEdges));
			}else{//edge from outside goes direct into the first vertex (start layer +1 or last layer - 1), so just build a normal edge!
				//TODO
				assert(Math.abs(index1 - index2) == 1);	//should be so !
				newFAedges.add(new DefaultDirectedEdge("","",source,target));
			}
		}
		return paths;
	}
	
	
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
	
	private void printGraph(Set<Vertex> vertices, Set<DirectedEdge> edges){
		String res = "Vertices:";
		for(Vertex v : vertices){
			res += v.getID() +", ";
		}
		System.out.println(res);
		res="Edges: ";
		for(DirectedEdge e : edges){
			res += e.getSource().getID()+"->"+e.getTarget().getID()+", ";
		}
		System.out.println(res);
	}
	
	private void printGraph(List<Vertex> vertices, List<DirectedEdge> edges){
		String res = "Vertices:";
		for(Vertex v : vertices){
			res += v.getY() +", ";
		}
		System.out.println(res);
		res="Edges: ";
		for(DirectedEdge e : edges){
			res += e.getSource().getY()+"->"+e.getTarget().getY()+", ";
		}
		System.out.println(res);
	}
	
	private int getDummyID(){
		int temp = this.dummyId;
		dummyId--;
		return temp;
	}
	
	
	
	
	//y-values of old edges between two layers (two vertices nearby)
	//first layer is between y-value of the box and the first vertex (index 0)
	private Map<Integer, List<Double>> assignedYvalsBetweenLayers(FieldAccess fa){
		double boxYtop = fa.getY();
		double boxYbottom = fa.getY() + fa.getSize().y;
		Map<Integer, List<Double>> map = new HashMap<>();
		List<Double> yvals = new LinkedList<>();
		yvals.add(boxYtop);
		yvals.add(boxYbottom);
		for(JoanaVertex v : fa.getGraph().getVertexSet()){
			yvals.add((double) v.getY());
			yvals.add(v.getY() + v.getSize().y);
		}
		for(JoanaEdge e : fa.getGraph().getEdgeSet()){
			List<DoublePoint> points = e.getPath().getNodes();
			Double start = e.getSource().getY() < e.getTarget().getY() ? e.getSource().getY() + e.getSource().getSize().y: e.getTarget().getY() + e.getTarget().getSize().y;
			Double end = (double) (e.getTarget().getY() > e.getSource().getY() ? e.getTarget().getY() : e.getSource().getY());
			//look here for yet assigned y-vals between layers. through these y-coords no new edge can go through
			//this is the orientation for every new edge!
			int index1 = -1;
			int index2 = -1;
			int i = 0;
			for(Double d : yvals){	//might be not necessary, there are other possibilities to calc layers
									//just with the points of an edgepath!
				if(index1 != -1 && index2 != -1){
					break;
				}
				if(dEquals(d, start)){
					index1 = i;
					continue;
				}
				if(dEquals(d, end)){
					index2 = i;
					continue;
				}
				i++;
			}
			assert((index2 - index1) % 2 == 1);
			//an edgepath between 2 vertices is described by 2 or 4 points
			//4 points: 2 different y vals, 2 points: only one y-value
			assert(points.size() / 2 == 0); //either two or four values should have been added at once
			int index = index1;	//start index, end index is not necessary because we go over the edgepaths till end
			for(int j = 0; j < points.size();){
				if(points.size() - j >= 4){	//look now if edgepath between 2 layers consist of 2 or 4 points
					if(dEquals(points.get(j).x, points.get(j + 1).x)//are these 3 checks enough ?
						&& dEquals(points.get(j + 1).y, points.get(j + 2).y)
						&& dEquals(points.get(j + 2).x, points.get(j + 3).x)){
						if(!map.containsKey(index/2)){
							map.put(index/2, new LinkedList<>());
						}
						map.get(index/2).add(points.get(j + 1).y);	//enough because between 2 layers can only be 1 kink
						j+=4;//because 4 are one layer
					}else if(dEquals(points.get(j).x, points.get(j + 1).x)//enough for check, that the next 4 points 
							&& dEquals(points.get(j + 1).x, points.get(j + 2).x)//describe 2 layerdiffs instead of 1 ?
							&& dEquals(points.get(j + 2).x, points.get(j + 3).x)){
						//don't add a point here because if edgepath between 2 layers has length 2 there is no kink
						j+=2; //because 4 are 2 layer, so 2 are 1 layer
					} else{assert(false);}	//must not occur
				}
				index += 2;	//normally add one, but index will be halved, s add every iteration 2
				assert(points.size() - j >= 2);	//at least 1 point must be after index j (2 points with index j)
				//here for just 2 points left and assigning them to an layer. 
				//without if, because the assertion checks it !!!
			}
		}
		return map;
	}
	
	//edges that have been drawn in layouting that go through at least one layer. 
	//mapped are layer number (index 0 means the first layer...) to the points with y-coordinate of the middle of the vertex box of this layer and the
	//x-coordinate of the edge crossing this y-coordinate section
	private Map<Integer, List<Double>> oldEdgesPointsSkipLayer(FieldAccess fa){
		Map<Integer, List<Double>> map = new HashMap<Integer, List<Double>>();
		Set<JoanaVertex> faVertices = fa.getGraph().getVertexSet();
		Set<JoanaEdge> faEdges = fa.getGraph().getEdgeSet();
		List<Double> doubley = new LinkedList<>();
		for(JoanaVertex v : faVertices){
			doubley.add((double) v.getY());
			doubley.add(v.getY() + v.getSize().y);
		}
		for(JoanaEdge e : faEdges){	//start always above end, even if the edge goes from bottom to the top
			Double start = e.getSource().getY() < e.getTarget().getY() ? e.getSource().getY() + e.getSource().getSize().y: e.getTarget().getY() + e.getTarget().getSize().y;
			Double end = (double) (e.getTarget().getY() > e.getSource().getY() ? e.getTarget().getY() : e.getSource().getY());
			//look whether an edge go through at least one layer, then add mapping if not added yet, otherwise add point or do nothing ;)
			
			int index1 = -1;
			int index2 = -1;
			int i = 0;
			for(Double d : doubley){
				if(index1 != -1 && index2 != -1){
					break;
				}
				if(dEquals(d, start)){
					index1 = i;
					continue;
				}
				if(dEquals(d, end)){
					index2 = i;
					continue;
				}
				i++;
			}
			assert((index2 - index1) % 2 == 1);
			for(int j = index1; j + 1 < index2; j+=2){
				if(index2 - index1 > 2){
					if(!map.containsKey(index1/2)){
						List<Double> list = new LinkedList<Double>();
						map.put(index1/2, list);
					}else{
						map.get(index1/2).add(doubley.get(index1 + 1) + (doubley.get(index1 + 2) - doubley.get(index1 + 1)) / 2);	//middle between both points
					}
				}
			}
		}
		return map;
	}
	
	//vertices that have to be drawn new and skip a certain layer
	private int[] newVerticesSkipLayer(FieldAccess fa, Set<JoanaEdge> fromOutEdges){
		double boxYtop = fa.getY();
		double boxYbottom = fa.getY() + fa.getSize().y;
		int[] result;
		List<Double> yVals = new LinkedList<>(); 
		for(JoanaVertex v : fa.getGraph().getVertexSet()){
			yVals.add((double) v.getY());
			yVals.add(v.getY() + v.getSize().y);
		}
		assert(yVals.size() % 2 == 0);
		result = new int[yVals.size()/2];
		yVals.sort((d1,d2)->Double.compare(d1, d2));
		Double start = null;
		Double end = null;
		for(JoanaEdge e : fromOutEdges){
			List<DoublePoint> points = e.getPath().getNodes();
			if(dEquals(points.get(points.size() - 1).y, boxYtop)){	//draw from top of box down to the target vertex
				start = points.get(points.size() - 1).y;
				JoanaVertex target = e.getTarget().getY() > e.getSource().getY() ? e.getTarget() : e.getSource();
				end = (double) target.getY();
			} else{	//draw from target vertex to bottom 
				JoanaVertex source = e.getSource().getY() < e.getTarget().getY() ? e.getSource() : e.getTarget();
				start = (double) source.getY();
				end = points.get(0).y;
			}
			int index1 = -1;
			int index2 = -1;
			int i = 0;
			for(Double d : yVals){	//looks for the distance between start and end point and therefore the layer numbers where an edge skips this layer.
				if(index1 != -1 && index2 != -1){
					break;
				}
				if(dEquals(d, start)){
					index1 = i;
					continue;
				}
				if(dEquals(d, end)){
					index2 = i;
					continue;
				}
				i++;
			}
			//set here the amount of edges skipping a certain layer
			assert((index2 - index1) % 2 == 1 );
			for(int j = index1; j + 1 < index2; j+=2){
				if(index2 - index1 > 2){
					result[index1 / 2]++;
				}
			}
		}
		return result;
	}
	
	//first list points on top of vertex, second list on its bottom. (lists are sorted from left to right)
	private Map<Integer, List<List<DoublePoint>>> getIdToInOutPoints(FieldAccess fa){
		Map<Integer, List<List<DoublePoint>>> idToLists = new HashMap<>();
		for(JoanaVertex v : fa.getGraph().getVertexSet()){
				List<DoublePoint> in = new LinkedList<>();
				List<DoublePoint> out = new LinkedList<>();
				List<List<DoublePoint>> listlist = new ArrayList<>(2);
				listlist.add(in);
				listlist.add(out);
				idToLists.put(v.getID(), listlist);
		}
		for(JoanaEdge e : fa.getGraph().getEdgeSet()){
			List<DoublePoint> points = e.getPath().getNodes();
			JoanaVertex source = e.getSource();
			JoanaVertex target = e.getTarget();
			// find correct points and correct positions of points (top or bottom) and add them to mapping!
			assert(dEquals(source.getY(), points.get(0).y) || dEquals(source.getY() + source.getSize().x, points.get(0).y));
			assert(dEquals(target.getY(), points.get(points.size() - 1).y) || dEquals(target.getY() + target.getSize().x, points.get(points.size() - 1).y));
			if(dEquals(source.getY(), points.get(0).y)){
				idToLists.get(source.getID()).get(0).add(points.get(0));
			}else{
				idToLists.get(source.getID()).get(1).add(points.get(0));
			}
			if(dEquals(target.getY(), points.get(points.size() - 1).y)){
				idToLists.get(target.getID()).get(0).add(points.get(points.size() - 1));
			}else{
				idToLists.get(target.getID()).get(1).add(points.get(points.size() - 1));
			}
			
		}
		return idToLists;
	}

	private double[] getSpaceBetweenLayers(FieldAccess fa){
		//count differernt layers (not where vertices are, between them!)
		List<Double> temp = new LinkedList<>();
		temp.add((double)fa.getY());
		temp.add(fa.getY() + fa.getSize().y);
		for(JoanaVertex v : fa.getGraph().getVertexSet()){
			temp.add((double)v.getY());
			temp.add(v.getY() + v.getSize().y);
		}
		double[] result = new double[temp.size()/2];
		for(int i = 0; i < temp.size()/2; i++){
			result[i] = temp.get(2*i + 1) - temp.get(2*i);
		}
		return result;
	}
	
	//amount of layers is the amount of spaces between two vertices with a different y-coordinate and laying nearby each other
	private int[] getNewEdgesPerLayer(FieldAccess fa, Set<JoanaEdge> fromOutEdges){
		double boxYtop = fa.getY();
		double boxYbottom = fa.getY() + fa.getSize().y;
		List<Double> temp = new LinkedList<>();
		temp.add((double)fa.getY());
		temp.add(fa.getY() + fa.getSize().y);
		for(JoanaVertex v : fa.getGraph().getVertexSet()){
			temp.add((double)v.getY());
			temp.add(v.getY() + v.getSize().y);
		}
		int[] result = new int[temp.size()/2];
		temp.sort((p1,p2)->Double.compare(p1,p2));
		for(JoanaEdge e : fromOutEdges){
			List<DoublePoint> points = e.getPath().getNodes();
			Double start = null, end = null;
			if(dEquals(points.get(points.size() - 1).y, boxYtop)){
				start = points.get(points.size() - 1).y;
				end = (double) (e.getTarget().getY() > e.getSource().getY() ? e.getTarget().getY() : e.getSource().getY());
				int i =0;
				for(Double d : temp){	//count amount of edges on layer between start and end. start is on top of the box, end in the FieldAccess.
					if(d < end || dEquals(end, d)){
						i++;
					} else{break;}
					if(i % 2 == 0){
						result[i/2 - 1]++;
					}
				}
			}else if(dEquals(points.get(0).y, boxYbottom)){
				start = (double) (e.getSource().getY() < e.getTarget().getY() ? e.getSource().getY() + e.getSource().getSize().y : e.getTarget().getY() + e.getTarget().getSize().y);
				end = points.get(0).y;
				int i =0;
				for(Double d : temp){	//count amount of edges on layer between start and end. start is in the FieldAccess, end at the bottom of the box.
					if(d < start){
						i++;
						continue;
					}
					if(d < end || dEquals(end, d)){
						i++;
					} else{break;}
					if(i % 2 == 0){
						result[i/2 - 1]++;
					}
				}
			}else {assert(false);}
			
		}
		return result;
	}
	
	private double[] getDistancePerEdgeInLayer(double[] spaceBetweenLayers, int[] newEdgesPerLayer){
		assert(spaceBetweenLayers.length == newEdgesPerLayer.length);
		int length = spaceBetweenLayers.length;
		double[] result = new double[length];
		for(int i = 0; i < length; i++){
			result[i] = spaceBetweenLayers[i] / (newEdgesPerLayer[i] + 1);
		}
		return result;
	}
	
	private void drawEdge(FieldAccess fa, JoanaEdge e){
		Set<JoanaEdge> faEdges = fa.getGraph().getEdgeSet();
		Set<JoanaVertex> faVertices = fa.getGraph().getVertexSet();
		EdgePath path = e.getPath();
		List<DoublePoint> points = path.getNodes();
		double boxYtop = fa.getY();
		double boxYbottom = fa.getY() + fa.getSize().y;
		assert(dEquals(points.get(0).y, boxYbottom) ^ dEquals(points.get(points.size() - 1).y, boxYtop));
		List<DoublePoint> temp = new LinkedList<>();	//new points, always from top to bottom. Inserting to original edgepath later in if-else-statement!

		//both cases for the moment just rudimentary implemented
		if(dEquals(points.get(points.size() - 1).y, boxYtop)){	//draw from top of box down to the target vertex
			DoublePoint start = points.get(points.size() - 1);
			JoanaVertex target = e.getTarget().getY() > e.getSource().getY() ? e.getTarget() : e.getSource();
			DoublePoint end = new DoublePoint(target.getX(), target.getY());
			temp.add(end);
			temp.addAll(0, points);	//from here possible idea
			path.clear();
			temp.forEach(point->path.addPoint(point));
		} else{	//draw from target vertex to bottom 
			JoanaVertex source = e.getSource().getY() < e.getTarget().getY() ? e.getSource() : e.getTarget();
			DoublePoint start = new DoublePoint(source.getX(), source.getY());
			DoublePoint end = points.get(0);
			temp.add(start);
			temp.addAll(points);
			path.clear();
			temp.forEach(point->path.addPoint(point));
		}
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
		for(JoanaVertex v : faVertices){
//			logger.debug("vertex pos: "+v.getX()+","+v.getY()+" size: "+v.getSize().x+","+v.getSize().y);
		}
		return fromOutEdges;
	}
	
	/**
	 * turns edges coming from out into the representing vertex box so that:
	 * out edges going into the representing vertex in the top, and going out at the bottom.
	 */
	private Set<JoanaEdge> getTurnedEdges(FieldAccess fa, Set<JoanaEdge> fromOut){
		Set<JoanaEdge> turnedEdges = new HashSet<JoanaEdge>();
		double boxYtop = fa.getY();
		double boxYbottom = fa.getY() + fa.getSize().y;
		for(JoanaEdge e : fromOut){
			List<DoublePoint> points = e.getPath().getNodes();
			if(dEquals(points.get(0).y, boxYtop) ^ dEquals(points.get(points.size() - 1).y, boxYbottom)){
				Collections.reverse(points);	//reverse edgepath
				turnedEdges.add(e);
			}else{
				assert(dEquals(points.get(0).y, boxYbottom) ^ dEquals(points.get(points.size() - 1).y, boxYtop));	//normal edgePath
			}
		}
		return turnedEdges;
	}
	
	
	private List<FieldAccess> expandFieldAccesses(MethodGraph graph) {

		// Fill a map from id to edge for all outgoing edges of field accesses to match them later
		// with the new edges of the graph to the specific vertices in the field access
		Map<Integer, JoanaEdge> idToEdge = new HashMap<>();
		for (FieldAccess fa : graph.getFieldAccesses()) {
		    for (JoanaEdge e : graph.edgesOf(fa)) {
		        idToEdge.put(e.getID(), e);
		    }
		}

		List<FieldAccess> fas = graph.expandFieldAccesses();
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
		public Color getColor() {
			// not necessary
			return null;
		}
    	
    }

}
