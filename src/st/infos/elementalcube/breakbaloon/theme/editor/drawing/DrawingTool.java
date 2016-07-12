package st.infos.elementalcube.breakbaloon.theme.editor.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public abstract class DrawingTool {
	public abstract boolean canBeUsed(EnumUseType useType);
	
	public abstract boolean draw(DrawEditor editor, Point from, Point to, Color color);
	
	/**
	 * @param editor 	draw editor
	 * @param g2d 		Graphics to paint on.
	 */
	public void paintOverlay(DrawEditor editor, Graphics2D g2d) {}
	
	public boolean needRepainting() {
		return false;
	}
	
	public enum EnumUseType {
		PRESSED, RELEASED, DRAGGED
	}
}
