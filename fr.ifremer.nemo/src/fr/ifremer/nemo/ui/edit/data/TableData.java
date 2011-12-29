package fr.ifremer.nemo.ui.edit.data;

import java.util.ArrayList;
import java.util.HashMap;

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

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.LabelParameterManager;
import fr.ifremer.nemo.domain.MediaDomain;
import fr.ifremer.nemo.ui.FilteredTableFactory;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.tables.FormatsFilteredTable;
import fr.ifremer.nemo.ui.tables.ParameterFilteredTable;
import fr.ifremer.nemo.ui.tables.UnitFilteredTable;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSParameter;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;

public class TableData
{
    /**
     * Parametres provenant des web services (fichiers xml mis à jour par les
     * web services) les parametres P091 sont lus depuis le fichier
     * parameters.xml pour les autres, il faut lire le fichier Pxxx.xml
     */
    private ArrayList<TypeData> typesWS;
    /**
     * Parametres P061 provenant des web services (fichier P061.xml mis à jour
     * par les web services) ces parametres sont utilisés uniquement dans le cas
     * où on est en P011viaP021, pour affecter une unité
     */
    private ArrayList<TypeUnit> typesP061;
    /**
     * formats output
     */
    private ArrayList<TypeFormat> typesFormat;

    /**
     * Getter sur typesFormat.
     * 
     * @return the typesFormat
     */
    public ArrayList<TypeFormat> getTypesFormat()
    {
        return typesFormat;
    }

    /**
     * Setter sur typesFormat.
     * 
     * @param typesFormat
     *            the typesFormat to set
     */
    public void setTypesFormat(ArrayList<TypeFormat> typesFormat)
    {
        this.typesFormat = typesFormat;
    }

    /**
     * Parametres à afficher dans le tableau de l'onglet data
     */
    private ArrayList<TypeData> types;

    /**
     * la table contenant les parametres, affichée dans l'onglet data
     */
    private Table myTable;

    /**
     * reference sur le panneau de choix des parametres
     */
    private ParameterFilteredTable myParameterFilteredTable;

    /**
     * liste des titres de colonnes (ordonnée)
     */
    private HashMap<Integer, TableDataColumn> columnsMap;
    private final HashMap<String, Integer> colNbById =
        new HashMap<String, Integer>();

    /**
     * columns IDs
     */

    private static String COL_CODE = "COL_CODE";
    private static String COL_USERLABEL = "COL_USERLABEL";
    private static String COL_UNIT = "COL_UNIT";
    private static String COL_CONVERSION = "COL_CONVERSION";
    private static String COL_TEST = "COL_TEST";
    private static String COL_START = "COL_START";
    private static String COL_END = "COL_END";
    private static String COL_FORMAT = "COL_FORMAT";
    private static String COL_VALUE_IN = "COL_VALUE_IN";
    private static String COL_VALUE_OUT = "COL_VALUE_OUT";
    private static String COL_TEST_IN = "COL_TEST_IN";
    private static String COL_TEST_OUT = "COL_TEST_OUT";
    private static String COL_START_FLAG = "COL_START_FLAG";
    private static String COL_END_FLAG = "COL_END_FLAG";
    private static String COL_UNIT_CODE = "COL_UNIT_CODE";

    private final String[] colOrder = {COL_CODE, COL_USERLABEL, COL_UNIT,
            COL_CONVERSION, COL_TEST, COL_START, COL_END, COL_FORMAT,
            COL_VALUE_IN, COL_VALUE_OUT, COL_TEST_IN, COL_TEST_OUT,
            COL_START_FLAG, COL_END_FLAG, COL_UNIT_CODE};

    /**
     * constructeur initialise automatiquement les typesWS
     */
    private final GroupVerticalParameters myGroupVerticalParameters;

    public TableData()
    {
        initTypesWS();
        initColumnsMap();

        myGroupVerticalParameters =
            Nemo.getInstance().getViewMain().getEditDataForm()
                .getMyGroupeVerticalParameters();
    }

    private boolean isValueOutWritable()
    {
        return !Nemo.getInstance().getViewMain().getMyRulesManager().isConversionToODV();
    }

    private boolean isLabelWritable()
    {
        boolean res = false;
        return Nemo.getInstance().getViewMain().getMyRulesManager()
            .isConversionToODV()
                && !Nemo.getInstance().getViewMain().getMyRulesManager()
                    .isDataListTypeP091();
    }

