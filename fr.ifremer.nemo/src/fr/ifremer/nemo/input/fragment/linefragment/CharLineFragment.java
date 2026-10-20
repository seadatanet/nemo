package fr.ifremer.nemo.input.fragment.linefragment;

import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.input.ModelDescription;
import fr.ifremer.nemo.util.Global;

/**
 * CharLineFragment with first and last char offet.
 *
 */
public class CharLineFragment extends LineFragment {

	/**
	 * Offset dans la ligne du premier caractere de la chaine de caracteres
	 */
	protected int _firstCharOffset;

	/**
	 * Offset du dernier caractere de la chaine a lire dans le fichier d'entree.
	 * si -1 on lit jusqu'a la fin de ligne
	 */
	protected int _lastCharOffset;

	public CharLineFragment() {
		super();
	}

	public CharLineFragment(InputLineFragment rawLineFragment) {
		super(rawLineFragment);
		this.setFirstCharOffset(rawLineFragment.getFirstCharOffset());
		this.setLastCharOffset(rawLineFragment.getLastCharOffset());
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
	 * Get a value in a line. If it's a raw string line,
	 * 
	 * @param a_Line
	 * @return String
	 */
	@Override
	public String retreive(String a_Line) {
		String res;
		if (_lastCharOffset == -1) {
			res = a_Line.substring(_firstCharOffset - 1).trim();
		}
		res = a_Line.substring(_firstCharOffset - 1, _lastCharOffset).trim();
		return res;
	}

	/**
	 * 
	 * @param a_Line
	 * @return String
	 */
	@Override
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
	 * Getter pour _firstCharOffset.
	 * 
	 * @return int
	 * @see LineFragment.java#_firstCharOffset
	 */
	public int getFirstCharOffset() {
		return _firstCharOffset;
	}

	/**
	 * Setter pour _firstCharOffset
	 * 
	 * @param x : nouvelle valeur pour _firstCharOffset
	 * @see LineFragment.java#_firstCharOffset
	 */
	public void setFirstCharOffset(int x) {
		_firstCharOffset = x;
	}

	/**
	 * Getter pour _lastCharOffset.
	 * 
	 * @return int
	 * @see LineFragment.java#_lastCharOffset
	 */
	public int getLastCharOffset() {
		return _lastCharOffset;
	}

	/**
	 * Setter pour _lastCharOffset
	 * 
	 * @param x : nouvelle valeur pour _lastCharOffset
	 * @see LineFragment.java#_lastCharOffset
	 */
	public void setLastCharOffset(int x) {
		_lastCharOffset = x;
	}

}
