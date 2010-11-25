/* ============================================================================
 * Nom du fichier	: ParameterFilteredTable.java
 * Auteur			: ycaudal
 * Date				: 22 déc. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.tables;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Configuration.DataListEnum;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.ui.edit.data.TypeUnit;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: TODO
 * 
 * @author ycaudal
 * 
 */
public class UnitFilteredTable extends AbstractFilteredTable
{
    /**
     * la table permettant d'afficher les parametres
     */  
    private Table myFiltreTable;
    
    /**
     *  reference sur la tableData (de l'onglet data) depuis laquelle cette table a été ouverte
     */
    private TableData myTableDataRef;
    
    /**
     * copie de la liste des parametres typesWS pouvant être filtrée
     */
    private ArrayList<TypeUnit> listeFiltree;
    /**
     * @param comboRecherche
     */
    public UnitFilteredTable()
    {
        myTableDataRef =  Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData();
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
        tableColumn.setWidth(200);
        tableColumn.setText(Messages.unitTable_label_unit);

        TableColumn tableColumn2 = new TableColumn(myFiltreTable, SWT.NONE);
        tableColumn2.setWidth(81);
        tableColumn2.setText(Messages.unitTable_label_unitAbbr);
        
        TableColumn tableColumn3 = new TableColumn(myFiltreTable, SWT.NONE);
        tableColumn3.setWidth(200);
        tableColumn3.setText(Messages.unitTable_label_unitDef);

        for (int i = 0; i < listeFiltree.size(); i++)
        {
            TableItem item = new TableItem(myFiltreTable, SWT.NONE);
            item.setText(new String[] {
                    listeFiltree.get(i).getTerm(),
                    listeFiltree.get(i).getTermAbbr(),
                    listeFiltree.get(i).getUntiDef()
                    });
        }
    }
    
    /**
     * recharge la liste des paramètres filtrée depuis la liste des typesWS
     * (= annule le filtre)
     */
    private void reloadListeFiltree()
    {
        listeFiltree = new ArrayList<TypeUnit>();
        // liste filtree initialisee e typesWS au depart
        int taille = myTableDataRef.getTypesP061().size();
        for (int i = 0; i < taille; i++)
        {
            listeFiltree.add(myTableDataRef.getTypesP061().get(i));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void filterAction(String chaine)
    {

        ArrayList<TypeUnit> origine;
        ArrayList<TypeUnit> filtree;
        String regexp;

        filtree = new ArrayList<TypeUnit>();
        origine = myTableDataRef.getTypesP061();


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
        //TODO BGT
        return Messages.edit_data_P061;
    }

    /**
     * action appelée lorsqu'un parametre est selectionné dans le panneau ParameterFilteredTable
     * -> ajoute la parametre au tableau des data ou ouvre la liste des SubParameters
     * {@inheritDoc}
     */
    @Override
    protected void selectAction()
    {     
              
      TypeUnit typeUnit;
      
      int index = myFiltreTable.getSelectionIndex();
      typeUnit = listeFiltree.get(index);
      
      DataListEnum typeToUse= Nemo.getInstance().getConfig().getDataListToUse(myTableDataRef);
      
        // reinit validation
        Utils.invalidEtapes();

        myTableDataRef.setUnit(typeUnit);
        myTableDataRef.majTypeData();

        // refresh de la table
        myTableDataRef.razAndRecreateTable();
        shell.close();
    }
}