    /**
     * initialise les colonnes
     */
    private void initColumnsMap()
    {
        
        // init col nb by id
        for (int i = 0; i < colOrder.length; i++)
        {
            colNbById.put(colOrder[i], i);
        }

        TableDataColumn col;
        columnsMap = new HashMap<Integer, TableDataColumn>();

        col =
            new TableDataColumn(COL_CODE, colNbById.get(COL_CODE),
                Messages.table_label_code, 100, false, true);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_USERLABEL, colNbById.get(COL_USERLABEL),
                Messages.table_label_userlabel, 100, isLabelWritable(), false);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_UNIT, colNbById.get(COL_UNIT),
                Messages.table_label_unit, 90, false, true);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_CONVERSION, colNbById.get(COL_CONVERSION),
                Messages.label_conversion, 70, true, false);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_TEST, colNbById.get(COL_TEST),
                Messages.label_test, 50, false, false);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_START, colNbById.get(COL_START),
                Messages.label_start, 50, false, true);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_END, colNbById.get(COL_END),
                Messages.label_end, 50, false, true);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_FORMAT, colNbById.get(COL_FORMAT),
                Messages.label_format, 70, true, true);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_VALUE_IN, colNbById.get(COL_VALUE_IN),
                Messages.table_label_valueIn, 50, true, false);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_VALUE_OUT, colNbById.get(COL_VALUE_OUT),
                Messages.table_label_valueOut, 50, isValueOutWritable(), false);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_TEST_IN, colNbById.get(COL_TEST_IN),
                Messages.table_label_testIn, 50, false, false);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_TEST_OUT, colNbById.get(COL_TEST_OUT),
                Messages.table_label_testOut, 50, false, false);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_START_FLAG, colNbById.get(COL_START_FLAG),
                Messages.table_label_startFlag, 50, false, false);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_END_FLAG, colNbById.get(COL_END_FLAG),
                Messages.table_label_endFlag, 50, false, false);
        columnsMap.put(col.pos, col);
        col =
            new TableDataColumn(COL_UNIT_CODE, colNbById.get(COL_UNIT_CODE),
                Global.EMPTY_STRING, 0, false, false);
        columnsMap.put(col.pos, col);
    }

    public void reinitTable()
    {
        if (myTable != null)
        {
            initTypesWS();
            go();
        }
    }

    public void razAndRecreateTable()
    {
        razTableData();
        creationTable();

    }

    public void go()
    {
        initTypesWS();
        types = new ArrayList<TypeData>();
        if (!(myTable == null || myTable.isDisposed()))
        {
            razTableData();
        }
        creationTable();
    }

    public void setMyParameterFilteredTable(ParameterFilteredTable paramTable)
    {
        myParameterFilteredTable = paramTable;
    }

    /**
     * reinitialise la liste des paramètres de la table
     */
    public void reinitTypes()
    {
        types = null;
        types = new ArrayList<TypeData>();
    }

    /**
     * ajoute un parametre à la liste des parametres de la table
     * 
     * @param typeData
     */
    public void addType(TypeData typeData)
    {
        types.add(typeData);
        myGroupVerticalParameters.addTypeData(typeData);
    }

    /**
     * creation de la table contenant les paramètres dans l'onglet data
     */
    public void creationTable()
    {
        myTable =
            new Table(Nemo.getInstance().getViewMain().getEditDataForm()
                .getCompositeData(), SWT.FULL_SELECTION | SWT.H_SCROLL);

        myTable.setHeaderVisible(true);
        myTable.setLinesVisible(true);

        for (int i = 0; i < columnsMap.size(); i++)
        {
            TableColumn tableColumn = new TableColumn(myTable, SWT.NONE);
            tableColumn.setWidth(columnsMap.get(i).width);
            tableColumn.setText(columnsMap.get(i).label.toUpperCase());
            if (columnsMap.get(i).id.equals(COL_USERLABEL))
            {
                // columnsMap.get(i).setWritable(isLabelWritable());
                columnsMap.get(i).enable(isLabelWritable());
            }
            if (columnsMap.get(i).id.equals(COL_VALUE_OUT))
            {
                // columnsMap.get(i).setWritable(isLabelWritable());
                columnsMap.get(i).enable(isValueOutWritable());
            }
            // tableColumn.setAlignment(SWT.RIGHT);
        }
        initTable();

        // agrandissement de la table
        Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData()
            .getShell().layout();

    }

    private void checkTypesWS()
    {
        initTypesWS();
    }

    /**
     * chargement des parametres pour l'affichage de la liste de choix des
     * parametres (menu Parameter List du tableau de l'onget data)
     */
    public void initTypesWS()
    {
        typesWS = null;
        typesWS = new ArrayList<TypeData>();
        if (Nemo.getInstance().getViewMain().getMyRulesManager()
            .getMyDataListType() != DataListTypeEnum.P011_VIA_P021)

        {
            iniTypesWSP091();
        }
        else

        {
            iniTypesWSP021();
        }
    }

    private void iniTypesWSP091()
    {
        int taille = NemoLists.getParameters().length;

        for (int i = 0; i < taille; i++)
        {
            // initialisation de la ArrayList des types des donnees a partir du
            // fichier P091 parameters.xml
            WSParameter wsParameter = new WSParameter();
            wsParameter = NemoLists.getParameters()[i];

            TypeData type = new TypeData(wsParameter);

            // ajout dans liste
            typesWS.add(type);
        }
    }

    private void iniTypesWSP021()
    {
        // init des données a partir du fichier P021 p021.xml
        CodeTableRecordType[] listP021 =
            NemoLists.getVocabList(Intern.list_BODCpdv);
        TypeData type;

        for (CodeTableRecordType record : listP021)
        {
            type = new TypeData(record);
            type.setIsP011(false);
            // ajout dans liste
            typesWS.add(type);
        }
    }

    /**
     * initialize the P061 types to use as units choice in P011viaP021 case
     */
    public void initTypesP061()
    {
        if (typesP061 == null)
        {
            typesP061 = new ArrayList<TypeUnit>();
            // init des données a partir du fichier P021 p021.xml
            CodeTableRecordType[] listP061 =
                NemoLists.getVocabList(Intern.list_P061);
            TypeUnit typeUnit;
            for (CodeTableRecordType record : listP061)
            {
                typeUnit = new TypeUnit(record);
                typesP061.add(typeUnit);
            }
        }

    }

    public void fillTypes()
    {
        // initialisation de la ArrayList des types des donnees e partir du
        types = null;
        types = new ArrayList<TypeData>();

        // On ajoute systématiquement le paramètre pression pour les profils en
        // medatlas
        // verticaux sauf si la liste de paramètres à utiliser est la P011
        if (Nemo.getInstance().getViewMain().getMyRulesManager().isProfile()
                && Nemo.getInstance().getViewMain().getMyRulesManager()
                    .isConversionToMedatlas()
                && Nemo.getInstance().getViewMain().getMyRulesManager()
                    .getMyDataListType() == DataListTypeEnum.P091)
        {
            for (TypeData type : typesWS)
            {
                // ajout systematique de PRES
                if (type.getEntryKey().equals(Intern.paramPRES)
                        && Nemo.getInstance().getViewMain().getMyRulesManager()
                            .isProfile())
                {
                    // on rajoute PRES
                    addType(type);
                }
            }
        }
        razAndRecreateTable();
    }

    /**
     * initialisation de la table des paramètres de l'onglet data
     */
    public void initTable()
    {

        /*
         * Remplir la table avec les tableItems avant de linker les editors !
         */

        final Table table;
        table = myTable;

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

        /**
         * Listener pour le clic sur la table les colonnes modifiables sont
         * format entree sortie conversion
         */
        table.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event)
            {
                
                // clic 3e bouton (=clic droit): comportement par defaut
                if (event.button == 3) 
                {
                    return;
                }
                
                
                // FAE 9670 surlignage de la position des paramètres dans le fichier
                if (event.button == 1)
                {
                    int indice = myTable.getSelectionIndex();
                    selectInViewFile(
                        Integer.valueOf(myTable.getItem(indice).getText(
                            colNbById.get(COL_START))),
                        Integer.valueOf(myTable.getItem(indice).getText(
                            colNbById.get(COL_END))));
                }

                // calcul du numero de ligne selectionne
                Point pt = new Point(event.x, event.y);
                TableItem item = table.getItem(pt);

                if (item == null)
                {
                    return;
                }

                // edition directe de l'element selectionne
                Rectangle clientArea = table.getClientArea();
                int index = table.getTopIndex();

                while (index < table.getItemCount())
                {
                    boolean visible = false;
                    final TableItem itemTmp = table.getItem(index);

                    for (int colNumber = 0; colNumber < table.getColumnCount(); colNumber++)
                    {
                        // edition des colonnes autorisées en écriture
                        // uniquement

                        if (columnsMap.get(colNumber).writable)
                        {
                            Rectangle rect = itemTmp.getBounds(colNumber);
                            if (rect.contains(pt))
                            {
                                final int column = colNumber;
                                final Text text = new Text(table, SWT.NONE);
                                text.setText(itemTmp.getText(colNumber));

                                /**
                                 * Listener sur le texte d'une cellule
                                 */
                                Listener textListener = new Listener() {

                                    String s = text.getText();

                                    public void handleEvent(final Event e)
                                    {

                                        switch (e.type)
                                        {
                                            case SWT.FocusOut:
                                                itemTmp.setText(column,
                                                    text.getText());
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

                                            case SWT.Modify:
                                                if (!((Text) e.widget)
                                                    .getText().equals(s))
                                                    Utils.invalidEtapes();
                                            break;
                                        }
                                    }
                                };
                                text.addListener(SWT.FocusOut, textListener);
                                text.addListener(SWT.Traverse, textListener);
                                text.addListener(SWT.Modify, textListener);
                                editor.setEditor(text, itemTmp, colNumber);
                                text.selectAll();

                                text.setFocus();
                                return;
                            }
                            if (!visible && rect.intersects(clientArea))
                            {
                                visible = true;
                            }
                        }
                    }
                    if (!visible)
                        return;
                    index++;
                }
            }
        });
        
        table.addListener(SWT.KeyUp, new Listener() {
            public void handleEvent(Event event)
            {
                if (event.keyCode == SWT.ARROW_DOWN || event.keyCode == SWT.ARROW_UP)
                {
                    int indice = myTable.getSelectionIndex();
                    selectInViewFile(
                        Integer.valueOf(myTable.getItem(indice).getText(
                            colNbById.get(COL_START))),
                        Integer.valueOf(myTable.getItem(indice).getText(
                            colNbById.get(COL_END))));
                }
            }
        });
        
        /**
         * *********************************************************************
         * ******* creation du menu
         */
        Menu menu = new Menu(table.getShell(), SWT.POP_UP);

        // ========= Injecter debut/fin
        MenuItem itemInject = new MenuItem(menu, SWT.NONE);
        itemInject.setText(Messages.menu_label_setValue);
        itemInject.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                // table.forceFocus();
                Utils.injectData(Nemo.getInstance().getViewMain()
                    .getEditDataForm().getTextAreaData(),
                    colNbById.get(COL_START), colNbById.get(COL_END));
                majTypeData();
                // refresh de la table
                razAndRecreateTable();
                Utils.invalidEtapes();
            }
        });
        // ========= Maj champ test
        MenuItem itemMaj = new MenuItem(menu, SWT.NONE);
        itemMaj.setText(Messages.menu_label_updateTest);
        itemMaj.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                majChampTest();
            }
        });
        // ========= Liste de paramètres
        MenuItem itemList = new MenuItem(menu, SWT.NONE);
        itemList.setText(Messages.menu_label_paramList);
        itemList.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                if (Nemo.getInstance().getViewMain().getMyRulesManager()
                    .getMyDataListType() != DataListTypeEnum.UNKNOWN)
                {
                    checkTypesWS();
                    openMyParameterFilteredTable();
                }
                else
                {
                    Utils
                        .messageErreur(Messages.edit_data_error_dataListUnknown);
                }
            }
        });
        // ========= Liste des unités P061 (P011 via P021 seulement)
        if (Nemo.getInstance().getViewMain().getMyRulesManager()
            .getMyDataListType() == DataListTypeEnum.P011_VIA_P021)
        {
            MenuItem itemAddP061Unit = new MenuItem(menu, SWT.NONE);
            itemAddP061Unit.setText(Messages.menu_label_selectP061Unit);
            itemAddP061Unit.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event e)
                {
                    initTypesP061();
                    int indice = myTable.getSelectionIndex();
                    openMyUnitFilteredTable(indice);
                }
            });
        }
        // ========= Selection d'un format (P011 via P021 seulement)
        if (Nemo.getInstance().getViewMain().getMyRulesManager()
            .getMyDataListType() == DataListTypeEnum.P011_VIA_P021)
        {
            MenuItem selectFormat = new MenuItem(menu, SWT.NONE);
            selectFormat.setText(Messages.menu_label_selectFormat);
            selectFormat.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event e)
                {
                    initTypesP061();
                    int indice = myTable.getSelectionIndex();
                    openMyFormatFilteredTable(indice);
                }
            });
        }
        // ========= Monter paramètre sélectionné tout en haut de la liste
        MenuItem itemUpToTop = new MenuItem(menu, SWT.NONE);
        itemUpToTop.setText(Messages.menu_label_upToTopParam);
        itemUpToTop.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                // System.out.println("up");
                monterEltToTopTableData();

                Utils.invalidEtapes();
            }
        });
        // ========= Monter paramètre sélectionné
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
        // ========= descendre paramètres sélectionné
        MenuItem itemDown = new MenuItem(menu, SWT.NONE);
        itemDown.setText(Messages.menu_label_downParam);
        itemDown.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                descendreEltTableData();

                Utils.invalidEtapes();
            }
        });
        // ========= descendre paramètre sélectionné en bas de liste
        MenuItem itemDownToBottom = new MenuItem(menu, SWT.NONE);
        itemDownToBottom.setText(Messages.menu_label_downToBottomParam);
        itemDownToBottom.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                descendreEltToBottomTableData();

                Utils.invalidEtapes();
            }
        });
        // ========= supprimer paramètre sélectionné
        MenuItem itemDel = new MenuItem(menu, SWT.NONE);
        itemDel.setText(Messages.menu_label_removeParam);
        itemDel.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                supprEltTableData();
                Utils.invalidEtapes();
            }
        });

        // separateur
        new MenuItem(menu, SWT.SEPARATOR);

        // ========= Injecter flag
        MenuItem itemInjectFlag = new MenuItem(menu, SWT.NONE);
        itemInjectFlag.setText(Messages.menu_label_setFlag);
        itemInjectFlag.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                table.forceFocus();
                Utils.injectData(Nemo.getInstance().getViewMain()
                    .getEditDataForm().getTextAreaData(),
                    colNbById.get(COL_START_FLAG), colNbById.get(COL_END_FLAG));

                Utils.invalidEtapes();
            }
        });
        // ========= supprimer flag
        MenuItem itemRemoveFlag = new MenuItem(menu, SWT.NONE);
        itemRemoveFlag.setText(Messages.menu_label_deleteFlag);
        itemRemoveFlag.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                table.forceFocus();
                table.getItems()[table.getSelectionIndex()].setText(
                    colNbById.get(COL_TEST_IN), Global.EMPTY_STRING);
                table.getItems()[table.getSelectionIndex()].setText(
                    colNbById.get(COL_TEST_OUT), Global.EMPTY_STRING);
                table.getItems()[table.getSelectionIndex()].setText(
                    colNbById.get(COL_START_FLAG), Global.EMPTY_STRING);
                table.getItems()[table.getSelectionIndex()].setText(
                    colNbById.get(COL_END_FLAG), Global.EMPTY_STRING);
                types.get(table.getSelectionIndex()).setDebutFlag(null);
                types.get(table.getSelectionIndex()).setFinFlag(null);

                Utils.invalidEtapes();
            }
        });

        // separateur
        new MenuItem(menu, SWT.SEPARATOR);

        // ========= Suppression de tous les parametres à la fois
        MenuItem deleteAll = new MenuItem(menu, SWT.NONE);
        deleteAll.setText(Messages.menu_label_deleteAllParameters);
        deleteAll.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                table.clearAll();
                supprAllEltTableData();

            }
        });

        table.setMenu(menu);
        Utils.forceMenu(table, menu);

        // agrandissement de la table
        Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData()
            .getParent().layout(true, true);
        table.setLayout(new FillLayout());
        table.redraw();
    }

    /**
     * ouvre la fenetre de choix de parametres
     */
    public void openMyParameterFilteredTable()
    {
        getMyParameterFilteredTable().open(
            Nemo.getInstance().getViewMain().getEditDataForm()
                .getCompositeData().getParent());
    }

    public void openMyUnitFilteredTable(int rowIndex)
    {
        getMyUnitFilteredTable().open(
            Nemo.getInstance().getViewMain().getEditDataForm()
                .getCompositeData().getParent());
    }

    public void openMyFormatFilteredTable(int rowIndex)
    {
        getMyFormatFilteredTable().open(
            Nemo.getInstance().getViewMain().getEditDataForm()
                .getCompositeData().getParent());
    }

    public void initTableData()
    {
        // creation des TableItems e partir de la ArrayList types

        // nouvelle creation des items
        for (int i = 0; i < types.size(); i++)
        {
            TableItem item = new TableItem(myTable, SWT.NONE);
            String code;
            String label = "";

            if (types.get(i).isP011())
            {
                code =
                    types.get(i).getEntryKeyP011() + Global.SEP
                            + types.get(i).getEntryTermP011();

            }
            else
            {
                code = types.get(i).getFormattedCodeAndName();
            }

            if (types.get(i).isP011())
            {
                if (Nemo.getInstance().getViewMain().getMyRulesManager()
                    .isConversionToODV())
                {
                    String userLabel =
                        LabelParameterManager.getInstance().getUserLabel(
                            types.get(i).getEntryKeyP011());
                    if (userLabel != null)
                    {
                        label = userLabel;
                    }
                    else
                    {
                        // label = types.get(i).getEntryKeyP011();
                    }
                }
                else
                // medatlas
                {
                    // label = types.get(i).getEntryKeyP011();
                }
            }
            else
            {
                // label = types.get(i).getEntryKey();
            }
            item.setText(new String[] {code, label, types.get(i).getUnite(),
                    types.get(i).getConversion(), types.get(i).getTest(),
                    String.valueOf(types.get(i).getDebut()),
                    String.valueOf(types.get(i).getFin()),
                    types.get(i).getFormat(), types.get(i).getEntree(),
                    types.get(i).getSortie(), types.get(i).getTestIn(),
                    types.get(i).getTestOut(), types.get(i).getDebutFlag(),
                    types.get(i).getFinFlag(),
                    // FAE 6542
                    types.get(i).getUniteCode()});

        }

    }

    public void razTableData()
    {
        // effacement de tous les items de la table
        myTable.dispose();

    }

    public void monterEltToTopTableData()
    {
        // on monte d'un cran l'element e la position indice
        // swap (indice-1) <-> indice
        //

        // Utils.messageErreur(Global.EMPTY_STRING);
        int nbre;
        ArrayList<TypeData> listeTempo;
        nbre = types.size();

        int indice = myTable.getSelectionIndex();

        // si l'indice est 0, on ne fait rien
        if (indice != 0)
        {

            listeTempo = new ArrayList<TypeData>();

            // mise e jour prealable
            majTypeData();
            listeTempo.add(types.get(indice));

            // parcours de la liste
            for (int i = 0; i < (indice - 1); i++)
            {
                listeTempo.add(types.get(i));
            }
            // swap des 2
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

            // refresh de la table
            razAndRecreateTable();

        }
    }

    public void monterEltTableData()
    {
        // on monte d'un cran l'element e la position indice
        // swap (indice-1) <-> indice
        //

        // Utils.messageErreur(Global.EMPTY_STRING);
        int nbre;
        ArrayList<TypeData> listeTempo;
        nbre = types.size();

        int indice = myTable.getSelectionIndex();

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

            // refresh de la table
            razAndRecreateTable();

        }

    }

    public void descendreEltTableData()
    {
        // on descend d'un cran l'element e la position indice
        // swap (indice-1) <-> indice

        int indice = myTable.getSelectionIndex();

        // Si derniere ligne, on ne fait rien
        if (indice != myTable.getItemCount() - 1)
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
            razAndRecreateTable();
        }
    }

    public void descendreEltToBottomTableData()
    {
        // on descend d'un cran l'element e la position indice
        // swap (indice-1) <-> indice

        int indice = myTable.getSelectionIndex();

        // Si derniere ligne, on ne fait rien
        if (indice != myTable.getItemCount() - 1)
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

            // fin de parcours
            for (int i = (indice + 1); i < nbre; i++)
            {
                listeTempo.add(types.get(i));
            }
            listeTempo.add(types.get(indice));
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
            razAndRecreateTable();
        }
    }

    public void supprAllEltTableData()
    {
        System.out.println("suppr tout");
        // majTypeData();
        types.clear();
        myGroupVerticalParameters.removeAllToAllCombos();
        // refresh de la table
        razAndRecreateTable();
    }

    public void supprEltTableData()
    {
        // suppression de la ligne selectionnee
        int indice = myTable.getSelectionIndex();
        // d'abord mise e jour des donnees
        majTypeData();

        // on supprime l'element e l'indice donne
        // split sur " - "
        TypeData typeData = types.get(indice);
        // Pattern p = Pattern.compile(Global.SEP);
        // String mots[] = p.split(typeData.getCode());
        // String code = mots[0];
        //
        //
        System.out.println("suppr " + typeData.getEntryKey());

        types.remove(indice);

        myGroupVerticalParameters.removeToAllCombo(typeData);

        // refresh de la table
        razAndRecreateTable();

    }

    public void removeAllElts()
    {
        types.clear();
        // refresh de la table
        razAndRecreateTable();

    }

    public boolean majChampTest()
    {

        // maj preventive de tout
        majTypeData();

        boolean ok = true;

        int nbre;
        String selection = "";
        String ligne, resultat;
        double resultJEP;

        nbre = myTable.getItemCount();
        for (int i = 0; i < nbre; i++)
        {

            if ((!myTable.getItem(i).getText(colNbById.get(COL_START))
                .equals(Global.EMPTY_STRING))
                    && (!myTable.getItem(i)
                        .getText(colNbById.get(COL_CONVERSION))
                        .equals(Global.EMPTY_STRING)))
            {
                // on a selectionne un debut et une fin et il y a une conversion
                // e faire

                MediaDomain mediaDomain =
                    Nemo.getInstance().getDomain().getMedia();
                ligne =
                    Utils.lignesBrutes.get(mediaDomain.getModel()
                        .getFileHeader()
                            + mediaDomain.getStationHeaderNumberLines());

                try
                {
                    selection =
                        ligne.substring(
                            Integer.valueOf(myTable.getItem(i).getText(
                                colNbById.get(COL_START))) - 1,
                            Integer.valueOf(myTable.getItem(i).getText(
                                colNbById.get(COL_END)))).trim();
                }
                catch (Exception e)
                {
                    System.out.println("erreur substring "
                            + myTable.getItem(i).getText(
                                colNbById.get(COL_CODE)));
                    ok = false;
                    Utils.messageErreurAppend(Messages.Etape_98
                            + " "
                            + myTable.getItem(i).getText(
                                colNbById.get(COL_CODE)));
                    break;
                }
                // on formate puis on reaffiche la liste
                try
                {
                    String formatString =
                        myTable.getItem(i).getText(colNbById.get(COL_FORMAT));
                    String type =
                        formatString.substring(formatString.length() - 1);

                    if (selection.equals(myTable.getItem(i).getText(
                        colNbById.get(COL_VALUE_IN))))
                    {

                        // entree par defaut, le test est la valeur de sortie
                        // par defaut formatee
                        String fmOut = Global.EMPTY_STRING;
                        String outputDefaultValue =
                            myTable.getItem(i).getText(
                                colNbById.get(COL_VALUE_OUT));

                        // Float ou Decimal ?
                        if (type.equals("f") || type.equals("F")) { //$NON-NLS-1$//$NON-NLS-2$
                            fmOut =
                                String.format(java.util.Locale.US,
                                    formatString,
                                    Float.parseFloat(outputDefaultValue));
                        }
                        else if (type.equals("d") || type.equals("D")) { //$NON-NLS-1$ //$NON-NLS-2$
                            fmOut =
                                String.format(java.util.Locale.US,
                                    formatString,
                                    Integer.parseInt(outputDefaultValue));
                        }

                        if (fmOut.equals(outputDefaultValue))
                        {
                            // ok
                            resultat = fmOut;
                        }
                        else
                        {
                            // erreur
                            ok = false;
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
                        parser.parseExpression(myTable.getItem(i).getText(
                            colNbById.get(COL_CONVERSION)));
                        resultJEP = parser.getValue();

                        if (type.equals("f") || type.equals("F")) { //$NON-NLS-1$//$NON-NLS-2$
                            resultat =
                                String.format(
                                    myTable.getItem(i).getText(
                                        colNbById.get(COL_FORMAT)), new Float(
                                        resultJEP));
                        }
                        else if (type.equals("d") || type.equals("D")) { //$NON-NLS-1$ //$NON-NLS-2$
                            resultat =
                                String.format(
                                    myTable.getItem(i).getText(
                                        colNbById.get(COL_FORMAT)),
                                    (Double.valueOf(resultJEP)).intValue());
                        }
                        else
                        {
                            // error
                            ok = false;
                            resultat = Messages.error;
                        }
                    }
                }
                catch (Exception e)
                {
                    ok = false;
                    resultat = Messages.error;
                    e.printStackTrace();
                }

                types.get(i).setTest(resultat);
            }

            if ((!myTable.getItem(i).getText(colNbById.get(COL_START_FLAG))
                .equals(Global.EMPTY_STRING))
                    && (!myTable.getItem(i)
                        .getText(colNbById.get(COL_END_FLAG))
                        .equals(Global.EMPTY_STRING)))
            {

                MediaDomain mediaDomain =
                    Nemo.getInstance().getDomain().getMedia();
                ligne =
                    Utils.lignesBrutes.get(mediaDomain.getModel()
                        .getFileHeader()
                            + mediaDomain.getStationHeaderNumberLines() + 1);

                selection =
                    ligne.substring(
                        Integer.valueOf(myTable.getItem(i).getText(
                            colNbById.get(COL_START_FLAG))) - 1,
                        Integer.valueOf(myTable.getItem(i).getText(
                            colNbById.get(COL_END_FLAG)))).trim();

                types.get(i).setTestIn(selection);

                String flagSDN = Global.ZERO;
                ArrayList<Flag> flags =
                    Nemo.getInstance().getDomain().getStation().getModel()
                        .getFlagTable().getFlags();
                int size = flags.size();
                for (int j = 0; j < size; j++)
                {
                    if (flags.get(j).getLocal()
                        .equals(types.get(i).getTestIn()))
                    {
                        flagSDN =
                            fr.ifremer.nemo.util.Utils.extraitCode(flags.get(j)
                                .getSdn());
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

        // refresh de la table
        razAndRecreateTable();

        return ok;
    }

    public void majTypeData()
    {
        // on met a jour tous les types de donnees dans l'ArrayList global
        // a partir des valeurs de la table

        int nbre;
        nbre = myTable.getItemCount();

        for (int i = 0; i < nbre; i++)
        {
            if (!types.get(i).isP011())
            {
                types.get(i).setEntryKeyAndEntryTerm(
                    myTable.getItem(i).getText(colNbById.get(COL_CODE)));
            }
            String userLabel =
                myTable.getItem(i).getText(colNbById.get(COL_USERLABEL));

            if (!userLabel.equals(Global.EMPTY_STRING))
            {
                System.out.println("p11Label " + types.get(i).getEntryKeyP011()
                        + " userLabel: " + userLabel);
                LabelParameterManager.getInstance().updateLabel(
                    types.get(i).getEntryKeyP011(), userLabel);
            }
            // types.get(i).setUserLabel(myTable.getItem(i).getText(colNbById.get(COL_USERLABEL)));

            types.get(i).setUnite(
                myTable.getItem(i).getText(colNbById.get(COL_UNIT)));
            types.get(i).setConversion(
                myTable.getItem(i).getText(colNbById.get(COL_CONVERSION)));
            types.get(i).setTest(
                myTable.getItem(i).getText(colNbById.get(COL_TEST)));
            types.get(i).setDebut(
                Integer.valueOf(myTable.getItem(i).getText(
                    colNbById.get(COL_START))));
            types.get(i).setFin(
                Integer.valueOf(myTable.getItem(i).getText(
                    colNbById.get(COL_END))));
            types.get(i).setFormat(
                myTable.getItem(i).getText(colNbById.get(COL_FORMAT)));
            types.get(i).setEntree(
                myTable.getItem(i).getText(colNbById.get(COL_VALUE_IN)));
            types.get(i).setSortie(
                myTable.getItem(i).getText(colNbById.get(COL_VALUE_OUT)));

            if (!myTable.getItem(i).getText(colNbById.get(COL_START_FLAG))
                .equals(Global.EMPTY_STRING)
                    && !myTable.getItem(i).getText(colNbById.get(COL_END_FLAG))
                        .equals(Global.EMPTY_STRING))
            {
                types.get(i).setDebutFlag(
                    myTable.getItem(i).getText(colNbById.get(COL_START_FLAG)));
                types.get(i).setFinFlag(
                    myTable.getItem(i).getText(colNbById.get(COL_END_FLAG)));
            }
            // FAE 6542
            types.get(i).setUniteCode(
                myTable.getItem(i).getText(colNbById.get(COL_UNIT_CODE)));
        }
    }

    public ArrayList<TypeData> getTypesWS()
    {
        return typesWS;
    }

    public ArrayList<TypeData> getTypes()
    {
        return types;
    }

    public Table getMyTable()
    {
        return myTable;
    }

    public ParameterFilteredTable getMyParameterFilteredTable()
    {
        if (myParameterFilteredTable == null)
        {
            FilteredTableFactory filteredFactory =
                Nemo.getInstance().getViewMain().getMyFilteredTableFactory();
            myParameterFilteredTable =
                (ParameterFilteredTable) filteredFactory.go(Nemo.getInstance()
                    .getViewMain().getEditDataForm().getCompositeData()
                    .getParent(), Global.PARAMETER, null);
        }
        return myParameterFilteredTable;
    }

    public void reinitMyParameterFilteredTable()
    {
        myParameterFilteredTable = null;
    }

    public UnitFilteredTable getMyUnitFilteredTable()
    {
        UnitFilteredTable theTable = new UnitFilteredTable();
        return theTable;
    }

    public FormatsFilteredTable getMyFormatFilteredTable()
    {
        FormatsFilteredTable theTable = new FormatsFilteredTable();
        return theTable;
    }

    public void setUnit(TypeUnit typeUnit)
    {
        int selectedRow = myTable.getSelectionIndex();
        if (selectedRow > -1)
        {
            TableItem item = myTable.getItem(selectedRow);
            item.setText(colNbById.get(COL_UNIT), typeUnit.getTerm());
            // FAE 6542
            item.setText(colNbById.get(COL_UNIT_CODE), typeUnit.getUnitKey());
        }
    }

    public void setFormat(TypeFormat typeFormat)
    {
        int selectedRow = myTable.getSelectionIndex();
        if (selectedRow > -1)
        {
            TableItem item = myTable.getItem(selectedRow);
            item.setText(colNbById.get(COL_FORMAT), typeFormat.getFormat());
            item.setText(colNbById.get(COL_VALUE_OUT), typeFormat.getExample());
        }
    }

    public ArrayList<TypeUnit> getTypesP061()
    {
        return typesP061;
    }

    // FAE 9670 surlignage de la position des paramètres dans le fichier
    public void selectInViewFile(int debut, int fin)
    {
        int lign = 0;
        if (!Nemo.getInstance().getViewMain().getMyRulesManager()
            .isCruiseInADirectory()
                || Nemo.getInstance().getViewMain().getEditFileForm()
                    .isMultiStationFileChecked())
        {
            // fichier campagne
            lign =
                Nemo.getInstance().getDomain().getMedia().getModel()
                    .getFileHeader();
        }
        lign +=
            Nemo.getInstance().getDomain().getMedia()
                .getStationHeaderNumberLines() + 1;

        debut += Utils.taillesCumLignes[lign - 1];
        fin += Utils.taillesCumLignes[lign - 1];
        Nemo.getInstance().getViewMain().getEditDataForm().getTextAreaData()
            .setSelection(debut - 1, fin);
    }

}
