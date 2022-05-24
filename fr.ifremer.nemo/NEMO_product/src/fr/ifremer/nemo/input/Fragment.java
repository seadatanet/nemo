package fr.ifremer.nemo.input;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;

import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.util.Global;

/**
 * Class Fragment
 * Description :
 * Represente un fragment du fichier d'entree. Le fragment est definit par une
 * position de debut et de fin. comme illustre ci-dessous | | | v debut | | |
 * XXXXXXXXXXXXXX| | XXXXXXXXXXXXXXXXX| | XXXXXXXXXXXXXXXXX| | XXXXXXXXXXXX| | | ^
 * fin | | |
 * @author Gerard BUNEL
 */
public class Fragment extends AbstractModel
{

	/**
	 * Offset, dans la premiere ligne, du premier caractere de la donnee
	 */
	protected int _firstCharOffset;

	/**
	 * Index de la premiere ligne dans le header ou commence la donnee
	 */
	protected int _firstLine;

	/**
	 * Offset, dans la derniere ligne, du dernier caractere de la donnee. si -1
	 * on lit jusqu'a la fin de ligne
	 */
	protected int _lastCharOffset;

	/**
	 * Index de la ligne dans le header ou finit la donnee. Par defaut egale a
	 * la valeur de firstLine
	 */
	protected int _lastLine;

	/**
	 * Indique si le fragment est sur une ligne ou plusieurs
	 */
	protected boolean _singleLine;

	/**
	 * Indique si la longueur de la derniere ligne est variable
	 */
	protected boolean _variable;

	/**
	 * Indique si il y a une valeur par défault ou non associée à ce paramètre
	 */
	protected boolean _hasDefaultValue;

	/**
	 * Indique si valeur par défault est sélectionné
	 */
	protected boolean _defaultValueSelected;

	/**
	 * Valeur par défault
	 */
	protected String _defaultValue;

	//40216
	private int _colNb=-1;

