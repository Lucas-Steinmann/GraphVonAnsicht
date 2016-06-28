package edu.kit.student.joana.callgraph;

import java.util.List;
import java.util.Set;

import edu.kit.student.graphmodel.CompoundVertex;
import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraph;
import edu.kit.student.joana.JoanaVertex;

/**
 * This is a specified graph representation for the Callgraph in Joana.
 */
public class CallGraph extends JoanaGraph {

    public CallGraph(String name, Integer id) {
        super(name, id);
        // TODO Auto-generated constructor stub
    }

    @Override
	public CompoundVertex collapse(Set<JoanaVertex> subset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<JoanaVertex> expand(CompoundVertex vertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCompound(Vertex vertex) {
		// TODO Auto-generated method stub
		return false;
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
