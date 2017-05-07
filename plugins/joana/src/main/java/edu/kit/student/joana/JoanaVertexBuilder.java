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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoanaVertexBuilder that = (JoanaVertexBuilder) o;

        if (proc != that.proc) return false;
        if (bcIndex != that.bcIndex) return false;
        if (sr != that.sr) return false;
        if (sc != that.sc) return false;
        if (er != that.er) return false;
        if (ec != that.ec) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (kind != that.kind) return false;
        if (javaSource != null ? !javaSource.equals(that.javaSource) : that.javaSource != null) return false;
        if (operation != that.operation) return false;
        if (bcName != null ? !bcName.equals(that.bcName) : that.bcName != null) return false;
        if (localDef != null ? !localDef.equals(that.localDef) : that.localDef != null) return false;
        if (localUse != null ? !localUse.equals(that.localUse) : that.localUse != null) return false;
        return joanaObjectPool != null ? joanaObjectPool.equals(that.joanaObjectPool) : that.joanaObjectPool == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (kind != null ? kind.hashCode() : 0);
        result = 31 * result + (javaSource != null ? javaSource.hashCode() : 0);
        result = 31 * result + proc;
        result = 31 * result + (operation != null ? operation.hashCode() : 0);
        result = 31 * result + (bcName != null ? bcName.hashCode() : 0);
        result = 31 * result + bcIndex;
        result = 31 * result + sr;
        result = 31 * result + sc;
        result = 31 * result + er;
        result = 31 * result + ec;
        result = 31 * result + (localDef != null ? localDef.hashCode() : 0);
        result = 31 * result + (localUse != null ? localUse.hashCode() : 0);
        result = 31 * result + (joanaObjectPool != null ? joanaObjectPool.hashCode() : 0);
        return result;
    }
}
