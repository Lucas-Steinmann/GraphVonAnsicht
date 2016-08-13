package edu.kit.student.joana.methodgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javafx.util.Pair;

/**
 * Implements hierarchical layout with layers for {@link MethodGraph}.
 * This graph contains field access subgraphs.
 */
public class MethodGraphLayout implements LayoutAlgorithm<MethodGraph> {
	
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
		System.out.println("Graph before: Vertices: "+graph.getVertexSet().size()+", Edges: "+graph.getEdgeSet().size());
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
		
		System.out.println("Graph after: Vertices: "+graph.getVertexSet().size()+", Edges: "+graph.getEdgeSet().size());
		expandFieldAccesses(graph);
	}
	
	/**
	 * Layouts every single FieldAccessGraph.
	 * Also sets the sizes of the vertex representing this FieldAccessGraph appropriate.
	 */
	private void layoutFieldAccessGraphs(MethodGraph graph){
		for(FieldAccess fa : graph.getFieldAccesses()){
			FieldAccessGraph fag = fa.getGraph();
			this.sugiyamaLayoutAlgorithm.layout(fag);
			JoanaVertex rep = fa;
			System.out.println("new fag size: "+fag.getVertexSet().size() + "," +fag.getEdgeSet().size());

			//now set the size of rep new, according to the layered FieldAccessGraph which he represents
			Set<JoanaVertex> fagVertices = fag.getVertexSet();
			int minX, minY, maxX, maxY, newWidth, newHeight;
			minX = fagVertices.stream().mapToInt(vertex->vertex.getX()).min().getAsInt();
			maxX = fagVertices.stream().mapToInt(vertex->(int)Math.round(vertex.getX() + vertex.getSize().getKey())).max().getAsInt();
			minY = fagVertices.stream().mapToInt(vertex->vertex.getY()).min().getAsInt();
			maxY = fagVertices.stream().mapToInt(vertex->(int)Math.round(vertex.getY() + vertex.getSize().getValue())).max().getAsInt();
			newWidth = maxX - minX + 10;
			newHeight = maxY - minY + 10;
			
			// set now the new size of the representing vertex appropriated to the layouted FieldAccessGraphs
			rep.setSize(new Pair<Double, Double>((double)newWidth, (double)newHeight));	
		}
	}
	
	private void expandFieldAccesses(MethodGraph graph) {

		// Fill a map from id to edge for all outgoing edges of field accesses to match them later
		// with the new edges of the graph to the specific vertices in the field access
		Map<Integer, JoanaEdge> idToEdge = new HashMap<>();
		for (FieldAccess fa : graph.getFieldAccesses()) {
		    for (JoanaEdge e : graph.edgesOf(fa)) {
		        idToEdge.put(e.getID(), e);
		    }
		}

		graph.expandFieldAccesses();
		Set<JoanaEdge> copied = new HashSet<>();

		for (FieldAccess fa : graph.getFieldAccesses()) {

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

}
