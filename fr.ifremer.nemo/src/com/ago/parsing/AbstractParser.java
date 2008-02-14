package com.ago.parsing;

import java.text.ParseException;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.Vector;

import com.ago.io.PositionableByteArrayInputStream;
import com.ago.io.PositionableStream;

/**
 * class AbstractParser
 */
public class AbstractParser extends StreamScanner {

	public interface RuleEvaluator {

		boolean evaluate() throws ParseException;
	}

	/**
	 * Stack used to store tokens
	 */
	protected Stack<Object> _stack;

	/**
	 * Used to store source and stack position
	 */
	protected Stack<Integer> _backupStack;

	/**
	 * Default AbstractParser constructor
	 */
	public AbstractParser() {
		super();
	}

	/**
	 * Discard previously saved source position and stack size
	 */
	protected boolean backupDiscard() throws ParseException {
		try {
			_backupStack.pop();
			_backupStack.pop();
			return true;
		} catch (EmptyStackException e) {
			throw new ParseException(e.getMessage(), (int) mark);
		}
	}

	/**
	 * Restore stack size and source position to the values stored on the
	 * _backupStack
	 */
	protected boolean backupRestore() throws ParseException {
		try {
			popStackTo(_backupStack.pop());
			setSourcePosition(_backupStack.pop());
			return false;
		} catch (EmptyStackException e) {
			throw new ParseException(e.getMessage(), (int) mark);
		}
	}

	/**
	 * Save current Stack size and source position on the _backupStack
	 */
	protected void backupSave() throws ParseException {
		_backupStack.push((int) getSourcePosition());
		_backupStack.push(_stack.size());
	}

	/**
	 * Replaces the n element on top of the stack by a Vector containing these n
	 * elements (n = curentSize-oldSize)
	 */
	protected void combineStackTo(int oldSize) throws ParseException {
		int size = _stack.size();
		if (oldSize > size || oldSize < 0)
			throw new ParseException("Invalid size for combining _stack",
					(int) mark);
		Vector<Object> vector = new Vector<Object>();
		try {
			for (int i = 0; i < size - oldSize; i++) {
				vector.insertElementAt(_stack.pop(), 0);
			}
		} catch (EmptyStackException e) {
			throw new ParseException(e.getMessage(), (int) mark);
		}
		_stack.push(vector);
	}

	public void dumpStack() {
		for (int i = 0; i < _stack.size(); i++) {
			System.out.println(_stack.get(i));
		}
	}

	protected boolean expected(char expected) throws ParseException {
		StringBuffer b = new StringBuffer("<-- expected ");
		b.append(expected);
		b.append(", found: " + tokenType + "\"" + token + "\"");
		return notifyAt(b.toString(), mark);
	}

	protected boolean expected(String expected) throws ParseException {
		return notifyAt("<-- expected " + expected + ", found: " + tokenType
				+ " \"" + token + "\"", mark);
	}

	/**
	 * return the current stream position
	 */
	public long getSourcePosition() throws ParseException {
		return mark - 1;
	}

	public Stack<Object> getStack() {
		return _stack;
	}

	public boolean notify(String message) throws ParseException {
		return notifyAt(message, (int) getSourcePosition());
	}

	public boolean notifyAt(String message, int pos) throws ParseException {
		if (!_backupStack.empty()) {
			return false;
		}
		dumpStack();
		throw new ParseException(message, pos);
	}

	public void on(PositionableStream stream) throws ParseException {
		super.on(stream);
		_stack = new Stack<Object>();
		_backupStack = new Stack<Integer>();
		scanToken();
	}

	/**
	 * Parse the string beginning by the rule construct construct corresponds to
	 * a method name
	 */
	public Stack<Object> parse(String s, String construct)
			throws ParseException {
		return parse(new PositionableByteArrayInputStream(s.getBytes()),
				construct);
	}

	/**
	 * Parse the stream beginning by the rule construct construct corresponds to
	 * a method name
	 */
	public Stack<Object> parse(PositionableStream stream, String s)
			throws ParseException {
		on(stream);
		perform(s);
		return _stack;
	}

	/**
	 * return true if current token is of type s. Push token on the _stack. Then
	 * scan next token
	 */
	public boolean peekForType(String s) throws ParseException {
		if (reservedWords.contains(s)) {
			return peekForTypeNoPush(s);
		}
		if (tokenType == s) {
			_stack.push(token);
			scanToken();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * return true if current token is of type s. Does not push token the
	 * _stack. Then scan next token
	 */
	public boolean peekForTypeNoPush(String s) throws ParseException {
		if (tokenType == s) {
			scanToken();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * return true if the current token is of type s and its value is value The
	 * scan next token
	 */
	public boolean peekForValueType(char value) throws ParseException {
		return peekForValueType(CHARACTER, new Character(value));
	}

	/**
	 * return true if the current token is of type s and its value is value The
	 * scan next token
	 */
	public boolean peekForValueType(String s, Object value)
			throws ParseException {
		if (tokenType == s && token.equals(value)) {
			scanToken();
			return true;
		} else {
			return false;
		}
	}

	public boolean popNoTest() throws ParseException {
		_stack.pop();
		return true;
	}

	/**
	 * pop _stack until _stack size is oldSize
	 */
	public boolean popStackTo(int oldSize) throws ParseException {
		if (oldSize > _stack.size()) {
			return false;
		}
		try {
			while (_stack.size() != oldSize) {
				_stack.pop();
			}
		} catch (EmptyStackException e) {
			throw new ParseException(e.getMessage(), (int) mark);
		}
		return true;
	}

	public boolean popTest() throws ParseException {
		if (!((Boolean) _stack.pop()).booleanValue()) {
			unexpectedError();
		}
		return true;
	}

	/**
	 * Replace top of _stack by element
	 */
	public void replaceStackTop(Object element) throws ParseException {
		try {
			_stack.setElementAt(element, _stack.size() - 1);
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), (int) mark);
		}
	}

	/**
	 * Reset file position at pos. then reinitialize scanner
	 */
	protected void setSourcePosition(long pos) throws ParseException {
		try {
			source.setPosition(pos);
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), (int) mark);
		}
		step();
		scanToken();
	}

	public boolean sourcePop() throws ParseException {
		PositionableSource v = (PositionableSource) _stack.pop();
		v.setSourcePositions(((Integer) _stack.pop()).intValue(), mark);
		_stack.push(v);
		return true;
	}

	public boolean sourcePush() throws ParseException {
		_stack.push(new Integer(mark));
		return true;
	}

	protected boolean uncombine() throws ParseException {
		try {
			Vector v = (Vector) _stack.pop();
			for (int i = 0; i < v.size(); i++) {
				_stack.push(v.elementAt(i));
			}
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), mark);
		}
		return true;
	}

	public void unexpectedError() throws ParseException {
		notify("syntax error");
	}
}
