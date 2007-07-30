package com.ago.io;

public interface PositionableStream {

	/**
	 * Close the stream
	 */
	void close() throws java.io.IOException;

	/**
	 * return the current stream position
	 */
	long getPosition() throws java.io.IOException;

	/**
	 * reads the next byte from the stream
	 */
	int next() throws java.io.IOException;

	/**
	 * returns the available number of bytes in the stream
	 */
	int remainder() throws java.io.IOException;

	/**
	 * set the current stream position
	 */
	void setPosition(long pos) throws java.io.IOException;

	/**
	 * Positions the stream at end
	 */
	void setToEnd() throws java.io.IOException;

	/**
	 * Positions the stream at the beginning
	 */
	void setToStart() throws java.io.IOException;

	/**
	 * skips the next shift bytes
	 */
	void shift(int shift) throws java.io.IOException;
}
