package st.infos.elementalcube.breakbaloon.theme.editor.drawing;

import st.infos.elementalcube.breakbaloon.theme.editor.Editor;
import st.infos.elementalcube.breakbaloon.theme.editor.drawing.DrawingTool.EnumUseType;

import java.awt.BasicStroke;
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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.undo.UndoManager;

public class DrawEditor extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = -5583820906102563391L;
	private BufferedImage image;
	private int zoomLevel = 5;
	private Point last;
	private Editor editor;
	private ImageEditorToolbar toolbar;
	public Point mouseCoords;
	public UndoManager undoManager;
	
	public DrawEditor(Editor editor, Dimension imageDimension) {
		setTransferHandler(new ImageEditorTransferHandler(this));
		setDoubleBuffered(false);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		undoManager = new UndoManager();
		image = new BufferedImage(imageDimension.width, imageDimension.height, BufferedImage.TYPE_INT_ARGB);
		this.editor = editor;
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				image.setRGB(i, j, getBackgroundColor().getRGB());
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		((Graphics2D) g).setStroke(new BasicStroke(2));
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, image.getWidth() * zoomLevel, image.getHeight() * zoomLevel);
		g.drawImage(image, 0, 0, image.getWidth() * zoomLevel, image.getHeight() * zoomLevel, null);
		toolbar.currentTool.paintOverlay(this, (Graphics2D) g);
	}
	
	public ImageEditorToolbar getToolbar() {
		return toolbar;
	}
	
	public void setToolbar(ImageEditorToolbar toolbar) {
		this.toolbar = toolbar;
	}
	
	public void clear() {
		int rgb = Integer.parseInt(editor.theme.getMetadata("background", null, "" + 0xFFFFFF));
		Point[] points = new Point[image.getWidth() * image.getHeight()];
		Color[] colors = new Color[image.getWidth() * image.getHeight()],
				newColor = new Color[image.getWidth() * image.getHeight()];
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				colors[i * image.getWidth() + j] = new Color(image.getRGB(i, j), true);
				image.setRGB(i, j, rgb);
				newColor[i * image.getWidth() + j] = new Color(rgb, true);
			}
		}
		undoManager.addEdit(new UndoableImageEdit(this, points, colors, newColor));
		repaint();
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) {
		BufferedImage previousImage = this.image;
		this.image = image;
		Point[] points = new Point[image.getWidth() * image.getHeight()];
		Color[] previousColors = new Color[image.getWidth() * image.getHeight()];
		Color[] newColors = new Color[image.getWidth() * image.getHeight()];
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				points[i * image.getWidth() + j] = new Point(i, j);
				previousColors[i * image.getWidth() + j] = new Color(previousImage.getRGB(i, j), true);
				newColors[i * image.getWidth() + j] = new Color(image.getRGB(i, j), true);
			}
		}
		undoManager.addEdit(new UndoableImageEdit(this, points, previousColors, newColors));
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (toolbar.currentTool.canBeUsed(EnumUseType.PRESSED)) {
			draw(e.getPoint(), SwingUtilities.isLeftMouseButton(e));
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (toolbar.currentTool.canBeUsed(EnumUseType.RELEASED)) {
			draw(e.getPoint(), SwingUtilities.isLeftMouseButton(e));
		}
		last = null;
		editor.repaint();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		mouseCoords = e.getPoint();
		if (toolbar.currentTool.needRepainting()) {
			repaint();
		}
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		mouseCoords = null;
		if (toolbar.currentTool.needRepainting()) {
			repaint();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (toolbar.currentTool.canBeUsed(EnumUseType.DRAGGED)) {
			draw(e.getPoint(), SwingUtilities.isLeftMouseButton(e));
		}
		mouseCoords = e.getPoint();
		if (toolbar.currentTool.needRepainting()) {
			repaint();
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseCoords = e.getPoint();
		if (toolbar.currentTool.needRepainting()) {
			repaint();
		}
	}
	
	private void draw(Point point, boolean leftClick) {
		Point scaled = new Point(point.x / zoomLevel, point.y / zoomLevel);
		if (last == null) last = scaled;
		try {
			if (toolbar.currentTool.draw(this, last, new Point(scaled), leftClick ? toolbar.currentColor : getBackgroundColor())) {
				editor.makeDirty();
			}
		} catch (ArrayIndexOutOfBoundsException ex) {}
		repaint();
		last = scaled;
	}
	
	private Color getBackgroundColor() {
		return new Color(0, 0, 0, 0);
	}
	
	public int getZoomLevel() {
		return zoomLevel;
	}
	
	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.isControlDown()) {
			zoomLevel -= e.getWheelRotation();
			repaint();
		}
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
				editor.setImage(toBufferedImage(((Image) ts.getTransferable().getTransferData(DataFlavor.imageFlavor)), 
						editor.getImage().getWidth(), editor.getImage().getHeight()));
				editor.repaint();
				return true;
			} else if (ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>) ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				if (files.size() == 1) {
					editor.setImage(toBufferedImage(ImageIO.read(files.get(0)), editor.getImage().getWidth(), 
							editor.getImage().getHeight()));
				}
				editor.repaint();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static BufferedImage toBufferedImage(Image img, int newWidth, int newHeight) {
		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		
		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, newWidth, newHeight, null);
		bGr.dispose();
		
		// Return the buffered image
		return bimage;
	}
}
