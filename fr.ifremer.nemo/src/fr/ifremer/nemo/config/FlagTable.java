package fr.ifremer.nemo.config;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import fr.ifremer.nemo.ui.Utils;

/**
 * Class FlagTable
 * 
 * Classe permettant la description d'un tableau de correspondance de flags
 * 
 * @author Herve Taniou
 */
public class FlagTable {
	/**
	 * Code du tableau
	 */
	protected String _code;

	/**
	 * Libelle du tableau
	 */
	protected String _libelle;

	/**
	 * Liste des flags
	 */
	protected ArrayList<Flag> _flags;

	/**
	 * Constructeur
	 */
	public FlagTable() {
		_code = "";
		_libelle = "";
		_flags = new ArrayList<Flag>();
	}

	public FlagTable(FlagTable flagTable) {
		_code = flagTable.getCode();
		_libelle = flagTable.getLibelle();
		_flags = flagTable.getFlags();
	}

	/**
	 * Methodes
	 */

	public String getCode() {
		return _code;
	}

	public void setCode(String _code) {
		this._code = _code;
	}

	public ArrayList<Flag> getFlags() {
		return _flags;
	}

	public void setFlags(ArrayList<Flag> _flags) {
		this._flags = _flags;
	}

	public String getLibelle() {
		return _libelle;
	}

	public void setLibelle(String _libelle) {
		this._libelle = _libelle;
	}

	/**
	 * Retourne une table de correspondance de flag en fonction du code
	 * 
	 * @param code
	 */
	public static FlagTable getFlagTable(ArrayList<FlagTable> flagTables,
			String code) {
		int i;
		for (i = 0; i < flagTables.size(); i++) {
			if (flagTables.get(i).getCode().equals(code)) {
				return flagTables.get(i);
			}
		}
		return null;
	}

	/**
	 * Retourne le flag correspondant au code
	 */
	public Flag getFlag(String local) {
		for (int i = 0; i < getFlags().size(); i++) {
			if (getFlags().get(i).getLocal().equals(local)) {
				return getFlags().get(i);
			}
		}
		return null;
	}

	/**
	 * Retourne le flag SDN correspondant au flag local
	 */
	public String getFlagSDN(String local) {
		for (int i = 0; i < getFlags().size(); i++) {
			if (getFlags().get(i).getLocal().equals(local)) {
				return Utils.extraitCode(getFlags().get(i).getSdn());
			}
		}
		return "0";
	}

	public void fillTable(Table table) {
		table.removeAll();

		ArrayList<Flag> flags = getOrderedFlags();
		for (int i = 0; i < flags.size(); i++) {
			TableItem itemTemp = new TableItem(table, SWT.NONE);
			itemTemp.setText(0, flags.get(i).getLocal());
			itemTemp.setText(1, flags.get(i).getSdn());
		}
	}

	public void fillFlagTable(Table table) {
		setFlags(new ArrayList<Flag>(table.getItemCount()));

		for (int i = 0; i < table.getItemCount(); i++) {
			getFlags().add(new Flag(table.getItems()[i].getText(0), table.getItems()[i].getText(1)));
		}
	}

	/**
	 * Retourne la liste des flags triee par local
	 */
	public ArrayList<Flag> getOrderedFlags() {
		ArrayList<Flag> arrayFlag = new ArrayList<Flag>();

		TreeMap<String, Flag> treeFlags = new TreeMap<String, Flag>();

		for (Flag flag : getFlags()) {
			treeFlags.put(flag.getLocal(), flag);
		}

		for (Entry<String, Flag> currentEntry : treeFlags.entrySet()) {
			arrayFlag.add(currentEntry.getValue());
		}

		return arrayFlag;
	}

	/**
	 * Retourne la liste des flagtables triee par code
	 */
	public static ArrayList<FlagTable> getOrderedFlagTables(
			ArrayList<FlagTable> flagTables) {
		ArrayList<FlagTable> arrayFlagTable = new ArrayList<FlagTable>();

		TreeMap<String, FlagTable> treeFlagTables = new TreeMap<String, FlagTable>();

		for (int i = 0; i < flagTables.size(); i++) {
			treeFlagTables.put(flagTables.get(i).getCode(), flagTables.get(i));
		}

		for (Entry<String, FlagTable> currentEntry : treeFlagTables.entrySet()) {
			arrayFlagTable.add(currentEntry.getValue());
		}

		return arrayFlagTable;
	}
}
