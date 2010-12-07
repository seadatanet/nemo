/* ============================================================================
 * Nom du fichier	: StationSection.java
 * Auteur			: dboulang
 * Date				: 21 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.station;

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
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.DataType;
import fr.ifremer.nemo.input.Fragment;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.Couple;
import fr.ifremer.nemo.ui.edit.Cursor;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.ui.edit.cruise.TableDataSurvey;
import fr.ifremer.nemo.ui.edit.station.AutoLine.MetaInData;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.format.DateTimeFormat;
import fr.ifremer.nemo.util.format.ExternalizableFormat;
import fr.ifremer.nemo.util.format.LatitudeFormat;
import fr.ifremer.nemo.util.format.LongitudeFormat;

/**
 * Description: objet graphique représentant une section dans l'onglet station
 * 
 * @author dboulang
 */
public class StationSection extends NemoSection
{
    private Couple converter;
    protected ComboType cType = ComboType.NONE;
    private final int nbLignes;

    protected GrpManualInput manualInput;
    private final boolean blnDefaultManual;

    protected GrpAutoInput autoInput;
    private MetaInData enableInData;

    private final boolean test;
    private Button buttonTest;
    protected Text resultTest;

    private Couple datatype;

    private boolean timeSeriesInfo = false;

    public static final String defaultValue = "Default";
    
    protected EditStationForm myForm;

    /*
     * construct StationSection methodes
     */

    /**
     * initialize StationSection
     * 
     * @param shell
     * @param title
     * @param description
     * @param blnManuel
     * @param test
     */
    public StationSection(String name, String title, String description,
            boolean blnDefaultManual, boolean test, int nbLignes, EditStationForm form)
    {

        super(name, title, description, form);
        this.blnDefaultManual = blnDefaultManual;
        this.test = test;
        this.nbLignes = nbLignes;
        myForm = form;
    }

    /**
     * add a converter group
     * 
     * @param shell
     * @param cType
     * @param defaultValue
     */
    public void addConverter(ComboType type, String defaultValue)
    {
        cType = type;
        if (cType != ComboType.NONE)
        {
            converter = new Couple(cType, defaultValue);
        }
    }

    /**
     * add a ManualInput group
     * 
     * @param shell
     * @param manualLabel
     * @param cType
     * @param defaultValue
     */
    public void addManualInput(String manualLabel, ComboType type,
            String defaultValue)
    {
        manualInput =
            new GrpManualInput(nbLignes, manualLabel, type, defaultValue);
    }
    
    
    public void  addNoValueInput(String manualLabel, ComboType type,
            String defaultValue)
    {
        manualInput =
            new GrpNoValueInput(nbLignes, manualLabel, type, defaultValue);
    }
    /**
     * add a AutoInput group
     * 
     * @param shell
     * @param nbAutoLines
     * @param autoLabel
     * @param variablePos
     * @param enableInData
     */
    public void addAutoInput(String autoLabel, boolean variablePos,
            MetaInData enable, boolean withCursor, boolean defaultValueEnable)
    {
        enableInData = enable;
        autoInput =
            new GrpAutoInput(1, autoLabel, variablePos, enableInData,
                withCursor, defaultValueEnable);
    }
  
    /**
     * add a simple Couple group (DATATYPE)
     * 
     * @param shell
     * @param cType
     * @param defaultValue
     */
    public void addCouple(ComboType type, String defaultValue)
    {
        datatype = new Couple(type, defaultValue);
    }

    
    // BGT not used?
//    public void copyValues(StationSection initiale)
//    {
//        if (initiale.getConverter() != null)
//            converter = initiale.getConverter();
//        if (initiale.getAutoInput() != null)
//            autoInput = initiale.getAutoInput();
//        if (initiale.getManualInput() != null)
//            manualInput = initiale.getManualInput();
//    }

    /*
     * Modify StationSection properties
     */

    public void setTimeSeriesInfo(boolean timeInfo)
    {
        timeSeriesInfo = timeInfo;
    }

    /**
     * Setter sur enableInData.
     * 
     * @param enableInData
     *            the enableInData to set
     */
    public void setEnableInData(MetaInData enableInData)
    {
        this.enableInData = enableInData;
        autoInput.setEnableInData(enableInData);
    }

