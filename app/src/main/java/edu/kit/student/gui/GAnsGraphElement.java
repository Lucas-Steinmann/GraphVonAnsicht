package edu.kit.student.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * A abstract class to generalize the visual elements that can be displayed in
 * the {@link GraphView}.
 * 
 * @author Nicolas
 */
public abstract class GAnsGraphElement extends StackPane {

	/**
	 * Sets the text shown on the element.
	 * 
	 * @param text
	 *            The text that will be displayed on the element.
	 */
	public abstract void setText(String text);

	/**
	 * Returns the text shown on the element.
	 * 
	 * @return The text that is being displayed on the element.
	 */
	public abstract String getText();

	/**
	 * Sets the color of the element.
	 * 
	 * @param color
	 *            The color the element will be displayed in.
	 */
	public abstract void setColor(Color color);
	
	public abstract Shape getElementShape();
}
