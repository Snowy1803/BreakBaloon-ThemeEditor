package st.infos.elementalcube.breakbaloon.theme.editor.drawing;

import java.awt.Color;
import java.awt.Point;

public class DrawingToolPencil extends DrawingTool {
	@Override
	public boolean canBeUsed(EnumUseType useType) {
		return useType != EnumUseType.RELEASED;
	}
	
	@Override
	public boolean draw(DrawEditor editor, Point from, Point to, Color color) {
		int x = from.x, x2 = to.x, y = from.y, y2 = to.y;
		int w = x2 - x;
		int h = y2 - y;
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
		if (w < 0) dx1 = -1;
		else if (w > 0) dx1 = 1;
		if (h < 0) dy1 = -1;
		else if (h > 0) dy1 = 1;
		if (w < 0) dx2 = -1;
		else if (w > 0) dx2 = 1;
		int longest = Math.abs(w);
		int shortest = Math.abs(h);
		if (!(longest > shortest)) {
			longest = Math.abs(h);
			shortest = Math.abs(w);
			if (h < 0) dy2 = -1;
			else if (h > 0) dy2 = 1;
			dx2 = 0;
		}
		int numerator = longest >> 1;
		for (int i = 0; i <= longest; i++) {
			editor.getImage().setRGB(x, y, color.getRGB());
			numerator += shortest;
			if (!(numerator < longest)) {
				numerator -= longest;
				x += dx1;
				y += dy1;
			} else {
				x += dx2;
				y += dy2;
			}
		}
		return true;
	}
}
