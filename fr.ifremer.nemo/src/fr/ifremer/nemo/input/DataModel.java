package fr.ifremer.nemo.input;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.PathElementConsumer;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.util.Global.DataListEnum;


/**
 * Class DataModel.java
 * Description: Informations descrivant l'onglet Data
 * @author Delphine BOULANGER
 */
public class DataModel extends AbstractModel
{
    /**
     * Liste, ordonnee, des paramètres mesurés sur la station
     */
    protected List<ParameterModel> _parameterModels;
    /**
     * liste de paramètres à utiliser
     *
     */
    private DataListEnum _dataList;

    public DataModel(){
        _tagName = ModelDescription.node_dataModel;
        initValues();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initValues(){
        _parameterModels = new ArrayList<ParameterModel>();
        _dataList = DataListEnum.UNKNOWN;
    }

    public DataListEnum getDataList(){
        return _dataList;
    }
    /**
     * @see AbstractModel.java#fromElement
     */
    public static SAXConsumer fromElement(String a_URI, 
                                          String a_LocalName,
                                          String a_QName, 
                                          Attributes a_Attributes){
        DataModel el = new DataModel();
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
                                      Attributes a_Attributes){
        
        if (a_LocalName.equals(ModelDescription.node_listParameters)){
            return new PathElementConsumer(ModelDescription.node_listParameters, this);
        }
        else if (a_LocalName.equals(ModelDescription.node_datalist))
        {
            Element2Field setter = new Element2Field(this, ModelDescription.setter_dataList);
            return setter;
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
        if (path.equals(ModelDescription.node_listParameters)){
            el = ParameterModel.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
            addParameterModel((ParameterModel) el);
            return el;
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeXMLBodyOn(XMLOutputWriter a_Writer){
        a_Writer.putTag(ModelDescription.node_datalist, getDataList().toString());
        a_Writer.openTag(ModelDescription.node_listParameters);
        for (ParameterModel el : getParameterModels()){
            el.writeXMLOn(a_Writer);
        }
        a_Writer.closeTag();
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        DataModel copy = null;
        copy = (DataModel) super.clone();
        copy._parameterModels = new ArrayList<ParameterModel>();
        for (ParameterModel t1 : getParameterModels()){
            copy.addParameterModel((ParameterModel) t1.clone());
        }
        return copy;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void release(){
        releaseParameterModels();
    }

    /**
     * Supprime _parameterModels
     */
    public void releaseParameterModels(){
        for (ParameterModel el : getParameterModels()){
            el.release();
            removeParameterModel(el);
        }
        _parameterModels = null;
    }   

    /**
     * Getter pour _parameterModels
     * @return ParameterModel[] : le tableau des ParameterModel
     * @see DataModel.java#_parameterModels
     */
    public ParameterModel[] getParameterModels(){
        ParameterModel[] v = new ParameterModel[_parameterModels.size()];
        _parameterModels.toArray(v);
        return v;
    }
    
    public ParameterModel getParameterModel(String code){
        ParameterModel parameter = null;
        for (ParameterModel p : getParameterModels()){
            if (p.getParameter().getCode().equals(code)){
                parameter = p;
                break;
            }
        }
        return parameter;
    }

    /**
     * Renvoit le ParameterModel trouvé à l'index i de _parameterModels
     * @param i : index
     * @return ParameterModel
     * @see DataModel.java#_parameterModels
     */
    public ParameterModel parameterModelsAt(int i){
        return _parameterModels.get(i);
    }

    /**
     * Supprime x de _parameterModels
     * @param x : ParameterModel à supprimer
     * @see DataModel.java#_parameterModels
     */
    public void removeParameterModel(ParameterModel x){
        if (_parameterModels.contains(x))
            _parameterModels.remove(x);
    }

    /**
     * Ajoute x à _parameterModels
     * @param x : ParameterModel à ajouter
     * @see DataModel.java#_parameterModels
     */
    public void addParameterModel(ParameterModel x){
        if (!_parameterModels.contains(x))
            _parameterModels.add(x);
    }
    public void setDataList(String dataList)
    {
        if (dataList.equals(DataListEnum.P091.toString()))
        {
            setDataList(DataListEnum.P091);
        }
        else if(dataList.equals(DataListEnum.P011_VIA_P091.toString()))
        {
            setDataList(DataListEnum.P011_VIA_P091);
        }
        else if(dataList.equals(DataListEnum.P011_VIA_P021.toString()))
        {
            setDataList(DataListEnum.P011_VIA_P021);
        }else
        {
            setDataList(DataListEnum.UNKNOWN);
        }
    }
    public void setDataList(DataListEnum dataList)
    {
        _dataList = dataList;
    }   
}