package edu.kit.student.graphmodel.viewable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import edu.kit.student.graphmodel.GraphModel;
import edu.kit.student.graphmodel.ViewableGraph;

public class GenericGraphModel extends GraphModel {
	
	private List<Node<DirectedViewableGraph>> graphs;
	
	public GenericGraphModel() {
		this.graphs = new LinkedList<>();
	}

	@Override
	public List<? extends ViewableGraph> getRootGraphs() {
		return graphs.stream()
					 .map(n -> n.data)
					 .collect(Collectors.toList());
	}

	@Override
	public DirectedViewableGraph getGraphFromId(Integer id) {
		if (id == 0) {
			return null;
		}
		return getGraphNodefromId(id).data;
	}
	
	private Node<DirectedViewableGraph> getGraphNodefromId(Integer id) {
		for (Node<DirectedViewableGraph> root : graphs) {
			for (Node<DirectedViewableGraph> node : root) {
				if (node.data.getID().equals(id)) {
					return node;
				}
			}
		} return null;
	}

	@Override
	public DirectedViewableGraph getParentGraph(ViewableGraph graph) {
		return getGraphNodefromId(graph.getID()).parent.data;
	}

	@Override
	public List<DirectedViewableGraph> getChildGraphs(ViewableGraph graph) {
		return getGraphNodefromId(graph.getID()).children.stream()
														 .map(n -> n.data)
														 .collect(Collectors.toList());
	}
	
	void addRootGraph(DirectedViewableGraph graph) {
		this.graphs.add(new Node<>(graph));
	}
	
	void addGraph(DirectedViewableGraph parent, DirectedViewableGraph graph) {
		Node<DirectedViewableGraph> parentNode = getGraphNodefromId(parent.id);
		assert (parentNode != null);
		parentNode.children.add(new Node<DirectedViewableGraph>(graph));
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("GenericGraphModel:\n");
		for (Node<DirectedViewableGraph> rootNode : graphs) {
			sb.append(rootNode.toString());
		}
		return sb.toString();
	}
	
	private class Node<T> implements Iterable<Node<T>> {
        private T data;
        private Node<T> parent;
        private List<Node<T>> children;
        
        public Node(T data) {
        	this.data = data;
        	children = new LinkedList<>();
        }
        
        public String toString() {
        	StringBuilder sb  = new StringBuilder(data.toString() + "\n");
        	for (Node<T> c : children) {
        		sb.append(Arrays.stream(c.toString().split("\n")).map(line -> "\t" + line).reduce("", (r, s) -> r + "\n" + s));
        	}
        	return sb.toString();
        }

		@Override
		public Iterator<Node<T>> iterator() {
			return new Iterator<Node<T>>() {
				
				private Iterator<Node<T>> it;
				private Node<T> next;
				private Iterator<Node<T>> childit;
				{
					childit = Node.this.children.iterator();
					next = Node.this;
					if (childit.hasNext()) {
						it = childit.next().iterator();
					}
//					if (it != null && it.hasNext()){
//						next = it.next();
//					}
				}

				@Override
				public boolean hasNext() {
					return next != null;
				}

				@Override
				public Node<T> next() {
					Node<T> ret = next;
					if (it != null && !it.hasNext() && childit.hasNext()) {
						it = childit.next().iterator();
					} 
					if (it != null && it.hasNext()){
						next = it.next();
					} else {
						next = null;
					}
					return ret;
				}
			};
		}	
        
	}
}
