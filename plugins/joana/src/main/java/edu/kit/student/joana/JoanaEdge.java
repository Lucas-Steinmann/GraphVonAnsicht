package edu.kit.student.joana;

import java.util.LinkedList;
import java.util.List;

import edu.kit.student.graphmodel.EdgeArrow;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.OrthogonalEdgePath;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.util.IdGenerator;
import javafx.scene.paint.Color;

/**
 * A Joana specific {@link Edge}. It contains parameters which are only
 * used/usefull in {@link JoanaGraph}.
 */
public class JoanaEdge implements DirectedEdge {
    
    JoanaVertex source;
    JoanaVertex target;
	private Integer id;
	private GAnsProperty<String> name;
	private GAnsProperty<String> label;
    private GAnsProperty<Kind> edgeKind;
	private OrthogonalEdgePath path;

    public JoanaEdge(String name, String label, JoanaVertex source, JoanaVertex target, Kind kind) {
        this.edgeKind = new GAnsProperty<Kind>("edgeKind", kind);
        this.target = target;
        this.source = source;
        this.name = new GAnsProperty<String>("name", name);
        this.label = new GAnsProperty<String>("label", label);
        this.id = IdGenerator.getInstance().createId();
        this.path = new OrthogonalEdgePath();
    }

    public void setProperties(Kind edgeKind) {
        this.edgeKind.setValue(edgeKind);
    }

    /**
     * Returns the edgeKind of the JoanaEdge.
     * 
     * @return The edgeKind of the JoanaEdge.
     */
    public Kind getEdgeKind() {
        return edgeKind.getValue();
    }
    
    @Override
    public List<GAnsProperty<?>> getProperties() {
		LinkedList<GAnsProperty<?>> properties = new LinkedList<>();
		properties.add(name);
		properties.add(label);
		properties.add(edgeKind);
		return properties;
    }

	@Override
	public Color getColor() {
		return edgeKind.getValue().color();
	}
    
    public enum Kind {
    	CD, CE, CF, CL, DD, DH, HE, PI, PO, PS, RF, SU, NF, JF, UN, CC, JD, NTSCD,
    	SD, DA, DL, VD, RD, SH, SF, ID, IW, RY, FORK, FORK_IN, FORK_OUT, JOIN, JOIN_OUT,
    	CONFLICT_DATA, CONFLICT_ORDER, FD, FI;
    	
    	@Override
    	public String toString() {
    		return this.name();
    	}
    	
        public Color color() {
        	switch(this) {
        	case RF:
        	case PO:
        	case CF:
        	case NF:
        	case JF:
        	case JD:
        	case DL:
        	case VD:
        	case RD:
        	case SF:
        	case FI:
        	case ID:
        	case IW:
        	case FD:
        	case FORK_OUT:
        	case DD: return Color.web("0x0000FF");
        	case NTSCD:
        	case CONFLICT_DATA:
        	case CONFLICT_ORDER:
        	case CD: return Color.web("0xFF0000");
        	case HE:
        	case UN:
        	case CE: return Color.web("0xFFC125");
        	case DH: return Color.web("0x0000AA");
        	case SU: return Color.web("0xEE82EE");
        	case CC: return Color.web("0x00EE00");
        	case DA:
        	case SD: return Color.web("0x006600");
        	case SH: return Color.web("0x44AA44");
        	case FORK:
        	case JOIN:
        	case CL: return Color.web("0x006400");
        	case FORK_IN:
        	case PI: return Color.web("0x3CB371");
        	case PS: return Color.web("0x000000");
        	default: return Color.web("0xFFC125");
        	}
        }
    }

    @Override
    public List<JoanaVertex> getVertices() {
        List<JoanaVertex> result = new LinkedList<>();
        result.add(source);
        result.add(target);
        return result;
    }

    @Override
    public String getName() {
        return this.name.getValue();
    }

    @Override
    public Integer getID() {
        return this.id;
    }

    @Override
    public String getLabel() {
        return this.label.getValue();
    }

    @Override
    public void addToFastGraphAccessor(FastGraphAccessor fga) {
	    fga.addEdgeForAttribute(this, "name", this.name.toString());
	    fga.addEdgeForAttribute(this, "id", this.id);
	    fga.addEdgeForAttribute(this, "label", this.label.toString());

	    fga.addEdgeForAttribute(this, "sourceVertex", this.source.getID());
	    fga.addEdgeForAttribute(this, "targetVertex", this.target.getID());
    }

    @Override
    public OrthogonalEdgePath getPath() {
        return this.path;
    }
    
    @Override
	public EdgeArrow getArrowHead() {
		return EdgeArrow.ARROW;
	}

    @Override
    public JoanaVertex getSource() {
        return this.source;
    }

    @Override
    public JoanaVertex getTarget() {
        return this.target;
    }
}
