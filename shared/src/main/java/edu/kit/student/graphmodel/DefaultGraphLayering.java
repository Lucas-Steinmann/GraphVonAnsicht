package edu.kit.student.graphmodel;

import edu.kit.student.util.IntegerPoint;

import java.util.*;

public class DefaultGraphLayering<V extends Vertex> implements GraphLayering<V> {
    
    private ArrayList<ArrayList<V>> layers = new ArrayList<>();
    private Map<V, IntegerPoint> vertexToPoint = new HashMap<>();
    
    public DefaultGraphLayering(Set<V> vertices) {
        this.layers.add(new ArrayList<>(vertices));
        for (V vertex : vertices) {
            vertexToPoint.put(vertex, IntegerPoint.zero());
        }
    }

    @Override
    public int getLayerWidth(int layerN) {
        return layers.get(layerN).size();
    }

    @Override
    public int getMaxWidth() {
        int maxWidth = 0;
        for (List<V> layer : layers) {
            maxWidth = maxWidth < layer.size() ? layer.size() : maxWidth;
        }
        return maxWidth;
    }

    @Override
    public int getLayerCount() {
        return layers.size();
    }

    //TODO Remove duplicate getHeight/getLayerCount
    @Override
    public int getHeight() {
        return layers.size();
    }

    @Override
    public int getVertexCount(int layerNum) {
        return layers.get(layerNum).size();
    }

    @Override
    public int getLayerFromVertex(Vertex vertex) {
        return getPosition(vertex).y;
    }

    public IntegerPoint getPosition(Vertex vertex) {
        if (!this.vertexToPoint.containsKey(vertex)) {
            throw new IllegalArgumentException("Vertex is not contained in layering!");
        }
        return vertexToPoint.get(vertex).clone();
    }
    
    public V getVertex(IntegerPoint point) {
        List<V> layer = layers.get(point.y);
        if (layer.size() <= point.x) {
            return null;
        }
        return layer.get(point.x);
    }
    
    public void setPosition(V vertex, IntegerPoint point) {
    	IntegerPoint oldPos = getPosition(vertex);
        assert (vertex == getVertex(oldPos));
        // Remove from old position
        ArrayList<V> vs = this.layers.get(oldPos.y);
        if (oldPos.x >=  vs.size()) {
            System.out.println("Error");
        }
        vs.remove(oldPos.x);
        for (int i = oldPos.x; i < vs.size(); i++) {
            vertexToPoint.put(vs.get(i), new IntegerPoint(i, oldPos.y));
        }

        // Add enough layers to insert vertex
		for (int i = layers.size() - 1; i < point.y; i++) {
			this.layers.add(new ArrayList<>());
		}

		// Add to new position
		ArrayList<V> layer = this.layers.get(point.y);
        layer.add(point.x, vertex);
        vertexToPoint.put(vertex, point.clone());

        for (int i = point.x + 1; i < layer.size(); i++) {
            vertexToPoint.put(layer.get(i), new IntegerPoint(i, point.y));
        }

        if (vertex != getVertex(getPosition(vertex))) {
            System.out.println("Error");
        }
        assert (vertex == getVertex(getPosition(vertex)));
    }
    
    public void setLayer(V vertex, int layer) {
        if (getLayerFromVertex(vertex) == layer) {
            return;
        }
        List<V> assignedLayer = null;

        if (getHeight() <= layer) {
            assignedLayer = new LinkedList<>();
        } else {
            assignedLayer = layers.get(layer);
        }

        setPosition(vertex, new IntegerPoint(assignedLayer.size(), layer));
        assert (vertex == getVertex(getPosition(vertex)));
    }
    
    public void addVertex(V vertex, int layer) {
        this.vertexToPoint.put(vertex, new IntegerPoint(layers.get(0).size(), 0));
        this.layers.get(0).add(vertex);
        
        this.setLayer(vertex, layer);
    }

    @Override
    public List<V> getLayer(int layerIndex) {
        if (getHeight() <= layerIndex) {
            return new LinkedList<>();
        }
        return new LinkedList<>(layers.get(layerIndex));
    }

    @Override
    public List<List<V>> getLayers() {
        List<List<V>> copy = new LinkedList<>();
        for (List<V> layer : layers) {
            copy.add(new LinkedList<V>(layer));
        }
        return copy;
    }
    
}
