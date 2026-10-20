/* ============================================================================
 * Nom du fichier	: ManualInput.java
 * Auteur			: sbregent
 * Date				: 28 oct. 2010
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.station;


import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;


/**
 * Description: graphical object "No Value Input"
 * Cet objet est une variante du GrpManualInput (dont il hérite)
 * Il propose un bouton radio "no value" SANS champ de saisie
 * Il est considéré comme une saisie manuelle (par opposition à l'input automatique)
 * 
 * @author sbregent
 */
public class GrpNoValueInput extends GrpManualInput
{


    public GrpNoValueInput(int nbLine, String labelButton, ComboType cType,
            String defaultValue)
    {
        super(nbLine, labelButton, cType, defaultValue, null);
        this.labelButton = labelButton;
        if (this.labelButton == null)
        {
            this.labelButton = Messages.label_noValue_01;
        }
        // remove lines added by super constructor
       removeLines();
    }
    
    @Override
    public void setEnabled(boolean enabled)
    {
        buttonSaisie.setSelection(enabled);
    }
    
    @Override
    public String getValue()
    {
        return Global.NO_VALUE;
    }
    @Override
    public String getValue(int i)
    {
        return Global.NO_VALUE;
    }
    @Override
    public void draw(Composite sectionClient, boolean filled)
    {

        Composite compo = new Composite(sectionClient, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(nbColumns).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);

        buttonSaisie = new Button(compo, SWT.RADIO);
        buttonSaisie.setParent(compo);
        buttonSaisie.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
        buttonSaisie.setText(labelButton);

    }
}
