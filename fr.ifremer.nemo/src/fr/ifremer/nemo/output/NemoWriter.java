package fr.ifremer.nemo.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.DataDomain;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.MediaDomain;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.MediaModel;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Utils;

/**
 * 
 */
public abstract class NemoWriter
{

	protected HashMap<Double, String> stationDatas;

	/**
	 * Input File Reader
	 */
	protected BufferedReader bufferedReader = null;

	/**
	 * Ouput File Writer
	 */
	protected PrintWriter outputFileWriter = null;
	protected String outputFileName;

	/**
	 * current readed line
	 */
	protected String readedLine = Global.EMPTY_STRING;
	/**
	 * current number of line
	 */
	protected int numLine = 0;

	protected final InputDomain iDomain;
	protected MediaDomain mediaDomain;
	protected MediaModel mediaModel;
	protected StationDomain stationDomain;
	protected CruiseDomain cruiseDomain;
	protected DataDomain dataDomain;

	protected HashSet<String> stationsList;

	protected boolean isProcessType_CruiseFile = false;
	protected boolean isProcessType_CollectionDir = false;

	protected boolean isInputType_Trajectory;
	protected boolean isInputType_Profile;

	protected boolean isEndData_BeginLineChanges = false;
	protected boolean isEndData_NextStartWith = false;
	protected Conversion myConversion;
	protected boolean isConversionTo_ODVMonostation = false;
	protected boolean isConversionTo_ODVMultistations = false;
	protected boolean isRefIncremented = false;

	protected String beginningOfPreviousLine = Global.NO_VALUE;
	protected String previousReadedLine = Global.NO_VALUE;
	protected boolean isBeginningOfStation = false;

	protected NemoWriter()
	{
		stationsList = new HashSet<String>();
		iDomain = Nemo.getInstance().getDomain();
		mediaDomain = iDomain.getMedia();
		mediaModel = mediaDomain.getModel();
		stationDomain = iDomain.getStation();
		cruiseDomain = iDomain.getCruise();
		dataDomain = iDomain.getData();
		stationDatas = new HashMap<Double, String>();
		isRefIncremented = iDomain.getStation().getModel().getReferenceProperty().isUserProvided();
		isProcessType_CruiseFile = mediaDomain.getModel().getProcessType() == E_ProcessType.CRUISE_FILE;
		isProcessType_CollectionDir = mediaDomain.getModel().getProcessType() == E_ProcessType.COLLECTION_DIR;

		E_TypeConversion selectedConversion = iDomain.getConversion().getCurrent();
		isConversionTo_ODVMonostation = (selectedConversion == E_TypeConversion.ODV_MONOSTATION);
		isConversionTo_ODVMultistations = (selectedConversion == E_TypeConversion.ODV_MULTISTATIONS);

		E_InputType inputType = iDomain.getMedia().getModel().getInputType();
		isInputType_Trajectory = inputType == E_InputType.TRAJECTORY;
		isInputType_Profile = inputType == E_InputType.PROFILE;

		isEndData_BeginLineChanges = mediaModel.getEndData() == E_EndData.BEGIN_LINE_CHANGES;
		isEndData_NextStartWith = mediaModel.getEndData() == E_EndData.NEXT_START_WITH;

	}

	protected void incrementLine()
	{
		numLine++;
		Nemo.getInstance().setLog_currentInputLine(String.valueOf(numLine));
	}

	protected void setBeginningOfPreviousLine()
	{
		isBeginningOfStation = !beginningOfPreviousLine.equals(readedLine.substring(0, mediaModel.getEndDataPattern().length()));
		beginningOfPreviousLine = readedLine.substring(0, mediaModel.getEndDataPattern().length());
		previousReadedLine = readedLine;
	}

