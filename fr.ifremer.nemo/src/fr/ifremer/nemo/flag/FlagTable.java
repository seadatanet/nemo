package fr.ifremer.nemo.flag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
	
	private Set changeListeners = new HashSet();

	
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
	 * Méthodes
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
	 *  Retourne une table de correspondance de flag en fonction du code
	 *  @param code
	 */
	public static FlagTable getFlagTable(ArrayList<FlagTable> flagTables, String code) {
		int i;
		for (i = 0; i < flagTables.size(); i++) {
			if (flagTables.get(i).getCode().equals(code)) {
				return flagTables.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Add a new task to the collection of tasks
	 */
	public void addFlag(String local, String sdn) {
		Flag flag = new Flag(local, sdn);
		getFlags().add(getFlags().size(), flag);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IFlagListViewer) iterator.next()).addFlag(flag);
	}

	/**
	 * @param task
	 */
	public void removeFlag(Flag flag) {
		getFlags().remove(flag);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IFlagListViewer) iterator.next()).removeFlag(flag);
	}
	
	/**
	 * @param task
	 */
	public void flagChanged(Flag flag) {
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IFlagListViewer) iterator.next()).updateFlag(flag);
	}
	
	/**
	 * @param viewer
	 */
	public void removeChangeListener(IFlagListViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(IFlagListViewer viewer) {
		changeListeners.add(viewer);
	}
}
