/* ============================================================================
 * Nom du fichier	: AutoInput.java
 * Auteur			: dboulang
 * Date				: 21 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.station;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.UIobject;
import fr.ifremer.nemo.ui.edit.station.AutoLine.MetaInData;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique "Automatic Input"
 * 
 * @author dboulang
 */
public class GrpAutoInput implements UIobject
{

    private Button buttonPos;
    private String labelButtonPos;
    private String variableLabel;
    protected ArrayList<AutoLine> lines;
    private final boolean variablePos;
    protected Button buttonVari;

    protected boolean defaultValueEnable;
    protected Button checkBoxHasDefaultValue;
    protected Text textDefaultValue;
    private final  SelectionListener listenerAuto;

    public GrpAutoInput(int nbLine, String labelButtonPos, boolean variablePos,
            MetaInData inData, boolean withCursor, boolean defaultValueEnabled,  SelectionListener listenerAuto)
    {

        defaultValueEnable = defaultValueEnabled;
        this.listenerAuto=listenerAuto;

        this.labelButtonPos = labelButtonPos;
        if (this.labelButtonPos == null)
            this.labelButtonPos = Messages.label_auto_01;

        lines = new ArrayList<AutoLine>();
        for (int i = 0; i < nbLine; i++)
        {
            lines.add(new AutoLine(i, (nbLine == 1), inData, withCursor));
        }

        this.variablePos = variablePos;

        this.variableLabel = Messages.label_variable;
    }

    public void setVariableLabel(String value)
    {
        this.variableLabel = value;
    }

