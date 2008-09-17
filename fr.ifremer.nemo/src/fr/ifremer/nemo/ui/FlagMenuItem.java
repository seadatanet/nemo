package fr.ifremer.nemo.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class FlagMenuItem {
	private Menu _menu;

	private Table _table;

	private String _code;

	private MenuItem _item;

	public FlagMenuItem(Menu menu, Table table, String code, String name) {
		_menu = menu;
		_table = table;
		_code = code;

		_item = new MenuItem(_menu, SWT.PUSH);
		_item.setText(name);

		_item.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// Teste si la metadonnée est déjà présente dans le tableau
				for (int i = 0; i < _table.getItemCount(); i++) {
					if (_table.getItems()[i].getText(0).equals(_code)) {
						return;
					}
				}
				
				// Ajout de la metadonnée dans le tableau
				TableItem itemTemp = new TableItem(_table, SWT.NONE);
				itemTemp.setText(0, _code);
				itemTemp.setText(1, _item.getText());
				ViewEdit.formEdition.reflow(true);
			}
		});
	}

	public String getCode() {
		return _code;
	}

	public void setCode(String _code) {
		this._code = _code;
	}

	public MenuItem getItem() {
		return _item;
	}

	public void setItem(MenuItem _item) {
		this._item = _item;
	}

	public Menu getMenu() {
		return _menu;
	}

	public void setMenu(Menu _menu) {
		this._menu = _menu;
	}

	public Table getTable() {
		return _table;
	}

	public void setTable(Table _table) {
		this._table = _table;
	}

}
