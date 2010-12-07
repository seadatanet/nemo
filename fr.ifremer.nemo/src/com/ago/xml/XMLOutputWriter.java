package com.ago.xml;

import java.io.OutputStream;
import java.io.PrintWriter;

public class XMLOutputWriter {

	protected PrintWriter _writer = null;

	protected Element _currentTag = null;

	protected String _indent = "";

	protected static String _indentElement = "  ";

	protected class Element {
		/**
		 * Tag name
		 */
		protected String _name;

		/*
		 * Whether or not the element has attributes
		 */
		protected boolean _hasAttributes = false;

		/*
		 * current subelement
		 */
		protected Element _subElement = null;

		/*
		 * parent element
		 */
		protected Element _parent = null;

		/*
		 * Element constructor
		 * 
		 * @param a_Name Element name
		 */
		protected Element(String a_Name) {
			_parent = _currentTag;
			if (_parent != null) {
				if (_parent._subElement == null) {
					_writer.print('>');
				}
				_parent._subElement = this;
				_indent = _indent + _indentElement;
				_writer.print('\n');
				_writer.print(_indent);
			}
			_name = a_Name;
			_writer.print("<");
			_writer.print(a_Name);
			_writer.flush();
			_currentTag = this;
		}

		protected Element(String a_Name, String a_CData) {
			_parent = _currentTag;
			if (_parent != null) {
				if (_parent._subElement == null) {
					_writer.print('>');
				}
				_indent = _indent + _indentElement;
				_parent._subElement = this;
				_writer.print('\n');
				_writer.print(_indent);
			}
			_name = a_Name;
			_writer.print("<");
			_writer.print(a_Name);
			_writer.print(">");
			_writer.print(normalizedString(a_CData));
			_writer.print("</");
			_writer.print(a_Name);
			_writer.print(">");
			_writer.flush();
			unindent();
		}

		protected Element(String a_Name, String a_CData, boolean a_IsCDATA) {
			if (!a_IsCDATA) {
				_parent = _currentTag;
				if (_parent != null) {
					if (_parent._subElement == null) {
						_writer.print('>');
					}
					_indent = _indent + _indentElement;
					_parent._subElement = this;
					_writer.print('\n');
					_writer.print(_indent);
				}
				_name = a_Name;
				_writer.print("<");
				_writer.print(a_Name);
				_writer.print(">");
				_writer.print(normalizedString(a_CData));
				_writer.print("</");
				_writer.print(a_Name);
				_writer.print(">");
				_writer.flush();
				unindent();
			} else {
				_parent = _currentTag;
				if (_parent != null) {
					if (_parent._subElement == null) {
						_writer.print('>');
					}
					_indent = _indent + _indentElement;
					_parent._subElement = this;
					_writer.print('\n');
					_writer.print(_indent);
				}
				_name = a_Name;
				_writer.print("<");
				_writer.print(a_Name);
				_writer.print("><![CDATA[");
				_writer.print(a_CData);
				_writer.print("]]></");
				_writer.print(a_Name);
				_writer.print(">");
				_writer.flush();
				unindent();
			}
		}

		protected void close() {
			if (_subElement != null) {
				_writer.print('\n');
				_writer.print(_indent);
				_writer.print("</");
				_writer.print(_name);
				_writer.print('>');
			} else {
				_writer.write("/>");
			}
			_writer.flush();
			unindent();
			_currentTag = _parent;
		}

		protected void close(String a_Value) {
			if (_subElement != null) {
				_writer.print('\n');
				_writer.print(_indent + _indentElement);
				_writer.print(normalizedString(a_Value));
				_writer.print('\n');
				_writer.print(_indent);
				_writer.print("</");
				_writer.print(_name);
				_writer.print('>');
			} else {
				_writer.write(">");
				_writer.print(normalizedString(a_Value));
				_writer.print("</");
				_writer.print(_name);
				_writer.print('>');
			}
			_writer.flush();
			unindent();
			_currentTag = _parent;
		}

		public void putAttribute(String a_Name, String a_Value) {
			_hasAttributes = true;
			_writer.print(' ');
			_writer.print(a_Name);
			_writer.print("=\"");
			_writer.print(normalizedString(a_Value));
			_writer.print("\"");
		}
	}

	/*
	 * XMLOutputWriter default constructor
	 * 
	 */
	public XMLOutputWriter() {
	}

	/*
	 * XMLOutputWriter constructor
	 * 
	 * @param a_Stream OutputStream on which to write
	 */
	public XMLOutputWriter(OutputStream a_Stream) {
		_writer = new PrintWriter(a_Stream);
	}

	/*
	 * XMLOutputWriter constructor
	 * 
	 * @param a_Stream OutputStream on which to write
	 */
	public XMLOutputWriter(PrintWriter a_Writer) {
		_writer = a_Writer;
	}

	/*
	 * XMLOutputWriter constructor
	 * 
	 * @param a_Stream OutputStream on which to write @param a_TagName tag name
	 * of the root element
	 */
	public XMLOutputWriter(OutputStream a_Stream, String a_TagName) {
		_writer = new PrintWriter(a_Stream);
		openTag(a_TagName);
	}

	/*
	 * XMLOutputWriter constructor
	 * 
	 * @param a_Stream OutputStream on which to write @param a_TagName tag name
	 * of the root element
	 */
	public XMLOutputWriter(PrintWriter a_Writer, String a_TagName) {
		_writer = a_Writer;
		openTag(a_TagName);
	}

