package edu.kit.student.joana;

import java.util.List;

import edu.kit.student.graphmodel.LayeredGraph;

/**
 * A {@link JoanaGraph} which specifies a {@link FieldAccess} in a {@link JoanaGraph}
 */
public class FieldAccessGraph extends JoanaGraph {

	public FieldAccessGraph(String name, Integer id) {
        super(name, id);
        // TODO Auto-generated constructor stub
    }

    @Override
	public int getLayerWidth(int layerN) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<LayeredGraph<JoanaVertex, JoanaEdge>> getSubgraphs() {
		// TODO Auto-generated method stub
		return null;
	}
}
