package fr.ifremer.nemo.config;

import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.UpdateStatus.EtatUpdate;
import fr.ifremer.nemo.ns_ws_edmo.Edmo_webservice;
import fr.ifremer.nemo.ns_ws_edmo.Edmo_webserviceLocator;
import fr.ifremer.nemo.ns_ws_edmo.Edmo_webserviceSoap;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.NemoService;
import fr.ifremer.nemo.ws.NemoServiceClient;
import fr.ifremer.nemo.ws.model.Parameter;
import fr.ifremer.nemo.ws.model.SurveyDescription;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;

/**
 * Class NemoLists
 * 
 * 
 * @author Gerard BUNEL
 */
public class NemoLists implements Cloneable {

	static protected List<Parameter> _parameters = new ArrayList<>();

	static protected List<SurveyDescription> _surveys = new ArrayList<>();

	/**
	 * list of updates
	 */

	public static ArrayList<String> listeNavire, listeCentreDonnees, listeRegion;

	public static ArrayList<String> listeFiltree;

	private static List<EdmoEntity> _edmoList;

	public NemoLists() {
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		NemoLists copy = null;
		copy = (NemoLists) super.clone();
		return copy;
	}

	/**
	 * release all links for role "parameters"
	 */
	public static void releaseParameters() {
		for (Parameter t : getParameters()) {
			removeParameter(t);
		}
	}

	/**
	 * remove link with x for role "parameters"
	 * 
	 * @param x the linked object to unlink
	 */
	public static void removeParameter(Parameter x) {
		if (!parametersIncludes(x))
			return;
		nocheckRemoveParameter(x);
	}

	/**
	 * Add a link for role "parameters"
	 * 
	 * @param x the object to link with
	 */
	public static void addParameter(Parameter x) {
		if (parametersIncludes(x))
			return;
		nocheckAddParameter(x);
	}

	/**
	 * Returns the collection of linked objects for role "parameters"
	 * 
	 * @return Array of linked objects
	 */
	public static Parameter[] getParameters() {
		return _parameters.toArray(new Parameter[] {});
	}

	/**
	 * Returns linked object for role "parameters" at index i
	 * 
	 * @param i the index of the linked object
	 * @return linked object at index i
	 */
	public static Parameter parametersAt(int i) {
		return _parameters.get(i);
	}

	/**
	 * returns index of object x in "parameters"
	 * 
	 * @param x the linked object
	 */
	public static int parametersIndexOf(Parameter x) {
		if (!parametersIncludes(x))
			return -1;
		for (int i = 0; i < parametersSize(); i++) {
			if (parametersAt(i) == x) {
				return i;
			}
		}
		return -1;
	}

	public static String getParameterLabel(String P09Code) {
		for (int i = 0; i < parametersSize(); i++) {
			if (parametersAt(i).getCode().equals(P09Code)) {
				return parametersAt(i).getUnit();
			}
		}
		return null;
	}

