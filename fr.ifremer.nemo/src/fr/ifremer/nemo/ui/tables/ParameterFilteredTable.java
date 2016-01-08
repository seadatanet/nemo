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
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Messages;

/**
 * Description:
 * 
 * @author ycaudal
 * 
 */
public class ParameterFilteredTable extends AbstractFilteredTable
{

	/**
     *  reference sur la tableData (de l'onglet data) depuis laquelle cette table a été ouverte
	 */
    private final TableData myTableDataRef;

	/**
	 * copie de la liste des parametres typesWS pouvant être filtrée
	 */
	private ArrayList<TypeData> listeFiltree;

	/**
	 * @param comboRecherche
	 */
	public ParameterFilteredTable(CCombo comboRecherche)
	{
		_comboRecherche = comboRecherche;
		myTableDataRef = Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createDataList()
	{
		// liste filtree initialisee e typesWS au depart
		reloadListeFiltree();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createTable()
	{
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
    protected void populateTableWithFilteredList(){
		for (int i = 0; i < listeFiltree.size(); i++)
		{
			TableItem item = new TableItem(myFiltreTable, SWT.NONE);
            item.setText(new String[] {listeFiltree.get(i).getFormattedCodeAndName(),
					listeFiltree.get(i).getUnite() });
		}
	}
	/**
	 * recharge la liste des paramètres filtrée depuis la liste des typesWS
	 * (= annule le filtre)
	 */
	private void reloadListeFiltree()
	{
		listeFiltree = new ArrayList<TypeData>();
		// liste filtree initialisee e typesWS au depart
		int taille = myTableDataRef.getTypesWS().size();
		for (int i = 0; i < taille; i++)
		{
			listeFiltree.add(myTableDataRef.getTypesWS().get(i));
		}
		
		Collections.sort(listeFiltree);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void filterAction(String chaine)
	{

		ArrayList<TypeData> origine, filtree;
		String regexp;

		filtree = new ArrayList<TypeData>();
		origine = myTableDataRef.getTypesWS();

		// remplissage de la liste filtree
		for (int i = 0; i < origine.size(); i++)
		{
			regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
			Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(origine.get(i).getFormattedCodeAndName());
			if (m.matches())
			{
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
	protected String getDialogTitle()
	{

		DataListTypeEnum listToUse = RulesManager.getInstance().getMyDataListType();
		String title = Messages.table_parameters;
		title += Global.SPACE;
		// title += listToUse.name();
		title += Global.SPACE;

		if (listToUse == DataListTypeEnum.P011_VIA_P021)
		{
			title += Messages.edit_data_P021;
		} else if (listToUse == DataListTypeEnum.P011_VIA_P091 || listToUse == DataListTypeEnum.P091)
		{
			title += Messages.edit_data_P091;
		}
		return title;
	}

	/**
     * action appelée lorsqu'un parametre est selectionné dans le panneau ParameterFilteredTable
     * -> ajoute la parametre au tableau des data ou ouvre la liste des SubParameters
     * {@inheritDoc}
	 */
	@Override
	protected void selectAction()
	{

		TypeData typeData;

		int index = myFiltreTable.getSelectionIndex();
		typeData = listeFiltree.get(index);

		DataListTypeEnum typeToUse = RulesManager.getInstance().getMyDataListType();
		if (typeToUse == DataListTypeEnum.P011_VIA_P021 || typeToUse == DataListTypeEnum.P011_VIA_P091)
		{
			SubParameterFilteredTable subTable = new SubParameterFilteredTable(typeData, typeToUse);
			subTable.open(this.shell);
			if (subTable.isValidated())
				typeData = subTable.getSelectedData();
			else
				return;
		}

		// ajout d'un parameter dans l'onglet Data
		Utils.invalidEtapes();

		// test avant ajout pour doublons
		int taille = myTableDataRef.getTypes().size();
		Boolean found = false;

		if (taille != 0)
		{
			for (int i = 0; i < taille; i++)
			{
				String codeModele, codeActuel;

				codeActuel = typeData.getCodeToUse();
				codeModele = myTableDataRef.getTypes().get(i).getCodeToUse();

				if (codeModele.equals(codeActuel))
				{
					found = true;
				}
			}

			if (found == false)
			{
				myTableDataRef.addType(new TypeData(typeData));

				// Suppression message erreur FAE 3752
				Utils.messageErreur(""); //$NON-NLS-1$
            }
            else
			{
				Utils.messageErreur(Messages.error_table_parameters);
			}
        }
        else
		{
			myTableDataRef.addType(new TypeData(typeData));
		}

		myTableDataRef.majTypeData();

		// refresh de la table
		myTableDataRef.razAndRecreateTable();

		shell.close();
	}
}
