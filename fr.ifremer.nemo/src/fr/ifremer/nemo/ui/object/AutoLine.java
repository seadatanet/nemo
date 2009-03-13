/* ============================================================================
 * Nom du fichier	: AutoLine.java
 * Auteur			: dboulang
 * Date				: 23 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.object;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.ui.Cursor;
import fr.ifremer.nemo.ui.DataModel;
import fr.ifremer.nemo.ui.EditStation;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.ViewFile;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique contenant l'injection d'une "ligne"
 * @author dboulang
 *
 */
public class AutoLine implements UIobject
{    
    public static enum MetaInData{
        OBLIGATOIRE, INTERDIT, FACULTATIF;
    }
    
    private final static String LABEL_GROUP     = Messages.getString("Drawing.9");      //$NON-NLS-1$
    private final static String LABEL_LINE      = Messages.getString("Drawing.10");     //$NON-NLS-1$
    private final static String LABEL_START     = Messages.getString("Drawing.11");     //$NON-NLS-1$
    private final static String LABEL_END       = Messages.getString("Drawing.12");     //$NON-NLS-1$
    private final static String LABEL_SET       = Messages.getString("Drawing.1");      //$NON-NLS-1$
    
    protected Couple numberLine;
    protected Couple start;
    protected Couple end;
    protected Button buttonSet;
    protected Cursor cursor;
    
    private boolean unique;
    protected MetaInData enableInData;
    protected boolean inData = false;
    
    public AutoLine(Shell shell, int i, boolean unique, MetaInData enableInData, boolean withCursor){

        this.unique = unique;
        this.enableInData = enableInData;
        String addedLabel = Global.EMPTY_STRING;
        if (!unique) addedLabel = Global.SPACE + (i + 1);            
        numberLine = new Couple(shell, null, (LABEL_LINE + addedLabel), ComboType.NONE, Global.EMPTY_STRING);
        start = new Couple(shell, null, (LABEL_START + addedLabel), ComboType.NONE, Global.EMPTY_STRING);
        end = new Couple(shell, null, (LABEL_END + addedLabel), ComboType.NONE, Global.EMPTY_STRING);
        buttonSet = new Button(shell, SWT.NULL);
        if (withCursor) cursor = new Cursor();
    }

    /**
     * {@inheritDoc}
     */
    public void draw(Composite compo)
    {
        Group group = new Group(compo, SWT.NONE);
        group.setText(LABEL_GROUP);
        group.setLayout(new GridLayout());

        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

        Composite compo1 = drawLineCompo(group);
        drawLine(compo1);
        
        Composite compo2 = drawSetCompo(group, 1);
        drawSet(compo2, 1);
    }

    public static Composite drawLineCompo(Group group)
    {
        Composite compo = new Composite(group, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(6).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);
        
        return compo;
    }

    public void drawLine(Composite compo)
    {
        numberLine.draw(compo);
        start.draw(compo);
        end.draw(compo);
    }

    public static Composite drawSetCompo(Group group, int nbLines)
    {
        Composite compo = new Composite(group, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(nbLines + 1).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);
        
        return compo;
    }

