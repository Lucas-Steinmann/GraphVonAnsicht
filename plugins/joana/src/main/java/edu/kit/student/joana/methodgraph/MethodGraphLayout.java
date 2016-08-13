package edu.kit.student.joana.methodgraph;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.joana.FieldAccess;
import edu.kit.student.joana.FieldAccessGraph;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.parameter.Settings;
import edu.kit.student.sugiyama.*;
import edu.kit.student.sugiyama.steps.LayerAssigner;
import edu.kit.student.util.DoublePoint;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements hierarchical layout with layers for {@link MethodGraph}.
 * This graph contains field access subgraphs.
 */
public class MethodGraphLayout implements LayeredLayoutAlgorithm<MethodGraph> {
	
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
		graph.restoreGraph();
		logger.info("Graph before: Vertices: "+graph.getVertexSet().size()+", Edges: "+graph.getEdgeSet().size());
		this.addAdditionalFieldAccessEdges(graph);
		this.layoutFieldAccessGraphs(graph);
		this.adjustVerticesAndEdges(graph);

	    
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
		
		//now, after layouting the graph, replace the vertices representing the FieldAccessGraphs
		this.replaceRepresentingVertex(graph);
		logger.info("Graph after: Vertices: "+graph.getVertexSet().size()+", Edges: "+graph.getEdgeSet().size());
	}
	
	
	/**
	 * Searches in all edges for more edges that are part of an FieldAccess but are currently not in this FieldAccess.
	 */
	private void addAdditionalFieldAccessEdges(MethodGraph graph){
		Set<JoanaEdge> edges = graph.getEdgeSet();
		
		for(FieldAccess fa : graph.getFieldAccesses()){
			FieldAccessGraph fag = fa.getGraph();
			Set<JoanaVertex> fagVertices = fag.getVertexSet();
//			Set<JoanaEdge> fagEdges = fag.getEdgeSet();
			
			for(JoanaEdge e : edges){
				if(fagVertices.contains(e.getSource()) && fagVertices.contains(e.getTarget())){
					fag.addEdge(e);
//					fagEdges.add(e);
				}
			}
		}
	}
	
	
	/**
	 * Layouts every single FieldAccessGraph.
	 * Also sets the sizes of the vertex representing this FieldAccessGraph appropriate.
	 */
	private void layoutFieldAccessGraphs(MethodGraph graph){
		for(FieldAccess fa : graph.getFieldAccesses()){
			FieldAccessGraph fag = fa.getGraph();
			this.sugiyamaLayoutAlgorithm.layout(fag);
			JoanaVertex rep = fa.getGraph().getRepresentingVertex();
			logger.info("new fag size: "+fag.getVertexSet().size() + "," +fag.getEdgeSet().size());
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
	
	/**
	 * Removes all vertices and edges that are part in a FieldAccessGraph form the graphs vertex- and edge-set.
	 * Additionally adds an vertex to the graphs vertex-set that represents this FieldAccessGraph in later layouting.
	 */
	private void adjustVerticesAndEdges(MethodGraph graph){
		Set<JoanaVertex> vertices = graph.getVertexSet();
		Set<JoanaEdge> edges = graph.getEdgeSet();

		for(FieldAccess fa : graph.getFieldAccesses()){
			FieldAccessGraph fag = fa.getGraph();
			Set<JoanaVertex> fagVertices = fag.getVertexSet();
			Set<JoanaEdge> fagEdges = fag.getEdgeSet();
			
			for(JoanaEdge e : fagEdges){
				assert(fagVertices.contains(e.getSource())&&fagVertices.contains(e.getTarget()));
			}
			
			String fagV = "";
			for(JoanaVertex v : fagVertices){
				fagV += v.getID() + ",";
			}
			logger.info("fagVertices: "+fagVertices.size()+" ["+fagV+"], fagEdges: "+fagEdges.size());
			
			fagVertices.forEach(v->graph.removeVertex(v));
//			vertices.removeAll(fagVertices);	//removes FieldAccess-vertices from the vertex set of the normal graph
			
			Set<JoanaEdge> inFieldAccess = new HashSet<>();	//edges that go into this FieldAccess
			Set<JoanaEdge> outFieldAccess = new HashSet<>();	//edges that go out of this FieldAccess
			for(JoanaEdge e : edges){
				if(vertices.contains(e.getSource()) && fagVertices.contains(e.getTarget())){
					assert(!vertices.contains(e.getTarget()) && !fagVertices.contains(e.getSource()));
					logger.info("in: "+e.getSource().getID()+","+e.getTarget().getID()+",entry: "+fag.getFieldEntry().getID());
					inFieldAccess.add(e);
				} else if(vertices.contains(e.getTarget()) && fagVertices.contains(e.getSource())){
					assert(!vertices.contains(e.getSource()) && !fagVertices.contains(e.getTarget()));
					logger.info("out: "+e.getSource().getID()+","+e.getTarget().getID());
					outFieldAccess.add(e);
				}
			}
			assert(inFieldAccess.size() > 0);	//there must be at least one edge going in an FieldAccess
			
			graph.addVertex(fag.getRepresentingVertex()); //adds representing vertex to normal vertex-set
//			vertices.add(fag.getRepresentingVertex());	
			
			for(JoanaEdge eIn : inFieldAccess){	//add new edge from graph into representing vertex and deletes the old one
				fag.addInEdge(eIn);	//saves the old edge to insert it later again
				eIn.setVertices(eIn.getSource(), fag.getRepresentingVertex());
//				assert(edges.remove(eIn));
//				assert(edges.add(eIn));
				edges.remove(eIn);	//cause setting edges vertices is not transferred to the edgelist of the graph so
				edges.add(eIn);		//remove it and add the edge with new set vertices
//				graph.addEdge(new JoanaEdge(graph.getName(), "FieldAccess", eIn.getSource(), fag.getRepresentingVertex(), EdgeKind.UNKNOWN));
//				edges.add(new JoanaEdge(graph.getName(), "FieldAccess", eIn.getSource(), fag.getRepresentingVertex(), EdgeKind.UNKNOWN));
			}
			
			for(JoanaEdge eOut : outFieldAccess){	//add new edge from representing vertex into normal graph and deletes the old one
				fag.addOutEdge(eOut);	//saves the old edge to insert it later again
				eOut.setVertices(fag.getRepresentingVertex(), eOut.getTarget());
				
//				assert(edges.remove(eOut));
//				assert(edges.add(eOut));
				edges.remove(eOut);
				edges.add(eOut);
//				graph.addEdge(new JoanaEdge(graph.getName(), "FieldAccess",  fag.getRepresentingVertex(), eOut.getTarget(), EdgeKind.UNKNOWN));
//				edges.add(new JoanaEdge(graph.getName(), "FieldAccess",  fag.getRepresentingVertex(), eOut.getTarget(), EdgeKind.UNKNOWN));
			}
			fagEdges.forEach(e->graph.removeEdge(e));
//			edges.removeAll(fagEdges);
		}
	}
	
	private void replaceRepresentingVertex(MethodGraph graph){
		
		for(FieldAccess fa : graph.getFieldAccesses()){
			FieldAccessGraph fag = fa.getGraph();
			JoanaVertex rep = fag.getRepresentingVertex();
			Set<JoanaVertex> fagVertices = fag.getVertexSet();
			Set<JoanaEdge> fagEdges = fag.getEdgeSet();
			
			//now, after layouting the normal graph adjust the coordinates of every vertex contained in the FieldAccessGraph by addig them something of the representing's coordinates
			for(JoanaVertex v : fagVertices){
				v.setX(v.getX() + rep.getX() + 5);
				v.setY(v.getY() + rep.getY() + 5);
			}
			
			for(JoanaEdge e : fagEdges){
				List<DoublePoint> points = e.getPath().getNodes();
				List<DoublePoint> newPoints = new LinkedList<>();
				assert(!points.isEmpty());
				points.forEach(p->newPoints.add(new DoublePoint(p.x + rep.getX() + 5, p.y + rep.getY() + 5)));
				points.clear();
				points.addAll(newPoints);
			}
			
			
//			Set<JoanaEdge> edgesIn = new HashSet<>();
//			Set<JoanaEdge> edgesOut = new HashSet<>();

			
//			for(JoanaEdge e : edges){	//searches for the edge(s) of the graph that must be replaced through edges of the FieldAccess
//				if(vertices.contains(e.getSource()) && e.getTarget().getID() == fag.getRepresentingVertex().getID()){
//					edgesIn.add(e);
//				} else if(e.getSource().getID() == fag.getRepresentingVertex().getID() && vertices.contains(e.getTarget())){
//					edgesOut.add(e);
//				}
//			}
//			assert(!edgesIn.isEmpty());	//there must be an edge going from the graph into the vertex that represents an FieldAccessGraph
			
			
			Set<JoanaEdge> restoredIn = fag.getReplacedInEdges();	//edge going into this FieldAccessGraph that was replaced temporary for layouting the whole graph.
			Set<JoanaEdge> restoredOut = fag.getReplacedOutEdges();// edge that comes into this FieldAccessGraph that was replaced temporary for layouting the whole graph.
//			assert(!edgesIn.isEmpty() && !restoredIn.isEmpty() && edgesIn.size() == restoredIn.size());
//			assert((edgesOut.isEmpty() && restoredOut.isEmpty())||(!edgesOut.isEmpty() && !restoredOut.isEmpty() && edgesOut.size() == restoredOut.size()));
			
			//TODO: also draw the edgePath from the vertex out into the FieldAccessGraph here!
			//or just from the entry point in the box to the vertices in it ?
			restoredIn.forEach(e->e.setVertices(e.getLastSource(), e.getLastTarget()));
			restoredOut.forEach(e->e.setVertices(e.getLastSource(), e.getLastTarget()));
			
			for(JoanaEdge e1: restoredIn){
				assert(!e1.getPath().getNodes().isEmpty());
			}
			for(JoanaEdge e2 : restoredOut){
				assert(!e2.getPath().getNodes().isEmpty());
			}
			
			//all Edgepaths have been set, now add the restoredEdges and FieldAccess-vertices to the original graph
			
//			restoredIn.forEach(e->graph.addEdge(e));
//			restoredOut.forEach(e->graph.addEdge(e));
			fagVertices.forEach(v->graph.addVertex(v));
			fagEdges.forEach(e->graph.addEdge(e));
			
//			graph.removeVertex(fag.getRepresentingVertex());	//TODO: experiment cause of its background that one want to have
//			vertices.remove(fag.getRepresentingVertex());
//			edgesIn.forEach(e->graph.removeEdge(e));
//			edges.remove(edgeIn);
//			edgesOut.forEach(e->graph.removeEdge(e));
		}
	}

	@Override
	public void layoutLayeredGraph(MethodGraph graph) {
		// TODO Auto-generated method stub
		
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
