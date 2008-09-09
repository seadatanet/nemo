package fr.ifremer.nemo.config;

import java.util.ArrayList;

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
	 * Retourne le flag correspondant au code
	 */
	public Flag getFlag(String local) {
		int i;
		for (i = 0; i < getFlags().size(); i++) {
			if (getFlags().get(i).getLocal().equals(local)) {
				return getFlags().get(i);
			}
		}
		return null;
	}
}
