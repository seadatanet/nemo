package fr.ifremer.nemo.ui.actions;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.ui.Application;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Viewer for Flags
 * 
 * @author altran
 * 
 */
public class FlagsViewer {
	private TableViewer viewer;
	private Shell shellFlags;
	private Button buttonValider;
	private Button buttonCancel;
	private final int gwidth = 550;
	private static FlagTable _flagTable = new FlagTable();
	private static Table tabFlagTable, tabFlag;
	private static Combo _flagDefaultCombo;
	private static Label textAreaFlag;
	private final OptionsAction optionAction;


	private final      ArrayList<FlagTable> tmpFlagTables;
	private String tmpDefaultFlagTable;
	
	
	public FlagsViewer(OptionsAction optionsAction) {
		this.optionAction = optionsAction;
		ArrayList<FlagTable>  originalFlagTables = optionsAction.getTmpFlagsChoices().getFlagTables();

		this.tmpFlagTables = new ArrayList<FlagTable>();
		for (FlagTable ft : originalFlagTables){
			tmpFlagTables.add(new FlagTable(ft));
		}

		createViewer();
	}

	private void createViewer() {

		shellFlags = new Shell(Application.display, SWT.VIRTUAL);
		shellFlags.setLocation(20, 20);
		shellFlags.setSize(gwidth, 450);
		shellFlags.setMinimumSize(gwidth, 450);
		shellFlags.setLayout(new GridLayout());

		
		// Creation du tableau listant les echelles de flags
		createTableFlagTable(shellFlags);

		// Creation du tableau des correspondaces
		createTableFlag(shellFlags);

		// Creation du bouton de flag par defaut
		textAreaFlag = new Label(shellFlags, SWT.NONE);
		textAreaFlag.setFont(Utils.font08);
		textAreaFlag.setText(Messages.settings_flagDefault);

		// FAE 4431 Flag qualité par défault configurable
		_flagDefaultCombo=new Combo(shellFlags, SWT.BORDER | SWT.READ_ONLY);
		_flagDefaultCombo.setFont(Utils.font08);
		fillFlagTable();
		// FAE 4431 Flag qualité par défault configurable
		_flagDefaultCombo.add("0");
		_flagDefaultCombo.add("1");
		_flagDefaultCombo.add("2");
		selectDefaultFlagsTab(optionAction.getTmpFlagsChoices().getDefaultFlagTable());
		// FAE 4431 Flag qualité par défault configurable
		_flagDefaultCombo.select(Integer.valueOf(optionAction.getTmpFlagsChoices().getDefaultFlag()));

		createOkCancelButtons(shellFlags);
		shellFlags.pack();
		shellFlags.open();
		while (!shellFlags.isDisposed()) {
			if (!Application.display.readAndDispatch())
				Application.display.sleep();
		}

	}
	private void createTableFlagTable(Composite parent) {
		tabFlagTable = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
		tabFlagTable.setFont(Utils.font08);
		tabFlagTable.setHeaderVisible(true);
		tabFlagTable.setLinesVisible(true);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tabFlagTable);

		TableColumn tableColumn1 = new TableColumn(tabFlagTable, SWT.NONE);
		tableColumn1.setWidth(60);
		tableColumn1.setText(Messages.table_label_code);

		TableColumn tableColumn2 = new TableColumn(tabFlagTable, SWT.NONE);
		tableColumn2.setWidth(200);
		tableColumn2.setText(Messages.table_label_label);

		// Menu contextuel
		Menu menu = new Menu(tabFlagTable.getShell(), SWT.POP_UP);
		MenuItem itemAdd = new MenuItem(menu, SWT.NONE);
		itemAdd.setText(Messages.menu_label_add);
		MenuItem itemRemove = new MenuItem(menu, SWT.NONE);
		itemRemove.setText(Messages.menu_label_remove);
		MenuItem itemDefault = new MenuItem(menu, SWT.NONE);
		itemDefault.setText(Messages.menu_label_default);
		tabFlagTable.setMenu(menu);

