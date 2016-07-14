package edu.kit.student.joana;


import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.kit.student.graphmodel.Graph;
import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.ViewableGraph;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.util.IdGenerator;

/**
 * An abstract superclass for all JOANA specific graphs.
 */
public abstract class JoanaGraph
    implements DirectedGraph, LayeredGraph, ViewableGraph {
    
	
	public abstract Set<JoanaEdge> getEdgeSet();

    public ViewableGraph parent;
    public List<ViewableGraph> children = new LinkedList<>();
    public String name;
    public Integer id;

    public JoanaGraph(String name) {
        this.name = name;
        this.id = IdGenerator.getInstance().createId();    
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getID() {
        return this.id;
    }

    @Override
    public ViewableGraph getParentGraph() {
        return this.parent;
    }

    @Override
    public void setParentGraph(ViewableGraph parent) {
        this.parent = parent;
        
    }

    @Override
    public List<ViewableGraph> getChildGraphs() {
        return this.children;
    }

    @Override
    public void addChildGraph(ViewableGraph child) {
        this.children.add(child);
    }
}
