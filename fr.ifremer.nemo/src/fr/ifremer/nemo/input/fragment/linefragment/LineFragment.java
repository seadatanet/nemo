package fr.ifremer.nemo.input.fragment.linefragment;

import org.xml.sax.Attributes;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.AbstractModel;
import fr.ifremer.nemo.input.ModelDescription;

/**
 * Class LineFragment Description : Represente un fragment d'une ligne du
 * fichier d'entree. Le fragment est definit par un offset de debut et de fin.
 * L'offset de fin est optionnel, dans ce cas sa valeur vaut -1 et la ligne est
 * lue jusqu'e la fin comme illustre ci-dessous v debut du fragment | |
 * XXXXXXXXX| | ^ fin du fragment
 * 
 * @author Gerard BUNEL
 */
public abstract class LineFragment extends AbstractModel {

	public LineFragment() {
		_tagName = ModelDescription.nodetitle_default;
		initValues();
	}

	public LineFragment(InputLineFragment inputLineFragment) {
		this();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initValues() {
		// _firstCharOffset = 1;
		// _lastCharOffset = 1;
	}

//  /**
//  * @see AbstractModel.java#fromElement
//  */
//	public static SAXConsumer fromElement(String a_URI, 
//                                       String a_LocalName,
//                                       String a_QName, 
//                                       Attributes a_Attributes){		
//		LineFragment el;
//		//FIXME problème au premier chargement de fichier nemo ici pourquoi ???
////		if (!Nemo.getInstance().getDomain().getMedia().getModel().getUseTableForCsvData()) {
//		if (!Nemo.getInstance().TESTINPUTMODELCURRENT.getMediaModel().getUseTableForCsvData()) {
//			 el = new CharLineFragment();
//		} else {
//			 el = new CsvLineFragment();
//		}
//		
//		el.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
//		return el;
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildFromElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer consumeElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) {
		// no node
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer handlePathElement(String path, String a_URI, String a_LocalName, String a_QName,
			Attributes a_Attributes) {
		// non utilisé
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		// non utilisé
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		LineFragment copy = null;
		copy = (LineFragment) super.clone();
		return copy;
	}

	/**
	 * Get a value in a line. If it's a raw string line,
	 * 
	 * @param a_Line
	 * @return String
	 */
	public String retreive(String a_Line) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param a_Line
	 * @return String
	 */
	public String fullRetreive(String a_Line) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void release() {
		// no link to release
	}

	/**
	 * Convert inputLineFragment into CSV/Char LineFragment
	 * @param rawLineFragment
	 * @return
	 */
	public static LineFragment convertFragment(InputLineFragment rawLineFragment) {
		LineFragment frag = null;
		if (rawLineFragment != null) {
			if (Nemo.getInstance().getDomain().getMedia().getModel().getSeparator().isEmpty() || !Nemo.getInstance().getDomain().getMedia().getModel().getUseTableForCsvData()) {
				frag = new CharLineFragment(rawLineFragment);
			} else {
				frag = new CsvLineFragment(rawLineFragment);
			}
		}
		return frag;
	}

}