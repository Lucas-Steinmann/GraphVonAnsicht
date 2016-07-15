package edu.kit.student.graphmodel;

import java.util.List;
import java.util.Stack;

import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.objectproperty.GAnsProperty;
import javafx.scene.paint.Color;

public class DirectedOnionPath<E extends DirectedEdge, V extends CollapsedVertex> implements DirectedEdge {
    
    private Stack<V> target = new Stack<>();
    private Stack<V> source = new Stack<>();
    private E edge;

    public DirectedOnionPath(E edge) {
        this.edge = edge;
    }

    @Override
    public List<? extends Vertex> getVertices() {
       return  edge.getVertices();
    }

    @Override
    public String getName() {
       return  edge.getName();
    }

    @Override
    public Integer getID() {
       return  edge.getID();
    }

    @Override
    public String getLabel() {
        return edge.getLabel();
    }

    @Override
    public void addToFastGraphAccessor(FastGraphAccessor fga) {
    }

    @Override
    public EdgePath getPath() {
        // TODO: Adapt should be replaced
        return edge.getPath();
    }

    @Override
    public List<GAnsProperty<?>> getProperties() {
        return edge.getProperties();

    }

    @Override
    public Color getColor() {
        return edge.getColor();
    }

    @Override
    public V getSource() {
        if (source.isEmpty())
            return null;
        return source.get(source.size()- 1);
    }

    @Override
    public V getTarget() {
        if (target.isEmpty())
            return null;
        return target.peek();
    }

    public void removeNode(V vertex) {
        if (!source.isEmpty()) {
            if (this.source.peek() == vertex) {
                this.source.pop();
                return;
            }
        } if (!target.isEmpty()) {
            if (this.target.peek() == vertex) {
                this.target.pop();
                return;
            }
        }
        if (this.source.peek() != vertex && this.target.get(target.size() - 1) != vertex) {
            // Only assert for testing. In general this should not be necessary
            throw new IllegalArgumentException("Can only add to innermost layer of nodes");
        }
    }
    
    public void removeSource(V vertex) {
        if (this.source.peek() != vertex) {
            // Only assert for testing. In general this should not be necessary
            throw new IllegalArgumentException("Can only add to innermost layer of nodes");
        }
        this.source.pop();
    }

    public void removeTarget(V vertex) {
        if (this.target.peek() != vertex) {
            // Only assert for testing. In general this should not be necessary
            throw new IllegalArgumentException("Can only add to innermost layer of nodes");
        }
        this.target.pop();
    }

    public void addAsSource(V vertex) {
        Vertex toContain = edge.getSource();
        if (!source.isEmpty()) {
            toContain = source.peek();
        }
        if (!vertex.getGraph().getVertexSet().contains(toContain))
            throw new IllegalArgumentException("Cannot add source vertex not containing the source of the innermost layer");
        this.source.push(vertex);
    }

    public void addAsTarget(V vertex) {
        Vertex toContain = edge.getTarget();
        if (!target.isEmpty()) {
            toContain = target.peek();
        }
        if (!vertex.getGraph().getVertexSet().contains(toContain))
            throw new IllegalArgumentException("Cannot add target vertex not containing the target of the innermost layer");
        this.target.push(vertex);
    }

    public E getEdge() {
        return edge;
    }
    
    @Override
    public String toString() {
        StringBuilder sourceB = new StringBuilder();
        sourceB.append("(" + edge.getSource().getID() + ")->");
        for (V node : source) {
            sourceB.append(node.getID());
            sourceB.append("->");
        }
        sourceB.append("   ");
        StringBuilder targetB = new StringBuilder();
        for (V node : target) {
            targetB.insert(0, node.getID());
            targetB.insert(0, "->");
        }
        targetB.append("(" + edge.getTarget().getID() + ")");
        return sourceB.toString() + targetB.toString();
    }

}
