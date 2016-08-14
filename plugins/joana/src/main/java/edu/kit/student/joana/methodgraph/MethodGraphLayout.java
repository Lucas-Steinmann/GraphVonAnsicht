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

import edu.kit.student.graphmodel.EdgePath;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.joana.FieldAccess;
import edu.kit.student.joana.FieldAccessGraph;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.parameter.Settings;
import edu.kit.student.plugin.LayoutAlgorithm;
import edu.kit.student.sugiyama.AbsoluteLayerConstraint;
import edu.kit.student.sugiyama.LayerContainsOnlyConstraint;
import edu.kit.student.sugiyama.RelativeLayerConstraint;
import edu.kit.student.sugiyama.SugiyamaLayoutAlgorithm;
import edu.kit.student.sugiyama.steps.LayerAssigner;
import edu.kit.student.util.DoublePoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements hierarchical layout with layers for {@link MethodGraph}.
 * This graph contains field access subgraphs.
 */
public class MethodGraphLayout implements LayoutAlgorithm<MethodGraph> {
	
    final Logger logger = LoggerFactory.getLogger(MethodGraphLayout.class);
	private SugiyamaLayoutAlgorithm<MethodGraph> sugiyamaLayoutAlgorithm;

	
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
        Set<Vertex> firstLayer = new HashSet<Vertex>();
        firstLayer.add(graph.getEntryVertex());
        absoluteLayerConstraints.add(new AbsoluteLayerConstraint(firstLayer, 0));
		layerContainsOnlyConstraints.add(new LayerContainsOnlyConstraint(firstLayer, 0));
        
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
		expandedFas.forEach(fa->drawFieldAccessEdges(graph,fa));
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
		//TODO: draw here every? edge in the FieldAccess new. beginning from the point going to the top or bottom of the representing vertex
		Set<JoanaEdge> fromOutEdges = this.getEdgesFromOutside(graph, fa);	//edges from out in representing vertex
		Set<JoanaEdge> turnedEdges = this.getTurnedEdges(fa, fromOutEdges);	//turned edges so on top are just incoming and on bottom just outgoing
		Set<JoanaEdge> notTurned = fromOutEdges.stream().filter(e->!turnedEdges.contains(e)).collect(Collectors.toSet());	//not turned edges
		
		turnedEdges.forEach(edge->drawEdge(fa,edge));
		notTurned.forEach(edge->drawEdge(fa,edge));
		turnedEdges.forEach(edge->Collections.reverse(edge.getPath().getNodes()));
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
			result[i] = spaceBetweenLayers[i] / newEdgesPerLayer[i];
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

		//TODO: both cases for the moment just rudimentary implemented
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
		logger.debug("from out edges: "+fromOutEdges.size());
		logger.debug("box pos: "+fa.getX()+","+fa.getY()+" size: "+fa.getSize().x+","+fa.getSize().y);
		for(JoanaVertex v : faVertices){
			logger.debug("vertex pos: "+v.getX()+","+v.getY()+" size: "+v.getSize().x+","+v.getSize().y);
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
                v.setX(v.getX() + fa.getX() + (int)FieldAccess.padding/2);
                v.setY(v.getY() + fa.getY() + (int)FieldAccess.padding/2);
            }
			for(JoanaEdge e : fa.getGraph().getEdgeSet()){
				List<DoublePoint> points = e.getPath().getNodes();
				List<DoublePoint> newPoints = new LinkedList<>();
				assert(!points.isEmpty());
				points.forEach(p->newPoints.add(new DoublePoint(p.x + fa.getX() + FieldAccess.padding/2, 
				                                                p.y + fa.getY() + FieldAccess.padding/2)));
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
    	logger.debug("comparing "+a+" with "+b);
		return Math.abs(a-b) < Math.pow(10, -6);
	}

}
