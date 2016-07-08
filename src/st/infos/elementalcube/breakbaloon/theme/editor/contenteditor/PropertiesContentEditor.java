package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import st.infos.elementalcube.breakbaloon.theme.editor.BBTheme;
import st.infos.elementalcube.snowylangapi.Lang;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class PropertiesContentEditor extends ContentEditor {
	private static final long serialVersionUID = 5506200457569487080L;
	private JTabbedPane tabbedPane;
	private PropertiesEditor baseLang;

	public PropertiesContentEditor(String name) {
		super(name);
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(Lang.getString("props.lang.base"), null, new PropertiesEditor(null), Lang.getString("props.lang.base.tooltip"));
		tabbedPane.addTab(Lang.getString("props.lang.fr_FR"), null, new PropertiesEditor(Locale.FRANCE), Lang.getString("props.lang.fr_FR.tooltip"));
		tabbedPane.addTab(Lang.getString("props.lang.en_US"), null, new PropertiesEditor(Locale.US), Lang.getString("props.lang.en_US.tooltip"));
		tabbedPane.addTab(Lang.getString("props.lang.da_DK"), null, new PropertiesEditor(Locale.forLanguageTag("da-DK")), 
				Lang.getString("props.lang.da_DK.tooltip"));//LangLoader
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadFromBBTheme(BBTheme theme) {
		// TODO Auto-generated method stub
		
	}
	
	private class PropertiesEditor extends JPanel {
		private static final long serialVersionUID = 8013423756318123940L;

		public PropertiesEditor(Locale lang) {
			if (lang == null) baseLang = this;
			
			// TODO construct
		}
	}
}
