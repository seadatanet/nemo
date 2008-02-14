package com.ago.parsing;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Vector;

import com.ago.io.PositionableByteArrayInputStream;
import com.ago.io.PositionableStream;
import com.ago.model.MessageSend;

/**
 * StreamScanner scans a stream picking out syntactic token.s It looks one
 * character ahead. It puts each token found into token and its type into
 * tokenType.
 */
public class StreamScanner {

	public static final String DOIT = "doIt".intern();

	public static final String WORD = "word".intern();

	public static final String NUMBER = "number".intern();

	public static final String QUOTEDSTRING = "quotedString".intern();

	public static final String STRING = "string".intern();

	public static final String CHARACTER = "character".intern();

	public static final String BINARY = "binary".intern();

	public static final String XLETTER = "xLetter".intern();

	public static final String XDIGIT = "xDigit".intern();

	public static final String XDOLLAR = "xDollar".intern();

	public static final String XDOUBLEQUOTE = "xDoubleQuote".intern();

	public static final String XDEFAULT = "xDefault".intern();

	public static final String XDELIMITER = "xDelimiter".intern();

	public static final String XSINGLEQUOTE = "xSingleQuote".intern();

	public static final String XBINARY = "xBinary".intern();

	/**
	 * The stream to be scanned
	 */
	protected PositionableStream source;

	/**
	 * one character ahead (of the current token)
	 */
	protected int hereChar;

	/**
	 * current token value
	 */
	protected Object token;

	/**
	 * current token type
	 */
	protected String tokenType;

	/**
	 * Position of the beginning of the current token
	 */
	protected int mark;

	/**
	 * methods to invoke
	 */
	protected String[] typeTable = new String[255];

	/**
	 * Array of reserved words. None is define by default. see xLetter()
	 */
	protected Vector reservedWords = new Vector();

	public StreamScanner() {
		initScanner();
	}

	/**
	 * Answer a sequence of String for aString, one substring per token. This is
	 * for tokenized comparison of code.
	 */
	public Vector<String> breakIntoTokens(String aString) throws ParseException {
		Vector<String> strm = new Vector<String>();
		if (aString.trim().length() == 0) {
			/* a common case where we can avoid overhead */
			return strm;
		}
		scan(aString);
		while (tokenType != DOIT) {
			strm.add((String) token);
			scanToken();
		}
		return strm;
	}

	/**
	 * Insert the method's description here. Creation date: (22/03/2000
	 * 17:38:24)
	 * 
	 * @return java.lang.String[]
	 */
	public Vector getReservedWords() {
		return reservedWords;
	}

	/*
	 * StreamScanner methodsFor: 'initialize-release'
	 */

	protected void initScanner() {
		initTypeTable();
	}

	protected void initTypeTable() {

		for (int i = 0; i < typeTable.length; i++) {
			typeTable[i] = XDEFAULT;
		}

		String separators = " \r\n\t";
		for (int i = 0; i < separators.length(); i++) {
			typeTable[(int) separators.charAt(i)] = XDELIMITER;
		}
		String digits = "0123456789";
		for (int i = 0; i < digits.length(); i++) {
			typeTable[(int) digits.charAt(i)] = XDIGIT;
		}
		String letters = "abcdefghijklmnopqrstuvwxytzABCDEFGHIJKLMNOPQRSTUVWXYTZ_";
		for (int i = 0; i < letters.length(); i++) {
			typeTable[(int) letters.charAt(i)] = XLETTER;
		}
		String binaries = "!%&*+-/<=>?@\\~";
		for (int i = 0; i < binaries.length(); i++) {
			typeTable[(int) binaries.charAt(i)] = XBINARY;
		}

		// Other multi-character tokens
		typeTable[(int) '"'] = XDOUBLEQUOTE;
		typeTable[(int) '\''] = XSINGLEQUOTE;
		typeTable[(int) '#'] = CHARACTER;
		typeTable[(int) '('] = CHARACTER;
		typeTable[(int) ')'] = CHARACTER;
		typeTable[(int) '$'] = CHARACTER;
		typeTable[(int) ','] = CHARACTER;
		typeTable[(int) '.'] = CHARACTER;
		typeTable[(int) ':'] = CHARACTER;
		typeTable[(int) ';'] = CHARACTER;
		typeTable[(int) '['] = CHARACTER;
		typeTable[(int) ']'] = CHARACTER;
		typeTable[(int) '{'] = CHARACTER;
		typeTable[(int) '}'] = CHARACTER;
		typeTable[(int) '^'] = CHARACTER;
		typeTable[(int) '|'] = CHARACTER;
	}

