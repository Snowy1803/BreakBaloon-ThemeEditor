package st.infos.elementalcube.breakbaloon.theme.editor.drawing;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class UndoableImageEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -1642754291994374741L;
	private DrawEditor editor;
	private Point[] pointsChanged;
	private Color[] previousColor, newColor;
	
	public UndoableImageEdit(DrawEditor editor, Point[] points, Color[] previousColor, Color[] newColor) {
		this.editor = editor;
		this.pointsChanged = points;
		this.previousColor = previousColor;
		this.newColor = newColor;
	}
	
	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		for (int i = 0; i < pointsChanged.length; i++) {
			editor.getImage().setRGB(pointsChanged[i].x, pointsChanged[i].y, previousColor[i].getRGB());
		}
	}
	
	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		for (int i = 0; i < pointsChanged.length; i++) {
			editor.getImage().setRGB(pointsChanged[i].x, pointsChanged[i].y, newColor[i].getRGB());
		}
	}
	
	public static class UndoableImageEditBuilder {
		private ArrayList<Point> points = new ArrayList<>();
		private ArrayList<Color> previousColors = new ArrayList<>();
		private ArrayList<Color> newColors = new ArrayList<>();
		
		public boolean add(Point point, Color previous, Color next) {
			if (!previous.equals(next)) {
				points.add(point);
				previousColors.add(previous);
				newColors.add(next);
				return true;
			}
			return false;
		}
		
		public UndoableImageEdit toUndoableImageEdit(DrawEditor editor) {
			return points.isEmpty() ? null : new UndoableImageEdit(editor, points.toArray(new Point[0]), previousColors.toArray(new Color[0]), 
					newColors.toArray(new Color[0]));
		}
	}
}
