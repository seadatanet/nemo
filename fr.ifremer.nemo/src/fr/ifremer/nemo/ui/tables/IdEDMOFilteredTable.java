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

import fr.ifremer.nemo.config.EdmoHandler;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Messages;

public class IdEDMOFilteredTable extends AbstractFilteredTable
{

    /**
     * @param comboRecherche
     */
    public IdEDMOFilteredTable(Combo comboRecherche)
    {
        _comboRecherche = comboRecherche;
    }

    @Override
    protected void createDataList()
    {
        EdmoHandler.listeFiltree = new ArrayList<String>();
        EdmoHandler.listeFiltree.addAll(EdmoHandler.listeOrigine);
    }

    @Override
    protected void createTable()
    {
        final Table table;
        int nbre;

        EdmoHandler.filtreTable = new Table(top, SWT.FULL_SELECTION);

        table = EdmoHandler.filtreTable;
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setBounds(new Rectangle(3, 45, 298, 237));

        nbre = EdmoHandler.listeFiltree.size();
        // creation des colonnes...
        TableColumn tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setWidth(280);
        tableColumn.setText(Messages.table_label_codeName);

        for (int i = 0; i < nbre; i++){
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(EdmoHandler.listeFiltree.get(i));
        }
    }

    @Override
    protected void filterAction(String value)
    {

        ArrayList<String> origine, filtree;
        int nbre;
        String regexp;

        filtree = new ArrayList<String>();
        origine = EdmoHandler.listeOrigine;

        nbre = origine.size();

        // remplissage de la liste filtree
        for (int i = 0; i < nbre; i++){
            regexp = "^.*" + value + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$

            Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(origine.get(i));

            if (m.matches()){
                // ajout dans la liste filtree
                filtree.add(origine.get(i));
            }
        }

        EdmoHandler.listeFiltree = filtree;
        // affichage de la liste filtree

        razTable(EdmoHandler.filtreTable);
        createTable();
    }

    @Override
    protected String getDialogTitle()
    {
       return Messages.table_edmo;
    }

    @Override
    protected void selectAction()
    {
        Utils.selectComboValeur(_comboRecherche,
            EdmoHandler.filtreTable
                .getItem(
                    EdmoHandler.filtreTable
                        .getSelectionIndex()).getText());
        shell.close();
    }

}
