package com.ago.io;

import java.io.ByteArrayInputStream;

/**
 * PositionableByteArrayInputStream.java
 * 
 * 
 * Created: Fri Oct 29 13:37:31 1999
 * 
 * @author Gerard BUNEL
 * @version 1.0
 */

public class PositionableByteArrayInputStream extends ByteArrayInputStream
		implements PositionableStream {

	public PositionableByteArrayInputStream(byte[] buf) {
		super(buf);
	}

	public void close() throws java.io.IOException {
	}

	public long getPosition() throws java.io.IOException {
		return (long) pos;
	}

	public void mark() {
	}

	public boolean markSupported() {
		return false;
	}

	public int next() throws java.io.IOException {
		return super.read();
	}

	public int remainder() throws java.io.IOException {
		return (int) available();
	}

	public void setPosition(long pos) throws java.io.IOException {
		reset();
		skip(pos);
	}

	public void setToEnd() throws java.io.IOException {
		shift(available());
	}

	public void setToStart() throws java.io.IOException {
		reset();
	}

	public void shift(int shift) throws java.io.IOException {
		setPosition(pos + shift);
	}
} // PositionableByteArrayInputStream
