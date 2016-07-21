package edu.kit.student.joana;

import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.joana.JoanaVertex.VertexKind;
import edu.kit.student.plugin.LayoutOption;

import java.util.List;
import java.util.Set;


/**
 * A {@link JoanaGraph} which specifies a {@link FieldAccess} in a {@link JoanaGraph}.
 */
public class FieldAccessGraph extends JoanaGraph {
	private JoanaVertex representingVertex;	//the vertex that represents this FieldAccessGraph for later layouting.

    private JoanaVertex fieldEntry;
    
    public FieldAccessGraph(String name, Set<JoanaVertex> vertices, Set<JoanaEdge> edges) {
        //TODO: Check whether the sets build a valid field access
        super(name, vertices, edges);
        this.representingVertex = new JoanaVertex(name,"FieldAccessRepresenting",VertexKind.UNKNOWN);
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
