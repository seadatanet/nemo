package fr.ifremer.nautilus.ws;

import fr.ifremer.nautilus.ws.model.CollectedData;
import fr.ifremer.nautilus.ws.model.Survey;
import fr.ifremer.ws.util.RestClientUtil;

import java.util.List;
import java.util.Map;

public class NautilusSurveyServiceClient implements NautilusSurveyService {

    private final String apiUrl;

    public NautilusSurveyServiceClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public List<CollectedData> getDataTypeFor(String camCrno) {
        return RestClientUtil.queryForList(apiUrl, "/dataType/" + camCrno, CollectedData.class);
    }

    @Override
    public Map<String, String> getProjectName(String cprj) {
        return RestClientUtil.queryForMap(apiUrl, "/projectName/" + cprj);
    }

    @Override
    public Survey getSurvey(String camCrno) {
        return RestClientUtil.queryForObject(apiUrl, "/survey/" + camCrno, Survey.class);
    }
}
