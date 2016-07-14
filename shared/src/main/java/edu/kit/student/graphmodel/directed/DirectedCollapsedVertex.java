package edu.kit.student.graphmodel.directed;

import java.util.List;
import java.util.Map;

import edu.kit.student.graphmodel.CollapsedVertex;
import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.objectproperty.GAnsProperty;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class DirectedCollapsedVertex implements CollapsedVertex {

    DirectedGraph graph;
    public DirectedCollapsedVertex(String name, String label, DirectedGraph graph,
            Map<Vertex, DirectedEdge> collapsedVertexToCutEdge) {
  //      super(name, label, graph, collapsedVertexToCutEdge);
        this.graph = graph;
    }

    @Override
    public DirectedGraph getGraph() {
        return this.graph;
    }

    @Override
    public Vertex getConnectedVertex(Edge edge) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLabel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getX() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getY() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setX(int x) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setY(int y) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addToFastGraphAccessor(FastGraphAccessor fga) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<GAnsProperty<?>> getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Pair<Double, Double> getSize() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Color getColor() {
        // TODO Auto-generated method stub
        return null;
    }
}
