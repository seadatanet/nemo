/* ============================================================================
 * Nom du fichier	: ComboType.java
 * Auteur			: dboulang
 * Date				: 23 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.cruise.TableDataSurvey;
import fr.ifremer.nemo.ui.edit.station.EditStationForm;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import sdn.vocabulary.interfaces.IElement;

/**
 * Description: enumération des types de Combo possibles
 * 
 * @author dboulang
 */
public enum ComboType {
	NONE, BLOCK, FRQ, DATE, TIME, LAT, LON, DATATYPE, SHIP, REGION, COUNTRY, DATACENTER, CONFIDENT;

	public CCombo createCombo(Composite parent) {
		/**
		 * SHIP, REGION, COUNTRY, DATACENTER, CONFIDENT must be read only
		 */
		CCombo valueCombo;
		switch (this) {
		case DATE:
		case TIME:
		case LAT:
		case LON:
		case DATATYPE:
			valueCombo = new CCombo(parent, SWT.BORDER);
			break;
		case REGION:
		case COUNTRY:
		case SHIP:
		case DATACENTER:
		case CONFIDENT:
			valueCombo = new CCombo(parent, SWT.READ_ONLY);
			break;
		default:
			valueCombo = null;
			break;
		}
		return valueCombo;
	}

	public CCombo fillCombo(CCombo valueCombo) {
		String temp;
		switch (this) {
		case DATE:
			valueCombo.add("DD/MM/YY"); //$NON-NLS-1$
			valueCombo.add("DD/MM/YYYY"); //$NON-NLS-1$
			valueCombo.add("DDMMYY"); //$NON-NLS-1$
			valueCombo.add("DDMMYYYY"); //$NON-NLS-1$
			valueCombo.add("MMM DD YYYY"); //$NON-NLS-1$
			valueCombo.add("YY/MM/DD"); //$NON-NLS-1$
			valueCombo.add("YYMMDD"); //$NON-NLS-1$
			valueCombo.add("YYYY/MM/DD"); //$NON-NLS-1$
			valueCombo.add("YYYY-MM-DD"); //$NON-NLS-1$
			valueCombo.add("YYYYMMDD"); //$NON-NLS-1$
			valueCombo.add("DDDDDDD.d"); //$NON-NLS-1$
			break;
		case TIME:
			valueCombo.add("hh:mm PM"); //$NON-NLS-1$
			valueCombo.add("hh:mm:ss PM"); //$NON-NLS-1$
			valueCombo.add("hh24:mm"); //$NON-NLS-1$
			valueCombo.add("hh24:mm:ss"); //$NON-NLS-1$
			valueCombo.add("hh24:mm:ss.sss"); //$NON-NLS-1$
			valueCombo.add("hh24mm"); //$NON-NLS-1$
			valueCombo.add("hh24mmss"); //$NON-NLS-1$
			valueCombo.add("hh24 mm"); //$NON-NLS-1$
			// véritable "Decimal Time" : valueCombo.add("cc.cc.cc"); //$NON-NLS-1$
			valueCombo.add("hh24.ccc"); //$NON-NLS-1$
			valueCombo.add("D.dddddd"); //$NON-NLS-1$
			valueCombo.add("ttttt"); //$NON-NLS-1$
			break;
		case LAT:
			valueCombo.add("+DD MM.mm"); //$NON-NLS-1$
			valueCombo.add("+DD.ddd"); //$NON-NLS-1$
			valueCombo.add("+DDMMSS"); //$NON-NLS-1$
			valueCombo.add("NDD MM.mm"); //$NON-NLS-1$
			valueCombo.add("NDD.ddd"); //$NON-NLS-1$
			valueCombo.add("NDDMMSS"); //$NON-NLS-1$
			if (RulesManager.getInstance().isSeparatorChecked()) {
				valueCombo.add(EditStationForm.LATLON_CHOICE_DEC_DEGREES_FOR_FILES_WITH_SEP);
			}
			break;
		case LON:
			valueCombo.add("+DDD MM.mm"); //$NON-NLS-1$
			valueCombo.add("+DDD.ddd"); //$NON-NLS-1$
			valueCombo.add("+DDDMMSS"); //$NON-NLS-1$
			valueCombo.add("NDDD MM.mm"); //$NON-NLS-1$
			valueCombo.add("NDDD.ddd"); //$NON-NLS-1$
			valueCombo.add("NDDDMMSS"); //$NON-NLS-1$
			if (RulesManager.getInstance().isSeparatorChecked()) {
				valueCombo.add(EditStationForm.LATLON_CHOICE_DEC_DEGREES_FOR_FILES_WITH_SEP);
			}
			break;
		case DATATYPE:
			// FAE022
			if (!RulesManager.getInstance().isProcessCollection() && TableDataSurvey.types != null) {
				int taille = TableDataSurvey.types.size();
				for (int i = 0; i < taille; i++) {
					// on n'ajoute que les sensors
					if (TableDataSurvey.types.get(i).getSensor()) {
						valueCombo.add(TableDataSurvey.types.get(i).getCode());
					}
				}
			}
			break;
		case SHIP:
			Collection<IElement> navires = VocabLists.getVocabList(Intern.list_ship).getElements();

			TreeMap<String, String> treeNavire = new TreeMap<String, String>();

			for (IElement record : navires) {
//				treeNavire.put(record.getLabel(), record.getKey()); //42715
				treeNavire.put(record.getKey(), record.getLabel());
			}

			NemoLists.listeNavire = new ArrayList<String>();
			temp = Global.EMPTY_STRING;
			for (Entry<String, String> currentEntry : treeNavire.entrySet()) {
//				temp =	currentEntry.getValue() + Global.SEP + currentEntry.getKey(); //42715
				temp = currentEntry.getKey() + Global.SEP + currentEntry.getValue();
				NemoLists.listeNavire.add(temp);
				valueCombo.add(temp);
			}
			Utils.createSearchMenu(valueCombo, Global.SHIP);
			break;
		case REGION:
			NemoLists.listeRegion = new ArrayList<String>();
			Collection<IElement> regions = VocabLists.getVocabList(Intern.list_regionsPolygones).getElements();

			TreeMap<String, String> treeRegion = new TreeMap<String, String>();

			for (IElement record : regions) {
				treeRegion.put(record.getLabel(), record.getKey());
			}

			temp = Global.EMPTY_STRING;
			for (Entry<String, String> currentEntry : treeRegion.entrySet()) {
				temp = currentEntry.getValue() + Global.SEP + currentEntry.getKey();
				NemoLists.listeRegion.add(temp);
				valueCombo.add(temp);
			}
			Utils.createSearchMenu(valueCombo, Global.REGION);
			break;
		case COUNTRY:
			Collection<IElement> records = VocabLists.getVocabList(Intern.list_countries).getElements();

			TreeMap<String, String> treePays = new TreeMap<String, String>();

			for (IElement record : records) {
				treePays.put(record.getLabel(), record.getKey());
			}

			for (Entry<String, String> currentEntry : treePays.entrySet()) {
				valueCombo.add(currentEntry.getValue() + Global.SEP + currentEntry.getKey());
			}
			break;
		case DATACENTER:
			Collection<IElement> centres = VocabLists.getVocabList(Intern.list_dataCenter).getElements();

			TreeMap<String, String> treeCentre = new TreeMap<String, String>();

			for (IElement record : centres) {
				treeCentre.put(record.getKey(), record.getLabel());
			}

			NemoLists.listeCentreDonnees = new ArrayList<String>();
			temp = Global.EMPTY_STRING;
			for (Entry<String, String> currentEntry : treeCentre.entrySet()) {
				temp = currentEntry.getKey() + Global.SEP + currentEntry.getValue();
				NemoLists.listeCentreDonnees.add(temp);
				valueCombo.add(temp);
			}
			Utils.createSearchMenu(valueCombo, Global.DATACENTRE);
			break;
		case CONFIDENT:
			valueCombo.add("P - Public"); //$NON-NLS-1$
			valueCombo.add("L - Limited"); //$NON-NLS-1$
			valueCombo.add("C - Confidential"); //$NON-NLS-1$
			break;
		default:
			break;
		}
		return valueCombo;
	}
}
