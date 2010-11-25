/* ============================================================================
 * Nom du fichier	: FileEndData.java
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
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Global.E_EndData;

/**
 * Description: "Indicator of end of Data" Section in File Description
 * 
 * @author dboulang
 */
public class FileEndData extends FileSection
{
    protected Composite subCompo, col01, col02;
    protected Button radEOF, radLigne, radNextStation, radManuel;
    protected SetText setterStartStation;

    /**
     * @param shell
     * @param name
     * @param title
     * @param description
     */
    public FileEndData(String name, String title, String description, EditFileForm form)
    {
        super(name, title, description, form);
        setterStartStation = new SetText(Messages.edit_file_termination_start, Global.EMPTY_STRING, true); //$NON-NLS-1$
    }

    @Override
    public void addSetter(String stringLabel, String defaultValue)
    {
        setter = new SetText(stringLabel, defaultValue, true);
    }

    private void addRadListener(final Button radio)
    {

        radio
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent g)
                {
                    selectEndData(radio);
                    Utils.invalidEtapes();

                }
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawSection(Composite parent, boolean filled)
    {
        compo = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);

        radEOF = new Button(compo, SWT.RADIO);
        radEOF.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
        radEOF.setText(Messages.edit_file_idcEOF);

        radLigne = new Button(compo, SWT.RADIO);
        radLigne.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
        radLigne.setText(Messages.edit_file_idcEmptyLine);

        radNextStation = new Button(compo, SWT.RADIO);
        radNextStation.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
        radNextStation.setText(Messages.edit_file_idcNextStation);
        setterStartStation.draw(compo, filled);

        radManuel = new Button(compo, SWT.RADIO);
        radManuel.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
        radManuel.setText(Messages.edit_file_idcManual);
        setter.draw(compo, filled);

        selectEndData(radEOF);
        addRadListener(radEOF);
        addRadListener(radLigne);
        addRadListener(radNextStation);
        addRadListener(radManuel);
    }

    @Override
    public boolean checkMandatory()
    {
        boolean ok = true;
        String log =
            Global.RN + "Section : " + title.trim() + Global.RN + "==>"; //$NON-NLS-1$  //$NON-NLS-2$

        if (setter != null && radManuel.getSelection())
        {
            ok = setter.checkMandatoryValue();
            log = log + Messages.error_file_idcEmpty;
        }
        else if (radNextStation.getSelection())
        {
            ok = setterStartStation.checkMandatoryValue();
            log = log + Messages.error_file_idcStartStationEmpty;
        }

        if (!ok)
            Utils.messageErreurAppend(log);

        return ok;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void razValues()
    {
        super.razValues();
        setterStartStation.razValues();
        selectEndData(radEOF);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
        super.dispose();
        radEOF.dispose();
        radLigne.dispose();
        radNextStation.dispose();
        radManuel.dispose();
        if (subCompo != null)
            subCompo.dispose();
        if (col01 != null)
            col01.dispose();
        if (col02 != null)
            col02.dispose();
    }

    public void selectEndData(Button radio)
    {
        if (radio != null)
        {
            if (radio == radEOF)
            {
                radEOF.setSelection(true);
                radLigne.setSelection(false);
                radNextStation.setSelection(false);
                setterStartStation.setEnabled(false);
                radManuel.setSelection(false);
                setter.setEnabled(false);

            }
            else if (radio == radLigne)
            {
                radEOF.setSelection(false);
                radLigne.setSelection(true);
                radNextStation.setSelection(false);
                setterStartStation.setEnabled(false);
                radManuel.setSelection(false);
                setter.setEnabled(false);

            }
            else if (radio == radNextStation)
            {
                radEOF.setSelection(false);
                radLigne.setSelection(false);
                radNextStation.setSelection(true);
                setterStartStation.setEnabled(true);
                radManuel.setSelection(false);
                setter.setEnabled(false);

            }
            else if (radio == radManuel)
            {
                radEOF.setSelection(false);
                radLigne.setSelection(false);
                radNextStation.setSelection(false);
                setterStartStation.setEnabled(false);
                radManuel.setSelection(true);
                setter.setEnabled(true);

            }
            else
            {
                radEOF.setSelection(true);
                radLigne.setSelection(false);
                radNextStation.setSelection(false);
                setterStartStation.setEnabled(false);
                radManuel.setSelection(false);
                setter.setEnabled(false);
            }

        }
    }

    /**
     * Getter sur radEOF.
     * 
     * @return the radEOF
     */
    public Button getRadEOF()
    {
        return radEOF;
    }

    /**
     * Getter sur radLigne.
     * 
     * @return the radLigne
     */
    public Button getRadLigne()
    {
        return radLigne;
    }

    /**
     * Getter sur radNextStation.
     * 
     * @return the radNextStation
     */
    public Button getRadNextStation()
    {
        return radNextStation;
    }

    /**
     * Getter sur radManuel.
     * 
     * @return the radManuel
     */
    public Button getRadManuel()
    {
        return radManuel;
    }

    public boolean radEOFselected()
    {
        return radEOF.getSelection();
    }

    public boolean radLigneSelected()
    {
        return radLigne.getSelection();
    }

    public boolean radNextStationSelected()
    {
        return radNextStation.getSelection();
    }

    public boolean radManuelSelected()
    {
        return radManuel.getSelection();
    }

    public String getStartStationIndicator()
    {
        return setterStartStation.getValue();
    }

    public void setStartStationIndicator(String value)
    {
        setterStartStation.setValue(value);
    }
    

    

    // TRADUCTION DES BOUTONS RADIO
    public E_EndData getEndData(){
        if (radNextStation.getSelection()){
            return E_EndData.NEXT_START_WITH;
        }
        else if (radManuel.getSelection()){
            return E_EndData.END_WITH;
        }
        else if (radLigne.getSelection()){
            return E_EndData.EMPTY_LINE;
        }
        else if (radEOF.getSelection()){
            return E_EndData.EOF;
        }else{
            // should not happend
            return E_EndData.EOF;  
        }
    }
    
    public void setEndData(E_EndData end, String pattern){
        switch (end){
            case NEXT_START_WITH:
                selectEndData(getRadNextStation());
                setStartStationIndicator(pattern);
                break;
            case END_WITH:
                selectEndData(getRadManuel());
                setValue(pattern);
                break;
            case EMPTY_LINE:
                selectEndData(getRadLigne());
                break;
            default:
                selectEndData(getRadEOF());
                break;
        }
    }
    
    public void addSpaceBeforeDataTerminator()
    {
        setStartStationIndicator(Global.SPACE +  getStartStationIndicator());
    }
    
    public String getIndicatorValue()
    {
        String indic = null;
        if (radManuelSelected())
        {
            indic = getValue();
        }
        return indic;
    }
}
