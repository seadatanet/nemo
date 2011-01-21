package fr.ifremer.nemo.ui.edit.data;

import java.util.ArrayList;
import java.util.HashMap;
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

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.domain.MediaDomain;
import fr.ifremer.nemo.ui.FilteredTableFactory;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.tables.ParameterFilteredTable;
import fr.ifremer.nemo.ui.tables.UnitFilteredTable;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSParameter;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;

public class TableData
{
    /** 
     * Parametres provenant des web services (fichiers xml mis à jour par les web services)
     * les parametres P091 sont lus depuis le fichier parameters.xml
     * pour les autres, il faut lire le fichier Pxxx.xml
     * 
     */
    private ArrayList<TypeData> typesWS;
    /** 
     * Parametres P061 provenant des web services (fichier P061.xml mis à jour par les web services)
     * ces parametres sont utilisés uniquement dans le cas où on est en P011viaP021, pour affecter une unité
     * 
     */
    private ArrayList<TypeUnit> typesP061;
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
     * liste des colonnes modifiables
     */
    public ArrayList<Integer> columnsWritable;
    /**
     * liste des titres de colonnes (ordonnée)
     */
    private HashMap<Integer, String> columnsMap;
    /**
     * constructeur
     * initialise automatiquement les typesWS
     *
     */
    private GroupVerticalParameters myGroupVerticalParameters;
   
