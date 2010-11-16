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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.config.Configuration.DataListEnum;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: TODO
 * 
 * @author ycaudal
 * 
 */
public class ParameterFilteredTable extends AbstractFilteredTable
{

   
    /**
     * @param comboRecherche
     */
    public ParameterFilteredTable(Combo comboRecherche)
    {
        _comboRecherche = comboRecherche;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createDataList()
    {
        // liste filtree initialisee e typesWS au depart
        try
        {
//            TableData.initTypesWS();
            
            TableData.listeFiltree = new ArrayList<TypeData>();
            // liste filtree initialisee e typesWS au depart
            int taille = TableData.typesWS.size();
            for (int i = 0; i < taille; i++)
            {
                TableData.listeFiltree.add(TableData.typesWS.get(i));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createTable()
    {
        final Table table;
        int nbre;

        TableData.filtreTable = new Table(top, SWT.FULL_SELECTION);

        table = TableData.filtreTable;
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setBounds(new Rectangle(3, 45, 298, 237));

        nbre = TableData.listeFiltree.size();
        // creation des colonnes...
        TableColumn tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setWidth(200);
        tableColumn.setText(Messages.table_label_codeName);

        TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
        tableColumn2.setWidth(81);
        tableColumn2.setText(Messages.table_label_unit);

        for (int i = 0; i < nbre; i++)
        {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[] {TableData.listeFiltree.get(i).getCode(),
                    TableData.listeFiltree.get(i).getUnite()});
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void filterAction(String chaine)
    {

        ArrayList<TypeData> origine, filtree;
        int nbre;
        String regexp;

        filtree = new ArrayList<TypeData>();
        origine = TableData.typesWS;

        nbre = origine.size();

        // remplissage de la liste filtree
        for (int i = 0; i < nbre; i++)
        {
            regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
            Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(origine.get(i).getCode());
            if (m.matches())
            {
                // ajout dans la liste filtree
                filtree.add(origine.get(i));
            }
        }

        TableData.listeFiltree = filtree;
        // affichage de la liste filtree

        razTable(TableData.filtreTable);
        createTable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle()
    {
        return Messages.table_parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void selectAction()
    {     
              
      TypeData typeData;
      
      int index = TableData.filtreTable.getSelectionIndex();
      typeData = TableData.listeFiltree.get(index);
      Configuration config = Nemo.getInstance().getConfig();
      
      
      // Correction FAE 3760 si on charge un modele si on a utilisé liste P091 on continue à utiliser la liste P091
      boolean isTypeP011=false;
      DataListEnum currentType=config.getDataList();
      
      if (!TableData.types.isEmpty())
      {
          if (TableData.types.get(0).isP011())
          {
              isTypeP011=true;
              currentType= DataListEnum.P011_VIA_P091;
          }
      }
      
      
      if (isTypeP011)
      {
          SubParameterFilteredTable subTable = new SubParameterFilteredTable(typeData);
          subTable.open(this.shell);
          if (subTable.isValidated())
              typeData = subTable.getSelectedData();
          else
              return;
      }

        
        // ajout d'un parameter dans l'onglet Data
        Utils.invalidEtapes();


        // test avant ajout pour doublons
        int taille = TableData.types.size();
        Boolean found = false;

        if (taille != 0)
        {

            for (int i = 0; i < taille; i++)
            {
                String codeModele, codeActuel;

                // split sur " - "
                Pattern p = Pattern.compile(Global.SEP);
                String mots[] = p.split(typeData.getCode());
                codeActuel = mots[0];

                String mots2[] = p.split(TableData.types.get(i).getCode());
                codeModele = mots2[0];

                if (codeModele.equals(codeActuel))
                {
                    found = true;
                }
            }

            if (found == false)
            {
                TableData.types.add(new TypeData(typeData));
                // Suppression message erreur FAE 3752
                Utils.messageErreur("");
            }
            else
            {
                Utils.messageErreur(Messages.error_table_parameters);
            }
        }
        else
        {
            TableData.types.add(new TypeData(typeData));
        }

        TableData.majTypeData();

        // razTableData
        TableData.razTableData();

        // refresh de la table
        TableData.creationTable();

        shell.close();
    }

}
