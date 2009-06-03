package fr.ifremer.nemo.ui.edit.data;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.FiltrageTableType;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSParameter;

public class TableData
{

    public static Table filtreTable;

    public static ArrayList<TypeData> types;
    public static ArrayList<TypeData> listeFiltree;
    public static ArrayList<TypeData> typesWS;

    public static void go()
    {
        initTypesWS();
        types = new ArrayList<TypeData>();
        if (!(ViewMain.tableData == null || ViewMain.tableData.isDisposed()))
        {
            razTableData();
        }
        creationTable();
    }

    public static void creationTable()
    {

        final Table table;

        ViewMain.tableData =
            new Table(ViewMain.compositeData, SWT.FULL_SELECTION | SWT.H_SCROLL);

        table = ViewMain.tableData;

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        TableColumn tableColumn0 = new TableColumn(table, SWT.NONE);
        tableColumn0.setWidth(110);
        tableColumn0.setText(Messages.table_label_code.toUpperCase());

        TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
        tableColumn1.setWidth(90);
        tableColumn1.setText(Messages.table_label_unit);

        TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
        tableColumn2.setWidth(100);
        tableColumn2.setText(Messages.label_conversion);
        tableColumn2.setAlignment(SWT.RIGHT);

        TableColumn tableColumn3 = new TableColumn(table, SWT.NONE);
        tableColumn3.setWidth(50);
        tableColumn3.setText(Messages.label_test);
        tableColumn3.setAlignment(SWT.RIGHT);

        TableColumn tableColumn4 = new TableColumn(table, SWT.NONE);
        tableColumn4.setWidth(50);
        tableColumn4.setText(Messages.label_start);
        tableColumn4.setAlignment(SWT.RIGHT);

        TableColumn tableColumn5 = new TableColumn(table, SWT.NONE);
        tableColumn5.setWidth(50);
        tableColumn5.setText(Messages.label_end);
        tableColumn5.setAlignment(SWT.RIGHT);

        TableColumn tableColumn6 = new TableColumn(table, SWT.NONE);
        tableColumn6.setWidth(100);
        tableColumn6.setText(Messages.label_format);
        tableColumn6.setAlignment(SWT.RIGHT);

        TableColumn tableColumn7 = new TableColumn(table, SWT.NONE);
        tableColumn7.setWidth(100);
        tableColumn7.setText(Messages.table_label_valueIn);
        tableColumn7.setAlignment(SWT.RIGHT);

        TableColumn tableColumn8 = new TableColumn(table, SWT.NONE);
        tableColumn8.setWidth(100);
        tableColumn8.setText(Messages.table_label_valueOut);
        tableColumn8.setAlignment(SWT.RIGHT);

        TableColumn tableColumn9 = new TableColumn(table, SWT.NONE);
        tableColumn9.setWidth(65);
        tableColumn9.setText(Messages.table_label_testIn);
        tableColumn9.setAlignment(SWT.RIGHT);

        TableColumn tableColumn10 = new TableColumn(table, SWT.NONE);
        tableColumn10.setWidth(65);
        tableColumn10.setText(Messages.table_label_testOut);
        tableColumn10.setAlignment(SWT.RIGHT);

        TableColumn tableColumn11 = new TableColumn(table, SWT.NONE);
        tableColumn11.setWidth(65);
        tableColumn11.setText(Messages.table_label_startFlag);
        tableColumn11.setAlignment(SWT.RIGHT);

        TableColumn tableColumn12 = new TableColumn(table, SWT.NONE);
        tableColumn12.setWidth(65);
        tableColumn12.setText(Messages.table_label_endFlag);
        tableColumn12.setAlignment(SWT.RIGHT);

        initTable();

        // agrandissement de la table
        ViewMain.compositeData.getShell().layout();

    }

    public static void initTypesWS()
    {

        // initialisation de la ArrayList des types des donnees e partir du
        typesWS = null;
        typesWS = new ArrayList<TypeData>();

        int taille = DataModel.listeWSParameters.length;

        for (int i = 0; i < taille; i++)
        {
            WSParameter wsParameter = new WSParameter();
            wsParameter = DataModel.listeWSParameters[i];

            // new type
            TypeData type = new TypeData();

            // parametres
            type.setCode(wsParameter.getCode() + Global.SEP
                    + wsParameter.getName());
            type.setUnite(wsParameter.getUnit());
            type.setFormat(wsParameter.getFormat());
            type.setEntree(Global.EMPTY_STRING);
            type.setSortie(wsParameter.getDefaultValue());

            // ajout dans liste
            typesWS.add(type);
        }
    }

