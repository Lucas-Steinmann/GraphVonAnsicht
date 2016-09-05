package edu.kit.student.joana;

import java.util.function.Predicate;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.joana.JoanaEdge.EdgeKind;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.EdgeFilter;

public class JoanaEdgeFilter extends EdgeFilter {

	private EdgeKind kind;
	
	public JoanaEdgeFilter(EdgeKind kind) {
		super(kind.name());
		
		this.kind = kind;
	}

	@Override
	public Predicate<Edge> getPredicate() {
		return new Predicate<Edge>() {

            @Override
            public boolean test(Edge t) {
                for (GAnsProperty<?> property : t.getProperties()) {
                    if (property.getName().equals("edgeKind")) {
                        if (property.getValue().equals(kind)) {
                            return true;
                        }
                        return false;
                    }
                }
                return false;
            } 
        };
	}

}
