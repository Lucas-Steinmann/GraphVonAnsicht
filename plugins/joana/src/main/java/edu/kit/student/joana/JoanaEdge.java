package edu.kit.student.joana;

import java.util.List;

import edu.kit.student.graphmodel.directed.DefaultDirectedEdge;
import edu.kit.student.objectproperty.GAnsProperty;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 * A Joana specific {@link Edge}. It contains parameters which are only
 * used/usefull in {@link JoanaGraph}.
 */
public class JoanaEdge extends DefaultDirectedEdge {
    
    private GAnsProperty<Kind> edgeKind;

    public JoanaEdge(String name, String label, Kind kind) {
        super(name, label);
        edgeKind = new GAnsProperty<Kind>("edgeKind", kind);
    }

    public JoanaEdge(JoanaVertex source, JoanaVertex target, Kind edgeKind) {
        super(edgeKind.toString(), edgeKind.toString(), source, target);
        this.edgeKind = new GAnsProperty<Kind>("edgeKind", edgeKind);
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
    	List<GAnsProperty<?>> properties = super.getProperties();
    	properties.add(edgeKind);
    	return properties;
    }
    
	@Override
	public Pair<Double, Double> getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getColor() {
		return edgeKind.getValue().color();
	}
    
    // TODO: Add missing.
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
}
