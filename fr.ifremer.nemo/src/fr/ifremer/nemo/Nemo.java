/*
 * ============================================================================
 * Nom du fichier : Nemo.java
 * Application : NEMO version 1.2.20
 * Auteur : dboulang
 * Date de création : 9 déc. 09
 * Client : Ifremer
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.ago.xml.XMLBootstrapParser;

import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.ui.ApplicationWorkbenchWindowAdvisor;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

/**
 * Class Nemo.java Description: Classe principale de l'application - utilisée en
 * singleton
 * 
 * @author dboulang
 */
public class Nemo {
	private static Nemo instance;

	private Configuration config;
	private InputDomain domain;
	private String nomModele;
	// 4063
	private String currentOpenModelPath;

	private ViewMain myViewMain;

	private boolean batchMode;

	/**
	 * informations stockées pour afficher dans la log en cas d'erreur
	 */
	private String log_currentInputFile;
	private String log_currentInputLine;

	/**
	 * Getter pour instance.
	 * 
	 * @return Nemo
	 * @see Nemo.java#instance
	 */
	public static Nemo getInstance() {
		if (null == instance) { // Premier appel
			instance = new Nemo();
		}
		return instance;
	}

	/**
	 * constructeur
	 */
	private Nemo() {
		config = new Configuration();
		// InputModel model = new InputModel();
		domain = new InputDomain();
		nomModele = Global.EMPTY_STRING;
	}

	public void resetInstance() {
		instance = null;
	}

	public void restoreDefaultConfig() {
		// Restoration de la configuration initiale
		config.restoreDefault();
		// Enregistrement de la configuration
		config.save();
	}

	public void resetModel() {
		nomModele = Global.EMPTY_STRING;
		InputModel model = new InputModel();
		domain.update(model);
	}

	public boolean openModel(String pathModel, Logger logger) throws FileNotFoundException,
			TransformerException {

		// nom du model
		nomModele = new File(pathModel).getName();

		InputModel model = new InputModel();
		XMLBootstrapParser parser = new XMLBootstrapParser(model);

		FileInputStream stream = new FileInputStream(pathModel);
		parser.parse(stream, false);
		boolean isBeforeP011 = false;

		if (Global.EMPTY_STRING.equals(model.getVersion())) {
			isBeforeP011 = true;
			// traduction des modèles antérieurs à la version 1.2.20 (P011
			// introduits en version 1.2.20)
			model = new InputModel();
			parser = new XMLBootstrapParser(model);

			StreamSource xmlSource = new StreamSource(new File(pathModel));
			StreamSource xslSource =
					new StreamSource(new File(Intern.dir_resources + File.separator
							+ Intern.ModelXSL));
			Result result = new SAXResult(parser);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(xslSource);
			transformer.transform(xmlSource, result);
		}
		//FAE 16868
		if (compareVersion(model.getVersion(), "1.5.1") == -1) {
			String regionName = model.getCruiseModel().getRegionName();
			String C19RegionName = CruiseModel.getTranscodeRegionName(regionName);
			model.getCruiseModel().setRegionName(C19RegionName);
		}
		// models for profiles, MEDATLAS are invalids if older than 1.6.0 
		if (compareVersion(model.getVersion(), "1.6.0") == -1
				&& model.getConversionModel().getOutputFormat()==E_TypeConversion.MEDATLAS
				&& model.getMediaModel().getInputType() == E_InputType.PROFILE) {// TODO check conversion and type
			if (logger != null){
				logger.error("models for medatlas profiles older than 1.6.0 shall be validated in Nemo before be used in batch mode");// TODO 20755
			}
			model.setIsValid(false);
		}
		completeDataList(model, isBeforeP011);
		domain.update(model);
		return model.isValid();
	}

	private Integer compareVersion(String version1, String version2) {
		String[] vals1 = version1.split("\\.");
		String[] vals2 = version2.split("\\.");
		int i = 0;
		while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
			i++;
		}

		if (i < vals1.length && i < vals2.length) {
			int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
			return diff < 0 ? -1 : diff == 0 ? 0 : 1;
		}

