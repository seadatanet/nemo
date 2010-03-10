/* ============================================================================
 * Nom du fichier	: ManualInput.java
 * Auteur			: dboulang
 * Date				: 21 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.station;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.Couple;
import fr.ifremer.nemo.ui.edit.UIobject;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique "Manual Input"
 * 
 * @author dboulang
 */
public class GrpManualInput implements UIobject
{

    private String labelButton;
    private Button buttonSaisie;
    private final ArrayList<Couple> lines;
    private int nbColumns = 1;

    public GrpManualInput(int nbLine, String labelButton, ComboType cType,
            String defaultValue)
    {

        this.labelButton = labelButton;
        if (this.labelButton == null)
        {
            this.labelButton = Messages.label_manual_01;
        }

        lines = new ArrayList<Couple>();
        if (nbLine > 1)
        {
            for (int i = 0; i < nbLine; i++)
            {
                String label = Messages.label_line + Global.SPACE + (i + 1);
                lines.add(new Couple(null, label, cType, defaultValue));
            }
        }
        else
        {
            Couple couple = new Couple(cType, defaultValue);
            if (cType == ComboType.BLOCK)
                couple.setTextLimit(500);
            lines.add(couple);
        }
    }

    public void draw(Composite sectionClient, boolean filled)
    {

        Composite compo = new Composite(sectionClient, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(nbColumns).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);

        buttonSaisie = new Button(compo, SWT.RADIO);
        buttonSaisie.setParent(compo);
        buttonSaisie.setBackground(ViewMain.backgroungEdit);
        buttonSaisie.setText(labelButton);

        for (Couple line : lines)
        {
            line.draw(compo, filled);
        }
    }

    public void dispose()
    {
        buttonSaisie.dispose();
        for (Couple line : lines)
        {
            line.dispose();
        }
    }

    public void setEnabled(boolean enabled)
    {
        buttonSaisie.setSelection(enabled);
        for (Couple manuel : lines)
        {
            manuel.setEnabled(enabled);
        }
    }

    public void setValue(String value, int i)
    {
        setEnabled(true);
        Couple manuel = lines.get(i);
        manuel.setText(value);
    }

    public void setGrpEnabled(boolean enabled)
    {
        buttonSaisie.setEnabled(enabled);
    }

    public boolean isSelected()
    {
        return buttonSaisie.getSelection();
    }

    public void razValues()
    {
        for (Couple manuel : lines)
        {
            manuel.razValues();
        }
    }

    public void razDefaultValues()
    {
        for (Couple manuel : lines)
        {
            manuel.razDefaultValues();
        }
    }

    /**
     * Getter sur buttonSaisie.
     * 
     * @return the buttonSaisie
     */
    public Button getButtonSaisie()
    {
        return buttonSaisie;
    }

    /**
     * Setter sur labelButton.
     * 
     * @param labelButton
     *            the labelButton to set
     */
    public void setLabelButton(String labelButton)
    {
        this.labelButton = labelButton;
    }

    /**
     * Setter sur nbColumns.
     * 
     * @param nbColumns
     *            the nbColumns to set
     */
    public void setNbColumns(int nbColumns)
    {
        this.nbColumns = nbColumns;
    }

    public String getValue()
    {
        String result = Global.EMPTY_STRING;
        for (Couple line : lines)
        {
            if ((line.getText() != null)
                    && (!line.getText().trim().equals(Global.EMPTY_STRING)))
            {
                result += line.getText();
            }
        }
        return result;
    }

    public String getValue(int i)
    {
        String result = Global.EMPTY_STRING;
        Couple line = lines.get(i);
        if ((line.getText() != null)
                && (!line.getText().trim().equals(Global.EMPTY_STRING)))
        {
            result = line.getText();
        }
        return result;
    }
    
    public List<Couple> getLines()
    {
        return lines;
    }

}
