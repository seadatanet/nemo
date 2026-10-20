//package fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit;
//
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import javax.annotation.PostConstruct;
//import javax.inject.Inject;
//
//import org.apache.poi.ss.util.ImageUtils;
//import org.eclipse.e4.core.contexts.ContextInjectionFactory;
//import org.eclipse.e4.core.contexts.IEclipseContext;
//import org.eclipse.e4.core.services.events.IEventBroker;
//import org.eclipse.jface.dialogs.MessageDialog;
//import org.eclipse.jface.layout.TableColumnLayout;
//import org.eclipse.jface.viewers.ArrayContentProvider;
//import org.eclipse.jface.viewers.ColumnLabelProvider;
//import org.eclipse.jface.viewers.ColumnViewerEditor;
//import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
//import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
//import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
//import org.eclipse.jface.viewers.ColumnWeightData;
//import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
//import org.eclipse.jface.viewers.TableViewer;
//import org.eclipse.jface.viewers.TableViewerColumn;
//import org.eclipse.jface.viewers.TableViewerEditor;
//import org.eclipse.jface.viewers.TableViewerFocusCellManager;
//import org.eclipse.jface.viewers.Viewer;
//import org.eclipse.jface.viewers.ViewerCell;
//import org.eclipse.jface.viewers.ViewerComparator;
//import org.eclipse.jface.viewers.ViewerFilter;
//import org.eclipse.jface.window.ToolTip;
//import org.eclipse.jface.window.Window;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.TableEditor;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Table;
//import org.eclipse.swt.widgets.TableColumn;
//import org.eclipse.swt.widgets.TableItem;
//import org.eclipse.ui.part.EditorPart;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.event.EventConstants;
//
//import fr.ifremer.bgmb.lithotheca.utils.rcp.MessagesUtils;
//import fr.ifremer.bgmb.lithotheca.views.sample.edit.DepthEndEditingSupport;
//import fr.ifremer.bgmb.lithotheca.views.sample.edit.DepthStartEditingSupport;
//import fr.ifremer.bgmb.lithotheca.views.sample.edit.InitialWeightEditingSupport;
//import fr.ifremer.bgmb.lithotheca.views.sample.edit.MarkEditingSupport;
//import fr.ifremer.bgmb.lithotheca.views.sample.edit.RemainingWeightEditingSupport;
//import fr.ifremer.bgmb.lithotheca.views.sample.edit.SampleChiefEditingSupport;
//import fr.ifremer.bgmb.lithotheca.views.sample.edit.SampleMethodEditingSupport;
//import fr.ifremer.bgmb.lithotheca.views.sample.edit.SampleNameEditingSupport;
//import fr.ifremer.bgmb.lithotheca.views.sample.edit.SampleThemeEditingSupport;
//import fr.ifremer.bgmb.lithotheca.views.sample.edit.SampleTypeEditingSupport;
//import fr.ifremer.bgmb.lithotheca.views.sample.edit.SectionNumberEditingSupport;
//import fr.ifremer.bgmb.lithotheca.views.sample.edit.VolumeEditingSupport;
//import fr.ifremer.oceanographicCampaign.model.Sample;
//import fr.ifremer.oceanographicCampaign.model.Specimen;
//
//public class OperationSampleViewer extends Composite {
//
//	private static Logger logger = LoggerFactory.getLogger(OperationSampleViewer.class);
//
//	@Inject
//	private IEventBroker eventBroker;
//
//	@Inject
//	private IEclipseContext context;
//
//	private final EditorPart editorView;
//
//	private Map<Sample, Button> deleteButtons;
//	private Map<Sample, Button> editButtons;
//
//	private static Image deleteImage;
//	private static Image inputImage;
//
//	private TableViewer tableViewer;
//	private TableColumnLayout tableColumnLayout;
//	private TableViewerColumn initialWeightColumn;
//	private TableViewerColumn remainingWeightColumn;
//	private TableViewerColumn markColumn;
//	private TableViewerColumn volumeColumn;
//	private TableViewerColumn sectionNumberColumn;
//	private TableViewerColumn depthStartColumn;
//	private TableViewerColumn depthEndColumn;
//
//	private static Operation currentOperation;
//
//	/**
//	 * Custom widget that allow the user to enter geographic coordinates for an
//	 * operation.
//	 * <p>
//	 * 
//	 * @param editorView
//	 * @param parent
//	 *            a composite control which will be the parent of the new
//	 *            instance (cannot be null)
//	 * @param style
//	 *            the style of control to construct
//	 */
//	public OperationSampleViewer(EditorPart editorView, Composite parent, int style) {
//		super(parent, style);
//		this.editorView = editorView;
//	}
//
//	@PostConstruct
//	private void createContents() {
//		deleteButtons = new HashMap<Sample, Button>();
//		editButtons = new HashMap<Sample, Button>();
//
//		deleteImage = ImageUtils.createImage("icons/16x16/delete.png", this.getClass());
//		inputImage = ImageUtils.createImage("icons/16x16/input.png", this.getClass());
//
//		createTableViewer();
//
//	}
//
//	private void createTableViewer() {
//		Composite tableComposite = new Composite(this, SWT.NONE);
//		tableColumnLayout = new TableColumnLayout();
//		tableComposite.setLayout(tableColumnLayout);
//		tableViewer = new TableViewer(tableComposite,
//				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
//		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
//
//		tableComposite.setLayoutData(gridData);
//		createColumns();
//
//		// sort by refernce
//		tableViewer.getTable().setSortDirection(SWT.UP);
//		tableViewer.setComparator(new ViewerComparator() {
//			@Override
//			public int compare(Viewer viewer, Object e1, Object e2) {
//				Sample s1 = (Sample) e1;
//				Sample s2 = (Sample) e2;
//				Table table = tableViewer.getTable();
//				int result = 0;
//				result = s1.getAuthorReference().compareTo(s2.getAuthorReference());
//				return table.getSortDirection() == SWT.UP ? result : -result;
//
//			}
//		});
//
//		final Table table = tableViewer.getTable();
//		table.setHeaderVisible(true);
//		table.setLinesVisible(true);
//
//		// activate the tooltip support for the viewer
//		ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.RECREATE);
//
//		tableViewer.setUseHashlookup(true);
//		tableViewer.setContentProvider(new ArrayContentProvider());
//		if (null != currentOperation) {
//			// Create sample list
//			// FIXME add subsample
//			List<Sample> sampleList = new ArrayList<Sample>();
//			for (Point point : currentOperation.getPointList()) {
//				for (Specimen specimen : point.getSpecimenList()) {
//					sampleList.addAll(specimen.getSampleList());
//				}
//			}
//			tableViewer.setInput(sampleList);
//		} else {
//			tableViewer.setInput(new ArrayList<Sample>());
//		}
//
//		// Allow to hide points to delete in database
//		tableViewer.addFilter(new ViewerFilter() {
//			@Override
//			public boolean select(Viewer viewer, Object parentElement, Object element) {
//				boolean match = true;
//				if (element instanceof Sample) {
//					final Sample subsample = (Sample) element;
//					return !subsample.isToDelete();
//				} else {
//					match = false;
//				}
//				return match;
//			}
//		});
//
//		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(tableViewer,
//				new FocusCellOwnerDrawHighlighter(tableViewer));
//		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(tableViewer) {
//			@Override
//			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
//				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
//						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
//						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
//						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.F2)
//						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED
//								&& Character.isSpaceChar(event.character))
//						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode > 31
//								&& event.keyCode < 127)
//						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED
//								&& event.keyCode == 16777296)
//						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
//			}
//		};
//		TableViewerEditor.create(tableViewer, focusCellManager, actSupport,
//				ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
//						| ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);
//
//		// define layout for the viewer
//		gridData = new GridData();
//		gridData.verticalAlignment = GridData.FILL;
//		gridData.horizontalSpan = 2;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
//		gridData.horizontalAlignment = GridData.FILL;
//		tableViewer.getControl().setLayoutData(gridData);
//	}
//
//	public TableViewer getViewer() {
//		return tableViewer;
//	}
//
//	// create the columns for the table
//	private void createColumns() {
//		String[] titles = { "", MessagesUtils.editorview_subsample_name_columntitle,
//				MessagesUtils.editorview_subsample_type_columntitle,
//				MessagesUtils.editorview_subsample_theme_columntitle,
//				MessagesUtils.editorview_subsample_method_columntitle,
//				MessagesUtils.editorview_subsample_chief_columntitle,
//				MessagesUtils.editorview_subsample_initialweight_columntitle,
//				MessagesUtils.editorview_subsample_remainingweight_columntitle,
//				MessagesUtils.editorview_subsample_mark_columntitle,
//				MessagesUtils.editorview_subsample_volume_columntitle,
//				MessagesUtils.editorview_subsample_sectionnumber_columntitle,
//				MessagesUtils.editorview_subsample_depthbegin_columntitle,
//				MessagesUtils.editorview_subsample_depthend_columntitle, "", "" };
//		int[] bounds = { 0, 150, 130, 80, 120, 100, 30, 30, 30, 30, 30, 30, 30, 22, 22 };
//
//		// First column is an empty one to be able to right align point n�
//		// column because due to
//		// a restriction on some platforms, the first column is always left
//		// aligned
//		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], SWT.LEFT, 0);
//		col.setLabelProvider(new ColumnLabelProvider());
//		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, bounds[0], false));
//		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=236274
//		// Setting non-resizable ColumnLayoutData for TableViewer does not work
//		// Patch to make columns not resizable
//		col.getColumn().setResizable(false);
//
//		// AuthorReference
//		col = createTableViewerColumn(titles[1], bounds[1], SWT.LEFT, 1);
//		col.setLabelProvider(new ValidationCellLabelProvider() {
//			@Override
//			public void update(ViewerCell cell) {
//				super.update(cell);
//				if (null != cell.getElement() && cell.getElement() instanceof Sample) {
//					Sample sample = (Sample) cell.getElement();
//					if (null != sample.getAuthorReference()) {
//						cell.setText(sample.getAuthorReference());
//					} else {
//						cell.setText("");
//					}
//				} else {
//					cell.setText("");
//				}
//			}
//
//			@Override
//			public boolean useNativeToolTip(Object object) {
//				return true;
//			}
//
//			@Override
//			public String getToolTipText(Object element) {
//				Integer sampleCode = ((Sample) element).getSampleCode();
//
//				String valueStr = null;
//				if (sampleCode != null) {
//					valueStr = sampleCode.toString();
//				} else {
//					valueStr = "UNKOWN";
//				}
//				return "ID ECN_CECN = " + valueStr;
//			}
//		});
//		col.setEditingSupport(new SampleNameEditingSupport(editorView, tableViewer));
//		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, bounds[1], false));
//		col.getColumn().setResizable(false);
//
//		// Type
//		col = createTableViewerColumn(titles[2], bounds[2], SWT.LEFT, 2);
//		col.setLabelProvider(new ValidationCellLabelProvider() {
//			@Override
//			public void update(ViewerCell cell) {
//				super.update(cell);
//				if (null != cell.getElement() && cell.getElement() instanceof Sample) {
//					Sample sample = (Sample) cell.getElement();
//					if (null != sample.getType()) {
//						cell.setText(sample.getType().getLibelleFR());
//					} else {
//						cell.setText("");
//					}
//				} else {
//					cell.setText("");
//				}
//			}
//		});
//		SampleTypeEditingSupport stes = new SampleTypeEditingSupport(editorView, tableViewer);
//		ContextInjectionFactory.inject(stes, context);
//		col.setEditingSupport(stes);
//		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, bounds[2], false));
//		col.getColumn().setResizable(false);
//
//		// Theme
//		col = createTableViewerColumn(titles[3], bounds[3], SWT.LEFT, 3);
//		col.setLabelProvider(new ValidationCellLabelProvider() {
//			@Override
//			public void update(ViewerCell cell) {
//				super.update(cell);
//				if (null != cell.getElement() && cell.getElement() instanceof Sample) {
//					Sample sample = (Sample) cell.getElement();
//					if (null != sample.getTheme()) {
//						cell.setText(sample.getTheme().getLibelleFR());
//					} else {
//						cell.setText("");
//					}
//				} else {
//					cell.setText("");
//				}
//			}
//		});
//		col.setEditingSupport(new SampleThemeEditingSupport(editorView, tableViewer));
//		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, bounds[3], false));
//		col.getColumn().setResizable(false);
//
//		// Methode
//		col = createTableViewerColumn(titles[4], bounds[4], SWT.LEFT, 4);
//		col.setLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public void update(ViewerCell cell) {
//				if (null != cell.getElement() && cell.getElement() instanceof Sample) {
//					Sample sample = (Sample) cell.getElement();
//					if (null != sample.getSamplingMethod()) {
//						cell.setText(sample.getSamplingMethod().getLibelleFR());
//					} else {
//						cell.setText("");
//					}
//				} else {
//					cell.setText("");
//				}
//			}
//		});
//		col.setEditingSupport(new SampleMethodEditingSupport(editorView, tableViewer));
//		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, bounds[4], false));
//		col.getColumn().setResizable(false);
//
//		// Chief
//		col = createTableViewerColumn(titles[5], bounds[5], SWT.LEFT, 5);
//		col.setLabelProvider(new ValidationCellLabelProvider() {
//			@Override
//			public void update(ViewerCell cell) {
//				super.update(cell);
//				if (null != cell.getElement() && cell.getElement() instanceof Sample) {
//					Sample sample = (Sample) cell.getElement();
//					if (null != sample.getChief()) {
//						cell.setText(sample.getChief().getChiefName());
//					} else {
//						cell.setText("");
//					}
//				} else {
//					cell.setText("");
//				}
//			}
//		});
//		col.setEditingSupport(new SampleChiefEditingSupport(editorView, tableViewer));
//		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(15, true));
//
//		// Initial weight
//		initialWeightColumn = createTableViewerColumn(titles[6], bounds[6], SWT.RIGHT | SWT.WRAP | SWT.MULTI, 6);
//		initialWeightColumn.setLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public String getText(Object element) {
//				Sample subsample = (Sample) element;
//				if (null != subsample.getRock() && null != subsample.getRock().getInitialWeight()) {
//					return subsample.getRock().getInitialWeight().toString();
//				} else {
//					return "";
//				}
//			}
//		});
//		initialWeightColumn.setEditingSupport(new InitialWeightEditingSupport(editorView, tableViewer));
//		tableColumnLayout.setColumnData(initialWeightColumn.getColumn(), new ColumnWeightData(20, true));
//
//		// Remaining Weight
//		remainingWeightColumn = createTableViewerColumn(titles[7], bounds[7], SWT.RIGHT | SWT.WRAP | SWT.MULTI, 7);
//		remainingWeightColumn.setLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public String getText(Object element) {
//				Sample subsample = (Sample) element;
//				if (null != subsample.getRock() && null != subsample.getRock().getRemainingWeight()) {
//					return subsample.getRock().getRemainingWeight().toString();
//				} else {
//					return "";
//				}
//			}
//		});
//		remainingWeightColumn.setEditingSupport(new RemainingWeightEditingSupport(editorView, tableViewer));
//		tableColumnLayout.setColumnData(remainingWeightColumn.getColumn(), new ColumnWeightData(20, true));
//
//		// Mark
//		markColumn = createTableViewerColumn(titles[8], bounds[8], SWT.RIGHT | SWT.WRAP | SWT.MULTI, 8);
//		markColumn.setLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public String getText(Object element) {
//				Sample subsample = (Sample) element;
//				if (null != subsample.getFluid() && null != subsample.getFluid().getMark()) {
//					return subsample.getFluid().getMark().toString();
//				} else {
//					return "";
//				}
//			}
//		});
//		markColumn.setEditingSupport(new MarkEditingSupport(editorView, tableViewer));
//		tableColumnLayout.setColumnData(markColumn.getColumn(), new ColumnWeightData(20, true));
//
//		// Volume
//		volumeColumn = createTableViewerColumn(titles[9], bounds[9], SWT.RIGHT | SWT.WRAP | SWT.MULTI, 9);
//		volumeColumn.setLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public String getText(Object element) {
//				Sample subsample = (Sample) element;
//				if (null != subsample.getFluid() && null != subsample.getFluid().getVolume()) {
//					return subsample.getFluid().getVolume().toString();
//				} else {
//					return "";
//				}
//			}
//		});
//		volumeColumn.setEditingSupport(new VolumeEditingSupport(editorView, tableViewer));
//		tableColumnLayout.setColumnData(volumeColumn.getColumn(), new ColumnWeightData(20, true));
//
//		// Section Number
//		sectionNumberColumn = createTableViewerColumn(titles[10], bounds[10], SWT.RIGHT | SWT.WRAP | SWT.MULTI, 10);
//		sectionNumberColumn.setLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public String getText(Object element) {
//				Sample subsample = (Sample) element;
//				if (null != subsample.getCore() && null != subsample.getCore().getSectionNumber()) {
//					return subsample.getCore().getSectionNumber().toString();
//				} else {
//					return "";
//				}
//			}
//		});
//		sectionNumberColumn.setEditingSupport(new SectionNumberEditingSupport(editorView, tableViewer));
//		tableColumnLayout.setColumnData(sectionNumberColumn.getColumn(),
//				new ColumnWeightData(sectionNumberColumn.getColumn().getWidth(), true));
//
//		// Start End
//		depthStartColumn = createTableViewerColumn(titles[11], bounds[11], SWT.RIGHT | SWT.WRAP | SWT.MULTI, 11);
//		depthStartColumn.setLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public String getText(Object element) {
//				Sample subsample = (Sample) element;
//				if (null != subsample.getCore() && null != subsample.getCore().getDepthStart()) {
//					return subsample.getCore().getDepthStart().toString();
//				} else {
//					return "";
//				}
//			}
//		});
//		depthStartColumn.setEditingSupport(new DepthStartEditingSupport(editorView, tableViewer));
//		tableColumnLayout.setColumnData(depthStartColumn.getColumn(),
//				new ColumnWeightData(depthStartColumn.getColumn().getWidth(), true));
//
//		// Depth End
//		depthEndColumn = createTableViewerColumn(titles[12], bounds[12], SWT.RIGHT | SWT.WRAP | SWT.MULTI, 12);
//		depthEndColumn.setLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public String getText(Object element) {
//				Sample subsample = (Sample) element;
//				if (null != subsample.getCore() && null != subsample.getCore().getDepthEnd()) {
//					return subsample.getCore().getDepthEnd().toString();
//				} else {
//					return "";
//				}
//			}
//		});
//		depthEndColumn.setEditingSupport(new DepthEndEditingSupport(editorView, tableViewer));
//		tableColumnLayout.setColumnData(depthEndColumn.getColumn(),
//				new ColumnWeightData(depthEndColumn.getColumn().getWidth(), true));
//
//		// Edit Button
//		col = createTableViewerColumn(titles[13], bounds[13], SWT.CENTER, 13);
//		col.setLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public void update(ViewerCell cell) {
//				TableItem item = (TableItem) cell.getItem();
//				Button button;
//				final Sample subsample = (Sample) cell.getElement();
//				if (editButtons.containsKey(cell.getElement()) && !editButtons.get(cell.getElement()).isDisposed()) {
//					button = editButtons.get(cell.getElement());
//				} else {
//					button = new Button((Composite) cell.getViewerRow().getControl(), SWT.NONE);
//					button.setImage(inputImage);
//					// button.setEnabled(editorView.isEnabled());
//					button.addSelectionListener(new SelectionAdapter() {
//						@Override
//						public void widgetSelected(SelectionEvent e) {
//							if (editorView.isDirty()) {
//								MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
//										MessagesUtils.editorview_subsample_fathersamplenotsavederror_title,
//										MessagesUtils.editorview_subsample_fathersamplenotsavederror_message);
//							} else {
//								eventBroker.send(EventConstants.SELECT_SUBSAMPLE_IN_VIEWER, subsample);
//								editorView.setSelection(subsample);
//							}
//							refreshTable();
//						}
//					});
//					editButtons.put((Sample) cell.getElement(), button);
//				}
//				TableEditor editor = new TableEditor(item.getParent());
//				editor.grabHorizontal = true;
//				editor.grabVertical = true;
//				editor.setEditor(button, item, cell.getColumnIndex());
//				editor.layout();
//			}
//		});
//		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, bounds[13], false));
//		col.getColumn().setResizable(false);
//
//		// Delete Button Column
//		col = createTableViewerColumn(titles[14], bounds[14], SWT.CENTER, 14);
//		col.setLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public void update(ViewerCell cell) {
//				TableItem item = (TableItem) cell.getItem();
//				Button button;
//				if (deleteButtons.containsKey(cell.getElement())
//						&& !deleteButtons.get(cell.getElement()).isDisposed()) {
//					button = deleteButtons.get(cell.getElement());
//				} else {
//					button = new Button((Composite) cell.getViewerRow().getControl(), SWT.NONE);
//					button.setImage(deleteImage);
//					button.setEnabled(editorView.isEnabled());
//					button.addSelectionListener(new SelectionAdapter() {
//						@Override
//						public void widgetSelected(SelectionEvent e) {
//							MessageDialog dialog = new MessageDialog(getShell(),
//									MessagesUtils.editorview_subsample_simpledeletedialog_title, null,
//									MessagesUtils.editorview_subsample_simpledeletedialog_message,
//									MessageDialog.CONFIRM, new String[] { MessagesUtils.genericdialog_yesbutton_label,
//											MessagesUtils.genericdialog_cancelbutton_label },
//									0);
//							dialog.create();
//							if (dialog.open() == Window.OK) {
//								for (Entry<Sample, Button> entry : deleteButtons.entrySet()) {
//									if (e.getSource().equals(entry.getValue())) {
//										Sample sampleToDelete = entry.getKey();
//										if (sampleToDelete != null) {
//											if (null != sampleToDelete.getSampleCode()) {
//												sampleToDelete.setToDelete(true);
//											} else {
//												sampleToDelete.setToAdd(false);
//
//												if (sampleToDelete.isSubsample()) {
//													sampleToDelete.getFatherSample().removeSubsample(sampleToDelete);
//												} else {
//													sampleToDelete.getSpecimen().removeSample(sampleToDelete);
//												}
//											}
//											eventBroker.post(EventConstants.UPDATE_SAMPLE_THEME_STATUT, null);
//											((Button) e.getSource()).dispose();
//											setDirty(true);
//										}
//										Button editButton = editButtons.get(sampleToDelete);
//										if (null != editButton && !editButton.isDisposed()) {
//											editButton.dispose();
//										}
//										Button deleteButton = deleteButtons.get(sampleToDelete);
//										if (null != deleteButton && !deleteButton.isDisposed()) {
//											deleteButton.dispose();
//										}
//										setDirty(true);
//									}
//									refreshTable();
//								}
//							}
//						}
//					});
//					deleteButtons.put((Sample) cell.getElement(), button);
//				}
//				TableEditor editor = new TableEditor(item.getParent());
//				editor.grabHorizontal = true;
//				editor.grabVertical = true;
//				editor.setEditor(button, item, cell.getColumnIndex());
//				editor.layout();
//			}
//		});
//		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, bounds[14], false));
//		col.getColumn().setResizable(false);
//	}
//
//	private TableViewerColumn createTableViewerColumn(String title, int bound, final int style, final int colNumber) {
//		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
//		final TableColumn column = viewerColumn.getColumn();
//		column.setText(title);
//		column.setWidth(bound);
//		column.setAlignment(style);
//		column.setResizable(true);
//		column.setMoveable(true);
//		return viewerColumn;
//	}
//
//	public void updateOperation(Operation operation) {
//		currentOperation = operation;
//
//		if (tableViewer.getTable() != null && tableViewer.getTable().getChildren() != null) {
//			for (Control item : (tableViewer).getTable().getChildren()) {
//
//				if ((item != null) && (item instanceof Button) && (!item.isDisposed())) {
//					item.dispose();
//				}
//			}
//		}
//		if (null != currentOperation) {
//			// Create sample list
//			// FIXME add subsample
//			List<Sample> sampleList = new ArrayList<Sample>();
//			for (Point point : currentOperation.getPointList()) {
//				for (Specimen specimen : point.getSpecimenList()) {
//					sampleList.addAll(specimen.getSampleList());
//				}
//			}
//			tableViewer.setInput(sampleList);
//
//			// Check nature and theme and show/hide Columns
//			showHideColumn(sampleList);
//		} else {
//			tableViewer.setInput(new ArrayList<Sample>());
//		}
//	}
//
//	private void refreshTable() {
//		tableViewer.refresh();
//		Table table = tableViewer.getTable();
//		if (null != table && null != table.getParent()) {
//			table.getParent().layout();
//		}
//	}
//
//	/**
//	 * Set the view dirty.
//	 * 
//	 * @param dirty
//	 *            true if the view is dirty, false otherwise
//	 */
//	private void setDirty(boolean dirty) {
//		editorView.setDirty(dirty);
//	}
//
//	@Override
//	public void setEnabled(boolean enabled) {
//		for (Button button : deleteButtons.values()) {
//			if (!button.isDisposed()) {
//				button.setEnabled(enabled);
//			}
//		}
//	}
//
//	/**
//	 * Check nature and theme and show/hide Columns
//	 * 
//	 * @param sampleList
//	 */
//	public void showHideColumn(List<Sample> sampleList) {
//		// Specimen Nature
//		boolean isSpecimenCarotte = false;
//		boolean isSpecimenAutre = false;
//
//		// Specimen Theme
//		boolean isSampleFluide = false;
//
//		for (Sample sample : sampleList) {
//
//			if ("C".equals(sample.getSpecimen().getNature().getCode())) {
//				isSpecimenCarotte = true;
//			} else if ("A".equals(sample.getSpecimen().getNature().getCode())) {
//				isSpecimenAutre = true;
//			}
//			if (sample.getSpecimen().isFluidSpecimen()) {
//				isSampleFluide = true;
//			}
//
//		}
//
//		if (isSpecimenCarotte && isSpecimenAutre) { // Nature "Carotte" &
//													// "Autre"
//			initialWeightColumn.getColumn().setWidth(0);
//			remainingWeightColumn.getColumn().setWidth(0);
//			depthStartColumn.getColumn().setWidth(0);
//			depthEndColumn.getColumn().setWidth(0);
//			sectionNumberColumn.getColumn().setWidth(0);
//			tableColumnLayout.setColumnData(markColumn.getColumn(), new ColumnWeightData(30, true));
//			tableColumnLayout.setColumnData(volumeColumn.getColumn(), new ColumnWeightData(30, true));
//			initialWeightColumn.getColumn().setResizable(false);
//			remainingWeightColumn.getColumn().setResizable(false);
//			depthStartColumn.getColumn().setResizable(false);
//			depthEndColumn.getColumn().setResizable(false);
//			sectionNumberColumn.getColumn().setResizable(false);
//			markColumn.getColumn().setResizable(true);
//			volumeColumn.getColumn().setResizable(true);
//
//			tableColumnLayout.setColumnData(initialWeightColumn.getColumn(), new ColumnWeightData(30, true));
//			initialWeightColumn.getColumn().setResizable(true);
//			tableColumnLayout.setColumnData(remainingWeightColumn.getColumn(), new ColumnWeightData(30, true));
//			remainingWeightColumn.getColumn().setResizable(true);
//			tableColumnLayout.setColumnData(markColumn.getColumn(), new ColumnWeightData(30, true));
//			markColumn.getColumn().setResizable(true);
//			tableColumnLayout.setColumnData(volumeColumn.getColumn(), new ColumnWeightData(30, true));
//			volumeColumn.getColumn().setResizable(true);
//			tableColumnLayout.setColumnData(sectionNumberColumn.getColumn(), new ColumnWeightData(30, true));
//			sectionNumberColumn.getColumn().setResizable(true);
//			tableColumnLayout.setColumnData(depthStartColumn.getColumn(), new ColumnWeightData(30, true));
//			depthStartColumn.getColumn().setResizable(true);
//			tableColumnLayout.setColumnData(depthEndColumn.getColumn(), new ColumnWeightData(30, true));
//			depthEndColumn.getColumn().setResizable(true);
//
//		} else if (isSpecimenCarotte && !isSpecimenAutre) {
//			// Nature Only "Carotte"
//			initialWeightColumn.getColumn().setWidth(0);
//			initialWeightColumn.getColumn().setResizable(false);
//			remainingWeightColumn.getColumn().setWidth(0);
//			initialWeightColumn.getColumn().setResizable(false);
//
//			if (isSampleFluide) { // Theme Fluide
//				tableColumnLayout.setColumnData(markColumn.getColumn(), new ColumnWeightData(30, true));
//				markColumn.getColumn().setResizable(true);
//				tableColumnLayout.setColumnData(volumeColumn.getColumn(), new ColumnWeightData(30, true));
//				volumeColumn.getColumn().setResizable(true);
//			} else {
//				markColumn.getColumn().setWidth(0);
//				markColumn.getColumn().setResizable(false);
//				volumeColumn.getColumn().setWidth(0);
//				volumeColumn.getColumn().setResizable(false);
//			}
//
//			tableColumnLayout.setColumnData(sectionNumberColumn.getColumn(), new ColumnWeightData(30, true));
//			sectionNumberColumn.getColumn().setResizable(true);
//			tableColumnLayout.setColumnData(depthStartColumn.getColumn(), new ColumnWeightData(30, true));
//			depthStartColumn.getColumn().setResizable(true);
//			tableColumnLayout.setColumnData(depthEndColumn.getColumn(), new ColumnWeightData(30, true));
//			depthEndColumn.getColumn().setResizable(true);
//
//		} else if (!isSpecimenCarotte && isSpecimenAutre) {
//			// Nature Only "Autre"
//			tableColumnLayout.setColumnData(initialWeightColumn.getColumn(), new ColumnWeightData(30, true));
//			initialWeightColumn.getColumn().setResizable(true);
//			tableColumnLayout.setColumnData(remainingWeightColumn.getColumn(), new ColumnWeightData(30, true));
//			remainingWeightColumn.getColumn().setResizable(true);
//			markColumn.getColumn().setWidth(0);
//			markColumn.getColumn().setResizable(false);
//
//			if (isSampleFluide) { // Theme Fluide
//				tableColumnLayout.setColumnData(volumeColumn.getColumn(), new ColumnWeightData(30, true));
//				volumeColumn.getColumn().setResizable(true);
//			} else {
//				volumeColumn.getColumn().setWidth(0);
//				volumeColumn.getColumn().setResizable(false);
//			}
//			sectionNumberColumn.getColumn().setWidth(0);
//			sectionNumberColumn.getColumn().setResizable(false);
//			depthStartColumn.getColumn().setWidth(0);
//			depthStartColumn.getColumn().setResizable(false);
//			depthEndColumn.getColumn().setWidth(0);
//			depthEndColumn.getColumn().setResizable(false);
//
//		}
//		refreshTable();
//	}
//
//}
