package fr.ifremer.nemo.ui.tables;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.config.EdmerpEntity;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.ui.filter.EdmerpFilter;
import fr.ifremer.nemo.util.Messages;

public class WSEdmerpListDialog extends Dialog {

	private TableViewer viewer;

	private Composite container;
	
	private Text searchText;

	private Button okButton;

	private EdmerpEntity selectedEdmerpEntity;

	/**
	 * Filter for the surveys.
	 */
	private EdmerpFilter filter;

	/**
	 * Constructor.
	 * 
	 * @param parentShell The parent shell.
	 */
	public WSEdmerpListDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		// Dialog window
		container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

//		// "search" label.
//		Label searchLabel = new Label(container, SWT.NONE);
//		searchLabel.setText(Messages.menu_label_search);

		// Search field
		searchText = new Text(container, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		searchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				if (keyEvent.keyCode == SWT.CR) { // enter
					search();
				}
			}
		});
		
		// "Search" button.
		Button searchButton = new Button(container, SWT.PUSH);
		searchButton.setText(Messages.menu_label_search);
		searchButton.addListener(SWT.Selection, event -> {
			search();
		});
		
		// TableViewer generation.
		createViewer(container);

		

		


		// Set the search filter
		filter = new EdmerpFilter();
		viewer.addFilter(filter);

		return container;
	}

	
	protected void search() {
		Display.getCurrent().getActiveShell().setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT));
		filter.setSearchText(searchText.getText());
		viewer.refresh();
		Display.getCurrent().getActiveShell().setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
	}
	
	/**
	 * Create, config the TableViewer and set the data.
	 * 
	 * @param parent The parent (dialog grid).
	 */
	private void createViewer(Composite parent) {
		// define the TableViewer
		viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// Enable the select button when a selection exists.
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				okButton.setEnabled(true);
			}
		});

		// Get the data
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		// create the TableViewer columns
		createColumns();

		// provide the input to the viewer
		viewer.setInput(NemoLists.getEdmerpList());

		// make lines and header visible
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
	}

	// Customize Dialog buttons
	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		// Change parent layout data to fill the whole bar
		parent.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

		okButton = createButton(parent, IDialogConstants.OK_ID, Messages.button_select, false);
		okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.button_cancel, false);
	}

	// Set the Dialog title
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.table_survey);
	}

	// Set the Dialog dimensions
	@Override
	protected Point getInitialSize() {
		return new Point(450, 450);
	}

	// The Dialog is resizable
	@Override
	protected boolean isResizable() {
		return true;
	}

	/**
	 * Create table columns.
	 * 
	 * @param parent The parent composite.
	 */
	public void createColumns() {

		TableViewerColumn colReference = new TableViewerColumn(viewer, SWT.NONE);
		colReference.getColumn().setWidth(200);
		colReference.getColumn().setText(Messages.table_label_code);
		colReference.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				EdmerpEntity ee = (EdmerpEntity) element;
				return String.valueOf(ee.getCode());
			}
		});

		TableViewerColumn colName = new TableViewerColumn(viewer, SWT.NONE);
		colName.getColumn().setWidth(200);
		colName.getColumn().setText(Messages.table_label_label);
		colName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				EdmerpEntity ee = (EdmerpEntity) element;
				return ee.getName();
			}
		});
	}

	// On OK, the selection is set
	@Override
	protected void okPressed() {
		IStructuredSelection selection = viewer.getStructuredSelection();
		setSelectedEdmerpEntity((EdmerpEntity) selection.getFirstElement());

		super.okPressed();
	}

	public EdmerpEntity getSelectedEdmerpEntity() {
		return selectedEdmerpEntity;
	}

	public void setSelectedEdmerpEntity(EdmerpEntity selectedEdmerpEntity) {
		this.selectedEdmerpEntity = selectedEdmerpEntity;
	}
}
