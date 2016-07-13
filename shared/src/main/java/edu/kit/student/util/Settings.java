package edu.kit.student.util;

import javafx.scene.text.Text;
import javafx.util.Pair;

public class Settings {
	// predefined values, that will be used if nothing else is specified
	private static double minWidth = 20;
	private static double minHeight = 5;
	private static double maxWidth = 50;
	private static double maxHeight = 20;
	
	// margins stay the same for every vertex size calculated
	public static double leftRightMargin = 8;
	public static double topBottomMargin = 4;
	
	public static Pair<Double,Double> getSize(String text, boolean alignToText) {
		if(alignToText) {
			Text textShape = new Text(text);
			double width = textShape.getLayoutBounds().getWidth() + leftRightMargin;
			double height = textShape.getLayoutBounds().getHeight() + topBottomMargin;
			return new Pair<Double,Double>(width,height);
		} else {
			return getSize(text, minWidth, minHeight, maxWidth, maxHeight);
		}
	}
	
	public static Pair<Double,Double> getSize(String text, double maxWidth, double maxHeight) {
		return getSize(text, minWidth, minHeight, maxWidth, maxHeight);
	}
	
	public static Pair<Double,Double> getSize(String text, double minWidth, double minHeight, double maxWidth, double maxHeight) {
		Text textShape = new Text(text);
		double width = textShape.getLayoutBounds().getWidth() + leftRightMargin;
		double height = textShape.getLayoutBounds().getHeight() + topBottomMargin;

		if (width < minWidth)
			width = minWidth;
		else if(width > maxWidth)
			width = maxWidth;
		if (height < minHeight)
			height = minHeight;
		else if(height > maxHeight)
			height = maxHeight;
		
		return new Pair<Double,Double>(width,height);
	}
}
