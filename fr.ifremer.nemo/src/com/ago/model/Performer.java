package com.ago.model;

/* From Jack Version V1.5 on October 21, 1999 at 7:54:49 am */

// Package libraries inclusion
import java.lang.reflect.InvocationTargetException;

// Description of interface: Performer

/**
 * description: none
 */
public interface Performer {

	/**
	 * No Description
	 * 
	 * @param message
	 * @return Class
	 * 
	 */
	Class getReturnTypeFor(Message message);

	/**
	 * No Description
	 * 
	 * @param message
	 * @param args
	 * @return Object
	 * 
	 */
	Object perform(Message message, Object args[])
			throws InvocationTargetException;

} /* End of Interface: Performer */
