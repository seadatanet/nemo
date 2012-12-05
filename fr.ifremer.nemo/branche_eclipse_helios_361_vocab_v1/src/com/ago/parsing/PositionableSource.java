package com.ago.parsing;

/**
 * PositionableSource.java
 * 
 * 
 * Created: Thu Nov 25 22:01:47 1999
 * 
 * @author Gerard BUNEL
 * @version 1.0
 */

public interface PositionableSource {

	int getStartPosition();

	int getStopPosition();

	void setSourcePositions(int start, int stop);

	void setStartPosition(int start);

	void setStopPosition(int stop);
} // PositionableSource
