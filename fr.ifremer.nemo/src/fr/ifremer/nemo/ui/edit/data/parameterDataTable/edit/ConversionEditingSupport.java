package fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import fr.ifremer.nemo.ui.edit.data.TypeData;


public class ConversionEditingSupport extends EditingSupport {

	private final TableViewer tableViewer;
	private final CellEditor editor;

	public ConversionEditingSupport(final TableViewer viewer) {
		super(viewer);
		  this.tableViewer = viewer;
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
//		return true;
		TypeData typeData = (TypeData) element;
		return !typeData.isText();
	}

	@Override
	protected Object getValue(Object element) {
		TypeData typeData = (TypeData) element;
		
		String conversion = "";
		if (typeData.isText()) {
			conversion = "";
		} else {
			conversion = typeData.getConversion();
		}
		return conversion;

		
		
	}

	@Override
	protected void setValue(Object element, Object value) {
		String conversion = ((String) value).trim();
		TypeData typeData = (TypeData) element;
		
		typeData.setConversion(conversion);
		
		 tableViewer.refresh();

	}




}
