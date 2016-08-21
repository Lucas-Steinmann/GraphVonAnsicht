package edu.kit.student.sugiyama.steps;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.sugiyama.graph.ISugiyamaEdge;
import edu.kit.student.sugiyama.graph.ISugiyamaVertex;
import edu.kit.student.sugiyama.graph.IVertexPositionerGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class takes a directed graph and position its vertices in order to look more clearly. (e.g. position vertices in a row or column)
 */
public class VertexPositioner implements IVertexPositioner {

    final Logger logger = LoggerFactory.getLogger(VertexPositioner.class);

	@Override
	public void positionVertices(IVertexPositionerGraph graph) {
		logger.info("VertexPositioner.positionVertices():");

		if (graph.getVertexSet().size() == 0) {
			return;
		}

		int maxwidth = graph.getLayers().stream().mapToInt(layer -> layer.size()).max().getAsInt();
		List<Segment> allsegments = new LinkedList<>();
		Set<ISugiyamaVertex> addedVertices = new HashSet<>();
		Map<Integer, List<ISugiyamaVertex>> segmentStarts = new HashMap<>();
		Map<Vertex, Segment> vertexToSegment = new HashMap<>();

		int[] horizontalWidth = new int[maxwidth*graph.getLayerCount()*10];
		int[] horizontalOffset = new int[maxwidth*graph.getLayerCount()*10];
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
			List<ISugiyamaVertex> vertices = path.getDummyVertices();
			ISugiyamaVertex source = path.getReplacedEdge().getSource();
			ISugiyamaVertex target = path.getReplacedEdge().getTarget();

			if (!addedVertices.contains(source)) {
				vertices.add(source);
				addedVertices.add(source);
			}
			if (!addedVertices.contains(target)) {
				vertices.add(target);
				addedVertices.add(target);
			}

			Segment probableNewSegment = new Segment(vertices);
			List<Segment> newSegments = probableNewSegment.cutWithSegments(allsegments);

			for (Segment segment : newSegments) {
				for (ISugiyamaVertex vertex : segment.getVertices()) {
					vertexToSegment.put(vertex, segment);
				}
			}

			//System.out.println("run " + counter++ + " adds " + newSegments.size() + " segments");

			allsegments.addAll(newSegments);
		}

		//add all possible edges as segments
		for (ISugiyamaEdge edge : graph.getEdgeSet()) {
			if (edge.getSource().isDummy() || edge.getTarget().isDummy() || addedVertices.contains(edge.getSource()) || addedVertices.contains(edge.getTarget())) {
				continue;
			}
			if (edge.getTarget().getLayer() - edge.getSource().getLayer() == 0) {
				continue;
			}

			List<ISugiyamaVertex> vertices = new LinkedList<>();
			vertices.add(edge.getSource());
			vertices.add(edge.getTarget());
			addedVertices.add(edge.getSource());
			addedVertices.add(edge.getTarget());
			Segment probableNewSegment = new Segment(vertices);
			List<Segment> newSegments = probableNewSegment.cutWithSegments(allsegments);

			for (Segment segment : newSegments) {
				for (ISugiyamaVertex vertex : segment.getVertices()) {
					vertexToSegment.put(vertex, segment);
				}
			}

			allsegments.addAll(newSegments);
		}

		//add all other vertices as segments
		for (ISugiyamaVertex vertex : graph.getVertexSet()) {
			if (!vertex.isDummy() && !addedVertices.contains(vertex)) {
				List<ISugiyamaVertex> list = new LinkedList<ISugiyamaVertex>();
				list.add(vertex);
				Segment newSegment = new Segment(list);
				allsegments.add(newSegment);
				vertexToSegment.put(vertex, newSegment);
				addedVertices.add(vertex);
				//System.out.println("rest run " + counter++ + " adds 1 segments");
			}
		}

		//make all segments a line by making all vertices align (pun may or may not be intended)
		for (Segment segment : allsegments) {
			segment.align(graph);
		}

