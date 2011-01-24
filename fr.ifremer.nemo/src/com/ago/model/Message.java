package com.ago.model;

/* From Jack Version V1.5 on October 21, 1999 at 7:54:48 am */

// Package libraries inclusion
import java.lang.reflect.InvocationTargetException;

/**
 * Cette classe est le portage en Java de la classe Smalltalk du meme nom. Elle
 * permet, par utilisation de l'API reflection, d'invoquer une methode sur un
 * objet sans connaitre a priori le type de cet objet. ses variables d'instance
 * sont - methodName : Le nom de la methode a invoquer - argTypes : liste des
 * types des arguments de la methode
 */

public class Message extends Object {

	/**
	 * Nom de la methode a invoquer
	 */
	protected String methodName;

	/**
	 * Types des arguments de la methode a invoquer
	 */
	protected Class argTypes[];

	/**
	 * Constructeur du Message. Les arguments correspondent aux variables
	 * d'instance a initialiser
	 * 
	 * @param methodName
	 *            le nom de la methode a invoquer
	 * @param argTypes
	 *            les types arguments de la methode <methodName>
	 * 
	 */
	public Message(String methodName, Class argTypes[]) {
		setMethodName(methodName);
		setArgTypes(argTypes);
	}

	/**
	 * Read accessor (getter) for attribute argTypes
	 * 
	 * @return Class[]
	 * @see Message#argTypes
	 */
	public Class[] getArgTypes() {
		return argTypes;
	}

	/**
	 * Read accessor (getter) for attribute methodName
	 * 
	 * @return String
	 * @see Message#methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Methode permettant d'obtenir le type du code retour de la methode a
	 * invoquer
	 * 
	 * @param receiver
	 *            L'objet implantant la methode <methodName>
	 * @return Class
	 * 
	 */
	public Class getReturnTypeFor(Object receiver) {
		// "receiver" est peut etre un Performer, on essaie de lui deleguer le
		// message
		try {
			return ((Performer) receiver).getReturnTypeFor(this);
		} catch (Exception e) {
            e.printStackTrace();
		}
		try {
			// Ce n'est pas un performer, on utilise l'API reflexion
			java.lang.reflect.Method method = receiver.getClass().getMethod(
					getMethodName(), getArgTypes());
			return method.getReturnType();
		} catch (Exception e) {
			return Object.class;
		}
	}

	/**
	 * Methode d'invocation de la methode <methodName>
	 * 
	 * @param receiver
	 *            L'objet sur lequel doit etre invoquee la methode <methodName>
	 * @param args
	 *            Les arguments a utiliser lors de l'invocation de <methodName>
	 * @return Object
	 * 
	 */
	public Object perform(Object receiver, Object args[])
			throws InvocationTargetException {
		try {
			// C'est peut etre un Performer, on essaie
			return ((Performer) receiver).perform(this, args);
		} catch (ClassCastException e) {
		} catch (InvocationTargetException ite) {
			throw ite;
		}
		// Le cast a echoue, On essaie par l'API reflexion
		try {
			java.lang.reflect.Method method = receiver.getClass().getMethod(
					methodName, argTypes);
			return method.invoke(receiver, args);
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
			throw ite;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Write accessor (setter) for attribute argTypes
	 * 
	 * @param x
	 *            new value for argTypes
	 * @see Message#argTypes
	 */
	public void setArgTypes(Class x[]) {
		argTypes = x;
	}

	/**
	 * Write accessor (setter) for attribute methodName
	 * 
	 * @param x
	 *            new value for methodName
	 * @see Message#methodName
	 */
	public void setMethodName(String x) {
		methodName = x;
	}

	/**
	 * surcharge de la methode equivalente dans Object
	 * 
	 * @return String
	 * 
	 */
	public String toString() {
		return "Message: " + methodName;
	}
} /* End of class: Message */