		final TableEditor editor = new TableEditor(tabFlagTable);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;


		tabFlagTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				// On affiche les flags correspondants
				_flagTable = FlagTable.getFlagTable(tmpFlagTables, tabFlagTable.getSelection()[0].getText());
				_flagTable.fillTable(tabFlag);

				// Saisie
				if (event.button == 3) {
					return;
				}
				Control old = editor.getEditor();
				if (old != null)
					old.dispose();

				Point pt = new Point(event.x, event.y);

				final TableItem item = tabFlagTable.getItem(pt);
				if (item == null) {
					return;
				}
				int column = -1;
				for (int i = 0, n = tabFlagTable.getColumnCount(); i < n; i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						column = i;
						break;
					}
				}

				if (column == -1) {
					return;
				}

				final Text text = new Text(tabFlagTable, SWT.NONE);
				text.setText(item.getText(column));
				text.setForeground(item.getForeground());
				text.selectAll();
				text.setFocus();

				editor.minimumWidth = text.getBounds().width;

				editor.setEditor(text, item, column);

				final int col = column;
				text.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent event) {
						switch (col) {
						case 0:
							_flagTable.setCode(text.getText());
							break;
						case 1:
							_flagTable.setLibelle(text.getText());
							break;
						default:
						}

						item.setText(col, text.getText());
					}
				});
			}
		});

		itemDefault.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				selectDefaultFlagsTab(tabFlagTable.getSelection()[0].getText());
			}
		});

		itemAdd.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TableItem item = new TableItem(tabFlagTable, SWT.NONE);
				item.setText(0, Messages.menu_label_new);
				item.setText(1, Messages.menu_label_new);

				FlagTable temp = new FlagTable();
				temp.setCode(item.getText(0));
				temp.setLibelle(item.getText(1));
				tmpFlagTables.add(temp);
			}
		});

		itemRemove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				tmpFlagTables.remove(_flagTable);
				tabFlagTable.remove(tabFlagTable.getSelectionIndex());
				tabFlag.clearAll();
			}
		});

		Utils.forceMenu(tabFlagTable, menu);
	}
	

	private void createTableFlag(Composite parent) {
		tabFlag = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
		tabFlag.setFont(Utils.font08);
		tabFlag.setHeaderVisible(true);
		tabFlag.setLinesVisible(true);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(tabFlag);
		TableColumn flagColumn1 = new TableColumn(tabFlag, SWT.NONE);
		flagColumn1.setWidth(60);
		flagColumn1.setText(Messages.table_label_local);

		TableColumn flagColumn2 = new TableColumn(tabFlag, SWT.NONE);
		flagColumn2.setWidth(210);
		flagColumn2.setText(Messages.table_label_seadatanet);

		// Menu contextuel
		Menu menu = new Menu(tabFlag.getShell(), SWT.POP_UP);
		MenuItem itemAdd = new MenuItem(menu, SWT.NONE);
		itemAdd.setText(Messages.menu_label_add);
		MenuItem itemRemove = new MenuItem(menu, SWT.NONE);
		itemRemove.setText(Messages.menu_label_remove);

		tabFlag.setMenu(menu);

		final Table tab = tabFlag;

		final TableEditor editor = new TableEditor(tabFlag);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		tabFlag.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				if (event.button == 3) {
					return;
				}
				Control old = editor.getEditor();
				if (old != null)
					old.dispose();

				Point pt = new Point(event.x, event.y);

				final TableItem item = tab.getItem(pt);
				if (item == null) {
					return;
				}
				int column = -1;
				for (int i = 0, n = tab.getColumnCount(); i < n; i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						column = i;
						break;
					}
				}

				if (column == -1) {
					return;
				}

				final int col = column;
				final Text text = new Text(tab, SWT.NONE);
				final CCombo combo = new CCombo(tab, SWT.READ_ONLY);

				String[] flags = Flag.flagSDN();
				for (int i = 0; i < flags.length; i++) {
					combo.add(flags[i]);
				}
				if (flags.length > 20) {
					combo.setVisibleItemCount(20);
				} else {
					combo.setVisibleItemCount(flags.length);
				}

				final Flag flag = _flagTable.getFlag(item.getText());

				switch (column) {
				case 0:
					text.setForeground(item.getForeground());

					text.setText(item.getText(column));
					text.setForeground(item.getForeground());
					text.selectAll();
					text.setFocus();

					editor.minimumWidth = text.getBounds().width;

					editor.setEditor(text, item, column);

					text.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent event) {
							flag.setLocal(text.getText());
							item.setText(col, text.getText());
						}
					});

					break;
				case 1:
					combo.setForeground(item.getForeground());

					combo.setText(item.getText(column));
					combo.setForeground(item.getForeground());
					editor.minimumWidth = combo.getBounds().width;

					editor.setEditor(combo, item, column);

					combo.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent event) {
							flag.setSdn(combo.getText());
							item.setText(col, combo.getText());
						}
					});
					break;
				default:
					return;
				}
			}
		});

		itemAdd.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TableItem item = new TableItem(tab, SWT.NONE);
				item.setText(0, Messages.menu_label_new);
				item.setText(1, Messages.menu_label_new);

				Flag temp = new Flag(item.getText(0), item.getText(1));
				_flagTable.getFlags().add(temp);
			}
		});

		itemRemove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				_flagTable.getFlags().remove(_flagTable.getFlag(tab.getSelection()[0].getText()));
				tab.remove(tab.getSelectionIndex());
			}
		});

		Utils.forceMenu(tabFlag, menu);
	}
	protected void selectDefaultFlagsTab(String defaultCode) {

		tmpDefaultFlagTable = defaultCode;
		// Mise en gras du flagtable par defaut
		Font normal = new Font(Application.display, Global.EMPTY_STRING, 8, SWT.NORMAL);
		Font bold = new Font(Application.display, Global.EMPTY_STRING, 8, SWT.BOLD);

		for (int i = 0; i < tabFlagTable.getItemCount(); i++) {
			if (tabFlagTable.getItem(i).getText(0).equals(defaultCode)) {
				tabFlagTable.getItem(i).setFont(bold);
			} else {
				tabFlagTable.getItem(i).setFont(normal);
			}
		}
	}
	
	private void fillFlagTable() {
		int i;

		for (i = 0; i < tmpFlagTables.size(); i++) {
			TableItem item = new TableItem(tabFlagTable, SWT.NONE);
			item.setText(0, tmpFlagTables.get(i).getCode());
			item.setText(1, tmpFlagTables.get(i).getLibelle());
		}

		if (tmpFlagTables.size() > 0) {
			tabFlagTable.setSelection(0);
			_flagTable = FlagTable.getFlagTable(tmpFlagTables, tabFlagTable.getSelection()[0].getText());
			_flagTable.fillTable(tabFlag);

		}
	}

	

	private void createOkCancelButtons(Composite parent) {
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


		buttonValider = new Button(compoButton, SWT.NONE);
		buttonValider.setFont(Utils.font08);
		buttonValider.setText(Messages.button_validate);
		buttonValider.pack();
		// listener cancel
		buttonValider.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				optionAction.updateFlags(tmpFlagTables,tmpDefaultFlagTable, _flagDefaultCombo.getText() );
				
				shellFlags.close();
			}
		});
		buttonCancel = new Button(compoButton, SWT.NONE);
		buttonCancel.setFont(Utils.font08);
		buttonCancel.setText(Messages.button_cancel);
		buttonCancel.pack();
		// listener cancel
		buttonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				shellFlags.close();
			}
		});
		compoButton.pack();
	}

	public TableViewer getViewer() {
		return viewer;
	}


}
