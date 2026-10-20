package fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import fr.ifremer.nemo.ui.edit.data.TypeData;


public class FormatEditingSupport extends EditingSupport {

	private final TableViewer viewer;
	private final CellEditor editor;

	public FormatEditingSupport(final TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new TextCellEditor(viewer.getTable());
		this.editor.getControl();
		this.editor.addListener(new ICellEditorListener() {
			@Override
			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
				if (newValidState && null != viewer.getColumnViewerEditor()
						&& null != viewer.getColumnViewerEditor().getFocusCell()
						&& null != viewer.getColumnViewerEditor().getFocusCell().getElement()
						&& viewer.getColumnViewerEditor().getFocusCell().getElement() instanceof TypeData) {
					setValue(viewer.getColumnViewerEditor().getFocusCell().getElement(), editor.getValue());
				}
			}

			@Override
			public void applyEditorValue() {
			}

			@Override
			public void cancelEditor() {
			}
		});
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
//		return editorView.isEnabled();
		TypeData typeData = (TypeData) element;
		return !typeData.isText();
	}

	@Override
	protected Object getValue(Object element) {
		TypeData typeData = (TypeData) element;
		
		String format = null;
//		if (typeData.isText()) {
////			format = "";
//		} else {
			format = typeData.getFormat();
//		}
		return format;

		
		
	}

	@Override
	protected void setValue(Object element, Object value) {
		String format = ((String) value).trim();
		TypeData typeData = (TypeData) element;
		
		typeData.setFormat(format);
		viewer.update(element, null);
		

	}




}
