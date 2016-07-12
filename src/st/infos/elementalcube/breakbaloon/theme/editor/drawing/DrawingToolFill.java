package st.infos.elementalcube.breakbaloon.theme.editor.drawing;

import st.infos.elementalcube.breakbaloon.theme.editor.drawing.UndoableImageEdit.UndoableImageEditBuilder;

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
		UndoableImageEditBuilder builder = new UndoableImageEditBuilder();
		propagate(editor.getImage(), to, new Color(editor.getImage().getRGB(to.x, to.y), true), color, builder);
		UndoableImageEdit edit = builder.toUndoableImageEdit(editor);
		if (edit != null) {
			editor.undoManager.addEdit(edit);
			return true;
		}
		return false;
	}

	private boolean propagate(BufferedImage image, Point point, Color seekingColor, Color color, UndoableImageEditBuilder builder) {
		if (seekingColor == color) {
			return false;
		}
		builder.add(point, new Color(image.getRGB(point.x, point.y), true), color);
		image.setRGB(point.x, point.y, color.getRGB());
		if (point.x + 1 < image.getHeight() && image.getRGB(point.x + 1, point.y) == seekingColor.getRGB()) {
			propagate(image, new Point(point.x + 1, point.y), seekingColor, color, builder);
		}
		if (point.x > 0 && image.getRGB(point.x - 1, point.y) == seekingColor.getRGB()) {
			propagate(image, new Point(point.x - 1, point.y), seekingColor, color, builder);
		}
		if (point.y + 1 < image.getHeight() && image.getRGB(point.x, point.y + 1) == seekingColor.getRGB()) {
			propagate(image, new Point(point.x, point.y + 1), seekingColor, color, builder);
		}
		if (point.y > 0 && image.getRGB(point.x, point.y - 1) == seekingColor.getRGB()) {
			propagate(image, new Point(point.x, point.y - 1), seekingColor, color, builder);
		}
		return true;
	}
}
