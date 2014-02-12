package fr.ifremer.nemo.ui.actions;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.config.EdmoEntity;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.ui.Application;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Messages;

/**
 * Viewer for EDMO code choice: display a table with Edmo codes, names and
 * acountry, with column sorting and filter
 * 
 * @author altran
 * 
 */
public class EdmoViewer {
	private TableViewer viewer;
	private Shell shellEdmo;
	private Button buttonValider;
	private Button buttonCancel;
	private final int gwidth = 550;
	private Composite compoTable;
	private TableColumn colCode;
	private TableColumn colName;
	private TableColumn colCountry;
	private EdmoFilter filter;
	private final EdmoChoices tmpEdmoChoices;
	private final int choice;
	private final OptionsAction optionActions;

	public EdmoViewer(OptionsAction optionsAction, int choice) {
		this.choice = choice;
		this.optionActions = optionsAction;
		this.tmpEdmoChoices = new EdmoChoices();
		createViewer();
	}

	private void createViewer() {

		shellEdmo = new Shell(Application.display, SWT.VIRTUAL);
		shellEdmo.setLocation(20, 20);
		shellEdmo.setSize(gwidth, 220);
		shellEdmo.setMinimumSize(gwidth, 220);
		shellEdmo.setLayout(new GridLayout());

		createCompoTable(shellEdmo);
		createCompoBouton(shellEdmo);

		shellEdmo.pack();
		shellEdmo.open();
		while (!shellEdmo.isDisposed()) {
			if (!Application.display.readAndDispatch())
				Application.display.sleep();
		}

	}

	private void createCompoTable(final Composite parent) {
		compoTable = new Composite(parent, SWT.NONE);
		compoTable.setFont(Utils.font08);
		compoTable.setBackground(new Color(null, 255, 0, 0));

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		TableColumnLayout layout = new TableColumnLayout();
		compoTable.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = gwidth;
		data.heightHint = 200;
		compoTable.setLayoutData(data);

		viewer = new TableViewer(compoTable, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL);

		createColumns(viewer);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new EdmoLabelProvider());
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		EdmoSorter comparator = new EdmoSorter(EdmoSorter.SORT_CODE, SWT.DOWN);
		viewer.setComparator(comparator);

		java.util.List<EdmoEntity> edmoList = NemoLists.getEdmoList();
		viewer.setInput(edmoList);
		viewer.getTable().addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				EdmoEntity edmo = (EdmoEntity) arg0.item.getData();
				if (choice == EdmoChoices.CHOICE_IdEdmoRespCdi) {
					tmpEdmoChoices.setIdEdmoRespCdi(edmo.getCode());
				} else if (choice == EdmoChoices.CHOICE_CodeEdmoDonnees) {
					tmpEdmoChoices.setCodeEdmoDonnees(edmo.getCode());
				} else if (choice == EdmoChoices.CHOICE_IdEdmoRespDonnees) {
					tmpEdmoChoices.setIdEdmoRespDonnees(edmo.getCode());
				} else if (choice == EdmoChoices.CHOICE_IdEdmoDistDonnees) {
					tmpEdmoChoices.setIdEdmoDistDonnees(edmo.getCode());
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});
	}

	private void createCompoBouton(Composite parent) {
		Composite compoButton = new Composite(parent, SWT.NONE);
		compoButton.setFont(Utils.font08);
		compoButton.setBackground(parent.getBackground());
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		compoButton.setLayout(gridLayout);
		GridData data = new GridData();
		data.widthHint = gwidth;
		data.heightHint = 30;
		compoButton.setLayoutData(data);

		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText(Messages.settings_EDMO_search_label);
		final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));

		// New to support the search
		searchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {
				filter.setSearchText(searchText.getText());
				viewer.refresh();
			}

		});
		filter = new EdmoFilter();
		viewer.addFilter(filter);

		buttonValider = new Button(compoButton, SWT.NONE);
		buttonValider.setFont(Utils.font08);
		buttonValider.setText(Messages.button_validate);
		buttonValider.pack();
		// listener cancel
		buttonValider
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						optionActions.updateEdmo(tmpEdmoChoices);
						shellEdmo.close();
					}
				});
		buttonCancel = new Button(compoButton, SWT.NONE);
		buttonCancel.setFont(Utils.font08);
		buttonCancel.setText(Messages.button_cancel);
		buttonCancel.pack();
		// listener cancel
		buttonCancel
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						shellEdmo.close();
					}
				});
		compoButton.pack();
	}

	public TableViewer getViewer() {
		return viewer;
	}

	/**
	 * create the columns for the table
	 * 
	 * @param viewer
	 */
	private void createColumns(final TableViewer viewer) {
		Listener sortListener = new Listener() {
			public void handleEvent(Event e) {
				// determine new sort column and direction
				TableColumn sortColumn = viewer.getTable().getSortColumn();
				TableColumn currentColumn = (TableColumn) e.widget;
				int dir = viewer.getTable().getSortDirection();
				if (sortColumn == currentColumn) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					viewer.getTable().setSortColumn(currentColumn);
					dir = SWT.UP;
				}
				// sort the data based on column and direction
				int sortIdentifier = -1;
				if (currentColumn == colCode) {
					sortIdentifier = EdmoSorter.SORT_CODE;
				}
				if (currentColumn == colName) {
					sortIdentifier = EdmoSorter.SORT_NAME;
				}
				if (currentColumn == colCountry) {
					sortIdentifier = EdmoSorter.SORT_COUNTRY;
				}
				viewer.getTable().setSortDirection(dir);
				viewer.setSorter(new EdmoSorter(sortIdentifier, dir));
			}
		};
		colCode = new TableColumn(viewer.getTable(), SWT.LEFT);
		colCode.setText("Code");
		((TableColumnLayout) compoTable.getLayout()).setColumnData(colCode,
				new ColumnWeightData(10, true));
		colCode.addListener(SWT.Selection, sortListener);

		colName = new TableColumn(viewer.getTable(), SWT.LEFT);
		colName.setText("Name");
		((TableColumnLayout) compoTable.getLayout()).setColumnData(colName,
				new ColumnWeightData(50, true));
		colName.addListener(SWT.Selection, sortListener);

		colCountry = new TableColumn(viewer.getTable(), SWT.LEFT);
		colCountry.setText("Country");
		((TableColumnLayout) compoTable.getLayout()).setColumnData(colCountry,
				new ColumnWeightData(40, true));
		colCountry.addListener(SWT.Selection, sortListener);

	}
}
