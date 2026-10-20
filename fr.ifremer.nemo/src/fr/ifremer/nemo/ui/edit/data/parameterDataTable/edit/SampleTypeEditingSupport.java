//package fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit;
//
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.eclipse.jface.dialogs.MessageDialog;
//import org.eclipse.jface.viewers.ArrayContentProvider;
//import org.eclipse.jface.viewers.CellEditor;
//import org.eclipse.jface.viewers.EditingSupport;
//import org.eclipse.jface.viewers.ISelectionChangedListener;
//import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.jface.viewers.LabelProvider;
//import org.eclipse.jface.viewers.SelectionChangedEvent;
//import org.eclipse.jface.viewers.TableViewer;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.ui.part.EditorPart;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import fr.ifremer.bgmb.lithotheca.utils.listener.ComboViewerFilter;
//import fr.ifremer.bgmb.lithotheca.views.viewers.ValidationComboBoxViewerCellEditor;
//import fr.ifremer.oceanographicCampaign.model.Codification;
//import fr.ifremer.oceanographicCampaign.model.Sample;
//import fr.ifremer.oceanographicCampaign.model.utils.ConfigurationUtils;
//import fr.ifremer.oceanographicCampaign.provider.CodificationProvider;
//
//public class SampleTypeEditingSupport extends EditingSupport {
//
//	private static Logger logger = LoggerFactory.getLogger(SampleTypeEditingSupport.class);
//
//	private final EditorPart editorView;
//	private final TableViewer viewer;
//	private final ValidationComboBoxViewerCellEditor editor;
//
//	public SampleTypeEditingSupport(EditorPart editorView, final TableViewer viewer) {
//		super(viewer);
//		this.editorView = editorView;
//		this.viewer = viewer;
//		this.editor = new ValidationComboBoxViewerCellEditor(viewer.getTable());
//		this.editor.setLabelProvider(new LabelProvider());
//		this.editor.setContentProvider(new ArrayContentProvider());
//		try {
//			List<Codification> subsampleTypeList = new ArrayList<Codification>();
//			for (Codification type : CodificationProvider.INSTANCE.getSampleTypeList()) {
//				if (type.getCode().startsWith("SS")) {
//					subsampleTypeList.add(type);
//				}
//			}
//
//			this.editor.setInput(subsampleTypeList);
//			this.editor.getViewer().insert(null, 0);
//		} catch (SQLException sqle) {
//			logger.info(sqle.getMessage());
//			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Erreur",
//					"Probl\u00e8me lors de la r\u00e9cup\u00e9ration de la liste des types d'\u00e9chantillon \n "
//							+ sqle.getMessage());
//		}
//		// Add filter
//		this.editor.getViewer().addFilter(new ComboViewerFilter(this.editor.getViewer()));
//
//		// updateTypeList((Sample) this.editor.getValue());
//
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
//	}
//
//	private void updateTypeList(Sample sample) {
//		try {
//			if (null != sample) {
//				List<Codification> subsampleTypeList = new ArrayList<Codification>();
//				for (Codification type : CodificationProvider.INSTANCE.getSampleTypeList()) {
//					boolean codificationOk = false;
//					if (null != sample.getTheme()) {
//						if ("G".equals(sample.getTheme().getCode())) {
//							List<String> sampleTypeFilters = ConfigurationUtils.getConfigurationUtils()
//									.getList("sample.type.geologicaltheme.filter");
//							if (sampleTypeFilters.contains(type.getCode())) {
//								codificationOk = true;
//							}
//						} else if ("F".equals(sample.getTheme().getCode())) {
//							List<String> sampleTypeFilters = ConfigurationUtils.getConfigurationUtils()
//									.getList("sample.type.fluidtheme.filter");
//							if (sampleTypeFilters.contains(type.getCode())) {
//								codificationOk = true;
//							}
//						} else if ("B".equals(sample.getTheme().getCode())) {
//							List<String> sampleTypeFilters = ConfigurationUtils.getConfigurationUtils()
//									.getList("sample.type.biologicaltheme.filter");
//							if (sampleTypeFilters.contains(type.getCode())) {
//								codificationOk = true;
//							}
//						}
//					}
//					if (codificationOk) {
//						subsampleTypeList.add(type);
//					}
//				}
//				this.editor.setInput(subsampleTypeList);
//				if (!subsampleTypeList.isEmpty()) {
//					// Remove filter before insert null to avoir error
//					ComboViewerFilter filter = (ComboViewerFilter) this.editor.getViewer().getFilters()[0];
//					this.editor.getViewer().removeFilter(filter);
//					this.editor.getViewer().insert(null, 0);
//					this.editor.getViewer().addFilter(filter);
//					filter.setSearchText("");
//				}
//			} else {
//				this.editor.setInput(new ArrayList<Codification>());
//			}
//		} catch (SQLException sqle) {
//			logger.info(sqle.getMessage());
//			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Erreur",
//					"Probl\u00e8me lors de la r\u00e9cup\u00e9ration de la liste des types d'\u00e9chantillon \n "
//							+ sqle.getMessage());
//		}
//	}
//	// @Inject
//	// @Optional
//	// private void
//	// updateEditor(@UIEventTopic(EventConstants.UPDATE_SAMPLE_TYPE_EDITOR)
//	// Sample sample) {
//	// try {
//	// if (null != sample) {
//	// List<Codification> subsampleTypeList = new ArrayList<Codification>();
//	// for (Codification type :
//	// CodificationProvider.INSTANCE.getSampleTypeList()) {
//	// boolean codificationOk = false;
//	// if (null != sample.getTheme()) {
//	// if ("G".equals(sample.getTheme().getCode())) {
//	// List<String> sampleTypeFilters =
//	// ConfigurationUtils.getConfigurationUtils()
//	// .getList("sample.type.geologicaltheme.filter");
//	// if (sampleTypeFilters.contains(type.getCode())) {
//	// codificationOk = true;
//	// }
//	// } else if ("F".equals(sample.getTheme().getCode())) {
//	// List<String> sampleTypeFilters =
//	// ConfigurationUtils.getConfigurationUtils()
//	// .getList("sample.type.fluidtheme.filter");
//	// if (sampleTypeFilters.contains(type.getCode())) {
//	// codificationOk = true;
//	// }
//	// } else if ("B".equals(sample.getTheme().getCode())) {
//	// List<String> sampleTypeFilters =
//	// ConfigurationUtils.getConfigurationUtils()
//	// .getList("sample.type.biologicaltheme.filter");
//	// if (sampleTypeFilters.contains(type.getCode())) {
//	// codificationOk = true;
//	// }
//	// }
//	// }
//	// if (codificationOk) {
//	// subsampleTypeList.add(type);
//	// }
//	// }
//	// this.editor.setInput(subsampleTypeList);
//	// if (!subsampleTypeList.isEmpty()) {
//	// // Remove filter before insert null to avoir error
//	// ComboViewerFilter filter = (ComboViewerFilter)
//	// this.editor.getViewer().getFilters()[0];
//	// this.editor.getViewer().removeFilter(filter);
//	// this.editor.getViewer().insert(null, 0);
//	// this.editor.getViewer().addFilter(filter);
//	// filter.setSearchText("");
//	// }
//	// } else {
//	// this.editor.setInput(new ArrayList<Codification>());
//	// }
//	// } catch (SQLException sqle) {
//	// logger.info(sqle.getMessage());
//	// MessageDialog.openError(Display.getCurrent().getActiveShell(), "Erreur",
//	// "Probl\u00e8me lors de la r\u00e9cup\u00e9ration de la liste des types
//	// d'\u00e9chantillon \n "
//	// + sqle.getMessage());
//	// }
//	// }
//
//	@Override
//	protected CellEditor getCellEditor(Object element) {
//		return editor;
//	}
//
//	@Override
//	protected boolean canEdit(Object element) {
//		return editorView.isEnabled();
//	}
//
//	@Override
//	protected Object getValue(Object element) {
//		updateTypeList((Sample) element);
//		return ((Sample) element).getType();
//	}
//
//	@Override
//	protected void setValue(Object element, Object value) {
//		Sample sample = (Sample) element;
//		Codification type = (Codification) value;
//		// only set new value if it differs from old one
//		if (null != type) {
//			if (!type.equals(sample.getType())) {
//				sample.setType(type);
//				viewer.update(element, null);
//
//				updateTypeList(sample);
//			}
//		} else {
//			sample.setType(null);
//			viewer.update(element, null);
//		}
//	}
//
//}
