package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import st.infos.elementalcube.breakbaloon.theme.editor.BBTheme;
import st.infos.elementalcube.breakbaloon.theme.editor.Editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class IconImageContentEditor extends ContentEditor {
	private static final long serialVersionUID = 6556988077515024109L;
	private EnumIconType type;
	private DrawEditor drawing;
	private ImageEditorToolbar toolbar;
	
	public IconImageContentEditor(Editor frame, String name, EnumIconType type) {
		super(name);
		setLayout(new BorderLayout());
		this.type = type;
		this.toolbar = new ImageEditorToolbar();
		drawing = new DrawEditor(frame, new Dimension(16, 16));
		drawing.setToolbar(toolbar);
		add(drawing, BorderLayout.CENTER);
		add(toolbar, BorderLayout.SOUTH);
	}
	
	@Override
	public void paintOverview(Dimension dimension, Graphics2D g2d) {
		super.paintOverview(dimension, g2d);
		g2d.drawImage(drawing.getImage(), dimension.width / 2 - 32, dimension.height / 2 - 32,
				64, 64, null);
	}

	@Override
	public void saveToBBTheme(BBTheme theme) {
		switch (type) {
		case ICON:
			theme.icon = drawing.getImage();
			break;
		case WICON:
			theme.wicon = drawing.getImage();
			break;
		case CURSOR:
			theme.cursor= drawing.getImage();
			break;
		}
	}

	@Override
	public void loadFromBBTheme(BBTheme theme) {
		switch (type) {
		case ICON:
			if (theme.icon != null) drawing.setImage(theme.icon);
			break;
		case WICON:
			if (theme.wicon != null) drawing.setImage(theme.wicon);
			break;
		case CURSOR:
			if (theme.cursor != null) drawing.setImage(theme.cursor);
			break;
		}
	}
	
	public EnumIconType getBaloonType() {
		return type;
	}
	
	public static enum EnumIconType {
		ICON, WICON, CURSOR;
	}
}
