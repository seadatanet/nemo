package fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import fr.ifremer.nemo.domain.NetCDFLongNameParameterManager;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.Global;


public class LongNameEditingSupport extends EditingSupport {

	private final TableViewer viewer;
	private final CellEditor editor;

	public LongNameEditingSupport(final TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new TextCellEditor(viewer.getTable());
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
		return RulesManager.getInstance().isConversionToNetCDF();
	}

	@Override
	protected Object getValue(Object element) {
		TypeData typeData = (TypeData) element;
		
		String longName = "";
		if (RulesManager.getInstance().isConversionToNetCDF()) {
			String userLongName = NetCDFLongNameParameterManager.getInstance()
					.getUserLongName(typeData.getEntryKeyP011());
			if (userLongName != null) {
				longName = userLongName;
			}
		}
		return longName;
	
	
	}

	@Override
	protected void setValue(Object element, Object value) {
		String userLongName = ((String) value).trim();
		TypeData typeData = (TypeData) element;
		
		if (!userLongName.equals(Global.EMPTY_STRING)) {
			NetCDFLongNameParameterManager.getInstance().updateLongName(typeData.getEntryKeyP011(),
					userLongName);
		}
		
		viewer.update(element, null);

	}




}
