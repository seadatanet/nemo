package fr.ifremer.nemo.input;

import java.util.Dictionary;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.xml.sax.Attributes;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_TypeConversion;

/**
 * Class InputModel Description : Description d'un modèle XML
 * 
 * @author Gerard BUNEL
 */
public class InputModel extends AbstractModel
{
    /**
     * numéro de version de l'application à l'enregistrement du modèle
     */
    protected String _version;
    /**
     * modèle valide ou non
     */
    protected boolean _isValid;
    /**
     * Type de fichier en sortie (type de conversion)
     */
    protected E_TypeConversion _outputFormat;

    /**
     * Model du media
     */
    protected MediaModel _mediaModel;

    /**
     * Model de la campagne isolé pour permettre son initialisation propre
     */
    protected CruiseModel _cruiseModel;

    /**
     * Model de la station
     */
    protected StationModel _stationModel;

    /**
     * Model des données
     */
    protected DataModel _dataModel;

    /**
     * Model de la station
     */
    protected ConversionModel _conversionModel;

    /**
     * Contructeur
     */
    public InputModel()
    {
        _tagName = ModelDescription.node_root;
        initValues();
    }

    /**
     * Contructeur
     */
    public InputModel(MediaModel media, CruiseModel cruise,
            StationModel station, DataModel data, ConversionModel convert,
            boolean isValid)
    {
        _tagName = ModelDescription.node_root;

        Bundle bundle = Platform.getBundle("fr.ifremer.nemo");
        Dictionary headers = bundle.getHeaders();
        _version = (String) headers.get("Bundle-Version"); //$NON-NLS-1$ 
        _isValid = isValid;
        _mediaModel = media;
        _cruiseModel = cruise;
        _stationModel = station;
        _dataModel = data;
        _conversionModel = convert;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initValues()
    {
        _version = Global.EMPTY_STRING;
        _isValid = true;
        _mediaModel = new MediaModel();
        _cruiseModel = new CruiseModel();
        _stationModel = new StationModel();
        _dataModel = new DataModel();
        _conversionModel = new ConversionModel();
    }

    /**
     * @see AbstractModel.java#fromElement
     */
    public static SAXConsumer fromElement(String a_URI, String a_LocalName,
            String a_QName, Attributes a_Attributes)
    {
        InputModel el = new InputModel();
        el.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
        return el;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildFromElement(String a_URI, String a_LocalName,
            String a_QName, Attributes a_Attributes)
    {
        String tmp;
        if ((tmp = a_Attributes.getValue(ModelDescription.a_version)) != null)
        {
            setVersion(tmp);
        }
        if ((tmp = a_Attributes.getValue(ModelDescription.a_isValid)) != null)
        {
            setIsValid(Boolean.valueOf(tmp));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SAXConsumer consumeElement(String a_URI, String a_LocalName,
            String a_QName, Attributes a_Attributes)
    {
        SAXConsumer el = null;
        if (a_LocalName.equals(ModelDescription.node_mediaModel))
        {
            el =
                MediaModel.fromElement(a_URI, a_LocalName, a_QName,
                    a_Attributes);
            setMediaModel((MediaModel) el);
            return el;
        }
        if (a_LocalName.equals(ModelDescription.node_cruiseModel))
        {
            el =
                CruiseModel.fromElement(a_URI, a_LocalName, a_QName,
                    a_Attributes);
            setCruiseModel((CruiseModel) el);
            return el;
        }
        if (a_LocalName.equals(ModelDescription.node_stationModel))
        {
            el =
                StationModel.fromElement(a_URI, a_LocalName, a_QName,
                    a_Attributes);
            setStationModel((StationModel) el);
            return el;
        }
        if (a_LocalName.equals(ModelDescription.node_dataModel))
        {
            el =
                DataModel
                    .fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
            setDataModel((DataModel) el);
            return el;
        }
        if (a_LocalName.equals(ModelDescription.node_conversionModel))
        {
            el =
                ConversionModel.fromElement(a_URI, a_LocalName, a_QName,
                    a_Attributes);
            setConversionModel((ConversionModel) el);
            return el;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SAXConsumer handlePathElement(String path, String a_URI,
            String a_LocalName, String a_QName, Attributes a_Attributes)
    {
        // non utilisé
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeXMLBodyOn(XMLOutputWriter a_Writer)
    {
        a_Writer.putAttribute(ModelDescription.a_version, getVersion());
        a_Writer.putAttribute(ModelDescription.a_isValid, isValid());
        getMediaModel().writeXMLOn(a_Writer);
        getCruiseModel().writeXMLOn(a_Writer);
        getStationModel().writeXMLOn(a_Writer);
        getDataModel().writeXMLOn(a_Writer);
        getConversionModel().writeXMLOn(a_Writer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        InputModel copy = null;
        copy = (InputModel) super.clone();
        copy._mediaModel = null;
        if (getMediaModel() != null)
        {
            copy.setMediaModel((MediaModel) getMediaModel().clone());
        }
        copy._cruiseModel = null;
        if (getCruiseModel() != null)
        {
            copy.setCruiseModel((CruiseModel) getCruiseModel().clone());
        }
        copy._stationModel = null;
        if (getStationModel() != null)
        {
            copy.setStationModel((StationModel) getStationModel().clone());
        }
        copy._dataModel = null;
        if (getDataModel() != null)
        {
            copy.setDataModel((DataModel) getDataModel().clone());
        }
        copy._conversionModel = null;
        if (getConversionModel() != null)
        {
            copy.setConversionModel((ConversionModel) getConversionModel()
                .clone());
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release()
    {
        releaseMediaModel();
        releaseCruiseModel();
        releaseStationModel();
        releaseDataModel();
        releaseConversionModel();
    }

    /**
     * Supprime _mediaModel
     */
    private void releaseMediaModel()
    {
        if (_mediaModel != null)
            _mediaModel.release();
        _mediaModel = null;
    }

    /**
     * Supprime _cruiseModel
     */
    public void releaseCruiseModel()
    {
        if (_cruiseModel != null)
            _cruiseModel.release();
        _cruiseModel = null;
    }

    /**
     * Supprime _stationModel
     */
    public void releaseStationModel()
    {
        if (_stationModel != null)
            _stationModel.release();
        _stationModel = null;
    }

    /**
     * Supprime _dataModel
     */
    public void releaseDataModel()
    {
        if (_dataModel != null)
            _dataModel.release();
        _dataModel = null;
    }

    /**
     * Supprime _conversionModel
     */
    public void releaseConversionModel()
    {
        if (_conversionModel != null)
            _conversionModel.release();
        _conversionModel = null;
    }

    /**
     * Getter pour _version.
     * 
     * @return String
     * @see InputModel.java#_version
     */
    public String getVersion()
    {
        return _version;
    }

    /**
     * Setter pour _version.
     * 
     * @param x
     *            : nouvelle valeur pour _version
     * @see InputModel.java#_version
     */
    public void setVersion(String a_String)
    {
        _version = a_String;
    }

    /**
     * Getter pour _version.
     * 
     * @return String
     * @see InputModel.java#_version
     */
    public boolean isValid()
    {
        return _isValid;
    }

    /**
     * Setter pour _isValid.
     * 
     * @param x
     *            : nouvelle valeur pour _isValid
     * @see InputModel.java#_version
     */
    public void setIsValid(Boolean a_String)
    {
        if (a_String == null)
            _isValid = true;
        else
            _isValid = a_String;
    }

    /**
     * Getter pour _mediaModel.
     * 
     * @return MediaModel
     * @see InputModel.java#_mediaModel
     */
    public MediaModel getMediaModel()
    {
        return _mediaModel;
    }

    /**
     * Setter pour _mediaModel.
     * 
     * @param x
     *            : nouvelle valeur pour _mediaModel
     * @see InputModel.java#_mediaModel
     */
    public void setMediaModel(MediaModel x)
    {
        if (_mediaModel == x)
            return;
        if (_mediaModel != null)
        {
            _mediaModel = null;
        }
        _mediaModel = x;
    }

    /**
     * Getter pour _cruiseModel.
     * 
     * @return CruiseModel
     * @see InputModel.java#_cruiseModel
     */
    public CruiseModel getCruiseModel()
    {
        return _cruiseModel;
    }

    /**
     * Setter pour _cruiseModel.
     * 
     * @param x
     *            : nouvelle valeur pour _cruiseModel
     * @see InputModel.java#_cruiseModel
     */
    public void setCruiseModel(CruiseModel x)
    {
        if (_cruiseModel == x)
            return;
        _cruiseModel = x;
    }

    /**
     * Getter pour _stationModel.
     * 
     * @return StationModel
     * @see InputModel.java#_stationModel
     */
    public StationModel getStationModel()
    {
        return _stationModel;
    }

    /**
     * Setter pour _stationModel.
     * 
     * @param x
     *            : nouvelle valeur pour _stationModel
     * @see InputModel.java#_stationModel
     */
    public void setStationModel(StationModel x)
    {
        if (_stationModel == x)
            return;
        _stationModel = x;
    }

    /**
     * Getter pour _dataModel.
     * 
     * @return DataModel
     * @see InputModel.java#_dataModel
     */
    public DataModel getDataModel()
    {
        return _dataModel;
    }

    /**
     * Setter pour _dataModel.
     * 
     * @param x
     *            : nouvelle valeur pour _dataModel
     * @see InputModel.java#_dataModel
     */
    public void setDataModel(DataModel x)
    {
        if (_dataModel == x)
            return;
        _dataModel = x;
    }

    /**
     * Getter pour _conversionModel.
     * 
     * @return ConversionModel
     * @see InputModel.java#_conversionModel
     */
    public ConversionModel getConversionModel()
    {
        return _conversionModel;
    }

    /**
     * Setter pour _conversionModel.
     * 
     * @param x
     *            : nouvelle valeur pour _conversionModel
     * @see InputModel.java#_conversionModel
     */
    public void setConversionModel(ConversionModel x)
    {
        if (_conversionModel == x)
            return;
        _conversionModel = x;
    }

    public E_TypeConversion getOutputFormat()
    {
        return _outputFormat;
    }

    public void setOutputFormat(E_TypeConversion outputFormat)
    {
        _outputFormat = outputFormat;
    }
}