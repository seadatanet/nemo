/* ============================================================================
 * Nom du fichier	: RegionFilteredTable.java
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

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: TODO
 * 
 * @author ycaudal
 * 
 */
public class RegionFilteredTable extends AbstractFilteredTable
{

    /**
     * @param comboRecherche
     */
    public RegionFilteredTable(Combo comboRecherche)
    {
        _comboRecherche = comboRecherche;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createDataList()
    {
        NemoLists.listeFiltree = new ArrayList<String>();
        NemoLists.listeFiltree.addAll(NemoLists.listeRegion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createTable()
    {
        final Table table;
        int nbre;

        NemoLists.filtreTable = new Table(top, SWT.FULL_SELECTION);

        table = NemoLists.filtreTable;
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setBounds(new Rectangle(3, 45, 298, 237));

        nbre = NemoLists.listeFiltree.size();
        // creation des colonnes...
        TableColumn tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setWidth(280);
        tableColumn.setText(Messages.table_label_codeName);

        for (int i = 0; i < nbre; i++)
        {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(NemoLists.listeFiltree.get(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void filterAction(String value)
    {

        ArrayList<String> origine, filtree;
        int nbre;
        String regexp;

        filtree = new ArrayList<String>();
        origine = new ArrayList<String>();

        origine = NemoLists.listeRegion;

        nbre = origine.size();

        // remplissage de la liste filtree
        for (int i = 0; i < nbre; i++)
        {

            regexp = "^.*" + value + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
            Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(origine.get(i));

            if (m.matches())
            {
                // ajout dans la liste filtree
                filtree.add(origine.get(i));
            }
        }

        NemoLists.listeFiltree = filtree;
        // affichage de la liste filtree

        razTable(NemoLists.filtreTable);
        createTable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle()
    {
        return Messages.table_region;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void selectAction()
    {
        Utils.selectComboValeur(_comboRecherche, NemoLists.filtreTable.getItem(
            NemoLists.filtreTable.getSelectionIndex()).getText());
        shell.close();
    }

}
