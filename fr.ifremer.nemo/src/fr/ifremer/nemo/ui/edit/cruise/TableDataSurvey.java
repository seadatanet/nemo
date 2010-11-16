package fr.ifremer.nemo.ui.edit.cruise;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.tables.DatatypeFilteredTable;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSDataType;

/**
 * 
 * Description: Table affichée dans l'onglet Cruise, section "Type of data"
 * @author sbregent
 *
 */
public class TableDataSurvey
{

    public static Table tableTypeSurvey;

    public static Table filtreTable;

    public static int ligneSelec, colonneSelec;

    public static int[] lignesSelec;

    // public static String[][]tableTypes;
    public static ArrayList<TypeSurvey> types, listeFiltree, typesWS;

    private static Composite sectionClientType;

    public static void go(Composite parent)
    {
        sectionClientType = parent;
        initTypes();
        creationTable();
    }

    public static void setSectionClientType(Composite parent)
    {
        sectionClientType = parent;
    }

    public static void creationTable()
    {
        final Table table;

        TableDataSurvey.tableTypeSurvey =
            new Table(sectionClientType, SWT.MULTI | SWT.FULL_SELECTION
                    | SWT.BORDER);

        table = TableDataSurvey.tableTypeSurvey;

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        final TableColumn tableColumn = new TableColumn(table, SWT.NONE);

        tableColumn.setText(Messages.table_label_code.toUpperCase());

        final TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);

        tableColumn1.setText(Messages.table_label_number);

