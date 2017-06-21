package fr.ifremer.nemo.input;

import org.xml.sax.Attributes;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.util.Global;


/**
 * Class LineFragment
 * Description :
 * Represente un fragment d'une ligne du fichier d'entree. Le fragment est
 * definit par un offset de debut et de fin. L'offset de fin est optionnel, dans
 * ce cas sa valeur vaut -1 et la ligne est lue jusqu'e la fin comme illustre
 * ci-dessous
 * v debut du fragment | | XXXXXXXXX| | ^ fin du fragment
 * @author Gerard BUNEL
 */
public class LineFragment extends AbstractModel
{
	/**
	 * Offset dans la ligne du premier caractere de la chaine de caracteres
	 */
	protected int _firstCharOffset;

	/**
	 * Offset du dernier caractere de la chaine a lire dans le fichier d'entree.
	 * si -1 on lit jusqu'a la fin de ligne
	 */
	protected int _lastCharOffset;

	public LineFragment() {
        _tagName = ModelDescription.nodetitle_default;
		initValues();
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initValues(){
        //_firstCharOffset = 1;
        //_lastCharOffset = 1;
    }


    /**
     * @see AbstractModel.java#fromElement
     */
	public static SAXConsumer fromElement(String a_URI, 
                                          String a_LocalName,
                                          String a_QName, 
                                          Attributes a_Attributes){
		LineFragment el = new LineFragment();
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
		String tmp;
		if ((tmp = a_Attributes.getValue(ModelDescription.a_firstoffset)) != null) {
			setFirstCharOffset(tmp);
		}
		if ((tmp = a_Attributes.getValue(ModelDescription.a_lastoffset)) != null) {
			setLastCharOffset(tmp);
		}
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public SAXConsumer consumeElement(String a_URI, 
                                      String a_LocalName,
                                      String a_QName, 
                                      Attributes a_Attributes){
        // no node
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
        // non utilisé
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putAttribute(ModelDescription.a_firstoffset, getFirstCharOffset());
		a_Writer.putAttribute(ModelDescription.a_lastoffset, getLastCharOffset());
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
     * 
     * @param a_Line
     * @return String
     */
    public String retreive(String a_Line) {
    	String res;
    	if (Nemo.getInstance().getDomain().getMedia().getModel().getSeparator().isEmpty() ||
    			!Nemo.getInstance().getDomain().getMedia().getModel().getUseTableForCsvData()
    			){
    		if (_lastCharOffset == -1) {
    			res= a_Line.substring(_firstCharOffset - 1).trim();
    		}
    		res= a_Line.substring(_firstCharOffset - 1, _lastCharOffset).trim();
    	}else{
    		//33278: split as columns
    		String[] splittedLine = a_Line.split(Nemo.getInstance().getDomain().getMedia().getModel().getSeparator());
    		res= splittedLine[_lastCharOffset-1].trim();
    	}
    	return res;
    }

    /**
     * 
     * @param a_Line
     * @return String
     */
    public String fullRetreive(String a_Line) {
        String fullFrag = Global.EMPTY_STRING;
        if (_lastCharOffset == -1) {
            fullFrag = a_Line.substring(_firstCharOffset - 1);
        } else {
            fullFrag = a_Line.substring(_firstCharOffset - 1, _lastCharOffset);
        }
        return fullFrag;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
	public void release() {
        // no link to release
	}


    /**
     * Getter pour _firstCharOffset.
     * @return int
     * @see LineFragment.java#_firstCharOffset
     */
	public int getFirstCharOffset() {
		return _firstCharOffset;
	}

    /**
     * Setter pour _firstCharOffset
     * @param x : nouvelle valeur pour _firstCharOffset
     * @see LineFragment.java#_firstCharOffset
     */
	public void setFirstCharOffset(int x) {
		_firstCharOffset = x;
	}

    /**
     * Setter pour _firstCharOffset
     * @param s : nouvelle valeur pour _firstCharOffset sous le forme de String
     * @see LineFragment.java#_firstCharOffset
     */
	public void setFirstCharOffset(String s) {
		try {
            _firstCharOffset = Integer.parseInt(s);
		} catch (Exception e) {
            e.printStackTrace();
		}
	}

    /**
     * Getter pour _lastCharOffset.
     * @return int
     * @see LineFragment.java#_lastCharOffset
     */
	public int getLastCharOffset() {
		return _lastCharOffset;
	}

    /**
     * Setter pour _lastCharOffset
     * @param x : nouvelle valeur pour _lastCharOffset
     * @see LineFragment.java#_lastCharOffset
     */
	public void setLastCharOffset(int x) {
		_lastCharOffset = x;
	}

    /**
     * Setter pour _lastCharOffset
     * @param s : nouvelle valeur pour _lastCharOffset sous le forme de String
     * @see LineFragment.java#_lastCharOffset
     */
	public void setLastCharOffset(String s) {
		try {
            _lastCharOffset = Integer.parseInt(s);
		} catch (Exception e) {
            e.printStackTrace();
		}
	}
}