	/**
	 * move x one index before in the list for role "parameters"
	 * 
	 * @param x the object to move
	 */
	public static void parametersMoveUp(Parameter x) {
		int index;
		index = parametersIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveParameterAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "parameters"
	 * 
	 * @param x the object to move
	 */
	public static void parametersMoveTop(Parameter x) {
		int index;
		index = parametersIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveParameterAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "parameters"
	 * 
	 * @param x the object to move
	 */
	public static void parametersMoveDown(Parameter x) {
		int index;
		index = parametersIndexOf(x);
		if (index < 0 || index > parametersSize() - 2)
			return;
		nocheckMoveParameterAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "parameters"
	 * 
	 * @param x the object to move
	 */
	public static void parametersMoveBottom(Parameter x) {
		int index;
		index = parametersIndexOf(x);
		if (index < 0 || index > parametersSize() - 2)
			return;
		nocheckMoveParameterAt(x, parametersSize() - 1);
	}

	/**
	 * move x one index more in the list for role "parameters"
	 * 
	 * @param x the object to move
	 */
	public static void nocheckMoveParameterAt(Parameter x, int a_Index) {
		int index;
		Parameter tmp;
		index = parametersIndexOf(x);
		tmp = _parameters.get(index);
		_parameters.remove(index);
		_parameters.add(a_Index, tmp);
	}

	/**
	 * remove x from the linked objects for role "parameters"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x the object to link with
	 * @see NemoLists#removeParameter(Parameter)
	 */
	protected static void nocheckRemoveParameter(Parameter x) {
		_parameters.remove(x);
	}

	/**
	 * adds x to the linked objects for role "parameters"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x the object to link with
	 * @see NemoLists#addParameter(Parameter)
	 */
	protected static void nocheckAddParameter(Parameter x) {
		_parameters.add(x);
	}

	/**
	 * returns the number of linked objects for role "parameters"
	 * 
	 * @return the number of linked objects
	 */
	public static int parametersSize() {
		return _parameters.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "parameters"
	 * 
	 * @param x an instance of Parameter
	 */
	public static boolean parametersIncludes(Parameter x) {
		return _parameters.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "parameters"
	 * 
	 */
	public static java.util.Iterator<Parameter> parametersIterator() {
		return _parameters.iterator();

	}

	/**
	 * release all links for role "surveys"
	 */
	public static void releaseSurveys() {
		for (SurveyDescription t : getSurveys()) {
			removeSurvey(t);
		}
	}

	/**
	 * remove link with x for role "surveys"
	 * 
	 * @param x the linked object to unlink
	 */
	public static void removeSurvey(SurveyDescription x) {
		if (!surveysIncludes(x))
			return;
		nocheckRemoveSurvey(x);
	}

	/**
	 * Add a link for role "surveys"
	 * 
	 * @param x the object to link with
	 */
	public static void addSurvey(SurveyDescription x) {
		if (surveysIncludes(x))
			return;
		nocheckAddSurvey(x);
	}

	/**
	 * Returns the collection of linked objects for role "surveys"
	 * 
	 * @return Array of linked objects
	 */
	public static SurveyDescription[] getSurveys() {
		SurveyDescription[] v = new SurveyDescription[_surveys.size()];
		_surveys.toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "surveys" at index i
	 * 
	 * @param i the index of the linked object
	 * @return linked object at index i
	 */
	public static SurveyDescription surveysAt(int i) {
		return _surveys.get(i);
	}

	/**
	 * returns index of object x in "surveys"
	 * 
	 * @param x the linked object
	 */
	public static int surveysIndexOf(SurveyDescription x) {
		if (!surveysIncludes(x))
			return -1;
		for (int i = 0; i < surveysSize(); i++) {
			if (surveysAt(i) == x) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * move x one index before in the list for role "surveys"
	 * 
	 * @param x the object to move
	 */
	public static void surveysMoveUp(SurveyDescription x) {
		int index;
		index = surveysIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveSurveyAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "surveys"
	 * 
	 * @param x the object to move
	 */
	public static void surveysMoveTop(SurveyDescription x) {
		int index;
		index = surveysIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveSurveyAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "surveys"
	 * 
	 * @param x the object to move
	 */
	public static void surveysMoveDown(SurveyDescription x) {
		int index;
		index = surveysIndexOf(x);
		if (index < 0 || index > surveysSize() - 2)
			return;
		nocheckMoveSurveyAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "surveys"
	 * 
	 * @param x the object to move
	 */
	public static void surveysMoveBottom(SurveyDescription x) {
		int index;
		index = surveysIndexOf(x);
		if (index < 0 || index > surveysSize() - 2)
			return;
		nocheckMoveSurveyAt(x, surveysSize() - 1);
	}

	/**
	 * move x one index more in the list for role "surveys"
	 * 
	 * @param x the object to move
	 */
	public static void nocheckMoveSurveyAt(SurveyDescription x, int a_Index) {
		int index;
		SurveyDescription tmp;
		index = surveysIndexOf(x);
		tmp = _surveys.get(index);
		_surveys.remove(index);
		_surveys.add(a_Index, tmp);
	}

	/**
	 * remove x from the linked objects for role "surveys"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x the object to link with
	 * @see NemoLists#removeSurvey(SurveyDescription)
	 */
	protected static void nocheckRemoveSurvey(SurveyDescription x) {
		_surveys.remove(x);
	}

	/**
	 * adds x to the linked objects for role "surveys"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x the object to link with
	 * @see NemoLists#addSurvey(SurveyDescription)
	 */
	protected static void nocheckAddSurvey(SurveyDescription x) {
		_surveys.add(x);
	}

	/**
	 * returns the number of linked objects for role "surveys"
	 * 
	 * @return the number of linked objects
	 */
	public static int surveysSize() {
		return _surveys.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "surveys"
	 * 
	 * @param x an instance of SurveyDescription
	 */
	public static boolean surveysIncludes(SurveyDescription x) {
		return _surveys.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "surveys"
	 * 
	 */
	public static java.util.Iterator<SurveyDescription> surveysIterator() {
		return _surveys.iterator();

	}

	/**
	 * init all lists
	 */
	public static void initLists() {

		initParameters();
		initSurveys();
		initEdmo();
	}

	/**
	 * An alternative way for reading files that have been created by XMLDecoder
	 * 
	 * @param filename xml file name
	 * @param expectedType the type of the list items
	 * @return a list of objects with properties that match the content of the
	 * xml file
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	private static <T> List<T> readXmlList(String filename, Class<T> expectedType)
			throws IOException, SAXException, ParserConfigurationException {

		final List<T> list = new ArrayList<>();

		final DefaultHandler handler = new DefaultHandler() {

			private StringBuilder elementValue = null;

			private String propertyName = null;

			private T currentObject = null;

			private void setStringProperty(T obj, String propertyName, String value) {
				final String methodName = "set" + Character.toUpperCase(propertyName.charAt(0)) //$NON-NLS-1$
						+ propertyName.substring(1);
				try {
					Method method = expectedType.getMethod(methodName, String.class);
					method.invoke(obj, value);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			@SuppressWarnings("unused")
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) {
				int propertyIdx = attributes.getIndex("property"); //$NON-NLS-1$
				if (qName.equals("void") && propertyIdx != -1) { //$NON-NLS-1$
					// get the name of the property
					propertyName = attributes.getValue(propertyIdx);
				} else if (qName.equals("object")) { //$NON-NLS-1$
					try {
						// start a new object
						currentObject = expectedType.newInstance();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				// reset the current string
				elementValue = new StringBuilder();
			}

			@Override
			public void characters(char[] ch, int start, int length) {
				elementValue.append(ch, start, length);
			}

			@SuppressWarnings("unused")
			@Override
			public void endElement(String uri, String localName, String qName) {
				if (qName.equals("string") && propertyName != null) { //$NON-NLS-1$
					// set the appropriate attribute on the current object
					setStringProperty(currentObject, propertyName, elementValue.toString());
				} else if (qName.equals("object") && currentObject != null) { //$NON-NLS-1$
					list.add(currentObject);
					currentObject = null;
				}
			}
		};

		SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		saxParser.parse(new File(filename), handler);

		return list;
	}

	public static void initParameters() {
		try {
			_parameters = readXmlList(Intern.dir_resources + File.separator + Intern.list_nemo_parameters,
					Parameter.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initSurveys() {
		try {

			_surveys = readXmlList(Intern.dir_resources + File.separator + Intern.list_nemo_survey,
					SurveyDescription.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int initEdmo() {
		int edmoSize = 0;
		try {

			EdmoHandler edmoH = new EdmoHandler();
			
			SAXParserFactory.newInstance().newSAXParser()
					.parse(new FileInputStream(Intern.dir_resources + File.separator + Intern.list_edmo), edmoH);

			_edmoList = edmoH.getEdmoList();
			edmoSize = _edmoList.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return edmoSize;
	}

	public static List<EdmoEntity> getEdmoList() {
		return _edmoList;
	}

	public static NemoService callNemoWS() {
		final String apiUrl = Nemo.getInstance().getConfig().getNemoWebServiceURL();
		return new NemoServiceClient(apiUrl);
	}

	/**
	 * 
	 */
	public static void updateParameters(IProgressMonitor monitor, NemoService service,
			UpdateStatusList updateNemoList) {

		String listName = Intern.list_nemo_parameters;
		int nbParameters = 0;
		UpdateStatusNemo updateParameters = new UpdateStatusNemo(listName, listName);

		if (service != null) {
			// BackUp de l'objet
			List<Parameter> bkParameters = new ArrayList<Parameter>();
			for (Parameter bkParam : _parameters) {
				bkParameters.add(bkParam);
			}

			FileOutputStream stream = null;
			XMLEncoder encoder = null;
			try {
				if (monitor == null || !monitor.isCanceled()) {
					List<Parameter> parameters = service.getParameters();
					releaseParameters();
					parameters.forEach(param -> {
						// 25794
						SdnVocabularyManager sdnMgr = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
						try {
							String p06 = sdnMgr.getBestVocabMatchCode(

									SdnVocabularyManager.LIST_P06, SdnVocabularyManager.LIST_P09, param.getCode());
							param.setUnitP06(sdnMgr.getP06UnitLabel(p06));
						} catch (Exception e) {
							Nemo.getInstance().getLogger().warn("vocabulary not found", e); //$NON-NLS-1$
						}
						addParameter(param);
					});
					nbParameters = parameters.size();
				}

				if (monitor == null || !monitor.isCanceled()) {
					File file = new java.io.File(Intern.dir_resources + File.separator + listName);
					stream = new FileOutputStream(file.toString());
					encoder = new XMLEncoder(stream);
					encoder.writeObject(_parameters);

					updateParameters.setEtat(EtatUpdate.UPDATED);
					updateParameters.setNbRetrieve(nbParameters);

				} else {
					updateParameters.setEtat(EtatUpdate.INTERRUPTED);
					if (nbParameters != 0) {
						// rollback
						releaseParameters();
						for (Parameter bkParam : bkParameters) {
							addParameter(bkParam);
						}
						nbParameters = 0;
					}
				}
			} catch (Throwable t) {
				Nemo.getInstance().getLogger().error("updateParameters", t);
				updateParameters.setEtat(EtatUpdate.FAILED);
				t.printStackTrace();
			} finally {
				if (encoder != null) {
					try {
						encoder.close();
					} catch (Exception ex) {
					}
				}
				if (stream != null) {
					try {
						stream.close();
					} catch (Exception ex) {
					}
				}
			}
		} else {
			updateParameters.setEtat(EtatUpdate.FAILED);
		}

		updateNemoList.addUpdateStatus(updateParameters);
		if (updateNemoList.getEtat().compareTo(updateParameters.getEtat()) < 0) {
			updateNemoList.setEtat(updateParameters.getEtat());
		}
	}

	/**
	 * 
	 */
	public static void updateSurveys(IProgressMonitor monitor, NemoService service, UpdateStatusList updateNemoList) {

		String listName = Intern.list_nemo_survey;
		int nbSurveys = 0;
		UpdateStatusNemo updateSurveys = new UpdateStatusNemo(listName, listName);

		if (service != null) {

			// BackUp de l'objet
			List<SurveyDescription> bkSurveys = new ArrayList<SurveyDescription>();
			for (SurveyDescription bkSurvey : _surveys) {
				bkSurveys.add(bkSurvey);
			}

			FileOutputStream stream = null;
			XMLEncoder encoder = null;
			try {
				if (monitor == null || !monitor.isCanceled()) {
					List<SurveyDescription> surveys = service.getSurveys();
					releaseSurveys();
					surveys.forEach(s -> addSurvey(s));
					nbSurveys = surveys.size();
				}

				if (monitor == null || !monitor.isCanceled()) {
					File file = new java.io.File(Intern.dir_resources + File.separator + listName);
					stream = new FileOutputStream(file.toString());

					encoder = new XMLEncoder(stream);
					encoder.writeObject(_surveys);

					updateSurveys.setEtat(EtatUpdate.UPDATED);
					updateSurveys.setNbRetrieve(nbSurveys);
				} else {
					updateSurveys.setEtat(EtatUpdate.INTERRUPTED);
					if (nbSurveys != 0) {
						// rollback
						releaseSurveys();
						for (SurveyDescription bkSurvey : bkSurveys) {
							addSurvey(bkSurvey);
						}
						nbSurveys = 0;
					}
				}

			} catch (Exception e) {
				updateSurveys.setEtat(EtatUpdate.FAILED);
				e.printStackTrace();
			} finally {
				if (encoder != null) {
					try {
						encoder.close();
					} catch (Exception ex) {
					}
				}
				if (stream != null) {
					try {
						stream.close();
					} catch (Exception ex) {
					}
				}
			}
		} else {
			updateSurveys.setEtat(EtatUpdate.FAILED);
		}

		updateNemoList.addUpdateStatus(updateSurveys);
		if (updateNemoList.getEtat().compareTo(updateSurveys.getEtat()) < 0) {
			updateNemoList.setEtat(updateSurveys.getEtat());
		}
	}

	public static void updateEdmo(IProgressMonitor monitor) {

		UpdateStatusNemo updateEdmo = (UpdateStatusNemo) ManageLists.getUpdate(Intern.id_edmo);
		int nbEdmo = 0;
		FileOutputStream stream = null;
		String result = Global.EMPTY_STRING;

		try {
			if (monitor == null || !monitor.isCanceled()) {
				// Appel au WebService
				Edmo_webservice service = new Edmo_webserviceLocator();
				((Edmo_webserviceLocator) service)
						.setedmo_webserviceSoap12EndpointAddress(Nemo.getInstance().getConfig().getEdmoURL());
				Edmo_webserviceSoap port = service.getedmo_webserviceSoap12();
				result = port.ws_edmo_get_list();
			}
			if (monitor == null || !monitor.isCanceled()) {
				if (!result.equals(Global.EMPTY_STRING)) {

					if (monitor != null) {
						monitor.subTask(Messages.getString(Intern.id_edmo));
					}

					// écriture du nouveau fichier
					File file = new java.io.File(Intern.dir_resources + File.separator + Intern.list_edmo);

					stream = new FileOutputStream(file.toString());
					OutputStreamWriter osw = new OutputStreamWriter(stream, "UTF-8"); //$NON-NLS-1$
					osw.write(result);
					osw.flush();
					osw.close();

					// stockage des valeurs dans l'objet
					nbEdmo = initEdmo();
					updateEdmo.setEtat(EtatUpdate.UPDATED);
					updateEdmo.setNbRetrieve(nbEdmo);
				} else {
					updateEdmo.setEtat(EtatUpdate.FAILED);
				}
			} else {
				updateEdmo.setEtat(EtatUpdate.INTERRUPTED);
			}

		} catch (Exception exG) {
			updateEdmo.setEtat(EtatUpdate.FAILED);
			exG.printStackTrace();
		}
	}

} /* End of class: NemoLists */
