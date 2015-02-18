/* ============================================================================
 * Nom du fichier : ParameterFilteredTable.java
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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.data.TypeInstrument;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: TODO
 * 
 * @author ycaudal
 * 
 */
public class InstrumentFilteredTable extends AbstractFilteredTable
{
	/**
	 * la table permettant d'afficher les parametres
	 */
	private Table myFiltreTable;

	/**
     *  reference sur la tableData (de l'onglet data) depuis laquelle cette table a été ouverte
	 */
    private final TableData myTableDataRef;

	/**
	 * copie de la liste des parametres typesWS pouvant être filtrée
	 */
	private ArrayList<TypeInstrument> listeFiltree;

	/**
	 * @param comboRecherche
	 */
	public InstrumentFilteredTable()
	{
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
		myFiltreTable = new Table(top, SWT.FULL_SELECTION);

		myFiltreTable.setHeaderVisible(true);
		myFiltreTable.setLinesVisible(true);
		myFiltreTable.setBounds(new Rectangle(3, 45, 298, 237));

		// creation des colonnes...
		TableColumn tableColumn = new TableColumn(myFiltreTable, SWT.NONE);
		tableColumn.setWidth(80);
		tableColumn.setText("ID");// TODO
		
		TableColumn tableColumn2 = new TableColumn(myFiltreTable, SWT.NONE);
		tableColumn2.setWidth(300);
		tableColumn2.setText("label");// TODO
		

		for (int i = 0; i < listeFiltree.size(); i++)
		{
			TableItem item = new TableItem(myFiltreTable, SWT.NONE);
            item.setText(new String[] {
					listeFiltree.get(i).getKey(),
					listeFiltree.get(i).getTerm()
					});
		}
	}

	/**
	 * recharge la liste des paramètres filtrée depuis la liste des typesWS
	 * (= annule le filtre)
	 */
	private void reloadListeFiltree()
	{
		listeFiltree = new ArrayList<TypeInstrument>();
		// liste filtree initialisee e typesWS au depart
		int taille = myTableDataRef.getTypesL22().size();
		for (int i = 0; i < taille; i++)
		{
			listeFiltree.add(myTableDataRef.getTypesL22().get(i));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void filterAction(String chaine)
	{

		ArrayList<TypeInstrument> origine;
		ArrayList<TypeInstrument> filtree;
		String regexp;

		filtree = new ArrayList<TypeInstrument>();
		origine = myTableDataRef.getTypesL22();

		// remplissage de la liste filtree
		for (int i = 0; i < origine.size(); i++)
		{
			regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
			Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(origine.get(i).getTerm());
			if (m.matches())
			{
				// ajout dans la liste filtree
				filtree.add(origine.get(i));
			}
		}
		listeFiltree = filtree;
		// affichage de la liste filtree
		razTable(myFiltreTable);
		createTable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDialogTitle()
	{
		return Messages.edit_data_L33;
	}

	/**
     * action appelée lorsqu'un parametre est selectionné dans le panneau ParameterFilteredTable
     * -> ajoute la parametre au tableau des data ou ouvre la liste des SubParameters
     * {@inheritDoc}
	 */
	@Override
	protected void selectAction()
	{

		TypeInstrument type;

		int index = myFiltreTable.getSelectionIndex();
		type = listeFiltree.get(index);

		// reinit validation
		Utils.invalidEtapes();

		myTableDataRef.setInstrument(type);
		myTableDataRef.majTypeData();

		// refresh de la table
		myTableDataRef.razAndRecreateTable();
		shell.close();
	}
}