    public Fragment() {
		_tagName = ModelDescription.nodetitle_default;
		initValues();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void initValues(){
		// _firstCharOffset = 1;
		// _firstLine = 1;
		// _lastCharOffset = 1;
		// _lastLine = 1;
		_singleLine = true;
		_variable = false;
		_hasDefaultValue = false;
		_defaultValueSelected = false;
		_defaultValue = "";
	}

	/**
	 * @see AbstractModel.java#fromElement
	 */
	public static SAXConsumer fromElement(String a_URI,
											String a_LocalName,
											String a_QName,
                                          Attributes a_Attributes){
		Fragment el = new Fragment();
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
        if ((tmp = a_Attributes.getValue(ModelDescription.a_singleline)) != null) {
			setSingleLine(tmp);
		}
        if ((tmp = a_Attributes.getValue(ModelDescription.a_variable)) != null) {
			setVariable(tmp);
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
        if (a_LocalName.equals(ModelDescription.node_firstline)) {            
			Element2Field setter = new Element2Field(this, ModelDescription.setter_firstline);
			return setter;
		}
        if (a_LocalName.equals(ModelDescription.node_lastline)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_lastline);
			return setter;
		}
        if (a_LocalName.equals(ModelDescription.node_firstoffset)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_firstoffset);
			return setter;
		}
        if (a_LocalName.equals(ModelDescription.node_lastoffset)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_lastoffset);
			return setter;
		}
        if (a_LocalName.equals(ModelDescription.node_defaultselected)) {
			_hasDefaultValue = true;
			Element2Field setter = new Element2Field(this, ModelDescription.setter_defaultselected);
			return setter;
		}
        if (a_LocalName.equals(ModelDescription.node_defaultvalue)) {
			_hasDefaultValue = true;
			Element2Field setter = new Element2Field(this, ModelDescription.setter_defaultvalue);
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
		// non utilisé
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void writeXMLBodyOn(XMLOutputWriter a_Writer) {

		a_Writer.putAttribute(ModelDescription.a_singleline, isSingleLine());
		a_Writer.putAttribute(ModelDescription.a_variable, isVariable());
		a_Writer.putTag(ModelDescription.node_firstline, getFirstLine());
		a_Writer.putTag(ModelDescription.node_lastline, getLastLine());
		a_Writer.putTag(ModelDescription.node_firstoffset, getFirstCharOffset());
		a_Writer.putTag(ModelDescription.node_lastoffset, getLastCharOffset());

		if (_hasDefaultValue)
		{
			a_Writer.putTag(ModelDescription.node_defaultselected, isDefaultValueSelected());
			a_Writer.putTag(ModelDescription.node_defaultvalue, getDefaultValue());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public Object clone() throws CloneNotSupportedException {
		Fragment copy = null;
		copy = (Fragment) super.clone();
		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void release() {
		// aucun lien à supprimer
	}


    public String retreive(String[] a_Header) {
        if (_firstLine == 0) {
            if (_lastCharOffset == -1 || isVariable()) {
				return a_Header[0].substring(_firstCharOffset - 1);
			}
			return a_Header[0].substring(_firstCharOffset - 1, _lastCharOffset);
		}

		String res = "";
		boolean moreThanOneLine = _firstLine < _lastLine;
		for (int i = _firstLine - 1; i < _lastLine; i++)
		{
			int start = 0;
			int end = -1;

            if (i == _firstLine - 1) {
				start = _firstCharOffset - 1;
			}
            if (i == _lastLine - 1) {
                if (_lastCharOffset != -1 && !isVariable()) {
					end = _lastCharOffset - 1;
				}
			}

			if (end == -1)
			{
				res += a_Header[i].substring(start);
			} else
			{
				res += a_Header[i].substring(start, end + 1);
			}
			if (moreThanOneLine)
			{
				res += Global.RN;
			}
		}

		return res;
	}

    public String retreiveInLine(String a_Line) {

		int end = -1;

		int start = _firstCharOffset - 1;

		// 40216: guess the column number from the caracter start number (for files with separator)
		String separator = Nemo.getInstance().getDomain().getMedia().getModel().getSeparatorForSplit();
		boolean isUseTableForCsvData = Nemo.getInstance().getDomain().getMedia().getModel().getUseTableForCsvData();

		if (isUseTableForCsvData && separator != null) {
			if (this._colNb == -1 ) {
				int colNb = -1;
				int currentCol = 0;
				int indexSearch = 0;
				String[] split = a_Line.split(separator);
				String foundData = "";

				for (String colData : split) {

					foundData = colData;

					indexSearch = indexSearch + colData.length();
					if (start < indexSearch) {
						colNb = currentCol;
						break;
					}
					currentCol++;
					indexSearch++; // add separator
				}
				this._colNb = colNb;

				// The value has been found AND the line is clearly with a separator.
				return foundData;
			}else {
				return a_Line.split(separator)[_colNb];
			}
		}

    	// Here, the data has no separators ; must retrieve data by character
    	StringWriter stringWriter = new StringWriter();
    	PrintWriter writer = new PrintWriter(stringWriter, true);


    	if (_lastCharOffset != -1 && !isVariable()) {
    		end = _lastCharOffset - 1;
    	}

    	if (end == -1) {
    		writer.print(a_Line.substring(start));
    	} else {
    		writer.print(a_Line.substring(start, end + 1));
    	}
	
		return stringWriter.toString();
    	
	}
    
    public String retreiveVariable(String[] a_Header) {
        if (_firstLine == 0) {
            if (_lastCharOffset == -1 || isVariable()) {
				return a_Header[0].substring(_firstCharOffset - 1);
			}
			return a_Header[0].substring(_firstCharOffset - 1, _lastCharOffset);
		}
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter, true);

        for (int i = _firstLine - 1; i < _lastLine; i++) {
			int start = 0;
			int end = -1;

            if (i == _firstLine - 1) {
				start = _firstCharOffset - 1;
			}
            if (i == _lastLine - 1) {

				int index = _firstCharOffset;
				String nextChar = Global.ZERO;
				boolean continueBoucle = true;
                while (continueBoucle && index < a_Header[i].length()){
					nextChar = a_Header[i].substring(index, index + 1);
					if (index < _lastCharOffset
							|| nextChar.equals(".") //$NON-NLS-1$
							|| nextChar.equals(",") //$NON-NLS-1$
                        || fr.ifremer.nemo.util.Utils.isNumeric(nextChar)){
						index++;
                    }else{
						continueBoucle = false;
					}
				}
				end = index - 1;
			}

            if (end == -1) {
				writer.println(a_Header[i].substring(start));
            } else {
				writer.println(a_Header[i].substring(start, end + 1));
			}
		}

		return stringWriter.toString();
	}

    public String retreiveVariableInLine(String a_Line) {

		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter, true);

		// for (int i = _firstLine - 1; i < _lastLine; i++) {
		// int start = 0;
		int end = -1;

		// if (i == _firstLine - 1) {
		int start = _firstCharOffset - 1;
		// }
		// if (i == _lastLine - 1) {

		int index = _firstCharOffset;
		String nextChar = Global.ZERO;
		boolean continueBoucle = true;
                while (continueBoucle && index < a_Line.length()){
			nextChar = a_Line.substring(index, index + 1);
			if (index < _lastCharOffset
						|| nextChar.equals(".") //$NON-NLS-1$
					|| nextChar.equals(",") //$NON-NLS-1$
                        || fr.ifremer.nemo.util.Utils.isNumeric(nextChar)){
				index++;
                    }else{
				continueBoucle = false;
			}
		}
		end = index - 1;
		// }

            if (end == -1) {
			writer.println(a_Line.substring(start));
            } else {
			writer.println(a_Line.substring(start, end + 1));
		}
		// }

		return stringWriter.toString();
	}

    public String fullRetreiveInLine(String a_Line){
		String fullFrag = Global.EMPTY_STRING;
        if (_lastCharOffset == -1) {
			fullFrag = a_Line.substring(_firstCharOffset - 1);
        } else {
			fullFrag = a_Line.substring(_firstCharOffset - 1, _lastCharOffset);
		}
		return fullFrag;

	}
    
    /**
     * Retrieve data within a line with known separator.
     * 
     * The column number of the searched data is given by this fragment.
     * 
     * @param a_Line - The source line to search data into.
     * @param separator - The line separator.
     * @return the String representing the data.
     * @throws ParseException
     */
	public String retrieveInLineWithSeparator(String a_Line, String separator) throws ParseException {
		// String separator=Nemo.getInstance().getDomain().getMedia().getModel().getSeparator();

		String lineSubstring = a_Line;
		
		if (separator != null) {
			try {
				if (lineSubstring.startsWith(separator)) {
					lineSubstring = lineSubstring.substring(lineSubstring.indexOf(separator) + 1, lineSubstring.length());
				}
				return lineSubstring.split(separator)[this.getColNb()];
			} catch (Exception e) {
				// TODO
				throw new ParseException("error", 0);
			}
		}
		
		// TODO error
		throw new ParseException("error: can not use FreeDecimalDegrees format on files which are not using a separator", 0);
	}

    

	/**
	 * Getter pour _firstCharOffset.
	 * @return int
	 * @see Fragment.java#_firstCharOffset
	 */
    public int getFirstCharOffset() {
		return _firstCharOffset;
	}

	/**
	 * Setter pour _firstCharOffset
     * @param x : nouvelle valeur pour _firstCharOffset
	 * @see Fragment.java#_firstCharOffset
	 */
    public void setFirstCharOffset(int x) {
		_firstCharOffset = x;
	}

	/**
	 * Setter pour _firstCharOffset
     * @param s : nouvelle valeur pour _firstCharOffset sous forme de String
	 * @see Fragment.java#_firstCharOffset
	 */
    public void setFirstCharOffset(String s) {
        try {
			_firstCharOffset = Integer.parseInt(s);
        } catch (Exception e) { e.printStackTrace();
		}
	}

	/**
	 * Getter pour _firstLine.
	 * @return int
	 * @see Fragment.java#_firstLine
	 */
    public int getFirstLine() {
		return _firstLine;
	}

	/**
	 * Setter pour _firstLine
     * @param x : nouvelle valeur pour _firstLine
	 * @see Fragment.java#_firstLine
	 */
    public void setFirstLine(int x) {
		_firstLine = x;
	}

	/**
	 * Setter pour _firstLine
     * @param s : nouvelle valeur pour _firstLine sous forme de String
	 * @see Fragment.java#_firstLine
	 */
    public void setFirstLine(String s) {
        try {
			_firstLine = Integer.parseInt(s);
        } catch (Exception e) { e.printStackTrace();
		}
	}

	/**
	 * Getter pour _lastCharOffset.
	 * @return int
	 * @see Fragment.java#_lastCharOffset
	 */
    public int getLastCharOffset() {
		return _lastCharOffset;
	}

	/**
	 * Setter pour _lastCharOffset
     * @param x : nouvelle valeur pour _lastCharOffset
	 * @see Fragment.java#_lastCharOffset
	 */
    public void setLastCharOffset(int x) {
		_lastCharOffset = x;
	}

	/**
	 * Setter pour _lastCharOffset
     * @param s : nouvelle valeur pour _lastCharOffset sous forme de String
	 * @see Fragment.java#_lastCharOffset
	 */
    public void setLastCharOffset(String s) {
        try {
			_lastCharOffset = Integer.parseInt(s);
        } catch (Exception e) { e.printStackTrace();
		}
	}

	/**
	 * Getter pour _lastLine.
	 * @return int
	 * @see Fragment.java#_lastLine
	 */
    public int getLastLine() {
		return _lastLine;
	}

	/**
	 * Setter pour _lastLine
     * @param x : nouvelle valeur pour _lastLine
	 * @see Fragment.java#_lastLine
	 */
    public void setLastLine(int x) {
		_lastLine = x;
	}

	/**
	 * Setter pour _lastLine
     * @param s : nouvelle valeur pour _lastLine sous forme de String
	 * @see Fragment.java#_lastLine
	 */
    public void setLastLine(String s) {
        try {
			_lastLine = Integer.parseInt(s);
        } catch (Exception e) { e.printStackTrace();
		}
	}

	/**
	 * Getter pour _singleLine.
	 * @return boolean
	 * @see Fragment.java#_singleLine
	 */
    public boolean isSingleLine() {
		return _singleLine;
	}

	/**
	 * Setter pour _singleLine
     * @param x : nouvelle valeur pour _singleLine
	 * @see Fragment.java#_singleLine
	 */
    public void setSingleLine(boolean x) {
		_singleLine = x;
	}

	/**
	 * Setter pour _singleLine
     * @param s : nouvelle valeur pour _singleLine sous forme de String
	 * @see Fragment.java#_singleLine
	 */
    public void setSingleLine(String s) {
        try {
			_singleLine = new Boolean(s).booleanValue();
        } catch (Exception e) { e.printStackTrace();
		}
	}

	/**
	 * Getter pour _variable.
	 * @return boolean
	 * @see Fragment.java#_variable
	 */
    public boolean isVariable() {
		return _variable;
	}

	/**
	 * Setter pour _variable
     * @param x : nouvelle valeur pour _variable
	 * @see Fragment.java#_variable
	 */
    public void setVariable(boolean x) {
		_variable = x;
	}

	/**
	 * Setter pour _variable
     * @param s : nouvelle valeur pour _variable sous forme de String
	 * @see Fragment.java#_variable
	 */
    public void setVariable(String s) {
        try {
			_variable = new Boolean(s).booleanValue();
        } catch (Exception e) { e.printStackTrace();
		}
	}

	/**
	 * Getter pour _defaultValue
	 * @return String
	 * @see Fragment.java#_defaultValue
	 */
  public String getDefaultValue() {
		return _defaultValue;
	}

	/**
	 * Setter pour _defaultValue
     * @param s : nouvelle valeur pour _defaultValue sous forme de String
	 * @see Fragment.java#_defaultValue
	 */
  public void setDefaultValue(String s) {
		_defaultValue = s;
	}

	/**
	 * Getter pour _hasDefaultValue.
	 * @return boolean
	 * @see Fragment.java#_hasDefaultValue
	 */
    public boolean isDefaultValue() {
		return _hasDefaultValue;
	}

	/**
	 * Setter pour _variable
     * @param x : nouvelle valeur pour _variable
	 * @see Fragment.java#_variable
	 */
    public void setHasDefaultValue(boolean x) {
		_hasDefaultValue = x;
	}

	/**
	 * Setter pour _variable
     * @param s : nouvelle valeur pour _variable sous forme de String
	 * @see Fragment.java#_variable
	 */
    public void setHasDefaultValue(String s) {
        try {
			_hasDefaultValue = new Boolean(s).booleanValue();
        } catch (Exception e) { e.printStackTrace();
		}
	}

	/**
	 * Getter pour _defaultValueSelected.
	 * @return boolean
	 * @see Fragment.java#_defaultValueSelected
	 */
    public boolean isDefaultValueSelected() {
		return _defaultValueSelected;
	}

	/**
	 * Setter pour _defaultValueSelected
     * @param x : nouvelle valeur pour _defaultValueSelected
	 * @see Fragment.java#_defaultValueSelected
	 */
    public void setDefaultValueSelected(boolean x) {
		_defaultValueSelected = x;
	}

	/**
	 * Setter pour _defaultValueSelected
     * @param s : nouvelle valeur pour _defaultValueSelected sous forme de String
	 * @see Fragment.java#_defaultValueSelected
	 */
    public void setDefaultValueSelected(String s) {
        try {
			_defaultValueSelected = new Boolean(s).booleanValue();
        } catch (Exception e) { e.printStackTrace();
		}
	}

	/**
	 * 40216
	 */
    public int getColNb() {
		return _colNb;
	}

}