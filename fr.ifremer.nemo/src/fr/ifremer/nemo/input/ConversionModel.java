package fr.ifremer.nemo.input;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.util.Global.E_TypeConversion;

/**
 * Class ConversionModel
 * Description: Informations descrivant l'onglet Conversion
 * @author Delphine BOULANGER
 */
public class ConversionModel extends AbstractModel
{
    /**
     * liste des conversions autorisées
     */
    protected List<E_TypeConversion> _enableConversions;

    
    /**
     * format de sortie choisi
     */
    private E_TypeConversion _outputFormat;
    
    /**
     * indicateur de tri des stations en sortie
     */
    protected boolean _sortStations;

    public ConversionModel(){
        _tagName = ModelDescription.node_conversionModel;
        initValues();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initValues(){
        _enableConversions = new ArrayList<E_TypeConversion>();
        for (E_TypeConversion e : E_TypeConversion.values()){
            _enableConversions.add(e);
        }
        _sortStations = false;
        _outputFormat = E_TypeConversion.MEDATLAS;
    }


    /**
     * @see AbstractModel.java#fromElement
     */
    static public SAXConsumer fromElement(String a_URI, 
                                          String a_LocalName,
                                          String a_QName, 
                                          Attributes a_Attributes){
        ConversionModel el = new ConversionModel();
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
        // l'élément n'a pas d'attributs
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SAXConsumer consumeElement(String a_URI, 
                                      String a_LocalName,
                                      String a_QName, 
                                      Attributes a_Attributes)
    {
    // lecture permissions
        for (E_TypeConversion e : E_TypeConversion.values()){
            if (a_LocalName.equals(e.toString())){
                boolean enable = new Boolean(a_Attributes.getValue(ModelDescription.a_enable));
                if (enable && ! _enableConversions.contains(e)){
                    _enableConversions.add(e);
                }
                else if (!enable &&  _enableConversions.contains(e))
                {
                    _enableConversions.remove(e);
                }
                return this;
            }
        }
            
        if (a_LocalName.equals(ModelDescription.node_listPermissions)){
//            for (E_TypeConversion e : E_TypeConversion.values()){
//                //TODO corriger lecture
//                if (a_LocalName.equals(e.toString())){
//                    boolean enable = new Boolean(a_Attributes.getValue(ModelDescription.a_enable));
//                    if (enable)
//                        _enableConversions.add(e);
//                    else
//                        _enableConversions.remove(e);
//                }
//            }
            return this;
        }
       
        if (a_LocalName.equals(ModelDescription.node_sort)){            
            Element2Field setter = new Element2Field(this, ModelDescription.setter_sort);
            return setter;
        }
        if (a_LocalName.equals(ModelDescription.node_outputFormat))
        {
            Element2Field setter = new Element2Field(this, ModelDescription.setter_outputFormat);
            return setter;
        }
        return this;
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
        // non utilisé
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
        
        a_Writer.openTag(ModelDescription.node_listPermissions);
        for (E_TypeConversion e : E_TypeConversion.values()){
            a_Writer.openTag(e.toString());
            a_Writer.putAttribute(ModelDescription.a_enable, _enableConversions.contains(e));
            a_Writer.closeTag();
        }
        a_Writer.closeTag();
        a_Writer.putTag(ModelDescription.node_sort, isSortStations());
        a_Writer.putTag(ModelDescription.node_outputFormat, getOutputFormat().toString());
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ConversionModel copy = null;
        copy = (ConversionModel) super.clone();
        copy._enableConversions = new ArrayList<E_TypeConversion>();
        for (E_TypeConversion t1 : getEnableConversions()){
            copy.addEnableConversion(t1);
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release(){
        _enableConversions = null;
    }    

    /**
     * Getter pour _enableConversions
     * @return E_TypeConversion[] : le tableau des E_TypeConversion
     * @see ConversionModel.java#_enableConversions
     */
    public E_TypeConversion[] getEnableConversions(){
        E_TypeConversion[] v = new E_TypeConversion[_enableConversions.size()];
        _enableConversions.toArray(v);
        return v;
    }

    /**
     * Renvoit la E_TypeConversion trouvée à l'index i de _enableConversions
     * @param i : index
     * @return E_EnableConversion
     * @see ConversionModel.java#_enableConversions
     */
    public E_TypeConversion EnableConversionAt(int i){
        return _enableConversions.get(i);
    }

    /**
     * Supprime x de _enableConversions
     * @param x : E_TypeConversion à supprimer
     * @see ConversionModel.java#_enableConversions
     */
    public void removeEnableConversion(E_TypeConversion x){
        if (_enableConversions.contains(x))
            _enableConversions.remove(x);
    }

    /**
     * Ajoute x à _enableConversions
     * @param x : E_TypeConversion à ajouter
     * @see ConversionModel.java#_enableConversions
     */
    public void addEnableConversion(E_TypeConversion x){
        if (!_enableConversions.contains(x))
            _enableConversions.add(x);
    }

    /**
     * Getter pour _sortStations.
     * @return boolean
     * @see ConversionModel.java#_sortStations
     */
    public boolean isSortStations(){
        return _sortStations;
    }

    /**
     * Setter pour _sortStations
     * @param x : nouvelle valeur pour _sortStations
     * @see ConversionModel.java#_sortStations
     */
    public void setSortStations(boolean x){
        _sortStations = x;
    }  

    /**
     * Setter pour _sortStations
     * @param s : nouvelle valeur pour _sortStations sous forme de String
     * @see ConversionModel.java#_sortStations
     */
    public void setSortStations(String s){
        _sortStations = new Boolean(s).booleanValue();;
    }

    public E_TypeConversion getOutputFormat()
    {
        return _outputFormat;
    }

    public void setOutputFormat(String outputFormat)
    {
        if (outputFormat.equals(E_TypeConversion.MEDATLAS.toString()))
        {
            setOutputFormat(E_TypeConversion.MEDATLAS);
        }
        else if(outputFormat.equals(E_TypeConversion.ODV_MONOSTATION.toString()))
        {
            setOutputFormat(E_TypeConversion.ODV_MONOSTATION);
        }
        else if(outputFormat.equals(E_TypeConversion.ODV_MULTISTATIONS.toString()))
        {
            setOutputFormat(E_TypeConversion.ODV_MULTISTATIONS);
        }
    }
    public void setOutputFormat(E_TypeConversion outputFormat)
    {
        _outputFormat = outputFormat;
    }   
}