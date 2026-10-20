/* ============================================================================
 * Nom du fichier : ParameterFilteredTable.java
 * Auteur : ycaudal
 * Date : 22 déc. 09
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.ui.edit.data.TypeUnit;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;
import sdn.vocabulary.interfaces.IElement;
import sdn.vocabulary.interfaces.VocabularyException;

/**
 * Description:
 * 
 * @author ycaudal
 * 
 */
public class ParameterFilteredTable extends AbstractFilteredTable {

	/**
	 * reference sur la tableData (de l'onglet data) depuis laquelle cette table
	 * a été ouverte
	 */
	private final TableData myTableDataRef;

	/**
	 * copie de la liste des parametres typesWS pouvant être filtrée
	 */
	private ArrayList<TypeData> listeFiltree;

	/**
	 * @param comboRecherche
	 */
	public ParameterFilteredTable(CCombo comboRecherche) {
		_comboRecherche = comboRecherche;
		myTableDataRef = Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createDataList() {
		// liste filtree initialisee e typesWS au depart
		reloadListeFiltree();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createTable() {
		super.createTable();
		// ArrayList<TypeData> filteredList =myTableDataRef.getListeFiltree();

		//		myFiltreTable = new Table(top, SWT.FULL_SELECTION);
		//
		//		myFiltreTable.setHeaderVisible(true);
		//		myFiltreTable.setLinesVisible(true);
		//		myFiltreTable.setBounds(new Rectangle(3, 45, 298, 237));

		// creation des colonnes...
		TableColumn tableColumn = new TableColumn(myFiltreTable, SWT.NONE);
		tableColumn.setWidth(500);
		tableColumn.setText(Messages.table_label_codeName);

		TableColumn tableColumn2 = new TableColumn(myFiltreTable, SWT.NONE);
		tableColumn2.setWidth(100);
		tableColumn2.setText(Messages.table_label_unit);

		populateTableWithFilteredList();
	}

	@Override
	protected void populateTableWithFilteredList() {
		for (int i = 0; i < listeFiltree.size(); i++) {
			TableItem item = new TableItem(myFiltreTable, SWT.NONE);
			item.setText(
					new String[] { listeFiltree.get(i).getFormattedCodeAndName(), listeFiltree.get(i).getUnite() });
		}
	}

	/**
	 * recharge la liste des paramètres filtrée depuis la liste des typesWS (=
	 * annule le filtre)
	 */
	private void reloadListeFiltree() {
		listeFiltree = new ArrayList<TypeData>();
		// liste filtree initialisee e typesWS au depart
		int taille = myTableDataRef.getTypesWS().size();
		for (int i = 0; i < taille; i++) {
			listeFiltree.add(myTableDataRef.getTypesWS().get(i));
		}

		Collections.sort(listeFiltree);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void filterAction(String chaine) {

		ArrayList<TypeData> origine, filtree;
		String regexp;

		filtree = new ArrayList<TypeData>();
		origine = myTableDataRef.getTypesWS();

		// remplissage de la liste filtree
		for (int i = 0; i < origine.size(); i++) {
			regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
			Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(origine.get(i).getFormattedCodeAndName());
			if (m.matches()) {
				// ajout dans la liste filtree
				filtree.add(origine.get(i));
			}
		}
		listeFiltree = filtree;
		// affichage de la liste filtree
		//		razTable(myFiltreTable);
		//		createTable();
		refreshTable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDialogTitle() {

		DataListTypeEnum listToUse = RulesManager.getInstance().getMyDataListType();
		String title = Messages.table_parameters;
		title += Global.SPACE;
		// title += listToUse.name();
		title += Global.SPACE;

		if (listToUse == DataListTypeEnum.P011_VIA_P021) {
			title += Messages.edit_data_P021;
		} else if (listToUse == DataListTypeEnum.P011_VIA_P091 || listToUse == DataListTypeEnum.P091) {
			title += Messages.edit_data_P091;
		}
		return title;
	}

	/**
	 * action appelée lorsqu'un parametre est selectionné dans le panneau
	 * ParameterFilteredTable -> ajoute la parametre au tableau des data ou
	 * ouvre la liste des SubParameters {@inheritDoc}
	 */
	@Override
	protected void selectAction() {

		TypeData selectedTypeData;

		int index = myFiltreTable.getSelectionIndex();
		selectedTypeData = listeFiltree.get(index);

		DataListTypeEnum typeToUse = RulesManager.getInstance().getMyDataListType();
		if (typeToUse == DataListTypeEnum.P011_VIA_P021 || typeToUse == DataListTypeEnum.P011_VIA_P091) {
			SubParameterFilteredTable subTable = new SubParameterFilteredTable(selectedTypeData, typeToUse);
			subTable.open(this.shell);
			if (subTable.isValidated())
				selectedTypeData = subTable.getSelectedData();
			else
				return;
		}

		// ajout d'un parameter dans l'onglet Data
		Utils.invalidEtapes();

		// Try to get Unit to selected typeData if P01 via P02 with BODC webservice
		if (selectedTypeData!= null && RulesManager.getInstance().isDataListTypeP011viaP021()) {

			SdnVocabularyManager sdnVocabsMgr = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
			try {
				IElement unitEl = sdnVocabsMgr.getP06OfP01(selectedTypeData.getEntryKeyP011());

				if (unitEl != null) {
					TypeUnit typeUnit = new TypeUnit(unitEl);
					selectedTypeData.setUnite(typeUnit.getTerm());
					selectedTypeData.setP06Label(typeUnit.getTerm());
					selectedTypeData.setUniteCode(typeUnit.getUnitKey());
				}		
			} catch (VocabularyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}

		// test avant ajout pour doublons
		Boolean duplicateTypeDataFound = false;
		TypeData dupliactedTypeData = null;
		// 47818 & 48779 do not allow duplicate if Medatlas & P09 or P01 via P09 (different P01 with same P09)
		if (!myTableDataRef.getTypes().isEmpty() && (RulesManager.getInstance().isConversionToMedatlas() 
				// FIXME pas de P01 via P09 en MEDATLAS ??
				&& (RulesManager.getInstance().isDataListTypeP091() || RulesManager.getInstance().isDataListTypeP011viaP091()))) {
			for (TypeData typeData : myTableDataRef.getTypes()) {
				String codeModele, codeActuel;

				// get the P09 code
				codeActuel = selectedTypeData.getEntryKey();
				codeModele = typeData.getEntryKey();

				if (codeModele.equals(codeActuel)) {
					duplicateTypeDataFound = true;
					dupliactedTypeData = typeData;
					break;
				}

			}

			if (duplicateTypeDataFound == false) {
				myTableDataRef.addType(new TypeData(selectedTypeData));

				// Suppression message erreur FAE 3752
				Utils.messageErreur(""); //$NON-NLS-1$
			} else {
				if (RulesManager.getInstance().isDataListTypeP091()) {
					Utils.messageErreur(Messages.error_table_parameters + " (P09 code = "+ dupliactedTypeData.getEntryKey()+")");
				} else if (RulesManager.getInstance().isDataListTypeP011viaP091()) {
					Utils.messageErreur(Messages.error_table_parameters + " (P09 code = "+ dupliactedTypeData.getEntryKey() +", P01 codes = "+dupliactedTypeData.getEntryKeyP011()+" & "+selectedTypeData.getEntryKeyP011()+")");
				}
				
			}
		} else {
			myTableDataRef.addType(new TypeData(selectedTypeData));
		}

		// refresh de la table
		myTableDataRef.refreshTable();

		shell.close();
	}
}
