package fr.ifremer.nautilus.ws;

import fr.ifremer.nautilus.ws.model.CollectedData;
import fr.ifremer.nautilus.ws.model.Survey;

import java.util.List;
import java.util.Map;

public interface NautilusSurveyService {

    List<CollectedData> getDataTypeFor(String camCrno);

    Map<String, String> getProjectName(String cprj);

    Survey getSurvey(String camCrno);
}
