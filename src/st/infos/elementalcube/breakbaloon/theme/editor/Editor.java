package st.infos.elementalcube.breakbaloon.theme.editor;

import st.infos.elementalcube.breakbaloon.theme.editor.contenteditor.ContentEditor;
import st.infos.elementalcube.snowylangapi.Lang;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Editor extends JFrame {
	private static final long serialVersionUID = -247298518651532746L;
	public static final String VERSION = "1.1";
	public BBTheme theme;
	private boolean saved = true;
	private File saveFile;
	private JPanel menu, container;
	
	public Editor(String file) {
		this(new File(file));
	}
	
	public Editor() {
		this.theme = new BBTheme();
		construct();
	}
	
	public Editor(File file) {
		saveFile = file;
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
		setTitle(Lang.getString("editor.name", VERSION));
		
		menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));
		for (LeftMenuComponent component : LeftMenuComponent.getList(this)) {
			menu.add(component);
		}
		menu.setMinimumSize(new Dimension(100, 200 * menu.getComponentCount()));
		menu.setMaximumSize(new Dimension(300, 200 * menu.getComponentCount()));
		container = new JPanel(new GridLayout(1, 1));
		
		for (Component component : menu.getComponents()) {
			((LeftMenuComponent) component).getContentEditor().loadFromBBTheme(theme);
		}
		
		JSplitPane contentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(menu), new JScrollPane(container));
		contentPane.setContinuousLayout(true);
		contentPane.setDividerLocation(200);
		setContentPane(contentPane);
		setJMenuBar(constructJMenuBar());
		setSize(900, 650);
		setLocationRelativeTo(null);
		addWindowListener(new WindowCloseListener());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	private JMenuBar constructJMenuBar() {
		JMenuBar bar = new JMenuBar();
		
		JMenu file = new JMenu(Lang.getString("menu.file"));
		
		JMenuItem newWindow = new JMenuItem(Lang.getString("menu.file.new.window")), open = new JMenuItem(Lang.getString("menu.file.open")),
				save = new JMenuItem(Lang.getString("menu.file.save")), saveAs = new JMenuItem(Lang.getString("menu.file.saveAs")),
				exportZip = new JMenuItem(Lang.getString("menu.file.export.zip")), addIngame = new JMenuItem(Lang.getString("menu.file.ingame"));
				
		newWindow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
		exportZip.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
		
		file.setMnemonic('F');
		newWindow.setMnemonic('N');
		open.setMnemonic('O');
		save.setMnemonic('S');
		saveAs.setMnemonic('A');
		exportZip.setMnemonic('E');
		addIngame.setMnemonic('I');
		
		newWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Editor().setVisible(true);
			}
		});
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		saveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
		});
		exportZip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportZip();
			}
		});
		addIngame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addIngame();
			}
		});
		
		file.add(newWindow);
		file.add(open);
		file.addSeparator();
		file.add(save);
		file.add(saveAs);
		file.add(exportZip);
		file.add(addIngame);
		
		bar.add(file);
		
		return bar;
	}
	
	public void setContentEditor(ContentEditor content) {
		container.removeAll();
		if (content != null) {
			container.add(content);
		}
		revalidate();
	}
	
	public ContentEditor getContentEditor() {
		return container.getComponentCount() == 1 ? (ContentEditor) container.getComponent(0) : null;
	}
	
	public void makeDirty() {
		saved = false;
		setTitle(Lang.getString("editor.name.unsaved", VERSION));
	}
	
	public void saved() {
		saved = true;
		setTitle(Lang.getString("editor.name", VERSION));
	}
	
	// Editor actions
	
	public void open() {
		JFileChooser chooser = new JFileChooser(saveFile);
		chooser.setFileFilter(new FileNameExtensionFilter(Lang.getString("filefilter.bbtheme"), "bbtheme"));
		chooser.showOpenDialog(this);
		if (chooser.getSelectedFile() != null) {
			new Editor(chooser.getSelectedFile()).setVisible(true);
		}
	}
	
	public void save() {
		if (saveFile == null) {
			saveAs();
			return;
		}
		reload();
		try {
			showWarningList(theme.saveToDirectory(saveFile));
			saved();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveAs() {
		JFileChooser chooser = new JFileChooser(saveFile);
		chooser.setFileFilter(new FileNameExtensionFilter(Lang.getString("filefilter.bbtheme"), "bbtheme"));
		chooser.showSaveDialog(this);
		if (chooser.getSelectedFile() != null) {
			saveFile = chooser.getSelectedFile();
			save();
		}
	}
	
	public void exportZip() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter(Lang.getString("filefilter.zip"), "zip"));
		chooser.showSaveDialog(this);
		if (chooser.getSelectedFile() != null) {
			reload();
			try {
				showWarningList(theme.saveToZip(chooser.getSelectedFile()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addIngame() {
		reload();
		String id = JOptionPane.showInputDialog(this, Lang.getString("transfer.ingame.text"), Lang.getString("transfer.ingame.title"), 
				JOptionPane.PLAIN_MESSAGE);
		if (id != null) {
			try {
				File file = new File(System.getenv("APPDATA") + "/BreakBaloon/resources/" + id + "/" + id + ".bbtheme");
				file.getParentFile().mkdirs();
				showWarningList(theme.saveToDirectory(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void showWarningList(List<String> warning) {
		if(!warning.isEmpty()) {
			JOptionPane.showMessageDialog(this, new JList<>(warning.toArray()), Lang.getString("save.warning.title"), JOptionPane.PLAIN_MESSAGE);
		}
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
	
	public void reload() {
		for (Component leftPane : menu.getComponents()) {
			((LeftMenuComponent) leftPane).getContentEditor().saveToBBTheme(theme);
		}
		for (Component leftPane : menu.getComponents()) {
			((LeftMenuComponent) leftPane).getContentEditor().loadFromBBTheme(theme);
		}
	}
	
	public Component[] getMenuComponents() {
		return menu.getComponents();
	}
}
