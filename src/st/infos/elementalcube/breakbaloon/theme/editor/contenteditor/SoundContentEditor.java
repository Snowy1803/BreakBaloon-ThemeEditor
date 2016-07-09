package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import st.infos.elementalcube.breakbaloon.theme.editor.BBTheme;
import st.infos.elementalcube.snowylangapi.Lang;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.TransferHandler;

public class SoundContentEditor extends ContentEditor implements MouseMotionListener {
	private static final long serialVersionUID = -4537253303749917591L;
	private byte[] content;
	private EnumSoundType type;
	private Rectangle defaultButton;
	private boolean hoverDefaultButton;

	public SoundContentEditor(String name, EnumSoundType type) {
		super(name);
		this.type = type;
		addMouseMotionListener(this);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(new Color(160, 160, 160));
		g2d.setStroke(new BasicStroke(10));
		g2d.draw(new Ellipse2D.Double(getWidth() / 2 - 100, getHeight() / 3 - 100, 200, 200));
		try {
			g2d.drawImage(ImageIO.read(getClass().getResource("/img/pump.png")), 
					getWidth() / 2 - 64, getHeight() / 3 - 64, 128, 128, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		g2d.setFont(new Font(null, Font.BOLD, 25));
		String s1 = Lang.getString("editor.sound.drag");
		g2d.drawString(s1, getWidth() / 2 - (int) g2d.getFont().getStringBounds(s1, g2d.getFontRenderContext()).getWidth() / 2, getHeight() / 3 * 2);
		String s2 = Lang.getString("editor.sound.or");
		g2d.drawString(s2, getWidth() / 2 - (int) g2d.getFont().getStringBounds(s2, g2d.getFontRenderContext()).getWidth() / 2, getHeight() / 30 * 22);

		String s3 = Lang.getString("editor.sound.useDefault");
		int s3width = (int) g2d.getFont().getStringBounds(s3, g2d.getFontRenderContext()).getWidth();
		defaultButton = new Rectangle(getWidth() / 2 - s3width / 2 - 10, getHeight() / 10 * 8 - 30, s3width + 20, 40);
		g2d.fill(defaultButton);
		g2d.setColor(hoverDefaultButton ? new Color(240, 240, 240) : Color.WHITE);
		g2d.drawString(s3, getWidth() / 2 - s3width / 2, getHeight() / 10 * 8);
	}
	
	@Override
	public void paintOverview(Dimension dimension, Graphics2D g2d) {
		super.paintOverview(dimension, g2d);
		try {
			g2d.drawImage(ImageIO.read(getClass().getResource("/img/" + type.name().toLowerCase() + ".png")), 
					dimension.width / 2 - 32, dimension.height / 2 - 32, 64, 64, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveToBBTheme(BBTheme theme) {
		switch (type) {
		case PUMP:
			theme.pump = content;
			break;
		case WPUMP:
			theme.wpump = content;
			break;
		}
	}

	@Override
	public void loadFromBBTheme(BBTheme theme) {
		switch (type) {
		case PUMP:
			content = theme.pump;
			break;
		case WPUMP:
			content = theme.wpump;
			break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		hoverDefaultButton = defaultButton.contains(e.getPoint());
		repaint();
	}
	
	public static enum EnumSoundType {
		PUMP, WPUMP;
	}
}

class SoundEditorTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 7842831186846618964L;
	private SoundContentEditor editor;
	
	public SoundEditorTransferHandler(SoundContentEditor editor) {
		this.editor = editor;
	}
	
	@Override
	public boolean canImport(TransferSupport ts) {
		if ((COPY & ts.getSourceDropActions()) == COPY) {// Force copy
			ts.setDropAction(COPY);
		} else {
			return false;
		}
		return ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
	}
	
	@Override
	public boolean importData(TransferSupport ts) {
		/*try {//TODO
			if (ts.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				editor.setImage(toBufferedImage((Image) ts.getTransferable().getTransferData(DataFlavor.imageFlavor)));
				editor.repaint();
				return true;
			} else if (ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>) ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				if (files.size() == 1) {
					editor.setImage(ImageIO.read(files.get(0)));
				}
				editor.repaint();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return false;
	}
}
