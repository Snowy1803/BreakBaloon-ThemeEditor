package st.infos.elementalcube.breakbaloon.theme.editor;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

public class Editor extends JFrame {
	private static final long serialVersionUID = -247298518651532746L;
	private BBTheme theme;
	
	public Editor(String file) {
		this(new File(file));
	}

	public Editor() {
		this.theme = new BBTheme();
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
	}
}