    public void drawSet(Composite compo, int i)
    {
        String addedLabel = Global.EMPTY_STRING;
        if (!unique) addedLabel = Global.SPACE + LABEL_LINE + Global.SPACE + (i + 1);            
        buttonSet.setParent(compo);
        buttonSet.setText(LABEL_SET + addedLabel);
        
        buttonSet
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {

                    StyledText styledtext = ViewFile.styledTextStation;
                    int deb, fi, lign;

                    // on recupere les coordonnees de la selection
                    deb = styledtext.getSelection().x;
                    fi = styledtext.getSelection().y;

                    if (deb == fi) {
                        // message d'erreur
                        Utils.messageErreurAppend(Messages.getString("Utils.26"));    //$NON-NLS-1$

                    } else {
                        lign = 1;
                        // calcul de la ligne en fonction de la valeur de x
                        // parcours du tableau des tailles de lignes cumulees
                        for (int idx = 0; idx < Utils.lignesBrutes.size(); idx++) {

                            if (deb == Utils.taillesCumLignes[idx]) {
                                lign = idx + 1;
                                break;
                            }
                            if (idx == (Utils.lignesBrutes.size() - 1)) {
                                lign = idx + 1;
                                break;
                            }
                            if ((deb >= Utils.taillesCumLignes[idx])
                                    && (deb < Utils.taillesCumLignes[idx + 1])) {
                                lign = idx + 1;
                                break;
                            }

                        }
                        
                        // test sur 1ère ligne de données ou Métadata
                        boolean ligneOK = checkInDataOrHeader(String.valueOf(lign), "This section");        //$NON-NLS-1$
                        
                        if (ligneOK){
                            // réactivation du champ "ligne"
                            numberLine.setEnabled(ligneOK);
                            
                            // remplissage ligne dans interface
                            numberLine.setText(String.valueOf(lign)); 
                            
                            deb = deb - Utils.taillesCumLignes[lign - 1] + 1; // debut reel sur ligne
                            
                            // nbre caracs par rapport
                            // debut de ligne
                            fi = fi - Utils.taillesCumLignes[lign - 1]; // fin reelle sur ligne
                            
                            // remplissage debut et fin dans interface
                            start.setText(String.valueOf(deb)); 
                            end.setText(String.valueOf(fi));
                            
                            numberLine.getValueText().pack();
                            start.getValueText().pack();
                            end.getValueText().pack();
                            
                            Utils.messageErreur(Global.EMPTY_STRING);
                        }
                    }
                    if (cursor != null){
                        cursor.setX(ViewFile.styledTextStation.getSelection().x);
                        cursor.setY(ViewFile.styledTextStation.getSelection().y);
                    }
                }
            });
    }

    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        numberLine.dispose();
        start.dispose();
        end.dispose();
        buttonSet.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public void razValues()
    {
        inData = false;
        numberLine.razValues();
        start.razValues();
        end.razValues();
        if (cursor != null) cursor.reset();
    }

    /**
     * {@inheritDoc}
     */
    public void setEnabled(boolean enabled)
    {
        numberLine.setEnabled(enabled);
        start.setEnabled(enabled);
        end.setEnabled(enabled);
        buttonSet.setEnabled(enabled);
    }
    
    protected int getStationHeaderPos(){
        int stationHeader = getFileHeaderPos();
        stationHeader += DataModel.stationModel.getNumberOfLines();
        return stationHeader;
    }
    
    protected int getFileHeaderPos(){
        int fileHeader = 0;
        if (!ViewFile.dossierCampagne) {
            // fichier campagne
            fileHeader = DataModel.singleSurvey.getHeaderLinesNumber();
        }
        return fileHeader;
    }
    
    public boolean checkInDataOrHeader(String ligne, String sectionName)
    {
        // FAE 1687 (evol) : ajout du test "inFileHeader"
        boolean inStationHeader;
        boolean inFileHeader = false;
        boolean ligneOK = true;
        int lign = 0;
        
        if (ligne != null && !ligne.equals(Global.EMPTY_STRING)){
            lign = Integer.parseInt(ligne);
            inData = (lign == (getStationHeaderPos() + 1));
            inFileHeader =  ((lign <= (getFileHeaderPos())) && lign != 0);
            inStationHeader = (lign > (getFileHeaderPos())) && (lign <= (getStationHeaderPos()));
        }else{
            inData = true;
            inStationHeader = false;
        }

        if (!inFileHeader){
            switch (enableInData){
                case OBLIGATOIRE:
                    // OK if inData
                    if (!inData){
                        ligneOK = false;
                        if (sectionName != null) 
                            Utils.messageErreurAppend(sectionName + Global.SPACE + Messages.getString("Regexp.19"));            //$NON-NLS-1$
                    }
                    break;
                case INTERDIT:
                    // OK if inHeader
                    if (!inStationHeader || inData){
                        ligneOK = false;
                        if (sectionName != null) 
                            Utils.messageErreurAppend(sectionName + Global.SPACE + Messages.getString("Regexp.21"));        //$NON-NLS-1$
                    }
                    break;
                case FACULTATIF:
                default:
                    // comportement FACULTATIF : OK if inData or inHeader
                    if (!inData && !inStationHeader && lign != 0){
                        ligneOK = false;
                        if (sectionName != null) 
                            Utils.messageErreurAppend(sectionName + Global.SPACE + Messages.getString("Regexp.19"));   //$NON-NLS-1$ 
                    }
                    EditStation.actualiseEdition();
                    break;
            }
        }else{
            ligneOK = false;
            Utils.messageErreurAppend(Messages.getString("Regexp.22"));            //$NON-NLS-1$
        }
        return ligneOK;
    }

    /**
     * Getter sur inData.
     * @return the inData
     */
    public MetaInData getEnableInData()
    {
        return enableInData;
    }

    /**
     * Setter sur inData.
     * @param inData the inData to set
     */
    public void setEnableInData(MetaInData inData)
    {
        this.enableInData = inData;
    }

    /**
     * Getter sur buttonSet.
     * @return the buttonSet
     */
    public Button getButtonSet()
    {
        return buttonSet;
    }

    /**
     * Getter sur end.
     * @return the end
     */
    public Couple getEnd()
    {
        return end;
    }

    /**
     * Getter sur numberLine.
     * @return the numberLine
     */
    public Couple getNumberLine()
    {
        return numberLine;
    }

    /**
     * Getter sur start.
     * @return the start
     */
    public Couple getStart()
    {
        return start;
    }

    /**
     * Getter sur inData.
     * @return the inData
     */
    public boolean isInData()
    {
        return inData;
    }
    
    /**
     * Getter sur cursor.
     * @return the cursor
     */
    public Cursor getCursor()
    {
        return cursor;
    }

    public String getValue(boolean posVariable){
        String result = Global.EMPTY_STRING;
        if (numberLine.getText() != null 
                && start.getText() != null && !start.getText().equals(Global.EMPTY_STRING)
                && end.getText() != null && !end.getText().equals(Global.EMPTY_STRING))
        {            
            String ligne = Global.EMPTY_STRING;
            if (numberLine.getText().equals(Global.EMPTY_STRING)) {
                ligne = Utils.lignesBrutes.get(getStationHeaderPos());
            } else if (numberLine.getText().equals(Global.ZERO)){
                // la ligne correspond au nom du fichier
                if (ViewFile.radioFichier.getSelection()) {
                    ligne = Utils.extraitFichierChemin(ViewFile.nomFichier);
                } else {
                    File directory = new File(ViewFile.nomRepertoire);
                    File[] subfiles = directory.listFiles();
                    ligne = subfiles[0].getName();
                }
            }else{
                ligne = Utils.lignesBrutes.get(Integer.parseInt(numberLine.getText()) - 1);
            }

            int iEnd = Integer.parseInt(end.getText());
            if (posVariable){ 
                int index = Integer.parseInt(start.getText());
                String nextChar = Global.ZERO;
                boolean continueBoucle = true;
                while (continueBoucle && index < ligne.length()){
                    nextChar = ligne.substring(index, index + 1);
                    if (nextChar.equals(".")  //$NON-NLS-1$
                        || nextChar.equals(",")  //$NON-NLS-1$
                        || Utils.isNumeric(nextChar)){
                        index++;
                    }else{
                        continueBoucle = false;
                    }
                }
                iEnd = index;
            }
            
            result = ligne.substring(Integer.parseInt(start.getText()) - 1, iEnd);
        }
        return result;
    }
    
    public void setValue(String line, String start, String end){
        numberLine.setText(line);
        this.start.setText(start);
        this.end.setText(end);
        checkInDataOrHeader(line, null); 
        //numberLine.setEnabled(!inData);
    }
    
    public void selectInViewFile(){
        int lign, deb, fi;
        if (numberLine.getText().equals(Global.EMPTY_STRING)) {
            lign = getStationHeaderPos() + 1;
        } else {
            lign = Integer.parseInt(numberLine.getText());
        }
        
        deb = Integer.parseInt(start.getText()) + Utils.taillesCumLignes[lign - 1];
        fi = Integer.parseInt(end.getText()) + Utils.taillesCumLignes[lign - 1];
        
        ViewFile.styledTextStation.setSelection(deb - 1, fi);
    }
    
    public boolean checkValues(String nomSection){

        String log = "\nSection : " + nomSection + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
        
        boolean ok = true;
        boolean result;
        
        String debut = start.getText();
        String fin = end.getText();

        // format des differents champs 
        
        // ligne
        if (!isInData()) {
            result = numberLine.checkValue();
            ok = ok && result;
            if (!result)
                log = log + "==>" + Messages.getString("Regexp.8"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // debut
        result = start.checkValue();
        ok = ok && result;
        if (!result)
            log = log + "==>" + Messages.getString("Regexp.10"); //$NON-NLS-1$ //$NON-NLS-2$

        // fin
        result = end.checkValue();
        ok = ok && result;
        if (!result)
            log = log + "==>" + Messages.getString("Regexp.12"); //$NON-NLS-1$ //$NON-NLS-2$

        
        // tests des relations des champs
        if (!debut.trim().equals(Global.EMPTY_STRING) && (!fin.trim().equals(Global.EMPTY_STRING))) {
            if (Integer.parseInt(debut) > Integer.parseInt(fin)) {
                ok = ok && false;
                log = log + "==>" + Messages.getString("Regexp.15"); //$NON-NLS-1$//$NON-NLS-2$
            }
        }

        if (!ok)
            Utils.messageErreurAppend(log);
        
        return ok;
    }
    
    public int calculNbreLignes(){

        int nbre = 0, lignDepart, lignFin;

        lignDepart = 1;
        lignFin = 1;
        // calcul de la ligne en fonction de la valeur de x
        // parcours du tableau des tailles de lignes cumulees

        if (cursor.getX() == cursor.getY()) {
            Utils.messageErreurAppend(Messages.getString("Utils.26")); //$NON-NLS-1$

        } else {
            // calcul ligne depart
            for (int i = 0; i < Utils.lignesBrutes.size(); i++) {

                if (cursor.getX() == Utils.taillesCumLignes[i]) {
                    lignDepart = i + 1;
                    break;
                }
                if (i == (Utils.lignesBrutes.size() - 1)) {
                    lignDepart = i + 1;
                    break;
                }
                if ((cursor.getX() >= Utils.taillesCumLignes[i])
                        && (cursor.getX() < Utils.taillesCumLignes[i + 1])) {
                    lignDepart = i + 1;
                    break;
                }

            }

            // calcul ligne fin
            for (int i = 0; i < Utils.lignesBrutes.size(); i++) {

                if (cursor.getY() == Utils.taillesCumLignes[i]) {
                    lignFin = i + 1;
                    break;
                }
                if (i == (Utils.lignesBrutes.size() - 1)) {
                    lignFin = i + 1;
                    break;
                }
                if ((cursor.getY() >= Utils.taillesCumLignes[i])
                        && (cursor.getY() < Utils.taillesCumLignes[i + 1])) {
                    lignFin = i + 1;
                    break;
                }

            }

            // remplissage nbreLignes dans interface
            nbre = (lignFin - lignDepart + 1); 

        }

        return nbre;
    }
    
    public boolean checkPosition(int nbreLignes){
        int ligneFin = Integer.parseInt(numberLine.getText()) + nbreLignes - 1;
        
        int enteteFichier;
        if (ViewFile.dossierCampagne) {
            // dossier
            enteteFichier = 0;
        } else {
            // fichier campagne
            enteteFichier = DataModel.singleSurvey
                    .getHeaderLinesNumber();
        }
        int ligneLimite = enteteFichier
                + DataModel.stationModel.getNumberOfLines();
        
        if (ligneFin > ligneLimite) {
            return false;
        } 
        // else
        return true;
    }
    
//    public void diseable(){
//        if (numberLine)
//    }
}
