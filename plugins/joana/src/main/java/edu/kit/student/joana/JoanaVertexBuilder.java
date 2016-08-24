package edu.kit.student.joana;

import edu.kit.student.graphmodel.builder.IVertexBuilder;

/**
 * The JoanaVertexBuilder implements an {@link IVertexBuilder} and
 * creates a {@link JoanaVertex}.
 */
public class JoanaVertexBuilder implements IVertexBuilder {
    
    String name = "";
    String label = "";
    JoanaVertex.VertexKind kind;
    private String source = "";
    private int proc;
    private String operation = "";
    private String bcName = "";
    private int bcIndex;
    private int sr;
    private int sc;
    private int er;
    private int ec;
    
    public JoanaVertexBuilder(String id) {
        // The id in the persistent data is the name of the joana vertex.
        this.name = id;
    }
    
    
    @Override
    public void addData(String keyname, String value) {
        switch (keyname) {
          case "nodeKind":
              kind = JoanaVertex.VertexKind.valueOf(value);
              break;
          case "nodeSource": 
              this.source = value;
              break;
          case "nodeOperation":
              operation = value;
              break;
          case "nodeLabel":
              label = value;
              break;
          case "nodeBcName":
              bcName = value;
              break;
          case "nodeBCIndex":
              bcIndex = parseNum(value);
              break;
          case "nodeSr":
              sr = parseNum(value);
              break;
          case "nodeSc":
              sc = parseNum(value);
              break;
          case "nodeEr":
              er = parseNum(value);
              break;
          case "nodeEc":
              ec = parseNum(value);
              break;
          default:
              break;
        }
    }
    
    private Integer parseNum(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    /**
     * Builds JoanaVertex from the given data.
     * @return the built vertex
     */
    public JoanaVertex build() {
        if (kind == null) {
            throw new IllegalArgumentException("JoanaVertex " + name + " needs a NodeKind");
        }
        
        JoanaVertex vertex = new JoanaVertex(name, label, kind, source, proc, 
                                             operation, bcName, bcIndex, sr, sc, er, ec);
        //TODO Check relations nodeKind-nodeOperation and maybe others
        return vertex;
    }

    @Override
    public void setID(String id) {
        this.name = id;
    }

}
