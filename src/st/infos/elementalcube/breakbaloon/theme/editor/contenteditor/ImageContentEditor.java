package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

public abstract class ImageContentEditor extends ContentEditor {
	private static final long serialVersionUID = 1827994183055703933L;

	public ImageContentEditor(String name) {
		super(name);
	}
	
	public abstract DrawEditor currentDrawEditor();
}
