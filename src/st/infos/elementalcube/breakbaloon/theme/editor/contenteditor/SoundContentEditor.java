package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import st.infos.elementalcube.breakbaloon.theme.editor.BBTheme;
import st.infos.elementalcube.breakbaloon.theme.editor.Editor;
import st.infos.elementalcube.snowylangapi.Lang;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.swing.TransferHandler;

import org.apache.commons.io.IOUtils;

public class SoundContentEditor extends ContentEditor implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -4537253303749917591L;
	private byte[] content;
	private EnumSoundType type;
	private Rectangle defaultButton, removeButton;
	private Polygon playButton;
	private boolean hoverDefaultButton, hoverPlayButton, hoverRemoveButton, isPlaying;
	private Editor editor;
	
	public SoundContentEditor(Editor editor, String name, EnumSoundType type) {
		super(name);
		this.type = type;
		this.editor = editor;
		addMouseMotionListener(this);
		addMouseListener(this);
		setTransferHandler(new SoundEditorTransferHandler(this));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(new Color(160, 160, 160));
		g2d.setStroke(new BasicStroke(10));
		g2d.draw(new Ellipse2D.Double(getWidth() / 2 - 100, getHeight() / 3 - 100, 200, 200));
		try {
			g2d.drawImage(ImageIO.read(getClass().getResource("/img/pump.png")), getWidth() / 2 - 64, getHeight() / 3 - 64, 128, 128, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (content == null) {
			g2d.setFont(new Font(null, Font.BOLD, 25));
			String s1 = Lang.getString("editor.sound.drag");
			g2d.drawString(s1, getWidth() / 2 - (int) g2d.getFont().getStringBounds(s1, g2d.getFontRenderContext()).getWidth() / 2,
					getHeight() / 3 * 2);
			String s2 = Lang.getString("editor.sound.or");
			g2d.drawString(s2, getWidth() / 2 - (int) g2d.getFont().getStringBounds(s2, g2d.getFontRenderContext()).getWidth() / 2,
					getHeight() / 30 * 22);
					
			String s3 = Lang.getString("editor.sound.useDefault");
			int s3width = (int) g2d.getFont().getStringBounds(s3, g2d.getFontRenderContext()).getWidth();
			defaultButton = new Rectangle(getWidth() / 2 - s3width / 2 - 10, getHeight() / 10 * 8 - 30, s3width + 20, 40);
			g2d.fill(defaultButton);
			g2d.setColor(hoverDefaultButton ? new Color(240, 240, 240) : Color.WHITE);
			g2d.drawString(s3, getWidth() / 2 - s3width / 2, getHeight() / 10 * 8);
		} else {
			int toolbarY = getHeight() / 3 * 2;
			// Play button
			if (!isPlaying) {
				g2d.setColor(hoverPlayButton ? Color.LIGHT_GRAY : new Color(160, 160, 160));
				playButton = new Polygon(new int[] { getWidth() / 2 - 10, getWidth() / 2 - 10, getWidth() / 2 + 10 },
						new int[] { toolbarY - 10, toolbarY + 10, toolbarY }, 3);
				g2d.fillPolygon(playButton);
			} else {
				playButton = null;
			}
			// Remove button
			g2d.setStroke(new BasicStroke(5));
			g2d.setColor(hoverRemoveButton ? Color.LIGHT_GRAY : new Color(160, 160, 160));
			removeButton = new Rectangle(getWidth() - 30, 10, 20, 20);
			g2d.drawLine((int) removeButton.getMinX(), (int) removeButton.getMinY(), (int) removeButton.getMaxX(), (int) removeButton.getMaxY());
			g2d.drawLine((int) removeButton.getMaxX(), (int) removeButton.getMinY(), (int) removeButton.getMinX(), (int) removeButton.getMaxY());
		}
	}
	
	@Override
	public void paintOverview(Dimension dimension, Graphics2D g2d) {
		super.paintOverview(dimension, g2d);
		try {
			g2d.drawImage(ImageIO.read(getClass().getResource("/img/" + type.name().toLowerCase() + ".png")), dimension.width / 2 - 32,
					dimension.height / 2 - 32, 64, 64, null);
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
		if (defaultButton != null) {
			hoverDefaultButton = defaultButton.contains(e.getPoint());
		}
		if (playButton != null) {
			hoverPlayButton = playButton.contains(e.getPoint());
		}
		if (removeButton != null) {
			hoverRemoveButton = removeButton.contains(e.getPoint());
		}
		repaint();
	}
	
	public void setBytes(byte[] bytes) {
		content = bytes;
		editor.makeDirty();
		repaint();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (hoverDefaultButton) {
			try {
				setBytes(IOUtils.toByteArray(getClass().getResourceAsStream("/sounds/" + type.name().toLowerCase() + ".wav")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (hoverRemoveButton) {
			setBytes(null);
		} else if (hoverPlayButton && !isPlaying) {
			play();
		}
	}
	
	private synchronized void play() {
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(content));
			clip.open(inputStream);
			clip.start();
			isPlaying = true;
			repaint();
			clip.addLineListener(new LineListener() {
				@Override
				public void update(LineEvent e) {
					if (e.getType() == Type.STOP) {
						isPlaying = false;
						repaint();
					}
				}
			});
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
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
		try {
			if (ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>) ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				if (files.size() == 1) {
					editor.setBytes(Files.readAllBytes(files.get(0).toPath()));
				}
				editor.repaint();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
