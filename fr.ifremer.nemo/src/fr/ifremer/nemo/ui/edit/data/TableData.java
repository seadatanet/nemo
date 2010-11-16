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

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.config.Configuration.DataListEnum;
import fr.ifremer.nemo.domain.MediaDomain;
import fr.ifremer.nemo.ui.FilteredTableFactory;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.tables.ParameterFilteredTable;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
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
     * constructeur
     * initialise automatiquement les typesWS
     *
     */
    public TableData()
    {
        initTypesWS();
    }
    
    
    public void reinitTable()
    {
        if (myTable != null)
        {
        //TODO BGT verifier que initTypesWS est vraiment inutile ici
//            initTypesWS();
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
//        initTypesWS();
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
    }
    
    
    /**
     * recharge la liste des paramètres filtrée depuis la liste des typesWS
     * (= annule le filtre)
     *
     */
//    public void reloadListeFiltree()
//    {
//        listeFiltree = new ArrayList<TypeData>();
//        // liste filtree initialisee e typesWS au depart
//        int taille = typesWS.size();
//        for (int i = 0; i < taille; i++)
//        {
//            listeFiltree.add(typesWS.get(i));
//        }
//    }
 
    /**
     * creation de la table contenant les paramètres dans l'onglet data
     *
     */
    public void creationTable()
    {
        myTable = new Table(Nemo.getInstance().getViewMain().compositeData, SWT.FULL_SELECTION | SWT.H_SCROLL);

        myTable.setHeaderVisible(true);
        myTable.setLinesVisible(true);
        TableColumn tableColumn0 = new TableColumn(myTable, SWT.NONE);
        // tableColumn0.setWidth(110);
        tableColumn0.setWidth(100);
        tableColumn0.setText(Messages.table_label_code.toUpperCase());

        TableColumn tableColumn1 = new TableColumn(myTable, SWT.NONE);
        tableColumn1.setWidth(90);
        tableColumn1.setText(Messages.table_label_unit);

        TableColumn tableColumn2 = new TableColumn(myTable, SWT.NONE);
        // tableColumn2.setWidth(100);
        tableColumn2.setWidth(70);
        tableColumn2.setText(Messages.label_conversion);
        tableColumn2.setAlignment(SWT.RIGHT);

        TableColumn tableColumn3 = new TableColumn(myTable, SWT.NONE);
        tableColumn3.setWidth(50);
        tableColumn3.setText(Messages.label_test);
        tableColumn3.setAlignment(SWT.RIGHT);

        TableColumn tableColumn4 = new TableColumn(myTable, SWT.NONE);
        // tableColumn4.setWidth(50);
        tableColumn4.setWidth(45);
        tableColumn4.setText(Messages.label_start);
        tableColumn4.setAlignment(SWT.RIGHT);

        TableColumn tableColumn5 = new TableColumn(myTable, SWT.NONE);
        // tableColumn5.setWidth(50);
        tableColumn5.setWidth(45);
        tableColumn5.setText(Messages.label_end);
        tableColumn5.setAlignment(SWT.RIGHT);

        TableColumn tableColumn6 = new TableColumn(myTable, SWT.NONE);
        // tableColumn6.setWidth(100);
        tableColumn6.setWidth(50);
        tableColumn6.setText(Messages.label_format);
        tableColumn6.setAlignment(SWT.RIGHT);

        TableColumn tableColumn7 = new TableColumn(myTable, SWT.NONE);
        // tableColumn7.setWidth(100);
        tableColumn7.setWidth(90);
        tableColumn7.setText(Messages.table_label_valueIn);
        tableColumn7.setAlignment(SWT.RIGHT);

        TableColumn tableColumn8 = new TableColumn(myTable, SWT.NONE);
        // tableColumn8.setWidth(100);
        tableColumn8.setWidth(90);
        tableColumn8.setText(Messages.table_label_valueOut);
        tableColumn8.setAlignment(SWT.RIGHT);

        TableColumn tableColumn9 = new TableColumn(myTable, SWT.NONE);
        // tableColumn9.setWidth(65);
        tableColumn9.setWidth(75);
        tableColumn9.setText(Messages.table_label_testIn);
        tableColumn9.setAlignment(SWT.RIGHT);

        TableColumn tableColumn10 = new TableColumn(myTable, SWT.NONE);
        // tableColumn10.setWidth(65);
        tableColumn10.setWidth(75);
        tableColumn10.setText(Messages.table_label_testOut);
        tableColumn10.setAlignment(SWT.RIGHT);

        TableColumn tableColumn11 = new TableColumn(myTable, SWT.NONE);
        tableColumn11.setWidth(65);
        tableColumn11.setText(Messages.table_label_startFlag);
        tableColumn11.setAlignment(SWT.RIGHT);

        TableColumn tableColumn12 = new TableColumn(myTable, SWT.NONE);
        tableColumn12.setWidth(65);
        tableColumn12.setText(Messages.table_label_endFlag);
        tableColumn12.setAlignment(SWT.RIGHT);

        initTable();

        // agrandissement de la table
        Nemo.getInstance().getViewMain().compositeData.getShell().layout();

    }

    public void initTypesWS()
    {
        System.out.println("initTypesWS");
        // TODO BGT code ci dessous pour test uniquement (FAE5863 en cours)

        // initialisation de la ArrayList des types des donnees e partir du fichier parameters.xml
//        typesWS = null;
//        typesWS = new ArrayList<TypeData>();
//
//        int taille = NemoLists.getParameters().length;
//
//        for (int i = 0; i < taille; i++)
//        {
//            WSParameter wsParameter = new WSParameter();
//            wsParameter = NemoLists.getParameters()[i];
//
//            // new type
//            TypeData type = new TypeData();
//
//            // parametres
//            type.setCode(wsParameter.getCode() + Global.SEP
//                    + wsParameter.getName());
//            type.setUnite(wsParameter.getUnit());
//            type.setFormat(wsParameter.getFormat());
//            type.setEntree(Global.EMPTY_STRING);
//            type.setSortie(wsParameter.getDefaultValue());
//
//            // ajout dans liste
//            typesWS.add(type);
//        }
        
        
        
        //**********************************************************************************
        // init des données a partir du fichier P021
        typesWS = null;
        typesWS = new ArrayList<TypeData>();
        CodeTableRecordType[] listP021 = NemoLists.getVocabList(Intern.list_BODCpdv);
        TypeData type;
        String code;
        String name;
        for (CodeTableRecordType record : listP021)
        {
            type = new TypeData();
            String entryKey = record.getEntryKey();
            String[] entryKeySplit = entryKey.split("/"); //$NON-NLS-1$
            code = entryKeySplit[entryKeySplit.length - 1];
            
            name = record.getEntryTerm();
            type.setIsP011(false);
            type.setCode(code +  Global.SEP+name);
            type.setAbbr(record.getEntryTermAbbr());
//            type.setNomP011(record.getEntryTerm());
            // ajout dans liste
          typesWS.add(type);
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
        if (Nemo.getInstance().getViewMain().getEditFileForm().radProfilVertical.getSelection()
                && config.getDataList().equals(DataListEnum.P091))
        {
            for (TypeData type : typesWS)
            {
                // ajout systematique de PRES
                if (type.getCode().startsWith(Intern.paramPRES)
                        && Nemo.getInstance().getViewMain().getEditFileForm().radProfilVertical.getSelection())
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
                            text.setText(itemTmp.getText(i));
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
                            editor.setEditor(text, itemTmp, i);
                            text.selectAll();

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
                Utils.injectData(Nemo.getInstance().getViewMain().textAreaData, 4, 5);
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
                openMyParameterFilteredTable();
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
                Utils.injectData(Nemo.getInstance().getViewMain().textAreaData, 11, 12);

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
        
        
        table.setMenu(menu);
        Utils.forceMenu(table, menu);

        // agrandissement de la table
        Nemo.getInstance().getViewMain().compositeData.getParent().layout(true, true);
        table.setLayout(new FillLayout());
        table.redraw();
    }
 
    /**
     * ouvre la fenetre de choix de parametres
     */
    public void openMyParameterFilteredTable()
    {
       getMyParameterFilteredTable().open(Nemo.getInstance().getViewMain().compositeData.getParent());
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
                        types.get(i).getCodeP011() + Global.SEP
                                + types.get(i).getNomP011(),
                        types.get(i).getUnite(), types.get(i).getConversion(),
                        types.get(i).getTest(),
                        String.valueOf(types.get(i).getDebut()),
                        String.valueOf(types.get(i).getFin()),
                        types.get(i).getFormat(), types.get(i).getEntree(),
                        types.get(i).getSortie(), types.get(i).getTestIn(),
                        types.get(i).getTestOut(), types.get(i).getDebutFlag(),
                        types.get(i).getFinFlag()});
            }
            else
            {

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

    public void supprEltTableData()
    {
        // suppression de la ligne selectionnee
        int indice = myTable.getSelectionIndex();
        // d'abord mise e jour des donnees
        majTypeData();

        // on supprime l'element e l'indice donne
        // split sur " - "
        Pattern p = Pattern.compile(Global.SEP);
        String mots[] = p.split(types.get(indice).getCode());
        String code = mots[0];

        // On peut maintenant retirer PRES (FAE 3228)

        // pas forcement de - dans le nom du type
        // if (!code.equals(Intern.paramPRES)
        // || ViewMain.radSerieTempo.getSelection()
        // || ViewMain.radTrajectoire.getSelection())
        // {

        types.remove(indice);
        // }
        // else
        // {
        // Utils.messageErreurAppend(Messages.error_data_removePRES);
        // }

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
                    Nemo.getInstance().getDomain().getCruise().getStation()
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
                types.get(i).setCode(myTable.getItem(i).getText(0));
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
            myParameterFilteredTable= (ParameterFilteredTable)filteredFactory.go(Nemo.getInstance().getViewMain().compositeData.getParent(),
                Global.PARAMETER, null);
        }
        return myParameterFilteredTable;
    }
}
