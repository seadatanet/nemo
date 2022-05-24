package fr.ifremer.nemo.ui.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.ui.edit.data.TypeStandardName;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;
import sdn.vocabulary.interfaces.IElement;
import sdn.vocabulary.interfaces.VocabularyException;


/**
 * Description: TODO
 * 
 * @author dcapitai
 * 
 */
public class StandardNameFilteredTable extends AbstractFilteredTable
{

    /**
     *  reference sur la tableData (de l'onglet data) depuis laquelle cette table a été ouverte
     */
    private final TableData myTableDataRef;

    /**
     * copie de la liste des parametres typesWS pouvant être filtrée
     */
    private ArrayList<TypeStandardName> listeFiltree;
    
    
    /**
     * copie de la liste des parametres typesWS pouvant être filtrée
     */
    private ArrayList<TypeStandardName> listeOrigine;
    
    
    private final TypeData _typeParameter;
    
    /**
     * type de parametres utilisé (, P011 via P091, P011 via P021)
     */
    private final DataListTypeEnum _typeToUse;
    
    /**
     * @param comboRecherche
     */
    public StandardNameFilteredTable(TypeData typeParameter, DataListTypeEnum typeToUse)
    {
        _typeToUse = typeToUse;
        _typeParameter = typeParameter;
        myTableDataRef = Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createDataList()
    {
        // liste filtree initialisee e typesWS au depart
        initTypesP07();
        reloadListeFiltree();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createTable()
    {
    	super.createTable();
//        myFiltreTable = new Table(top, SWT.FULL_SELECTION);
//
//        myFiltreTable.setHeaderVisible(true);
//        myFiltreTable.setLinesVisible(true);
//        myFiltreTable.setBounds(new Rectangle(3, 45, 400, 237));

        // creation des colonnes...
        TableColumn tableColumn = new TableColumn(myFiltreTable, SWT.NONE);
        tableColumn.setWidth(381);
        tableColumn.setText("Standard Name");

        populateTableWithFilteredList();
    }
    @Override
    protected void populateTableWithFilteredList(){
    	 for (int i = 0; i < listeFiltree.size(); i++)
         {
             TableItem item = new TableItem(myFiltreTable, SWT.NONE);
             item.setText(new String[] {
                     listeFiltree.get(i).getTerm()
                     });
         }
    }
    /**
     * initialize the P07 types to use as units choice
     */
    public void initTypesP07()
    {
    	SdnVocabularyManager sdnVocabsMgr = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
    	listeOrigine= new ArrayList<TypeStandardName>();
    	Collection<IElement> listP07 = new ArrayList<IElement>();
    	try {
    		if(_typeToUse == DataListTypeEnum.P011_VIA_P021)
    		{
    			listP07 = sdnVocabsMgr.getVocabMatchs(Intern.list_P07,Intern.list_P021,  _typeParameter.getEntryKey());//order ok
    		}
    		else
    		{
    			List<IElement> listeP091_P021 = sdnVocabsMgr.getVocabMatchs( Intern.list_P021,Intern.list_P091, _typeParameter.getEntryKey());//order ok

    			for (IElement liste : listeP091_P021)
    			{
    				listP07 = sdnVocabsMgr.getVocabMatchs(Intern.list_P07, Intern.list_P021, liste.getKey());//order ok
    			}
    		}
    	} catch (VocabularyException e) {
    		Nemo.getInstance().getLogger().error(e.getMessage());    	
    	}

    	TypeStandardName typeStandardName;
    	for (IElement record : listP07)
    	{
    		typeStandardName = new TypeStandardName(record);
    		listeOrigine.add(typeStandardName);
    	}


    }

    /**
     * recharge la liste des paramètres filtrée depuis la liste des typesWS
     * (= annule le filtre)
     */
    private void reloadListeFiltree()
    {
        listeFiltree = new ArrayList<TypeStandardName>();
        // liste filtree initialisee e typesWS au depart
        int taille = listeOrigine.size();
        for (int i = 0; i < taille; i++)
        {
            listeFiltree.add(listeOrigine.get(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void filterAction(String chaine)
    {
        ArrayList<TypeStandardName> filtree;
        String regexp;

        filtree = new ArrayList<TypeStandardName>();


        // remplissage de la liste filtree
        for (int i = 0; i < listeOrigine.size(); i++)
        {
            regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
            Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(listeOrigine.get(i).getTerm());
            if (m.matches())
            {
                // ajout dans la liste filtree
                filtree.add(listeOrigine.get(i));
            }
        }
        listeFiltree = filtree;
        // affichage de la liste filtree
//        razTable(myFiltreTable);
//        createTable();
        refreshTable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle()
    {
        return Messages.edit_data_P07;
    }

    /**
     * action appelée lorsqu'un parametre est selectionné dans le panneau ParameterFilteredTable
     * -> ajoute la parametre au tableau des data ou ouvre la liste des SubParameters
     * {@inheritDoc}
     */
    @Override
    protected void selectAction()
    {

        TypeStandardName typeStandardName;

        int index = myFiltreTable.getSelectionIndex();
        typeStandardName = listeFiltree.get(index);

        // reinit validation
        Utils.invalidEtapes();

        myTableDataRef.setStandardName(typeStandardName);
        myTableDataRef.majTypeData();

        // refresh de la table
        myTableDataRef.razAndRecreateTable();
        shell.close();
    }
   

}
