package edu.kit.student.sugiyama.steps;

import edu.kit.student.sugiyama.graph.ISugiyamaVertex;
import edu.kit.student.sugiyama.graph.IVertexPositionerGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * This class takes a directed graph and position its vertices in order to look more clearly. (e.g. position vertices in a row or column)
 */
public class VertexPositioner implements IVertexPositioner {

	@Override
	public void positionVertices(IVertexPositionerGraph graph) {
		int maxwidth = graph.getLayers().stream().mapToInt(layer -> layer.size()).max().getAsInt() * 4;
		List<List<List<ISugiyamaVertex>>> vertexMatrix = new ArrayList<>();

		for (int i = 0; i < graph.getLayerCount(); i++) {
			vertexMatrix.add(new ArrayList<>());
			int j = 0;

			for (ISugiyamaVertex vertex : graph.getLayer(i)) {
				if (vertex.isDummy() && j > 0 && vertexMatrix.get(i).get(j-1).get(0).isDummy() && vertexMatrix.get(i).get(j-1).size() < 15) {
					vertexMatrix.get(i).get(j-1).add(vertex);
				} else {
					vertexMatrix.get(i).add(new ArrayList<>());
					vertexMatrix.get(i).get(j).add(vertex);
					j++;
				}
			}
		}

		for (int i = 0; i < vertexMatrix.size(); i++) {
			for (int j = 0; j < vertexMatrix.get(i).size(); j++) {
				if (vertexMatrix.get(i).get(j) != null) {
					int counter = 0;

					for (ISugiyamaVertex vertex: vertexMatrix.get(i).get(j) ) {
						vertex.setY(i*50);
						vertex.setX(j*100+counter*3);
						counter++;
					}
				}
			}
		}
	}


}