    public void draw(Composite sectionClient, boolean filled)
    {
        Color backgroundEditColor = Nemo.getInstance().getViewMain().getBackgroundEdit();
        buttonPos = new Button(sectionClient, SWT.RADIO);
        buttonPos.setBackground(backgroundEditColor);
        buttonPos.setText(labelButtonPos);
        
        if (listenerAuto!=null) {
        	buttonPos.addSelectionListener(listenerAuto);
        }
        	

        Group groupe = new Group(sectionClient, SWT.NONE);
        groupe.setBackground(backgroundEditColor);
        groupe.setText(Messages.label_position);
        groupe.setLayout(new GridLayout());

        GridDataFactory.fillDefaults().grab(true, false).applyTo(groupe);

        Composite compo1 = AutoLine.drawLineCompo(groupe);
        compo1.setBackground(backgroundEditColor);
        for (AutoLine line : lines)
        {
            line.drawLine(compo1, filled);
        }

        Composite compo2 = AutoLine.drawSetCompo(groupe, lines.size());
        compo2.setBackground(backgroundEditColor);
        for (int i = 0; i < lines.size(); i++)
        {
            lines.get(i).drawSet(compo2, i);
        }

        if (variablePos)
        {
            buttonVari = new Button(compo2, SWT.CHECK);
            buttonVari.setParent(compo2);
            buttonVari.setBackground(backgroundEditColor);
            buttonVari.setText(this.variableLabel);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.END,
                SWT.CENTER).applyTo(buttonVari);

            buttonVari
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent g)
                    {
                        disableEndVari();
                        Utils.invalidEtapes();
                    }
                });
        }

        if (defaultValueEnable)
        {
            // Ajout d'un filler pour l'alignement
            if (buttonVari == null)
                new Label(compo2, SWT.NONE);

            checkBoxHasDefaultValue = new Button(compo2, SWT.CHECK);
            checkBoxHasDefaultValue.setText(Messages.label_defaultValue);
            checkBoxHasDefaultValue
                .addSelectionListener(new SelectionListener() {

                    public void widgetDefaultSelected(SelectionEvent e)
                    {
                        widgetSelected(e);
                    }

                    public void widgetSelected(SelectionEvent e)
                    {
                    
                        if (checkBoxHasDefaultValue.getSelection())
                        {
                            textDefaultValue.setEnabled(true);
                        }
                        else
                        {
                            textDefaultValue.setEnabled(false);
                            textDefaultValue.setText("");
                        }

                        Utils.invalidEtapes();
                    }

                });
            GridDataFactory.fillDefaults().grab(true, false).applyTo(
                checkBoxHasDefaultValue);

            textDefaultValue = new Text(compo2, SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(
                textDefaultValue);
            textDefaultValue.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e)
                {
                    Utils.invalidEtapes();
                }

            });
        }
    }

    public void dispose()
    {
        buttonPos.dispose();
        for (AutoLine line : lines)
        {
            line.dispose();
        }
        if (buttonVari != null)
            buttonVari.dispose();
    }

    public void setEnabled(boolean enabled)
    {
        buttonPos.setSelection(enabled);
        for (AutoLine line : lines)
        {
            line.setEnabled(enabled);
        }

        if (buttonVari != null)
            buttonVari.setEnabled(enabled);

        if (defaultValueEnable)
        {
            checkBoxHasDefaultValue.setEnabled(enabled);
            if (checkBoxHasDefaultValue.getSelection() || !enabled)
            {
                textDefaultValue.setEnabled(enabled);
            }
        }
    }

    public boolean isSelected()
    {
        return buttonPos.getSelection();
    }

    public void razValues()
    {
        for (AutoLine line : lines)
        {
            line.razValues();
        }
        if (buttonVari != null)
            buttonVari.setSelection(false);

        if (textDefaultValue != null)
            textDefaultValue.setText("");

        if (checkBoxHasDefaultValue != null)
            checkBoxHasDefaultValue.setSelection(false);

    }

    public int nbLineFilled()
    {
        int cpt = 0;
        for (AutoLine line : lines)
        {
            if (!line.isEmpty())
                cpt++;
        }
        return cpt;
    }

    public void setEnableInData(MetaInData enableInData)
    {
        for (AutoLine line : lines)
        {
            line.setEnableInData(enableInData);
        }
    }

    public boolean isVariablePos()
    {
        boolean variable = false;
        if (buttonVari != null)
        {
            variable = buttonVari.getSelection();
        }
        return variable;
    }

    /**
     * Getter sur buttonPos.
     * 
     * @return the buttonPos
     */
    public Button getButtonPos()
    {
        return buttonPos;
    }

    /**
     * Getter sur buttonVari.
     * 
     * @return the buttonVari
     */
    public Button getButtonVari()
    {
        return buttonVari;
    }

    // pas utile pour l'instant
    public String getValue()
    {
        String result = Global.EMPTY_STRING;
        try
        {
            boolean posVariable = isVariablePos();
            for (AutoLine line : lines)
            {
                if ((line.getValue(posVariable) != null)
                        && (!line.getValue(posVariable).trim().equals(
                            Global.EMPTY_STRING)))
                {
                    result += line.getValue(posVariable);
                }
            }
        }
        catch (StringIndexOutOfBoundsException e)
        {
            return null;
        }
        return result;
    }

    public String getValue(int i)
    {
        boolean posVariable = isVariablePos();
        String result = Global.EMPTY_STRING;
        AutoLine line = lines.get(i);
        if ((line.getValue(posVariable) != null)
                && (!line.getValue(posVariable).trim().equals(
                    Global.EMPTY_STRING)))
        {
            result = line.getValue(posVariable);
        }
        return result;
    }
    
    /**
     * Return the entire raw line at index i.
     * @param i the line index (cf. autoLine)
     * 
     * @return the raw line found
     */
    public String getRawLine(int i) {
    	
        AutoLine line = lines.get(i);
        return line.getRawLine();
    }

    public void setValue(String line, String start, String end,
            boolean posVariable, boolean valueVariable, int i,
            boolean hasDefaultValue, boolean isDefaultValueSeceted,
            String defaultValue)
    {
        setEnabled(true);
        AutoLine autoLine = lines.get(i);
        autoLine.setValue(line, start, end);

        if (posVariable)
            buttonVari.setSelection(valueVariable);

        if (defaultValueEnable && hasDefaultValue)
        {
            textDefaultValue.setEnabled(isDefaultValueSeceted);

            if (checkBoxHasDefaultValue != null)
                checkBoxHasDefaultValue.setSelection(isDefaultValueSeceted);

            if (textDefaultValue != null)
                textDefaultValue.setText(defaultValue);
        }
    }

    /**
     * Getter sur lines.
     * 
     * @return the lines
     */
    public ArrayList<AutoLine> getLines()
    {
        return lines;
    }

    public boolean isInData()
    {
        boolean result = false;
        for (AutoLine line : lines)
        {
            result = (result || line.isInData());
        }
        return result;
    }

    public boolean checkAutoLine(int i, String nomSection)
    {
        AutoLine line = lines.get(i);
        return line.checkValues(nomSection);
    }

    public int calculNbreLignes(int i)
    {
        AutoLine line = lines.get(i);
        return line.calculNbreLignes();
    }

    public boolean checkPosition(int i, int nbreLignes)
    {
        AutoLine line = lines.get(i);
        return line.checkPosition(nbreLignes);
    }

    public void selectInViewFile()
    {
        AutoLine line = lines.get(0);
        line.selectInViewFile();
    }

    public boolean checkInDataOrHeader(int i, String lign, String sectionName)
    {
        AutoLine line = lines.get(i);
        return line.checkInDataOrHeader(lign, sectionName);
    }

    public void disableEndVari()
    {
        if (buttonVari != null)
        {
            for (AutoLine line : lines)
            {
                line.getEnd().setEnabled(!buttonVari.getSelection());
            }
        }
    }

    protected boolean isDefaultValueEnable()
    {
        return defaultValueEnable;
    }

    protected boolean isDefaultValueSelected()
    {
        if (checkBoxHasDefaultValue != null)
            return checkBoxHasDefaultValue.getSelection();

        return false;
    }

    protected String getDefaultValue()
    {
        if (textDefaultValue != null)
            return textDefaultValue.getText();
        else
            return "";
    }
}
