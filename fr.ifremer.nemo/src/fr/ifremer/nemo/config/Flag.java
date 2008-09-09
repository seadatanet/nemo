package fr.ifremer.nemo.config;

import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.ui.OptionsAction;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;

/**
 * Class Flag
 * 
 * Classe permettant la description d'une correspondance de flags
 * 
 * @author Herve Taniou
 */
public class Flag {
	/**
	 * flag local
	 */
	protected String _local;

	/**
	 * flag SDN
	 */
	protected String _sdn;

	/**
	 * Constructeurs
	 */
	public Flag() {
		_local = "";
		_sdn = "";
	}

	public Flag(String local, String sdn) {
		_local = local;
		_sdn = sdn;
	}

	/**
	 * Methodes
	 */

	public String getLocal() {
		return _local;
	}

	public void setLocal(String _local) {
		this._local = _local;
	}

	public String getSdn() {
		return _sdn;
	}

	public void setSdn(String _sdn) {
		this._sdn = _sdn;
	}

	/**
	 * Retourne la liste des flags SDN
	 * 
	 * @return String[]
	 */
	public static String[] flagSDN() {
		CodeTableRecordType[] flags = NemoLists.getVocabList("L201");
		String[] listFlags = new String[flags.length];

		TreeMap<String, String> treeFlags = new TreeMap<String, String>();

		for (CodeTableRecordType record : flags) {
			treeFlags.put(Utils.extraitCodeURN(record.getEntryKey()), record
					.getEntryTerm());
		}

		int i = 0;
		for (Entry<String, String> currentEntry : treeFlags.entrySet()) {
			listFlags[i] = currentEntry.getKey() + " - "
					+ currentEntry.getValue();
			i++;
		}

		return listFlags;
	}

	public static void createTableFlag(Table table, FlagTable flagTable,
			boolean menuLoad) {
		// table = new Table(composite, SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(table);

		TableColumn flagColumn1 = new TableColumn(table, SWT.NONE);
		flagColumn1.setWidth(60);
		flagColumn1.setText(Messages.getString("OptionsAction.36")); //$NON-NLS-1$

		TableColumn flagColumn2 = new TableColumn(table, SWT.NONE);
		flagColumn2.setWidth(210);
		flagColumn2.setText(Messages.getString("OptionsAction.37")); //$NON-NLS-1$

		// Menu contextuel
		Menu menu = new Menu(table.getShell(), SWT.POP_UP);
		MenuItem itemAdd = new MenuItem(menu, SWT.NONE);
		itemAdd.setText(Messages.getString("OptionsAction.38")); //$NON-NLS-1$
		MenuItem itemRemove = new MenuItem(menu, SWT.NONE);
		itemRemove.setText(Messages.getString("OptionsAction.39")); //$NON-NLS-1$
				
		table.setMenu(menu);

		final Table tab = table;
		final FlagTable flagTab = flagTable;
		final Menu men = menu;

		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		table.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
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

				final Flag flag = flagTab.getFlag(item.getText());

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
					// combo.selectAll();
					// combo.setFocus();
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
				item.setText(0, "new");
				item.setText(1, "new");

				Flag temp = new Flag(item.getText(0), item.getText(1));
				flagTab.getFlags().add(temp);
			}
		});

		itemRemove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				flagTab.getFlags().remove(
						flagTab.getFlag(tab.getSelection()[0].getText()));
				tab.remove(tab.getSelectionIndex());
			}
		});
		
		if (menuLoad) {
			MenuItem itemLoad = new MenuItem(menu, SWT.CASCADE);
			itemLoad.setText(Messages.getString("OptionsAction.43")); //$NON-NLS-1$
			final Menu submenu = new Menu(table.getShell(), SWT.DROP_DOWN);
			itemLoad.setMenu(submenu);
			final ArrayList<FlagTable> flagTables = Utils.configuration.getFlagTables();
			
			for (int i = 0; i < flagTables.size(); i++) {
				final MenuItem childItem = new MenuItem(submenu, SWT.PUSH);
				childItem.setText(flagTables.get(i).getCode() + " - " + flagTables.get(i).getLibelle());
				final int j = i;
				childItem.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						fillTable(tab, flagTables.get(j));
					}
				});
			}
		}

		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				// Lors d'un clic droit sur le tableau, on force l'affichage du
				// menu contextuel
				if (event.button == 3) {
					men.setVisible(true);
				}
			}
		});
	}
	
	public static void fillTable(Table table, FlagTable flagTable) {
		int i;
		table.removeAll();

		if (flagTable != null) {
			for (i = 0; i < flagTable.getFlags().size(); i++) {
				TableItem itemTemp = new TableItem(table, SWT.NONE);
				itemTemp.setText(0, flagTable.getFlags().get(i).getLocal());
				itemTemp.setText(1, flagTable.getFlags().get(i).getSdn());
			}
		}
	}
}
