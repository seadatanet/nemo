package fr.ifremer.nemo.odv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.velocity.VelocityContext;

import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.output.SurveyWriter;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.util.Conversion;

/**
 * Class ODVSurveyWriter
 * 
 * Class d'implementation de SurveyWriter permettant de stocker une Campagne au
 * format ODV (Ocen Data View)
 * 
 * @author Gerard BUNEL
 */
public class ODVSurveyWriter extends SurveyWriter {

	public ODVSurveyWriter() {
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
    public Object clone() throws CloneNotSupportedException {
		ODVSurveyWriter copy = null;
		copy = (ODVSurveyWriter) super.clone();
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
	 * @exception IOException
	 * @exception GenerationException
	 */
	@Override
    public void writeSurvey(Survey a_Survey, 
                            File a_Filename,
                            Conversion a_Conversion,
                            ProgressNotification a_Progress) throws GenerationException {
        
		FileWriter writer = null;
		try {
			writer = new FileWriter(a_Filename);
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
					ex.printStackTrace();
				}
			}
		}
	}
    

    @Override
    public InputStreamReader specificWriteSurvey(VelocityContext context,
                                                 Conversion a_Conversion){
        
        // possibité de rajouter des choses spécifiques au context
        
        InputStream stream = getClass().getResourceAsStream(a_Conversion.getTemplate());
        return new InputStreamReader(stream);
    }

} /* End of class: ODVSurveyWriter */
