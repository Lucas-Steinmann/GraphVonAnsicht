package edu.kit.student.sugiyama.steps;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.sugiyama.graph.ISugiyamaEdge;
import edu.kit.student.sugiyama.graph.ISugiyamaVertex;
import edu.kit.student.sugiyama.graph.IVertexPositionerGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;

import java.util.*;

/**
 * This class takes a directed graph and position its vertices in order to look more clearly. (e.g. position vertices in a row or column)
 */
public class VertexPositioner implements IVertexPositioner {

	@Override
	public void positionVertices(IVertexPositionerGraph graph) {
		System.out.println("VertexPositioner.positionVertices():");
		int maxwidth = graph.getLayers().stream().mapToInt(layer -> layer.size()).max().getAsInt();
		List<List<Segment>> segments = new LinkedList<>();
		List<Segment> allsegments = new LinkedList<>();
		Map<Integer, List<ISugiyamaVertex>> segmentStarts = new HashMap<>();
		Map<Vertex, Segment> vertexToSegment = new HashMap<>();

		int[] horizontalPositions = new int[maxwidth];
		int[] horizontalWidth = new int[maxwidth*4];
		int[] horizontalOffset = new int[maxwidth*4];
		int[] localOffsets = new int[maxwidth];
		boolean[][] blocked = new boolean[graph.getLayerCount()][maxwidth*4];
		int horizontalSpacing = 2;
		HorizontalDirection horizontalDirection = HorizontalDirection.RIGHT;

		for (int i = 0; i < maxwidth*4; i++) {
			segmentStarts.put(i, new LinkedList<>());
		}

		for (int i = 0; i < graph.getLayerCount(); i++) {
			int j = 0;

			for (ISugiyamaVertex vertex : graph.getLayer(i)) {
				vertex.setX(j);
				vertex.setY(i);
				++j;
			}
		}

		Set<SugiyamaGraph.SupplementPath> currentSupplementPaths = graph.getSupplementPaths();
		Set<SugiyamaGraph.SupplementPath> nextSupplementPaths = new HashSet<>();
		int run = 0;

		while (currentSupplementPaths.size() > 0) {
			List<Segment> runList = new LinkedList<>();
			segments.add(runList);

			for (SugiyamaGraph.SupplementPath path : currentSupplementPaths) {
				Segment probableNewSegment = new Segment(path.getDummyVertices(), path.getSupplementEdges());

				boolean crossesExisting = false;

				for (Segment existingSegment : runList) {
					if (probableNewSegment.crosses(existingSegment)) {
						crossesExisting = true;
						break;
					}
				}

				if (!crossesExisting) {
					allsegments.add(probableNewSegment);
					runList.add(probableNewSegment);
					path.getDummyVertices().forEach(vertex -> vertexToSegment.put(vertex, probableNewSegment));
					segmentStarts.get(path.getDummyVertices().get(0).getX()).add(path.getDummyVertices().get(0));

					//((JoanaEdge) path.getReplacedEdge().getWrappedEdge()).setEdgeKind(JoanaEdge.Kind.DEBUG);
				} else {
					nextSupplementPaths.add(path);
				}
			}

			currentSupplementPaths = nextSupplementPaths;
			nextSupplementPaths = new HashSet<>();
			run++;
		}


/*		Set<ISugiyamaEdge> edges = graph.getEdgeSet().stream().filter(edge -> !edge.isSupplementEdge()).collect(Collectors.toSet());

		for (ISugiyamaEdge edge : edges) {
			List<ISugiyamaEdge> containedEdges = new LinkedList<>();
			containedEdges.add(edge);
			List<ISugiyamaVertex> containedVertices = new LinkedList<>();
			containedVertices.add(edge.getTarget());
			containedVertices.add(edge.getSource());

			Segment probableNewSegment = new Segment(containedVertices, containedEdges);

			boolean crossesExisting = false;

			for (Segment existingSegment : segments) {
				if (probableNewSegment.crosses(existingSegment)) {
					crossesExisting = true;
					break;
				}
			}

			if (!crossesExisting) {
				segments.add(probableNewSegment);
				containedVertices.forEach(vertex -> vertexToSegment.put(vertex, probableNewSegment));
				((JoanaEdge) edge.getWrappedEdge()).setEdgeKind(JoanaEdge.Kind.DEBUG);
			}
		}*/

		segments.forEach(segment -> segment.sort((o1, o2) -> (o1.getVertices().stream().mapToInt(vertex -> vertex.getX()).max().getAsInt() - o2.getVertices().stream().mapToInt(vertex -> vertex.getX()).max().getAsInt()) * 10000 + (o1.getVertices().get(0).getY() - o2.getVertices().get(0).getY())));
		//Collections.reverse(segments);

		for (List<Segment> segmentRun : segments) {
			for (Segment segment : segmentRun) {
				int extremePosition = segment.getVertices().get(0).getX();
				/*for (int i = 0; i < 10; i++) {
					if (!isBlocked(extremePosition, segment.getVertices().get(0).getY(), segment.getVertices().get(segment.getVertices().size() - 1).getY(), blocked)) {
						break;
					}

					extremePosition++;
				}*/

				correctSegment(segment, extremePosition, graph);
				segmentStarts.get(extremePosition).remove(segment);

				for (ISugiyamaVertex segmentStart : segmentStarts.get(extremePosition)) {
					segmentStart.setX(extremePosition + 1);
				}

				if (!segmentStarts.containsKey(extremePosition + 1)) {
					segmentStarts.put(extremePosition + 1, new LinkedList<>());
				}

				segmentStarts.get(extremePosition + 1).addAll(segmentStarts.get(extremePosition));
				segmentStarts.get(extremePosition).add(segment.getVertices().get(0));
			}
		}

		for (Vertex vertex : graph.getVertexSet()) {
			horizontalWidth[vertex.getX()] = Math.max(horizontalWidth[vertex.getX()], Math.round(vertex.getSize().getKey().floatValue()));
		}

		horizontalOffset[0] = 0;

		for (int i = 1; i < horizontalWidth.length; i++) {
			horizontalOffset[i] = horizontalOffset[i - 1] + horizontalWidth[i - 1] + horizontalSpacing;
		}

		for (Vertex vertex : graph.getVertexSet()) {
			vertex.setX(horizontalOffset[vertex.getX()]);
			vertex.setY(vertex.getY() * 60);
		}
	}

