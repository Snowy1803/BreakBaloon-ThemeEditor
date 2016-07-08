package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import st.infos.elementalcube.breakbaloon.theme.editor.BBTheme;
import st.infos.elementalcube.breakbaloon.theme.editor.Editor;
import st.infos.elementalcube.snowylangapi.Lang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class PropertiesContentEditor extends ContentEditor {
	private static final long serialVersionUID = 5506200457569487080L;
	private JTabbedPane tabbedPane;
	private PropertiesEditor baseLang;
	private PropertiesEditor[] langEditors = new PropertiesEditor[3];
	private Editor editor;

	public PropertiesContentEditor(Editor editor, String name) {
		super(name);
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(Lang.getString("props.lang.base"), null, new PropertiesEditor(-1, null), Lang.getString("props.lang.base.tooltip"));
		tabbedPane.addTab(Lang.getString("props.lang.fr_FR"), null, new PropertiesEditor(0, Locale.FRANCE), Lang.getString("props.lang.fr_FR.tooltip"));
		tabbedPane.addTab(Lang.getString("props.lang.en_US"), null, new PropertiesEditor(1, Locale.US), Lang.getString("props.lang.en_US.tooltip"));
		tabbedPane.addTab(Lang.getString("props.lang.da_DK"), null, new PropertiesEditor(2, Locale.forLanguageTag("da-DK")), 
				Lang.getString("props.lang.da_DK.tooltip"));
		this.editor = editor;
		propertyChange();
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
	}
	
	@Override
	public void paintOverview(Dimension dimension, Graphics2D g2d) {
		super.paintOverview(dimension, g2d);
		try {
			g2d.drawImage(ImageIO.read(getClass().getResource("/img/properties.png")), dimension.width /2 - 32, dimension.height /2 - 32, 64, 64, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveToBBTheme(BBTheme theme) {
		baseLang.save(theme);
		for (PropertiesEditor pe : langEditors) {
			pe.save(theme);
		}
	}

	@Override
	public void loadFromBBTheme(BBTheme theme) {
		baseLang.load(theme);
		for (PropertiesEditor pe : langEditors) {
			pe.load(theme);
		}
		propertyChange();
	}
	
	private void propertyChange() {
		for (PropertiesEditor pe : langEditors) {
			pe.updateFields();
		}
	}
	
	private class PropertiesEditor extends JPanel implements ActionListener {
		private static final long serialVersionUID = 8013423756318123940L;
		private JTextField name, description, version, author;
		private JFormattedTextField baloons;
		private JPanel contentPane;
		private Locale locale;
		private JLabel baloonsLabel;
		private boolean loaded = false;

		public PropertiesEditor(int i, Locale lang) {
			super(new BorderLayout());
			if (lang == null) {
				baseLang = this;
			} else {
				langEditors[i] = this;
			}
			
			contentPane = new JPanel(new GridLayout(0, 2, 0, 5));
			locale = lang;
			
			// TODO other properties: background, differentBaloonsForPumpedGood
			name = new JTextField();
			description = new JTextField();
			version = new JTextField();
			author = new JTextField();
			
			name.setPreferredSize(new Dimension(name.getWidth(), 23));
			description.setPreferredSize(new Dimension(name.getWidth(), 23));
			version.setPreferredSize(new Dimension(name.getWidth(), 23));
			author.setPreferredSize(new Dimension(name.getWidth(), 23));
			
			contentPane.add(createLabel("editor.props.name"));
			contentPane.add(name);
			contentPane.add(createLabel("editor.props.description"));
			contentPane.add(description);
			contentPane.add(createLabel("editor.props.version"));
			contentPane.add(version);
			contentPane.add(createLabel("editor.props.author"));
			contentPane.add(author);
			if (baseLang == this) {
				baloons = new JFormattedTextField(NumberFormat.getIntegerInstance(Lang.getInstance().usedLocale()));
				baloons.addActionListener(this);
				baloons.setActionCommand("baloons");
				contentPane.add(new JSeparator());//Left
				contentPane.add(new JSeparator());//Right
				contentPane.add(baloonsLabel = new JLabel(Lang.getString("editor.props.baloons")));
				contentPane.add(baloons);
			}
			add(contentPane, BorderLayout.NORTH);
		}
		
		public void load(BBTheme theme) {
			setText(name, theme.getMetadata("name", locale));
			setText(description, theme.getMetadata("description", locale));
			setText(version, theme.getMetadata("version", locale));
			setText(author, theme.getMetadata("author", locale));
			if (baloons != null) {
				setText(baloons, theme.getMetadata("baloons", locale));
			}
			loaded = true;
		}
		
		public void save(BBTheme theme) {
			theme.setMetadata("name", locale, name.getText());
			theme.setMetadata("description", locale, description.getText());
			theme.setMetadata("version", locale, version.getText());
			theme.setMetadata("author", locale, author.getText());
			if (baloons != null && baloonsLabel.getForeground() == Color.BLACK) {
				theme.setMetadata("baloons", locale, baloons.getText());
			}
		}
		
		private void setText(JTextField text, String value) {
			Component c = contentPane.getComponent(Arrays.asList(contentPane.getComponents()).indexOf(text) - 1);
			if (c instanceof JCheckBox && !loaded) {
				((JCheckBox) c).setSelected(value != null);
				text.setEnabled(value != null);
			}
			text.setText(value);
		}

		private JComponent createLabel(String label) {
			if (baseLang == this) {
				JCheckBox check = new JCheckBox(Lang.getString(label));
				check.addActionListener(this);
				check.setSelected(true);
				check.setActionCommand(label);
				return check;
			}
			return new JLabel(Lang.getString(label));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("editor.props.name")) {
				name.setEnabled(((JCheckBox) e.getSource()).isSelected());
			} else if (e.getActionCommand().equals("editor.props.description")) {
				description.setEnabled(((JCheckBox) e.getSource()).isSelected());
			} else if (e.getActionCommand().equals("editor.props.version")) {
				version.setEnabled(((JCheckBox) e.getSource()).isSelected());
			} else if (e.getActionCommand().equals("editor.props.author")) {
				author.setEnabled(((JCheckBox) e.getSource()).isSelected());
			} else if ("baloons".equals(e.getActionCommand())) {
				try {
					if (Integer.parseInt(baloons.getText()) > 32) {
						baloonsLabel.setText(Lang.getString("editor.props.baloons.toobig"));
						baloonsLabel.setForeground(Color.RED);
						return;
					} else if (Integer.parseInt(baloons.getText()) < 1) {
						baloonsLabel.setText(Lang.getString("editor.props.baloons.toosmall"));
						baloonsLabel.setForeground(Color.RED);
						return;
					} else {
						baloonsLabel.setText(Lang.getString("editor.props.baloons"));
						baloonsLabel.setForeground(Color.BLACK);
					}
				} catch (NumberFormatException ex) {}
			}
			propertyChange();
			editor.reload();
		}
		
		private void updateFields() {
			name.setEnabled(!baseLang.name.isEnabled());
			description.setEnabled(!baseLang.description.isEnabled());
			version.setEnabled(!baseLang.version.isEnabled());
			author.setEnabled(!baseLang.author.isEnabled());
		}
	}
}
