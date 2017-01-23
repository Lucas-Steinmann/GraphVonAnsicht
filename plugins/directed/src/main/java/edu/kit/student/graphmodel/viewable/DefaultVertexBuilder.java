/**
 * 
 */
package edu.kit.student.graphmodel.viewable;

import java.util.HashMap;
import java.util.Map;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.builder.IVertexBuilder;

/**
 * @author Lucas Steinmann
 */
public class DefaultVertexBuilder implements IVertexBuilder {

	private String name = "vertex";
	private Map<String, String> vertexData = new HashMap<>();

    public DefaultVertexBuilder(String id) {
        // The id in the persistent data is the name of the joana vertex.
        this.name = id;
    }
	
	@Override
	public void setID(String id) {
		this.name = id;
	}
	public String getID() {
		return this.name;
	}

	@Override
	public void addData(String keyname, String value) {
		vertexData.put(keyname, value);
	}
	
	public DefaultVertex build() {
		DefaultVertex v = new DefaultVertex(name, name, vertexData);
		return v;
	}

	@Override
	public String toString(){
		return "VertexBuilder: " + getID();
	}

}
