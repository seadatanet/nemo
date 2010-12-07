package com.ago.io;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * PositionableFileInputStream.java
 * 
 * 
 * Created: Fri Oct 29 13:37:31 1999
 * 
 * @author Gerard BUNEL
 * @version 1.0
 */

public class PositionableFileInputStream extends RandomAccessFile implements
		PositionableStream {

	public PositionableFileInputStream(File file) throws java.io.IOException {
		super(file, "r");
	}

	public long getPosition() throws java.io.IOException {
		return getFilePointer();
	}

	public int next() throws java.io.IOException {
		return read();
	}

	public int remainder() throws java.io.IOException {
		return (int) (length() - getPosition());
	}

	public void setPosition(long pos) throws java.io.IOException {
		seek((long) pos);
	}

	public void setToEnd() throws java.io.IOException {
		setPosition(length());
	}

	public void setToStart() throws java.io.IOException {
		seek(0);
	}

	public void shift(int shift) throws java.io.IOException {
		setPosition(getPosition() + shift);
	}
} // PositionableFileInputStream
