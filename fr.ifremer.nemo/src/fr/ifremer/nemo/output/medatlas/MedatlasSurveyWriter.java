package fr.ifremer.nemo.output.medatlas;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.output.SurveyWriter;
import fr.ifremer.nemo.util.Conversion;

/**
 * Class MedatlasSurveyWriter
 * 
 * Class d'implementation de SurveyWriter permettant de stocker une Campagne au
 * format MEDATLAS
 * 
 * @author Gerard BUNEL
 */
public class MedatlasSurveyWriter extends SurveyWriter {

	public MedatlasSurveyWriter() {
        super();
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
    public Object clone() throws CloneNotSupportedException {
		MedatlasSurveyWriter copy = null;
		copy = (MedatlasSurveyWriter) super.clone();
		return copy;
	}

	/**
	 * 
	 * @param a_Survey
	 *            La campagne a convertir
	 * @param a_Filename
	 *            Nom du fichier dans lequel generer la campagne
	 * @param a_Progress
	 *            Interface permettant au SurveyWriter de notifier de la
	 *            progression
	 * @exception GenerationException
	 */
	@Override
    public void writeSurvey(Survey a_Survey,
                            File a_Filename,
                            Conversion a_Conversion,
                            ProgressNotification a_Progress) throws GenerationException {

		BufferedWriter writer = null;
		try {
			// FAE01 probleme de performances reseau
			writer = new BufferedWriter(new FileWriter(a_Filename));
			super.writeSurvey(a_Survey, writer, a_Conversion, a_Progress);
		} 
        catch (Exception e) {
			throw new GenerationException(e);
		} 
        finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (Exception ex) {
				}
			}
		}
	}

    
    @Override
    public InputStreamReader specificWriteSurvey(Conversion a_Conversion)
                                throws Exception{
        
        // possibité de rajouter des choses spécifiques au context 
        // (en "protected" dans SurveyWriter)
        
        InputStream stream = getClass().getResourceAsStream(a_Conversion.getTemplateName());
        return new InputStreamReader(stream);
    }

} /* End of class: MedatlasSurveyWriter */
