package fr.ifremer.nemo.ui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.config.EdmoHandler;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.ui.edit.cruise.TableDataSurvey;
import fr.ifremer.nemo.ui.edit.cruise.TableWS_Survey;
import fr.ifremer.nemo.ui.edit.cruise.TypeSurvey;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.ui.tables.DatacentreFilteredTable;
import fr.ifremer.nemo.ui.tables.DatatypeFilteredTable;
import fr.ifremer.nemo.ui.tables.IdEDMOFilteredTable;
import fr.ifremer.nemo.ui.tables.ParameterFilteredTable;
import fr.ifremer.nemo.ui.tables.RegionFilteredTable;
import fr.ifremer.nemo.ui.tables.ShipFilteredTable;
import fr.ifremer.nemo.ui.tables.WSSurveyFilteredTable;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class FiltrageTableType{
    
    public static int listeOuverte;

    public static Composite top;

    public static Table filtreTableData, filtreTableSurvey;

    public static Combo comboRecherche;

    public static Text filtreText;

    public static Shell shell;

    public static Label labelText;

    public static Button filtreButtonSelectionner;

    public static Button filtreButtonCancel;

    public static String codeSelected;
    
    public static void go(Composite parent, String table, Combo comboRecherche){
        
        if (table.equals(Global.IDEDMO)){
            new IdEDMOFilteredTable(comboRecherche).open(parent);
        }
        else if (table.equals(Global.WSSURVEY)){
            new WSSurveyFilteredTable(comboRecherche).open(parent);
            }
        else if (table.equals(Global.DATATYPE)){
            new DatatypeFilteredTable(comboRecherche).open(parent);
            }
        else if (table.equals(Global.PARAMETER)){
            new ParameterFilteredTable(comboRecherche).open(parent);
            }
        else if (table.equals(Global.SHIP)){
            new ShipFilteredTable(comboRecherche).open(parent);
            }
        else if (table.equals(Global.DATACENTRE)){
            new DatacentreFilteredTable(comboRecherche).open(parent);
            }
        else if (table.equals(Global.REGION)){
            new RegionFilteredTable(comboRecherche).open(parent);
            }
    }
    
    public static void creationTableData(){
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

        for (int i = 0; i < nbre; i++){
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[] {TableData.listeFiltree.get(i).getCode(),
                                       TableData.listeFiltree.get(i).getUnite()});
        }
    }
    
    public static void creationTableEdmo(){
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

    public static void creationTableBODC(){
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

        for (int i = 0; i < nbre; i++){
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(NemoLists.listeFiltree.get(i));
        }
    }

    public static void creationTableSurvey(){
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

        for (int i = 0; i < nbre; i++){
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[] {
                    TableDataSurvey.listeFiltree.get(i).getCode(),
                    "" + TableDataSurvey.listeFiltree.get(i).getNombre(), //$NON-NLS-1$
                    TableDataSurvey.listeFiltree.get(i).getQc()});
        }
    }

    public static void filtrageListeData(String chaine){

        ArrayList<TypeData> origine, filtree;
        int nbre;
        String regexp;

        filtree = new ArrayList<TypeData>();
        origine = TableData.typesWS;

        nbre = origine.size();

        // remplissage de la liste filtree
        for (int i = 0; i < nbre; i++){
            regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
            Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(origine.get(i).getCode());
            if (m.matches()){
                // ajout dans la liste filtree
                filtree.add(origine.get(i));
            }
        }

        TableData.listeFiltree = filtree;
        // affichage de la liste filtree

        razTable(TableData.filtreTable);
        creationTableData();
    }

    public static void filtrageListeEdmo(String chaine){

        ArrayList<String> origine, filtree;
        int nbre;
        String regexp;

        filtree = new ArrayList<String>();
        origine = EdmoHandler.listeOrigine;

        nbre = origine.size();

        // remplissage de la liste filtree
        for (int i = 0; i < nbre; i++){
            regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$

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
        creationTableEdmo();
    }

    public static void filtrageListeBODC(String chaine, String liste){

        ArrayList<String> origine, filtree;
        int nbre;
        String regexp;

        filtree = new ArrayList<String>();
        origine = new ArrayList<String>();
        if (liste.equals(Global.SHIP)){
            origine = NemoLists.listeNavire;
        }
        else if (liste.equals(Global.DATACENTRE)){
            origine = NemoLists.listeCentreDonnees;
        }
        else if (liste.equals(Global.REGION)){
            origine = NemoLists.listeRegion;
        }

        nbre = origine.size();

        // remplissage de la liste filtree
        for (int i = 0; i < nbre; i++){

            regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
            Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(origine.get(i));

            if (m.matches()){
                // ajout dans la liste filtree
                filtree.add(origine.get(i));
            }
        }

        NemoLists.listeFiltree = filtree;
        // affichage de la liste filtree

        razTable(NemoLists.filtreTable);
        creationTableBODC();
    }

    public static void filtrageListeDataSurvey(String chaine){
        ArrayList<TypeSurvey> origine, filtree;
        int nbre;
        String regexp;

        filtree = new ArrayList<TypeSurvey>();
        origine = TableDataSurvey.typesWS;

        nbre = origine.size();

        // remplissage de la liste filtree
        for (int i = 0; i < nbre; i++){

            regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
            Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(origine.get(i).getCode());
            if (m.matches()){
                // ajout dans la liste filtree
                filtree.add(origine.get(i));
            }
        }

        TableDataSurvey.listeFiltree = filtree;
        // affichage de la liste filtree

        razTable(TableDataSurvey.filtreTable);
        creationTableSurvey();
    }

    public static void filtrageListeWS_Survey(String chaine){
        ArrayList<String> origine, filtree;
        int nbre;
        String regexp;

        filtree = new ArrayList<String>();
        origine = TableWS_Survey.refsWS;

        nbre = origine.size();

        // remplissage de la liste filtree
        for (int i = 0; i < nbre; i++){
            regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
            Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(origine.get(i));
            if (m.matches()){
                // ajout dans la liste filtree
                filtree.add(origine.get(i));
            }
        }

        TableWS_Survey.listeFiltree = filtree;
        // affichage de la liste filtree

        razTable(TableWS_Survey.filtreTable);
        creationTableWS_Survey();
    }

    public static void razTable(Table table){
        // maj
        table.dispose();
    }

    public static void creationTableWS_Survey(){
        final Table table;
        int nbre;

        TableWS_Survey.filtreTable = new Table(top, SWT.FULL_SELECTION);
        table = TableWS_Survey.filtreTable;
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setBounds(new Rectangle(3, 45, 298, 237));

        nbre = TableWS_Survey.listeFiltree.size();
        // creation des colonnes...
        TableColumn tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setWidth(290);
        tableColumn.setText(Messages.table_label_refName);

        for (int i = 0; i < nbre; i++){
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[] {TableWS_Survey.listeFiltree.get(i)});
        }

        // ajout listeners sur la table
        table.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event){

            }
        });

    }

}
