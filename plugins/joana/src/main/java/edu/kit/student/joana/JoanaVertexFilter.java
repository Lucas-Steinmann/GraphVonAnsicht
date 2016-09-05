package edu.kit.student.joana;

import java.util.function.Predicate;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.joana.JoanaVertex.VertexKind;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.VertexFilter;

public class JoanaVertexFilter extends VertexFilter {

	private VertexKind kind;
	
	public JoanaVertexFilter(VertexKind kind) {
		super(kind.name());
		
		this.kind = kind;
	}

	@Override
	public Predicate<Vertex> getPredicate() {
		return new Predicate<Vertex>() {

            @Override
            public boolean test(Vertex t) {
                for (GAnsProperty<?> property : t.getProperties()) {
                    if (property.getName().equals("nodeKind")) {
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
