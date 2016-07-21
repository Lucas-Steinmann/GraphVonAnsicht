package edu.kit.student.joana.methodgraph;

import java.util.HashSet;
import java.util.Set;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.joana.FieldAccess;
import edu.kit.student.joana.FieldAccessGraph;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaEdge.EdgeKind;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.parameter.Settings;
import edu.kit.student.sugiyama.AbsoluteLayerConstraint;
import edu.kit.student.sugiyama.LayeredLayoutAlgorithm;
import edu.kit.student.sugiyama.RelativeLayerConstraint;
import edu.kit.student.sugiyama.SugiyamaLayoutAlgorithm;
import edu.kit.student.sugiyama.steps.LayerAssigner;

/**
 * Implements hierarchical layout with layers for {@link MethodGraph}.
 * This graph contains field access subgraphs.
 */
public class MethodGraphLayout implements LayeredLayoutAlgorithm<MethodGraph> {
	
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
//		this.addAdditionalFieldAccessEdges(graph);
//		this.adjustVerticesAndEdges(graph);
//		this.layoutFieldAccessGraphs(graph);
	    
	  //create absoluteLayerConstraints
        Set<AbsoluteLayerConstraint> absoluteLayerConstraints = new HashSet<AbsoluteLayerConstraint>();
        
        //create absoluteLayerConstraint for Entry vertex
        Set<Vertex> firstLayer = new HashSet<Vertex>();
        firstLayer.add(graph.getEntryVertex());
        absoluteLayerConstraints.add(new AbsoluteLayerConstraint(firstLayer, false, true, 0, 0));
        
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
        
        absoluteLayerConstraints.add(new AbsoluteLayerConstraint(secondLayer, false, true, 1, 1));
        
        LayerAssigner assigner = new LayerAssigner();
        assigner.addRelativeConstraints(relativeLayerConstraints);
        assigner.addAbsoluteConstraints(absoluteLayerConstraints);
	    
        sugiyamaLayoutAlgorithm.setLayerAssigner(assigner);
		sugiyamaLayoutAlgorithm.layout(graph);
	}
	
	
	/**
	 * Searches in all edges for more edges that are part of an FieldAccess but are currently not in this FieldAccess.
	 */
	private void addAdditionalFieldAccessEdges(MethodGraph graph){
		Set<JoanaEdge> edges = graph.getEdgeSet();
		
		for(FieldAccess fa : graph.getFieldAccesses()){
			FieldAccessGraph fag = fa.getGraph();
			Set<JoanaVertex> fagVertices = fag.getVertexSet();
			Set<JoanaEdge> fagEdges = fag.getEdgeSet();
			
			for(JoanaEdge e : edges){
				if(fagVertices.contains(e.getSource()) && fagVertices.contains(e.getTarget())){
					fagEdges.add(e);
				}
			}
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
			
			vertices.removeAll(fagVertices);	//removes FieldAccess-vertices from the vertex set of the normal graph
			
			Set<JoanaEdge> inFieldAccess = new HashSet<>();	//edges that go into this FieldAccess
			Set<JoanaEdge> outFieldAccess = new HashSet<>();	//edges that go out of this FieldAccess
			for(JoanaEdge e : fagEdges){
				if(vertices.contains(e.getSource()) && fagVertices.contains(e.getTarget())){
					inFieldAccess.add(e);
				} else if(vertices.contains(e.getTarget()) && fagVertices.contains(e.getSource())){
					outFieldAccess.add(e);
				}
			}
			assert(inFieldAccess.size() == 1);	//there must be always an edge going in an FieldAccess
			assert(outFieldAccess.size() <= 1);	//there can be one or zero edges coming out of an FieldAccess
			
			vertices.add(fag.getRepresentingVertex());	//adds representing vertex to normal vertex-set
			
			for(JoanaEdge eIn : inFieldAccess){	//add new edge from graph into representing vertex
				assert(edges.remove(eIn));
				edges.add(new JoanaEdge(graph.getName(), "FieldAccess", eIn.getSource(), fag.getRepresentingVertex(), EdgeKind.UNKNOWN));
			}
			
			for(JoanaEdge eOut : outFieldAccess){	//add new edge from representing vertex into normal graph
				assert(edges.remove(eOut));
				edges.add(new JoanaEdge(graph.getName(), "FieldAccess",  fag.getRepresentingVertex(), eOut.getTarget(), EdgeKind.UNKNOWN));
			}
			
			edges.removeAll(fagEdges);
		}
	}
	
	private void layoutFieldAccessGraphs(MethodGraph graph){
		for(FieldAccess fa : graph.getFieldAccesses()){
			this.sugiyamaLayoutAlgorithm.layout(fa.getGraph());
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
