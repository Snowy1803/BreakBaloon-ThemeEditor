package st.infos.elementalcube.breakbaloon.theme.editor;

import st.infos.elementalcube.snowylangapi.Lang;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Editor extends JFrame {
	private static final long serialVersionUID = -247298518651532746L;
	private BBTheme theme;
	private boolean saved;
	private File saveDirectory;
	
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
		
		setSize(900, 750);
		setLocationRelativeTo(null);
		addWindowListener(new WindowCloseListener());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
