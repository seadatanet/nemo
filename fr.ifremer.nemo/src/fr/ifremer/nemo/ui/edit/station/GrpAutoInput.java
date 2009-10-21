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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.UIobject;
import fr.ifremer.nemo.ui.edit.station.AutoLine.MetaInData;
import fr.ifremer.nemo.ui.view.ViewMain;
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
    protected ArrayList<AutoLine> lines;
    private final boolean variablePos;
    protected Button buttonVari;

    public GrpAutoInput(int nbLine, String labelButtonPos, boolean variablePos,
            MetaInData inData, boolean withCursor)
    {

        this.labelButtonPos = labelButtonPos;
        if (this.labelButtonPos == null)
            this.labelButtonPos = Messages.label_auto_01;

        lines = new ArrayList<AutoLine>();
        for (int i = 0; i < nbLine; i++)
        {
            lines.add(new AutoLine(i, (nbLine == 1), inData, withCursor));
        }

        this.variablePos = variablePos;
    }

    public void draw(Composite sectionClient, boolean filled)
    {

        buttonPos = new Button(sectionClient, SWT.RADIO);
        buttonPos.setBackground(ViewMain.backgroungEdit);
        buttonPos.setText(labelButtonPos);

        Group groupe = new Group(sectionClient, SWT.NONE);
        groupe.setBackground(ViewMain.backgroungEdit);
        groupe.setText(Messages.label_position);
        groupe.setLayout(new GridLayout());

        GridDataFactory.fillDefaults().grab(true, false).applyTo(groupe);

        Composite compo1 = AutoLine.drawLineCompo(groupe);
        compo1.setBackground(ViewMain.backgroungEdit);
        for (AutoLine line : lines)
        {
            line.drawLine(compo1, filled);
        }

        Composite compo2 = AutoLine.drawSetCompo(groupe, lines.size());
        compo2.setBackground(ViewMain.backgroungEdit);
        for (int i = 0; i < lines.size(); i++)
        {
            lines.get(i).drawSet(compo2, i);
        }

        if (variablePos)
        {
            buttonVari = new Button(compo2, SWT.CHECK);
            buttonVari.setParent(compo2);
            buttonVari.setBackground(ViewMain.backgroungEdit);
            buttonVari.setText(Messages.label_variable);
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
    }
    
    public int nbLineFilled(){
        int cpt = 0;
        for (AutoLine line : lines){
            if (!line.isEmpty()) cpt++;
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
        boolean posVariable = isVariablePos();
        String result = Global.EMPTY_STRING;
        for (AutoLine line : lines)
        {
            if ((line.getValue(posVariable) != null)
                    && (!line.getValue(posVariable).trim().equals(
                        Global.EMPTY_STRING)))
            {
                result += line.getValue(posVariable);
            }
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

    public void setValue(String line, String start, String end,
            boolean posVariable, boolean valueVariable, int i)
    {
        setEnabled(true);
        AutoLine autoLine = lines.get(i);
        autoLine.setValue(line, start, end);
        if (posVariable)
            buttonVari.setSelection(valueVariable);
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

}