	/**
	 * Bind the input stream, fill the character buffer
	 */
	public void on(PositionableStream inputStream) throws ParseException {
		source = inputStream;
		try {
			hereChar = source.next();
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), 0);
		}
	}

	protected void perform(String selector) throws ParseException {
		MessageSend m = new MessageSend(selector, this, new Class[0],
				new Object[0]);
		try {
			m.perform();
		} catch (Exception e) {
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ex;
				ex = (InvocationTargetException) e;
				if (ex.getTargetException() instanceof ParseException) {
					throw (ParseException) ex.getTargetException();
				} else {
					throw new ParseException(e.getMessage(), mark);
				}
			} else {
				throw new ParseException(e.getMessage(), mark);
			}
		}
	}

	/**
	 * Bind the input stream, fill the character buffers and first token buffers
	 */
	protected void scan(String aString) throws ParseException {
		this.on(new PositionableByteArrayInputStream(aString.getBytes()));
		// this.scanToken();
	}

	/**
	 * Bind the input stream, fill the character buffers and first token buffers
	 */
	protected void scan(PositionableStream inputStream) throws ParseException {
		this.on(inputStream);
		this.scanToken();
	}

	/**
	 * Answer with an Array of strings which are the identifiers in the input
	 * string.
	 */
	public String[] scanFieldNames(String aString) throws ParseException {
		Vector<String> strm = new Vector<String>();
		if (aString.trim().length() == 0) {
			/* a common case where we can avoid overhead */
			return new String[0];
		}
		scan(aString);
		while (tokenType != DOIT) {
			if (tokenType == WORD) {
				strm.add((String) token);
			}
			scanToken();
		}
		String[] fields = new String[strm.size()];
		strm.copyInto(fields);
		return fields;
	}

	/**
	 * Answer an Array of the character positions at which aSymbol occurs as a
	 * word token in aString. Don't count occurrences inside literals
	 */
	public int[] scanPositionsFor(String aSymbol, String inString)
			throws ParseException {
		Vector<Integer> positions = new Vector<Integer>();
		scan(inString);
		while (tokenType != DOIT) {
			if ((tokenType == WORD) && (aSymbol.compareTo((String) token) == 0)) {
				positions.add(mark);
			}
			scanToken();
		}
		int[] pos = new int[positions.size()];
		for (int i = 0; i < positions.size(); i++) {
			pos[i] = ((Integer) positions.elementAt(i)).intValue();
		}
		return pos;
	}

	/*
	 * Scans the stream for the next token
	 */
	public Object scanToken() throws ParseException {
		if (hereChar < 0) {
			tokenType = DOIT;
			token = null;
			return token;
		}
		while ((tokenType = typeTable[hereChar]) == XDELIMITER) {
			// Skip delimiters fast, there almost always is one.
			// open code step for speed
			try {
				hereChar = source.next();
			} catch (Exception e) {
				throw new ParseException(e.getMessage(), mark);
			}
			if (hereChar < 0) {
				break;
			}
		}
		try {
			mark = (int) source.getPosition() - 1;
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), mark);
		}
		if (hereChar < 0) {
			tokenType = DOIT;
			token = null;
			return token;
		}
		if (tokenType.charAt(0) == 'x') {
			// x as first letter
			this.perform(tokenType); // means perform to compute token & type
		} else {
			// else just return the character
			token = new Character((char) hereChar);
			try {
				hereChar = source.next();
			} catch (Exception e) {
				throw new ParseException(e.getMessage(), mark);
			}
		}
		return token;
	}

	/**
	 * Answer with an Array which has been tokenized as though the input text
	 * had appeared between #( and ) in a Smalltalk literal expression."
	 */
	public Object scanTokens(String aString) throws ParseException {
		scan(aString);
		return token;
	}

	public int step() throws ParseException {
		int c;
		c = hereChar;
		try {
			hereChar = source.next();
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), mark);
		}
		return c;
	}

	/*
	 * StreamScanner methodsFor: 'multi-character scans'
	 */

	public void xBinary() throws ParseException {
		int c;
		StringBuffer buff = new StringBuffer();
		tokenType = BINARY;
		c = hereChar;
		try {
			hereChar = source.next();
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), mark);
		}
		if (hereChar > 0 && (typeTable[hereChar] == XBINARY) && hereChar != '-') {
			buff.append((char) c);
			buff.append((char) this.step());
			token = buff.toString();
		} else {
			buff.append((char) c);
			token = buff.toString();
		}
	}

	public void xDefault() throws ParseException {
		throw new ParseException("Unknown character: " + (char) hereChar
				+ " ASCII: " + hereChar, 0);
	}

	public void xDelimiter() throws ParseException {
		// ignore blanks etc.
		scanToken();
	}

	public void xDigit() throws ParseException {
		// form a number

		StringBuffer buff = new StringBuffer();
		tokenType = NUMBER;
		buff.append((char) hereChar);
		try {
			hereChar = source.next();
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), mark);
		}
		while (hereChar > 0 && (typeTable[hereChar] == XDIGIT)) {
			buff.append((char) hereChar);
			try {
				hereChar = source.next();
			} catch (Exception e) {
				throw new ParseException(e.getMessage(), mark);
			}
		}
		token = buff.toString();
	}

	public void xDollar() throws ParseException {
		// form a Character literal

		try {
			token = new Character((char) source.next());
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), mark);
		}
		try {
			hereChar = source.next(); // refill buffer
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), mark);
		}
		tokenType = CHARACTER;
	}

	public void xDoubleQuote() throws ParseException {
		StringBuffer buffer = new StringBuffer();
		try {
			hereChar = source.next();
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), mark);
		}
		while (hereChar > 0 && typeTable[hereChar] != XDOUBLEQUOTE) {
			buffer.append((char) hereChar);
			try {
				hereChar = source.next();
			} catch (Exception e) {
				throw new ParseException(e.getMessage(), mark);
			}
		}
		token = buffer.toString();
		tokenType = QUOTEDSTRING;
		step();
	}

	public void xLetter() throws ParseException {
		StringBuffer buffer = new StringBuffer();
		buffer.append((char) hereChar);
		try {
			hereChar = source.next();
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), mark);
		}
		while (hereChar > 0
				&& (typeTable[hereChar] == XLETTER || typeTable[hereChar] == XDIGIT)) {
			buffer.append((char) hereChar);
			try {
				hereChar = source.next();
			} catch (Exception e) {
				throw new ParseException(e.getMessage(), mark);
			}
		}
		token = buffer.toString();
		if (reservedWords.contains(token)) {
			tokenType = ((String) token).intern();
		} else {
			tokenType = WORD;
		}
	}

	protected void xSingleQuote() throws ParseException {
		StringBuffer buffer = new StringBuffer();
		try {
			hereChar = source.next();
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), mark);
		}
		while (hereChar > 0 && typeTable[hereChar] != XSINGLEQUOTE) {
			buffer.append((char) hereChar);
			try {
				hereChar = source.next();
			} catch (Exception e) {
				throw new ParseException(e.getMessage(), mark);
			}
		}
		token = buffer.toString();
		tokenType = STRING;
		step();
	}
}
