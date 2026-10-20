package fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.Messages;

public class TextEditingSupport extends EditingSupport {

	private static Logger logger = LoggerFactory.getLogger(TextEditingSupport.class);

	private final TableViewer tableViewer;
	private final CheckboxCellEditor editor;

	public TextEditingSupport(final TableViewer viewer) {
		super(viewer);
		  this.tableViewer = viewer;
//		this.editor = new CheckboxCellEditor(viewer.getTable(), SWT.CHECK);
		  this.editor = new CheckboxCellEditor(null, SWT.CHECK);
//		CheckboxCellEditor(null, SWT.CHECK
		
//		this.editor.
////		this.editor.setLabelProvider(new LabelProvider());
////		this.editor.setContentProvider(new ArrayContentProvider());
//		try {
//			List<Codification> subsampleTypeList = new ArrayList<Codification>();
//			for (Codification type : CodificationProvider.INSTANCE.getSampleTypeList()) {
//				if (type.getCode().startsWith("SS")) {
//					subsampleTypeList.add(type);
//				}
//			}
//
////			this.editor.setInput(subsampleTypeList);
////			this.editor.getViewer().insert(null, 0);
//		} catch (SQLException sqle) {
//			logger.info(sqle.getMessage());
//			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Erreur",
//					"Probl\u00e8me lors de la r\u00e9cup\u00e9ration de la liste des types d'\u00e9chantillon \n "
//							+ sqle.getMessage());
//		}
//		// Add filter
//		this.editor.getViewer().addFilter(new ComboViewerFilter(this.editor.getViewer()));

		// updateTypeList((Sample) this.editor.getValue());

//		this.editor.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
//				Codification codification = (Codification) selection.getFirstElement();
//				if (null != codification) {
//					IStructuredSelection selectedSample = (IStructuredSelection) viewer.getSelection();
//					Sample sample = (Sample) selectedSample.getFirstElement();
//					if (null != sample && codification != sample.getType()) {
//						setValue(sample, codification);
//					}
//				}
//			}
//		});
	}

	

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
//		return new CheckboxCellEditor(null, SWT.CHECK);
	}

	@Override
	protected boolean canEdit(Object element) {
		return !(RulesManager.getInstance().isConversionToMedatlas()
				|| RulesManager.getInstance().isConversionToNetCDF());
	}

	@Override
	protected Object getValue(Object element) {
		TypeData typeData = (TypeData) element;
		return typeData.isText();
	}

	@Override
	protected void setValue(Object element, Object value) {
		TypeData typeData = (TypeData) element;
		typeData.setIsText((boolean) value);
		
		if ((boolean) value) {
			Utils.messageOK(Messages.edit_data_parameterIsText);
		} else {
			Utils.messageOK(Messages.edit_data_parameterIsNumeric);
		
			typeData.setConversion(TypeData.DEFAULT_CONVERT);
		}
		
		
		 tableViewer.refresh();
	}
	
}
