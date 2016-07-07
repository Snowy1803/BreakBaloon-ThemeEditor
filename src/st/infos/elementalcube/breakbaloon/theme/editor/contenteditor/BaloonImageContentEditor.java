package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import st.infos.elementalcube.breakbaloon.theme.editor.Editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class BaloonImageContentEditor extends ContentEditor {
	private static final long serialVersionUID = 6556988077515024109L;
	private EnumBaloonType type;
	private DrawEditor editor;
	private ImageEditorToolbar toolbar;
	
	public BaloonImageContentEditor(Editor editor, String name, EnumBaloonType type) {
		super(name);
		setLayout(new BorderLayout());
		this.type = type;
		this.editor = new DrawEditor(editor, new Dimension(75, 75));
		this.toolbar = new ImageEditorToolbar(this.editor);
		JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JPanel(), this.editor);
		pane.setContinuousLayout(true);
		add(pane, BorderLayout.CENTER);
		add(toolbar, BorderLayout.SOUTH);
	}
	
	@Override
	public void paintOverview(Dimension dimension, Graphics2D g2d) {
		super.paintOverview(dimension, g2d);
		try {
			g2d.drawImage(ImageIO.read(getClass().getResource("/img/" + type.langKey + ".png")), dimension.width / 2 - 32, dimension.height / 2 - 32,
					64, 64, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static enum EnumBaloonType {
		CLOSED("closed"), OPENED("opened"), OPENED_GOOD("openedGood");
		
		private String langKey;
		
		private EnumBaloonType(String langKey) {
			this.langKey = langKey;
		}
	}
}
