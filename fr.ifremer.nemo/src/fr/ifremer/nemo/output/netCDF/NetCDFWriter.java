/* ============================================================================
 * Nom du fichier	: NetCDFWriter.java
 * Auteur			: dcapitai
 * Date				: 15 oct. 2012
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.output.netCDF;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.NemoWriter;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.sdnNetCDF.SdnDimensionManager;
import fr.ifremer.sdnNetCDF.SdnGlobalAttribute;
import fr.ifremer.sdnNetCDF.SdnNetCDFManager;
import fr.ifremer.sdnNetCDF.util.ParameterNames;
import fr.ifremer.sdnNetCDF.util.ParameterValues;
import fr.ifremer.sdnNetCDF.variable.AncillaryVariable;
import fr.ifremer.sdnNetCDF.variable.CoOrdinateVariable;
import fr.ifremer.sdnNetCDF.variable.GeophysicalVariable;
import fr.ifremer.sdnNetCDF.variable.ISdnVariable;
import fr.ifremer.sdnNetCDF.variable.SdnBotDepthVariable;
import fr.ifremer.sdnNetCDF.variable.SdnCruiseVariable;
import fr.ifremer.sdnNetCDF.variable.SdnEdmoCodeVariable;
import fr.ifremer.sdnNetCDF.variable.SdnLocalCdiIdVariable;
import fr.ifremer.sdnNetCDF.variable.SdnReferenceVariable;
import fr.ifremer.sdnNetCDF.variable.SdnStationVariable;
import fr.ifremer.sdnNetCDF.variable.SdnT;
import fr.ifremer.sdnNetCDF.variable.SdnX;
import fr.ifremer.sdnNetCDF.variable.SdnY;
import fr.ifremer.sdnNetCDF.variable.SdnZ;

/**
 * Description: TODO
 * @author dcapitai
 *
 */

public class NetCDFWriter extends NemoWriter
{
    private enum URNtype
    {
        subject,
        object,
        units;
    }
    int indexRecord;
    SdnNetCDFManager netCDFWriter;
    SdnDimensionManager  dims;
    Map<String,Object[][]> stationData;
    Map<String,ISdnVariable> variables;
    int nbStations;
    
