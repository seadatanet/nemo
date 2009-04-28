package fr.ifremer.nemo.output;

import fr.ifremer.nemo.medatlas.MedatlasSurveyWriter;
import fr.ifremer.nemo.odv.ODVSurveyWriter;
import fr.ifremer.nemo.util.Conversion;

/**
 * Factory of SurveyWriter
 * @author IFREMER
 */
public final class SurveyWriterFactory {


	/**
	 * select SurveyWriter according to conversion format
	 * @param Conversion
	 * @return SurveyWriter selected
	 */
	public static SurveyWriter createWriter(final Conversion conversion) {
		
        switch (conversion){
            case MEDATLAS : 
                return new MedatlasSurveyWriter(); 
            case ODV : 
                return new ODVSurveyWriter(); 
        }
        return null;
	}
}
