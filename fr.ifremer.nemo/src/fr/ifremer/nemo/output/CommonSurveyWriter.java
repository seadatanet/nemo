package fr.ifremer.nemo.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.SizeBasedProgressNotifier;

/**
 * Class CommonSurveyWriter
 * 
 * Class d'implementation de SurveyWriter permettant de stocker une Campagne
 * 
 * @author Gerard BUNEL
 */
public class CommonSurveyWriter implements Cloneable, SurveyWriter {

	public CommonSurveyWriter() {
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
    public Object clone() throws CloneNotSupportedException {
		CommonSurveyWriter copy = null;
		copy = (CommonSurveyWriter) super.clone();
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
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
	public void writeSurvey(Survey a_Survey, 
                            File a_Filename,
                            Conversion a_Conversion,
                            ProgressNotification a_Progress) throws GenerationException 
   {
		BufferedWriter writer = null;
		try {
			// FAE01 probleme de performances reseau
			writer = new BufferedWriter(new FileWriter(a_Filename));
			writeSurvey(a_Survey, writer, a_Conversion, a_Progress);
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

	/**
	 * 
	 * @param a_Survey
	 *            La campagne a convertir
	 * @param a_Writer
	 *            Le flux vers lequel ecrire les donnees
	 * @param a_Progress
	 *            Interface permettant au SurveyWriter de notifier de la
	 *            progression
	 * @exception GenerationException
	 */
	public void writeSurvey(Survey a_Survey, 
                            Writer a_Writer,
                            Conversion a_Conversion,
                            ProgressNotification a_Progress) throws GenerationException 
    {
		InputStream stream = null;
		InputStreamReader reader = null;
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, this);
			ve.init();
			VelocityContext context = new VelocityContext();
			context.put("survey", a_Survey); //$NON-NLS-1$
            context.put("stationModel", StationModel.getSingleton()); //$NON-NLS-1$
			context.put("formatter", Formatter.getSingleton()); //$NON-NLS-1$
			context.put("nemoLists", NemoLists.class.newInstance()); //$NON-NLS-1$
			context.put("configuration", Configuration.getSingleton()); //$NON-NLS-1$
			stream = getClass().getResourceAsStream(a_Conversion.getTemplate());
			reader = new InputStreamReader(stream);
			SizeBasedProgressNotifier notifier = new SizeBasedProgressNotifier(
					a_Survey.getValuesCount(), 0, a_Progress);
			context.put("notifier", notifier); //$NON-NLS-1$
			ve.evaluate(context, a_Writer, a_Conversion.toString(), reader);
		} 
        catch (Exception e) {
			throw new GenerationException(e);
		} 
        finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception ex) {
				}
			}
		}
	}

} /* End of class: CommonSurveyWriter */
