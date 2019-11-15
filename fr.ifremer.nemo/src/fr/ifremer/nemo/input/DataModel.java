package fr.ifremer.nemo.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.PathElementConsumer;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.input.parameters.VerticalParametersList;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_MesureType;
import fr.ifremer.nemo.util.Intern;

/**
 * Class DataModel.java
 * Description: Informations descrivant l'onglet Data
 * @author Delphine BOULANGER
 */
public class DataModel extends AbstractModel {
	/**
	 * Liste, ordonnee, des paramètres mesurés sur la station
	 */
	protected List<ParameterModel> _parameterModels;
	/**
	 * liste de paramètres à utiliser
	 *
	 */
	private DataListTypeEnum _dataListType;
	private E_MesureType _mesureType;
	private VerticalParametersList _verticalParametersList;
	private String fallrateCode;
	private String fallrateLabel;

	public DataModel() {
		_tagName = ModelDescription.node_dataModel;
		initValues();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initValues() {
		_verticalParametersList = new VerticalParametersList();
		_parameterModels = new ArrayList<ParameterModel>();
		_dataListType = DataListTypeEnum.UNKNOWN;
		_mesureType = E_MesureType.UNKNOWN;
		fallrateCode = Global.EMPTY_STRING;
		fallrateLabel= Global.EMPTY_STRING;
	}

	public DataListTypeEnum getDataListType() { 
		return _dataListType;
	}

	/**
	 * @see AbstractModel.java#fromElement
	 */
	public static SAXConsumer fromElement(String a_URI,
			String a_LocalName,
			String a_QName,
			Attributes a_Attributes) {
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
			Attributes a_Attributes) {
		// l'élément n'a pas d'attributs
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer consumeElement(String a_URI,
			String a_LocalName,
			String a_QName,
			Attributes a_Attributes) {
		SAXConsumer el = null;
		if (a_LocalName.equals(ModelDescription.node_listParameters)) {
			return new PathElementConsumer(ModelDescription.node_listParameters, this);
		} else if (a_LocalName.equals(ModelDescription.node_dataListType)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_dataListType);
			return setter;
		} else if (a_LocalName.equals(ModelDescription.node_mesureType)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_mesureType);
			return setter;
		} else if (a_LocalName.equals(ModelDescription.node_verticalParametersList)) {
			el = VerticalParametersList.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			setVerticalParametersList((VerticalParametersList) el);
			return el;
		}
		if (a_LocalName.equals(ModelDescription.node_fallrateCode)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_fallrateCode);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_fallrateLabel)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_fallrateLabel);
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
			Attributes a_Attributes) {
		SAXConsumer el = null;
		if (path.equals(ModelDescription.node_listParameters)) {
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
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putTag(ModelDescription.node_dataListType, getDataListType().toString());
		a_Writer.putTag(ModelDescription.node_mesureType, getMesureType().toString());
		a_Writer.putTag(ModelDescription.node_fallrateCode, getFallrateCode());
		a_Writer.putTag(ModelDescription.node_fallrateLabel, getFallrateLabel());
		// ref parametres verticaux
		getVerticalParametersList().writeXMLOn(a_Writer);

		// liste des parametres
		a_Writer.openTag(ModelDescription.node_listParameters);
		for (ParameterModel el : getParameterModels()) {
			el.writeXMLOn(a_Writer);
		}
		a_Writer.closeTag();
	}

	public String getFallrateCode() {
		return fallrateCode;
	}

	public void setFallrateCode(String fallrateCode) {
		this.fallrateCode = fallrateCode;
	}

	public String getFallrateLabel() {
		return fallrateLabel;
	}

	public void setFallrateLabel(String fallrateLabel) {
		this.fallrateLabel = fallrateLabel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		DataModel copy = null;
		copy = (DataModel) super.clone();
		copy._parameterModels = new ArrayList<ParameterModel>();
		for (ParameterModel t1 : getParameterModels()) {
			copy.addParameterModel((ParameterModel) t1.clone());
		}
		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void release() {
		releaseParameterModels();
	}

	public void reinitParameterModelList() {
		_parameterModels = new ArrayList<ParameterModel>();
	}

	/**
	 * Supprime _parameterModels
	 */
	public void releaseParameterModels() {
		for (ParameterModel el : getParameterModels()) {
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
	public ParameterModel[] getParameterModels() {
		ParameterModel[] v = new ParameterModel[_parameterModels.size()];
		_parameterModels.toArray(v);
		return v;
	}

	public ParameterModel getParameterModel(String code) {
		ParameterModel parameter = null;
		for (ParameterModel p : getParameterModels()) {
			if (p.getParameter().getCode().equals(code)) {
				parameter = p;
				break;
			}
		}
		return parameter;
	}

	public ParameterModel getParameterModelByP011(String codeP011) {
		ParameterModel parameter = null;
		for (ParameterModel p : getParameterModels()) {
			if (p.getParameter().getCodeP011().equals(codeP011)) {
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
	public ParameterModel parameterModelsAt(int i) {
		return _parameterModels.get(i);
	}

	/**
	 * Supprime x de _parameterModels
	 * @param x : ParameterModel à supprimer
	 * @see DataModel.java#_parameterModels
	 */
	public void removeParameterModel(ParameterModel x) {
		if (_parameterModels.contains(x))
			_parameterModels.remove(x);
	}

	/**
	 * Ajoute x à _parameterModels
	 * @param x : ParameterModel à ajouter
	 * @see DataModel.java#_parameterModels
	 */
	public void addParameterModel(ParameterModel x) {
		if (!_parameterModels.contains(x))
			_parameterModels.add(x);
	}

	public void setVerticalParametersList(VerticalParametersList list) {
		_verticalParametersList = list;
	}

	public void setDataListType(DataListTypeEnum dataList) {
		_dataListType = dataList;
	}

	public void setDataListType(String dataList) {
		if (dataList.equals(DataListTypeEnum.P091.toString())) {
			setDataListType(DataListTypeEnum.P091);
		} else if (dataList.equals(DataListTypeEnum.P011_VIA_P021.toString())) {
			setDataListType(DataListTypeEnum.P011_VIA_P021);
		} else if (dataList.equals(DataListTypeEnum.P011_VIA_P091.toString())) {
			setDataListType(DataListTypeEnum.P011_VIA_P091);
		}
	}

	public E_MesureType getMesureType() {
		return _mesureType;
	}


	public void setMesureType(E_MesureType mesureType) {
		_mesureType = mesureType;
	}

	public void setMesureType(String mesureType) {

		if (mesureType.equals(E_MesureType.BSSURF.toString())) {
			setMesureType(E_MesureType.BSSURF);
		} else if (mesureType.equals(E_MesureType.BSBED.toString())) {
			setMesureType(E_MesureType.BSBED);
		} else if (mesureType.equals(E_MesureType.ASLVL.toString())) {
			setMesureType(E_MesureType.ASLVL);
		}

	}

	public VerticalParametersList getVerticalParametersList() {
		return _verticalParametersList;
	}
	
	/**
	 * Sets the indexes for parameters that share the same P011 value with other parameters.
	 */
	public void majMultipleP011Indexes() {
		// This map has key = P011 ; value = quantity of this P011 found in the parameters.
		HashMap<String, Integer> mapDuplicates = new HashMap<String, Integer>();
		
		for (ParameterModel parameterModel : _parameterModels) {
			String currentP011 = parameterModel.getParameter().getCodeP011();
			if ( currentP011== null || currentP011.isEmpty()) {
				currentP011 = VocabLists.getVocabMatch(Intern.list_P091, Intern.list_P011,  parameterModel.getParameter().getCode());
				parameterModel.getParameter().setCodeP011(currentP011);
			}
			if (mapDuplicates.containsKey(currentP011)) {
				
				Integer currentCount = mapDuplicates.get(currentP011);
				// this P011 already exists in the parameters. Increment the count + set as index for the parameter
				currentCount++;
				
				parameterModel.getParameter().setIndexP011(currentCount);
			}
			else {
				// first time this P011 is seen, create the map entry for it.
				mapDuplicates.put(currentP011, 1);
				parameterModel.getParameter().setIndexP011(1);
			}
		}
	}
}