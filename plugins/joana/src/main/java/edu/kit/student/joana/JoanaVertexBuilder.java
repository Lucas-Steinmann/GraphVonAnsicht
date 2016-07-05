package edu.kit.student.joana;

import edu.kit.student.graphmodel.builder.IVertexBuilder;

/**
 * The JoanaVertexBuilder implements an {@link IVertexBuilder} and
 * creates a {@link JoanaVertex}.
 */
public class JoanaVertexBuilder implements IVertexBuilder {
    
    String id = "";
    String label = "";
    JoanaVertex.KIND kind;
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
        this.id = id;
    }
    
    
    @Override
    public void addData(String value, String keyname) {
        switch (keyname) {
          case "nodeKind":
              kind = JoanaVertex.KIND.valueOf(keyname);
              break;
          case "nodeSource": 
              this.source = value;
              break;
          case "nodeOperaton":
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
        // The ID in the persistent data is the name of the vertex.
        JoanaVertex vertex = new JoanaVertex(id, label);
        vertex.setProperties(kind, source, proc, operation, bcName, bcIndex, sr, sc, er, ec);
        //TODO Check relations nodeKind-nodeOperation and maybe others
        return vertex;
    }

    @Override
    public void setID(String id) {
        this.id = id;
    }

}
