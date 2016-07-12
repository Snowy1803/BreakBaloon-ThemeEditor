package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import st.infos.elementalcube.breakbaloon.theme.editor.BBTheme;
import st.infos.elementalcube.snowylangapi.Lang;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.undo.UndoManager;

public abstract class ContentEditor extends JPanel {
	private static final long serialVersionUID = 6518309996553924717L;
	
	public ContentEditor(String name) {
		setName(name);
	}

	public void paintOverview(Dimension dimension, Graphics2D g2d) {
		String s = Lang.getString(getName());
		g2d.setFont(new Font(null, Font.PLAIN, 16));
		g2d.setColor(Color.BLACK);
		g2d.drawString(s, dimension.width / 2 - ((int) g2d.getFont().getStringBounds(s, g2d.getFontRenderContext()).getWidth()) / 2, 
				dimension.height - getFont().getSize() - 5);
	}
	
	public abstract void saveToBBTheme(BBTheme theme);
	
	public abstract void loadFromBBTheme(BBTheme theme);
	
	public UndoManager getUndoManager() {
		return null;
	}

	public void removedFromView() {}

	public void addedToView() {}
}
