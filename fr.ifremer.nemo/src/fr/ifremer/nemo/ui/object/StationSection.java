/* ============================================================================
 * Nom du fichier	: StationSection.java
 * Auteur			: dboulang
 * Date				: 21 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.object;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.domain.DataType;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.input.FlagFragment;
import fr.ifremer.nemo.input.HeaderFragment;
import fr.ifremer.nemo.input.LineFragment;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.Cursor;
import fr.ifremer.nemo.ui.DataModel;
import fr.ifremer.nemo.ui.EditDesc;
import fr.ifremer.nemo.ui.EditStation;
import fr.ifremer.nemo.ui.TableDataSurvey;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.ViewFile;
import fr.ifremer.nemo.ui.object.AutoLine.MetaInData;
import fr.ifremer.nemo.util.DateTimeFormat;
import fr.ifremer.nemo.util.ExternalizableFormat;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.LatitudeFormat;
import fr.ifremer.nemo.util.LongitudeFormat;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique représentant une section dans l'onglet station
 * @author dboulang
 *
 */
public class StationSection extends NemoSection
{    
    private Couple converter;
    protected ComboType cType = ComboType.NONE;
    private int nbLignes;
    
    protected GrpManualInput manualInput;
    private boolean blnDefaultManual;
    
    protected GrpAutoInput autoInput;
    private MetaInData enableInData;
    
    private boolean test;
    private Button buttonTest;
    protected Text resultTest;
    
    private Couple datatype;
    
    private boolean timeSeriesInfo = false;
    
    
    /*
     *  construct StationSection methodes
     */
    
    /**
     * initialize StationSection
     * @param shell
     * @param title
     * @param description
     * @param blnManuel
     * @param test
     */
    public StationSection(Shell shell, String name, String title, String description, boolean blnDefaultManual, boolean test, int nbLignes){
        
        super(shell, name, title, description);
        this.blnDefaultManual = blnDefaultManual;
        this.test = test;
        this.nbLignes = nbLignes;
                
        if (test){
            buttonTest = new Button(shell, SWT.NONE);
            resultTest = new Text(shell, SWT.BORDER);
        }

    }
    
    /**
     * add a converter group
     * @param shell
     * @param cType
     * @param defaultValue
     */
    public void addConverter(Shell shell, ComboType type, String defaultValue){
        cType = type;
        if (cType != ComboType.NONE){
            converter = new Couple(shell, cType, defaultValue);
        }
    }
    
    /**
     * add a ManualInput group
     * @param shell
     * @param manualLabel
     * @param cType
     * @param defaultValue
     */
    public void addManualInput(Shell shell, String manualLabel, ComboType type, String defaultValue){
        manualInput = new GrpManualInput(shell, nbLignes, manualLabel, type, defaultValue);
    }
    
    /**
     * add a AutoInput group
     * @param shell
     * @param nbAutoLines
     * @param autoLabel
     * @param variablePos
     * @param enableInData
     */
    public void addAutoInput(Shell shell, String autoLabel, boolean variablePos, MetaInData enable, boolean withCursor){
        enableInData = enable;
        autoInput = new GrpAutoInput(shell, nbLignes, autoLabel, variablePos, enableInData, withCursor);
    }
    
    /**
     * add a simple Couple group (DATATYPE)
     * @param shell
     * @param cType
     * @param defaultValue
     */
    public void addCouple(Shell shell, ComboType type, String defaultValue){
        datatype = new Couple(shell, type, defaultValue);
    }
    
    public void copyValues(StationSection initiale){
        if (initiale.getConverter() != null) converter = initiale.getConverter();
        if (initiale.getAutoInput() != null) autoInput = initiale.getAutoInput();
        if (initiale.getManualInput() != null) manualInput = initiale.getManualInput();
    }
    
    /*
     * Modify StationSection properties
     */
    
    public void setTimeSeriesInfo(boolean timeInfo){
        timeSeriesInfo = timeInfo;
    }

    /**
     * Setter sur enableInData.
     * @param enableInData the enableInData to set
     */
    public void setEnableInData(MetaInData enableInData)
    {
        this.enableInData = enableInData;
        autoInput.setEnableInData(enableInData);
    }
    
