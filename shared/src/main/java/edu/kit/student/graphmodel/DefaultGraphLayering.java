package edu.kit.student.graphmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DefaultGraphLayering<V extends Vertex> implements GraphLayering<V> {
    
    private List<List<V>> layers;
    
    public DefaultGraphLayering(Set<V> vertices) {
        layers = new ArrayList<List<V>>();
    }


    @Override
    public int getHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getLayerWidth(int layerN) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxWidth() {
        // TODO Auto-generated method stub
        return 0;
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
    public int getLayerFromVertex(V vertex) {
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
}
