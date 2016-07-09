package st.infos.elementalcube.breakbaloon.theme.editor;

import st.infos.elementalcube.breakbaloon.theme.editor.contenteditor.BaloonImageContentEditor;
import st.infos.elementalcube.breakbaloon.theme.editor.contenteditor.BaloonImageContentEditor.EnumBaloonType;
import st.infos.elementalcube.breakbaloon.theme.editor.contenteditor.ContentEditor;
import st.infos.elementalcube.breakbaloon.theme.editor.contenteditor.PropertiesContentEditor;
import st.infos.elementalcube.breakbaloon.theme.editor.contenteditor.SoundContentEditor;
import st.infos.elementalcube.breakbaloon.theme.editor.contenteditor.SoundContentEditor.EnumSoundType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class LeftMenuComponent extends JPanel implements MouseListener {
	private static final long serialVersionUID = 7466111736993275155L;
	private Editor editor;
	private ContentEditor action;
	private boolean hover;
	
	private LeftMenuComponent(Editor editor, ContentEditor action) {
		this.editor = editor;
		this.action = action;
		addMouseListener(this);
		setMinimumSize(new Dimension(100, 100));
		setMaximumSize(new Dimension(300, 300));
		setPreferredSize(new Dimension(150, 150));
	}

	public static LeftMenuComponent[] getList(Editor editor) {
		LeftMenuComponent[] list = new LeftMenuComponent[6];
		list[0] = new LeftMenuComponent(editor, new PropertiesContentEditor(editor, "editor.properties"));
		list[1] = new LeftMenuComponent(editor, new BaloonImageContentEditor(editor, "editor.baloon.closed", EnumBaloonType.CLOSED));
		list[2] = new LeftMenuComponent(editor, new BaloonImageContentEditor(editor, "editor.baloon.opened", EnumBaloonType.OPENED));
		list[3] = new LeftMenuComponent(editor, new BaloonImageContentEditor(editor, "editor.baloon.openedGood", EnumBaloonType.OPENED_GOOD));
		list[4] = new LeftMenuComponent(editor, new SoundContentEditor("editor.sound.pump", EnumSoundType.PUMP));
		list[5] = new LeftMenuComponent(editor, new SoundContentEditor("editor.sound.wpump", EnumSoundType.WPUMP));
		return list;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(editor.getContentEditor() == action ? Color.LIGHT_GRAY : hover ? new Color(240, 240, 240) : Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		action.paintOverview(getSize(), (Graphics2D) g);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {
		editor.setContentEditor(!isEnabled() ? null : editor.getContentEditor() == action ? null : action);
		editor.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		hover = true;
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		hover = false;
		repaint();
	}

	public ContentEditor getContentEditor() {
		return action;
	}

	public void dbfpgPropertyChanged(boolean selected) {
		if (action instanceof BaloonImageContentEditor) {
			if (((BaloonImageContentEditor) action).getBaloonType() == EnumBaloonType.OPENED_GOOD) {
				setEnabled(selected);
				if (selected) {
					setMinimumSize(new Dimension(100, 100));
					setMaximumSize(new Dimension(300, 300));
					setPreferredSize(new Dimension(150, 150));
				} else {
					setMinimumSize(new Dimension(0, 0));
					setMaximumSize(new Dimension(0, 0));
					setPreferredSize(new Dimension(0, 0));
				}
				revalidate();
			}
		}
	}
}