		return vals1.length < vals2.length ? -1 : vals1.length == vals2.length ? 0 : 1;
	}

	private void completeDataList(InputModel model, boolean isBeforeP011) {
		DataListTypeEnum dataList = model.getDataModel().getDataListType();

		// balise dataList inconnue -> version <1.2.33 (avant les P021 si on
		// exclue les versions non distribuées de 1.2.29 à 1.2.32)
		if (dataList == DataListTypeEnum.UNKNOWN) {
			// si on est avant l'usage des P011 (introduits en 1.2.20), on est
			// forcément en P091
			if (isBeforeP011) {
				dataList = DataListTypeEnum.P091;
			}
			// si on est apres l'introduction des P011 mais qu'on a pas de
			// valeur de dataList, c'est qu'on est dans les version
			// intermédiaires
			// donc les listes possibles sont P091 ou P011viaP091
			// on détecte le type à partir de l'attribut isP011 du premier
			// paramètre
			else {
				if (model.getDataModel().getParameterModels().length != 0) {
					if (model.getDataModel().getParameterModels()[0].getParameter().isP011()) {
						dataList = DataListTypeEnum.P011_VIA_P091;
					} else {
						dataList = DataListTypeEnum.P091;
					}
				}
			}
		}

		model.getDataModel().setDataListType(dataList);

	}

	public void saveModel(FileOutputStream a_Stream) {
		InputModel inputModel =
				new InputModel(domain.getMedia().getModel(), domain.getCruise()
						.getModel(), domain.getStation().getModel(), domain.getData()
						.getModel(), domain.getConversion().getModel(), Nemo
						.getInstance().getViewMain().getEditDataForm().isValidated());
		inputModel.writeXMLOn(a_Stream);
	}

	/**
	 * Getter pour config.
	 * 
	 * @return Configuration
	 * @see Nemo.java#config
	 */
	public Configuration getConfig() {
		return config;
	}

	/**
	 * Setter pour config
	 * 
	 * @param config
	 *            : nouvelle valeur pour config
	 * @see Nemo.java#config
	 */
	public void setConfig(Configuration config) {
		this.config = config;
	}

	/**
	 * Getter pour domain.
	 * 
	 * @return InputDomain
	 * @see Nemo.java#domain
	 */
	public InputDomain getDomain() {
		return domain;
	}

	/**
	 * Setter pour domain
	 * 
	 * @param domain
	 *            : nouvelle valeur pour domain
	 * @see Nemo.java#domain
	 */
	public void setDomain(InputDomain domain) {
		this.domain = domain;
	}

	/**
	 * Getter pour nomModele.
	 * 
	 * @return String
	 * @see Nemo.java#nomModele
	 */
	public String getNomModele() {
		return nomModele;
	}

	/**
	 * Getter pour nomModele.
	 * 
	 * @return String
	 * @see Nemo.java#nomModele
	 */
	public void setNomModele(String nomModel) {
		nomModele = nomModel;
	}

	public void refreshNomNemo() {
		// nom du model
		String modelName = Nemo.getInstance().getNomModele();

		// nom de l'application
		if (RulesManager.getInstance().isCruiseInADirectory()) {
			// dossier campagne
			File fich = new File(myViewMain.getEditFileForm().nomRepertoire);
			if (!Global.EMPTY_STRING.equals(modelName)) {
				ApplicationWorkbenchWindowAdvisor.configurer_titre
						.setTitle(Messages.application_directoryTitle
								+ fich.getAbsolutePath()
								+ Messages.application_modelTitle + modelName
								+ " ]"); //$NON-NLS-1$

			} else {
				ApplicationWorkbenchWindowAdvisor.configurer_titre
						.setTitle(Messages.application_directoryTitle
								+ fich.getAbsolutePath() + " ]"); //$NON-NLS-1$ 
			}

		} else {
			// fichier campagne
			File fich;
			fich = new File(myViewMain.getEditFileForm().nomFichier);

			if (!Global.EMPTY_STRING.equals(modelName)) {
				ApplicationWorkbenchWindowAdvisor.configurer_titre
						.setTitle(Messages.application_fileTitle
								+ fich.getAbsolutePath()
								+ Messages.application_modelTitle + modelName
								+ " ]"); //$NON-NLS-1$
			} else {
				ApplicationWorkbenchWindowAdvisor.configurer_titre
						.setTitle(Messages.application_fileTitle
								+ fich.getAbsolutePath() + " ]"); //$NON-NLS-1$ 
			}
		}
	}

	public String getCurrentOpenModelPath() {
		return currentOpenModelPath;
	}

	public void setCurrentOpenModelPath(String currentOpenModelPath) {
		this.currentOpenModelPath = currentOpenModelPath;
	}

	public void setViewMain(ViewMain viewMain) {
		myViewMain = viewMain;
	}

	public ViewMain getViewMain() {
		return myViewMain;
	}

	public boolean isBatchMode() {
		return batchMode;
	}

	public void setBatchMode(boolean batchMode) {
		this.batchMode = batchMode;
	}

	public String getLog_currentInputFile() {
		return log_currentInputFile;
	}

	public void setLog_currentInputFile(String log_currentInputFile) {
		this.log_currentInputFile = log_currentInputFile;
	}

	public String getLog_currentInputLine() {
		return log_currentInputLine;
	}

	public void setLog_currentInputLine(String log_currentInputLine) {
		this.log_currentInputLine = log_currentInputLine;
	}

	public String getErrorMessageWithFileAndLineNumber() {
		return Messages.error_fileName_message + Global.DBL_PT + Global.SPACE
				+ getLog_currentInputFile() + Global.SPACE
				+ Messages.error_lineNumber_message + Global.DBL_PT
				+ Global.SPACE + getLog_currentInputLine();
	}

	public String getVersion() {
		String version = null;
		//		try {
		//			InputStream in = new FileInputStream("META-INF/MANIFEST.MF");
		//			Manifest manifest = new Manifest(in);
		//			version = manifest.getMainAttributes().getValue("Bundle-Version");
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
		Pattern p;
		Matcher m;
		try {
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream("resources/version.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			if ((strLine = br.readLine()) != null) {
				version = strLine;
				p = Pattern.compile("([a-zA-Z\\s])");
				m = p.matcher(strLine);
				if (m.find()) {
					version = strLine.substring(0, m.start());
				}

			}
			//Close the input stream
			in.close();
		} catch (Exception e) {//Catch exception if any
			e.printStackTrace();
		}
		return version;
	}
}
