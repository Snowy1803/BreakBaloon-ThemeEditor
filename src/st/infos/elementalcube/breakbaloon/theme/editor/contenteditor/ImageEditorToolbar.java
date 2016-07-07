package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import st.infos.elementalcube.snowylangapi.Lang;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class ImageEditorToolbar extends JToolBar {
	private static final long serialVersionUID = 2792625817326549362L;
	private static final String BUTTON_COLOR = "color";
	private DrawEditor editor;
	
	public ImageEditorToolbar(DrawEditor editor) {
		super(Lang.getString("editor.image.toolbar"));
		this.editor = editor;
		adaptColorButton(addButton(BUTTON_COLOR));
	}
	
	private void adaptColorButton(JButton color) {
		try {
			BufferedImageOp lookup = new LookupOp(new ColorMapper(Color.WHITE, editor.getCurrentColor()), null);
			BufferedImage convertedImage = lookup.filter(ImageIO.read(getClass().getResource("/img/" + BUTTON_COLOR + ".png")), null);
			color.setIcon(new ImageIcon(convertedImage));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JButton addButton(String name) {
		JButton button = new JButton(new ImageIcon(getClass().getResource("/img/" + name + ".png")));
		button.setActionCommand(name);
		button.setToolTipText(Lang.getString("editor.image.toolbar." + name));
		add(button);
		return button;
	}
	
	public class ColorMapper extends LookupTable {
	    private final int[] from;
	    private final int[] to;

	    public ColorMapper(Color from, Color to) {
	        super(0, 4);

	        this.from = new int[] {
	            from.getRed(),
	            from.getGreen(),
	            from.getBlue(),
	            from.getAlpha(),
	        };
	        this.to = new int[] {
	            to.getRed(),
	            to.getGreen(),
	            to.getBlue(),
	            to.getAlpha(),
	        };
	    }

	    @Override
	    public int[] lookupPixel(int[] src, int[] dest) {
	        if (dest == null) {
	            dest = new int[src.length];
	        }

	        int[] newColor = (Arrays.equals(src, from) ? to : src);
	        System.arraycopy(newColor, 0, dest, 0, newColor.length);

	        return dest;
	    }
	}
}
