package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import st.infos.elementalcube.breakbaloon.theme.editor.Editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

public class DrawEditor extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -5583820906102563391L;
	private BufferedImage image;
	private int zoomLevel = 5;
	private Point last;
	private Editor editor;
	private ImageEditorToolbar toolbar;
	
	public DrawEditor(Editor editor, Dimension imageDimension) {
		setTransferHandler(new ImageEditorTransferHandler(this));
		setDoubleBuffered(false);
		addMouseListener(this);
		addMouseMotionListener(this);
		image = new BufferedImage(imageDimension.width, imageDimension.height, BufferedImage.TYPE_INT_RGB);
		this.editor = editor;
		for (int i = 0; i < image.getWidth(); i++) {
	        for (int j = 0; j < image.getHeight(); j++) {
	            image.setRGB(i, j, Color.white.getRGB());
	        }
	    }
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, image.getWidth() * getZoomLevel(), image.getHeight() * zoomLevel, null);
	}
	
	public void setToolbar(ImageEditorToolbar toolbar) {
		this.toolbar = toolbar;
	}
	
	public void clear() {
		int rgb = Integer.parseInt(editor.theme.getMetadata("background", null, "" + 0xFFFFFF));
		for (int i = 0; i < image.getWidth(); i++) {
	        for (int j = 0; j < image.getHeight(); j++) {
	            image.setRGB(i, j, rgb);
	        }
	    }
		repaint();
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {
		draw(e.getPoint(), SwingUtilities.isLeftMouseButton(e));
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		last = null;
		editor.repaint();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		draw(e.getPoint(), SwingUtilities.isLeftMouseButton(e));
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {}
	
	private void draw(Point point, boolean leftClick) {
		Point scaled = new Point(point.x / zoomLevel, point.y / zoomLevel);
		if (last == null) last = scaled;
		try {
			drawLineImpl(new Point(scaled), leftClick ? toolbar.currentColor.getRGB() : getBackgroundColor());
		} catch (ArrayIndexOutOfBoundsException ex) {}
		repaint();
		last = scaled;
	}
	
	private int getBackgroundColor() {
		return Integer.parseInt(editor.theme.getMetadata("background", null, "" + 0xFFFFFF));
	}

	private void drawLineImpl(Point point, int color) {
		int x = last.x, x2 = point.x, y = last.y, y2 = point.y;
		int w = x2 - x;
		int h = y2 - y;
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
		if (w < 0) dx1 = -1;
		else if (w > 0) dx1 = 1;
		if (h < 0) dy1 = -1;
		else if (h > 0) dy1 = 1;
		if (w < 0) dx2 = -1;
		else if (w > 0) dx2 = 1;
		int longest = Math.abs(w);
		int shortest = Math.abs(h);
		if (!(longest > shortest)) {
			longest = Math.abs(h);
			shortest = Math.abs(w);
			if (h < 0) dy2 = -1;
			else if (h > 0) dy2 = 1;
			dx2 = 0;
		}
		int numerator = longest >> 1;
		for (int i = 0; i <= longest; i++) {
			image.setRGB(x, y, color);
			numerator += shortest;
			if (!(numerator < longest)) {
				numerator -= longest;
				x += dx1;
				y += dy1;
			} else {
				x += dx2;
				y += dy2;
			}
		}
	}
	
	public int getZoomLevel() {
		return zoomLevel;
	}
	
	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}
}

class ImageEditorTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 7842831186846618964L;
	private DrawEditor editor;
	
	public ImageEditorTransferHandler(DrawEditor editor) {
		this.editor = editor;
	}
	
	@Override
	public boolean canImport(TransferSupport ts) {
		if ((COPY & ts.getSourceDropActions()) == COPY) {// Force copy
			ts.setDropAction(COPY);
		} else {
			return false;
		}
		return ts.isDataFlavorSupported(DataFlavor.imageFlavor) || ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
	}
	
	@Override
	public boolean importData(TransferSupport ts) {
		try {
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
		}
		return false;
	}
	
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		
		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		
		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		
		// Return the buffered image
		return bimage;
	}
}