	/**
	 * @throws GenerationException
	 */
	public void write(ProgressNotification a_Progress) throws GenerationException
	{

		stationDomain.updateRef();

		try
		{

			int readerSize = 0;
			// pour chaque repertoire d'entree
			for (String inputFilePath : mediaDomain.getInputFilesNames().keySet())
			{

				// FAE 6087
				stationDomain.updateRef();

				// pour chaque fichier de ce repertoire d'entree
				for (String inputFileName : mediaDomain.getInputFilesNames().get(inputFilePath))
				{
					Nemo.getInstance().setLog_currentInputFile(inputFilePath + File.separator + inputFileName);

					File inputFile = new File(inputFilePath + File.separator + inputFileName);

					bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), Global.NEMO_ENCODING));
					readerSize = Long.valueOf(inputFile.length()).intValue();
					bufferedReader.mark(readerSize + 1);

					if (!stationDomain.stationIsRegistered(inputFilePath, inputFileName))
					{
						// première lecture pour obtenir le nombre de valeurs
						// par stations
						Station st = null;
						int nbrecords = 0;

						while ((readedLine = bufferedReader.readLine()) != null)
						{
							incrementLine();

							if (inFileHeader())
							{
								continue;
							}
							if (isEndData() && (st != null))
							{

								st.setNbRecords(nbrecords);
								stationDomain.addStation(inputFilePath, st);
								st = null;
							}

							if (isStartStationHeader())
							{
								nbrecords = 0;
								String[] inputStationHeader = null;
								inputStationHeader = consumeStationHeader();
								if (inputStationHeader != null)
								{
									st = new Station(inputFile.getName(), inputStationHeader);
									String ref = stationDomain.getReference(inputFile.getName(), inputStationHeader, true);

									st.setReference(ref);

									if (stationDomain.isSomeInData())
									{

										// sampling rate peut etre à calculer
										if (mediaDomain.getModel().getInputType() != E_InputType.PROFILE)
											if (stationDomain.getModel().getProperty(Intern.S_DATE).isInData())
											{
												if (!isEndData()) // FAE 4673 Pb
													// si une seule ligne
													// données et
													// pas d'entête station
													st.calculSamplingRate(readedLine);
											}

									}
									if (!isEndData()) // FAE 4673 Pb si une
										// seule ligne données et pas d'entête
										// station
										st.updateGlobalParamQc(readedLine);

									nbrecords++;

								} else
								{
									break;
								}
							} else if (st != null)
							{
								if (mediaDomain.getModel().getInputType() == E_InputType.TRAJECTORY)
								{
									st.completeMedatlasTrajectory(readedLine);
								}
								st.updateGlobalParamQc(readedLine);
								nbrecords++;
							}
						}

						if (st != null)
						{
							// endData = EOF : la station n'a pas été
							// enregistrée
							st.setNbRecords(nbrecords);
							stationDomain.addStation(inputFilePath, st);
						}

					}

					// FAE1105 : tri des stations selon Nom Fichier - Date -
					// Référence Station
					Collections.sort(stationDomain.getStations(inputFilePath));

					numLine = 0;
					bufferedReader.reset();

				}
				bufferedReader.close();

