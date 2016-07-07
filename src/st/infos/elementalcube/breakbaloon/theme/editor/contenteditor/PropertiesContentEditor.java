package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PropertiesContentEditor extends ContentEditor {
	private static final long serialVersionUID = 5506200457569487080L;

	public PropertiesContentEditor(String name) {
		super(name);
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
}
