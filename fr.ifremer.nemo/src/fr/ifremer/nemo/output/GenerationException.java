package fr.ifremer.nemo.output;

/**
 * Class GenerationException
 * 
 * 
 * @author Gerard BUNEL
 */
public class GenerationException extends Exception{

    private static final long serialVersionUID = 8951114939401799198L;

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
    
    public GenerationException(String msg) {
        super(msg);
    }
    
    public GenerationException(String msg, Throwable a_Throwable) {
        super(msg, a_Throwable);
    }

}