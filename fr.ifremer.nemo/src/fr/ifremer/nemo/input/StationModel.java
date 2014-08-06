package fr.ifremer.nemo.input;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.widgets.TableItem;
import org.xml.sax.Attributes;

import com.ago.xml.PathElementConsumer;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.config.ConfigDescription;
import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.FlagTable;

/**
 * Class StationModel 
 * Description : Informations descrivant l'onglet Station
 * @author Gerard BUNEL
 */
public class StationModel extends AbstractModel
{
    /**
     * la description de la référence de la station
     */
    protected PropertyModel _referenceProperty;

    /**
     * Liste des propriétés de l'entête station
     */
    protected Map<String, PropertyModel> _properties;

    /**
     * Type de donnee mesure par cette station. Il s'agit d'un d'un des
     * dataTypes de la relation datatType sur Survey. Le dataType doit de plus
     * correspondre e un capteur.
     */
    protected DataType _dataType;

    /**
     * Tableau de correspondance des flags
     */
    protected FlagTable _flagTable;

	private TableItem[] sdnReferences;

    public StationModel(){
        _tagName = ModelDescription.node_stationModel;
        initValues();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initValues(){
        _referenceProperty = null;
        _properties = new LinkedHashMap<String, PropertyModel>();
        _flagTable = null;
    }


    /**
     * @see AbstractModel.java#fromElement
     */
    public static SAXConsumer fromElement(String a_URI, 
                                          String a_LocalName,
                                          String a_QName, 
                                          Attributes a_Attributes){
        StationModel el = new StationModel();
        el.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
        return el;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void buildFromElement(String a_URI, 
                                 String a_LocalName,
                                 String a_QName, 
                                 Attributes a_Attributes){
        // no attribute
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SAXConsumer consumeElement(String a_URI, 
                                      String a_LocalName,
                                      String a_QName, 
                                      Attributes a_Attributes){
        
        if (a_LocalName.equals(ModelDescription.node_reference)){
            return new PathElementConsumer(ModelDescription.node_reference, this);
        }
        if (a_LocalName.equals(ModelDescription.node_listProperties)){
            return new PathElementConsumer(ModelDescription.node_listProperties, this);
        }
        if (a_LocalName.equals(ModelDescription.node_datatypeModel)){
            SAXConsumer el = DataType.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
            setDataType((DataType) el);
            return el;
            //return new PathElementConsumer(ModelDescription.node_datatypeModel, this);
        }
        if (a_LocalName.equals(ConfigDescription.node_FlagTable)){
            String code = null;
            String label = null;
            FlagTable flagTable = new FlagTable();
            if ((code = a_Attributes.getValue(ConfigDescription.a_code)) != null
                    && (label = a_Attributes.getValue(ConfigDescription.a_label)) != null){
                flagTable.setCode(code);
                flagTable.setLibelle(label);
                setFlagTable(flagTable);
            }
            return this;
        }
        if (a_LocalName.equals(ConfigDescription.node_Flag)){
            String local = null;
            String sdn = null;
            Flag flag = new Flag();
            if ((local = a_Attributes.getValue(ConfigDescription.a_local)) != null
                    && (sdn = a_Attributes.getValue(ConfigDescription.a_SDN)) != null){
                flag.setLocal(local);
                flag.setSdn(sdn);
                getFlagTable().getFlags().add(flag);
            }
            return this;
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SAXConsumer handlePathElement(String path, 
                                         String a_URI,
                                         String a_LocalName, 
                                         String a_QName, 
                                         Attributes a_Attributes){
        SAXConsumer el = null;
        if (path.equals(ModelDescription.node_reference)){
            el = PropertyModel.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
            setReferenceProperty((PropertyModel) el);
            return el;
        }
        if (path.equals(ModelDescription.node_listProperties)){
            el = PropertyModel.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
            addProperty((PropertyModel) el);
            return el;
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeXMLBodyOn(XMLOutputWriter a_Writer){

        a_Writer.openTag(ModelDescription.node_reference);
        if (getReferenceProperty() != null)
            getReferenceProperty().writeXMLOn(a_Writer);
        a_Writer.closeTag();
        
        a_Writer.openTag(ModelDescription.node_listProperties);
        for (PropertyModel el : getProperties()){
            el.writeXMLOn(a_Writer);
        }
        a_Writer.closeTag();
        
        if (getDataType() != null){
            getDataType().writeXMLOn(a_Writer);
        }
        
        if (getFlagTable() != null){
            a_Writer.openTag(ConfigDescription.node_FlagTable);
            a_Writer.putAttribute(ConfigDescription.a_code, getFlagTable().getCode());
            a_Writer.putAttribute(ConfigDescription.a_label, getFlagTable().getLibelle());
            for (int i = 0; i < getFlagTable().getFlags().size(); i++){
                a_Writer.openTag(ConfigDescription.node_Flag);
                a_Writer.putAttribute(ConfigDescription.a_local, getFlagTable().getFlags().get(i).getLocal());
                a_Writer.putAttribute(ConfigDescription.a_SDN, getFlagTable().getFlags().get(i).getSdn());
                a_Writer.closeTag();
            }
            a_Writer.closeTag();
        }
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        StationModel copy = null;
        copy = (StationModel) super.clone();
        copy._referenceProperty = null;
        if (getReferenceProperty() != null){
            copy.setReferenceProperty((PropertyModel) getReferenceProperty().clone());
        }
        copy._properties = new LinkedHashMap<String, PropertyModel>();
        for (PropertyModel t0 : getProperties()){
            copy.addProperty((PropertyModel) t0.clone());
        }
        copy._flagTable = null;
        FlagTable flagTable = new FlagTable();
        if (getFlagTable() != null){
            flagTable.setCode(getFlagTable().getCode());
            flagTable.setLibelle(getFlagTable().getLibelle());
            flagTable.getFlags().addAll(getFlagTable().getFlags());
            copy.setFlagTable(flagTable);
        }
        return copy;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void release(){
        releaseProperties();
        releaseReferenceProperty();
        _flagTable = null;
    }

    /**
     * Supprime _properties
     */
    private void releaseProperties(){
        for (PropertyModel el : getProperties()){
            el.release();
            removeProperty(el);
        }
        _properties = null;
    }

    /**
     * Supprime _referenceProperty
     */
    private void releaseReferenceProperty(){
        if (_referenceProperty != null)
            _referenceProperty.release();
        setReferenceProperty(null);
    }


    /**
     * Getter pour _referenceProperty.
     * @return PropertyModel
     * @see StationModel.java#_referenceProperty
     */
    public PropertyModel getReferenceProperty(){
        return _referenceProperty;
    }

    /**
     * Setter pour _referenceProperty
     * @param x : nouvelle valeur pour _referenceProperty
     * @see StationModel.java#_referenceProperty
     */
    public void setReferenceProperty(PropertyModel x){
        if (_referenceProperty == x)
            return;
        _referenceProperty = x;
    }

    /**
     * Getter pour _properties.
     * @return PropertyModel[] : le tableau des PropertyModel
     * @see StationModel.java#_properties
     */
    public PropertyModel[] getProperties(){
        PropertyModel[] v = new PropertyModel[_properties.size()];
        _properties.values().toArray(v);
        return v;
    }
    
    public PropertyModel getProperty(String name){
        PropertyModel property = null;
        for (PropertyModel p : getProperties()){
            if (p.getProperty().equals(name)){
                property = p;
                break;
            }
        }
        return property;
    }

    /**
     * Renvoit le PropertyModel trouvé à l'index i de _properties
     * @param i : index
     * @return PropertyModel
     * @see StationModel.java#_properties
     */
    public PropertyModel propertiesAt(String i){
        return _properties.get(i);
    }

    /**
     * Supprime x de _properties
     * @param x : PropertyModel à supprimer
     * @see StationModel.java#_properties
     */
    public void removeProperty(PropertyModel x){
        if (_properties.containsKey(x.getProperty()))
            _properties.remove(x.getProperty());
    }

    /**
     * Ajoute x à _properties
     * @param x : PropertyModel à ajouter
     * @see StationModel.java#_properties
     */
    public void addProperty(PropertyModel x){
        if (!_properties.containsKey(x.getProperty()))
            _properties.put(x.getProperty(), x);
    }
    

    /**
     * Getter pour _dataType.
     * @return DataType
     * @see StationModel.java#_dataType
     */
    public DataType getDataType(){
        return _dataType;
    }

    /**
     * Setter pour _dataType
     * @param dataType : nouvelle valeur pour dataType
     * @see StationModel.java#_dataType
     */
    public void setDataType(DataType dataType){
        _dataType = dataType;
    }

    /**
     * Getter pour _flagTable.
     * @return FlagTable
     * @see StationModel.java#_flagTable
     */
    public FlagTable getFlagTable(){
        //if (_flagTable != null){
            return _flagTable;
        //}
        //return Utils.getBlankFlagTable();
    }

    /**
     * Setter pour _flagTable
     * @param x : nouvelle valeur pour _flagTable
     * @see StationModel.java#_flagTable
     */
    public void setFlagTable(FlagTable x){
        _flagTable = x;
    }

	public void setSdnReferences(TableItem[] items) {
		this.sdnReferences = items;
		
	}
}