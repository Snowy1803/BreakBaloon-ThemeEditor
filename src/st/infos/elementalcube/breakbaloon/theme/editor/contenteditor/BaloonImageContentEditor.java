package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import st.infos.elementalcube.breakbaloon.theme.editor.BBTheme;
import st.infos.elementalcube.breakbaloon.theme.editor.Editor;
import st.infos.elementalcube.snowylangapi.Lang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class BaloonImageContentEditor extends ContentEditor {
	private static final long serialVersionUID = 6556988077515024109L;
	private EnumBaloonType type;
	private DrawEditor[] editors;
	private ImageEditorToolbar toolbar;
	private Editor frame;
	private JSplitPane pane;
	
	private Image overviewCache;
	
	public BaloonImageContentEditor(Editor editor, String name, EnumBaloonType type) {
		super(name);
		setLayout(new BorderLayout());
		this.type = type;
		this.toolbar = new ImageEditorToolbar();
		this.frame = editor;
		editors = new DrawEditor[Integer.parseInt(editor.theme.getMetadata("baloons", null))];
		constructEditors();
		JScrollPane selector = new JScrollPane(new ImageSelection());
		selector.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, selector, this.editors[0]);
		pane.setContinuousLayout(true);
		pane.setResizeWeight(0);
		pane.setEnabled(false);
		add(pane, BorderLayout.CENTER);
		add(toolbar, BorderLayout.SOUTH);
		
		try {
			overviewCache = ImageIO.read(getClass().getResource("/img/" + type.langKey + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paintOverview(Dimension dimension, Graphics2D g2d) {
		super.paintOverview(dimension, g2d);
		g2d.drawImage(overviewCache, dimension.width / 2 - 32, dimension.height / 2 - 32,
				64, 64, null);
	}
	
	public void constructEditors() {
		for (int i = 0; i < editors.length; i++) {
			editors[i] = new DrawEditor(frame, new Dimension(75, 75));
			editors[i].setToolbar(toolbar);
		}
	}

	@Override
	public void saveToBBTheme(BBTheme theme) {
		BufferedImage[] images = new BufferedImage[editors.length];
		for (int i = 0; i < editors.length; i++) {
			images[i] = editors[i].getImage();
		}
		switch (type) {
		case CLOSED:
			theme.closed = images;
			break;
		case OPENED:
			theme.opened = images;
			break;
		case OPENED_GOOD:
			theme.openedGood = images;
			break;
		}
	}

	@Override
	public void loadFromBBTheme(BBTheme theme) {
		BufferedImage[] images = null;
		switch (type) {
		case CLOSED:
			images = theme.closed;
			break;
		case OPENED:
			images = theme.opened;
			break;
		case OPENED_GOOD:
			images = theme.openedGood;
			break;
		}
		editors = Arrays.copyOf(editors, Integer.parseInt(theme.getMetadata("baloons", null)));
		if (images != null) {
			for (int i = 0; i < editors.length; i++) {
				if (editors[i] == null) {
					editors[i] = new DrawEditor(frame, new Dimension(75, 75));
					editors[i].setToolbar(toolbar);
				} else {
					editors[i].setImage(images[i]);
				}
			}
		}
	}
	
	public EnumBaloonType getBaloonType() {
		return type;
	}
	
	public static enum EnumBaloonType {
		CLOSED("closed"), OPENED("opened"), OPENED_GOOD("openedGood");
		
		private String langKey;
		
		private EnumBaloonType(String langKey) {
			this.langKey = langKey;
		}
	}
	
	private class ImageSelection extends JPanel implements MouseListener, MouseMotionListener {
		private static final long serialVersionUID = 3672102224710209295L;
		private int hover;
		
		public ImageSelection() {
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {
			int index = e.getX() / 100;
			if (index < editors.length) {
				pane.setBottomComponent(editors[index]);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			hover = e.getX() / 100;
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			hover = -1;
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			hover = e.getX() / 100;
			repaint();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			for (int i = 0; i < editors.length; i++) {
				g2d.setColor(pane.getBottomComponent() == editors[i] ? Color.LIGHT_GRAY : hover == i ? new Color(240, 240, 240) : Color.WHITE);
				g2d.fillRect(i * 100, 0, (i + 1) * 100, getHeight());
				g2d.drawImage(editors[i].getImage(), i * 100 + (50 - editors[i].getImage().getWidth() / 2), 0, null);
				g2d.setColor(Color.BLACK);
				String s = Lang.getString("editor.image.number", i + 1);
				g2d.drawString(s, i * 100 + (int) (50 - g2d.getFont().getStringBounds(s, g2d.getFontRenderContext()).getWidth() / 2), getHeight() - 5);
			}
		}
		
		@Override
		public int getWidth() {
			return 100 * editors.length;
		}
		
		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(getWidth(), 100);
		}
		
		@Override
		public Dimension getMaximumSize() {
			return getMinimumSize();
		}
	}
}
