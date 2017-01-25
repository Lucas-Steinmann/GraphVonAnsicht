package edu.kit.student.joana;

import java.util.List;

import edu.kit.student.joana.JoanaEdge.EdgeKind;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.util.DoublePoint;
import edu.kit.student.util.IntegerPoint;

public class InterproceduralVertex extends JoanaVertex{
	//TODO: maybe name it InterprocedualDummyVertex to clarify that it is just a small dummy vertex in a graph pointing to a real Vertex in another graph ?
	
	private String name;
	private String label;
	private JoanaVertex dummyVertex;
	private JoanaVertex connectedVertex;
	private int graphId;
	private String graphName;
	private EdgeDirection direction;
	private EdgeKind edgeKind;

	/**
	 * Vertex that is part of a foreign MethodGraph, so an InterprocedualVertex represents a little dummyVertex in the foreign MethodGraph, 
	 * so one can navigate from this dummy to the corresponding vertex in the other MethodGraph.
	 * An InterprocedualVertex has no label, just a color, depending on the VertexKind of the corresponding Vertex in the other Graph, 
	 * and is smaller than a normal Vertex.
	 * When selecting an InterprocedualVertex the InformationView shows it's corresponding vertex's attributes plus the name of the Graph it belongs to and its ID.
	 * Double clicking on an InterprocedualVertex opens the graph it's corresponding Vertex belongs to (if not opened yet, otherwise switches to it)
	 * and focuses on this corresponding Vertex.
	 * 
	 * @param name name of the vertex
	 * @param label label of the vertex
	 * @param dummyVertex the dummy vertex this InterprocedualVertex represents
	 * @param connectedVertex the vertex that is connected with this dummyVertex
	 * @param graphId id of the graph the dummyVertex links to (corresponding vertex in graph graphId with same id as dummyVertex)
	 * @param isTarget true if the edge that connects dummyVertex and connectedVertex goes from connectedVertex --> dummyVertex, false in other direction
	 */
	public InterproceduralVertex(String name, String label, JoanaVertex dummyVertex, JoanaVertex connectedVertex, int graphId, String graphName, boolean isTarget, EdgeKind edgeKind){
		super(name, label, dummyVertex.getNodeKind());
		this.name = name;
		this.label = label;
		this.dummyVertex = dummyVertex;
		this.connectedVertex = connectedVertex;
		this.graphId = graphId;
		this.graphName = graphName;
		this.direction = isTarget ? EdgeDirection.TO : EdgeDirection.FROM;
		this.edgeKind = edgeKind;
	}
	
	//override with dummyVertex attributes?!!
	//seems not to be necessary because its kind is passed through the constructor, the rest is not necessary
	//TODO: how pass the graph (id and) name of the vertex this is pointing to to this vertex' InformationView(creating GAnsProperties)
	
	@Override
	public List<GAnsProperty<?>> getProperties(){//new properties are : graph name, dummy label(already contains id and its kind)
		List<GAnsProperty<?>> properties = super.getProperties().subList(1, 2);//get second element of the properties (has to be the label)
		assert(properties.get(0).getName().equals("label"));
		properties.add(0, new GAnsProperty<String>("graphLabel",this.graphName));
		return properties;
	}
	
	public JoanaVertex getDummyVertex(){
		return this.dummyVertex;
	}
	
	public JoanaVertex getConnectedVertex(){
		return this.connectedVertex;
	}
	
	/**
	 * @return The label of the graph the dummy vertex points to.
	 */
	public String getGraphName(){
		return this.graphName;
	}
	
	/**
	 * @return The ID of the graph the corresponding Vertex of this dummy belongs.
	 */
	public int getGraphID(){
		return this.graphId;
	}
	
	/**
	 * 
	 * @return The kind of connection between this dummy vertex and it's connected vertex. 
	 * 		Either TO the dummy or FROM the dummy to the other.
	 */
	public EdgeDirection getEdgeDirection(){
		return this.direction;
	}
	
	public EdgeKind getEdgeKind(){
		return this.edgeKind;
	}
	//these vertices just have a color and no text
	@Override
	public String getLabel(){
		return "";
	}
	
	//TODO: either set size like this or let Settings.getSize set the size from the getLabel() method (will choose the minimum size, but can be too much)
	//InterprocedualVertices should have the same size. Otherwise Their layouting in MethodGraphLayout must be adjusted!
	@Override
	public DoublePoint getSize(){
		return new DoublePoint(10,10);
	}
	
	@Override 
	public IntegerPoint getLeftRightMargin(){
		return new IntegerPoint(2,2);
	}
	
	@Override
	public String toString(){
		String arrow = this.direction == EdgeDirection.TO ? "->" : "<-"; 
		return "[" + this.connectedVertex.getName() + arrow + "d" +this.dummyVertex.getName() + "]";
	}
	
	public enum EdgeDirection{
		FROM, TO
	}

}
