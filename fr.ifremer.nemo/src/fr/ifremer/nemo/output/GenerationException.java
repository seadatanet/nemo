package fr.ifremer.nemo.output;

/**
 * Class GenerationException
 * 
 * 
 * @author Gerard BUNEL
 */
public class GenerationException extends Exception implements Cloneable {
	public GenerationException() {
		super();
	}

	/**
	 * 
	 * @param a_Throwable
	 */
	public GenerationException(Throwable a_Throwable) {
		super(a_Throwable);
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	public Object clone() throws CloneNotSupportedException {
		GenerationException copy = null;
		copy = (GenerationException) super.clone();
		return copy;
	}

} /* End of class: GenerationException */
