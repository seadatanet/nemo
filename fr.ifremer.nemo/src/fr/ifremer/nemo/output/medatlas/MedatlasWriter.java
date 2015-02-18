package fr.ifremer.nemo.output.medatlas;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.DataDomain;
import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.NemoWriter;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.presdepth.CalcPres;

/**
 * specific NemoWriter : MEDATLAS
 */
public class MedatlasWriter extends NemoWriter
{

	int indexRecord;

	/**
	 * pour les references de stations incrementees, on gere avec cette variable
	 * - on laisse tomber le code de
	 * stationDomain.getReference(currentStation.getFileName(),
	 * inputStationHeader, false); currentStation.getReference renvoie des
	 * nombres a la suite, puiseque les stations mises en memoire dans le
	 * domaine ont eu un increment attribué quand incrementer: si on a un
	 * repertoire de fichiers contenant chacun une station, il faut incrementer
	 * a chaque fichier si on a 1 fichier contenant 1 ou plusieurs stations, il
	 * faut incrementer a chaque station lue
	 **/
	int increment = -1;

	public MedatlasWriter()
	{
		super();
		myConversion = Conversion.MEDATLAS;

	}

	@Override
	protected String getOutputFileName(String inputFilePath)
	{
		return iDomain.getMedia().getOutputFileName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ifremer.nemo.output.NemoWriter#writeFile()
	 */
	@Override
	public final void writeFile(String inputFilePath) throws GenerationException
	{
		try
		{

			if (isRefIncremented)
			{
				increment = Integer.valueOf(Nemo.getInstance().getDomain().getStation().getModel().getReferenceProperty().getUserValue());
			}
			stationDomain.updateRef();
			outputFileName = getOutputFileName(inputFilePath);

			// outputFileWriter = new PrintWriter(new BufferedWriter(new
			// FileWriter(outputFileName)));
			outputFileWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), Global.NEMO_ENCODING)));
			// écriture de l'entête campagne
			printCruiseHeader();

			/**
			 * boucle sur les stations prealablement lues et stockées dans le
			 * stationDomain (donc TOUTES les stations, quel que soit le nb de
			 * fichiers en entree)
			 */
			boolean stationWrited = false;
			boolean lastLineWrited = false;
			for (Station currentStation : stationDomain.getStations(inputFilePath))
			{

				// FAE 6143 check doublons
				stationDomain.getAndCheckLocalCDI(cruiseDomain, Conversion.ODV, currentStation.getReference());
				CalcPres.init();

				bufferedReader = getBufferReader(inputFilePath, currentStation);

				indexRecord = 0;
				stationDatas.clear();
				stationWrited = false;
				lastLineWrited = false;

				// scan the InputFile line by line
				while ((readedLine = bufferedReader.readLine()) != null)
				{
					incrementLine();
					/**
					 * file header
					 */
					if (inFileHeader())
					{
						continue;
					}
					/**
					 * derniere ligne d'une station en sortie (end station)
					 * reperee grace a endData
					 */
					if (isEndData() && stationWrited)
					{
						// écriture de la dernière ligne de données
						writeLastDataLine(dataDomain);
						lastLineWrited = true;
					}
					/**
					 * entete station
					 */
					if (isStartStationHeader())
					{

						// écriture de l'entête station
						String[] inputStationHeader = consumeStationHeader(mediaModel.getStationHeader() > 0);
						String readedRef = "";
						if (inputStationHeader != null)
						{
							// readedRef=
							// stationDomain.getReference(currentStation.getFileName(),
							// inputStationHeader, false);
							readedRef = readReference(currentStation.getFileName(), inputStationHeader);
						}

						if (inputStationHeader != null && currentStation.getReference().equals(readedRef))
						{

							outputFileWriter.println(stationDomain.getHeader(myConversion, currentStation, inputStationHeader));

							// écriture de l'entête des données (titres des
							// colonnes)
							outputFileWriter.println(dataDomain.getDataHeader(myConversion));

							writeDataLine(dataDomain, currentStation, readedLine);
							stationWrited = true;
						} else if (stationWrited || inputStationHeader == null)
						{
							break;
						}
						if (isProcessType_CruiseFile)
						{

							increment++;
						}
					}
					/**
					 * donnees (stationWrited = l'entete de station a deja ete
					 * ecrit)
					 */
					else if (stationWrited)
					{
						// écriture de la ligne de données
						writeDataLine(dataDomain, currentStation, readedLine);
					}
				}
				// fin de lecture du fichier d'entree courant
				if (isProcessType_CruiseFile && isRefIncremented)
				{
					increment = Integer.valueOf(Nemo.getInstance().getDomain().getStation().getModel().getReferenceProperty().getUserValue());
				}
		

				/*
				 * apres avoir ecrit les données de la staion
				 * on ecrit la derniere ligne.
				 * si celle si n'a pas pas déja été ecrite
				 */
				if (stationWrited && !lastLineWrited)
				{
					// écriture de la dernière ligne de données
					writeLastDataLine(dataDomain);
					lastLineWrited = true;
				}
				
				// FAE6315 on ajoute la station pour le CDI ici au lieu de le
				// faire dans la methode writeLastDataLine
				// qui n'est pas appelée dans tous les cas (slmt si endData=EOF)
				if (stationWrited)
				{
					mediaDomain.addStationInOutput(outputFileName, currentStation.getReference());
				}

				numLine = 0;
				if (!isProcessType_CruiseFile && isRefIncremented)
				{
					increment++;
				}
				stationDomain.updateRef();
			}
			// fin de la boucle sur les station du stationDomain
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new GenerationException(e);
		}
	}

	private String readReference(String inputFileName, String[] inputStationHeader) throws ParseException
	{
		String ref = null;
		if (iDomain.getStation().getModel().getReferenceProperty().isUserProvided())
		{
			ref = String.valueOf(increment);
		} else
		{
			ref = iDomain.getStation().getReference(inputFileName, inputStationHeader, false);
		}
		return ref;
	}

	private void writeDataLine(DataDomain data, Station currentStation, String inputLine) throws Exception
	{
		if (iDomain.getConversion().isSortData())
		{
			// préparation des données pour le tri
			Long key;
			if (iDomain.getMedia().getModel().getInputType() == E_InputType.PROFILE)
			{
				ParameterModel keyParam = data.getModel().getParameterModels()[0];
				keyParam.computeParameterValue(inputLine);
				key=(Long) nf.parse(keyParam.getLineValue().getValue());
			} else
			{
				key = DateTimeUtils.getCurrentDate(currentStation, indexRecord);
				//FAE 11356
                if(Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.TIMESERIES 
                        && Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DATE).isInData())
                    key = DateTimeUtils.convertTimestamp(inputLine);
			}

			stationDatas.put(key, data.getDataLine(myConversion, currentStation, inputLine, indexRecord).toString());

		} else
		{
			// écriture directe
			outputFileWriter.println(data.getDataLine(myConversion, currentStation, inputLine, indexRecord));
		}
		indexRecord++;
	}

	private void writeLastDataLine(DataDomain data)
	{
		if (iDomain.getConversion().isSortData())
		{
			// écriture des données après tri
			List<Long> keys = new ArrayList<Long>();
			keys.addAll(stationDatas.keySet());
			Collections.sort(keys);
			for (Long key : keys)
			{
				outputFileWriter.println(stationDatas.get(key));
			}
		}
		// écriture de la dernière ligne de données
		outputFileWriter.println(data.getMedatlasLastDataLine());
	}

}