    public TableData()
    {
        initTypesWS();
        initColumnsMap();
       
        // definit les colonnes pouvant être modfifiées par l'utilisateur
        columnsWritable = new ArrayList<Integer>();
        columnsWritable.add(2); //label_conversion
        columnsWritable.add(6);//label_format
        columnsWritable.add(7);//table_label_valueIn
        columnsWritable.add(8);//table_label_valueOut);
        myGroupVerticalParameters = Nemo.getInstance().getViewMain().getEditDataForm().getMyGroupeVerticalParameters();
    }
    /**
     * initialise les noms des colonnes
     *
     */
    private void initColumnsMap()
    {
        columnsMap = new HashMap<Integer, String>();
        columnsMap.put(0, Messages.table_label_code);
        columnsMap.put(1,Messages.table_label_unit);
        columnsMap.put(2,Messages.label_conversion);
        columnsMap.put(3,Messages.label_test);
        columnsMap.put(4,Messages.label_start);
        columnsMap.put(5,Messages.label_end);
        columnsMap.put(6,Messages.label_format);
        columnsMap.put(7,Messages.table_label_valueIn);
        columnsMap.put(8,Messages.table_label_valueOut);
        columnsMap.put(9,Messages.table_label_testIn);
        columnsMap.put(10,Messages.table_label_testOut);
        columnsMap.put(11,Messages.table_label_startFlag);
        columnsMap.put(12,Messages.table_label_endFlag);
        columnsMap.put(13,Global.EMPTY_STRING);// unite code non visible
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
     *
     */
    public void reinitTypes()
    {
        types = null;
        types = new ArrayList<TypeData>();
    }

    /**
     * ajoute un parametre à la liste des parametres de la table
     * @param typeData
     */
    public void addType(TypeData typeData)
    {
        types.add(typeData);
        System.out.println("add "+typeData.getEntryKey());
        myGroupVerticalParameters.addToAllCombo(typeData);
    }



    /**
     * creation de la table contenant les paramètres dans l'onglet data
     *
     */
    public void creationTable()
    {
        myTable = new Table(Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData(), SWT.FULL_SELECTION | SWT.H_SCROLL);

        myTable.setHeaderVisible(true);
        myTable.setLinesVisible(true);
        TableColumn tableColumn0 = new TableColumn(myTable, SWT.NONE);
        // tableColumn0.setWidth(110);
        tableColumn0.setWidth(100);
        tableColumn0.setText(columnsMap.get(0).toUpperCase());

        TableColumn tableColumn1 = new TableColumn(myTable, SWT.NONE);
        tableColumn1.setWidth(90);
        tableColumn1.setText(columnsMap.get(1));

        TableColumn tableColumn2 = new TableColumn(myTable, SWT.NONE);
        // tableColumn2.setWidth(100);
        tableColumn2.setWidth(70);
        tableColumn2.setText(columnsMap.get(2));
        tableColumn2.setAlignment(SWT.RIGHT);

        TableColumn tableColumn3 = new TableColumn(myTable, SWT.NONE);
        tableColumn3.setWidth(50);
        tableColumn3.setText(columnsMap.get(3));
        tableColumn3.setAlignment(SWT.RIGHT);

        TableColumn tableColumn4 = new TableColumn(myTable, SWT.NONE);
        // tableColumn4.setWidth(50);
        tableColumn4.setWidth(45);
        tableColumn4.setText(columnsMap.get(4));
        tableColumn4.setAlignment(SWT.RIGHT);

        TableColumn tableColumn5 = new TableColumn(myTable, SWT.NONE);
        // tableColumn5.setWidth(50);
        tableColumn5.setWidth(45);
        tableColumn5.setText(columnsMap.get(5));
        tableColumn5.setAlignment(SWT.RIGHT);

        TableColumn tableColumn6 = new TableColumn(myTable, SWT.NONE);
        // tableColumn6.setWidth(100);
        tableColumn6.setWidth(50);
        tableColumn6.setText(columnsMap.get(6));
        tableColumn6.setAlignment(SWT.RIGHT);

        TableColumn tableColumn7 = new TableColumn(myTable, SWT.NONE);
        // tableColumn7.setWidth(100);
        tableColumn7.setWidth(90);
        tableColumn7.setText(columnsMap.get(7));
        tableColumn7.setAlignment(SWT.RIGHT);

        TableColumn tableColumn8 = new TableColumn(myTable, SWT.NONE);
        // tableColumn8.setWidth(100);
        tableColumn8.setWidth(90);
        tableColumn8.setText(columnsMap.get(8));
        tableColumn8.setAlignment(SWT.RIGHT);

        TableColumn tableColumn9 = new TableColumn(myTable, SWT.NONE);
        // tableColumn9.setWidth(65);
        tableColumn9.setWidth(75);
        tableColumn9.setText(columnsMap.get(9));
        tableColumn9.setAlignment(SWT.RIGHT);

        TableColumn tableColumn10 = new TableColumn(myTable, SWT.NONE);
        // tableColumn10.setWidth(65);
        tableColumn10.setWidth(75);
        tableColumn10.setText(columnsMap.get(10));
        tableColumn10.setAlignment(SWT.RIGHT);

        TableColumn tableColumn11 = new TableColumn(myTable, SWT.NONE);
        tableColumn11.setWidth(65);
        tableColumn11.setText(columnsMap.get(11));
        tableColumn11.setAlignment(SWT.RIGHT);

        TableColumn tableColumn12 = new TableColumn(myTable, SWT.NONE);
        tableColumn12.setWidth(65);
        tableColumn12.setText(columnsMap.get(12));
        tableColumn12.setAlignment(SWT.RIGHT);
        
        // FAE 6542
        TableColumn tableColumn13 = new TableColumn(myTable, SWT.NONE);
        tableColumn13.setWidth(0);
        tableColumn13.setText(columnsMap.get(13));
        tableColumn13.setAlignment(SWT.RIGHT);
        

        initTable();

        // agrandissement de la table
        Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData().getShell().layout();

    }
    private void checkTypesWS()
    {
            initTypesWS();
    }
    
    
    /**
     * chargement des parametres pour l'affichage de la liste de choix des parametres
     * (menu Parameter List du tableau de l'onget data)
     *
     */
    public void initTypesWS()
    {
        typesWS = null;
        typesWS = new ArrayList<TypeData>();
        if (Nemo.getInstance().getViewMain().getMyRulesManager().getMyDataListType()!=DataListTypeEnum.P011_VIA_P021)

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
            // initialisation de la ArrayList des types des donnees a partir du fichier P091 parameters.xml
            WSParameter wsParameter = new WSParameter();
            wsParameter = NemoLists.getParameters()[i];

            TypeData type = new TypeData(wsParameter);
          
            // ajout dans liste
            typesWS.add(type);
        }
    }
    private void iniTypesWSP021()
    {
        //  init des données a partir du fichier P021 p021.xml
        CodeTableRecordType[] listP021 = NemoLists.getVocabList(Intern.list_BODCpdv);
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
     *
     */
    public void initTypesP061()
    {
        if (typesP061==null){
            typesP061 = new ArrayList<TypeUnit>();
            //  init des données a partir du fichier P021 p021.xml
            CodeTableRecordType[] listP061 = NemoLists.getVocabList(Intern.list_P061);
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

        Configuration config = Nemo.getInstance().getConfig();

        // On ajoute systématiquement le paramètre pression pour les profils
        // verticaux sauf si la liste de paramètres à utiliser est la P011
        //TODO BGT 2.6.3.2 deplacer ce code dans appel qd choix dataType coché
        if (Nemo.getInstance().getViewMain().getMyRulesManager().isProfile()
//                && config.getDataListToUse().equals(DataListEnum.P091))
                && Nemo.getInstance().getDomain().getData().getModel().getDataListType()==DataListTypeEnum.P091)
        {
            for (TypeData type : typesWS)
            {
                // ajout systematique de PRES
                if (type.getEntryKey().equals(Intern.paramPRES)
                        && Nemo.getInstance().getViewMain().getMyRulesManager().isProfile())
                {
                    // on rajoute PRES
                    System.out.println("ajout PRES dans le tableau");
                    addType(type);
                }
            }
        }
        razAndRecreateTable();
    }

    /**
     * initialisation de la table des paramètres de l'onglet data
     *
     */
    public void initTable()
    {

        /*
         * Remplir la table avec les tableItems avant de linker les editors !
         * 
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
         * Listener pour le clic sur la table
         * les colonnes modifiables sont
         * format
         * entree
         * sortie
         * conversion 
         */
        table.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event)
            {
                // clic 3e bouton (=clic droit): comportement par defaut
                if (event.button == 3) 
                {
                    return;
                }
                // calcul du numero de ligne selectionne
                Point pt = new Point(event.x, event.y);
                TableItem item = table.getItem(pt);

                if (item == null){
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
                        // edition des colonnes autorisées en écriture uniquement
                        if(columnsWritable.contains(colNumber))
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

                                            case SWT.Modify:
                                                if (!((Text)e.widget).getText().equals(s))                                            
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
                        } }
                    if (!visible)
                        return;
                    index++;
                }
            }
        });

        /** ****************************************************************************
         * creation du menu
         **************************************************************************** */ 
        Menu menu = new Menu(table.getShell(), SWT.POP_UP);

        // ========= Injecter debut/fin
        MenuItem itemInject = new MenuItem(menu, SWT.NONE);
        itemInject.setText(Messages.menu_label_setValue);
        itemInject.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                // table.forceFocus();
                Utils.injectData(Nemo.getInstance().getViewMain().getEditDataForm().getTextAreaData(), 4, 5);
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
                if (Nemo.getInstance().getViewMain().getMyRulesManager().getMyDataListType() != DataListTypeEnum.UNKNOWN){
                checkTypesWS();
                openMyParameterFilteredTable();
                }else{
                //TODO BGT
                    Utils.messageErreur("TODO aucun type de parametre choisi");
                }
            }
        });
        // ========= Liste des unités P061
        if (Nemo.getInstance().getViewMain().getMyRulesManager().getMyDataListType() == DataListTypeEnum.P011_VIA_P021)
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
                Utils.injectData(Nemo.getInstance().getViewMain().getEditDataForm().getTextAreaData(), 11, 12);

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
        Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData().getParent().layout(true, true);
        table.setLayout(new FillLayout());
        table.redraw();
    }

    /**
     * ouvre la fenetre de choix de parametres
     */
    public void openMyParameterFilteredTable()
    {
        getMyParameterFilteredTable().open(Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData().getParent());
    }
    public void openMyUnitFilteredTable(int rowIndex)
    {
        getMyUnitFilteredTable().open(Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData().getParent());
    }

    public void initTableData()
    {
        // creation des TableItems e partir de la ArrayList types

        // nouvelle creation des items
        for (int i = 0; i < types.size(); i++)
        {
            TableItem item = new TableItem(myTable, SWT.NONE);
            if (types.get(i).isP011())
            {
                item.setText(new String[] {
                        types.get(i).getEntryKeyP011() + Global.SEP
                        + types.get(i).getEntryTermP011(),
                        types.get(i).getUnite(), types.get(i).getConversion(),
                        types.get(i).getTest(),
                        String.valueOf(types.get(i).getDebut()),
                        String.valueOf(types.get(i).getFin()),
                        types.get(i).getFormat(), types.get(i).getEntree(),
                        types.get(i).getSortie(), types.get(i).getTestIn(),
                        types.get(i).getTestOut(), types.get(i).getDebutFlag(),
                        types.get(i).getFinFlag(),
               // FAE 6542
                types.get(i).getUniteCode()        
                });
            }
            else
            {

                item.setText(new String[] {types.get(i).getFormattedCodeAndName(),
                        types.get(i).getUnite(), types.get(i).getConversion(),
                        types.get(i).getTest(),
                        String.valueOf(types.get(i).getDebut()),
                        String.valueOf(types.get(i).getFin()),
                        types.get(i).getFormat(), types.get(i).getEntree(),
                        types.get(i).getSortie(), types.get(i).getTestIn(),
                        types.get(i).getTestOut(), types.get(i).getDebutFlag(),
                        types.get(i).getFinFlag(),
                        // FAE 6542
                        types.get(i).getUniteCode()        });
            }
        }

    }

    public void razTableData()
    {
        // effacement de tous les items de la table
        myTable.dispose();
      
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

    public void supprAllEltTableData()
    {
        System.out.println("suppr tout");
//        majTypeData();
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
//        Pattern p = Pattern.compile(Global.SEP);
//        String mots[] = p.split(typeData.getCode());
//        String code = mots[0];
//
//        
        System.out.println("suppr "+typeData.getEntryKey());

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
        String selection, ligne, resultat;
        double resultJEP;

        nbre = myTable.getItemCount();
        for (int i = 0; i < nbre; i++)
        {

            if ((!myTable.getItem(i).getText(4).equals(
                Global.EMPTY_STRING))
                && (!myTable.getItem(i).getText(2).equals(
                    Global.EMPTY_STRING)))
            {
                // on a selectionne un debut et une fin et il y a une conversion
                // e faire

                MediaDomain mediaDomain =
                    Nemo.getInstance().getDomain().getMedia();
                ligne =
                    Utils.lignesBrutes.get(mediaDomain.getModel()
                        .getFileHeader()
                        + mediaDomain.getStationHeaderNumberLines());

                selection =
                    ligne.substring(
                        Integer.valueOf(myTable.getItem(i)
                            .getText(4)) - 1,
                            Integer.valueOf(myTable.getItem(i)
                                .getText(5))).trim();

                // on formate puis on reaffiche la liste
                try
                {
                    String formatString =
                        myTable.getItem(i).getText(6);
                    String type =
                        formatString.substring(formatString.length() - 1);

                    if (selection.equals(myTable.getItem(i).getText(
                        7)))
                    {

                        // entree par defaut, le test est la valeur de sortie
                        // par defaut formatee
                        String fmOut = Global.EMPTY_STRING;
                        String outputDefaultValue =
                            myTable.getItem(i).getText(8);

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
                        parser.parseExpression(myTable.getItem(i)
                            .getText(2));
                        resultJEP = parser.getValue();

                        if (type.equals("f") || type.equals("F")) { //$NON-NLS-1$//$NON-NLS-2$
                            resultat =
                                String.format(myTable.getItem(i)
                                    .getText(6), new Float(resultJEP));
                        }
                        else if (type.equals("d") || type.equals("D")) { //$NON-NLS-1$ //$NON-NLS-2$
                            resultat =
                                String.format(myTable.getItem(i)
                                    .getText(6), (Double.valueOf(resultJEP))
                                    .intValue());
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

            if ((!myTable.getItem(i).getText(11).equals(
                Global.EMPTY_STRING))
                && (!myTable.getItem(i).getText(12).equals(
                    Global.EMPTY_STRING)))
            {
                /*
                 * ligne = Utils.lignesBrutes.get(Integer
                 * .valueOf(ViewMain.editFileForm.sectionEnteteFichier
                 * .getValue()) + DataModel.stationModel.getNumberOfLines()) +
                 * 1;
                 */
                MediaDomain mediaDomain =
                    Nemo.getInstance().getDomain().getMedia();
                ligne =
                    Utils.lignesBrutes.get(mediaDomain.getModel()
                        .getFileHeader()
                        + mediaDomain.getStationHeaderNumberLines() + 1);

                selection =
                    ligne.substring(
                        Integer.valueOf(myTable.getItem(i).getText(
                            11)) - 1,
                            Integer.valueOf(myTable.getItem(i).getText(
                                12))).trim();

                types.get(i).setTestIn(selection);

                String flagSDN = Global.ZERO;
                ArrayList<Flag> flags =
                    Nemo.getInstance().getDomain().getStation()
                    .getModel().getFlagTable().getFlags();
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
                types.get(i).setEntryKeyAndEntryTerm(myTable.getItem(i).getText(0));
            }
            types.get(i).setUnite(myTable.getItem(i).getText(1));
            types.get(i).setConversion(myTable.getItem(i).getText(2));
            types.get(i).setTest(myTable.getItem(i).getText(3));
            types.get(i).setDebut( Integer.valueOf(myTable.getItem(i).getText(4)) );
            types.get(i).setFin( Integer.valueOf(myTable.getItem(i).getText(5)) );
            types.get(i).setFormat(myTable.getItem(i).getText(6));
            types.get(i).setEntree(myTable.getItem(i).getText(7));
            types.get(i).setSortie(myTable.getItem(i).getText(8));

            if (!myTable.getItem(i).getText(11).equals(Global.EMPTY_STRING)
                    && !myTable.getItem(i).getText(12).equals(Global.EMPTY_STRING))
            {
                types.get(i).setDebutFlag(
                    myTable.getItem(i).getText(11));
                types.get(i).setFinFlag(
                    myTable.getItem(i).getText(12));
            }
            // FAE 6542
            types.get(i).setUniteCode(myTable.getItem(i).getText(13));
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
        if (myParameterFilteredTable==null)
        {
            FilteredTableFactory filteredFactory = Nemo.getInstance().getViewMain().getMyFilteredTableFactory();
            myParameterFilteredTable= (ParameterFilteredTable)filteredFactory.go(Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData().getParent(),
                Global.PARAMETER, null);
        }
        return myParameterFilteredTable;
    }

    public UnitFilteredTable getMyUnitFilteredTable()
    {
        UnitFilteredTable theTable = new UnitFilteredTable();
        return theTable;
    }
    public void setUnit(TypeUnit typeUnit)
    {
        int selectedRow = myTable.getSelectionIndex();
        if (selectedRow>-1){
            TableItem item =  myTable.getItem(selectedRow);
            item.setText(1, typeUnit.getTerm());
            // FAE 6542
            item.setText(13, typeUnit.getUnitKey());
        }
    }
    public ArrayList<TypeUnit> getTypesP061()
    {
        return typesP061;
    }
}
