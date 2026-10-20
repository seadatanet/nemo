package fr.ifremer.nemo.ui.edit.cruise;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.EdmerpEntity;
import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.DnDSortingSupport;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.DnDSortingSupport.IModelManipulator;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.ValidationCellLabelProvider;
import fr.ifremer.nemo.ui.tables.WSEdmerpListDialog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * 
 * Description: Table affichée dans l'onglet Cruise, section "Source of data"
 * 
 * @author pmahoudo
 *
 */
public class TableEdmerp {

	public static List<EdmerpEntity> edmerpList;

	private  Composite parentComposite;
	
	
	private Composite tableComposite;
	protected TableViewer tableViewer;
	private TableColumnLayout tableColumnLayout;
	
	

	public  void go(Composite parent) {
		parentComposite = parent;

		Label label = new Label(parentComposite, SWT.NONE);
		Label edmerpLabel = new Label(parentComposite, SWT.NONE);
		edmerpLabel.setText(Messages.edit_cruise_edmerp);
		// initialisation de la ArrayList des codes Edmerp e partir du
		// datamodel
		edmerpList = new ArrayList<EdmerpEntity>();	
		
		createTableViewer();
	}

	private void createTableViewer() {
		tableComposite = new Composite(parentComposite, SWT.NONE);
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		tableViewer = new TableViewer(tableComposite,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.heightHint = 100;

		tableComposite.setLayoutData(gridData);
		createColumns();
		createMenu();

		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// activate the tooltip support for the viewer
		ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.RECREATE);

		tableViewer.setUseHashlookup(true);
		tableViewer.setContentProvider(new ArrayContentProvider());
		
		tableViewer.setInput(edmerpList);



		// Add Drag and Drop support
		TypeModelManipulator manipulator = new TypeModelManipulator();
		DnDSortingSupport.addDnDSortingSupport(tableViewer, manipulator);
	}

	public TableViewer getViewer() {
		return tableViewer;
	}

	// create the columns for the table
	private void createColumns() {

		int i = 0;

		// Code
		i++;
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_code.toUpperCase(), 100, SWT.LEFT, i);
		// columnsMap.get(i).isMandatory()
		col.setLabelProvider(new ValidationCellLabelProvider(true) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof EdmerpEntity) {
					EdmerpEntity ee = (EdmerpEntity) cell.getElement();
					String code = String.valueOf(ee.getCode());
					cell.setText(code);
				}
			}
		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), true));

		// Label
		i++;
		col = createTableViewerColumn(Messages.table_label_label.toUpperCase(), 700, SWT.LEFT, i);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof EdmerpEntity) {
					EdmerpEntity ee = (EdmerpEntity) cell.getElement();
					String name = String.valueOf(ee.getName());
					cell.setText(name);
				}
			}

		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int style, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setAlignment(style);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}
	
	
	private void createMenu() {
		Table table = tableViewer.getTable();

		/**
		 * *********************************************************************
		 * ******* creation du menu
		 */
		Menu contextMenu = new Menu(table.getShell(), SWT.POP_UP);
		table.setMenu(contextMenu);

		int index = 0;
		// ========= Liste de edmerp codes
		MenuItem itemList = new MenuItem(contextMenu, SWT.NONE, index);
		index++;
		itemList.setText(Messages.menu_label_addEdmerp);
		itemList.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("unused")
			@Override
			public void handleEvent(Event e) {
				WSEdmerpListDialog dialog = new WSEdmerpListDialog(Display.getCurrent().getActiveShell());

				// Dialog validated
				if (dialog.open() == Window.OK) {
					EdmerpEntity ee = dialog.getSelectedEdmerpEntity();
					edmerpList.add(ee);
					// Update Cruise Model Edmerp Codes
					updateCruiseModelEdmerpCodes();
					tableViewer.refresh();
				}
				
			}
		});


		// ========= supprimer edmerp sélectionné
		MenuItem itemDel = new MenuItem(contextMenu, SWT.NONE, index);
		index++;
		itemDel.setText(Messages.menu_label_removeEdmerp);
		itemDel.addListener(SWT.Selection, new Listener() {
			@SuppressWarnings("unused")
			@Override
			public void handleEvent(Event e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				@SuppressWarnings("unchecked")
				List<EdmerpEntity> selectedEdmerpList = selection.toList();
				for (EdmerpEntity selecteEdmerpData : selectedEdmerpList) {
					edmerpList.remove(selecteEdmerpData);	
					// Update Cruise Model Edmerp Codes
					updateCruiseModelEdmerpCodes();
					tableViewer.refresh();
				}
			}
		});

	

	}
	
	/**
	 * Allows to insert elements before or after another element.
	 * 
	 * @author pmahoudo
	 *
	 */
	public class TypeModelManipulator implements IModelManipulator {

		@Override
		public boolean insertBefore(Object source, Object target) {
			// TODO FIXME MHO bug parfois ???
			IStructuredSelection selection = (IStructuredSelection) source;
			@SuppressWarnings("unchecked")
			List<EdmerpEntity> selectedEdmerpList = selection.toList();
			EdmerpEntity targetType = (EdmerpEntity) target;

			if (!selectedEdmerpList.contains(targetType)) {
				// remove source
				for (EdmerpEntity selectedEdmerp : selectedEdmerpList) {
					int sourceIndex = edmerpList.indexOf(selectedEdmerp);
					edmerpList.remove(sourceIndex);
				}

				// Add source to target index
				int targetIndex = edmerpList.indexOf(targetType);
				for (EdmerpEntity selectedEdmerp : selectedEdmerpList) {
					edmerpList.add(targetIndex, selectedEdmerp);
				}
				updateCruiseModelEdmerpCodes();
				return true;
			}
			return false;
		}

		@Override
		public boolean insertAfter(Object source, Object target) {
			IStructuredSelection selection = (IStructuredSelection) source;
			@SuppressWarnings("unchecked")
			List<EdmerpEntity> selectedEdmerpList = selection.toList();
			EdmerpEntity targetType = (EdmerpEntity) target;

			if (!selectedEdmerpList.contains(targetType)) {
				// remove source
				for (EdmerpEntity selectedEdmerp : selectedEdmerpList) {
					int sourceIndex = edmerpList.indexOf(selectedEdmerp);
					edmerpList.remove(sourceIndex);
				}

				// Add source to target index
				int targetIndex = edmerpList.indexOf(targetType);
				for (EdmerpEntity selectedEdmerp : selectedEdmerpList) {
					edmerpList.add(targetIndex + 1, selectedEdmerp);
				}
				updateCruiseModelEdmerpCodes();
				return true;
			}
			return false;
		}
	}

	/**
	 *  Update Cruise Model Edmerp Codes
	 */
	public void updateCruiseModelEdmerpCodes() {
		CruiseModel cruiseModel = Nemo.getInstance().getDomain().getCruise().getModel();
		cruiseModel.setEdmerpCodes(Global.EMPTY_STRING);
		
		for (EdmerpEntity ee : edmerpList) {
			cruiseModel.addEdmerpCodes(String.valueOf(ee.getCode()));
		}
		Utils.invalidEtapes();
		
	}

}
