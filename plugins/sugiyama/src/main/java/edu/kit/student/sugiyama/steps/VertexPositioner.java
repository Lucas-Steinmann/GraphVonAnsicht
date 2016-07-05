package edu.kit.student.sugiyama.steps;

import edu.kit.student.sugiyama.graph.IVertexPositionerGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;

/**
 * This class takes a directed graph and position its vertices in order to look more clearly. (e.g. position vertices in a row or column)
 */
public class VertexPositioner implements IVertexPositioner {

	@Override
	public void positionVertices(IVertexPositionerGraph graph) {
		int maxwidth = graph.getLayers().stream().mapToInt(layer -> layer.size()).min().getAsInt() * 4;
		SugiyamaGraph.SugiyamaVertex[][] vertices = new SugiyamaGraph.SugiyamaVertex[maxwidth][graph.getLayerCount()];

		for (int i = 0; i < graph.getLayerCount(); i++) {
			int j = 0;

			for (SugiyamaGraph.SugiyamaVertex vertex : graph.getLayer(i)) {
				vertices[j][i] = vertex;

				j++;
			}
		}

		for (int i = 0; i < vertices.length; i++) {
			for (int j = 0; j < vertices[0].length; j++) {
				if (vertices[i][j] != null) {
					vertices[i][j].setX(i*20);
					vertices[i][j].setY(j*20);
				}
			}
		}
	}


}
