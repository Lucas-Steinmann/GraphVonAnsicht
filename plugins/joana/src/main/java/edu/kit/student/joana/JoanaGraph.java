package edu.kit.student.joana;


import java.util.LinkedList;
import java.util.List;

import edu.kit.student.graphmodel.Graph;
import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.util.IdGenerator;

/**
 * An abstract superclass for all JOANA specific graphs.
 */
public abstract class JoanaGraph
    implements DirectedGraph, LayeredGraph {
    
    public Graph parent;
    public List<Graph> children = new LinkedList<>();
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
