/* ============================================================================
 * Nom du fichier	: GroupVerticalParameters.java
 * Auteur			: sbregent
 * Date				: 21 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.data;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.ParameterManager;
import fr.ifremer.nemo.input.parameters.VerticalParametersList;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: classe dédiée aux combos permettant de choisir les paramètres de référence verticale
 * contient les méthodes pour les mises à jour du contenu des combo, appellées par la tableData
 * @author sbregent
 *
 */
public class GroupVerticalParameters
{
    // combos pour choisir les paramètres verticaux de référence
    private Combo comboDBSSurf;
    private Combo comboDBSBed;
    private Combo comboP;
    
    
    public GroupVerticalParameters()
    {
       
    }
   
    public void createGroup(ScrolledForm formEdition)
    {
        
        Group group = new Group(formEdition.getBody(), SWT.NONE);
        group.setFont(Utils.font08);
        group.setText(Messages.edit_data_mesureType_label);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true)
            .margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
        
        // Depth below sea surface
        Label labelDBSSurf = new Label(group, SWT.NO_BACKGROUND);
        labelDBSSurf.setText("Depth below sea surface");
        comboDBSSurf = new Combo(group, SWT.BORDER);
//        comboDBSSurf.setBackground( new Color(formEdition.getBody().getDisplay(), 255, 0, 0));
        
      
        // Depth below sea bed
        Label labelDBSBed = new Label(group, SWT.NO_BACKGROUND);
        labelDBSBed.setText("depth below sea bed code");
        comboDBSBed = new Combo(group, SWT.BORDER);
//        comboDBSBed.setBackground( new Color(formEdition.getBody().getDisplay(), 255, 0, 0));
        
//      Pressure
        Label labelP = new Label(group, SWT.NO_BACKGROUND);
        labelP.setText("pressure code");
        comboP = new Combo(group, SWT.BORDER);
//        comboP.setBackground( new Color(formEdition.getBody().getDisplay(), 255, 0, 0));
        
        init();
    }
    
    private void init()
    {
        comboDBSSurf.add(Global.EMPTY_STRING);
        comboDBSBed.add(Global.EMPTY_STRING);
        comboP.add(Global.EMPTY_STRING);
    }
    
    public void addTypeData(TypeData typeData)
    {
        ParameterManager pm = Nemo.getInstance().getDomain().getData().getMyParameterManager();
        if (pm.isPossiblePressureParameter(typeData) )
        {
            comboP.add(typeData.getCodeToUse());
        }
        if ( pm.isPossibleDepthBSSurfParameter(typeData))
        {
            comboDBSSurf.add(typeData.getCodeToUse());
        }
        if ( pm.isPossibleDepthBSBedParameter(typeData) )
        {
            comboDBSBed.add(typeData.getCodeToUse());
        }
    }

    public void removeToAllCombo(TypeData typeData)
    {
        String code =typeData.getEntryKey();
        try{
            comboDBSSurf.remove(code);
        }catch (Exception e) {
            // TODO: handle exception
        }
        try{
            comboDBSBed.remove(code);
        }catch (Exception e) {
            // TODO: handle exception
        }
        try{
            comboP.remove(code);
        }catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void removeAllToAllCombos()
    {
        try
        {
            comboDBSSurf.removeAll();
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        try
        {
            comboDBSBed.removeAll();
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        try
        {
            comboP.removeAll();
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
       init();
    }
    
    public void enable_pDepthBSBed(boolean enabled)
    {
        comboDBSBed.setEnabled(enabled);
    }
    public void enable_pDepthBSSurf(boolean enabled)
    {
        comboDBSSurf.setEnabled(enabled);
    }
    public void enable_pPressure(boolean enabled)
    {
        comboP.setEnabled(enabled);
    }

    public Combo getComboDBSBed()
    {
        return comboDBSBed;
    }

    public Combo getComboDBSSurf()
    {
        return comboDBSSurf;
    }

    public Combo getComboP()
    {
        return comboP;
    }
    
    
    public void select(VerticalParametersList list)
    {
        for (int i=0; i<comboDBSSurf.getItemCount(); i++){
            if (comboDBSSurf.getItem(i).equals(list.getP_depthBSSurf())){
                comboDBSSurf.select(i);
            }
        }
        for (int i=0; i<comboDBSBed.getItemCount(); i++){
            if (comboDBSBed.getItem(i).equals(list.getP_depthBSBed())){
                comboDBSBed.select(i);
            }
        }
        for (int i=0; i<comboP.getItemCount(); i++){
            if (comboP.getItem(i).equals(list.getP_pressure())){
                comboP.select(i);
            }
        }
    }
}
