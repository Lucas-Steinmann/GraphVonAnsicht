package edu.kit.student.joana;

import java.util.List;

import edu.kit.student.graphmodel.DefaultDirectedGraph;
import edu.kit.student.graphmodel.LayeredGraph;

/**
 * An abstract superclass for all JOANA specific graphs.
 */
public abstract class JoanaGraph extends DefaultDirectedGraph<JoanaVertex, JoanaEdge> implements LayeredGraph<JoanaVertex, JoanaEdge> {

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
}
