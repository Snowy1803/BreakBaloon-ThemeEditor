package st.infos.elementalcube.breakbaloon.theme.editor.contenteditor;

import st.infos.elementalcube.breakbaloon.theme.editor.drawing.DrawEditor;

import javax.swing.undo.UndoManager;

public abstract class ImageContentEditor extends ContentEditor {
	private static final long serialVersionUID = 1827994183055703933L;

	public ImageContentEditor(String name) {
		super(name);
	}
	
	public abstract DrawEditor currentDrawEditor();
	
	@Override
	public UndoManager getUndoManager() {
		return currentDrawEditor() == null ? null : currentDrawEditor().undoManager;
	}
}