        final TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);

        tableColumn2.setText(Messages.table_label_qc);

        final TableColumn tableColumn3 = new TableColumn(table, SWT.READ_ONLY);
        // mise e zero de la largeur de la colonne avec isSensor() pour ne pas
        // qu'on puisse la modifier!!

        tableColumn3.setText(Messages.table_label_sensor);

        table.addControlListener(new ControlListener() {

            public void controlMoved(ControlEvent e)
            {
            }

            public void controlResized(ControlEvent e)
            {

                int w = ((Table) e.getSource()).getSize().x;
                int w1 = 60;
                int w2 = (w - (w1 * 3) - 35);
                tableColumn.setWidth(w2);
                tableColumn1.setWidth(w1);
                tableColumn2.setWidth(w1);
                tableColumn3.setWidth(w1);

            }

        });

        initTable();
    }

    public static void initTypes()
    {

        // initialisation de la ArrayList des types des donnees e partir du
        // datamodel
        types = new ArrayList<TypeSurvey>();
        typesWS = new ArrayList<TypeSurvey>();

        int taille = NemoLists.getDataTypes().length;

        for (int i = 0; i < taille; i++)
        {
            WSDataType wsDatatype = new WSDataType();
            wsDatatype = NemoLists.getDataTypes()[i];

            // new type
            TypeSurvey type = new TypeSurvey();

            // parametres
            type.setCode(wsDatatype.getCode() + Global.SEP
                    + wsDatatype.getLibelle());
            type.setQc(Global.NO);

            type.setSensor(wsDatatype.isSensor());
            // ajout dans liste
            typesWS.add(type);
        }

    }

    public static void initTable()
    {

        /*
         * Remplir la table avec les tableItems avant de linker les editors !
         * 
         */

        final Table table;
        table = TableDataSurvey.tableTypeSurvey;

        // REMPLISSAGE temporaire de la table
        initTableData();

        // LINK des editors

        // acces direct aux valeurs des colonnes des items

        final TableEditor editor = new TableEditor(table);

        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        /*
         * Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
         * table.setSize(size);
         */table.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event)
            {

                // calcul du numero de ligne selectionne

                Point pt = new Point(event.x, event.y);
                TableItem item = table.getItem(pt);

                if (item == null)
                    return;

                // edition directe de l'element selectionne

                Rectangle clientArea = table.getClientArea();
                int index = table.getTopIndex();

                for (int i = 0; i < table.getColumnCount(); i++)
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt))
                    {
                        int indexTmp = table.indexOf(item);
                        ligneSelec = indexTmp;
                        colonneSelec = i;
                    }
                }

                while (index < table.getItemCount())
                {
                    boolean visible = false;
                    final TableItem itemTmp = table.getItem(index);

                    for (int i = 0; i < table.getColumnCount(); i++)
                    {
                        Rectangle rect = itemTmp.getBounds(i);
                        if (rect.contains(pt))
                        {
                            final int column = i;
                            final Text text = new Text(table, SWT.NONE);
                            Listener textListener = new Listener() {
                                public void handleEvent(final Event e)
                                {
                                    switch (e.type)
                                    {
                                        case SWT.FocusOut:
                                            itemTmp.setText(column, text
                                                .getText());
                                            text.dispose();
                                        break;
                                        case SWT.Traverse:
                                            switch (e.detail)
                                            {
                                                case SWT.TRAVERSE_RETURN:
                                                    itemTmp.setText(column,
                                                        text.getText());

                                                case SWT.TRAVERSE_ESCAPE:
                                                    text.dispose();
                                                    e.doit = false;
                                            }
                                        break;
                                    }
                                }
                            };
                            text.addListener(SWT.FocusOut, textListener);
                            text.addListener(SWT.Traverse, textListener);
                            editor.setEditor(text, itemTmp, i);
                            text.setText(itemTmp.getText(i));
                            text.selectAll();

                            // invalidation de l'etape quand modif
                            Utils.invalidEtapes();

                            text.setFocus();
                            return;
                        }
                        if (!visible && rect.intersects(clientArea))
                        {
                            visible = true;
                        }
                    }
                    if (!visible)
                        return;
                    index++;
                }
            }
        });

        // creation du menu
        Menu menu = new Menu(table.getShell(), SWT.POP_UP);

        MenuItem itemList = new MenuItem(menu, SWT.NONE);
        itemList.setText(Messages.menu_label_list);

        MenuItem itemUp = new MenuItem(menu, SWT.NONE);
        itemUp.setText(Messages.menu_label_upType);

        MenuItem itemDown = new MenuItem(menu, SWT.NONE);
        itemDown.setText(Messages.menu_label_downType);

        MenuItem itemDel = new MenuItem(menu, SWT.NONE);
        itemDel.setText(Messages.menu_label_removeType);

        table.setMenu(menu);

        // ajouter des listeners sur les menu items ^^

        itemList.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                // System.out.println("selection liste");
                Nemo.getInstance().getViewMain().getMyFilteredTableFactory().go(sectionClientType, Global.DATATYPE, null);

            }
        });
        itemUp.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                // System.out.println("up");
                monterEltTableData(ligneSelec);
            }
        });
        itemDown.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                // System.out.println("down");
                descendreEltTableData(ligneSelec);
            }
        });
        itemDel.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                // System.out.println("suppression");
                // supprEltTableData(ligneSelec);
                supprEltTableData(table.getSelectionIndices());
                // remplirTableData();
            }
        });

        Utils.forceMenu(table, menu);

        GridDataFactory.fillDefaults().grab(true, false).applyTo(table);
        table.setLayout(new FillLayout());
        table.getParent().layout(true);
    }

    public static void initTableData()
    {
        // creation des TableItems e partir de la ArrayList types

        int nbre;
        nbre = types.size();

        Table table;
        table = TableDataSurvey.tableTypeSurvey;

        // nouvelle creation des items
        for (int i = 0; i < nbre; i++)
        {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[] {types.get(i).getCode(),
                    String.valueOf(types.get(i).getNombre()),
                    types.get(i).getQc(),
                    String.valueOf(types.get(i).getSensor())});

        }

    }

    public static void razTableData()
    {
        // effacement de tous les items de la table

        Table table;

        table = TableDataSurvey.tableTypeSurvey;

        table.dispose();

    }

    public static void nouvelElt()
    {
        // rajoute une ligne en derniere position de la table

        TypeSurvey type = new TypeSurvey();
        types.add(type);

        majTypeData();

        majIHM();

    }

    public static void monterEltTableData(int indice)
    {
        // on monte d'un cran l'element e la position indice
        // swap (indice-1) <-> indice
        // 

        // On teste si ce n'est pas le premier element
        if (indice > 0)
        {
            int nbre;
            ArrayList<TypeSurvey> listeTempo = new ArrayList<TypeSurvey>();
            ;
            nbre = types.size();

            // mise e jour prealable
            majTypeData();

            // parcours de la liste
            for (int i = 0; i < (indice - 1); i++)
            {
                listeTempo.add(types.get(i));
            }
            // swap des 2
            listeTempo.add(types.get(indice));
            listeTempo.add(types.get(indice - 1));

            // fin de parcours
            for (int i = (indice + 1); i < nbre; i++)
            {
                listeTempo.add(types.get(i));
            }

            // effacement de la liste principale avant re-remplissage
            for (int i = 0; i < nbre; i++)
            {
                types.remove(0);
            }

            // re-remplissage de la liste principale
            nbre = listeTempo.size();
            for (int i = 0; i < nbre; i++)
            {
                types.add(listeTempo.get(i));
            }

            // razTableData
            razTableData();

            // refresh de la table
            // initTable();
            creationTable();
        }
    }

    public static void descendreEltTableData(int indice)
    {
        // on descend d'un cran l'element e la position indice
        // swap (indice-1) <-> indice

        int nbre;
        nbre = types.size();

        if (indice < nbre - 1)
        {
            ArrayList<TypeSurvey> listeTempo = new ArrayList<TypeSurvey>();

            // mise e jour prealable
            majTypeData();

            // parcours de la liste
            for (int i = 0; i < indice; i++)
            {
                listeTempo.add(types.get(i));
            }
            // swap des 2
            listeTempo.add(types.get(indice + 1));
            listeTempo.add(types.get(indice));

            // fin de parcours
            for (int i = (indice + 2); i < nbre; i++)
            {
                listeTempo.add(types.get(i));
            }

            // effacement de la liste principale avant re-remplissage
            for (int i = 0; i < nbre; i++)
            {
                types.remove(0);
            }

            // re-remplissage de la liste principale
            nbre = listeTempo.size();
            for (int i = 0; i < nbre; i++)
            {
                types.add(listeTempo.get(i));
            }

            // razTableData
            razTableData();

            // refresh de la table
            creationTable();
        }
    }

    public static void supprEltTableData(int[] indice)
    {
        // suppression de la ligne selectionnee
        int i;

        // d'abord mise e jour des donnees
        majTypeData();

        // on supprime l'element e l'indice donne
        for (i = indice.length - 1; i > -1; i--)
        {
            types.remove(indice[i]);
        }

        majIHM();

    }

    public static void majTypeData()
    {
        // on met e jour tous les types de donnees dans l'ArrayList global
        // e partir des valeurs de la table

        int nbre;
        // types = null;
        // types = new ArrayList<TypeSurvey>();
        try
        {
            if (TableDataSurvey.tableTypeSurvey != null
                    && !TableDataSurvey.tableTypeSurvey.isDisposed())
            {
                nbre = TableDataSurvey.tableTypeSurvey.getItemCount();
                for (int i = 0; i < nbre; i++)
                {

                    types.get(i).setCode(
                        TableDataSurvey.tableTypeSurvey.getItem(i).getText(0));
                    if (TableDataSurvey.tableTypeSurvey.getItem(i).getText(1)
                        .equals(Global.EMPTY_STRING))
                    {
                        types.get(i).setNombre(0);
                    }
                    else
                    {
                        types.get(i).setNombre(
                            Integer.valueOf(TableDataSurvey.tableTypeSurvey
                                .getItem(i).getText(1)));
                    }
                    types.get(i).setQc(
                        TableDataSurvey.tableTypeSurvey.getItem(i).getText(2));
                    types.get(i).setSensor(
                        Boolean.parseBoolean(TableDataSurvey.tableTypeSurvey
                            .getItem(i).getText(3)));

                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // System.out.println("maj types data : ok");

    }

    public static void majIHM()
    {

        // razTableData
        razTableData();

        // refresh de la table
        creationTable();

        Composite c = Nemo.getInstance().getViewMain().getEditCruiseForm().sectionType.getSection();
        while (c != null)
        {
            // c.setRedraw(false);
            c = c.getParent();
            if (c instanceof ScrolledForm)
            {
                break;
            }
        }
        c = Nemo.getInstance().getViewMain().getEditCruiseForm().sectionType.getSection();
        while (c != null)
        {
            // c.layout(true);
            c = c.getParent();
            if (c instanceof ScrolledForm)
            {
                ((ScrolledForm) c).reflow(true);
                break;
            }
        }
        c = Nemo.getInstance().getViewMain().getEditCruiseForm().sectionType.getSection();
        while (c != null)
        {
            // c.setRedraw(true);
            c = c.getParent();
            if (c instanceof ScrolledForm)
            {
                break;
            }
        }
    }

    public static void ajouterCode(int indice)
    {
        // 

    }

}
