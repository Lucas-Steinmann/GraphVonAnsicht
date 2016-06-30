package edu.kit.student.joana;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * An abstract superclass for all JOANA specific graphs.
 */
public abstract class JoanaGraph 
    extends DefaultDirectedGraph<JoanaVertex, JoanaEdge> 
    implements LayeredGraph<JoanaVertex, JoanaEdge> {
    

    public JoanaGraph(String name, Integer id) {
        this(name, id, null, null);
    }
    
    public JoanaGraph(String name, Integer id, Set<JoanaVertex> vertices, Set<JoanaEdge> edges) {
        super(name, id);
    }

    @Override
    public int getLayerCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getVertexCount(int layerNum) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getLayer(JoanaVertex vertex) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<JoanaVertex> getLayer(int layerNum) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<List<JoanaVertex>> getLayers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxWidth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getLayerWidth(int layerN) {
        // TODO Auto-generated method stub
        return 0;
    }

    
    private class Layering {
        
        //private Map<JoanaVertex, Point> v2p = new HashMap<>();
        public int getLayer(JoanaVertex vertex) {
            // TODO Auto-generated method stub
            return 0;
        }

        public List<JoanaVertex> getLayer(int layerNum) {
            // TODO Auto-generated method stub
            return null;
        }

        public List<List<JoanaVertex>> getLayers() {
            // TODO Auto-generated method stub
            return null;
        }

        public int getHeight() {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getMaxWidth() {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getLayerWidth(int layerN) {
            // TODO Auto-generated method stub
            return 0;
        }
    }
}
