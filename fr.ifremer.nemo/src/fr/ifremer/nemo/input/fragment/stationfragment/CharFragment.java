package fr.ifremer.nemo.input.fragment.stationfragment;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.input.ModelDescription;
import fr.ifremer.nemo.util.Global;

/**
 * CharFragment with first and last char offet.
 *
 */
public class CharFragment extends Fragment {

	/**
	 * Offset, dans la premiere ligne, du premier caractere de la donnee
	 */
	protected int _firstCharOffset;

	/**
	 * Offset, dans la derniere ligne, du dernier caractere de la donnee. si -1
	 * on lit jusqu'a la fin de ligne
	 */
	protected int _lastCharOffset;

	/**
	 * Indique si la longueur de la derniere ligne est variable
	 */
	protected boolean _variable;

	public CharFragment() {
		super();
	}

	public CharFragment(InputFragment rawFragment) {
		super(rawFragment);
		this.setFirstCharOffset(rawFragment.getFirstCharOffset());
		this.setLastCharOffset(rawFragment.getLastCharOffset());
		this.setVariable(rawFragment.isVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initValues() {
		super.initValues();
		_variable = false;
	}

	
	@Override
	public void writeXMLAttributes(XMLOutputWriter a_Writer) {
		super.writeXMLAttributes(a_Writer);
		a_Writer.putAttribute(ModelDescription.a_variable, isVariable());
	}

	@Override
	public void writeXMLTags(XMLOutputWriter a_Writer) {
		super.writeXMLTags(a_Writer);
		a_Writer.putTag(ModelDescription.node_firstoffset, getFirstCharOffset());
		a_Writer.putTag(ModelDescription.node_lastoffset, getLastCharOffset());
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
		for (int i = _firstLine - 1; i < _lastLine; i++) {
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

			if (end == -1) {
				res += a_Header[i].substring(start);
			} else {
				res += a_Header[i].substring(start, end + 1);
			}
			if (moreThanOneLine) {
				res += Global.RN;
			}
		}

		return res;
	}

	@Override
	public String retreiveInLine(String a_Line) {

		int end = -1;
		int start = _firstCharOffset - 1;

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
				while (continueBoucle && index < a_Header[i].length()) {
					nextChar = a_Header[i].substring(index, index + 1);
					if (index < _lastCharOffset || nextChar.equals(".") //$NON-NLS-1$
							|| nextChar.equals(",") //$NON-NLS-1$
							|| fr.ifremer.nemo.util.Utils.isNumeric(nextChar)) {
						index++;
					} else {
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
		while (continueBoucle && index < a_Line.length()) {
			nextChar = a_Line.substring(index, index + 1);
			if (index < _lastCharOffset || nextChar.equals(".") //$NON-NLS-1$
					|| nextChar.equals(",") //$NON-NLS-1$
					|| fr.ifremer.nemo.util.Utils.isNumeric(nextChar)) {
				index++;
			} else {
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

	@Override
	public String fullRetreiveInLine(String a_Line) {
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
	 * @see Fragment.java#_firstCharOffset
	 */
	public int getFirstCharOffset() {
		return _firstCharOffset;
	}

	/**
	 * Setter pour _firstCharOffset
	 * 
	 * @param x : nouvelle valeur pour _firstCharOffset
	 * @see Fragment.java#_firstCharOffset
	 */
	public void setFirstCharOffset(int x) {
		_firstCharOffset = x;
	}

	/**
	 * Setter pour _firstCharOffset
	 * 
	 * @param s : nouvelle valeur pour _firstCharOffset sous forme de String
	 * @see Fragment.java#_firstCharOffset
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
	 * 
	 * @return int
	 * @see Fragment.java#_lastCharOffset
	 */
	public int getLastCharOffset() {
		return _lastCharOffset;
	}

	/**
	 * Setter pour _lastCharOffset
	 * 
	 * @param x : nouvelle valeur pour _lastCharOffset
	 * @see Fragment.java#_lastCharOffset
	 */
	public void setLastCharOffset(int x) {
		_lastCharOffset = x;
	}

	/**
	 * Setter pour _lastCharOffset
	 * 
	 * @param s : nouvelle valeur pour _lastCharOffset sous forme de String
	 * @see Fragment.java#_lastCharOffset
	 */
	public void setLastCharOffset(String s) {
		try {
			_lastCharOffset = Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter pour _variable.
	 * 
	 * @return boolean
	 * @see Fragment.java#_variable
	 */
	public boolean isVariable() {
		return _variable;
	}

	/**
	 * Setter pour _variable
	 * 
	 * @param x : nouvelle valeur pour _variable
	 * @see Fragment.java#_variable
	 */
	public void setVariable(boolean x) {
		_variable = x;
	}

	/**
	 * Setter pour _variable
	 * 
	 * @param s : nouvelle valeur pour _variable sous forme de String
	 * @see Fragment.java#_variable
	 */
	public void setVariable(String s) {
		try {
			_variable = new Boolean(s).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
