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
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.Global;
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

    private TypeData _typeP091;

    private Table _table;

    private boolean _validate;

    private TypeData _selectedData;

    public SubParameterFilteredTable(TypeData typeP091)
    {
        _typeP091 = typeP091;
        _datas = new ArrayList<TypeData>();
        _datasFiltered = new ArrayList<TypeData>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createDataList()
    {

        List<CodeTableRecordType> listeCompleteP091_P021_P011 =
            new ArrayList<CodeTableRecordType>();

        List<CodeTableRecordType> listeP091_P021 =
            NemoLists.getVocabMatchs("P091", "P021", _typeP091.getCode().split( //$NON-NLS-1$ //$NON-NLS-2$
                Global.SEP)[0]);

        for (CodeTableRecordType liste : listeP091_P021)
        {
            listeCompleteP091_P021_P011.addAll(NemoLists.getVocabMatchs("P021", //$NON-NLS-1$
                "P011", liste.getEntryTermAbbr())); //$NON-NLS-1$
        }

        for (CodeTableRecordType code : listeCompleteP091_P021_P011)
        {
            TypeData newElement = new TypeData(_typeP091);

            String entryKey = code.getEntryKey();
            String[] entryKeySplit = entryKey.split("/"); //$NON-NLS-1$

            newElement.setP011(true);

            newElement.setCodeP011(entryKeySplit[entryKeySplit.length - 1]);

            newElement.setAbbr(code.getEntryTermAbbr());

            newElement.setNomP011(code.getEntryTerm());

            _datas.add(newElement);

        }
        
        Collections.sort(_datas, new Comparator<TypeData>(){

            public int compare(TypeData arg0, TypeData arg1)
            {
                return arg0.getCodeP011().compareTo(arg1.getCodeP011());
             }
            
        });
        
        for (TypeData elmt : _datas)
            _datasFiltered.add(elmt);

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
        _table.setBounds(new Rectangle(3, 45, 298, 237));

        // creation des colonnes...
        TableColumn tableColumn = new TableColumn(_table, SWT.NONE);
        tableColumn.setWidth(200);
        tableColumn.setText(Messages.table_label_codeName);

        TableColumn tableColumn2 = new TableColumn(_table, SWT.NONE);
        tableColumn2.setWidth(81);
        tableColumn2.setText("Abbr");

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
                    currentTypeData.getCodeP011() + Global.SEP
                            + currentTypeData.getNomP011(),
                    currentTypeData.getAbbr()});
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

            Matcher m = p.matcher(currentTypeData.getCodeP011());
            Matcher m2 = p.matcher(currentTypeData.getNomP011());
            Matcher m3 = p.matcher(currentTypeData.getAbbr());

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
        return Messages.table_subparameters; //$NON-NLS-1$
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