		allsegments.sort((o1, o2) -> (o1.getBoundingBox().left - o2.getBoundingBox().left) * graph.getLayerCount() + (o1.getBoundingBox().top - o2.getBoundingBox().top));

		boolean changes = true;
		int runs = 0; //safeguard
		while (changes && runs < 1000) {
			changes = false;

			for (Segment segment : allsegments) {
				for (Segment other : allsegments) {
					if (segment.getId() == other.getId()) {
						continue;
					}
					if (segment.intersects(other)) {
						changes = true;
						other.move(1, graph);
					}
				}
			}

			runs++;
		}

		logger.debug("last was overlapping: " + changes);

		for (ISugiyamaVertex vertex : graph.getVertexSet()) {
		}

		for (Vertex vertex : graph.getVertexSet()) {
			//logger.debug(vertex.getSize().toString());
			horizontalWidth[vertex.getX()] = Math.max(horizontalWidth[vertex.getX()], Math.round((float) vertex.getSize().x));
			verticalHeight[vertex.getY()] = Math.max(verticalHeight[vertex.getY()], Math.round((float) vertex.getSize().y));
		}

		horizontalOffset[0] = 0;
		verticalOffset[0] = 0;

		for (int i = 1; i < horizontalWidth.length; i++) {
			horizontalOffset[i] = horizontalOffset[i - 1] + horizontalWidth[i - 1] + horizontalSpacing;
		}

		for (int i = 1; i < verticalHeight.length; i++) {
			verticalOffset[i] = verticalOffset[i - 1] + verticalHeight[i - 1] + verticalSpacing;
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

	private class Segment {
		private List<ISugiyamaVertex> vertices;
		private boolean corrected;
		private boolean changed;
		private BoundingBox boundingBox;
		private int id;

		public Segment(List<ISugiyamaVertex> vertices) {
			this.vertices = vertices;
			this.vertices.sort((o1, o2) -> o1.getLayer() - o2.getLayer());
			corrected = false;
			changed = true;
			id = IdGenerator.getInstance().createId();
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

			boolean stop = false;
			for (Segment segment : others)  {
				if (!intersects(segment)) {
					continue;
				}

				List<ISugiyamaVertex> otherVertices = segment.getVertices();
				int vOffset = this.vertices.get(0).getLayer();
				int oOffset = otherVertices.get(0).getLayer();

				int startLayer = Math.max(vOffset, oOffset);
				int endLayer = Math.min(this.vertices.get(this.vertices.size() - 1).getLayer(), otherVertices.get(otherVertices.size() - 1).getLayer());


				if (endLayer - startLayer == 0) {
					continue;
				}

				boolean firstIsLeft = false;
				firstIsLeft = (this.vertices.get(startLayer - vOffset).getX() - otherVertices.get(startLayer - oOffset).getX()) < 0;

				for (int i = startLayer + 1; i < endLayer; i++) {

					boolean isStillLeft = (this.vertices.get(i - vOffset).getX() - otherVertices.get(i - oOffset).getX()) < 0;

					if (firstIsLeft != isStillLeft) {
						result.addAll((new Segment(new LinkedList<ISugiyamaVertex>(this.vertices.subList(0, i - vOffset))).cutWithSegments(others)));
						result.addAll((new Segment(new LinkedList<ISugiyamaVertex>(this.vertices.subList(i - vOffset, this.vertices.size()))).cutWithSegments(others)));
						stop = true;
						break;
					}

					firstIsLeft = isStillLeft;
				}

				if (stop == true) {
					break;
				}
			}

			if (result.size() == 0) {
				result.add(this);
			}

			return result;
		}

		@Override
		public String toString() {
			return "Segment{" +
					"vertices=" + vertices +
					", changed=" + changed +
					", boundingBox=" + boundingBox +
					", id=" + id +
					'}';
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

		public int getId() {
			return id;
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
				return !(other.left > this.right
						|| other.right < this.left
						|| other.top > this.bottom
						|| other.bottom < this.top);
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
