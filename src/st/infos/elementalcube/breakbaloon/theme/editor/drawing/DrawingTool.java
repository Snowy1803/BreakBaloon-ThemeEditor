package st.infos.elementalcube.breakbaloon.theme.editor.drawing;

import java.awt.Color;
import java.awt.Point;

public abstract class DrawingTool {
	public abstract boolean canBeUsed(EnumUseType useType);
	
	public abstract void draw(DrawEditor editor, Point from, Point to, Color color);
	
	public enum EnumUseType {
		PRESSED, RELEASED, DRAGGED
	}
}
