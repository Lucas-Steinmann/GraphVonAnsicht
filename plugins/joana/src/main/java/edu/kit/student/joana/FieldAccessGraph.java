package edu.kit.student.joana;

import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.joana.JoanaVertex.VertexKind;
import edu.kit.student.plugin.LayoutOption;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A {@link JoanaGraph} which specifies a {@link FieldAccess} in a {@link JoanaGraph}.
 */
public class FieldAccessGraph extends JoanaGraph {
	private JoanaVertex representingVertex;	//the vertex that represents this FieldAccessGraph for later layouting.
	private Set<JoanaEdge> inEdges;
	private Set<JoanaEdge> outEdges;
    private JoanaVertex fieldEntry;
    private Set<JoanaEdge> edges;
    
    public FieldAccessGraph(String name, Set<JoanaVertex> vertices, Set<JoanaEdge> edges) {
        //TODO: Check whether the sets build a valid field access
        super(name, vertices, edges);
        this.representingVertex = new JoanaVertex(name,"",VertexKind.UNKNOWN);
        this.inEdges = new HashSet<>();
        this.outEdges = new HashSet<>();
        this.edges = edges;
    }
    
    public void addEdge(JoanaEdge edge){
    	this.edges.add(edge);
    }
    
    @Override
    public Set<JoanaEdge> getEdgeSet(){
    	return this.edges;
    }
    
    /**
     * Returns the edge which is going into the entry vertex of this FieldAccessGraph.
     * 
     * @return the edge going into this FieldAccessGraph
     */
    public Set<JoanaEdge> getReplacedInEdges(){
    	return this.inEdges;
    }
    
    /**
     * Returns the edge which is coming out of this FieldAccessGraph. 
     * If there is no such edge, returns null.
     * 
     * @return the edge going out of this FieldAccessGraph, or null if not present
     */
    public Set<JoanaEdge> getReplacedOutEdges(){
    	return this.outEdges;
    }
    
    /**
     * For the moment sets first the edge coming into this FieldAccessGraph, later being set without this method.
     */
    public void addInEdge(JoanaEdge e){
    	this.inEdges.add(e);
    }
    
    /**
     * For the moment sets the edge going out of this FieldAccessGraph, later being set without this method.
     */
    public void addOutEdge(JoanaEdge e){
    	this.outEdges.add(e);
    }

    /**
     * Returns the vertex that represents this FieldAccessGraph for later layouting and will then be replaced by the layered FieldAccessGraph.
     * @return the vertex that represents this FieldAccessGraph
     */
    public JoanaVertex getRepresentingVertex(){
    	return this.representingVertex;
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
    public List<LayoutOption> getRegisteredLayouts() {
        return super.getRegisteredLayouts();
    }

    @Override
    public LayoutOption getDefaultLayout() {
        return null;
    }


    public JoanaVertex getFieldEntry() {
        return fieldEntry;
    }


    public void setFieldEntry(JoanaVertex fieldEntry) {
        this.fieldEntry = fieldEntry;
    }
}
