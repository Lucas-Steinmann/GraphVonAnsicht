package edu.kit.student.joana;

import edu.kit.student.graphmodel.builder.GraphBuilderException;
import edu.kit.student.graphmodel.builder.IVertexBuilder;

/**
 * The JoanaVertexBuilder implements an {@link IVertexBuilder} and
 * creates a {@link JoanaVertex}.
 */
public class JoanaVertexBuilder implements IVertexBuilder {

    private String name = "";
    private String label = "";
    private JoanaVertex.VertexKind kind;
    private JavaSource javaSource = null;
    private int proc;
    private JoanaVertex.Operation operation;
    private String bcName = "";
    private int bcIndex;
    private int sr;
    private int sc;
    private int er;
    private int ec;
    private String localDef;
    private String localUse;
    private JoanaObjectPool joanaObjectPool;
    
    public JoanaVertexBuilder(String id, JoanaObjectPool pool) {
        // The id in the persistent data is the name of the joana vertex.
        this.name = id;
        this.joanaObjectPool = pool;
    }
    
    
    @Override
    public void addData(String keyname, String value) {
        switch (keyname) {
          case "nodeKind":
              kind = JoanaVertex.VertexKind.valueOf(value);
              break;
          case "nodeSource":
              this.javaSource = joanaObjectPool.getJavaSource(value);
              break;
          case "nodeOperation":
              operation = JoanaVertex.Operation.getOperationByValue(value);
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
          case "nodeLocalDef":
              localDef = value;
              break;
          case "nodeLocalUse":
              localUse = value;
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
     * @throws GraphBuilderException if the {@link JoanaVertex.VertexKind} was not set.
     */
    public JoanaVertex build() throws GraphBuilderException {
        if (kind == null) {
            throw new GraphBuilderException(GraphBuilderException.BuilderType.VERTEX, "JoanaVertex " + name + " needs a NodeKind");
        }

        //TODO Check relations nodeKind-nodeOperation and maybe others
        return new JoanaVertex(name, label, kind, javaSource, proc,
                                             operation, bcName, bcIndex, sr, sc, er, ec, localDef, localUse);
    }

    @Override
    public void setID(String id) {
        this.name = id;
    }

}
