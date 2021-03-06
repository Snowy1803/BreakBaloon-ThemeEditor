package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import st.infos.elementalcube.breakbaloon.theme.editor.BBTheme;
import st.infos.elementalcube.breakbaloon.theme.editor.Editor;
import st.infos.elementalcube.breakbaloon.theme.editor.LeftMenuComponent;
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
			g2d.drawImage(ImageIO.read(getClass().getResource("/img/properties.png")), dimension.width /2 - 32, dimension.height /2 - 32, 
					64, 64, null);
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
		private JButton backgroundChooser;
		private boolean loaded = false;
		private JCheckBox dbfpg;

		public PropertiesEditor(int i, Locale lang) {
			super(new BorderLayout());
			if (lang == null) {
				baseLang = this;
			} else {
				langEditors[i] = this;
			}
			
			contentPane = new JPanel(new GridLayout(0, 2, 0, 5));
			locale = lang;
			
			name = new JTextField();
			description = new JTextField();
			version = new JTextField();
			author = new JTextField();
			
			name.setPreferredSize(new Dimension(name.getWidth(), 23));
			description.setPreferredSize(new Dimension(name.getWidth(), 23));
			version.setPreferredSize(new Dimension(name.getWidth(), 23));
			author.setPreferredSize(new Dimension(name.getWidth(), 23));
			
			name.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					editor.makeDirty();
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					editor.makeDirty();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					editor.makeDirty();
				}
			});
			
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
				
				backgroundChooser = new JButton(Lang.getString("editor.props.background.button")) {
					private static final long serialVersionUID = -6643838956810809841L;
					
					@Override
					public Color getForeground() {
						return new Color(0xFFFFFF - getBackground().getRGB());//Complementary color
					}
				};
				backgroundChooser.addActionListener(this);
				backgroundChooser.setActionCommand("background");
                backgroundChooser.setContentAreaFilled(false);
                backgroundChooser.setOpaque(true);
                
                /* Different baloon for pumped good */
                dbfpg = new JCheckBox(Lang.getString("editor.props.dbfpg"));
                dbfpg.setActionCommand("dbfpg");
                dbfpg.addActionListener(this);
				
				contentPane.add(new JSeparator());//Left
				contentPane.add(new JSeparator());//Right
				contentPane.add(baloonsLabel = new JLabel(Lang.getString("editor.props.baloons")));
				contentPane.add(baloons);
				contentPane.add(new JLabel(Lang.getString("editor.props.background")));
				contentPane.add(backgroundChooser);
				contentPane.add(dbfpg);
				contentPane.add(new JPanel());//Nothing to right
			}
			add(contentPane, BorderLayout.NORTH);
		}
		
		public void load(BBTheme theme) {
			setText(name, theme.getMetadata("name", locale, ""));
			setText(description, theme.getMetadata("description", locale, ""));
			setText(version, theme.getMetadata("version", locale, ""));
			setText(author, theme.getMetadata("author", locale, ""));
			if (baloons != null) {
				setText(baloons, theme.getMetadata("baloons", locale, "" + 1));
			}
			if (backgroundChooser != null) {
				backgroundChooser.setBackground(new Color(Integer.parseInt(theme.getMetadata("background", locale, "" + 0xFFFFFF))));
			}
			if (dbfpg != null) {
				dbfpg.setSelected(Boolean.parseBoolean(theme.getMetadata("different-baloon-pumped-good", locale, "" + false)));
				for (Component cmp : editor.getMenuComponents()) {
					((LeftMenuComponent) cmp).dbfpgPropertyChanged(dbfpg.isSelected());
				}
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
			if (backgroundChooser != null) {
				theme.setMetadata("background", locale, "" + backgroundChooser.getBackground().getRGB());
			}
			if (dbfpg != null) {
				theme.setMetadata("different-baloon-pumped-good", locale, "" + dbfpg.isSelected());
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
			} else if ("background".equals(e.getActionCommand())) {
				backgroundChooser.setBackground(JColorChooser.showDialog(this, Lang.getString("editor.props.background.chooser"), 
						backgroundChooser.getBackground()));
			} else if ("dbfpg".equals(e.getActionCommand())) {
				for (Component cmp : editor.getMenuComponents()) {
					((LeftMenuComponent) cmp).dbfpgPropertyChanged(dbfpg.isSelected());
				}
			}
			propertyChange();
			editor.reload();
			editor.makeDirty();
		}
		
		private void updateFields() {
			name.setEnabled(!baseLang.name.isEnabled());
			description.setEnabled(!baseLang.description.isEnabled());
			version.setEnabled(!baseLang.version.isEnabled());
			author.setEnabled(!baseLang.author.isEnabled());
		}
	}
}
