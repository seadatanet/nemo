/* ============================================================================
 * Nom du fichier	: FileSection.java
 * Auteur			: dboulang
 * Date				: 19 mars 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique représentant une section dans l'onglet File. Cette section permet de choisir l'emplacement des titres de colonnes de données pour les fichiers csv.
 * 
 * @author sbregent
 */
public class FileDataColumnTitlesSection extends FileSection
{
    protected Composite compo;
    protected SetText setter;
    protected Button checkUseTableForCsv;
    protected Button checkColTitlesInInputFile;

    public FileDataColumnTitlesSection(String name, String title, String description, EditFileForm form)
    {
        super(name, title, description, form);

    }

    @Override
	public void addSetter(String stringLabel, String defaultValue)
    {
        setter = new SetTextLineNb(stringLabel, defaultValue, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawSection(Composite parent, boolean filled)
    {
        compo = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compo);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(compo);

        checkUseTableForCsv = new Button(compo, SWT.CHECK);
        checkUseTableForCsv.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
        checkUseTableForCsv.setText(Messages.edit_file_columnTitlesSectionOnOff);
        checkUseTableForCsv.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent g)
			{
				if (!checkUseTableForCsv.getSelection()){
		    		checkColTitlesInInputFile.setSelection(false);
		    	}
				Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);
				Utils.invalidEtapes();
			}
		});

        checkColTitlesInInputFile = new Button(compo, SWT.CHECK);
        checkColTitlesInInputFile.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
        checkColTitlesInInputFile.setText(Messages.edit_file_columnTitlesSection);
        checkColTitlesInInputFile.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent g)
			{
				Utils.invalidEtapes();
				setter.setEnabled(checkColTitlesInInputFile.getSelection());
				// will redraw the input data without/without title columns
				Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);
			}
		});

       
        setter.draw(compo, filled);
        setter.setEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void razValues()
    {
        if (setter != null)
            setter.razValues();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
        super.dispose();
        if (compo != null)
            compo.dispose();
        if (setter != null)
            setter.dispose();
    }

    @Override
	public boolean checkMandatory()
    {
        boolean ok = true;
        String log =
            Global.RN + "Section : " + title.trim() + Global.RN + "==>"; //$NON-NLS-1$  //$NON-NLS-2$

        if (setter != null)
        {
            ok = setter.checkMandatoryValue();
            log = log + Messages.error_notValid;
        }

        if (!ok)
            Utils.messageErreurAppend(log);

        return ok;
    }

    @Override
	public String getValue()
    {
        return setter.getValue();
    }

    @Override
	public void setValue(String value)
    {
        setter.setValue(value);
    }
    public boolean isColTitlesInStationHeader(){
    	return checkColTitlesInInputFile.getSelection();
    }
    public void setColTitlesInInputFile(boolean select){
    	checkColTitlesInInputFile.setSelection(select);
    }
    
    
    public boolean isTableEnabledForCsv(){
    	return checkUseTableForCsv.getSelection();
    }
    public void setTableEnabledForCsv(boolean select){
    	checkUseTableForCsv.setSelection(select);
    }
    public void enableTextSetter(boolean enabled){
    	setter.setEnabled(enabled);
    }
}
