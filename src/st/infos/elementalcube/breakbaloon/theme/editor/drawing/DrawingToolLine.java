package st.infos.elementalcube.breakbaloon.theme.editor.drawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class DrawingToolLine extends DrawingTool {
	private Point firstPoint;

	@Override
	public boolean canBeUsed(EnumUseType useType) {
		return useType == EnumUseType.RELEASED;
	}

	@Override
	public boolean draw(DrawEditor editor, Point from, Point to, Color color) {
		if (firstPoint == null) {
			firstPoint = to;
		} else {
			new DrawingToolPencil().draw(editor, firstPoint, to, color);
			firstPoint = null;
			return true;
		}
		return false;
	}
	
	@Override
	public void paintOverlay(DrawEditor editor, Graphics2D g2d) {
		if (firstPoint != null) {
			int scale = editor.getZoomLevel();
			g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {2F}, 0.0f));
			g2d.setColor(editor.getToolbar().currentColor);
			g2d.drawRect(firstPoint.x * scale, firstPoint.y * scale, scale, scale);
			if (editor.mouseCoords != null) {
				g2d.drawLine(firstPoint.x * scale + scale / 2, firstPoint.y * scale + scale / 2, editor.mouseCoords.x / scale * scale + scale / 2, 
						editor.mouseCoords.y / scale * scale + scale / 2);
				g2d.drawRect(editor.mouseCoords.x / scale * scale, editor.mouseCoords.y / scale * scale, scale, scale);
			}
		}
	}
	
	@Override
	public boolean needRepainting() {
		return true;
	}
}
