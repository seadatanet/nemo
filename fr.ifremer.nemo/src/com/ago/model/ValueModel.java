package com.ago.model;

/* From Jack Version V1.5 on October 21, 1999 at 7:54:49 am */

// Package libraries inclusion
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ValueModel implements PropertyChangeListener, Performer {

	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	private Object valueModel;

	/**
	 * No Description
	 * 
	 * @param value
	 * 
	 */
	public ValueModel(Object value) {
		setValueModel(value);
	}

	/**
	 * PropertyChange support method
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * PropertyChange support method
	 */
	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}

	/**
	 * No Description
	 * 
	 * @param message
	 * @return Class
	 * 
	 */
	public Class getReturnTypeFor(Message message) {
		// "valueModel" est peut etre un Performer, on essaie de lui deleguer le
		// message
		try {
			return ((Performer) valueModel).getReturnTypeFor(message);
		} catch (Exception e) {
            e.printStackTrace();
		}

		try {
			// Ce n'est pas un performer, on utilise l'API reflexion
			java.lang.reflect.Method method = valueModel.getClass().getMethod(
					message.getMethodName(), message.getArgTypes());
			return method.getReturnType();
		} catch (Exception e) {
			return Object.class;
		}
	}

	/**
	 * Read accessor (getter) for attribute valueModel
	 * 
	 * @return Object
	 * @see ValueModel#valueModel
	 */
	public Object getValueModel() {
		return valueModel;
	}

	/**
	 * No Description
	 * 
	 * @return MessageSend
	 * 
	 */
	public MessageSend hookupMessageSend() {
		java.lang.Class[] argTypes = { java.beans.PropertyChangeListener.class };
		java.lang.Object[] args = { this };
		return new MessageSend("addPropertyChangeListener", valueModel,
				argTypes, args);
	}

	/**
	 * No Description
	 * 
	 * 
	 */
	public void hookupValue() {
		try {
			hookupMessageSend().perform();
		} catch (Exception e) {
            e.printStackTrace();
		}

	}

	/**
	 * No Description
	 * 
	 * @return boolean
	 * 
	 */
	public boolean isValueNull() {
		return (valueModel == null);
	}

	/**
	 * No Description
	 * 
	 * @param message
	 * @param args
	 * @return Object
	 * 
	 */
	public Object perform(Message message, Object args[]) {
		// On essaie d'abord d'invoquer la methode sur le "receiver"
		try {
			java.lang.reflect.Method method = getClass().getMethod(
					message.getMethodName(), message.getArgTypes());
			return method.invoke(this, args);
		} catch (Exception e) {
            e.printStackTrace();
		}
		// l'invocation a echoue, Le message est probablement destine a l'objet
		// "valueModel"

		try {
			return message.perform(valueModel, args);
		} catch (Exception e) {
            e.printStackTrace();
		}

		return null;
	}

	/**
	 * action declenchee par le modele lors d'une modifcation de la valeur de la
	 * propriete
	 * 
	 * @param evt
	 *            evenement de changement de valeur de propriete
	 * 
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		firePropertyChange(evt.getPropertyName(), null, null);
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
		setValueModel(null);
	}

	/**
	 * PropertyChange support method
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * No Description
	 * 
	 * @param value
	 * 
	 */
	public void setValueModel(Object value) {
		if (valueModel != value) {
			if (valueModel != null)
				unhookValue();
			Object oldValue = valueModel;
			valueModel = value;
			if (value != null)
				hookupValue();
			firePropertyChange("valueModel", oldValue, value);
		} else {
			firePropertyChange("valueModel", value, value);
		}
	}

	/**
	 * No Description
	 * 
	 * @return MessageSend
	 * 
	 */
	public MessageSend unhookMessageSend() {
		java.lang.Class[] argTypes = { java.beans.PropertyChangeListener.class };
		java.lang.Object[] args = { this };
		return new MessageSend("removePropertyChangeListener", valueModel,
				argTypes, args);
	}

	/**
	 * No Description
	 * 
	 * 
	 */
	public void unhookValue() {
		try {
			unhookMessageSend().perform();
		} catch (Exception e) {
            e.printStackTrace();
		}

	}
} /* End of class: ValueModel */