    /**
     * @param manual
     */
    public void setEnabledManual(boolean manual)
    {
        if (!manual)
        {
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
    public void razValues()
    {

        if (datatype != null)
            datatype.razValues();
        if (converter != null)
        {
            converter.razValues();
        }
        if (autoInput != null)
        {
            autoInput.razValues();
            autoInput.setEnabled(!blnDefaultManual);
        }
        if (manualInput != null)
        {
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
    public void dispose()
    {
        super.dispose();
        if (datatype != null)
            datatype.dispose();
        if (converter != null)
            converter.dispose();
        if (manualInput != null)
            manualInput.dispose();
        if (autoInput != null)
            autoInput.dispose();
        if (test)
        {
            buttonTest.dispose();
            resultTest.dispose();
        }
    }

    /*
     * Setter StationSection properties
     */

    public void setManualValueInput(String value, int i)
    {
        autoInput.setEnabled(false);
        autoInput.razValues();
        
        if (!isNoValue()){
            manualInput.setValue(value, i);
        }
      
    }

    public void setManualValueInput(String value)
    {
        setManualValueInput(value, 0);
    }

    public void setAutoValueInput(String line, String start, String end,
            boolean posVariable, boolean valueVariable, int i,
            boolean hasDefaultValue, boolean isDefaultValueSeceted,
            String defaultValue)
    {
        manualInput.setEnabled(false);
        manualInput.razDefaultValues();
        autoInput.setValue(line, start, end, posVariable, valueVariable, i,
            hasDefaultValue, isDefaultValueSeceted, defaultValue);
    }

    public void setAutoValueInput(String line, String start, String end, int i)
    {
        setAutoValueInput(line, start, end, false, false, i, false, false, "");
    }

    public void setAutoValueInput(String line, String start, String end)
    {
        setAutoValueInput(line, start, end, false, false, 0, false, false, "");
    }

    public void setAutoValueInput(PropertyModel propertyModel,
            Integer real_ligne, boolean posVariable, boolean offset, int i)
    {
        int ligne, debut, fin;
        boolean vari = false;
        Fragment frag = propertyModel.getFragment();

        ligne = frag.getFirstLine();
        debut = frag.getFirstCharOffset();

        // FAE 0001226 : décaler le lastcharoffset pour qu'il prenne en compte
        // le nombre de lignes
        if (offset)
            Utils.calculRealOffsetFin(frag);
        fin = frag.getLastCharOffset();
        if (posVariable)
            vari = frag.isVariable();

        if (real_ligne == null)
        {
            real_ligne =
                new Integer(ligne
                        + Integer
                            .valueOf(Nemo.getInstance().getViewMain().getEditFileForm().getSectionFileHeader()
                                .getValue()));
        }

        boolean hasDefaultValue = frag.isDefaultValue();
        boolean isDefaultValueSeceted = frag.isDefaultValueSelected();
        String defaultValue = frag.getDefaultValue();
        setAutoValueInput(String.valueOf(real_ligne), String.valueOf(debut),
            String.valueOf(fin), posVariable, vari, i, hasDefaultValue,
            isDefaultValueSeceted, defaultValue);

    }

    public void setAutoValueInput(PropertyModel propertyModel,
            boolean posVariable)
    {
        setAutoValueInput(propertyModel, null, posVariable, false, 0);
    }

    public void setAutoValueInput(PropertyModel propertyModel,
            Integer real_ligne)
    {
        setAutoValueInput(propertyModel, real_ligne, false, false, 0);
    }

    public void setAutoValueInput(PropertyModel propertyModel, int i)
    {
        setAutoValueInput(propertyModel, null, false, false, i);
    }

    public void setAutoValueInput(PropertyModel propertyModel)
    {
        setAutoValueInput(propertyModel, null, false, false, 0);
    }

    /*
     * Getter StationSection properties
     */
    /**
     * @return true if ManualInput is selected
     */
    public boolean isManual()
    {
        boolean isManual=false;
        if (isNoValue())
        {
            isManual = ((GrpNoValueInput)manualInput).isSelected();
           
        }else 
        {
            isManual = manualInput.isSelected();
        }
        return isManual;
    }
    /**
     * @return true if ManualInput is NoValue type
     */
    public boolean isNoValue()
    {
        return (manualInput instanceof GrpNoValueInput);
    }
    public boolean isInData()
    {
        boolean inData = false;
        if (autoInput != null)
        {
            inData = autoInput.isInData();
        }
        return inData;
    }

    public boolean isVariablePos()
    {
        boolean variable = false;
        if (autoInput != null)
        {
            variable = autoInput.isVariablePos();
        }
        return variable;
    }

    public boolean isTimeSeriesInfo()
    {
        return timeSeriesInfo;
    }

    public boolean checkAutoInputValues(int i, String nomSection)
    {
        return autoInput.checkAutoLine(i, nomSection);
    }

    public boolean checkAutoInputValues(String nomSection)
    {
        return autoInput.checkAutoLine(0, nomSection);
    }

    public int calculNbreLignes(int i)
    {
        return autoInput.calculNbreLignes(i);
    }

    public int calculNbreLignes()
    {
        return autoInput.calculNbreLignes(0);
    }

    public boolean checkPosition(int i, int nbreLignes)
    {
        return autoInput.checkPosition(i, nbreLignes);
    }

    public boolean checkPosition(int nbreLignes)
    {
        return autoInput.checkPosition(0, nbreLignes);
    }

    public boolean checkInDataOrHeader(String lign, String sectionName)
    {
        return autoInput.checkInDataOrHeader(0, lign, sectionName);
    }

    /**
     * Getter sur enableInData.
     * 
     * @return the enableInData
     */
    public MetaInData getEnableInData()
    {
        return enableInData;
    }

    /**
     * Getter sur autoInput.
     * 
     * @return the autoInput
     */
    public GrpAutoInput getAutoInput()
    {
        return autoInput;
    }

    /**
     * Getter sur manualInput.
     * 
     * @return the manualInput
     */
    public GrpManualInput getManualInput()
    {
        return manualInput;
    }

    public String getValue() throws StringIndexOutOfBoundsException
    {
        String value = Global.EMPTY_STRING;
        if (isManual())
        {
            value = manualInput.getValue();
        }
        else
        {
            value = autoInput.getValue();

            if (autoInput.isDefaultValueEnable())
                if (autoInput.checkBoxHasDefaultValue.getSelection())
                    if (value.trim().equals(
                        autoInput.textDefaultValue.getText().trim()))
                        value = defaultValue;
        }

        return value;
    }

    public String getValue(int i)
    {
        String value = Global.EMPTY_STRING;
        if (isManual() && !isNoValue())
        {
            value = manualInput.getValue(i);
        }
        else if(isManual() && isNoValue())
        {
            value = ((GrpNoValueInput)manualInput).getValue(i);
        }
        else
        {
            value = autoInput.getValue(i);
        }
        return value;
    }

    public String getManualValue(int i)
    {
        String value = Global.EMPTY_STRING;
        if (manualInput != null)
            value = manualInput.getValue(i);
        return value;
    }

    public String getManualValue()
    {
        return getManualValue(0);
    }

    public String getLineText(int i)
    {
        String lineText = null;
        if (autoInput != null)
            lineText = autoInput.getLines().get(i).getNumberLine().getText();
        return lineText;
    }

    /**
     * Getter sur datatype.
     * 
     * @return the datatype
     */
    public Couple getDatatype()
    {
        return datatype;
    }

    /**
     * Getter sur converter.
     * 
     * @return the converter
     */
    public Couple getConverter()
    {
        return converter;
    }

    /**
     * Getter sur resultTest.
     * 
     * @return the resultTest
     */
    public Text getResultTest()
    {
        return resultTest;
    }

    public Cursor getCursor(int i)
    {
        return autoInput.getLines().get(i).getCursor();
    }

    /*
     * Drawing StationSection methodes
     */
    /**
     * drawing a standard StationSection
     * 
     * @param sectionClient
     */
    @Override
    protected void drawSection(Composite sectionClient, boolean filled)
    {

        if (datatype != null)
            datatype.draw(sectionClient, filled);

        if (converter != null)
            converter.draw(sectionClient, filled);

        if (manualInput != null)
        {
            manualInput.draw(sectionClient, filled);
            // LISTENER
            manualInput.getButtonSaisie().addSelectionListener(
                new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e)
                    {

                        manualInput.setEnabled(true);

                        if (autoInput != null)
                        {
                            autoInput.razValues();
                            autoInput.setEnabled(false);
                        }
                    }
                });
            // DefaultValue
            if (!Nemo.getInstance().getViewMain().getEditStationForm().filled)
                manualInput.setEnabled(blnDefaultManual);
        }

        if (autoInput != null)
        {
            autoInput.draw(sectionClient, filled);
            // LISTENER
            autoInput.getButtonPos().addSelectionListener(
                new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent f)
                    {

                        // raz de la valeur manuelles et injectées
                        if (manualInput != null)
                        {
                            manualInput.razDefaultValues();
                            manualInput.setEnabled(false);
                        }
                        autoInput.setEnabled(true);
                    }
                });
            // DefaultValue
            // autoInput.getButtonPos().setSelection(!blnDefaultManual);
            if (!Nemo.getInstance().getViewMain().getEditStationForm().filled)
                autoInput.setEnabled(!blnDefaultManual);
        }

        if (test)
            drawTest(sectionClient);
    }