				writeFile(inputFilePath);
			}

		} catch (Exception e)
		{
			stationDomain.resetStations();
			e.printStackTrace();
			throw new GenerationException(e);

		} finally
		{
			numLine = 0;
			try
			{
				if (bufferedReader != null)
					bufferedReader.close();
				if (outputFileWriter != null)
					outputFileWriter.close();
			} catch (IOException ioe)
			{
				throw new GenerationException(ioe);
			}
		}

	}

	protected abstract String getOutputFileName(String inputFilePath);

	protected void printCruiseHeader() throws ParseException, UnsupportedEncodingException
	{
		// écriture de l'entête campagne
		StringBuffer cruiseHeader = cruiseDomain.getHeader(myConversion);
		outputFileWriter.println(cruiseHeader);
	}

	/**
	 * adapted writing processing
	 * 
	 * @throws SplitterException
	 * @throws IOException
	 * @throws ParseException
	 */
	public abstract void writeFile(String inputFilePath) throws Exception;

	protected BufferedReader getBufferReader(String inputFilePath, Station currentStation) throws FileNotFoundException
	{
		File inputFile = new File(inputFilePath + File.separator + currentStation.getFileName());
		return new BufferedReader(new FileReader(inputFile));

	}

	protected boolean inFileHeader()
	{
		return numLine <= mediaModel.getFileHeader();
	}

	/**
	 * @return
	 */
	protected boolean isStartStationHeader()
	{
		boolean start = false;
		int nbLignesFileHeader = mediaModel.getFileHeader();
		if (numLine == (nbLignesFileHeader + 1))
		{
			start = true;
		} else if (mediaModel.getEndData() == E_EndData.BEGIN_LINE_CHANGES)
		{
			start = isEndData();
		} else if (isProcessType_CruiseFile || mediaModel.isMultiStations())
		{
			start = isEndData();
		}
		if (start)
		{
			setBeginningOfPreviousLine();
		}
		return start;
	}

	protected boolean isEndData()
	{

		boolean end = false;
		switch (mediaModel.getEndData()) {
		case NEXT_START_WITH:
		case END_WITH:
			if (mediaModel.getEndDataPatternIsRegexp())
			{
				end = Utils.isreadedLineMatchesRegexp(readedLine, mediaModel.getEndDataPattern() + ".*");
			} else
			{
				end = readedLine.startsWith(mediaModel.getEndDataPattern());
			}
			break;
		case EMPTY_LINE:
			end = readedLine.trim().matches("^$"); //$NON-NLS-1$
			break;
		case BEGIN_LINE_CHANGES:
			if (numLine == (mediaModel.getFileHeader() + 1))
			{
				end = false;
			} else
			{
				end = hasBeginLineChanged(readedLine);
			}

			break;
		default:
			end = false;
			break;
		}

		if (end)
		{
			// System.out.println("is end = " + end);
		}
		return end;
	}

	private boolean hasBeginLineChanged(String readedLine)
	{
		boolean hasChanged = false;
		String beginningOfCurrentLine = readedLine.substring(0, mediaModel.getEndDataPattern().length());
		hasChanged = !beginningOfCurrentLine.equals(beginningOfPreviousLine);
		return hasChanged;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	protected String[] consumeStationHeader() throws IOException
	{
		String[] res = null;
		StringBuffer stationHeader = new StringBuffer();

		if (mediaModel.getEndData() == E_EndData.BEGIN_LINE_CHANGES)
		{
			res = new String[] { readedLine };
		} else
		{
			if (numLine > (mediaModel.getFileHeader() + 1))
			{
				// consommation de la ligne de fin de données
				if (mediaModel.getEndData() != E_EndData.NEXT_START_WITH)
				{
					consumeLine();
				}

				// consommation des lignes séparant les stations
				for (int i = 0; i < mediaModel.getEndStation(); i++)
				{
					if (readedLine != null)
						consumeLine();
				}
			}

			/** récupération des lignes de l'entête station **/
			// si pattern
			String pattern = mediaModel.getStationHeaderPattern();
			if (!pattern.equals(Global.EMPTY_STRING))
			{
				while (readedLine != null && !readedLine.startsWith(pattern))
				{
					// récupération de toutes les lignes avant "pattern"
					stationHeader.append(consumeLine());
				}
				// récupération de la ligne commencant par "pattern"
				stationHeader.append(consumeLine());
			}
			// si nb lignes fixe?
			for (int i = 0; i < mediaModel.getStationHeader(); i++)
			{
				if (readedLine != null)
				{
					stationHeader.append(consumeLine());
				}
			}

			// + 1 ligne si certaines informations station sont dans les données
			if (iDomain.getStation().isSomeInData() && readedLine != null)
			{
				stationHeader.append(readedLine);
			}

			res = stationHeader.toString().split(Global.RN);

			if (readedLine == null)
				res = null;
		}
		return res;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private StringBuffer consumeLine() throws IOException
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(readedLine);
		buffer.append(Global.RN);

		readedLine = bufferedReader.readLine();
		incrementLine();
		return buffer;
	}
}