	public void setOutputStream(OutputStream a_Stream) {
		if (_writer != null) {
			try {
				_writer.close();
			} catch (Exception e) {
                e.printStackTrace();
			}
		}
		_writer = new PrintWriter(a_Stream);
	}

	public static String normalizedString(String a_String) {
		StringBuffer str = new StringBuffer();

		int len = (a_String != null) ? a_String.length() : 0;
		for (int i = 0; i < len; i++) {
			char ch = a_String.charAt(i);
			switch (ch) {
			case '<': {
				str.append("&lt;");
				break;
			}
			case '>': {
				str.append("&gt;");
				break;
			}
			case '&': {
				str.append("&amp;");
				break;
			}
			case '"': {
				str.append("&quot;");
				break;
			}
			case 0x00: {
				break;
			}
			case '\r':
			case '\n':
			case '\t': {
				str.append("&#");
				str.append(Integer.toString(ch));
				str.append(';');
				break;
			}
			default: {
				if (ch < 0x0020 || ch > 0x007F) {
					str.append("&#");
					str.append(Integer.toString(ch));
					str.append(';');
					break;
				} else {
					str.append(ch);
				}
			}
			}
		}
		return str.toString();
	}

	public void unindent() {
		int index = _indent.lastIndexOf(_indentElement);
		if (index >= 0) {
			_indent = _indent.substring(0, index);
		}
	}

	public void close() {
		closeTag();
		try {
			_writer.close();
		} catch (Exception e) {
            e.printStackTrace();
		}
	}

	public void closeTag() {
		_currentTag.close();
	}

	public void closeTag(String a_Value) {
		_currentTag.close(a_Value);
	}

	public void closeTag(int a_Value) {
		_currentTag.close(Integer.toString(a_Value));
	}

	public void closeTag(long a_Value) {
		_currentTag.close(Long.toString(a_Value));
	}

	public void closeTag(byte a_Value) {
		_currentTag.close(Byte.toString(a_Value));
	}

	public void closeTag(char a_Value) {
		_currentTag.close(new Character(a_Value).toString());
	}

	public void closeTag(float a_Value) {
		_currentTag.close(Float.toString(a_Value));
	}

	public void closeTag(double a_Value) {
		_currentTag.close(Double.toString(a_Value));
	}

	public void closeTag(boolean a_Value) {
		_currentTag.close(new Boolean(a_Value).toString());
	}

	public void putAttribute(String a_Name, String a_Value) {
		_currentTag.putAttribute(a_Name, a_Value);
	}

	public void putAttribute(String a_Name, int a_Value) {
		_currentTag.putAttribute(a_Name, Integer.toString(a_Value));
	}

	public void putAttribute(String a_Name, long a_Value) {
		_currentTag.putAttribute(a_Name, Long.toString(a_Value));
	}

	public void putAttribute(String a_Name, byte a_Value) {
		_currentTag.putAttribute(a_Name, Byte.toString(a_Value));
	}

	public void putAttribute(String a_Name, char a_Value) {
		_currentTag.putAttribute(a_Name, new Character(a_Value).toString());
	}

	public void putAttribute(String a_Name, float a_Value) {
		_currentTag.putAttribute(a_Name, Float.toString(a_Value));
	}

	public void putAttribute(String a_Name, double a_Value) {
		_currentTag.putAttribute(a_Name, Double.toString(a_Value));
	}

	public void putAttribute(String a_Name, boolean a_Value) {
		_currentTag.putAttribute(a_Name, new Boolean(a_Value).toString());
	}

	public void openTag(String a_Name) {
		_currentTag = new Element(a_Name);
	}

	public void putTag(String a_Name, String a_Value) {
		new Element(a_Name, a_Value);
	}

	public void putTag(String a_Name, int a_Value) {
		new Element(a_Name, Integer.toString(a_Value));
	}

	public void putTag(String a_Name, long a_Value) {
		new Element(a_Name, Long.toString(a_Value));
	}

	public void putTag(String a_Name, byte a_Value) {
		new Element(a_Name, Byte.toString(a_Value));
	}

	public void putTag(String a_Name, char a_Value) {
		new Element(a_Name, new Character(a_Value).toString());
	}

	public void putTag(String a_Name, float a_Value) {
		new Element(a_Name, Float.toString(a_Value));
	}

	public void putTag(String a_Name, double a_Value) {
		new Element(a_Name, Double.toString(a_Value));
	}

	public void putTag(String a_Name, boolean a_Value) {
		new Element(a_Name, new Boolean(a_Value).toString());
	}

	public void putCDATATag(String a_Name, String a_Value) {
		new Element(a_Name, a_Value, true);
	}

	public static void main(String[] args) {
		XMLOutputWriter writer = new XMLOutputWriter(System.out);
		writer.openTag("TEST");
		writer.putAttribute("TIMESTAMP", System.currentTimeMillis());
		writer.putTag("TEST2", 'A');
		writer.openTag("TEST3");
		writer.putAttribute("SYS", 1);
		writer.closeTag();
		writer.openTag("TEST4");
		writer.putAttribute("BOOL", true);
		writer.closeTag(1.2);
		writer.closeTag();
	}
}
