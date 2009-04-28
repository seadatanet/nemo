package fr.ifremer.nemo.input;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.output.ProgressNotification;

/**
 * Class SurveyMedia
 * 
 * 
 * @author Gerard BUNEL
 */
public abstract class SurveyMedia implements Cloneable, SAXConsumer {

	protected InputModel _inputModel;

	public static Boolean interrompu = false;

	public SurveyMedia() {
		_inputModel = null;
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
    public Object clone() throws CloneNotSupportedException {
		SurveyMedia copy = null;
		copy = (SurveyMedia) super.clone();
		copy._inputModel = null;
		return copy;
	}

	/**
	 * Remove any link for role inputModel
	 */
	public void releaseInputModel() {
		setInputModel(null);
	}
    
    public void release(){
        interrompu = false;
        releaseInputModel();
    }

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public InputModel getInputModel() {
		return _inputModel;
	}

	/**
	 * Create a new link with x for role inputModel
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setInputModel(InputModel x) {
		if (_inputModel == x)
			return;
		InputModel old = _inputModel;
		if (_inputModel != null) {
			_inputModel = null;
			old.setMedia(null);
		}
		_inputModel = x;
		if (x != null) {
			x.setMedia(this);
		}
	}

	/**
	 * 
	 * @param isRefDateBased
	 * @param a_RefDate
	 * @param a_Progress
	 * @return Survey
	 * @exception FileNotFoundException
	 * @exception IOException
	 * @exception java.text.ParseException
	 * @throws Exception
	 */
	public abstract Survey convert(/*long a_RefDate,*/
                                   IProgressMonitor a_monitor, 
                                   ProgressNotification a_Progress) throws Exception;

	/**
	 * 
	 * @return Survey
	 */
	public abstract Survey getModel();
    
    public abstract void setModel(Survey x);

	/**
	 * 
	 * @return StationModel
	 */
	public abstract StationModel getStationModel();

	/**
	 * 
	 * @param a_Writer
	 */
	public abstract void writeXMLOn(XMLOutputWriter a_Writer);
} /* End of class: SurveyMedia */
