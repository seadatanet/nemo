package com.ago.model;

/* From Jack Version V1.5 on October 21, 1999 at 7:54:48 am */

// Package libraries inclusion
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

/**
 * Cette classe est le portage de la classe Smalltalk du meme nom. C'est une
 * sous-classe de Message qui prend comme attribut suppl*mentaire: - receiver :
 * l'objet implantant la methode <methodName> - args : les arguments a utiliser
 * lors de l'invocation de <methodName> sur receiver
 */

public class MessageSend extends Message implements Runnable, ActionListener {

	/**
	 * L'objet sur lequel on doit invoquer la methode <methodName>
	 */
	private Object receiver;

	/**
	 * Les arguments a utiliser lors de l'invocation de la methode <methodName>
	 * sur l'objet <receiver>
	 */
	public Object args[];

	/**
	 * Constructeur du MessageSend
	 * 
	 * @param methodName
	 *            Nom de la methode a invoquer
	 * @param receiver
	 *            L'objet sur lequel doit etre invoquee la methode <methodName>
	 * @param argTypes
	 *            Les types des arguments de la methode <methodName>
	 * @param args
	 *            les arguments a utiliser lors de l'invocation de <methodName>
	 * 
	 */
	public MessageSend(String methodName, Object receiver, Class argTypes[],
			Object args[]) {
		super(methodName, argTypes);
		setReceiver(receiver);
		setArgs(args);
	}

	/**
	 * Constructeur du MessageSend
	 * 
	 * @param message
	 *            un Message
	 * @param argTypes
	 *            Les types des arguments de la methode <methodName>
	 * @param args
	 *            les arguments a utiliser lors de l'invocation de <methodName>
	 * 
	 */
	public MessageSend(Message message, Class argTypes[], Object args[]) {
		super(message.getMethodName(), message.getArgTypes());
		setReceiver(receiver);
		setArgs(args);
	}

	/**
	 * Read accessor (getter) for attribute args
	 * 
	 * @return Object[]
	 * @see MessageSend#args
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * Read accessor (getter) for attribute receiver
	 * 
	 * @return Object
	 * @see MessageSend#receiver
	 */
	public Object getReceiver() {
		return receiver;
	}

	/**
	 * retourne le type du code retour de la methode <methodName> de l'objet
	 * <receiver>
	 * 
	 * @return Class
	 * 
	 */
	public Class getReturnType() {
		return super.getReturnTypeFor(receiver);
	}

	/**
	 * Invocation de la methode l <methodName> sur l'objet <receiver>
	 * 
	 * @return Object
	 * 
	 */
	public Object perform() throws InvocationTargetException {
		return super.perform(receiver, args);
	}

	/**
	 * Runnable responsibility implementation Creation date: (13/11/99 14:01:44)
	 */
	public void run() {
		try {
			perform();
		} catch (Exception e) {
            e.printStackTrace();
		}
	}

	/**
	 * ActionListener responsibility implementation Creation date: (13/11/99
	 * 14:01:44)
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			perform();
		} catch (Exception ex) {
		}
	}

	/**
	 * Write accessor (setter) for attribute args
	 * 
	 * @param x
	 *            new value for args
	 * @see MessageSend#args
	 */
	public void setArgs(Object x[]) {
		args = x;
	}

	/**
	 * Write accessor (setter) for attribute receiver
	 * 
	 * @param x
	 *            new value for receiver
	 * @see MessageSend#receiver
	 */
	public void setReceiver(Object x) {
		receiver = x;
	}

	/**
	 * Surcharge de la methode equivalente dans Object
	 * 
	 * @return String
	 * 
	 */
	public String toString() {
		return "Message: " + methodName + " to receiver: " + receiver;
	}
} /* End of class: MessageSend */
