package edu.kit.student.joana;


import java.util.List;

import edu.kit.student.graphmodel.Graph;
import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.directed.DirectedGraph;

/**
 * An abstract superclass for all JOANA specific graphs.
 */
public abstract class JoanaGraph
    implements DirectedGraph, LayeredGraph {
    
    public Graph parent;
    public List<Graph> children;
    public String name;

    public JoanaGraph(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getID() {
        //TODO: Generate unique ID across Graphs
        return 0;
    }

    @Override
    public Graph getParentGraph() {
        return this.parent;
    }

    @Override
    public void setParentGraph(Graph parent) {
        this.parent = parent;
        
    }

    @Override
    public List<Graph> getChildGraphs() {
        return this.children;
    }

    @Override
    public void addChildGraph(Graph child) {
        this.children.add(child);
    }
}
