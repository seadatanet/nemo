package fr.ifremer.nemo.output;

import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.output.ProgressNotification;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * Interface SurveyWriter
 *
 * Interface utilisée pour la conversion vers un format donné.
 * Pour chaque format, une classe d'implémentation devra être définie
 *
 * @author	Gerard BUNEL
 */
public interface SurveyWriter {

  /**
   *
   * @param  a_Survey  La campagne a convertir
   * @param  a_Filename  Nom du fichier dans lequel generer la campagne
   * @param  a_Progress  Interface permettant au SurveyWriter de notifier de la progression
   * @exception  IOException
   * @exception  GenerationException
   */
  void writeSurvey ( Survey a_Survey, File a_Filename, ProgressNotification a_Progress) throws IOException, GenerationException;

  /**
   *
   * @param  a_Survey  La campagne a convertir
   * @param  a_Writer  Le flux vers lequel ecrire les données
   * @param  a_Progress  Interface permettant au SurveyWriter de notifier de la progression
   * @exception  GenerationException
   */
  void writeSurvey ( Survey a_Survey, Writer a_Writer, ProgressNotification a_Progress) throws GenerationException;

} /* End of Interface: SurveyWriter */