    public NetCDFWriter()
    {
        super();
        myConversion = Conversion.NETCDF;
        variables = new HashMap<String,ISdnVariable>();
        stationData = new HashMap<String,Object[][]>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected String getOutputFileName(String inputFilePath)
    {
        outputFileName = iDomain.getMedia().getOutputFileName();
        if (isProcessType_CollectionDir)
        {
            cruiseDomain.setDataSetRef(new File(inputFilePath).getName());
            outputFileName += File.separator + cruiseDomain.getDataSetRef();
        }
        return outputFileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeFile(String inputFilePath) throws Exception
    {
        try
        {
            stationDomain.updateRef();
            outputFileName = getOutputFileName(inputFilePath);
           

            if (isConversionTo_NetCDFMonostation)
            {
                // Conversion ODV - fichiers monostation => N fichiers de sortie
                Integer ref = 0;
                nbStations = 1;
                

                for (Station st : stationDomain.getStations(inputFilePath))
                {

                    if (isProcessType_CollectionDir)
                    {
                        new File(outputFileName).mkdir();
                    }

                    if (isInputType_Trajectory)
                    {
                        ref++;
                        st.setReference(ref.toString());
                    }

                    // FAE 6143
                    String localCdiId =
                        stationDomain.getAndCheckLocalCDI(cruiseDomain,
                            Conversion.ODV, st.getReference());

                    String mOutputFileName =
                        outputFileName + File.separator + localCdiId
                                + Conversion.NETCDF.getExt();

                    netCDFWriter = new SdnNetCDFManager(mOutputFileName);
                    variables = new HashMap<String,ISdnVariable>();
                    stationData = new HashMap<String,Object[][]>();
                    // écriture de l'entête campagne
                    setNetCDFHeader(inputFilePath, st);

                    if (isProcessType_CruiseFile)
                    {
                        iDomain.getStation().updateRef();
                    }
                    writeStation(st, inputFilePath, 0);
                    
                }
            }
            else
            {
                // conversion ODV multistations => 1 fichier de sortie
                nbStations = stationDomain.getStations(inputFilePath).size();
                if (isProcessType_CollectionDir)
                {
                    outputFileName += Conversion.NETCDF.getExt();
                    iDomain.getStation().updateRef();
                }
                // outputFileWriter = new PrintWriter(new BufferedWriter(new
                // FileWriter(outputFileName)));
                
                netCDFWriter = new SdnNetCDFManager(outputFileName);
                variables = new HashMap<String,ISdnVariable>();
                stationData = new HashMap<String,Object[][]>();
                // écriture de l'entête campagne
                setNetCDFHeader(inputFilePath, null);
                int indiceStation=0;
                for (Station st : stationDomain.getStations(inputFilePath))
                {
                    
                    // FAE 6087 reinitialisation de la reference à lecture le
                    // fichier entree (car pour ecrire une station on lit tout
                    // le fichier)
                    // 07/12/2010: 6087 ajout du if CRUISE_FILE, sinon erreur
                    // dans le cas cruise dir
                    if (isProcessType_CruiseFile)
                    {
                        iDomain.getStation().updateRef();
                    }
                    writeStation(st, inputFilePath, indiceStation);
                    indiceStation++;
                }

                

            }
        }
        catch (Exception e)
        {
            throw new GenerationException(e);
        }

    }
    
    /**
     * @param st
     * @param inputFilePath
     * @param outputFileName
     * @throws ParseException 
     * @throws FileNotFoundException 
     */
    private void writeStation(Station currentStation, String inputFilePath, int indiceStation) throws Exception
    {
        StationDomain station = iDomain.getStation();

        bufferedReader = getBufferReader(inputFilePath, currentStation);
        boolean stationWrited = false;
        indexRecord = 0;
        stationDatas.clear();
        StringBuffer stationHeader = station.getOdvEmptyStationHeader();
        stationData = new HashMap<String,Object[][]>();

        
        // scan the InputFile line by line
        while ((readedLine = bufferedReader.readLine()) != null)
        {
            incrementLine();
            if (inFileHeader())
            {
                continue;
            }
            if (isStartStationHeader())
            {
                String[] inputStationHeader =
                    consumeStationHeader(mediaModel.getStationHeader() > 0);

                String stRef = null;
                // si inputStationHeader est null, c'est qu'on a terminé la
                // derniere station
                if (inputStationHeader != null)
                {
                    if (isInputType_Trajectory)
                        stRef = currentStation.getReference();
                    else
                        stRef =
                            iDomain.getStation().getReference(
                                currentStation.getFileName(),
                                inputStationHeader, true);
                }

                if (inputStationHeader != null
                        && currentStation.getReference().equals(stRef))
                {

                    stationHeader =
                        iDomain.getStation().getHeader(myConversion,
                            currentStation, inputStationHeader);
                    
                    // FAE 4673 Pb si une seule ligne données et pas d'entête
                    // station
                    if (!isEndData() || (mediaModel.getEndData() == E_EndData.NEXT_START_WITH && mediaModel.getStationHeader() == 0))
                    {
                      //TODO CPT données déja lu
                        //Longitude
                        setDataValue(indiceStation, String.valueOf(currentStation.getLongitude()) , ParameterNames.LONGITUDE, currentStation.getMetaFlag(Intern.S_ELON) );
                        //Latitude
                        setDataValue(indiceStation, String.valueOf(currentStation.getLatitude()) , ParameterNames.LATITUDE, currentStation.getMetaFlag(Intern.S_ELAT));
                        //TIME
                        Double julianDay = DateTimeUtils.getJulianDay(DateTimeUtils.getCurrentDate(currentStation, indiceStation));
                        setDataValue(indiceStation, julianDay.toString() , ParameterNames.TIME, currentStation.getMetaFlag(Intern.S_ETIME));
                        //Local CDI ID
                        String localCdiId = stationDomain.getLocalCdiId(cruiseDomain, Conversion.ODV, currentStation.getReference());
                        setDataValue(indiceStation, localCdiId , ParameterNames.SDN_LOCAL_CDI_ID, currentStation.getMetaFlag(Intern.S_ELON));
                        //SDN CRUISE
                        setDataValue(indiceStation, cruiseDomain.getModel().getName().trim() , ParameterNames.SDN_CRUISE, "0");
                        //SDN Station
                        setDataValue(indiceStation, currentStation.getReference().trim() , ParameterNames.SDN_STATION, "0");
                        //SDN EDMO_CODE
                        setDataValue(indiceStation, Nemo.getInstance().getConfig().getIdEdmoRespCdi().trim() , ParameterNames.SDN_EDMO_CODE, "0");
                        
                        //SDN_BOT_DEPTH
                        if(station.getModel().getProperty(Intern.S_DEPTH).isInData())
                        {
                            Object botDepth = stationDomain.getModel().getProperty(Intern.S_DEPTH).applyOn(inputStationHeader);
                            setDataValue(indiceStation, botDepth.toString(), ParameterNames.SDN_BOT_DEPTH, "1");
                        }
                        else
                            setDataValue(indiceStation, "-999", ParameterNames.SDN_BOT_DEPTH, "1");
                        
                        //SDN_REFERENCE
                        if(!cruiseDomain.getDataSetRef().isEmpty())
                            setDataValue(indiceStation, cruiseDomain.getDataSetRef() , ParameterNames.SDN_REFERENCE, "0");
                        
                        else
                            setDataValue(indiceStation, null , ParameterNames.SDN_REFERENCE, "0");
                        
                        writeDataLine(currentStation, readedLine, indiceStation);
                        stationHeader = station.getOdvEmptyStationHeader();
                    }
                    stationWrited = true;
                }
                else if (stationWrited || inputStationHeader == null)
                {
                    break;
                }
            }
            else if (stationWrited)
            {
                // ligne de données : à ne prendre en compte qua si on est sur
                // la bonne station
                writeDataLine(currentStation, readedLine, indiceStation);
                stationHeader = station.getOdvEmptyStationHeader();
            }
            
        }

        if (stationWrited)
        {
            // enregistrement de la station
            iDomain.getMedia().addStationInOutput(outputFileName,
                currentStation.getReference());
            
            writeDataTonetCDFFile();
            
        }
        
        System.out.println(netCDFWriter);
        numLine = 0;
    }
    
    public void setDataValue(int indiceStation, String stringValue, String variableName, String qcValue){
        ISdnVariable var =  variables.get(variableName);
        Object[][] paramData = stationData.get(variableName);
        String ancillaryName=null;
        if (paramData == null)
        {
            if (var.getDims().size()>1)
                paramData = new Object[nbStations][var.getDims().get(1).getLength()];
            else
                paramData = new Object[nbStations][1];
        }
        
        if(var != null)
        {
            paramData[indiceStation][indexRecord] = convertValue(var.getDataType(), stringValue) ;
            
            if(var instanceof CoOrdinateVariable)
                ancillaryName = ((CoOrdinateVariable)var).getAncillaryVariables();
            if(var instanceof GeophysicalVariable)
                ancillaryName = ((GeophysicalVariable)var).getAncillaryVariables();
            
        }
        stationData.put(variableName, paramData);
        
        if(ancillaryName != null)
            addAncillaryData(ancillaryName, qcValue , indiceStation);
    }
    

    private void writeDataLine(Station currentStation, String inputLine, int indiceStation) throws Exception
    {
        long currentDate =
            DateTimeUtils.getCurrentDate(currentStation, indexRecord);
        // FAE 11356
        
        if (mediaDomain.getModel().getInputType() == E_InputType.TIMESERIES
                && Nemo.getInstance().getDomain().getStation().getModel()
                    .getProperty(Intern.S_DATE).isInData())
            currentDate =
                Long.valueOf(DateTimeUtils.convertTimestamp(inputLine));
        
        Object[][] paramData;
        String varName;
        //parametre ajouté
        for (ParameterModel pModel : dataDomain.getMyParameterManager().getNetCDFParameterModels())
        {
            varName = pModel.getParameter().getLocalCode();

            pModel.computeAddedParameterValue(currentStation, inputLine,
                currentDate);

            String defaultValue = pModel.getParameter().getDefault().trim();
            String stringValue = pModel.getLineValue().getValue().trim();
            
            if(!stringValue.equals(defaultValue))
                setDataValue(indiceStation, stringValue, varName, pModel.getLineValue().getQc());
            else
                setDataValue(indiceStation, null, varName, pModel.getLineValue().getQc());
            
           
            

        }

        // paramètres mesurés
        for (ParameterModel pModel : dataDomain.getModel().getParameterModels())
        {
            
            varName = pModel.getParameter().getLocalCode();
            paramData = stationData.get(varName);
            //if paramData == null => no data for Parameter
            if (paramData == null)
                paramData =new Object[nbStations][dims.getDimMax().getLength()];
            
            pModel.computeParameterValue(inputLine);

            String defaultValue = pModel.getParameter().getDefault();
            // 11976
            String stringValue = "";
            if (!pModel.getLineValue().getValue().equals(""))
            {

                stringValue =
                    Formatter.getSingleton().formatParameter(
                        pModel.getParameter().getFormat(),
                        pModel.getLineValue().getValue());
            }

            String ancillaryName= null;
            
            
            if(!stringValue.equals(defaultValue))
                setDataValue(indiceStation, stringValue, varName, pModel.getLineValue().getQc());
            else
                setDataValue(indiceStation, null, varName, pModel.getLineValue().getQc());
                    
     
            
        }
        
        
        indexRecord++;
    }
    
    public void writeDataTonetCDFFile() throws Exception{
        
        for( Entry<String,Object[][]> currentStationData : stationData.entrySet())
        {
            netCDFWriter.writeDataToVariable( variables.get(currentStationData.getKey()).getVarName() , currentStationData.getValue());
        }
        
        
        
    }

    private void addAncillaryData(String ancillaryName, String qc, int indiceStation ){
        Object[][] paramData;
        
            paramData = stationData.get(ancillaryName);
            AncillaryVariable currentAncillary = (AncillaryVariable) variables.get(ancillaryName);
            //if paramData == null => no data for Parameter
            if (paramData == null)
            {
                if (currentAncillary.getDims().size()>1)
                    paramData = new Object[nbStations][dims.getDimMax().getLength()];
                else
                    paramData = new Object[nbStations][1];
            }
            
            
            if(currentAncillary != null)
            {
                if(qc != null)
                    paramData[indiceStation][indexRecord] = convertValue(currentAncillary.getDataType(), qc);
                else
                    paramData[indiceStation][indexRecord] = currentAncillary.getFillvalue();
                   
                stationData.put(ancillaryName, paramData);
            }
        
    }

    public void  setNetCDFHeader(String inputFilePath, Station st) throws Exception{

        //create Dimension
        setSdnDimension(inputFilePath, st);
        
        List<Dimension> instanceDim = new ArrayList<Dimension>();
        instanceDim.add(dims.getDimInstance());
   
        List<Dimension> geoDim = new ArrayList<Dimension>();
        geoDim.add(dims.getDimInstance());
        
        if (mediaModel.getInputType() == E_InputType.PROFILE)
            geoDim.add(dims.getDimMaxZ());
        else
            geoDim.add(dims.getDimMaxT());
        
        List<Dimension> sdnDim = new ArrayList<Dimension>();
        sdnDim.add(dims.getDimInstance());
        sdnDim.add(dims.getString80());

        
        //add to net CDF
        netCDFWriter.addDimensions(dims);
        

        //create Global Attributes
        //TODO CPT ajouter Titre au global Attribute
        SdnGlobalAttribute globattr = new SdnGlobalAttribute( getFeatureType(), "a définir", new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ").format(new Date()));
        netCDFWriter.addGlobalAttributes(globattr);
        SdnX longitude=null;
        SdnY latitude=null;
        SdnT time=null;
        SdnZ z=null;
        //z Variable
        ParameterModel zParameterModel = dataDomain.getModel().getParameterModelByP011(getZLocalName());
        
        String varName = getZVarName();
        String standardName = getZStandardName();
        String longName = zParameterModel.getParameter().getFullName(); //entryTerm P091 ou P21
        String unit="à definir"; //TODO CPT ajouter l'unité pour Z
        String sdnParameterUrn  = getURNSyntaxe(URNtype.object, zParameterModel.getParameter()
                .getCodeP011(), null, Intern.mapping_objet, zParameterModel.isText()); 
        String sdnParameterName = zParameterModel.getParameter().getNameP011(); 
        
        String sdnUomUrn ;
        if (zParameterModel.getParameter().getUnitCode() == null
                || zParameterModel.getParameter().getUnitCode().isEmpty())
        {
            sdnUomUrn= getURNSyntaxe(URNtype.units, zParameterModel.getParameter().getCode(),
                Intern.mapping_subject, Intern.mapping_unit, zParameterModel.isText());
        }
        else
        {
            sdnUomUrn= getURNSyntaxe(URNtype.units, zParameterModel.getParameter()
                .getUnitCode(), null, Intern.mapping_unit, zParameterModel.isText());
        }
        String sdnUomName = zParameterModel.getParameter().getUnit();
        
        //create Co-ordinate Variable
        if (mediaModel.getInputType() == E_InputType.TRAJECTORY)
        {
          
            longitude = new SdnX(geoDim);
            latitude = new SdnY(geoDim);
            time = new SdnT(geoDim);
            //  TODO CPT GERE LE CAS DE Z
            z= new SdnZ(varName, geoDim, longName, standardName, unit, sdnParameterUrn, sdnParameterName, sdnUomUrn, sdnUomName);
        }
        else if (mediaModel.getInputType() == E_InputType.PROFILE)
        {
            longitude = new SdnX(instanceDim);
            latitude = new SdnY(instanceDim);
            time = new SdnT(instanceDim);
            //  TODO CPT GERE LE CAS DE Z
            z= new SdnZ(varName, geoDim, longName, standardName, unit, sdnParameterUrn, sdnParameterName, sdnUomUrn, sdnUomName);
        }
        else
        {
            longitude = new SdnX(instanceDim);
            latitude = new SdnY(instanceDim);
            time = new SdnT(geoDim);
            //  TODO CPT GERE LE CAS DE Z
            z= new SdnZ(varName, instanceDim, longName, standardName, unit, sdnParameterUrn, sdnParameterName, sdnUomUrn, sdnUomName);

        }

     // add default value if existe
        if(!zParameterModel.getInputDefault().isEmpty())
            z.addVariableAttAttribute(ParameterNames.FILLVALUE, Double.valueOf(zParameterModel.getInputDefault()));
        variables.put(longitude.getVarName(), longitude);
        variables.put(latitude.getVarName(), latitude);
        variables.put(time.getVarName(), time);
        variables.put(zParameterModel.getParameter().getLocalCode(), z);

        //Sdn variable
        SdnEdmoCodeVariable sdnEdmoCode = new SdnEdmoCodeVariable(instanceDim); 
        variables.put(sdnEdmoCode.getVarName(), sdnEdmoCode);
        
        SdnBotDepthVariable SdnBotDepth = new SdnBotDepthVariable(instanceDim);
        variables.put(SdnBotDepth.getVarName(), SdnBotDepth);
        
        SdnCruiseVariable sdnCruise = new SdnCruiseVariable(sdnDim,"à definir" ); // value cruiseDomain.getModel().getName()
        variables.put(sdnCruise.getVarName(), sdnCruise);
        SdnStationVariable sdnStation = new SdnStationVariable(sdnDim,"à definir");
        variables.put(sdnStation.getVarName(), sdnStation);
        SdnLocalCdiIdVariable SdnLocalCdi = new SdnLocalCdiIdVariable(sdnDim, getFeatureType()+"_id");
        variables.put(SdnLocalCdi.getVarName(), SdnLocalCdi);
        
        if(!cruiseDomain.getDataSetRef().isEmpty())
        {
            SdnReferenceVariable srnRef = new SdnReferenceVariable(sdnDim);
            variables.put(srnRef.getVarName(), srnRef);
        }
        
        
        //Geophycal variable
        ParameterModel[] paramAdded = dataDomain.getMyParameterManager().getNetCDFParameterModels();
        ParameterModel[] paramMesured = dataDomain.getModel().getParameterModels();
        // concaténation 
        ParameterModel[] params = new  ParameterModel[paramAdded.length + paramMesured.length] ;
        System.arraycopy(paramAdded, 0, params, 0, paramAdded.length);
        System.arraycopy(paramMesured, 0, params, paramAdded.length, paramMesured.length);
        
        
        String coordinates = longitude.getVarName() + " " + latitude.getVarName() + " " + time.getVarName() + " " + z.getVarName();
        for(ParameterModel param : params)
        {
            varName = param.getParameter().getLocalCode();
            longName = param.getParameter().getFullName(); //entryTerm P091 ou P21
            unit="à definir"; //TODO CPT ajouter l'unité pour Les variable geophysical
            
            sdnParameterUrn  = getURNSyntaxe(URNtype.object, param.getParameter()
                    .getCodeP011(), null, Intern.mapping_objet, param.isText()); 
            sdnParameterName = param.getParameter().getNameP011();             
            if (param.getParameter().getUnitCode() == null || param.getParameter().getUnitCode().isEmpty())
            {
                sdnUomUrn= getURNSyntaxe(URNtype.units, param.getParameter().getCode(),
                    Intern.mapping_subject, Intern.mapping_unit, param.isText());
            }
            else
            {
                sdnUomUrn= getURNSyntaxe(URNtype.units, param.getParameter()
                    .getUnitCode(), null, Intern.mapping_unit, param.isText());
            }
            sdnUomName = param.getParameter().getUnit();
            
            
            
            if(!sdnParameterUrn.equals(z.getSdnParameterUrn())){
                GeophysicalVariable current = new GeophysicalVariable(varName, getUnits(param.getParameter().getFormat()), geoDim, longName, coordinates, unit, sdnParameterUrn, sdnParameterName, sdnUomUrn, sdnUomName);

                // add default value if existe
                if(!param.getInputDefault().isEmpty())
                    current.addVariableAttAttribute(ParameterNames.FILLVALUE, convertValue(current.getDataType(), param.getInputDefault()));
                
                variables.put(current.getVarName(), current);

            }
        }
        Collection<ISdnVariable> sdnVar = variables.values();
        Map<String,ISdnVariable> AncillaryVariables = new HashMap<String,ISdnVariable>();;
        for(ISdnVariable current : variables.values())
        {
            if(current.isSDNCompliant()){
                netCDFWriter.addSdnVariable(current);
                AncillaryVariable currentAncillary = netCDFWriter.getAncillaryVariableFromSdnVariable(current);
                if(currentAncillary != null)
                    AncillaryVariables.put(currentAncillary.getVarName(), currentAncillary);
            }
            else
                throw new Exception("Variable "+ current.getVarName() +" is not SDN Compliant");
        }
        variables.putAll(AncillaryVariables);
        
    }

    private String getFeatureType(){
        return ParameterNames.getString(mediaModel.getInputType().toString());
    }
    
    private DataType getUnits(String format){
        int size = format.length();
        
        if (size>0){
         char type = format.charAt(size-1);
           if(type == 'f' || type == 'F')
               return DataType.FLOAT;
           if(type == 'D' || type == 'd')
               return DataType.INT;
        }
           return DataType.STRING;
    }
    
    private String getZLocalName()
    {
        if(!dataDomain.getModel().getVerticalParametersList().getP_depthBSBed().isEmpty())
            return dataDomain.getModel().getVerticalParametersList().getP_depthBSBed();
        
        if(!dataDomain.getModel().getVerticalParametersList().getP_depthBSSurf().isEmpty())
            return dataDomain.getModel().getVerticalParametersList().getP_depthBSSurf();
        
        if(!dataDomain.getModel().getVerticalParametersList().getP_pressure().isEmpty())
            return dataDomain.getModel().getVerticalParametersList().getP_pressure();
        
        return null;
    }
    
    private String getZVarName()
    {
        if(!dataDomain.getModel().getVerticalParametersList().getP_depthBSBed().isEmpty())
            return ParameterNames.DEPTH;
        
        if(!dataDomain.getModel().getVerticalParametersList().getP_depthBSSurf().isEmpty())
            return ParameterNames.DEPTH;
        
        if(!dataDomain.getModel().getVerticalParametersList().getP_pressure().isEmpty())
            return ParameterNames.PRESS;
        
        return null;
    }
    
    private String getZStandardName()
    {
        if (!dataDomain.getModel().getVerticalParametersList().getP_pressure().isEmpty() && mediaModel.getInputType() != E_InputType.TIMESERIES)
            return ParameterValues.Z_PRESSURE;
        
        if(!dataDomain.getModel().getVerticalParametersList().getP_depthBSSurf().isEmpty())
            return ParameterValues.Z_DEPTH;
        
        return null;
    }
    
    private void setSdnDimension(String inputFilePath, Station st){
        //create Dimension
        int instance = 1 ;
        int nbligne = 0;
        Integer maxZ = null;
        Integer maxT = null;
        
        if(isConversionTo_NetCDFMonostation)
        {
            nbligne = st.getNbRecords();
        }
        else{
            for (Station currentStation : stationDomain.getStations(inputFilePath))
            {
                instance = stationDomain.getStations(inputFilePath).size();
                if(nbligne< currentStation.getNbRecords())
                    nbligne = currentStation.getNbRecords();
            }
        }
        
        //int nbligne = stationDomain.getStations(inputFilePath).get(0).getNbRecords();
        
        if (mediaModel.getInputType() == E_InputType.PROFILE)
            maxZ = nbligne; //nombre de ligne dans la station
        else
            maxT = nbligne; //nombre de ligne dans la station

        dims = new SdnDimensionManager(instance,maxZ,maxT);
        
    }
    
    
    private static String getURNSyntaxe(URNtype type, String code,
            String list1, String list2, Boolean isText)
    {
        StringBuffer urnSyntaxe = new StringBuffer();

        switch (type)
        {
            case subject:
                urnSyntaxe.append(Global.SDN);
                urnSyntaxe.append(Global.DBL_PT);
                urnSyntaxe.append(Intern.file_mapping_subject);
                urnSyntaxe.append(Global.DBL_PT);
                urnSyntaxe.append(code);
            break;
            case object:
                urnSyntaxe.append(Global.SDN);
                urnSyntaxe.append(Global.DBL_PT);
                urnSyntaxe.append(list2);
                urnSyntaxe.append(Global.DBL_PT);
                urnSyntaxe.append(Global.DBL_PT);
                String pdvCodeobject;
                if (list1 != null)
                    pdvCodeobject = VocabLists
                            .getVocabMatch(list1, list2, code,
                                Intern.NemoLists_unknown);
                else
                    pdvCodeobject = code;
                urnSyntaxe.append(pdvCodeobject);
            break;
            case units:
                urnSyntaxe.append(Global.SDN);
                urnSyntaxe.append(Global.DBL_PT);
                urnSyntaxe.append(list2);
                urnSyntaxe.append(Global.DBL_PT);
                urnSyntaxe.append(Global.DBL_PT);
                String pdvCodeunits;
                if (list1 != null || isText)
                    pdvCodeunits = VocabLists
                            .getVocabMatch(list1, list2, code,
                                Intern.NemoLists_unknown);
                else
                    pdvCodeunits = code;
                // FAE 10755
                if (isText)
                    urnSyntaxe.append(Intern.NemoLists_unknown);
                else
                    urnSyntaxe.append(pdvCodeunits);
            break;
        }

        return urnSyntaxe.toString();
    }
    
    private static Object convertValue(DataType type, Object value){
        
        if(value!= null && value.toString().length()>0){
            if(type == DataType.FLOAT)
                return Float.valueOf(value.toString());
            if(type == DataType.DOUBLE)
                return Double.valueOf(value.toString());
            if(type == DataType.INT)
                return Integer.valueOf(value.toString());
            if(type == DataType.BYTE) 
                return value.toString().getBytes()[0] ;
            if(type == DataType.CHAR)
            {
                return value.toString();
            }
                
        }
        return null;
        
    }
   

}
