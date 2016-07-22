package edu.kit.student.sugiyama.steps;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.sugiyama.graph.ISugiyamaVertex;
import edu.kit.student.sugiyama.graph.IVertexPositionerGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class takes a directed graph and position its vertices in order to look more clearly. (e.g. position vertices in a row or column)
 */
public class VertexPositioner implements IVertexPositioner {

	@Override
	public void positionVertices(IVertexPositionerGraph graph) {
		System.out.println("VertexPositioner.positionVertices():");
		int maxwidth = graph.getLayers().stream().mapToInt(layer -> layer.size()).max().getAsInt();
		List<Segment> allsegments = new LinkedList<>();
		Map<Integer, List<ISugiyamaVertex>> segmentStarts = new HashMap<>();
		Map<Vertex, Segment> vertexToSegment = new HashMap<>();

		int[] horizontalWidth = new int[maxwidth*graph.getLayerCount()];
		int[] horizontalOffset = new int[maxwidth*graph.getLayerCount()];
		int horizontalSpacing = 2;
		int[] verticalHeight = new int[graph.getLayerCount()];
		int[] verticalOffset = new int[graph.getLayerCount()];
		int verticalSpacing = 70;

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

		//add all paths to segments
		List<SugiyamaGraph.SupplementPath> paths = new LinkedList<>();
		paths.addAll(graph.getSupplementPaths());
		paths.sort((o1, o2) -> o1.getLength() - o2.getLength());
		int counter = 0;

		for (SugiyamaGraph.SupplementPath path : paths) {
			Segment probableNewSegment = new Segment(path.getDummyVertices());
			List<Segment> newSegments = probableNewSegment.cutWithSegments(allsegments);

			for (Segment segment : newSegments) {
				for (ISugiyamaVertex vertex : segment.getVertices()) {
					vertexToSegment.put(vertex, segment);
				}
			}

			//System.out.println("run " + counter++ + " adds " + newSegments.size() + " segments");
			
			allsegments.addAll(newSegments);
		}

		//add all other vertices as segments
		for (ISugiyamaVertex vertex : graph.getVertexSet()) {
			if (!vertex.isDummy()) {
				List<ISugiyamaVertex> list = new LinkedList<ISugiyamaVertex>();
				list.add(vertex);
				Segment newSegment = new Segment(list);
				allsegments.add(newSegment);
				vertexToSegment.put(vertex, newSegment);
				//System.out.println("rest run " + counter++ + " adds 1 segments");
			}
		}

		//make all segments a line by making all vertices align (pun may or may not be intended)
		for (Segment segment : allsegments) {
			segment.align(graph);
		}

		allsegments.sort((o1, o2) -> (o1.getBoundingBox().left - o2.getBoundingBox().left) * graph.getLayerCount() + (o1.getBoundingBox().top - o2.getBoundingBox().top));

		for (Segment segment : allsegments) {
			for (Segment other : allsegments) {
				if (segment.equals(other)) {
					continue;
				}
				if (segment.intersects(other)) {
					other.move(1, graph);
				}
			}
		}

		for (ISugiyamaVertex vertex : graph.getVertexSet()) {
		}

		for (Vertex vertex : graph.getVertexSet()) {
			System.out.println(vertex.getSize());
			horizontalWidth[vertex.getX()] = Math.max(horizontalWidth[vertex.getX()], Math.round(vertex.getSize().getKey().floatValue()));
			verticalHeight[vertex.getY()] = Math.max(verticalHeight[vertex.getY()], Math.round(vertex.getSize().getValue().floatValue()));
		}

		horizontalOffset[0] = 0;
		verticalOffset[0] = 0;

		for (int i = 1; i < horizontalWidth.length; i++) {
			horizontalOffset[i] = horizontalOffset[i - 1] + horizontalWidth[i - 1] + horizontalSpacing;
		}

		for (int i = 1; i < verticalHeight.length; i++) {
			verticalOffset[i] = verticalOffset[i - 1] + verticalHeight[i - 1] + verticalSpacing;
		}

		for (int i : verticalHeight) {
			System.out.println(i);
		}

		for (Vertex vertex : graph.getVertexSet()) {
			vertex.setX(horizontalOffset[vertex.getX()]);
			vertex.setY(verticalOffset[vertex.getY()]);
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
		private boolean corrected;
		private boolean changed;
		private BoundingBox boundingBox;

		public Segment(List<ISugiyamaVertex> vertices) {
			this.vertices = vertices;
			this.vertices.sort((o1, o2) -> o1.getLayer() - o2.getLayer());
			corrected = false;
			changed = true;
		}

		public List<ISugiyamaVertex> getVertices() {
			return vertices;
		}

		public void move(int amount, IVertexPositionerGraph graph) {
			for (ISugiyamaVertex vertex : this.vertices) {
				graph.setX(vertex, vertex.getX() + amount);
			}

			this.changed = true;
		}

		public void align(IVertexPositionerGraph graph) {
			int x = this.vertices.get(0).getX();

			for (ISugiyamaVertex vertex : this.vertices) {
				graph.setX(vertex, x);
			}

			this.changed = true;
		}

		public List<Segment> cutWithSegments(List<Segment> others) {
			List<Segment> result = new LinkedList<>();

			for (Segment segment : others)  {
				if (!intersects(segment)) {
					continue;
				}

				List<ISugiyamaVertex> otherVertices = segment.getVertices();
				int vOffset = vertices.get(0).getLayer();
				int oOffset = otherVertices.get(0).getLayer();

				int startLayer = Math.max(vOffset, oOffset);
				int endLayer = Math.min(vertices.get(vertices.size() - 1).getLayer(), otherVertices.get(otherVertices.size() - 1).getLayer());


				if (endLayer - startLayer == 0) {
					continue;
				}

				boolean firstIsLeft = false;
				firstIsLeft = (vertices.get(startLayer - vOffset).getX() - otherVertices.get(startLayer - oOffset).getX()) < 0;

				for (int i = startLayer + 1; i < endLayer; i++) {

					boolean isStillLeft = (vertices.get(i - vOffset).getX() - otherVertices.get(i - oOffset).getX()) < 0;

					if (firstIsLeft != isStillLeft) {
						result.addAll((new Segment(this.vertices.subList(0, i)).cutWithSegments(others)));
						result.addAll((new Segment(this.vertices.subList(i, this.vertices.size())).cutWithSegments(others)));
						break;
					}

					firstIsLeft = isStillLeft;
				}
			}

			if (result.size() == 0) {
				result.add(this);
			}

			return result;
		}

		private boolean intersects(Segment other) {
			return getBoundingBox().intersects(other.getBoundingBox());
		}

		public boolean isCorrected() {
			return corrected;
		}

		public void setCorrected(boolean corrected) {
			this.corrected = corrected;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Segment segment = (Segment) o;

			if (isCorrected() != segment.isCorrected()) return false;
			if (changed != segment.changed) return false;
			return getVertices().equals(segment.getVertices());

		}

		@Override
		public int hashCode() {
			int result = getVertices().hashCode();
			result = 31 * result + (isCorrected() ? 1 : 0);
			result = 31 * result + (changed ? 1 : 0);
			return result;
		}

		public BoundingBox getBoundingBox() {
			if (changed) {
				int startX = Integer.MAX_VALUE;
				int startY = Integer.MAX_VALUE;
				int endX = 0;
				int endY = 0;

				for (ISugiyamaVertex vertex : vertices) {
					if (vertex.getX() < startX) {
						startX = vertex.getX();
					}
					if (vertex.getY() < startY) {
						startY = vertex.getY();
					}
					if (vertex.getX() > endX) {
						endX = vertex.getX();
					}
					if (vertex.getY() > endY) {
						endY = vertex.getY();
					}
				}

				this.boundingBox = new BoundingBox(startX, startY, endX, endY);
				changed = false;
			}

			return boundingBox;
		}

		private class BoundingBox {
			private final int left;
			private final int top;
			private final int right;
			private final int bottom;

			public BoundingBox(int left, int top, int right, int bottom) {
				this.left = left;
				this.top = top;
				this.right = right;
				this.bottom = bottom;
			}

			public boolean intersects(BoundingBox other) {
				return !(other.left >= this.right
						|| other.right <= this.left
						|| other.top >= this.bottom
						|| other.bottom <= this.top);
			}

			@Override
			public String toString() {
				return "BoundingBox{" +
						"left=" + left +
						", top=" + top +
						", right=" + right +
						", bottom=" + bottom +
						'}';
			}
		}
	}

	private enum HorizontalDirection {
		LEFT, RIGHT;
	}
}
