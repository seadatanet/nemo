/* ============================================================================
 * Nom du fichier	: DatatypeFilteredTable.java
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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
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
public class DatatypeFilteredTable extends AbstractFilteredTable
{

    /**
     * @param comboRecherche
     */
    public DatatypeFilteredTable(CCombo comboRecherche)
    {
        _comboRecherche = comboRecherche;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createDataList()
    {
        // init des listes filtrees survey + data
        TableDataSurvey.listeFiltree = new ArrayList<TypeSurvey>();

        // init de la liste des references campagne e partir des
        // listesWS
        TableWS_Survey.surveysWS = NemoLists.getSurveys();

        // remplissage des noms + ref
        // liste filtree initialisee e surveysWS au depart
        TableWS_Survey.listeFiltree = new ArrayList<String>();
        TableWS_Survey.refsWS = new ArrayList<String>();
        for (int i = 0; i < TableWS_Survey.surveysWS.length; i++)
        {
            TableWS_Survey.refsWS.add(TableWS_Survey.surveysWS[i]
                .getReference()
                    + Global.SEP + TableWS_Survey.surveysWS[i].getName());
            TableWS_Survey.listeFiltree.add(TableWS_Survey.surveysWS[i]
                .getReference()
                    + Global.SEP + TableWS_Survey.surveysWS[i].getName());
        }

        // liste filtree initialisee e typesWS au depart
        try
        {
            int taille = TableDataSurvey.typesWS.size();

            for (int i = 0; i < taille; i++)
            {
                TableDataSurvey.listeFiltree
                    .add(TableDataSurvey.typesWS.get(i));
            }

            Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().initTypesWS();
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

        TableDataSurvey.filtreTable = new Table(top, SWT.FULL_SELECTION);
        nbre = TableDataSurvey.listeFiltree.size();

        table = TableDataSurvey.filtreTable;
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setBounds(new Rectangle(3, 45, 298, 237));

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        TableColumn tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setWidth(100);
        tableColumn.setText(Messages.table_label_code.toUpperCase());
        TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
        tableColumn1.setWidth(100);
        tableColumn1.setText(Messages.table_label_number);
        TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
        tableColumn2.setWidth(100);
        tableColumn2.setText(Messages.table_label_qc);

        for (int i = 0; i < nbre; i++)
        {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[] {
                    TableDataSurvey.listeFiltree.get(i).getCode(),
                    "" + TableDataSurvey.listeFiltree.get(i).getNombre(), //$NON-NLS-1$
                    TableDataSurvey.listeFiltree.get(i).getQc()});
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void filterAction(String value)
    {
        ArrayList<TypeSurvey> origine, filtree;
        int nbre;
        String regexp;

        filtree = new ArrayList<TypeSurvey>();
        origine = TableDataSurvey.typesWS;

        nbre = origine.size();

        // remplissage de la liste filtree
        for (int i = 0; i < nbre; i++)
        {

            regexp = "^.*" + value + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
            Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(origine.get(i).getCode());
            if (m.matches())
            {
                // ajout dans la liste filtree
                filtree.add(origine.get(i));
            }
        }

        TableDataSurvey.listeFiltree = filtree;
        // affichage de la liste filtree

        razTable(TableDataSurvey.filtreTable);
        createTable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle()
    {
        return Messages.table_dataTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void selectAction()
    {
        // ajout d'un dataType dans l'onglet Survey
        TypeSurvey typeSurvey = new TypeSurvey();

        int index = TableDataSurvey.filtreTable.getSelectionIndex();
        typeSurvey = TableDataSurvey.listeFiltree.get(index);

        // test avant ajout pour doublons
        int taille = TableDataSurvey.types.size();
        Boolean found = false;

        if (taille != 0)
        {
            for (int i = 0; i < taille; i++)
            {

                // split sur " - "
                Pattern p = Pattern.compile(Global.SEP);
                String mots[] = p.split(typeSurvey.getCode());
                String codeActuel = mots[0];

                String mots2[] =
                    p.split(TableDataSurvey.types.get(i).getCode());
                String codeModele = mots2[0];

                if (codeModele.equals(codeActuel))
                {
                    found = true;
                }
            }

            if (found == false)
            {
                TableDataSurvey.types.add(typeSurvey);
            }
            else
            {
                Utils.messageErreur(Messages.error_table_dataTypes);
            }
        }
        else
        {
            TableDataSurvey.types.add(typeSurvey);
        }

        TableDataSurvey.majTypeData();
        TableDataSurvey.majIHM();

    //    ViewMain.editCruiseForm.sectionType.getSection().pack();
    //    ViewMain.editCruiseForm.sectionType.getSection().redraw();

        shell.close();

    }

}
