/*
 * ============================================================================
 * Nom du fichier : WSSurveyFilteredTable.java
 * Auteur : ycaudal
 * Date : 22 déc. 09
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.tables;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.cruise.TableDataSurvey;
import fr.ifremer.nemo.ui.edit.cruise.TableWS_Survey;
import fr.ifremer.nemo.ui.edit.cruise.TypeSurvey;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: TODO
 * 
 * @author ycaudal
 * 
 */
public class WSSurveyFilteredTable extends AbstractFilteredTable {

	/**
	 * @param comboRecherche
	 */
	public WSSurveyFilteredTable(CCombo comboRecherche) {
		// _comboRecherche = comboRecherche;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createDataList() {
		// init des listes filtrees survey + data
		TableDataSurvey.listeFiltree = new ArrayList<TypeSurvey>();

		// init de la liste des references campagne e partir des
		// listesWS
		TableWS_Survey.surveysWS = NemoLists.getSurveys();

		// remplissage des noms + ref
		// liste filtree initialisee e surveysWS au depart
		TableWS_Survey.listeFiltree = new ArrayList<String>();
		TableWS_Survey.refsWS = new ArrayList<String>();
		for (int i = 0; i < TableWS_Survey.surveysWS.length; i++) {
			TableWS_Survey.refsWS.add(
					TableWS_Survey.surveysWS[i].getReference() + Global.SEP + TableWS_Survey.surveysWS[i].getName());
			TableWS_Survey.listeFiltree.add(
					TableWS_Survey.surveysWS[i].getReference() + Global.SEP + TableWS_Survey.surveysWS[i].getName());
		}

		// liste filtree initialisee e typesWS au depart
		try {
			int taille = TableDataSurvey.typesWS.size();

			for (int i = 0; i < taille; i++) {
				TableDataSurvey.listeFiltree.add(TableDataSurvey.typesWS.get(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createTable() {

		super.createTable();
		// TableWS_Survey.filtreTable = new Table(top, SWT.FULL_SELECTION);
		// myFiltreTable = TableWS_Survey.filtreTable;
		// myFiltreTable.setHeaderVisible(true);
		// myFiltreTable.setLinesVisible(true);
		// myFiltreTable.setBounds(new Rectangle(3, 45, 298, 237));

		// creation des colonnes...
		TableColumn tableColumn = new TableColumn(myFiltreTable, SWT.NONE);
		tableColumn.setWidth(290);
		tableColumn.setText(Messages.table_label_refName);

		populateTableWithFilteredList();
		// ajout listeners sur la table
		// myFiltreTable.addListener(SWT.MouseDown, new Listener()
		// {
		// public void handleEvent(Event event)
		// {
		//
		// }
		// });

	}

	@Override
	protected void populateTableWithFilteredList() {
		int nbre = TableWS_Survey.listeFiltree.size();

		for (int i = 0; i < nbre; i++) {
			TableItem item = new TableItem(myFiltreTable, SWT.NONE);
			item.setText(new String[] { TableWS_Survey.listeFiltree.get(i) });
		}

		/**
		 * TODO BGT: le tableau plante si le nb de lignes est entre 8000 et
		 * 10000 (pourquoi???) il faudrait utiliser un table view. En attendant,
		 * onn rajoute des lignes vide pour combler jusqu'à 10000
		 */
		for (int i = nbre; i < 10000; i++) {
			TableItem item = new TableItem(myFiltreTable, SWT.NONE);
			item.setText("");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void filterAction(String value) {
		ArrayList<String> origine, filtree;
		int nbre;
		String regexp;

		filtree = new ArrayList<String>();
		origine = TableWS_Survey.refsWS;

		nbre = origine.size();

		// remplissage de la liste filtree
		for (int i = 0; i < nbre; i++) {
			regexp = "^.*" + value + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
			Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(origine.get(i));
			if (m.matches()) {
				// ajout dans la liste filtree
				filtree.add(origine.get(i));
			}
		}

		TableWS_Survey.listeFiltree = filtree;
		// affichage de la liste filtree

		// razTable(TableWS_Survey.filtreTable);
		// createTable();
		refreshTable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDialogTitle() {
		return Messages.table_survey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void selectAction() {

		// selection d'une reference campagne
		int index = myFiltreTable.getSelectionIndex();
		String reference = TableWS_Survey.listeFiltree.get(index);
		Pattern p = Pattern.compile(Global.SEP);

		String mots[] = p.split(reference);
		boolean ok = Nemo.getInstance().getViewMain().getEditCruiseForm().webService(mots[0]);
		if (ok) {
			Utils.messageOK(Messages.success_openSurveyDesc);
		}
		shell.close();

	}

}