    /**
     * drawing the TestGroup
     * 
     * @param sectionClient
     */
    private void drawTest(Composite sectionClient)
    {
        Composite compoTest = new Composite(sectionClient, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoTest);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(compoTest);

        buttonTest = new Button(compoTest, SWT.NONE);
        buttonTest.setText(Messages.label_test);
        // LISTENER
        buttonTest
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent g)
                {
                    String errorMessage = Global.EMPTY_STRING;
                    Utils.messageErreur(errorMessage);
                    try
                    {
                        switch (cType)
                        {
                            case DATE:
                                errorMessage = Messages.Etape_122;
                                dateTest();
                            break;
                            case TIME:
                                errorMessage = Messages.Etape_121;
                                timeTest();
                            break;
                            case LAT:
                                errorMessage = Messages.Etape_53;
                                latitudeTest();
                            break;
                            case LON:
                                errorMessage = Messages.Etape_58;
                                longitudeTest();
                            break;
                            case FRQ:
                                // errorMessage = Messages.Etape_103;
                                samplingRateTest();
                            break;
                            case NONE:
                                if (name.equals(Intern.S_STATIONNUMBER))
                                {
                                    // errorMessage = Messages.Etape_128 +
                                    // Messages.error;
                                    stationNumberTest();
                                }
                                else
                                {
                                    // errorMessage = Messages.Etape_70;
                                    defaultTest();
                                }
                            break;
                            default:
                                defaultTest();
                            break;
                        }
                    }
                    catch (Exception e)
                    {

                        Utils.messageErreur(errorMessage);
                        resultTest.setText(Messages.error);
                        resultTest.pack();
                    }
                }
            });

        resultTest = new Text(compoTest, SWT.BORDER);
        resultTest.setEnabled(false);
    }

    /*
     * TEST methodes
     */

    protected void stationNumberTest()
    {
        String result = Global.EMPTY_STRING;
        if (!isManual())
        {
            String line =
                autoInput.getLines().get(0).getNumberLine().getText().trim();
            String start =
                autoInput.getLines().get(0).getStart().getText().trim();
            String end = autoInput.getLines().get(0).getEnd().getText().trim();
            if (line.equals(Global.ZERO))
            {
                // On recupere le numero de station dans le nom du fichier
                String strFichier = Global.EMPTY_STRING;
                if (Nemo.getInstance().getViewMain().getEditFileForm().isCruiseIsAFile())
                {
                    strFichier =
                        Utils.extraitFichierChemin(Nemo.getInstance().getViewMain().getEditFileForm().nomFichier);
                }
                else
                {
                    File directory = new File(Nemo.getInstance().getViewMain().getEditFileForm().nomRepertoire);
                    File[] subfiles = directory.listFiles();
                    if (subfiles[0].isFile())
                        strFichier = subfiles[0].getName();
                    else
                        strFichier = subfiles[0].listFiles()[0].getName();
                }
                result =
                    (strFichier.substring(Integer.parseInt(start) - 1, Integer
                        .parseInt(end))).trim();
            }
            else
            {
                result = autoInput.getValue(0).trim();
                autoInput.getLines().get(0).selectInViewFile();
            }
        }
        else
        {
            result = manualInput.getValue().trim();
        }

        resultTest.setText(Formatter.getSingleton().padZero(result, 5));
        resultTest.pack();
    }

    protected void dateTest() throws ParseException
    {

        TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
        Calendar cal = Calendar.getInstance(tz);
        cal.clear();
        cal.setLenient(false);

        // TEST DATE
        resultTest.setText(getValue());
        if (!isManual())
            autoInput.selectInViewFile();

        DateTimeFormat format = new DateTimeFormat(converter.getText());
        Long DateLong =
            (Long) format.parseObject(resultTest.getText().replace(' ', '0'));

        cal.setTimeInMillis(DateLong /* + HourLong */);

        if (!isManual())
            autoInput.getLines().get(0).selectInViewFile();

        DateLong = cal.getTimeInMillis();

        Formatter formatter = Formatter.getSingleton();

        resultTest.setText(formatter.formatDateTime("yyyy-MM-dd", DateLong)); //$NON-NLS-1$
        resultTest.pack();
    }

    protected long timeTest() throws ParseException
    {

        resultTest.setText(getValue());

        if (resultTest.getText().equals(defaultValue))
            return 0;

        if (!isManual())
            autoInput.selectInViewFile();

        DateTimeFormat format = new DateTimeFormat(converter.getText());

        Float flDecalTU =
            Float.parseFloat(myForm.utConvertion.getValue()) * 3600 * 1000;
        Long lgDecalTU = flDecalTU.longValue();
        Long HourLong =
            (Long) format.parseObject(resultTest.getText().replace(' ', '0'))
                    + lgDecalTU;

        Formatter formatter = Formatter.getSingleton();

        resultTest.setText(formatter.formatDateTime(
            "hh24:mm:ss.sss", HourLong, "9999")); //$NON-NLS-1$       //$NON-NLS-2$

        resultTest.pack();

        return HourLong;
    }

    protected void latitudeTest() throws ParseException
    {

        resultTest.setText(getValue());
        if (!isManual())
            autoInput.selectInViewFile();

        LatitudeFormat format = new LatitudeFormat(converter.getText());
        format.setVariableLength(autoInput.getButtonVari().getSelection());

        Double latitudeDouble =
            (Double) format.parseObject(resultTest.getText());

        Formatter formatter = Formatter.getSingleton();

        resultTest.setText(formatter
            .formatLatitude("NDD MM.mm", latitudeDouble)); //$NON-NLS-1$
        resultTest.pack();
    }

    protected void longitudeTest() throws ParseException
    {

        resultTest.setText(getValue());
        if (!isManual())
            autoInput.selectInViewFile();

        LongitudeFormat format = new LongitudeFormat(converter.getText());
        format.setVariableLength(autoInput.getButtonVari().getSelection());
        Double longitudeDouble =
            (Double) format.parseObject(resultTest.getText());
        Formatter formatter = Formatter.getSingleton();

        resultTest.setText(formatter.formatLongitude(
            "NDDD MM.mm", longitudeDouble)); //$NON-NLS-1$
        resultTest.pack();
    }

    protected void samplingRateTest()
    {

        if (!converter.getText().trim().equals(Global.EMPTY_STRING))
        {
            String stringValue = Global.EMPTY_STRING;
            // il y a une formule de conversion
            if (isManual())
            {
                // saisie manuelle
                if (manualInput.getValue(0) != null
                        && !manualInput.getValue(0).equals(Global.EMPTY_STRING))
                {
                    stringValue = manualInput.getValue(0).trim();
                }
            }
            else
            {
                // position dans fichier
                String line =
                    autoInput.getLines().get(0).getNumberLine().getText()
                        .trim();
                String start =
                    autoInput.getLines().get(0).getStart().getText().trim();
                String end =
                    autoInput.getLines().get(0).getEnd().getText().trim();
                if (checkAutoInputValues(Messages.getString(
                    Intern.S_SAMPLINGRATE).trim()))
                {
                    // récupérer le texte en question et creer une nouvelle
                    // heure
                    String ligne =
                        Utils.lignesBrutes.get(Integer.parseInt(line) - 1);
                    stringValue =
                        ligne.substring(Integer.parseInt(start) - 1, Integer
                            .parseInt(end));
                }
            }

            if (!stringValue.equals(Global.EMPTY_STRING))
            {

                org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
                parser.addStandardFunctions();
                parser.addStandardConstants();
                parser.addVariable(Global.STR_CONVERT, Float
                    .valueOf(stringValue));
                parser.parseExpression(Global.STR_CONVERT
                        + converter.getText().trim());

                double resultJEP = parser.getValue();

                resultTest.setText(String.valueOf(resultJEP));
                resultTest.pack();
            }
        }
    }

    protected void defaultTest() throws NumberFormatException
    {
        String result = Global.EMPTY_STRING;

        if (!isManual())
        {
            result = autoInput.getValue(0).trim();

            if (autoInput.defaultValueEnable)
                if (autoInput.checkBoxHasDefaultValue.getSelection())
                    if (result.equals(autoInput.textDefaultValue.getText()))
                        result = defaultValue;

            autoInput.selectInViewFile();
        }
        else
        {
            result = manualInput.getValue().trim();
        }

        if (!result.equals(Global.EMPTY_STRING) && !result.equals(defaultValue))
        {
            result =
                Formatter.getSingleton().formatInt(
                    "%-5d", Float.parseFloat(result)); //$NON-NLS-1$
        }

        resultTest.setText(result);
        resultTest.pack();
    }

    @Override
    public void initModel(StationModel stationModel)
    {
        String ligne = null;
        Fragment frag;
        // Station station = stationModel.getModel();

        if (name.equals(Intern.S_STATIONNUMBER))
        {
            // Station number reste un cas particulier
            initReferenceModel(stationModel);
        }
        else if (name.equals(Intern.S_DATATYPE))
        {
            // DataType également un cas particulier
            initDataTypeModel(stationModel);
        }
        else
        {
            boolean position = false;
            // généralisation des autres sections
            for (int i = 0; i < nbLignes; i++)
            {

                // récupération du propertyModel correspondant
                // String tagXml = getTagXml(i);
                String propertyName = name;
                if (nbLignes > 1)
                    propertyName += (i + 1);
                PropertyModel propertyModel = new PropertyModel();
                propertyModel.setProperty(propertyName);
                // stationModel.getProperty(name);
                // getPropertyModel(stationModel, tagXml);
                // ExternalizableFormat format = propertyModel.getFormat();
                ExternalizableFormat format = null;

                // gestion des flags qualite
                String property = name;
                if (name.equals(Intern.S_TIME) || name.equals(Intern.S_DATE))
                    property = Intern.DATETIME;
                Fragment flag =
                    myForm.qcFlags.fillFlagFragment(property);
                if (flag != null)
                    propertyModel.setFlag(flag);

                // gestion des converteurs
                try
                {
                    if (converter != null)
                    {
                        String formatSaisi = converter.getText();
                        switch (cType)
                        {
                            case FRQ:
                                propertyModel.setConversionFormula(formatSaisi);
                            break;
                            case TIME:
                            case DATE:
                                format = new DateTimeFormat();
                                ((DateTimeFormat) format)
                                    .setFormatString(formatSaisi);
                            break;
                            case LAT:
                                format = new LatitudeFormat();
                                ((LatitudeFormat) format)
                                    .setFormatString(formatSaisi);
                            break;
                            case LON:
                                format = new LongitudeFormat();
                                ((LongitudeFormat) format)
                                    .setFormatString(formatSaisi);
                            break;
                            default:
                            break;
                        }
                    }
                }
                catch (ParseException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // gestion des valeurs saisies
                if (isManual())
                {
                    // saisie manuelle
                    propertyModel.fillUserValue(getValue(i));
                    // fillManualModel(propertyModel, station, tagXml, format,
                    // i);

                }
                else
                {
                    // position dans fichier
                    boolean fillVariPos = (autoInput.getButtonVari() != null);
                    // propertyModel.setInData(inData)

                    if (name.equals(Intern.S_COMMENT))
                    {
                        // cas particuliers : COMMENT
                        // surlignement et injection ligne debut fin
                        frag =
                            propertyModel.fillFragment(this, ligne, i, false,
                                fillVariPos);
                        int entete =
                            Nemo.getInstance().getDomain().getMedia()
                                .getModel().getFileHeader();
                        Utils.calculNbreLignesPuisRemplissageFrag(frag, entete);
                    }
                    else
                    {
                        // Généralisation
                        if ((!position)
                                && (((i > 0) && (getLineText(i).trim()
                                    .equals(Global.EMPTY_STRING)))
                                        || getValue() == null || getValue()
                                    .trim().equals(Global.EMPTY_STRING)))
                        {
                            propertyModel.fillUserValue(getValue(i));
                            // fillManualModel(propertyModel, station, tagXml,
                            // format, i);
                        }
                        else if (!getAutoInput().getLines().get(i).isEmpty())
                        {
                            propertyModel.setInData(getAutoInput().getLines()
                                .get(i).isInData());
                            try
                            {
                                propertyModel.fillFragment(this, ligne, i,
                                    true, fillVariPos, getAutoInput()
                                        .isDefaultValueEnable(), getAutoInput()
                                        .isDefaultValueSelected(),
                                    getAutoInput().getDefaultValue());
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            if (fillVariPos)
                            {
                                if (format instanceof LatitudeFormat)
                                {
                                    ((LatitudeFormat) format)
                                        .setVariableLength(isVariablePos());
                                }
                                else if (format instanceof LongitudeFormat)
                                {
                                    ((LongitudeFormat) format)
                                        .setVariableLength(isVariablePos());
                                }
                            }
                            position = true;

                            // test la ligne suivante
                            if (getAutoInput().nbLineFilled() == 1)
                            {
                                propertyModel.getFragment().setSingleLine(true);
                            }
                        }
                        else if ((i > 0) && position)
                        {
                            propertyModel.setUserProvided(false);
                        }
                    }
                }
                if (format != null)
                {
                    propertyModel.setFormat(format);
                }
                stationModel.addProperty(propertyModel);
            }
        }
    }

    /*
     * private void fillManualModel(PropertyModel propertyModel, Station
     * station, String tagXml, ExternalizableFormat format, int i) {
     * propertyModel.setUserProvided(true); String valueString =
     * getManualValue(i); if (isTimeSeriesInfo())
     * station.getTimeSeriesInformation().set(tagXml, valueString, format); else
     * station.set(tagXml, valueString, format); }
     */
    private void initReferenceModel(StationModel stationModel)
    {
        String ligne = null;
        PropertyModel propertyModel = new PropertyModel();
        // PropertyModel propertyModel = stationModel.getReferenceProperty();
        if (myForm.stationNumber.isManual())
        {
            propertyModel.fillUserValue(myForm.stationNumber
                .getManualValue());

            /*
             * String stationNumber =
             * ViewMain.editStationForm.stationNumber.getManualValue();
             * stationModel.setIncrementRef(true); if
             * (ViewMain.radTrajectoire.getSelection())
             * stationModel.getTrajectory().setStationNumber(stationNumber);
             * else stationModel.setIncrementNumber(stationNumber);
             * propertyModel.setUserProvided(true);
             */
        }
        else
        {
            // stationModel.setIncrementRef(false);
            int ligneSaisie =
                Integer.valueOf(myForm.stationNumber
                    .getLineText(0));
            if (ligneSaisie == 0)
            {
                ligne = Global.ZERO;
            }
            else
            {
                ligne =
                    String
                        .valueOf(ligneSaisie
                                - Integer
                                    .valueOf(Nemo.getInstance().getViewMain().getEditFileForm().getSectionFileHeader()
                                        .getValue()));
            }
            propertyModel.fillFragment(myForm.stationNumber,
                ligne);
        }
        stationModel.setReferenceProperty(propertyModel);
    }

    private void initDataTypeModel(StationModel stationModel)
    {
            if (!Nemo.getInstance().getViewMain().getMyRulesManager().isProcessCollection()) // FAE 022
        {
            String typeCombo = datatype.getText();
            DataType newDataType = new DataType();
            String code = Global.EMPTY_STRING;
            // si on a ouvert un modele, on a le code direct !

            // --- nom du model
            String modelName = Nemo.getInstance().getNomModele();

            if (!Global.EMPTY_STRING.equals(modelName))
            {
                code = typeCombo;
            }
            else
            {
                Pattern p = Pattern.compile(Global.SEP);
                String mots[] = p.split(typeCombo);

                int taille = mots.length;
                if (taille >= 1)
                {
                    code = mots[0];
                }
            }
            // creation d'un nouveau Datatype avec les infos recuperees au
            // dessus.
            newDataType.setCode(Utils.extraitCode(code));
            for (int i = 0; i < TableDataSurvey.types.size(); i++)
            {
                if (typeCombo.contentEquals(TableDataSurvey.types.get(i)
                    .getCode()))
                {
                    // c'est le bon dataType
                    // on recupere le nombre de mesures et le QC
                    newDataType.setNumberOfProfiles(TableDataSurvey.types
                        .get(i).getNombre());
                    newDataType.setQc(TableDataSurvey.types.get(i).getQc());
                    newDataType.setSensor(TableDataSurvey.types.get(i)
                        .getSensor());
                }
            }
            stationModel.setDataType(newDataType);
        }
    }

    @Override
    public void fillFromModel(StationModel stationModel)
    {
        // Station station = stationModel.getModel();
        if (name.equals(Intern.S_STATIONNUMBER))
        {
            fillReference(stationModel.getReferenceProperty());
        }
        else if (name.equals(Intern.S_DATATYPE))
        {
            fillDataType(stationModel);
        }
        else
        {
            // généralisation des autres sections
            for (int i = 0; i < nbLignes; i++)
            {

                // récupération du propertyModel correspondant
                // String tagXml = getTagXml(i);
                String propertyName = name;

                if (nbLignes > 1)
                    propertyName += (i + 1);

                PropertyModel propertyModel =
                    stationModel.getProperty(propertyName);
                // getPropertyModel(stationModel, tagXml);
                ExternalizableFormat format = propertyModel.getFormat();

                // gestion des flags qualite
                String propertyCode = name;
                if (name.equals(Intern.S_TIME) || name.equals(Intern.S_DATE))
                    propertyCode = Intern.DATETIME;
                myForm.qcFlags.setFlagFragment(propertyModel
                    .getFlag(), propertyCode, Messages.getString(propertyCode)
                    .trim());

                // gestion des converters
                if (converter != null)
                {
                    String formule = Global.EMPTY_STRING;
                    switch (cType)
                    {
                        case FRQ:
                            formule = propertyModel.getConversionFormula();
                            if (formule.equals(Global.EMPTY_STRING))
                                formule = "*1"; //$NON-NLS-1$ 
                        break;
                        case TIME:
                        case DATE:
                            // if (inputType != E_InputType.PROFILE &&
                            // Nemo.getInstance().getDomain().getCruise().getStation().isComputed())
                            // formule =
                            // ((DateTimeFormat)propertyModel.getFormat()).getFormatString();
                            // else
                            formule =
                                ((DateTimeFormat) format).getFormatString();
                        break;
                        case LAT:
                            formule =
                                ((LatitudeFormat) format).getFormatString();
                        break;
                        case LON:
                            formule =
                                ((LongitudeFormat) format).getFormatString();
                        break;
                        default:
                        break;
                    }
                    converter.setText(formule);
                }
                /*
                 * if ((name.equals(Intern.S_TIME) ||
                 * name.equals(Intern.S_DATE)) && inputType !=
                 * E_InputType.PROFILE &&
                 * !Nemo.getInstance().getDomain().getCruise().getStation().isComputed()){ //
                 * cas des données temporelles dans les données LineFragment
                 * lineTime = propertyModel.getFragment();
                 * setAutoValueInput(Global.EMPTY_STRING, String
                 * .valueOf(lineTime.getFirstCharOffset()), String
                 * .valueOf(lineTime.getLastCharOffset()), i); } else {
                 */
                // gestion des valeurs saisies
                if (propertyModel.isUserProvided())
                {
                    /*
                     * String value = Global.EMPTY_STRING; if
                     * (isTimeSeriesInfo()) value =
                     * station.getTimeSeriesInformation().get(tagXml, format);
                     * else value = station.get(tagXml, format);
                     */
                    String value = propertyModel.getUserValue();
                    if (value.equals("NaN"))value = Global.EMPTY_STRING; //$NON-NLS-1$
                    setManualValueInput(value, i);
                }
                else
                {
                    // position dans fichier
                    boolean fillVariPos = (autoInput.getButtonVari() != null);
                    boolean offset = false;
                    if (name.equals(Intern.S_COMMENT))
                        offset = true;

                    if ((i > 0) && propertyModel.isUserProvided())
                    {
                        // on a rentre que la ligne 1 donc les valeurs de
                        // ligne 2 sont vides
                        setAutoValueInput(Global.EMPTY_STRING,
                            Global.EMPTY_STRING, Global.EMPTY_STRING, i);
                    }
                    else
                    {
                        if (autoInput.getLines().get(i).getCursor() != null)
                            Utils.calculPosxPosy(propertyModel.getFragment(),
                                getCursor(i));
                        setAutoValueInput(propertyModel, null, fillVariPos,
                            offset, i);

                        // FAE 3145 problème de l'acquisition history espérons
                        // que ces lignes ne servaient à rien...

                        // if (propertyModel.getFragment().isSingleLine())
                        // break;
                    }
                }

            }

        }

    }

    private void fillReference(PropertyModel refProperty)
    {
        // SECTION numero de station
        // PropertyModel propertyModel = stationModel.getReferenceProperty();
        if (refProperty.isUserProvided())
        {
            Nemo.getInstance().getViewMain().getEditStationForm().stationNumber
                .setManualValueInput(refProperty.getUserValue());
        }
        else
        {
            Fragment frag = refProperty.getFragment();
            Integer realLigne = null;
            int ligne = frag.getFirstLine();
            if (ligne == 0)
                realLigne = new Integer(0);
            Nemo.getInstance().getViewMain().getEditStationForm().stationNumber.setAutoValueInput(
                refProperty, realLigne);
        }

        /*
         * if (stationModel.isIncrementRef()){ String value = null; if
         * (stationModel.getInputType().equals(Global.TRAJECTORY)) { // FAE 1386 :
         * saisie manuelle, non incrementable pour // Trajectoires value =
         * stationModel.getTrajectory().getStationNumber(); } else { value =
         * String.valueOf(stationModel.getIncrementNumber()); }
         * ViewMain.editStationForm.stationNumber.setManualValueInput(value); }
         * else { // valeurs de ligne/debut/fin PropertyModel propertyModel =
         * stationModel.getReferenceProperty(); Fragment frag =
         * propertyModel.getFragment(); Integer realLigne = null; int ligne =
         * frag.getFirstLine(); if (ligne == 0) realLigne = new Integer(0);
         * ViewMain.editStationForm.stationNumber.setAutoValueInput(
         * propertyModel, realLigne); }
         */
    }

    private void fillDataType(StationModel station)
    {

        // SECTION type de donnee
        if (!Nemo.getInstance().getViewMain().getMyRulesManager().isProcessCollection()) // FAE022
        {
            DataType dataType = station.getDataType();
            if (dataType != null)
            {
                String code = dataType.getCode();
                // faire un split sur - au cas oe et n'avoir QUE le code

                Pattern p = Pattern.compile(Global.SEP);
                String mots[] = p.split(code);
                String vraiCode = mots[0];

                // avant de charger le type de donnees, verifier qu'il existe
                // bien
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
                        Utils.selectComboCode(myForm.dataType
                            .getDatatype().getValueCombo(), code);
                    }
                }
            }
        }
    }

    private String getTagXml(int i)
    {
        String tagXml = name;
        if (nbLignes > 1)
            tagXml += String.valueOf(i + 1);
        return tagXml;
    }

    /*
     * private PropertyModel getPropertyModel(StationModel stationModel, String
     * tagXml) { PropertyModel propertyModel; if (isTimeSeriesInfo())
     * propertyModel = stationModel.getTimeSeriesModel().propertiesAt(tagXml);
     * else propertyModel = stationModel.propertiesAt(tagXml); return
     * propertyModel; }
     */

    public void diseableLineInData()
    {
        if (autoInput != null && autoInput.isSelected())
        {
            for (AutoLine line : autoInput.getLines())
            {
                line.getNumberLine().setEnabled(!line.isInData());
            }
            autoInput.disableEndVari();
        }
    }

    // public void refreshDataType()
    // {
    // datatype.refreshCombo(ComboType.DATATYPE);
    // }

}
