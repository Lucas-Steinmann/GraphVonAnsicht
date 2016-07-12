/**
 * 
 */
package edu.kit.student.sugiyama;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.plugin.LayoutAlgorithm;

/**
 * A layout algorithm which is able to apply Layouts while keeping the notion of layers in addition to layout regular graphs.
 */
public interface LayeredLayoutAlgorithm<G extends LayeredGraph> extends LayoutAlgorithm<G> {

	/**
	 * Applies its layout to a graph as in {@code layout(G graph)} but keeps the notion of layers.
	 * The algorithm will assign every vertex a coordinate and every edge a path.
	 * Additionally every vertex will be assigned a position in a layer in the LayeredGraph.
	 * A possible application is drawing of recursive graphs.
	 * @param graph the graph to apply the layout to
	 */
	public void layoutLayeredGraph(G graph);

}
