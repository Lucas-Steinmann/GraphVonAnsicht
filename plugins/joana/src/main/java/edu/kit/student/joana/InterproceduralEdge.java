package edu.kit.student.joana;

import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.util.DoublePoint;
import edu.kit.student.util.IntegerPoint;

import java.util.List;

/**
 */
public class InterproceduralEdge extends JoanaEdge{

    private JoanaEdge wrappedEdge;
    private int dummyGraphId;
    private String dummyGraphName;
    private DummyLocation location;
    private JoanaVertex normalVertex;
    private ForeignGraphDummyVertex dummyVertex;

    /**
     * An edge between two MethodGraphs (each Vertex is in a different MethodGraph).
     * For an edge between two MethodGraphs, both MethodGraphs need to get an InterproceduralEdge out of this edge, with the vertex not being in the graph as a dummy representing him.
     * Each InterproceduralEdge has a normal Vertex and a dummy vertex(representing the real vertex from the other graph).
     * In every MethodGraph there is one normal vertex and one foreign vertex that will be represented by a dummy
     *
     * @param edge edge to build a InterproceduralEdge of
     * @param dummyGraphId id of the graph that contains the dummy vertex
     * @param dummyGraphName name of the graph that contains the dummy
     * @param location whether the source or target vertex should be a dummy
     */
    public InterproceduralEdge(JoanaEdge edge, int dummyGraphId, String dummyGraphName, DummyLocation location) { //TODO: maybe new constructor that sets source and target adequately(not the edges real source and target)
        super(edge.getName(), edge.getLabel(), edge.getSource(), edge.getTarget(), edge.getEdgeKind());
        this.wrappedEdge = edge;
        this.dummyGraphId = dummyGraphId;
        this.dummyGraphName = dummyGraphName;
        this.location = location;
        //setting the normal edge and the dummy, also sets source and target of this edge new,
        //because the normal and the dummy vertex will be added later in the graph, not its origin source and target vertices
        if(location == DummyLocation.SOURCE){
            this.dummyVertex = new ForeignGraphDummyVertex(edge.getSource(), dummyGraphName);
            this.normalVertex = edge.getTarget();
            super.setVertices(dummyVertex,normalVertex);
        }else{
            this.normalVertex = edge.getSource();
            this.dummyVertex = new ForeignGraphDummyVertex(edge.getTarget(), dummyGraphName);
            super.setVertices(normalVertex,dummyVertex);
        }
    }


    public DummyLocation getDummyLocation() {
        return location;
    }

    public JoanaVertex getNormalVertex(){
        return normalVertex;
    }

    public ForeignGraphDummyVertex getDummyVertex() {
        return dummyVertex;
    }

    public enum DummyLocation{
        SOURCE, TARGET
    }

    public class ForeignGraphDummyVertex extends JoanaVertex{


        private String graphName;

        /**
         * A vertex that represents a vertex from an other graph.
         *
         *
         * @param vertex the vertex to represent
         * @param graphName name of the graph the vertex to represents is in
         */
        ForeignGraphDummyVertex(JoanaVertex vertex, String graphName) {
            super(vertex.getName(), vertex.getLabel(), vertex.getNodeKind());
            this.graphName = graphName;
        }

        @Override
        public List<GAnsProperty<?>> getProperties(){//new properties are : graph name, dummy label(already contains id and its kind)
            List<GAnsProperty<?>> properties = super.getProperties().subList(0, 3);//get first three elements (name, label, kind)
            assert(properties.get(1).getName().equals("label"));
            properties.add(0, new GAnsProperty<>("graphLabel",this.graphName));
            return properties;
        }

        //this vertex just has a color and no text
        @Override
        public String getLabel(){
            return "";
        }

        @Override
        public DoublePoint getSize(){
            return new DoublePoint(10,10);
        }

        @Override
        public IntegerPoint getLeftRightMargin(){
            return new IntegerPoint(2,2);
        }
    }
}
