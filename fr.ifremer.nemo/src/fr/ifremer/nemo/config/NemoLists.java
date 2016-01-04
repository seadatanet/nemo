package fr.ifremer.nemo.config;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.rpc.ServiceException;

import org.eclipse.core.runtime.IProgressMonitor;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.UpdateStatus.EtatUpdate;
import fr.ifremer.nemo.ns_ws_edmo.Edmo_webservice;
import fr.ifremer.nemo.ns_ws_edmo.Edmo_webserviceLocator;
import fr.ifremer.nemo.ns_ws_edmo.Edmo_webserviceSoap;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.NemoWS;
import fr.ifremer.nemo.ws.NemoWSServiceLocator;
import fr.ifremer.nemo.ws.WSParameter;
import fr.ifremer.nemo.ws.WSSurveyDescription;

/**
 * Class NemoLists
 * 
 * 
 * @author Gerard BUNEL
 */
public class NemoLists implements Cloneable {

	static protected List<WSParameter> _parameters = new java.util.ArrayList<WSParameter>();

	static protected List<WSSurveyDescription> _surveys = new java.util.ArrayList<WSSurveyDescription>();


	/**
	 * list of updates
	 */


//	public static Table filtreTable;

	public static ArrayList<String> listeNavire, listeCentreDonnees,
			listeRegion;

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
		for (WSParameter t : getParameters()) {
			removeParameter(t);
		}
	}

	/**
	 * remove link with x for role "parameters"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public static void removeParameter(WSParameter x) {
		if (!parametersIncludes(x))
			return;
		nocheckRemoveParameter(x);
	}

	/**
	 * Add a link for role "parameters"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public static void addParameter(WSParameter x) {
		if (parametersIncludes(x))
			return;
		nocheckAddParameter(x);
	}

	/**
	 * Returns the collection of linked objects for role "parameters"
	 * 
	 * @return Array of linked objects
	 */
	public static WSParameter[] getParameters() {
		WSParameter[] v = new WSParameter[_parameters.size()];
		_parameters.toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "parameters" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public static WSParameter parametersAt(int i) {
		return _parameters.get(i);
	}

	/**
	 * returns index of object x in "parameters"
	 * 
	 * @param x
	 *            the linked object
	 */
	public static int parametersIndexOf(WSParameter x) {
		if (!parametersIncludes(x))
			return -1;
		for (int i = 0; i < parametersSize(); i++) {
			if (parametersAt(i) == x) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * move x one index before in the list for role "parameters"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void parametersMoveUp(WSParameter x) {
		int index;
		index = parametersIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveParameterAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "parameters"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void parametersMoveTop(WSParameter x) {
		int index;
		index = parametersIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveParameterAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "parameters"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void parametersMoveDown(WSParameter x) {
		int index;
		index = parametersIndexOf(x);
		if (index < 0 || index > parametersSize() - 2)
			return;
		nocheckMoveParameterAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "parameters"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void parametersMoveBottom(WSParameter x) {
		int index;
		index = parametersIndexOf(x);
		if (index < 0 || index > parametersSize() - 2)
			return;
		nocheckMoveParameterAt(x, parametersSize() - 1);
	}

	/**
	 * move x one index more in the list for role "parameters"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void nocheckMoveParameterAt(WSParameter x, int a_Index) {
		int index;
		WSParameter tmp;
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
	 * @param x
	 *            the object to link with
	 * @see NemoLists#removeParameter(WSParameter)
	 */
	protected static void nocheckRemoveParameter(WSParameter x) {
		_parameters.remove(x);
	}

	/**
	 * adds x to the linked objects for role "parameters"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#addParameter(WSParameter)
	 */
	protected static void nocheckAddParameter(WSParameter x) {
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
	 * @param x
	 *            an instance of WSParameter
	 */
	public static boolean parametersIncludes(WSParameter x) {
		return _parameters.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "parameters"
	 * 
	 */
	public static java.util.Iterator<WSParameter> parametersIterator() {
		return _parameters.iterator();

	}

	/**
	 * release all links for role "surveys"
	 */
	public static void releaseSurveys() {
		for (WSSurveyDescription t : getSurveys()) {
			removeSurvey(t);
		}
	}

	/**
	 * remove link with x for role "surveys"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public static void removeSurvey(WSSurveyDescription x) {
		if (!surveysIncludes(x))
			return;
		nocheckRemoveSurvey(x);
	}

	/**
	 * Add a link for role "surveys"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public static void addSurvey(WSSurveyDescription x) {
		if (surveysIncludes(x))
			return;
		nocheckAddSurvey(x);
	}

	/**
	 * Returns the collection of linked objects for role "surveys"
	 * 
	 * @return Array of linked objects
	 */
	public static WSSurveyDescription[] getSurveys() {
		WSSurveyDescription[] v = new WSSurveyDescription[_surveys.size()];
		_surveys.toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "surveys" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public static WSSurveyDescription surveysAt(int i) {
		return _surveys.get(i);
	}

	/**
	 * returns index of object x in "surveys"
	 * 
	 * @param x
	 *            the linked object
	 */
	public static int surveysIndexOf(WSSurveyDescription x) {
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
	 * @param x
	 *            the object to move
	 */
	public static void surveysMoveUp(WSSurveyDescription x) {
		int index;
		index = surveysIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveSurveyAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "surveys"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void surveysMoveTop(WSSurveyDescription x) {
		int index;
		index = surveysIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveSurveyAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "surveys"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void surveysMoveDown(WSSurveyDescription x) {
		int index;
		index = surveysIndexOf(x);
		if (index < 0 || index > surveysSize() - 2)
			return;
		nocheckMoveSurveyAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "surveys"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void surveysMoveBottom(WSSurveyDescription x) {
		int index;
		index = surveysIndexOf(x);
		if (index < 0 || index > surveysSize() - 2)
			return;
		nocheckMoveSurveyAt(x, surveysSize() - 1);
	}

	/**
	 * move x one index more in the list for role "surveys"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void nocheckMoveSurveyAt(WSSurveyDescription x, int a_Index) {
		int index;
		WSSurveyDescription tmp;
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
	 * @param x
	 *            the object to link with
	 * @see NemoLists#removeSurvey(WSSurveyDescription)
	 */
	protected static void nocheckRemoveSurvey(WSSurveyDescription x) {
		_surveys.remove(x);
	}

	/**
	 * adds x to the linked objects for role "surveys"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#addSurvey(WSSurveyDescription)
	 */
	protected static void nocheckAddSurvey(WSSurveyDescription x) {
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
	 * @param x
	 *            an instance of WSSurveyDescription
	 */
	public static boolean surveysIncludes(WSSurveyDescription x) {
		return _surveys.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "surveys"
	 * 
	 */
	public static java.util.Iterator<WSSurveyDescription> surveysIterator() {
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
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static void initParameters() {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new FileInputStream(Intern.dir_resources + File.separator + Intern.list_nemo_parameters));
			_parameters = (List<WSParameter>) decoder.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (decoder != null) {
				try {
					decoder.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static void initSurveys() {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new FileInputStream(Intern.dir_resources + File.separator + Intern.list_nemo_survey));
			_surveys = (List<WSSurveyDescription>) decoder.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (decoder != null) {
				try {
					decoder.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	public static int initEdmo() {
		int edmoSize = 0;
		try {

			EdmoHandler edmoH = new EdmoHandler();

			SAXParserFactory.newInstance().newSAXParser().parse(new FileInputStream(Intern.dir_resources + File.separator + Intern.list_edmo), edmoH);

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

	public static NemoWS callNemoWS() {

		NemoWS service = null;
		try {
			service = new NemoWSServiceLocator().getNEMO(new java.net.URL(Nemo.getInstance().getConfig().getNemoWebServiceURL()));
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return service;
	}

	/**
	 * 
	 */
	public static void updateParameters(IProgressMonitor monitor, NemoWS service, UpdateStatusList updateNemoList) {

		String listName = Intern.list_nemo_parameters;
		int nbParameters = 0;
		UpdateStatusNemo updateParameters = new UpdateStatusNemo(listName, listName);

		if (service != null) {
			// BackUp de l'objet
			List<WSParameter> bkParameters = new ArrayList<WSParameter>();
			for (WSParameter bkParam : _parameters) {
				bkParameters.add(bkParam);
			}

			FileOutputStream stream = null;
			XMLEncoder encoder = null;
			try {
				if (!monitor.isCanceled()) {
					WSParameter[] parameters = service.getParameters();
					releaseParameters();
					for (int i = 0; i < parameters.length; i++) {
						addParameter(parameters[i]);
					}
					nbParameters = parameters.length;
				}

				if (!monitor.isCanceled()) {
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
						for (WSParameter bkParam : bkParameters) {
							addParameter(bkParam);
						}
						nbParameters = 0;
					}
				}
			} catch (Exception e) {
				updateParameters.setEtat(EtatUpdate.FAILED);
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
	public static void updateSurveys(IProgressMonitor monitor, NemoWS service, UpdateStatusList updateNemoList) {

		String listName = Intern.list_nemo_survey;
		int nbSurveys = 0;
		UpdateStatusNemo updateSurveys = new UpdateStatusNemo(listName, listName);

		if (service != null) {

			// BackUp de l'objet
			List<WSSurveyDescription> bkSurveys = new ArrayList<WSSurveyDescription>();
			for (WSSurveyDescription bkSurvey : _surveys) {
				bkSurveys.add(bkSurvey);
			}

			FileOutputStream stream = null;
			XMLEncoder encoder = null;
			try {
				if (!monitor.isCanceled()) {
					WSSurveyDescription[] surveys = service.getSurveyList();
					releaseSurveys();
					for (int i = 0; i < surveys.length; i++) {
						addSurvey(surveys[i]);
					}
					nbSurveys = surveys.length;
				}

				if (!monitor.isCanceled()) {
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
						for (WSSurveyDescription bkSurvey : bkSurveys) {
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
			if (!monitor.isCanceled()) {
				// Appel au WebService
				Edmo_webservice service = new Edmo_webserviceLocator();
				((Edmo_webserviceLocator) service).setedmo_webserviceSoap12EndpointAddress(Nemo.getInstance().getConfig().getEdmoURL());
				Edmo_webserviceSoap port = service.getedmo_webserviceSoap12();
				result = port.ws_edmo_get_list();
			}
			if (!monitor.isCanceled()) {
				if (!result.equals(Global.EMPTY_STRING)) {
					monitor.subTask(Messages.getString(Intern.id_edmo));

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
