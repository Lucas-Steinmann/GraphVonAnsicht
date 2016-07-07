package edu.kit.student.sugiyama.steps;

import edu.kit.student.sugiyama.graph.ISugiyamaVertex;
import edu.kit.student.sugiyama.graph.IVertexPositionerGraph;

/**
 * This class takes a directed graph and position its vertices in order to look more clearly. (e.g. position vertices in a row or column)
 */
public class VertexPositioner implements IVertexPositioner {

	@Override
	public void positionVertices(IVertexPositionerGraph graph) {
		int maxwidth = graph.getLayers().stream().mapToInt(layer -> layer.size()).max().getAsInt() * 4;
		ISugiyamaVertex[][] vertices = new ISugiyamaVertex[graph.getLayerCount()][maxwidth];

		for (int i = 0; i < graph.getLayerCount(); i++) {
			int j = 0;

			for (ISugiyamaVertex vertex : graph.getLayer(i)) {
				vertices[i][j] = vertex;

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
