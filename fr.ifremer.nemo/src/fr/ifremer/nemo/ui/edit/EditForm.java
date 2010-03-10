package fr.ifremer.nemo.ui.edit;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public abstract class EditForm
{
    public ScrolledForm formEdition;
    protected FormToolkit toolkitEdition;
    
    protected Button buttonValidate, buttonRAZ, buttonExpand;

    public Boolean okApresReset;
    public boolean expand, validee, loaded, filled;
    
    public ArrayList<NemoSection> listeSections;

    public void createEditionForm(Composite parent)
    {
        listeSections = new ArrayList<NemoSection>();
        
        validee = false;
        loaded = false;

        toolkitEdition = new FormToolkit(parent.getDisplay());
        formEdition = toolkitEdition.createScrolledForm(parent);
        formEdition.setVisible(false);
    }

    public void afficheEdition(){
        // raz message erreur
        Utils.messageErreur(Global.EMPTY_STRING);

        // de base les sections sont refermees
        if (!listeSections.isEmpty()){                
            expand = true;
            expandSections();
        }
        
        okApresReset = true;
        filled = false;

        // rendre le formulaire visible
        formEdition.setVisible(true);
    }

    public void razEdition()
    {
        formEdition.setVisible(false);
    }

    public void drawingBack()
    {
        // raz message erreur
        Utils.messageErreur(Global.EMPTY_STRING);

        if (!listeSections.isEmpty()){                
            expand = true;
            expandSections();
        }

        // refresh global
        formEdition.reflow(true);
        formEdition.setVisible(true);
    }

    public void expandSections()
    {
        for (NemoSection section : listeSections){
            if (!expand && section.isEnabled()){
                section.setExpanded(true);
            }
            else{
                section.setExpanded(false);
            }
        }
        
        if (!expand){
            expand = true;
            buttonExpand.setText(Global.MOINS);
        }
        else{
            expand = false;
            buttonExpand.setText(Global.PLUS);
        }

        // refresh global
        formEdition.reflow(true);
    }

    protected abstract void initSections();
    
    protected void createButtonValidate(){
        buttonValidate = new Button(formEdition.getBody(), SWT.NONE);
        buttonValidate.setBackground(ViewMain.backgroungEdit);
        buttonValidate.setText(Messages.button_validateStep);
    }
    
    protected void createButtonRaz(){
        buttonRAZ = new Button(formEdition.getBody(), SWT.NONE);
        buttonRAZ.setBackground(ViewMain.backgroungEdit);
        buttonRAZ.setText(Messages.button_reset);
        buttonRAZ
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    razText();
                    // invalidation de l'étape en cours et des suivantes
                    Utils.invalidEtapes();
                    // Suppression message erreur FAE 3752
                    Utils.messageErreur("");
                }
            });
    }
    
    protected void createButtonExpand(){
        buttonExpand = new Button(formEdition.getBody(), SWT.NONE);
        buttonExpand.setBackground(ViewMain.backgroungEdit);
        buttonExpand.setText(Global.PLUS);
        buttonExpand
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    expandSections();
                }
            });
    }
    
    protected void drawSections(){
        for (NemoSection section : listeSections){
            section.draw(toolkitEdition, formEdition, filled);
        }
    }

    public void razText()
    {
        for (NemoSection section : listeSections){
            section.razValues();
        }
    }

    protected void disposeElements()
    {
        if (listeSections != null){
            for (NemoSection section : listeSections){
                section.dispose();
            }
        }
        if (buttonRAZ != null) buttonRAZ.dispose();
        if (buttonExpand != null) buttonExpand.dispose();
        if (buttonValidate != null) buttonValidate.dispose();
    }

    public abstract void actualiseEdition();

    public void invalidEtape(int current)
    {
        okApresReset = false;
        validee = false;
        
        if (ViewMain.affiche != current && loaded){
            loaded = false;
            razEdition();
            //disposeElements();
        }
    }

}
