package fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit;


import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.edit.ComboViewerFilter;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.ui.edit.data.TypeInstrument;

public class InstrumentCodeEditingSupport extends EditingSupport {

	private static Logger logger = LoggerFactory.getLogger(InstrumentCodeEditingSupport.class);

	private final TableViewer viewer;
	private final CustomComboBoxViewerCellEditor editor;

	public InstrumentCodeEditingSupport(final TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new CustomComboBoxViewerCellEditor(viewer.getTable());
		//		((CCombo)this.editor.getControl()).setEditable(false);
		this.editor.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element != null) {
					TypeInstrument instrument = (TypeInstrument) element;
					return instrument.getKey();
				}
				return "";
			}
		});
		this.editor.setContentProvider(new ArrayContentProvider());
		Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().initTypesL22();
		List<TypeInstrument> instrumentList = Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getTypesL22();


		this.editor.setInput(instrumentList);
		this.editor.getViewer().insert(null, 0);
		// Add filter
		this.editor.getViewer().addFilter(new ComboViewerFilter(this.editor.getViewer(), ComboViewerFilter.MatchTypeEnum.BEGINS_WITH));

		this.editor.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				TypeInstrument instrument = (TypeInstrument) selection.getFirstElement();
				if (null != instrument) {
					IStructuredSelection selectedType = (IStructuredSelection) viewer.getSelection();
					TypeData typeData = (TypeData) selectedType.getFirstElement();
					if (null != typeData && instrument.getKey() != typeData.getInstrument()) {
						setValue(typeData, instrument);
					}
				}
			}
		});
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		TypeData typeData = (TypeData) element;
		return Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getInstrumentByCode(typeData.getInstrumentCode());
	}


	@Override
	protected void setValue(Object element, Object value) {
		TypeData typeData = (TypeData) element;
		TypeInstrument instrument = (TypeInstrument) value;
		// only set new value if it differs from old one
		if (null != instrument) {
			if (!instrument.getKey().equals(typeData.getInstrumentCode())) {
				typeData.setInstrument(instrument.getTerm());
				typeData.setInstrumentCode(instrument.getKey());
				viewer.update(element, null);


			}
		} else {
			typeData.setInstrument(null);
			typeData.setInstrumentCode(null);
			viewer.update(element, null);
		}
	}

}
