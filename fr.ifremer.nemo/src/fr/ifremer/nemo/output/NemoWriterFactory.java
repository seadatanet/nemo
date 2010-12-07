package fr.ifremer.nemo.output;

import fr.ifremer.nemo.output.medatlas.MedatlasWriter;
import fr.ifremer.nemo.output.odv.OdvWriter;
import fr.ifremer.nemo.util.Conversion;

/**
 * Factory of NemoWriter
 * @author IFREMER
 */
public final class NemoWriterFactory {


	/**
	 * select NemoWriter according to conversion type
	 * @param Conversion
	 * @return NemoWriter selected
	 */
	public static NemoWriter createWriter(final Conversion conversion){
        
        switch (conversion){
            case MEDATLAS : 
                return new MedatlasWriter(); 
            case ODV : 
                return new OdvWriter(); 
        }
        return null;
	}
}
