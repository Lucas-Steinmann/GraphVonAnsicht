package edu.kit.student.joana.methodgraph;

import edu.kit.student.graphmodel.CollapsedVertex;
import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.joana.FieldAccess;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraph;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.LayoutRegister;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This is a specific graph representation for a MethodGraph in JOANA .
 */
public class MethodGraph extends JoanaGraph {

    private static final String ENTRY_NAME = "Entry";
    private static LayoutRegister<MethodGraphLayoutOption> register;
    private JoanaVertex entry;
    private HashSet<FieldAccess> fieldAccesses;

    public MethodGraph(Set<JoanaVertex> vertices, Set<JoanaEdge> edges, 
            Set<FieldAccess> fieldAccesses, String methodName, Integer id) {
        super(methodName, id);
        this.fieldAccesses = new HashSet<>(fieldAccesses);
    }
    
    /**
     * Returns the entry vertex of a method.
     * 
     * @return The entry vertex of a method.
     */
    public JoanaVertex getEntryVertex() { 
        if (entry == null) {
            return searchEntry();
        }
        return entry;
    }

    private JoanaVertex searchEntry() {
        for (JoanaVertex v : getVertexSet()) {
            if (v.getName() == ENTRY_NAME) {
                entry = v;
                return entry;
            }
        }
        return null;
    }

    /**
     * Returns a list of all {@link FieldAccess} in the MethodGraph.
     * 
     * @return A list of all {@link FieldAccess} in the MethodGraph.
     */
    public List<FieldAccess> getFieldAccesses() { 
        return new LinkedList<>(fieldAccesses);
    }

    /**
     * Returns a list of all {@link JoanaVertex} which are method calls in the MethodGraph.
     * 
     * @return A list of all method calls.
     */
    public List<JoanaVertex> getMethodCalls() { 
        // TODO Auto-generated method
        return null;
    } 

    /**
     * Sets the {@link LayoutRegister}, which stores the available 
     * {@link LayoutOption} for all method graphs statically.
     * @param register The {@link LayoutRegister} that will be set.
     */
    protected static void setRegister(LayoutRegister<MethodGraphLayoutOption> register) {
        MethodGraph.register = register;
    }

    @Override
    public List<LayoutOption> getRegisteredLayouts() {
        List<LayoutOption> result = super.getRegisteredLayouts();
        result.addAll(MethodGraph.register.getLayoutOptions());
        return result;
    }

    @Override
    public List<LayeredGraph<JoanaVertex, JoanaEdge>> getSubgraphs() {
        List<LayeredGraph<JoanaVertex, JoanaEdge>> faGraphs = new LinkedList<>();
        this.getFieldAccesses().forEach((fa) -> faGraphs.add(fa.getGraph()));
        return faGraphs;
    }
}