    /**
     * @param manual
     */
    public void setEnabledManual(boolean manual){
        if (!manual){
            autoInput.setEnabled(!manual);
            manualInput.razValues();
            manualInput.setEnabled(manual);
        }
        manualInput.setGrpEnabled(manual);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void razValues(){
        
        if (datatype != null) datatype.razValues();
        if (converter != null){
            converter.razValues();
        }
        if (autoInput != null){
            autoInput.razValues();
            autoInput.setEnabled(!blnDefaultManual);   
        }
        if (manualInput != null){
            manualInput.razValues();
            manualInput.setEnabled(blnDefaultManual);  
        }
        if (test) 
            resultTest.setText(Global.EMPTY_STRING);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose(){
        super.dispose();
        if (datatype != null) datatype.dispose();
        if (converter != null) converter.dispose();
        if (manualInput != null)manualInput.dispose();
        if (autoInput != null)autoInput.dispose();
        if (test){
            buttonTest.dispose();
            resultTest.dispose();
        }
    }
    
    /*
     * Setter StationSection properties
     */
    
    public void setManualValueInput(String value, int i){
        autoInput.setEnabled(false);
        autoInput.razValues();
        manualInput.setValue(value, i);
    }
    public void setManualValueInput(String value){
        setManualValueInput(value, 0);
    }
    
    public void setAutoValueInput(String line, String start, String end, boolean posVariable, boolean valueVariable, int i){
        manualInput.setEnabled(false);
        manualInput.razDefaultValues();
        autoInput.setValue(line, start, end, posVariable, valueVariable, i);
    }
    
    public void setAutoValueInput(String line, String start, String end, int i){
        setAutoValueInput(line, start, end, false, false, i);
    }
    
    public void setAutoValueInput(String line, String start, String end){
        setAutoValueInput(line, start, end, false, false, 0);
    }
    
    public void setAutoValueInput(PropertyModel propertyModel, Integer real_ligne, boolean posVariable, boolean offset, int i){
        int ligne, debut, fin;
        boolean vari = false;
        HeaderFragment frag = propertyModel.getFragment();

        ligne = frag.getFirstLine();
        debut = frag.getFirstCharOffset();
        // FAE 0001226 : décaler le lastcharoffset pour qu'il prenne en compte le nombre de lignes
        if (offset) Utils.calculRealOffsetFin(frag);
        fin = frag.getLastCharOffset();
        if (posVariable) vari = frag.isVariable();
        
        if (real_ligne == null){
            real_ligne = new Integer(ligne + Integer.valueOf(EditDesc.sectionEnteteFichier.getNumberValue()));
        }
            
        setAutoValueInput(String.valueOf(real_ligne), String.valueOf(debut), String.valueOf(fin),posVariable, vari, i);
        
        
    }
    
    public void setAutoValueInput(PropertyModel propertyModel, boolean posVariable){        
        setAutoValueInput(propertyModel, null, posVariable, false, 0);
    }
    
    public void setAutoValueInput(PropertyModel propertyModel, Integer real_ligne){        
        setAutoValueInput(propertyModel, real_ligne, false, false, 0);
    }
    
    public void setAutoValueInput(PropertyModel propertyModel, int i){        
        setAutoValueInput(propertyModel, null, false, false, i);
    }
    
    public void setAutoValueInput(PropertyModel propertyModel){        
        setAutoValueInput(propertyModel, null, false, false, 0);
    }

    
    /*
     * Getter StationSection properties
     */    
    /**
     * @return true if ManualInput is selected
     */
    public boolean isManual(){
        return manualInput.isSelected();
    }
    
    public boolean isInData(){
        boolean inData = false;
        if (autoInput != null){
            inData = autoInput.isInData();
        }
        return inData;
    }
    
    
    public boolean isVariablePos(){
        boolean variable = false;
        if (autoInput != null){
            variable = autoInput.isVariablePos();
        }
        return variable;
    }
    
    public boolean isTimeSeriesInfo(){
        return timeSeriesInfo;
    }
    
    public boolean checkAutoInputValues(int i, String nomSection){
        return autoInput.checkAutoLine(i, nomSection);
    }
    
    public boolean checkAutoInputValues(String nomSection){
        return autoInput.checkAutoLine(0, nomSection);
    }
    
    public int calculNbreLignes(int i){
        return autoInput.calculNbreLignes(i);
    }
    
    public int calculNbreLignes(){
        return autoInput.calculNbreLignes(0);
    }
    
    public boolean checkPosition(int i, int nbreLignes){
        return autoInput.checkPosition(i, nbreLignes);
    }
    
    public boolean checkPosition(int nbreLignes){
        return autoInput.checkPosition(0, nbreLignes);
    }
    
//    public boolean checkInDataOrHeader(int i, String lign, String sectionName){
//        return autoInput.checkInDataOrHeader(i, lign, sectionName);
//    }
    
    public boolean checkInDataOrHeader(String lign, String sectionName){
        return autoInput.checkInDataOrHeader(0, lign, sectionName);
    }


    /**
     * Getter sur enableInData.
     * @return the enableInData
     */
    public MetaInData getEnableInData()
    {
        return enableInData;
    }
    
    /**
     * Getter sur autoInput.
     * @return the autoInput
     */
    public GrpAutoInput getAutoInput()
    {
        return autoInput;
    }

    /**
     * Getter sur manualInput.
     * @return the manualInput
     */
    public GrpManualInput getManualInput()
    {
        return manualInput;
    }
    
    public String getValue(){
        String value = Global.EMPTY_STRING;
        if (isManual()){
            value = manualInput.getValue();
        }else{
            value = autoInput.getValue();
        }
        return value;
    }
    
    public String getValue(int i){
        String value = Global.EMPTY_STRING;
        if (isManual()){
            value = manualInput.getValue(i);
        }else{
            value = autoInput.getValue(i);
        }
        return value;
    }
    
    public String getManualValue(int i){
        String value = Global.EMPTY_STRING;
        if (manualInput != null) 
            value = manualInput.getValue(i);
        return value;
    }
    
    public String getManualValue(){
        return getManualValue(0);
    }
    
    public String getLineText(int i){
        String lineText = null;
        if (autoInput != null) 
            lineText = autoInput.getLines().get(i).getNumberLine().getText();
        return lineText;
    }
    
    /**
     * Getter sur datatype.
     * @return the datatype
     */
    public Couple getDatatype()
    {
        return datatype;
    }

    /**
     * Getter sur converter.
     * @return the converter
     */
    public Couple getConverter()
    {
        return converter;
    }
    
    /**
     * Getter sur resultTest.
     * @return the resultTest
     */
    public Text getResultTest()
    {
        return resultTest;
    }
    
    public Cursor getCursor(int i){
        return autoInput.getLines().get(i).getCursor();
    }

    /*
     * Drawing StationSection methodes
     */        
    /**
     * drawing a standard StationSection
     * @param sectionClient
     */
    @Override
    protected void drawSection(Composite sectionClient){  
        
        if (datatype != null) datatype.draw(sectionClient);
        
        if (converter != null) converter.draw(sectionClient);
        
        if (manualInput != null){
            manualInput.draw(sectionClient);
            // LISTENER
            manualInput.getButtonSaisie()
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {

                    manualInput.setEnabled(true);
                    
                    if (autoInput != null){
                        autoInput.razValues();
                        autoInput.setEnabled(false);
                    }
                }
            });
            // DefaultValue
            if (!EditStation.filled) manualInput.setEnabled(blnDefaultManual);
        }
       
        
        if (autoInput != null){
            autoInput.draw(sectionClient);
            // LISTENER
            autoInput.getButtonPos()
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent f) {

                    // raz de la valeur manuelles et injectées
                    if (manualInput != null){
                        manualInput.razDefaultValues();
                        manualInput.setEnabled(false);
                    }
                    autoInput.setEnabled(true);
                }
            });      
            // DefaultValue
            //autoInput.getButtonPos().setSelection(!blnDefaultManual);
            if (!EditStation.filled) autoInput.setEnabled(!blnDefaultManual);     
        }
        
        if (test) drawTest(sectionClient);
    }
    
    /**
     * drawing the TestGroup
     * @param sectionClient
     */
    private void drawTest(Composite sectionClient){
        Composite compoTest = new Composite(sectionClient, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoTest);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(compoTest);
        
        buttonTest.setParent(compoTest);
        buttonTest.setText(Messages.label_test);
        // LISTENER
        buttonTest
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent g) {
                String errorMessage = Global.EMPTY_STRING;
                Utils.messageErreur(errorMessage);
                try{
                    switch (cType){
                        case DATE : 
                            errorMessage = Messages.Etape_122;
                            dateTest(); 
                            break;
                        case TIME : 
                            errorMessage = Messages.Etape_121;
                            timeTest(); 
                            break;
                        case LAT : 
                            errorMessage = Messages.Etape_53; 
                            latitudeTest(); 
                            break;
                        case LON : 
                            errorMessage = Messages.Etape_58;
                            longitudeTest(); 
                            break;
                        case FRQ : 
                            //errorMessage = Messages.Etape_103;
                            samplingRateTest(); 
                            break;
                        case NONE : 
                            if (name.equals(S_STATIONNUMBER)){    
                                //errorMessage = Messages.Etape_128 + Messages.error;           
                                stationNumberTest(); 
                            }else{
                                //errorMessage = Messages.Etape_70;
                                defaultTest();
                            }
                            break;
                        default : 
                            defaultTest();
                            break;
                    }
                }catch (Exception e) {
                    
                    Utils.messageErreur(errorMessage);
                    resultTest.setText(Messages.error);
                    resultTest.pack();
                }
            }
        });

        resultTest.setParent(compoTest);
        resultTest.setEnabled(false);
    }
    
    /*
     *  TEST methodes
     */

    protected void stationNumberTest(){
        String result = Global.EMPTY_STRING;
        if (!isManual()) {
            String line = autoInput.getLines().get(0).getNumberLine().getText().trim();
            String start = autoInput.getLines().get(0).getStart().getText().trim();
            String end = autoInput.getLines().get(0).getEnd().getText().trim();
            if (line.equals(Global.ZERO)) {
                // On recupere le numero de station dans le nom
                // du fichier
                String strFichier = Global.EMPTY_STRING;
                if (ViewFile.radioFichier.getSelection()) {
                    strFichier = Utils.extraitFichierChemin(ViewFile.nomFichier);
                } else {
                    File directory = new File(ViewFile.nomRepertoire);
                    File[] subfiles = directory.listFiles();
                    strFichier = subfiles[0].getName();
                }
                result = (strFichier.substring(Integer.parseInt(start) - 1, Integer.parseInt(end))).trim();
            } else {
                result = autoInput.getValue(0).trim();
            }
        } else {
            result = manualInput.getValue().trim();
        }
        resultTest.setText(Formatter.getSingleton().padZero(result, 5));
        resultTest.pack();
    }
   

    protected void dateTest() throws ParseException{
        
        TimeZone tz = TimeZone.getTimeZone("GMT");       //$NON-NLS-1$
        Calendar cal = Calendar.getInstance(tz);
        cal.clear();
        cal.setLenient(false);
        
        StationSection time = EditStation.time;

        // TEST DATE
        resultTest.setText(getValue());
        if (!isManual()) autoInput.selectInViewFile();
        
        DateTimeFormat format = new DateTimeFormat(converter.getText());
        Long DateLong = (Long) format.parseObject(resultTest.getText().replace(' ', '0'));

        // TEST HEURE (pour decalage TU)
        Long HourLong = time.timeTest();
        cal.setTimeInMillis(DateLong + HourLong);

        if (!isManual())
            autoInput.getLines().get(0).selectInViewFile();

        DateLong = cal.getTimeInMillis();

        Formatter formatter = Formatter.getSingleton();

        resultTest.setText(formatter.formatDateTime("yyyy-MM-dd", DateLong));       //$NON-NLS-1$
        resultTest.pack();
    }

    protected long timeTest() throws ParseException
    {
        
        resultTest.setText(getValue());
        if (!isManual()) autoInput.selectInViewFile();
        
        DateTimeFormat format = new DateTimeFormat(converter.getText());

        Float flDecalTU = Float.parseFloat(EditStation.utConvertion.getValue()) * 3600 * 1000;
        Long lgDecalTU = flDecalTU.longValue();
        Long HourLong = (Long) format.parseObject(resultTest.getText().replace(' ', '0')) + lgDecalTU;

        Formatter formatter = Formatter.getSingleton();

        resultTest.setText(formatter.formatDateTime("hh24:mm:ss.sss", HourLong, "9999"));       //$NON-NLS-1$       //$NON-NLS-2$
        resultTest.pack();
        
        return HourLong;
    }

    protected void latitudeTest() throws ParseException {
        
        resultTest.setText(getValue());
        if (!isManual()) autoInput.selectInViewFile();

        LatitudeFormat format = new LatitudeFormat(converter.getText());
        format.setVariableLength(autoInput.getButtonVari().getSelection());

        Double latitudeDouble = (Double) format.parseObject(resultTest.getText());

        Formatter formatter = Formatter.getSingleton();

        resultTest.setText(formatter.formatLatitude("NDD MM.mm", latitudeDouble));             //$NON-NLS-1$
        resultTest.pack();
    }

    protected void longitudeTest() throws ParseException {

        resultTest.setText(getValue());
        if (!isManual()) autoInput.selectInViewFile();

        LongitudeFormat format = new LongitudeFormat(converter.getText());
        format.setVariableLength(autoInput.getButtonVari().getSelection());
        Double longitudeDouble = (Double) format.parseObject(resultTest.getText());
        Formatter formatter = Formatter.getSingleton();

        resultTest.setText(formatter.formatLongitude("NDDD MM.mm", longitudeDouble));       //$NON-NLS-1$
        resultTest.pack();
    }

    protected void samplingRateTest(){
        
        if (!converter.getText().trim().equals(Global.EMPTY_STRING)) {
            String stringValue = Global.EMPTY_STRING;
            // il y a une formule de conversion
            if (isManual()){
                // saisie manuelle
                if (manualInput.getValue(0) != null
                      && !manualInput.getValue(0).equals(Global.EMPTY_STRING)){
                    stringValue = manualInput.getValue(0).trim();
                }
            }else{
                // position dans fichier
                String line = autoInput.getLines().get(0).getNumberLine().getText().trim();
                String start = autoInput.getLines().get(0).getStart().getText().trim();
                String end = autoInput.getLines().get(0).getEnd().getText().trim();
                if (checkAutoInputValues(Messages.getString(NemoSection.S_SAMPLINGRATE).trim())){ 
                    // récupérer le texte en question et creer une nouvelle heure
                    String ligne = Utils.lignesBrutes.get(Integer.parseInt(line) - 1);
                    stringValue = ligne.substring(Integer.parseInt(start) - 1, Integer.parseInt(end));
                }    
            }
          
            if (!stringValue.equals(Global.EMPTY_STRING)){
        
                org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
                parser.addStandardFunctions();
                parser.addStandardConstants();
                parser.addVariable(Global.STR_CONVERT, Float.valueOf(stringValue));
                parser.parseExpression(Global.STR_CONVERT + converter.getText().trim());
        
                double resultJEP = parser.getValue();
        
                resultTest.setText(String.valueOf(resultJEP));
                resultTest.pack();
            }    
        }
    }

    protected void defaultTest() throws NumberFormatException{
        String result = Global.EMPTY_STRING;
        if (!isManual()) {
            result = autoInput.getValue(0).trim();
            autoInput.selectInViewFile();
        } else {
            result = manualInput.getValue().trim();
        }
        if (!result.equals(Global.EMPTY_STRING)){
            result = Formatter.getSingleton().formatInt("%-5d", Float.parseFloat(result)); //$NON-NLS-1$
        }
        resultTest.setText(result);
        resultTest.pack();
    }
    

    
    @Override
    public void initModel(StationModel stationModel){
        String ligne = null;
        HeaderFragment frag;
        Station station = stationModel.getModel();
        
        if (name.equals(S_STATIONNUMBER)){    
            // Station number reste un cas particulier
            initReferenceModel(stationModel);
        }else if (name.equals(S_DATATYPE)){
            // DataType également un cas particulier
            initDataTypeModel(station);
        }else{
            // généralisation des autres sections
            for (int i = 0; i < nbLignes; i++){
                
                // récupération du propertyModel correspondant
                String tagXml = getTagXml(i);
                PropertyModel propertyModel = getPropertyModel(stationModel, tagXml);
                ExternalizableFormat format = propertyModel.getFormat();
                
                // gestion des flags qualite
                String property = name;
                if (name.equals(S_TIME) || name.equals(S_DATE)) property = Global.DATETIME;
                FlagFragment flag = EditStation.qcFlags.fillFlagFragment(property);
                if (flag != null) propertyModel.setFlag(flag);
                
                // gestion des converteurs
                if (converter != null){
                    String formatSaisi = converter.getText();
                    if (converter.getValueCombo() != null){
                        try
                        {
                            if (format instanceof LatitudeFormat){
                                ((LatitudeFormat) format).setFormatString(formatSaisi);
                            }else if (format instanceof LongitudeFormat){
                                ((LongitudeFormat) format).setFormatString(formatSaisi);
                            }else if (format instanceof DateTimeFormat){
                                ((DateTimeFormat) format).setFormatString(formatSaisi);
                                if ((name.equals(S_TIME) || name.equals(S_DATE)) && isInData()){
                                    AutoLine line = autoInput.getLines().get(i);
                                    ligne = String.valueOf(DataModel.stationModel.getNumberOfLines() + 1);
                                    stationModel.getTimestampModel().getFormat(name).setFormatString(formatSaisi);
                                    stationModel.getTimestampModel().getFragment(name).setFirstCharOffset(line.getStart().getText());
                                    stationModel.getTimestampModel().getFragment(name).setLastCharOffset(line.getEnd().getText());
                                }
                            }
                        }
                        catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                    }else{
                        // conversion JEP de la frequence
                        if (formatSaisi != null && !formatSaisi.trim().equals(Global.EMPTY_STRING))
                        {
                            // formule de conversion
                            stationModel.getTimeSeriesModel().setSampleRateConversion(formatSaisi);
                        }
                    }
                }
                
                // gestion des valeurs saisies
                if (isManual()){
                    // saisie manuelle
                    fillManualModel(propertyModel, station, tagXml, format, i);
                    
                }else{
                    // position dans fichier
                    boolean fillVariPos = (autoInput.getButtonVari() != null);;
                    
                    if (name.equals(NemoSection.S_COMMENT)){
                        // cas particuliers : COMMENT
                        // surlignement et injection ligne debut fin
                        frag = propertyModel.fillFragment(this, ligne, i, false, fillVariPos);
                        int entete = Integer.valueOf(EditDesc.sectionEnteteFichier.getNumberValue());
                        Utils.calculNbreLignesPuisRemplissageFrag(frag, Integer.valueOf(entete));
                    }else{
                       // Généralisation                        
                        if (((i > 0) && (getLineText(i).trim().equals(Global.EMPTY_STRING)))
                                || getValue() == null || getValue().trim().equals(Global.EMPTY_STRING)){
                            fillManualModel(propertyModel, station, tagXml, format, i);
                        }else{
                            propertyModel.fillFragment(this, ligne, i, true, fillVariPos);
                            if (fillVariPos){
                                if (format instanceof LatitudeFormat){
                                    ((LatitudeFormat) format).setVariableLength(isVariablePos());
                                }else if (format instanceof LongitudeFormat){
                                    ((LongitudeFormat) format).setVariableLength(isVariablePos());
                                }
                            }
                        }
                    }                 
                }
            }
        }
    }
    
    private void fillManualModel(PropertyModel propertyModel, Station station, String tagXml, ExternalizableFormat format, int i){
        propertyModel.setUserProvided(true);
        String valueString = getManualValue(i);
        if (isTimeSeriesInfo())
            station.getTimeSeriesInformation().set(tagXml, valueString, format);
        else
            station.set(tagXml, valueString, format);  
    }
    
    private void initReferenceModel(StationModel stationModel){
        String ligne = null;
        PropertyModel propertyModel = stationModel.getReferenceProperty();
        if (EditStation.stationNumber.isManual())
        {
            String stationNumber = EditStation.stationNumber.getManualValue();
            stationModel.setIncrementRef(true);
            if (ViewFile.radTrajectoire.getSelection())
                stationModel.getTrajectory().setStationNumber(stationNumber);
            else
                stationModel.setIncrementNumber(stationNumber);
            propertyModel.setUserProvided(true);
        }
        else
        {
            stationModel.setIncrementRef(false);
            int ligneSaisie = Integer.valueOf(EditStation.stationNumber.getLineText(0));
            if (ligneSaisie == 0){
                ligne = Global.ZERO;
            }else{
                ligne = String.valueOf(ligneSaisie - Integer.valueOf(EditDesc.sectionEnteteFichier.getNumberValue()));
            }
            propertyModel.fillFragment(EditStation.stationNumber, ligne);
        }
    }
    
    private void initDataTypeModel(Station station){
        if (!ViewFile.isCollection()) // FAE 022
        {            
            String typeCombo = datatype.getText();
            DataType newDataType = new DataType();
            String code = Global.EMPTY_STRING;                                                                         
            // si on a ouvert un modele, on a le code direct !
            if (DataModel.ouverture){
                code = typeCombo;
            }else{
                Pattern p = Pattern.compile(Global.SEP);                                                    //$NON-NLS-1$
                String mots[] = p.split(typeCombo);
                
                int taille = mots.length;
                if (taille >= 1){
                    code = mots[0];
                }
            }
            // creation d'un nouveau Datatype avec les infos recuperees au dessus.
            newDataType.setCode(Utils.extraitCode(code));
            for (int i = 0; i < TableDataSurvey.types.size(); i++)
            {
                if (typeCombo.contentEquals(TableDataSurvey.types.get(i).getCode()))
                {
                    // c'est le bon dataType
                    // on recupere le nombre de mesures et le QC
                    newDataType.setNumberOfProfiles(TableDataSurvey.types.get(i).getNombre());
                    newDataType.setQc(TableDataSurvey.types.get(i).getQc());
                    newDataType.setSensor(TableDataSurvey.types.get(i).getSensor());
                }
            }
            station.setDataType(newDataType);
        }
    }
    
    @Override
    public void fillFromModel(StationModel stationModel){
        Station station = stationModel.getModel();
        if (name.equals(S_STATIONNUMBER)){
            fillReference(stationModel);
        }else if (name.equals(S_DATATYPE)){
            fillDataType(station);
        }else{
            // généralisation des autres sections
            for (int i = 0; i < nbLignes; i++){
                
                // récupération du propertyModel correspondant
                String tagXml = getTagXml(i);
                PropertyModel propertyModel = getPropertyModel(stationModel, tagXml);
                ExternalizableFormat format = propertyModel.getFormat();

                // gestion des flags qualite
                String propertyCode = name;
                if (name.equals(S_TIME) || name.equals(S_DATE)) propertyCode = Global.DATETIME;
                EditStation.qcFlags.setFlagFragment(propertyModel.getFlag(), propertyCode, Messages.getString(propertyCode).trim());
                
                
                // gestion des converters
                if (converter != null){
                    String formule = Global.EMPTY_STRING;
                    switch (cType){
                        case FRQ :
                            formule = stationModel.getTimeSeriesModel().getSampleRateConversion();
                            if (formule.equals(Global.EMPTY_STRING)) formule = "*1"; //$NON-NLS-1$ 
                            break;
                        case TIME :
                        case DATE :
                            if (!ViewFile.radProfilVertical.getSelection() 
                                    && !stationModel.getTimestampModel().isComputed())
                                formule = stationModel.getTimestampModel().getFormat(name).getFormatString();
                            else
                                formule =  ((DateTimeFormat) format).getFormatString();
                            break;
                        case LAT :
                            formule =  ((LatitudeFormat) format).getFormatString();
                            break;
                        case LON :
                            formule =  ((LongitudeFormat) format).getFormatString();
                            break;
                        default : break;
                    }
                    converter.setText(formule);
                }
                

                if ((name.equals(S_TIME) || name.equals(S_DATE)) 
                        && !ViewFile.radProfilVertical.getSelection()
                        && !stationModel.getTimestampModel().isComputed())
                {
                    // cas des données tempoprelles dans les données
                    LineFragment lineTime = stationModel.getTimestampModel().getFragment(name);
                    setAutoValueInput(Global.EMPTY_STRING, String.valueOf(lineTime.getFirstCharOffset()), String.valueOf(lineTime.getLastCharOffset()), i);
                }
                else
                {
                    // gestion des valeurs saisies
                    if (propertyModel.isUserProvided())
                    {
                        String value = Global.EMPTY_STRING;
                        if (isTimeSeriesInfo())
                            value = station.getTimeSeriesInformation().get(tagXml, format);
                        else
                            value = station.get(tagXml, format);
                        setManualValueInput(value, i);
                    }
                    else
                    {
                        // position dans fichier
                        boolean fillVariPos = (autoInput.getButtonVari() != null);
                        boolean offset = false;
                        if (name.equals(NemoSection.S_COMMENT)) offset = true;
                        
                        
                        if ((i > 0) && propertyModel.isUserProvided()){
                            // on a rentre que la ligne 1 donc les valeurs de ligne 2 sont vides
                            setAutoValueInput(Global.EMPTY_STRING, Global.EMPTY_STRING, Global.EMPTY_STRING, i);
                        }
                        else
                        {
                            if (autoInput.getLines().get(i).getCursor() != null)
                                Utils.calculPosxPosy(propertyModel.getFragment(), getCursor(i));
                            setAutoValueInput(propertyModel, null, fillVariPos, offset, i);
                        }
                    }
                }
                
            }

            
        }
        
    }
    
    private void fillReference(StationModel stationModel){

        // SECTION numero de station
        if (stationModel.isIncrementRef())
        {
            String value = null;
            if (stationModel.getInputType().equals(Global.TRAJECTORY)){
                // FAE 1386 : saisie manuelle, non incrementable pour Trajectoires
                value = stationModel.getTrajectory().getStationNumber();
            }else{
                value = String.valueOf(stationModel.getIncrementNumber());
            }
            EditStation.stationNumber.setManualValueInput(value);
        }
        else
        {
            // valeurs de ligne/debut/fin
            PropertyModel propertyModel = stationModel.getReferenceProperty();
            HeaderFragment frag = propertyModel.getFragment();
            Integer realLigne = null;
            int ligne = frag.getFirstLine();
            if (ligne == 0) realLigne = new Integer(0);
            EditStation.stationNumber.setAutoValueInput(propertyModel, realLigne);

        }
    }
    
    private void fillDataType(Station station){

        // SECTION type de donnee
        if (!ViewFile.isCollection()) // FAE022
        {
            DataType dataType = station.getDataType();
            if (dataType != null){
                String code = dataType.getCode();
                // faire un split sur - au cas oe et n'avoir QUE le code
    
                Pattern p = Pattern.compile(Global.SEP); 
                String mots[] = p.split(code);
                String vraiCode = mots[0];
    
                // avant de charger le type de donnees, verifier qu'il existe bien
                // dans la liste mise à jour!!
                int taille = TableDataSurvey.types.size();
                Boolean ok = false, capteur = false;
                for (int i = 0; i < taille; i++)
                {
                    // check si le type existe toujours ou pas
                    Pattern pat = Pattern.compile(Global.SEP);
                    String motsType[] =
                        pat.split(TableDataSurvey.types.get(i).getCode());
                    String vraiCodeType = motsType[0];
    
                    if (vraiCode.equals(vraiCodeType))
                    {
                        ok = true;
                        if (TableDataSurvey.types.get(i).getSensor())
                        {
                            capteur = true;
                        }
                    }
                }
                // ajout du parametre ou non du coup
                if (ok)
                {
                    // ajoute que si c'est un capteur
                    if (capteur)
                    {
                        Utils.selectComboCode(EditStation.dataType.getDatatype().getValueCombo(), code);
                    }
                }
            }
        }
    }
    
    private String getTagXml(int i){
        String tagXml = name;
        if (nbLignes > 1) tagXml += String.valueOf(i + 1);
        return tagXml;
    }
    
    private PropertyModel getPropertyModel(StationModel stationModel, String tagXml){
        PropertyModel propertyModel;
        if (isTimeSeriesInfo())
            propertyModel = stationModel.getTimeSeriesModel().propertiesAt(tagXml);
        else
            propertyModel = stationModel.propertiesAt(tagXml);
        return propertyModel;        
    }
    
    public void diseableLineInData(){
        if (autoInput != null && autoInput.isSelected()){
            for (AutoLine line : autoInput.getLines()){
                line.getNumberLine().setEnabled(!line.isInData());
            }
            autoInput.disableEndVari();
        }
    }
    
}
