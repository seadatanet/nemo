/* ============================================================================
 * Nom du fichier	: SubParameterFilteredTable.java
 * Auteur			: ycaudal
 * Date				: 22 déc. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.Global;
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
 * @author ycaudal
 * 
 */
public class SubParameterFilteredTable extends AbstractFilteredTable
{

    private final ArrayList<TypeData> _datas;

    private final ArrayList<TypeData> _datasFiltered;

    private final TypeData _typeParameter;

//    private Table _table;

    private boolean _validate;

    private TypeData _selectedData;
    /**
     * type de parametres utilisé (P091, P011 via P091, P011 via P021...)
     */
    private final DataListTypeEnum _typeToUse;
    

    public SubParameterFilteredTable(TypeData typeParameter, DataListTypeEnum typeToUse)
    {
        _typeParameter = typeParameter;
        _datas = new ArrayList<TypeData>();
        _datasFiltered = new ArrayList<TypeData>();
        _typeToUse = typeToUse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createDataList()
    {
        if (_typeToUse == DataListTypeEnum.P011_VIA_P091)
        {
           createDataList_P011_via_P091();
        }
        else if(_typeToUse == DataListTypeEnum.P011_VIA_P021)
        {
            createDataList_P011_via_P021();
        }
        
        
        Collections.sort(_datas, new Comparator<TypeData>(){

            public int compare(TypeData arg0, TypeData arg1)
            {
                return arg0.getEntryKeyP011().compareTo(arg1.getEntryKeyP011());
            }

        });

        for (TypeData elmt : _datas)
        {
            _datasFiltered.add(elmt);
        }
    }
    
    /**
     * creation de la liste de parametres P011 correspondant au paramètre P021 choisi dans le panneau précedent
     */
    private void createDataList_P011_via_P021()
    {
    	SdnVocabularyManager sdnVocabsMgr = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
    	List<IElement> listeP021_P011;
    	try {
    		listeP021_P011 = sdnVocabsMgr.getVocabMatchs( Intern.list_P011, Intern.list_P021, _typeParameter.getEntryKey()); //order ok


    		for (IElement record : listeP021_P011)
    		{
    			TypeData newElement = new TypeData(_typeParameter);

    			newElement.setIsP011(true);

    			newElement.setEntryKeyP011(record.getKey());


    			newElement.setEntryTermP011(record.getLabel());

    			_datas.add(newElement);

    		}
    	} catch (VocabularyException e) {
    		Nemo.getInstance().getLogger().error(e.getMessage());    	
    	}
    }
    /**
     * creation de la liste de parametres P011 correspondant au paramètre P091 choisi dans le panneau précedent
     * on passe par le type P021
     */
    private void createDataList_P011_via_P091()
    {
    	SdnVocabularyManager sdnVocabsMgr = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
    	List<IElement> listeCompleteP091_P021_P011 = new ArrayList<IElement>();
    	List<IElement> listeP091_P021;
    	try {
    		listeP091_P021 = sdnVocabsMgr.getVocabMatchs( Intern.list_P021, Intern.list_P091,_typeParameter.getEntryKey());//order ok


    		for (IElement liste : listeP091_P021)
    		{
    			listeCompleteP091_P021_P011.addAll(sdnVocabsMgr.getVocabMatchs(
    					Intern.list_P011, Intern.list_P021, liste.getKey())); //order ok
    		}

    		for (IElement code : listeCompleteP091_P021_P011)
    		{
    			TypeData newElement = new TypeData(_typeParameter);



    			newElement.setIsP011(true);

    			newElement.setEntryKeyP011(code.getKey());

    			newElement.setEntryTermP011(code.getLabel());

    			_datas.add(newElement);

    		}
    	} catch (VocabularyException e) {
    		Nemo.getInstance().getLogger().error(e.getMessage());    	
    	}

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void createTable()
    {
    	super.createTable();
//        _table = new Table(top, SWT.FULL_SELECTION);
//
//        _table.setHeaderVisible(true);
//        _table.setLinesVisible(true);
//        _table.setBounds(new Rectangle(3, 45, 720, 237));

        // creation des colonnes...
        TableColumn tableColumn = new TableColumn(myFiltreTable, SWT.NONE);
        tableColumn.setWidth(700);
        tableColumn.setText(Messages.table_label_codeName);

        // comme la fenetre est plus large on decale egalement les boutons.
//        Rectangle bounds = filtreButtonSelectionner.getBounds();
//        bounds.x+=480;
//        filtreButtonSelectionner.setBounds(bounds);
//        
//        bounds = filtreButtonCancel.getBounds();
//        bounds.x+=480;
//        filtreButtonCancel.setBounds(bounds);
        
        populateTableWithFilteredList();
    }

    @Override
    protected void populateTableWithFilteredList(){
    

        for (int i = 0; i < _datasFiltered.size(); i++)
        {
            TableItem item = new TableItem(myFiltreTable, SWT.NONE);
            TypeData currentTypeData = _datasFiltered.get(i);
            item.setText(new String[] {
                    currentTypeData.getEntryKeyP011() + Global.SEP
                            + currentTypeData.getEntryTermP011(),
                    currentTypeData.getEntryKey()});
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void filterAction(String value)
    {

        int size = _datas.size();
        String regexp;
        _datasFiltered.clear();

        // remplissage de la liste filtree
        for (int i = 0; i < _datas.size(); i++)
        {
            regexp = "^.*" + value + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$

            Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);

            TypeData currentTypeData = _datas.get(i);

            Matcher m = p.matcher(currentTypeData.getEntryKeyP011());
            Matcher m2 = p.matcher(currentTypeData.getEntryTermP011());

            if (m.matches() || m2.matches())
            {
                // ajout dans la liste filtree
                _datasFiltered.add(_datas.get(i));
            }
        }

        // affichage de la liste filtree
        refreshTable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle()
    {
//        return _typeParameter.getCode().split(Global.MOINS)[0].trim() + Global.SEP 
//        + Messages.table_subparameters
//        + Global.SEP
//        + Messages.edit_data_P011;
        
        return _typeParameter.getEntryKey()
        + Global.SPACE
        + Messages.table_subparameters
        + Global.SEP
        + Messages.edit_data_P011;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void selectAction()
    {
        int index = myFiltreTable.getSelectionIndex();
        if (index != -1)
        {
            _selectedData = _datasFiltered.get(index);
            _validate = true;
            shell.close();
        }
    }

    public boolean isValidated()
    {
        return _validate;
    }

    public TypeData getSelectedData()
    {
        return _selectedData;
    }
}
