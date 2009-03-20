/* ============================================================================
 * Nom du fichier	: FileSection.java
 * Auteur			: dboulang
 * Date				: 19 mars 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.object;

import java.util.regex.Pattern;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.ViewFile;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: TODO
 * @author dboulang
 *
 */
public class FileSection extends NemoSection
{
    protected Button radEOF, radLigne, radManuel;
    protected Text text;
    protected Button buttonSet;
    private String defaultValue;


    public FileSection(Shell shell, String name, String title, String description, String defaultValue){
        
        super(shell, name, title, description);
        this.defaultValue = defaultValue;
        
        text = new Text(shell, SWT.BORDER);
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                Utils.invalidEtapes();
            }
        });
        
        buttonSet = new Button(shell, SWT.NONE);
        

        if (name.equals(F_ENDDATA))
        {
            radEOF = new Button(shell, SWT.RADIO);
            addRadListener(radEOF);
            radLigne = new Button(shell, SWT.RADIO);
            addRadListener(radLigne);
            radManuel = new Button(shell, SWT.RADIO);
            addRadListener(radManuel);

            buttonSet.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent g) {
                    text.setText(ViewFile.textAreaDescription.getText().substring(
                            ViewFile.textAreaDescription.getSelection().x, 
                            ViewFile.textAreaDescription.getSelection().y));
                    text.pack();

                }
            });
            
        }else{
            
            buttonSet.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent h) {
                    
                    int deb, fi, lignDepart, lignFin, i;

                    // on recupere les coordonnees de la selection
                    deb = ViewFile.textAreaDescription.getSelection().x;
                    fi = ViewFile.textAreaDescription.getSelection().y;

                    lignDepart = 1;
                    lignFin = 1;
                    // calcul de la ligne en fonction de la valeur de x
                    // parcours du tableau des tailles de lignes cumulees

                    if (deb == fi) {
                        Utils.messageErreurAppend(Messages.getString("Utils.26")); //$NON-NLS-1$

                    } else {

                        // calcul ligne depart
                        for (i = 0; i < Utils.lignesBrutes.size(); i++) {

                            if (deb == Utils.taillesCumLignes[i]) {
                                lignDepart = i + 1;
                                break;
                            }
                            if (i == (Utils.lignesBrutes.size() - 1)) {
                                lignDepart = i + 1;
                                break;
                            }
                            if ((deb >= Utils.taillesCumLignes[i])
                                    && (deb < Utils.taillesCumLignes[i + 1])) {
                                lignDepart = i + 1;
                                break;
                            }

                        }

                        // calcul ligne fin
                        for (i = 0; i < Utils.lignesBrutes.size(); i++) {

                            if (fi == Utils.taillesCumLignes[i]) {
                                lignFin = i + 1;
                                break;
                            }
                            if (i == (Utils.lignesBrutes.size() - 1)) {
                                lignFin = i + 1;
                                break;
                            }
                            if ((fi >= Utils.taillesCumLignes[i])
                                    && (fi < Utils.taillesCumLignes[i + 1])) {
                                lignFin = i + 1;
                                break;
                            }

                        }
                        // remplissage nbreLignes dans interface
                        text.setText("" + (lignFin - lignDepart + 1)); //$NON-NLS-1$
                        text.pack();
                    }
                    
                }
            });
        }

    }
    
    private void addRadListener(final Button radio){

        radio.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent g) {
                selectEndData(radio);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawSection(Composite parent)
    {
        Composite compo = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);
        
        if (name.equals(F_ENDDATA)){
            radEOF.setParent(compo);
            radEOF.setBackground(compo.getBackground());
            radEOF.setText("EOF"); //$NON-NLS-1$
            
            radLigne.setParent(compo);
            radLigne.setBackground(compo.getBackground());
            radLigne.setText(Messages.getString("EditDesc.ligneVide")); //$NON-NLS-1$
            
            radManuel.setParent(compo);
            radManuel.setBackground(compo.getBackground());
            radManuel.setText(Messages.getString("EditDesc.indicateur")); //$NON-NLS-1$
        }
        text.setParent(compo);
        text.setBackground(compo.getBackground());
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        text.setText(defaultValue);
        
        buttonSet.setParent(compo);
        buttonSet.setText(Messages.getString("Drawing.1")); //$NON-NLS-1$
        
        selectEndData(radEOF);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillFromModel(StationModel stationModel)
    {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initModel(StationModel stationModel)
    {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void razValues()
    {
        text.setText(defaultValue);
        selectEndData(radEOF);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose(){
        super.dispose();
        text.dispose();
        buttonSet.dispose();
        if (name.equals(NemoSection.F_ENDDATA)){
            radEOF.dispose();
            radLigne.dispose();
            radManuel.dispose();
        }
    }

    
    public void selectEndData(Button radio) {
        if (radio != null){
            if (radio == radEOF) {
                radEOF.setSelection(true);
                radLigne.setSelection(false);
                radManuel.setSelection(false);
    
                text.setEnabled(false);
                buttonSet.setEnabled(false);
                
            } else if (radio == radLigne) {
                radEOF.setSelection(false);
                radLigne.setSelection(true);
                radManuel.setSelection(false);
    
                text.setEnabled(false);
                buttonSet.setEnabled(false);
                
            } else if (radio == radManuel) {
                radEOF.setSelection(false);
                radLigne.setSelection(false);
                radManuel.setSelection(true);
    
                text.setEnabled(true);
                buttonSet.setEnabled(true);
                
            } else {
                radEOF.setSelection(true);
                radLigne.setSelection(false);
                radManuel.setSelection(false);
    
                text.setEnabled(false);
                buttonSet.setEnabled(false);
            }
            
            // invalidation de l'étape en cours et des suivantes
            Utils.invalidEtapes();
        }
    }

    public String getValue(){
        return text.getText();
    }

    public void setValue(String value){
        text.setText(value);
    }

    /**
     * Getter sur radEOF.
     * @return the radEOF
     */
    public Button getRadEOF()
    {
        return radEOF;
    }

    /**
     * Getter sur radLigne.
     * @return the radLigne
     */
    public Button getRadLigne()
    {
        return radLigne;
    }

    /**
     * Getter sur radManuel.
     * @return the radManuel
     */
    public Button getRadManuel()
    {
        return radManuel;
    }

    public void initValue(){
        text.setText(defaultValue);
    }
    
    public boolean radEOFselected(){
        return radEOF.getSelection();
    }
    
    public boolean radLigneSelected(){
        return radLigne.getSelection();
    }
    
    public boolean radManuelSelected(){
        return radManuel.getSelection();
    }


    public boolean checkValue() {
        Boolean ok;
        String valeur, log;

        ok = true;
        log = Global.RN + "Section : " + title.trim() + Global.RN;  //$NON-NLS-1$
        valeur = text.getText();

        if (Pattern.matches("^[0-9][0-9]*$", valeur)) { //$NON-NLS-1$
            ok = ok && true;
        } else {
            ok = ok && false;
            log = log + "==>" + Messages.getString("Regexp.18"); //$NON-NLS-1$ //$NON-NLS-2$

        }

        if (ok) {
            // log = log + "...ok";
        } else {
            Utils.messageErreurAppend(log);
        }
        return ok;
    }
    
}
