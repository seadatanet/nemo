/* ============================================================================
 * Nom du fichier	: ManualInput.java
 * Auteur			: dboulang
 * Date				: 21 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.object;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique "Manual Input"
 * @author dboulang
 *
 */
public class GrpManualInput implements UIobject
{

    private String labelButton;
    private Button buttonSaisie;
    private ArrayList<Couple> lines;
    private int nbColumns = 1;
    
    public GrpManualInput(Shell shell, int nbLine, String labelButton, ComboType cType, String defaultValue){
        
        this.labelButton = labelButton;
        if (this.labelButton == null){
            this.labelButton = Messages.getString("Drawing.4");  //$NON-NLS-1$
        }
        
        buttonSaisie = new Button(shell, SWT.RADIO);
        
        lines = new ArrayList<Couple>();
        if (nbLine > 1){            
            for (int i = 0; i < nbLine; i++){
                String label = Messages.getString("Drawing.10") + " " + (i + 1);           //$NON-NLS-1$ //$NON-NLS-2$
                lines.add(new Couple(shell, null, label, cType, defaultValue));
            }
        }else{
            lines.add(new Couple(shell, cType, defaultValue));
        }
    }
    
    public void draw(Composite sectionClient){

        Composite compo = new Composite(sectionClient, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(nbColumns).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);

        buttonSaisie.setParent(compo);
        buttonSaisie.setBackground(compo.getBackground());
        buttonSaisie.setText(labelButton);
        
        for (Couple line : lines){
            line.draw(compo);
        }
    }
    
    public void dispose(){
        buttonSaisie.dispose();
        for (Couple line : lines){
            line.dispose();
        }
    }
    
    public void setEnabled(boolean enabled){
        buttonSaisie.setSelection(enabled);
        for (Couple manuel : lines){
            manuel.setEnabled(enabled);
        }
    }
    
    public void setValue(String value, int i){
        setEnabled(true);
        Couple manuel = lines.get(i);
        manuel.setText(value);
    }
    
    public void setGrpEnabled(boolean enabled){
        buttonSaisie.setEnabled(enabled);
    }
    
    public boolean isSelected(){
        return buttonSaisie.getSelection();
    }
    
    public void razValues(){
        for (Couple manuel : lines){
            manuel.razValues();
        }
    }
    
    public void razDefaultValues(){
        for (Couple manuel : lines){
            manuel.razDefaultValues();
        }
    }

    /**
     * Getter sur buttonSaisie.
     * @return the buttonSaisie
     */
    public Button getButtonSaisie()
    {
        return buttonSaisie;
    }
//
//    /**
//     * Setter sur buttonSaisie.
//     * @param buttonSaisie the buttonSaisie to set
//     */
//    public void setButtonSaisie(Button buttonSaisie)
//    {
//        this.buttonSaisie = buttonSaisie;
//    }
//
//    /**
//     * Getter sur labelButton.
//     * @return the labelButton
//     */
//    public String getLabelButton()
//    {
//        return labelButton;
//    }
//
    /**
     * Setter sur labelButton.
     * @param labelButton the labelButton to set
     */
    public void setLabelButton(String labelButton)
    {
        this.labelButton = labelButton;
    }

//    /**
//     * Getter sur lines.
//     * @return the lines
//     */
//    public ArrayList<Couple> getLines()
//    {
//        return lines;
//    }
//
//    /**
//     * Setter sur lines.
//     * @param lines the lines to set
//     */
//    public void setLines(ArrayList<Couple> lines)
//    {
//        this.lines = lines;
//    }

//    /**
//     * Getter sur nbColumns.
//     * @return the nbColumns
//     */
//    public int getNbColumns()
//    {
//        return nbColumns;
//    }

    /**
     * Setter sur nbColumns.
     * @param nbColumns the nbColumns to set
     */
    public void setNbColumns(int nbColumns)
    {
        this.nbColumns = nbColumns;
    }
    
    
    public String getValue(){
        String result = Global.EMPTY_STRING;
        for (Couple line : lines){
            if ((line.getText() != null) && (!line.getText().trim().equals(Global.EMPTY_STRING))) {
                result += line.getText();
            }
        }
        return result;
    }
    
    public String getValue(int i){
        String result = Global.EMPTY_STRING;
        Couple line = lines.get(i);
        if ((line.getText() != null) && (!line.getText().trim().equals(Global.EMPTY_STRING))) {
            result = line.getText();
        }
        return result;
    }
    
}
