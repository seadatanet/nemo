/* ============================================================================
 * Nom du fichier	: FileStationHeader.java
 * Auteur			: dboulang
 * Date				: 30 avr. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.desc;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: "Station Header" Section in File Description
 * @author dboulang
 *
 */
public class FileStationHeader extends FileSection
{
    protected Composite subCompo, col01, col02;
    protected Button radNumber, radIndicator;
    protected SetText setterIndicator, setterLinesBeforeData;

    /**
     * @param shell
     * @param name
     * @param title
     * @param description
     */
    public FileStationHeader(Shell shell, String name, String title, String description)
    {
        super(shell, name, title, description);
        
        radNumber = new Button(shell, SWT.RADIO);
        addRadListener(radNumber);
        
        radIndicator = new Button(shell, SWT.RADIO);
        addRadListener(radIndicator);

        setterIndicator = new SetText(shell, Messages.edit_file_enteteStation_indicator, Global.EMPTY_STRING, true);
        setterIndicator.setTextSize(139);
        
        setterLinesBeforeData = new SetText(shell, Messages.edit_file_enteteStation_linesBeforeData, Global.ZERO, false);
        setterLinesBeforeData.setTextSize(139);
    }
    
    private void addRadListener(final Button radio){
        radio.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent g) {
                selectNumber(radNumber.getSelection());
            }
        });
    }
    
    public void selectNumber(boolean selected){
        
        radNumber.setSelection(selected);
        if (setter != null) setter.setEnabled(selected);
        
        radIndicator.setSelection(!selected);
        setterIndicator.setEnabled(!selected);
        setterLinesBeforeData.setEnabled(!selected);
        
        // invalidation de l'étape en cours et des suivantes
        Utils.invalidEtapes();
    }
    
    public boolean isSelectNumber(){
        return radNumber.getSelection();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawSection(Composite parent)
    {
        compo = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);
        

        radNumber.setParent(compo);
        radNumber.setBackground(compo.getBackground());
        radNumber.setText(Messages.edit_file_enteteStation_radNumber); 
        
        setter.draw(compo);
        
        radIndicator.setParent(compo);
        radIndicator.setBackground(compo.getBackground());
        radIndicator.setText(Messages.edit_file_enteteStation_radIndicator); 
        
            subCompo = new Composite(compo, SWT.NONE);
            GridLayoutFactory.fillDefaults().numColumns(2).applyTo(subCompo);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(subCompo);

                col01 = new Composite(subCompo, SWT.NONE);
                GridLayoutFactory.fillDefaults().numColumns(1).applyTo(col01);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(col01);
                
                setterIndicator.draw(col01);

                col02 = new Composite(subCompo, SWT.NONE);
                GridLayoutFactory.fillDefaults().numColumns(1).applyTo(col02);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(col02);

                setterLinesBeforeData.draw(col02);
                
        selectNumber(true);
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
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose(){
        super.dispose();
        radNumber.dispose();
        radIndicator.dispose();
        setterIndicator.dispose();
        setterLinesBeforeData.dispose();
        if (subCompo != null) subCompo.dispose();
        if (col01 != null) col01.dispose();
        if (col02 != null) col02.dispose();
    }


    @Override
    public boolean checkMandatory()
    {
        boolean ok = true;
        String log = Global.RN + "Section : " + title.trim();  //$NON-NLS-1$
        
        if (radNumber.getSelection()){
            if (setter != null){
                ok = setter.checkMandatoryValue();
                log = log + Global.RN + "==>" + Messages.error_notValid;  //$NON-NLS-1$
            }
        }
        else{
//            boolean idcOk = setterIndicator.checkMandatoryValue();
//            ok = ok && idcOk;
//            if (!idcOk) log = log + Global.RN + "==>" + Messages.error_file_idcEmpty; //$NON-NLS-1$

            boolean nbLinesOk = setterLinesBeforeData.checkMandatoryValue();
            ok = ok && nbLinesOk;
            if (!nbLinesOk) log = log + Global.RN + "==>" + Messages.error_notValid; //$NON-NLS-1$
        }
        
        if (!ok) 
            Utils.messageErreurAppend(log);
        
        return ok;
    }
    

    public String getIndicatorText(){
        return setterIndicator.getValue();
    }
    
    public void setIndicatorText(String value){
        setterIndicator.setValue(value);
    }


    public String getNbLinesBeforeData(){
        return setterLinesBeforeData.getValue();
    }
    
    public void setNbLinesBeforeData(String value){
        setterLinesBeforeData.setValue(value);
    }
}
