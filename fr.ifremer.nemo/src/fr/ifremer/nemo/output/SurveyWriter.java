package fr.ifremer.nemo.output;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.util.Conversion;

/**
 * Interface SurveyWriter
 * 
 * Interface utilisee pour la conversion vers un format donne. Pour chaque
 * format, une classe d'implementation devra etre definie
 * 
 * @author Gerard BUNEL
 */
public interface SurveyWriter {

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
	void writeSurvey(Survey a_Survey, 
                     File a_Filename,
                     Conversion a_Conversion,
                     ProgressNotification a_Progress) throws GenerationException;

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
	void writeSurvey(Survey a_Survey, 
                     Writer a_Writer,
                     Conversion a_Conversion,
                     ProgressNotification a_Progress) throws GenerationException;

} /* End of Interface: SurveyWriter */
