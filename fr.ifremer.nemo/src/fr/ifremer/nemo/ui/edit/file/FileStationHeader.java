/* ============================================================================
 * Nom du fichier	: FileStationHeader.java
 * Auteur			: dboulang
 * Date				: 30 avr. 09
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
 * Description: "Station Header" Section in File Description
 * 
 * @author dboulang
 */
public class FileStationHeader extends FileSection
{
    protected Composite subCompo, col01, col02;
    protected Button radNumber, radIndicator;
    protected SetText setterIndicator, setterLinesBeforeData;
//	private Button useSeparatorInStationHeader;
   
    

   
	

	/**
     * @param shell
     * @param name
     * @param title
     * @param description
     */
    public FileStationHeader(String name, String title, String description, EditFileForm form)
    {
        super(name, title, description, form);

        setterIndicator =
            new SetText(Messages.edit_file_enteteStation_indicator,
                Global.EMPTY_STRING, true);
        setterIndicator.setTextSize(Global.SIZE_SECTION / 2 - 10);

        setterLinesBeforeData =
            new SetText(Messages.edit_file_enteteStation_linesBeforeData,
                Global.ZERO, false);
        setterLinesBeforeData.setTextSize(Global.SIZE_SECTION / 2 - 10);
    }

    private void addRadListener(final Button radio)
    {
        radio
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent g)
                {
                    selectNumber(radNumber.getSelection());
                    Utils.invalidEtapes();
                }
            });
    }

    public void selectNumber(boolean selected)
    {

        radNumber.setSelection(selected);
        if (setter != null)
            setter.setEnabled(selected);

        radIndicator.setSelection(!selected);
        setterIndicator.setEnabled(!selected);
        setterLinesBeforeData.setEnabled(!selected);
    }

    public boolean isSelectNumber()
    {
        return radNumber.getSelection();
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
    
//        useSeparatorInStationHeader = new Button(compo, SWT.CHECK);
//    	useSeparatorInStationHeader.setText(Messages.view_file_separator);
//    	useSeparatorInStationHeader.setEnabled(false);
    	
        radNumber = new Button(compo, SWT.RADIO);
        radNumber.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
        radNumber.setText(Messages.edit_file_enteteStation_radNumber);

        setter.draw(compo, filled);

        radIndicator = new Button(compo, SWT.RADIO);
        radIndicator.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
        radIndicator.setText(Messages.edit_file_enteteStation_radIndicator);

        subCompo = new Composite(compo, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(subCompo);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(subCompo);

        col01 = new Composite(subCompo, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(col01);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(col01);

        setterIndicator.draw(col01, filled);

        col02 = new Composite(subCompo, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(col02);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(col02);

        setterLinesBeforeData.draw(col02, filled);
        
       

        selectNumber(true);
        addRadListener(radNumber);
        addRadListener(radIndicator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void razValues()
    {
        super.razValues();
        setterIndicator.razValues();
        setterLinesBeforeData.razValues();
        selectNumber(true);
//        useSeparatorInStationHeader.setSelection(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
        super.dispose();
        radNumber.dispose();
        radIndicator.dispose();
        setterIndicator.dispose();
        setterLinesBeforeData.dispose();
        if (subCompo != null)
            subCompo.dispose();
        if (col01 != null)
            col01.dispose();
        if (col02 != null)
            col02.dispose();
    }

    @Override
    public boolean checkMandatory()
    {
        boolean ok = true;
        String log = Global.RN + "Section : " + title.trim(); //$NON-NLS-1$

        if (radNumber.getSelection())
        {
            if (setter != null)
            {
                ok = setter.checkMandatoryValue();
                log = log + Global.RN + "==>" + Messages.error_notValid; //$NON-NLS-1$
            }
        }
        else
        {
// boolean idcOk = setterIndicator.checkMandatoryValue();
// ok = ok && idcOk;
//            if (!idcOk) log = log + Global.RN + "==>" + Messages.error_file_idcEmpty; //$NON-NLS-1$

            boolean nbLinesOk = setterLinesBeforeData.checkMandatoryValue();
            ok = ok && nbLinesOk;
            if (!nbLinesOk)
                log = log + Global.RN + "==>" + Messages.error_notValid; //$NON-NLS-1$
        }

        if (!ok)
            Utils.messageErreurAppend(log);

        return ok;
    }

    public String getIndicatorText()
    {
        return setterIndicator.getValue();
    }

    public void setIndicatorText(String value)
    {
        setterIndicator.setValue(value);
    }

    public String getNbLinesBeforeData()
    {
        return setterLinesBeforeData.getValue();
    }

    public void setNbLinesBeforeData(String value)
    {
        setterLinesBeforeData.setValue(value);
    }
//    public Button getUseSeparatorInStationHeader() {
//		return useSeparatorInStationHeader;
//	}
//    public void setUseSeparatorInStationHeader(Button useSeparatorInStationHeader) {
//		this.useSeparatorInStationHeader = useSeparatorInStationHeader;
//	}
}
