package fr.ifremer.nemo.config;

import java.util.TreeMap;
import java.util.Map.Entry;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
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
		_local = Global.EMPTY_STRING;
		_sdn = Global.EMPTY_STRING;
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
		CodeTableRecordType[] flags = NemoLists.getVocabList(Intern.list_flagsSDN);
		String[] listFlags = new String[flags.length];

		TreeMap<String, String> treeFlags = new TreeMap<String, String>();

		for (CodeTableRecordType record : flags) {
			treeFlags.put(fr.ifremer.nemo.util.Utils.extraitCodeURN(record.getEntryKey()), record
					.getEntryTerm());
		}

		int i = 0;
		for (Entry<String, String> currentEntry : treeFlags.entrySet()) {
			listFlags[i] = currentEntry.getKey() + Global.SEP
					+ currentEntry.getValue();
			i++;
		}

		return listFlags;
	}
}
