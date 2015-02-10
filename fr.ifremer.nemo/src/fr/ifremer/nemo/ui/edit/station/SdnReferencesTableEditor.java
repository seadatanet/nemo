package fr.ifremer.nemo.ui.edit.station;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
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
	ICollection l23List;
//	private final String[] optionsRole = { "isDescribedBy", "isObservedBy" };// TODO
	private final SdnXLinkReference.SDN_REFERENCE_ROLE[] optionsRole = 	
		{
			SdnXLinkReference.SDN_REFERENCE_ROLE.isDescribedBy,
			SdnXLinkReference.SDN_REFERENCE_ROLE.isObservedBy
			};

	private final Composite parent;

	public enum EDITOR_KEY {
		COMBO_TYPE_EDITOR, COMBO_ROLE_EDITOR, TEXT_HREF_EDITOR
	};

	// table columns keys (to store itam data)
	public enum DATA_KEY {
		TYPE, ROLE, HREF
	};

	public SdnReferencesTableEditor(Composite parent) {
		this.parent = parent;
		this.init();
		this.createContents();
	}

	private void init() {
		l23List = VocabLists.getVocabList("L23"); // TODO msg
		// TODO retirer CDI (ajouté automatiquement) et CSR (a ajouter dans
		// onglet cruise)?

	}

	private void createContents() {

		// Create the table
		table = new Table(parent, SWT.VIRTUAL | SWT.CHECK | SWT.BORDER
				| SWT.SINGLE | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumHeight = 500;
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		// Create columns
		TableColumn columnSelect = new TableColumn(table, SWT.CENTER);
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

	}

	public void addRow() {
		addRow(null);

	}

	public void addRow(SdnXLinkReference reference) {

		final TableItem item = new TableItem(table, SWT.NONE);

		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		final CCombo comboType = getComboType();
		editor.setEditor(comboType, item, 1);
		item.setData(EDITOR_KEY.COMBO_TYPE_EDITOR.toString(), editor);
		if (reference != null && reference.getType() != null) {
			comboType.setText(reference.getType());
			item.setData(DATA_KEY.TYPE.toString(), reference.getType());
		}

		editor.minimumWidth = comboType.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		comboType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// item.setText(col, comboType.getText());
				item.setData(DATA_KEY.TYPE.toString(), comboType.getText());

				// They selected an item; end the editing session
				// comboType.dispose();

			}
		});

		editor = new TableEditor(table);
		editor.grabHorizontal = true;
		final CCombo comboRole = getComboRole();
		editor.setEditor(comboRole, item, 2);
		item.setData(EDITOR_KEY.COMBO_ROLE_EDITOR.toString(), editor);
		if (reference != null && reference.getType() != null) {
			comboRole.setText(reference.getRole().toString());
			item.setData(DATA_KEY.ROLE.toString(), reference.getRole().toString());
		}

		editor.minimumWidth = comboRole.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		comboRole.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// item.setText(col, combo.getText());
				item.setData(DATA_KEY.ROLE.toString(), comboRole.getText());
				// They selected an item; end the editing session
				// combo.dispose();
			}
		});

		editor = new TableEditor(table);
		final Text text = new Text(table, SWT.NONE);
		editor.grabHorizontal = true;
		editor.setEditor(text, item, 3);
		item.setData(EDITOR_KEY.TEXT_HREF_EDITOR.toString(), editor);
		if (reference != null && reference.getHref() != null) {
			text.setText(reference.getHref());
			item.setData(DATA_KEY.HREF.toString(), reference.getHref()
					.toString());
		}

		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				// Set the text of the editor's control back into the cell
				// item.setText(col, text.getText());
				item.setData(DATA_KEY.HREF.toString(), text.getText());
			}
		});

		// if (reference != null){
		// if (reference.getType() != null){
		// item.setText(1, reference.getType());
		// item.setData(DATA_KEY.ROLE.toString(), reference.getType());
		// }
		// if (reference.getRole() != null){
		// item.setText(2,reference.getRole().toString());
		// item.setData(DATA_KEY.ROLE.toString(),
		// reference.getRole().toString());
		// }
		// item.setText(3, reference.getHref());
		// item.setData(DATA_KEY.HREF.toString(),
		// reference.getHref().toString());
		// }

		table.getColumn(0).setWidth(30);
		table.getColumn(1).setWidth(
				comboType.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
		table.getColumn(2).setWidth(
				comboRole.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);

	}

	private CCombo getComboType() {
		CCombo comboType = new CCombo(table, SWT.READ_ONLY);
		comboType.add("");
		for (IElement element : l23List.getElements()) {
			comboType.add(element.getKey());
		}
		return comboType;

	}

	private CCombo getComboRole() {
		final CCombo combo = new CCombo(table, SWT.READ_ONLY);
		combo.add("");
		for (int i = 0, n = optionsRole.length; i < n; i++) {
			combo.add(optionsRole[i].toString());
		}
		return combo;
	}

	public void deleteSelectedRows() {
		// debug
		TableItem[] items = table.getItems();
		for (TableItem item : items) {
			if (item.getChecked()) {
				TableEditor editor = (TableEditor) item
						.getData(EDITOR_KEY.COMBO_TYPE_EDITOR.toString());
				editor.getEditor().dispose();
				editor.dispose();
				editor = (TableEditor) item
						.getData(EDITOR_KEY.COMBO_ROLE_EDITOR.toString());
				editor.getEditor().dispose();
				editor.dispose();
				editor = (TableEditor) item.getData(EDITOR_KEY.TEXT_HREF_EDITOR
						.toString());
				editor.getEditor().dispose();
				editor.dispose();
				table.remove(table.indexOf(item));
				parent.pack();
			}
		}
		table.remove(table.getSelectionIndices());

	}

	public TableItem[] getItems() {

		return table.getItems();
	}

	public void fillFromModel(StationModel stationModel) {
//		if (stationModel.getSdnXLinkReferences() != null) {
//			for (SdnXLinkReference reference : stationModel
//					.getSdnXLinkReferences()) {
//
//				addRow(reference);
//			}
//		} else {
//			addRow();
//		}
	}

}
