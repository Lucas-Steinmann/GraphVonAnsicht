package edu.kit.student.joana.callgraph;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.joana.JoanaCompoundVertex;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraph;
import edu.kit.student.joana.methodgraph.MethodGraph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a specified graph representation for the Callgraph in Joana.
 */
public class CallGraph extends JoanaGraph<JoanaCompoundVertex,JoanaEdge<JoanaCompoundVertex>> {

    public CallGraph(String name) {
        super(name);
    }

    @Override
    public List<LayeredGraph<JoanaCompoundVertex, JoanaEdge<JoanaCompoundVertex>>> getSubgraphs() {
        return new LinkedList<LayeredGraph<JoanaCompoundVertex, JoanaEdge<JoanaCompoundVertex>>>();
    }
    
    public List<MethodGraph> getMethodgraphs() {
    	List<MethodGraph> list = new ArrayList<MethodGraph>();
    	getVertexSet().forEach((vertex) -> list.add((MethodGraph)vertex.getGraph()));
    	return list;
    }
}
