package fr.ifremer.nemo.output;

import java.io.File;
import java.io.IOException;
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
 * Class SurveyWriter
 * Classe abstraite utilisee pour la conversion vers un format donne. Pour chaque
 * format, une classe d'implementation devra etre definie
 * -- remplace l'interface SurveyWriter -- 
 * 
 * @author Gerard BUNEL
 */
public abstract class SurveyWriter implements Cloneable {
    
    protected VelocityContext context;

	public SurveyWriter() {
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
    public Object clone() throws CloneNotSupportedException {
		SurveyWriter copy = null;
		copy = (SurveyWriter) super.clone();
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
	public abstract void writeSurvey(Survey a_Survey, 
                            File a_Filename,
                            Conversion a_Conversion,
                            ProgressNotification a_Progress) throws GenerationException ;

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
		InputStreamReader reader = null;
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, this);
			ve.init();
            
            context = new VelocityContext();
            SizeBasedProgressNotifier notifier = new SizeBasedProgressNotifier(a_Survey.getValuesCount(), 0, a_Progress);
           
			context.put("survey", a_Survey); //$NON-NLS-1$
            context.put("stationModel", StationModel.getSingleton()); //$NON-NLS-1$
			context.put("formatter", Formatter.getSingleton()); //$NON-NLS-1$
			context.put("nemoLists", NemoLists.class.newInstance()); //$NON-NLS-1$
			context.put("configuration", Configuration.getSingleton()); //$NON-NLS-1$
            
			context.put("notifier", notifier); //$NON-NLS-1$
            
            reader = specificWriteSurvey(a_Conversion);
            
			ve.evaluate(context, a_Writer, a_Conversion.toString(), reader);
            context = null;
            ve = null;
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
            // on force le passage du garbadge collector afin de recuperer un maximum de memoire
            //Runtime.getRuntime().gc ();
		}
	}
    
    public abstract InputStreamReader specificWriteSurvey(Conversion a_Conversion)
                                                throws Exception;

} /* End of class: CommonSurveyWriter */
