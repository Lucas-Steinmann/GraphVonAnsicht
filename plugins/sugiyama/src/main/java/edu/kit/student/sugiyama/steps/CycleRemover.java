package edu.kit.student.sugiyama.steps;

import java.util.HashSet;
import java.util.Set;

import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.sugiyama.graph.ICycleRemoverGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaEdge;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaVertex;

/**
 * This class takes a directed Graph G = (V, E) and removes a set of edges E_ 
 * so that the resulting Graph G' = (V, E\E_) is a DAG(Directed Acyclic Graph).
 */
public class CycleRemover implements ICycleRemover {
	private DefaultDirectedGraph DDGraph = new DefaultDirectedGraph("", 0);
	
	@Override
	public Set<SugiyamaEdge> removeCycles(ICycleRemoverGraph graph) {

		Set<SugiyamaVertex> graphVertices = graph.getVertexSet();
		Set<SugiyamaEdge> graphEdges = graph.getEdgeSet();
		for(SugiyamaVertex vertex:graphVertices){
			DDGraph.addVertex(vertex);
		}
		for(SugiyamaEdge edge: graphEdges){
			DDGraph.addEdge(edge);
		}
		Set<SugiyamaEdge> DAGEdges = new HashSet<SugiyamaEdge>();
		Set<SugiyamaVertex> DDVertices = DDGraph.getVertexSet();
		Set<SugiyamaEdge> DDEdges = DDGraph.getEdgeSet();
		

		//DDGraph.vertex set instead of vertices !!!!!!!
		while(!graphVertices.isEmpty()){
			SugiyamaVertex u = getSink(DDVertices);
			while(u!=null){	//add sink vertices to the edge set in the final directed acyclic graph
				DAGEdges.addAll(DDGraph.incomingEdgesOf(u));
				DDVertices.remove(u);
				DDEdges.removeAll(DDGraph.incomingEdgesOf(u));
				u = getSink(DDVertices);
			}
			for(SugiyamaVertex vertex:DDVertices){	//remove all isolated vertices
				if(DDGraph.indegreeOf(vertex)==0 && DDGraph.outdegreeOf(vertex)==0){
					DDVertices.remove(vertex);
				}
			}
			SugiyamaVertex v = getSource(DDVertices);
			while(v!=null){	//add source vertices to the edge set in the final directed acyclic graph
				DAGEdges.addAll(DDGraph.outgoingEdgesOf(v));
				DDVertices.remove(v);
				DDEdges.removeAll(DDGraph.outgoingEdgesOf(v));
				v = getSource(DDVertices);
			}
			if(!DDVertices.isEmpty()){
				int outInDiff = 0;
				SugiyamaVertex w = null;
				for(SugiyamaVertex vertex:DDVertices){
					if(Math.abs(DDGraph.outdegreeOf(vertex) - DDGraph.indegreeOf(vertex)) > outInDiff){
						w = vertex;
					}
				}
				DAGEdges.addAll(DDGraph.outgoingEdgesOf(w));
				DDVertices.remove(w);
				DDEdges.removeAll(DDGraph.outgoingEdgesOf(w));
				DDEdges.removeAll(DDGraph.incomingEdgesOf(w));
			}
		}
		return DAGEdges;
	}
	
	private SugiyamaVertex getSink(Set<SugiyamaVertex> vertices){
		for(SugiyamaVertex v:vertices){
			if(DDGraph.outdegreeOf(v)==0){
				return v;
			}
		}
		return null;
	}
	
	private SugiyamaVertex getSource(Set<SugiyamaVertex> vertices){
		for(SugiyamaVertex v:vertices){
			if(DDGraph.indegreeOf(v)==0){
				return v;
			}
		}
		return null;
	}

}
