package fr.ifremer.nemo.ui.edit.station;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import sdn.vocabulary.interfaces.ICollection;
import sdn.vocabulary.interfaces.IElement;
import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.sismer_tools.seadatanet.SdnXLinkReference;

public class SdnReferencesTableEditor {

	Table table;
	// Number of rows
	private static final int NUM = 1;


	// Options for each dropdown
	ICollection l23List ;
	private final String[] optionsRole = { "isDescribedBy", "isObservedBy"};// TODO msg



	private final Composite parent;


	// table columns keys (to store itam data)
	public enum DATA_KEY {TYPE, ROLE, HREF};

	public SdnReferencesTableEditor(Composite parent) {
		this.parent = parent;
		this.init();
		this.createContents();
	}


	private void init()
	{
		l23List = VocabLists.getVocabList("L23"); // TODO msg
		// TODO retirer CDI (ajouté automatiquement) et CSR (a ajouter dans onglet cruise)?

	}
	private void createContents() {

		// Create the table
		table = new Table(parent, SWT.SINGLE | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION |SWT.CHECK);
		table.setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Create columns
		TableColumn columnSelect = new TableColumn(table, SWT.CENTER);
		//		columnSelect.setText("select");
		columnSelect.pack();

		TableColumn columnType = new TableColumn(table, SWT.CENTER);
		columnType.setText("type");// TODO msg
		columnType.pack();

		TableColumn columnRole = new TableColumn(table, SWT.CENTER);
		columnRole.setText("role");// TODO msg
		columnRole.pack();

		TableColumn columnHref = new TableColumn(table, SWT.CENTER);
		columnHref.setText("href");// TODO msg
		columnHref.pack();


		// create default NUM rows (empty)
		//		for (int i = 0; i < NUM; i++) {
		//			// Create the row
		//			final TableItem item = new TableItem(table, SWT.NONE);
		//
		//		}

		// Create an editor object to use for text editing
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Use a mouse listener, not a selection listener, since we're interested
		// in the selected column as well as row
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent event) {
				// Dispose any existing editor
				Control old = editor.getEditor();
				if (old != null) old.dispose();

				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = table.getItem(pt);

				if (item != null) {
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = table.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							// This is the selected column
							column = i;
							break;
						}
					}

					// Column 0 holds selection checkboxes
					if (column == 0) {
						final Button check = new Button(table, SWT.CHECK);
						check.setEnabled(true);
						// Set the control into the editor
						editor.setEditor(check, item, column);

					}
					// col 1: TYPE
					else if (column == 1) {
						// Create the dropdown and add data to it
						final CCombo combo = new CCombo(table, SWT.READ_ONLY);
						for (IElement element :l23List.getElements()){
							combo.add(element.getKey());
						}
						// Select the previously selected item from the cell
						combo.select(combo.indexOf(item.getText(column)));

						// Compute the width for the editor
						// Also, compute the column width, so that the dropdown fits
						editor.minimumWidth = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
						table.getColumn(column).setWidth(editor.minimumWidth);

						// Set the focus on the dropdown and set into the editor
						combo.setFocus();
						editor.setEditor(combo, item, column);

						// Add a listener to set the selected item back into the cell
						final int col = column;
						combo.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent event) {
								item.setText(col, combo.getText());
								item.setData(DATA_KEY.TYPE.toString(),combo.getText());

								// They selected an item; end the editing session
								combo.dispose();

							}
						});
						// col 2: ROLE
					} else if (column == 2){
						// Create the dropdown and add data to it
						final CCombo combo = new CCombo(table, SWT.READ_ONLY);
						for (int i = 0, n = optionsRole.length; i < n; i++) {
							combo.add(optionsRole[i]);
						}

						// Select the previously selected item from the cell
						combo.select(combo.indexOf(item.getText(column)));

						// Compute the width for the editor
						// Also, compute the column width, so that the dropdown fits
						editor.minimumWidth = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
						//						table.getColumn(column).setWidth(editor.minimumWidth);


						// Set the focus on the dropdown and set into the editor
						combo.setFocus();
						editor.setEditor(combo, item, column);

						// Add a listener to set the selected item back into the cell
						final int col = column;
						combo.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent event) {
								item.setText(col, combo.getText());
								item.setData(DATA_KEY.ROLE.toString(),combo.getText());
								// They selected an item; end the editing session
								combo.dispose();
							}
						});
						// col 3: Href
					}else if (column ==3) {

						// Create the Text object for our editor
						final Text text = new Text(table, SWT.NONE);
						text.setForeground(item.getForeground());

						// Transfer any text from the cell to the Text control,
						// set the color to match this row, select the text,
						// and set focus to the control
						text.setText(item.getText(column));
						text.setForeground(item.getForeground());
						text.selectAll();
						text.setFocus();

						// Recalculate the minimum width for the editor
						editor.minimumWidth = text.getBounds().width;

						// Set the control into the editor
						editor.setEditor(text, item, column);

						// Add a handler to transfer the text back to the cell
						// any time it's modified
						final int col = column;
						text.addModifyListener(new ModifyListener() {
							public void modifyText(ModifyEvent event) {
								// Set the text of the editor's control back into the cell
								item.setText(col, text.getText());
								item.setData(DATA_KEY.HREF.toString(),text.getText());
							}
						});
					}
				}
			}
		});
	}

	public void addRow()
	{
		final TableItem item = new TableItem(table, SWT.NONE);
	}


	public void deleteSelectedRows() {
//		debug
//		for (Item item : table.getSelection()){
//			System.out.println(item.getData(DATA_KEY.TYPE.toString()));
//		}
		table.remove(table.getSelectionIndices());

	}


	public TableItem[] getItems(){

		return table.getItems();
	}

	public void fillFromModel(StationModel stationModel)
	{
		if ( stationModel.getSdnXLinkReferences() != null){
			for (SdnXLinkReference ref : stationModel.getSdnXLinkReferences()){
				TableItem item = new TableItem(table, SWT.NULL);
				if (ref.getType() != null){
					item.setText(1, ref.getType());
					item.setData(DATA_KEY.ROLE.toString(), ref.getType());
				}
				if (ref.getRole() != null){
					item.setText(2,ref.getRole().toString());
					item.setData(DATA_KEY.ROLE.toString(), ref.getRole().toString());
				}
				item.setText(3, ref.getHref());
				item.setData(DATA_KEY.HREF.toString(), ref.getHref().toString());
			}
		}
	}

}
