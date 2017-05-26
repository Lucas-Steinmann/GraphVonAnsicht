package edu.kit.student.joana;

import java.util.LinkedList;
import java.util.List;

import edu.kit.student.graphmodel.Edge;
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
    
    private GAnsProperty<JoanaVertex> source;
    private GAnsProperty<JoanaVertex> target;
    private JoanaVertex lastSource;
    private JoanaVertex lastTarget;
	private Integer id;
	private GAnsProperty<String> name;
	private GAnsProperty<String> label;
    private GAnsProperty<EdgeKind> edgeKind;
	private OrthogonalEdgePath path;

    public JoanaEdge(String name, String label, Integer id, JoanaVertex source, JoanaVertex target, EdgeKind kind) {
        this.edgeKind = new GAnsProperty<>("edgeKind", kind);
        this.source = new GAnsProperty<>("source", source);
        this.target = new GAnsProperty<>("target", target);
        this.lastSource = source;
        this.lastTarget = target;
        this.name = new GAnsProperty<>("nameId", name);
        this.label = new GAnsProperty<>("label", label);
        this.id = id;
        this.path = new OrthogonalEdgePath();
    }
    
    public JoanaEdge(String name, String label, JoanaVertex source, JoanaVertex target, EdgeKind kind) {
        this(name, label, IdGenerator.getInstance().createId(), source, target, kind);
    }
    
    public void setVertices(JoanaVertex source, JoanaVertex target){
    	this.lastSource=this.source.getValue();
    	this.lastTarget=this.target.getValue();
    	this.source.setValue(source);
    	this.target.setValue(target);
    }
    
    /**
     * Returns the value of the source of this edge before it was changed.
     * Always has the value of the last change.
     * If there were no changes, returns the normal source of this.
     * 
     * @return the source of this edge before one call of setVertices
     */
    public JoanaVertex getLastSource(){
    	return this.lastSource;
    }
    
    /**
     * Returns the value of the target of this edge before it was changed.
     * Always has the value of the last change.
     * If there were no changes, returns the normal target of this.
     * 
     * @return the target of this edge before one call of setVertices
     */
    public JoanaVertex getLastTarget(){
    	return this.lastTarget;
    }

    /**
     * Returns the edgeKind of the JoanaEdge.
     * 
     * @return The edgeKind of the JoanaEdge.
     */
    public EdgeKind getEdgeKind() {
        return edgeKind.getValue();
    }
    
    @Override
    public List<GAnsProperty<?>> getProperties() {
		LinkedList<GAnsProperty<?>> properties = new LinkedList<>();
		properties.add(name);
		properties.add(label);
        properties.add(source);
        properties.add(target);
		properties.add(edgeKind);
		return properties;
    }

	@Override
	public Color getColor() {
		return edgeKind.getValue().color();
	}
    
    public enum EdgeKind {
    	DEBUG, CD, CE, CF, CL, DD, DH, HE, PI, PO, PS, PE, RF, SU, NF, JF, UN, CC, JD, NTSCD,
    	SD, DA, DL, VD, RD, SH, SF, ID, IW, RY, FORK, FORK_IN, FORK_OUT, JOIN, JOIN_OUT,
    	CONFLICT_DATA, CONFLICT_ORDER, FD, FI, UNKNOWN;
    	
    	@Override
    	public String toString() {
    		return this.name();
    	}
    	
        public Color color() {
        	switch(this) {
			case DEBUG: return Color.web("0xFF00FF");
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
        	case PE: return Color.web("00e7ffff");
        	case PS: return Color.web("0x000000");
        	default: return Color.web("0xFFC125");
        	}
        }
    }

    @Override
    public List<JoanaVertex> getVertices() {
        List<JoanaVertex> result = new LinkedList<>();
        result.add(source.getValue());
        result.add(target.getValue());
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
	    fga.addEdgeForAttribute(this, "nameId", this.name.toString());
	    fga.addEdgeForAttribute(this, "id", this.id);
	    fga.addEdgeForAttribute(this, "label", this.label.toString());

	    fga.addEdgeForAttribute(this, "sourceVertex", this.source.getValue().getID());
	    fga.addEdgeForAttribute(this, "targetVertex", this.target.getValue().getID());
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
        return this.source.getValue();
    }

    @Override
    public JoanaVertex getTarget() {
        return this.target.getValue();
    }

	public void setEdgeKind(EdgeKind edgeKind) {
		this.edgeKind.setValue(edgeKind);
	}

    @Override
    public String toString() {
        return getSource().getName() + " -" + getName() + "-> " + getTarget().getName();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