	private boolean isBlocked(int x, int yStart, int yStop, boolean[][] blocked) {
		for (int i = yStart; i < yStop; i++) {
			if (blocked[i][x]) {
				return true;
			}
		}

		return false;
	}

	private void correctSegment(Segment segment, int newX, IVertexPositionerGraph graph) {
		if (segment.isCorrected()) {
			return;
		}

		for (ISugiyamaVertex vertex : segment.getVertices()) {
			correctVertex(vertex, newX, graph);
		}

		segment.setCorrected(true);
	}

	private void correctVertex(ISugiyamaVertex vertex, int newX, IVertexPositionerGraph graph) {
		int oldX = vertex.getX();
		vertex.setX(newX);

		List<ISugiyamaVertex> layer = graph.getLayer(vertex.getY());

		for (int i = oldX + 1; i < layer.size(); i++) {
			layer.get(i).setX(newX + i);
		}
	}

	private class Segment {
		private List<ISugiyamaVertex> vertices;
		private List<ISugiyamaEdge> edges;
		private boolean corrected;

		public Segment(List<ISugiyamaVertex> vertices, List<ISugiyamaEdge> edges) {
			this.vertices = vertices;
			this.vertices.sort((o1, o2) -> o1.getLayer() - o2.getLayer());
			this.edges = edges;
			corrected = false;
		}

		public List<ISugiyamaVertex> getVertices() {
			return vertices;
		}

		public List<ISugiyamaEdge> getEdges() {
			return edges;
		}

		public boolean crosses(Segment other) {
			List<ISugiyamaVertex> otherVertices = other.getVertices();

			if (vertices.get(0).getLayer() > otherVertices.get(otherVertices.size() - 1).getLayer() || otherVertices.get(0).getLayer() > vertices.get(vertices.size() - 1).getLayer()) {
				return false;
			}

			int vOffset = vertices.get(0).getLayer();
			int oOffset = otherVertices.get(0).getLayer();

			int startLayer = Math.max(vOffset, oOffset);
			int endLayer = Math.min(vertices.get(vertices.size() - 1).getLayer(), otherVertices.get(otherVertices.size() - 1).getLayer());

			if (endLayer - startLayer == 1) {
				return false;
			}


			boolean firstIsLeft = (vertices.get(startLayer - vOffset).getX() - otherVertices.get(startLayer - oOffset).getX()) < 0;

			for (int i = startLayer + 1; i < endLayer; i++) {

				boolean isStillLeft = (vertices.get(i - vOffset).getX() - otherVertices.get(i - oOffset).getX()) < 0;

				if (firstIsLeft != isStillLeft) {
					return true;
				}

				firstIsLeft = isStillLeft;
			}

			return true;
		}

		public boolean isCorrected() {
			return corrected;
		}

		public void setCorrected(boolean corrected) {
			this.corrected = corrected;
		}
	}

	private enum HorizontalDirection {
		LEFT, RIGHT;
	}
}
