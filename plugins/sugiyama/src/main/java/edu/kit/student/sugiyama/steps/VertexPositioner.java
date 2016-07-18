package edu.kit.student.sugiyama.steps;

import edu.kit.student.sugiyama.graph.ISugiyamaVertex;
import edu.kit.student.sugiyama.graph.IVertexPositionerGraph;
import edu.kit.student.util.IntegerPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * This class takes a directed graph and position its vertices in order to look more clearly. (e.g. position vertices in a row or column)
 */
public class VertexPositioner implements IVertexPositioner {

	@Override
	public void positionVertices(IVertexPositionerGraph graph) {
		System.out.println("VertexPositioner.positionVertices():");
		int maxwidth = graph.getLayers().stream().mapToInt(layer -> layer.size()).max().getAsInt();
		List<List<List<ISugiyamaVertex>>> vertexMatrix = new ArrayList<>();
		List<VertexLine> vertexLines = new ArrayList<>();

		for (int i = 0; i < graph.getLayerCount(); i++) {
			vertexMatrix.add(new ArrayList<>());
			int j = 0;

			for (ISugiyamaVertex vertex : graph.getLayer(i)) {
				//if (vertex.isDummy() && j > 0 && vertexMatrix.get(i).get(j-1).get(0).isDummy() && vertexMatrix.get(i).get(j-1).size() < 15) {
				//	vertexMatrix.get(i).get(j-1).add(vertex);
				//} else {
					vertexMatrix.get(i).add(new ArrayList<>());
					vertexMatrix.get(i).get(j).add(vertex);
					vertex.setY(i);
					vertex.setX(j);
					j++;
				//}
			}
		}

		boolean[][] visited = new boolean[graph.getLayerCount()][maxwidth];

		//find lines
		for (int i = 0; i < vertexMatrix.size(); i++) {
			for (int j = 0; j < vertexMatrix.get(i).size(); j++) {
				if (visited[i][j]) {
					continue;
				}

				ISugiyamaVertex vertex = vertexMatrix.get(i).get(j).get(0);
				visited[i][j] = true;

				if (vertex.isDummy()) {
					VertexLine line = new VertexLine();
					line.add(new IntegerPoint(i, j));

					ISugiyamaVertex nextVertex = graph.outgoingEdgesOf(vertex).stream().findFirst().get().getTarget();
					int counter = 1;

					while (nextVertex.isDummy()) {
						visited[nextVertex.getX()][nextVertex.getY()] = true;
						line.add(new IntegerPoint(nextVertex.getX(), nextVertex.getY()));
						counter++;
					}

					if (line.size() > 1) {
						vertexLines.add(line);
					}
				}
			}
		}

		//mergeLines
		for (VertexLine line: vertexLines) {

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

	private class VertexLine {
		private List<IntegerPoint> positions;

		public VertexLine() {
			positions = new ArrayList<>();
		}

		public boolean add(IntegerPoint integerPoint) {
			return positions.add(integerPoint);
		}

		public int size() {
			return positions.size();
		}
	}
}
