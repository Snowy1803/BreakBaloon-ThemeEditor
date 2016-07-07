package st.infos.elementalcube.breakbaloon.theme.editor;

import st.infos.elementalcube.breakbaloon.theme.editor.contenteditor.ContentEditor;
import st.infos.elementalcube.snowylangapi.Lang;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class Editor extends JFrame {
	private static final long serialVersionUID = -247298518651532746L;
	private BBTheme theme;
	private boolean saved = true;
	private File saveDirectory;
	private JPanel menu, container;
	
	public Editor(String file) {
		this(new File(file));
	}

	public Editor() {
		this.theme = new BBTheme();
		construct();
	}

	public Editor(File file) {
		try {
			this.theme = BBTheme.parseTheme(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (this.theme == null) {
			this.theme = new BBTheme();
		}
		construct();
	}
	
	private void construct() {
		setTitle(Lang.getString("editor.name"));
		
		menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));
		for (LeftMenuComponent component : LeftMenuComponent.getList(this)) {
			menu.add(component);
		}
		menu.setMinimumSize(new Dimension(100, 200 * menu.getComponentCount()));
		menu.setMaximumSize(new Dimension(300, 200 * menu.getComponentCount()));
		container = new JPanel(new GridLayout(1, 1));
		
		JSplitPane contentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(menu), new JScrollPane(container));
		contentPane.setContinuousLayout(true);
		contentPane.setDividerLocation(200);
		setContentPane(contentPane);
		setSize(900, 650);
		setLocationRelativeTo(null);
		addWindowListener(new WindowCloseListener());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	public void setContentEditor(ContentEditor content) {
		container.removeAll();
		if (content != null) container.add(content);
	}

	public ContentEditor getContentEditor() {
		return container.getComponentCount() == 1 ? (ContentEditor) container.getComponent(0) : null;
	}
	
	public void makeDirty() {
		saved = false;
		setTitle(Lang.getString("editor.name.unsaved"));
	}
	
	public void saved() {
		saved = true;
		setTitle(Lang.getString("editor.name"));
	}
	
	// Editor actions
	
	public void save() {
		// TODO save
	}
	
	public void saveAs() {
		// TODO saveAs
	}
	
	public void quit() {
		if (!saved) {
			int input = JOptionPane.showConfirmDialog(Editor.this, Lang.getString("editor.saveUnsavedChanges.text"), 
					Lang.getString("editor.saveUnsavedChanges.title"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (input == JOptionPane.YES_OPTION) {
				save();
				dispose();
			} else if (input == JOptionPane.NO_OPTION) {
				dispose();
			}
		} else {
			dispose();
		}
	}
	
	private class WindowCloseListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			quit();
		}
	}
}
