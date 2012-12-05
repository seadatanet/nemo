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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;

/**
 * Description: TODO
 * 
 * @author ycaudal
 * 
 */
public class SubParameterFilteredTable extends AbstractFilteredTable
{

    private ArrayList<TypeData> _datas;

    private ArrayList<TypeData> _datasFiltered;

    private TypeData _typeParameter;

    private Table _table;

    private boolean _validate;

    private TypeData _selectedData;
    /**
     * type de parametres utilisé (P091, P011 via P091, P011 via P021...)
     */
    private DataListTypeEnum _typeToUse;
    

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
        List<CodeTableRecordType> listeP021_P011 =
            NemoLists.getVocabMatchs(Intern.list_BODCpdv, Intern.list_P011, _typeParameter.getEntryTermAbbr());

        for (CodeTableRecordType record : listeP021_P011)
        {
            TypeData newElement = new TypeData(_typeParameter);

            String entryKey = record.getEntryKey();
            String[] entryKeySplit = entryKey.split("/"); //$NON-NLS-1$

            newElement.setIsP011(true);

            newElement.setEntryKeyP011(entryKeySplit[entryKeySplit.length - 1]);

            newElement.setEntryTermAbbr(record.getEntryTermAbbr());

            newElement.setEntryTermP011(record.getEntryTerm());

            _datas.add(newElement);

        }
    }
    /**
     * creation de la liste de parametres P011 correspondant au paramètre P091 choisi dans le panneau précedent
     * on passe par le type P021
     */
    private void createDataList_P011_via_P091()
    {
        List<CodeTableRecordType> listeCompleteP091_P021_P011 = new ArrayList<CodeTableRecordType>();
        //list_BODCpdv = P021
//        List<CodeTableRecordType> listeP091_P021 =
//            NemoLists.getVocabMatchs(Intern.list_P091, Intern.list_BODCpdv, _typeParameter.getCode().split( 
//                Global.SEP)[0]);
        List<CodeTableRecordType> listeP091_P021 =
            NemoLists.getVocabMatchs(Intern.list_P091, Intern.list_BODCpdv, _typeParameter.getEntryKey());

        for (CodeTableRecordType liste : listeP091_P021)
        {
            listeCompleteP091_P021_P011.addAll(NemoLists.getVocabMatchs(Intern.list_BODCpdv, 
                Intern.list_P011, liste.getEntryTermAbbr())); 
        }

        for (CodeTableRecordType code : listeCompleteP091_P021_P011)
        {
            TypeData newElement = new TypeData(_typeParameter);

            String entryKey = code.getEntryKey();
            String[] entryKeySplit = entryKey.split("/"); //$NON-NLS-1$

            newElement.setIsP011(true);

            newElement.setEntryKeyP011(entryKeySplit[entryKeySplit.length - 1]);

            newElement.setEntryTermAbbr(code.getEntryTermAbbr());

            newElement.setEntryTermP011(code.getEntryTerm());

            _datas.add(newElement);

        }

       
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void createTable()
    {
        _table = new Table(top, SWT.FULL_SELECTION);

        _table.setHeaderVisible(true);
        _table.setLinesVisible(true);
        _table.setBounds(new Rectangle(3, 45, 800, 237));

        // creation des colonnes...
        TableColumn tableColumn = new TableColumn(_table, SWT.NONE);
        tableColumn.setWidth(695);
        tableColumn.setText(Messages.table_label_codeName);

        TableColumn tableColumn2 = new TableColumn(_table, SWT.NONE);
        tableColumn2.setWidth(100);
        tableColumn2.setText("Abbr");

        // comme la fenetre est plus large on decale egalement les boutons.
        Rectangle bounds = filtreButtonSelectionner.getBounds();
        bounds.x+=580;
        filtreButtonSelectionner.setBounds(bounds);
        
        bounds = filtreButtonCancel.getBounds();
        bounds.x+=580;
        filtreButtonCancel.setBounds(bounds);
        
        refreshTable();
    }

    protected void refreshTable()
    {
        _table.removeAll();

        for (int i = 0; i < _datasFiltered.size(); i++)
        {
            TableItem item = new TableItem(_table, SWT.NONE);
            TypeData currentTypeData = _datasFiltered.get(i);
            item.setText(new String[] {
                    currentTypeData.getEntryKeyP011() + Global.SEP
                            + currentTypeData.getEntryTermP011(),
                    currentTypeData.getEntryTermAbbr()});
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
            Matcher m3 = p.matcher(currentTypeData.getEntryTermAbbr());

            if (m.matches() || m2.matches() || m3.matches())
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
        int index = _table.getSelectionIndex();
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
