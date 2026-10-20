package fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import fr.ifremer.nemo.domain.LabelParameterManager;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.Global;


public class UserLabelEditingSupport extends EditingSupport {

	private final TableViewer viewer;
	private final CellEditor editor;

	public UserLabelEditingSupport(final TableViewer viewer) {
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
		return RulesManager.getInstance().isConversionToODV() && !RulesManager.getInstance().isDataListTypeP091();
	}

	@Override
	protected Object getValue(Object element) {
		TypeData typeData = (TypeData) element;
		String label = "";
		if (typeData.isP011()) {
			if (RulesManager.getInstance().isConversionToODV()) {
				String userLabel = LabelParameterManager.getInstance().getUserLabel(typeData.getEntryKeyP011());
				if (userLabel != null) {
					label = userLabel;
				}
				if (label.isEmpty()) {
					String specificLabel = LabelParameterManager.getInstance()
							.getSpecificParameterLabel(typeData.getEntryKeyP011());
					if (specificLabel != null) {
						label = specificLabel;
					}
				}
			}
		}
		return label;
	}

	@Override
	protected void setValue(Object element, Object value) {
		String userLabel = ((String) value).trim();
		TypeData typeData = (TypeData) element;

		if (!userLabel.equals(Global.EMPTY_STRING)) {
			LabelParameterManager.getInstance().updateLabel(typeData.getEntryKeyP011(), userLabel);
		}

		viewer.update(element, null);
	}




}
