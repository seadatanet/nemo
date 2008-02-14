package fr.ifremer.nemo.odv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.output.SurveyWriter;
import fr.ifremer.nemo.util.SizeBasedProgressNotifier;

/**
 * Class ODVSurveyWriter
 * 
 * Class d'implémentation de SurveyWriter permettant de stocker une Campagne au
 * format ODV (Ocen Data View)
 * 
 * @author Gerard BUNEL
 */
public class ODVSurveyWriter implements Cloneable, SurveyWriter {

	public ODVSurveyWriter() {
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	public Object clone() throws CloneNotSupportedException {
		ODVSurveyWriter copy = null;
		copy = (ODVSurveyWriter) super.clone();
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
	public void writeSurvey(Survey a_Survey, File a_Filename,
			ProgressNotification a_Progress) throws IOException,
			GenerationException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(a_Filename);
			writeSurvey(a_Survey, writer, a_Progress);
		} finally {
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
	 *            Le flux vers lequel ecrire les données
	 * @param a_Progress
	 *            Interface permettant au SurveyWriter de notifier de la
	 *            progression
	 * @exception GenerationException
	 */
	public void writeSurvey(Survey a_Survey, Writer a_Writer,
			ProgressNotification a_Progress) throws GenerationException {
		InputStream stream = null;
		InputStreamReader reader = null;
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
			ve.init();
			VelocityContext context = new VelocityContext();
			context.put("survey", a_Survey);
			context.put("formatter", fr.ifremer.nemo.util.Formatter
					.getSingleton());
			context.put("star", "*");
			context.put("cr", System.getProperty("line.separator"));
			stream = getClass().getResourceAsStream("odv.vtl");
			reader = new InputStreamReader(stream);
			SizeBasedProgressNotifier notifier = new SizeBasedProgressNotifier(
					a_Survey.getValuesCount(), 0, a_Progress);
			context.put("notifier", notifier);
			ve.evaluate(context, a_Writer, "ODV", reader);
		} catch (Exception e) {
			throw new GenerationException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception ex) {
				}
			}
		}
	}

} /* End of class: ODVSurveyWriter */
