package st.infos.elementalcube.breakbaloon.theme.editor.drawing;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class DrawingToolFill extends DrawingTool {

	@Override
	public boolean canBeUsed(EnumUseType useType) {
		return useType == EnumUseType.RELEASED;
	}
	
	@Override
	public boolean draw(DrawEditor editor, Point from, Point to, Color color) {
		return propagate(editor.getImage(), to, new Color(editor.getImage().getRGB(to.x, to.y), true), color);
	}

	private boolean propagate(BufferedImage image, Point point, Color seekingColor, Color color) {
		if (seekingColor == color) {
			return false;
		}
		image.setRGB(point.x, point.y, color.getRGB());
		if (point.x + 1 < image.getHeight() && image.getRGB(point.x + 1, point.y) == seekingColor.getRGB()) {
			propagate(image, new Point(point.x + 1, point.y), seekingColor, color);
		}
		if (point.x > 0 && image.getRGB(point.x - 1, point.y) == seekingColor.getRGB()) {
			propagate(image, new Point(point.x - 1, point.y), seekingColor, color);
		}
		if (point.y + 1 < image.getHeight() && image.getRGB(point.x, point.y + 1) == seekingColor.getRGB()) {
			propagate(image, new Point(point.x, point.y + 1), seekingColor, color);
		}
		if (point.y > 0 && image.getRGB(point.x, point.y - 1) == seekingColor.getRGB()) {
			propagate(image, new Point(point.x, point.y - 1), seekingColor, color);
		}
		return true;
	}
}
