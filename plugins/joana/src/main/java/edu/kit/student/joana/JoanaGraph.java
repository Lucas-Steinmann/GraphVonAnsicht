package edu.kit.student.joana;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract superclass for all JOANA specific graphs.
 */
public abstract class JoanaGraph<V extends JoanaVertex, E extends JoanaEdge<V>>
    extends DefaultDirectedGraph<V, E>
    implements LayeredGraph<V, E> {

    // TODO: Ist eine statische maximalbreite hier richtig?
    private static int maxWidth = 600;
    private List<List<V>> layers;
    
    public JoanaGraph(String name) {
        super(name);
        layers = new ArrayList<List<V>>();
    }

    @Override
    public int getLayerCount() {
        return layers.size();
    }

    @Override
    public int getVertexCount(int layerNum) {
        return layers.get(layerNum).size();
    }

    @Override
    public int getLayerFromVertex(JoanaVertex vertex) {
        for(int i = 0; i < layers.size(); i++){
            if(layers.get(i).contains(vertex)) return i;
        }
        return -1;
    }

    @Override
    public List<V> getLayer(int layerNum) {
        return layers.get(layerNum);
    }

    @Override
    public List<List<V>> getLayers() {
        return layers;
    }

    @Override
    public int getHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxWidth() {
        return maxWidth;
    }

    @Override
    public int getLayerWidth(int layerN) {
        // TODO Auto-generated method stub
        return 0;
    }
}