    public static void fillTypes()
    {
        // initialisation de la ArrayList des types des donnees e partir du
        types = null;
        types = new ArrayList<TypeData>();

        for (TypeData type : typesWS)
        {
            // ajout systematique de PRES
            if (type.getCode().startsWith(Intern.paramPRES)
                    && ViewMain.radProfilVertical.getSelection())
            {
                // on rajoute PRES
                types.add(type);
            }
        }
        razTableData();
        creationTable();
    }

    public static void initTable()
    {

        /*
         * Remplir la table avec les tableItems avant de linker les editors !
         * 
         */

        final Table table;
        table = ViewMain.tableData;

        // REMPLISSAGE temporaire de la table
        initTableData();
        // remplirTableData();

        // LINK des editors

        // acces direct aux valeurs des colonnes des items
        final TableEditor editor = new TableEditor(table);

        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        table.setSize(size);
        table.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event)
            {
                if (event.button == 3)
                {
                    return;
                }
                // calcul du numero de ligne selectionne
                Point pt = new Point(event.x, event.y);
                TableItem item = table.getItem(pt);
                if (item == null)
                    return;

                // edition directe de l'element selectionne
                Rectangle clientArea = table.getClientArea();
                int index = table.getTopIndex();

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
        MenuItem itemInject = new MenuItem(menu, SWT.NONE);
        itemInject.setText(Messages.menu_label_setValue);
        itemInject.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                // table.forceFocus();
                Utils.injectData(ViewMain.textAreaData, 4, 5);

                majTypeData();
                razTableData();

                // refresh de la table
                creationTable();

                Utils.invalidEtapes();
            }
        });

        MenuItem itemMaj = new MenuItem(menu, SWT.NONE);
        itemMaj.setText(Messages.menu_label_updateTest);
        itemMaj.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                majChampTest();

            }
        });

        MenuItem itemList = new MenuItem(menu, SWT.NONE);
        itemList.setText(Messages.menu_label_paramList);
        itemList.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                FiltrageTableType.go(Global.PARAMETER);

            }
        });

        MenuItem itemUp = new MenuItem(menu, SWT.NONE);
        itemUp.setText(Messages.menu_label_upParam);
        itemUp.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                // System.out.println("up");
                monterEltTableData();

                Utils.invalidEtapes();
            }
        });

        MenuItem itemDown = new MenuItem(menu, SWT.NONE);
        itemDown.setText(Messages.menu_label_downParam);
        itemDown.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                descendreEltTableData();

                Utils.invalidEtapes();
            }
        });

        MenuItem itemDel = new MenuItem(menu, SWT.NONE);
        itemDel.setText(Messages.menu_label_removeParam);
        itemDel.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                supprEltTableData();
                Utils.invalidEtapes();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        MenuItem itemInjectFlag = new MenuItem(menu, SWT.NONE);
        itemInjectFlag.setText(Messages.menu_label_setFlag);
        itemInjectFlag.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                table.forceFocus();
                Utils.injectData(ViewMain.textAreaData, 11, 12);

                Utils.invalidEtapes();
            }
        });

        MenuItem itemRemoveFlag = new MenuItem(menu, SWT.NONE);
        itemRemoveFlag.setText(Messages.menu_label_deleteFlag);
        itemRemoveFlag.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                table.forceFocus();
                table.getItems()[table.getSelectionIndex()].setText(9,
                    Global.EMPTY_STRING);
                table.getItems()[table.getSelectionIndex()].setText(10,
                    Global.EMPTY_STRING);
                table.getItems()[table.getSelectionIndex()].setText(11,
                    Global.EMPTY_STRING);
                table.getItems()[table.getSelectionIndex()].setText(12,
                    Global.EMPTY_STRING);
                types.get(table.getSelectionIndex()).setDebutFlag(null);
                types.get(table.getSelectionIndex()).setFinFlag(null);

                Utils.invalidEtapes();
            }
        });
        table.setMenu(menu);

        Utils.forceMenu(table, menu);

        // agrandissement de la table
        ViewMain.compositeData.getParent().layout(true, true);
        table.setLayout(new FillLayout());
        table.redraw();
    }

    public static void initTableData()
    {
        // creation des TableItems e partir de la ArrayList types

        int nbre;
        nbre = types.size();

        Table table;
        table = ViewMain.tableData;

        // nouvelle creation des items
        for (int i = 0; i < nbre; i++)
        {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[] {types.get(i).getCode(),
                    types.get(i).getUnite(), types.get(i).getConversion(),
                    types.get(i).getTest(),
                    String.valueOf(types.get(i).getDebut()),
                    String.valueOf(types.get(i).getFin()),
                    types.get(i).getFormat(), types.get(i).getEntree(),
                    types.get(i).getSortie(), types.get(i).getTestIn(),
                    types.get(i).getTestOut(), types.get(i).getDebutFlag(),
                    types.get(i).getFinFlag()});
        }

    }

    public static void razTableData()
    {
        // effacement de tous les items de la table

        ViewMain.tableData.dispose();

    }

    public static void nouvelElt()
    {
        // rajoute une ligne en derniere position de la table

        TypeData type = new TypeData();
        types.add(type);

        // razTableData
        razTableData();

        // refresh de la table
        creationTable();

    }

    public static void monterEltTableData()
    {
        // on monte d'un cran l'element e la position indice
        // swap (indice-1) <-> indice
        // 

        // Utils.messageErreur(Global.EMPTY_STRING);
        int nbre;
        ArrayList<TypeData> listeTempo;
        nbre = types.size();

        int indice = ViewMain.tableData.getSelectionIndex();

        // si l'indice est 0, on ne fait rien
        if (indice != 0)
        {

            listeTempo = new ArrayList<TypeData>();

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
            creationTable();

        }

    }

    public static void descendreEltTableData()
    {
        // on descend d'un cran l'element e la position indice
        // swap (indice-1) <-> indice

        int indice = ViewMain.tableData.getSelectionIndex();

        // Si derniere ligne, on ne fait rien
        if (indice != ViewMain.tableData.getItemCount() - 1)
        {

            int nbre;
            ArrayList<TypeData> listeTempo;
            nbre = types.size();

            listeTempo = new ArrayList<TypeData>();

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

    public static void supprEltTableData()
    {
        // suppression de la ligne selectionnee
        int indice = ViewMain.tableData.getSelectionIndex();
        // d'abord mise e jour des donnees
        majTypeData();

        // on supprime l'element e l'indice donne
        // split sur " - "
        Pattern p = Pattern.compile(Global.SEP);
        String mots[] = p.split(types.get(indice).getCode());
        String code = mots[0];

        // pas forcement de - dans le nom du type
        if (!code.equals(Intern.paramPRES)
                || ViewMain.radSerieTempo.getSelection()
                || ViewMain.radTrajectoire.getSelection())
        {
            types.remove(indice);
        }
        else
        {
            Utils.messageErreurAppend(Messages.error_data_removePRES);
        }

        // razTableData
        razTableData();

        // refresh de la table
        creationTable();

    }

    public static void majChampTest()
    {

        // maj preventive de tout
        majTypeData();

        int nbre;
        String selection, ligne, resultat;
        double resultJEP;

        nbre = ViewMain.tableData.getItemCount();
        for (int i = 0; i < nbre; i++)
        {

            if ((!ViewMain.tableData.getItem(i).getText(4).equals(
                Global.EMPTY_STRING))
                    && (!ViewMain.tableData.getItem(i).getText(2).equals(
                        Global.EMPTY_STRING)))
            {
                // on a selectionne un debut et une fin et il y a une conversion
                // e faire

                // on recupere le data
                ligne =
                    Utils.lignesBrutes.get(Integer
                        .valueOf(ViewMain.editFileForm.sectionEnteteFichier
                            .getValue())
                            + DataModel.stationModel.getNumberOfLines()) + 1;
                selection =
                    ligne.substring(
                        Integer.valueOf(ViewMain.tableData.getItem(i)
                            .getText(4)) - 1,
                        Integer.valueOf(ViewMain.tableData.getItem(i)
                            .getText(5))).trim();

                // on formate puis on reaffiche la liste
                try
                {
                    String formatString =
                        ViewMain.tableData.getItem(i).getText(6);
                    String type =
                        formatString.substring(formatString.length() - 1);

                    if (selection.equals(ViewMain.tableData.getItem(i).getText(
                        7)))
                    {

                        // entree par defaut, le test est la valeur de sortie
                        // par defaut formatee
                        String fmOut = Global.EMPTY_STRING;
                        String outputDefaultValue =
                            ViewMain.tableData.getItem(i).getText(8);

                        // Float ou Decimal ?
                        if (type.equals("f") || type.equals("F")) { //$NON-NLS-1$//$NON-NLS-2$
                            fmOut =
                                String.format(java.util.Locale.US,
                                    formatString, Float
                                        .parseFloat(outputDefaultValue));
                        }
                        else if (type.equals("d") || type.equals("D")) { //$NON-NLS-1$ //$NON-NLS-2$
                            fmOut =
                                String.format(java.util.Locale.US,
                                    formatString, Integer
                                        .parseInt(outputDefaultValue));
                        }

                        if (fmOut.equals(outputDefaultValue))
                        {
                            // ok
                            resultat = fmOut;
                        }
                        else
                        {
                            // erreur
                            resultat = Messages.error;
                        }

                    }
                    else
                    {
                        // valeur normale, on applique la formule puis formatage
                        org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
                        parser.addStandardFunctions();
                        parser.addStandardConstants();
                        parser.addVariable("x", Float.valueOf(selection)); //$NON-NLS-1$
                        parser.parseExpression(ViewMain.tableData.getItem(i)
                            .getText(2));
                        resultJEP = parser.getValue();

                        if (type.equals("f") || type.equals("F")) { //$NON-NLS-1$//$NON-NLS-2$
                            resultat =
                                String.format(ViewMain.tableData.getItem(i)
                                    .getText(6), new Float(resultJEP));
                        }
                        else if (type.equals("d") || type.equals("D")) { //$NON-NLS-1$ //$NON-NLS-2$
                            resultat =
                                String.format(ViewMain.tableData.getItem(i)
                                    .getText(6), (Double.valueOf(resultJEP))
                                    .intValue());
                        }
                        else
                        {
                            // error
                            resultat = Messages.error;
                        }
                    }
                }
                catch (Exception e)
                {
                    resultat = Messages.error;
                    e.printStackTrace();
                }

                types.get(i).setTest(resultat);
            }

            if ((!ViewMain.tableData.getItem(i).getText(11).equals(
                Global.EMPTY_STRING))
                    && (!ViewMain.tableData.getItem(i).getText(12).equals(
                        Global.EMPTY_STRING)))
            {
                ligne =
                    Utils.lignesBrutes.get(Integer
                        .valueOf(ViewMain.editFileForm.sectionEnteteFichier
                            .getValue())
                            + DataModel.stationModel.getNumberOfLines()) + 1;
                selection =
                    ligne.substring(
                        Integer.valueOf(ViewMain.tableData.getItem(i).getText(
                            11)) - 1,
                        Integer.valueOf(ViewMain.tableData.getItem(i).getText(
                            12))).trim();

                types.get(i).setTestIn(selection);

                String flagSDN = Global.ZERO;
                ArrayList<Flag> flags =
                    StationModel.getSingleton().getFlagTable().getFlags();
                int size = flags.size();
                for (int j = 0; j < size; j++)
                {
                    if (flags.get(j).getLocal()
                        .equals(types.get(i).getTestIn()))
                    {
                        flagSDN = Utils.extraitCode(flags.get(j).getSdn());
                        break;
                    }
                }

                types.get(i).setTestOut(flagSDN);
            }
            else
            {
                types.get(i).setTestIn(Global.EMPTY_STRING);
                types.get(i).setTestOut(Global.EMPTY_STRING);
            }
        }

        // razTableData
        razTableData();

        // refresh de la table
        creationTable();

    }

    public static void majTypeData()
    {
        // on met e jour tous les types de donnees dans l'ArrayList global
        // e partir des valeurs de la table

        int nbre;

        nbre = ViewMain.tableData.getItemCount();
        for (int i = 0; i < nbre; i++)
        {
            types.get(i).setCode(ViewMain.tableData.getItem(i).getText(0));
            types.get(i).setUnite(ViewMain.tableData.getItem(i).getText(1));
            types.get(i)
                .setConversion(ViewMain.tableData.getItem(i).getText(2));
            types.get(i).setTest(ViewMain.tableData.getItem(i).getText(3));
            types.get(i).setDebut(
                Integer.valueOf(ViewMain.tableData.getItem(i).getText(4)));
            types.get(i).setFin(
                Integer.valueOf(ViewMain.tableData.getItem(i).getText(5)));
            types.get(i).setFormat(ViewMain.tableData.getItem(i).getText(6));
            types.get(i).setEntree(ViewMain.tableData.getItem(i).getText(7));
            types.get(i).setSortie(ViewMain.tableData.getItem(i).getText(8));

            if (!ViewMain.tableData.getItem(i).getText(11).equals(
                Global.EMPTY_STRING)
                    && !ViewMain.tableData.getItem(i).getText(12).equals(
                        Global.EMPTY_STRING))
            {
                types.get(i).setDebutFlag(
                    ViewMain.tableData.getItem(i).getText(11));
                types.get(i).setFinFlag(
                    ViewMain.tableData.getItem(i).getText(12));
            }
        }
    }

}
