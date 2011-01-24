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
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.data.listeners.DepthBelowSeaBedSelectionListener;
import fr.ifremer.nemo.ui.edit.data.listeners.DepthBelowSeaSurfSelectionListener;
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
    
    // etat des combos (pas toujours necessaires, donc peuvent être grisées)
    private boolean isComboDBSSurfEnabled;
    private boolean isComboDBBedEnabled;
    private boolean isComboDPEnabled;
    
    
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
        comboDBSSurf.setBackground( new Color(formEdition.getBody().getDisplay(), 255, 0, 0));
        
        DepthBelowSeaSurfSelectionListener listenerDBSSurf = new DepthBelowSeaSurfSelectionListener();
        comboDBSSurf.addSelectionListener(listenerDBSSurf);
      
        // Depth below sea bed
        Label labelDBSBed = new Label(group, SWT.NO_BACKGROUND);
        labelDBSBed.setText("depth below sea bed code");
        comboDBSBed = new Combo(group, SWT.BORDER);
        comboDBSBed.setBackground( new Color(formEdition.getBody().getDisplay(), 255, 0, 0));
        DepthBelowSeaBedSelectionListener listenerDBSBed = new DepthBelowSeaBedSelectionListener();
        comboDBSBed.addSelectionListener(listenerDBSBed);
        
//      Pressure
        Label labelP = new Label(group, SWT.NO_BACKGROUND);
        labelP.setText("pressure code");
        comboP = new Combo(group, SWT.BORDER);
        comboP.setBackground( new Color(formEdition.getBody().getDisplay(), 255, 0, 0));
        DepthBelowSeaBedSelectionListener listenerP = new DepthBelowSeaBedSelectionListener();
        
        
        
        comboP.addSelectionListener(listenerP);
    }
    public void addToAllCombo(TypeData typeData)
    {
        String code =typeData.getEntryKey();
        comboDBSSurf.add(code);
        comboDBSBed.add(code);
        comboP.add(code);
    }
    public void removeToAllCombo(TypeData typeData)
    {
        String code =typeData.getEntryKey();
        comboDBSSurf.remove(code);
        comboDBSBed.remove(code);
        comboP.remove(code);
    }
    public void removeAllToAllCombos()
    {
        comboDBSSurf.removeAll();
        comboDBSBed.removeAll();
        comboP.removeAll();
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
}
