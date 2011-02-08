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
        group.setText(Messages.edit_data_verticalParameters_label);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true)
            .margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
        
        // Depth below sea surface
        Label labelDBSSurf = new Label(group, SWT.NO_BACKGROUND);
        labelDBSSurf.setText(Messages.edit_data_verticalParameters_depthBSSurf);
        comboDBSSurf = new Combo(group, SWT.BORDER);
        
        // Depth below sea bed
        Label labelDBSBed = new Label(group, SWT.NO_BACKGROUND);
        labelDBSBed.setText(Messages.edit_data_verticalParameters_depthBSBed);
        comboDBSBed = new Combo(group, SWT.BORDER);
        
        // Pressure
        Label labelP = new Label(group, SWT.NO_BACKGROUND);
        labelP.setText(Messages.edit_data_verticalParameters_pressure);
        comboP = new Combo(group, SWT.BORDER);
        
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
        if (pm.isPossible_PressureParameter(typeData) )
        {
            comboP.add(typeData.getCodeToUse());
        }
        if ( pm.isPossible_DepthBSSurfParameter(typeData))
        {
            comboDBSSurf.add(typeData.getCodeToUse());
        }
        if ( pm.isPossible_DepthBSBedParameter(typeData) )
        {
            comboDBSBed.add(typeData.getCodeToUse());
        }
    }

    public void removeToAllCombo(TypeData typeData)
    {
//        String code =typeData.getEntryKey();
        String code = typeData.getCodeToUse();
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
