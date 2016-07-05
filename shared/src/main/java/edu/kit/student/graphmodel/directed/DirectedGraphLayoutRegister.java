package edu.kit.student.graphmodel.directed;

import java.util.ArrayList;
import java.util.List;

import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.LayoutRegister;

/**
 * A {@link LayoutRegister} which is specific for
 * {@link DirectedGraphLayoutOption}.
 */
public class DirectedGraphLayoutRegister implements LayoutRegister<DirectedGraphLayoutOption> {

	private List<DirectedGraphLayoutOption> options = new ArrayList<>();

	@Override
	public List<DirectedGraphLayoutOption> getLayoutOptions() {
		return options;
	}

	@Override
	public void addLayoutOption(DirectedGraphLayoutOption option) {
		options.add(option);

	}

}
