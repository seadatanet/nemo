package fr.ifremer.nemo.ws;

import fr.ifremer.nemo.ws.model.*;
import fr.ifremer.ws.util.RestClientUtil;

import java.util.List;

public class NemoServiceClient implements NemoService {

    private final String apiUrl;

    public NemoServiceClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public List<Country> getCountries() {
        return RestClientUtil.queryForList(apiUrl, "/countries", Country.class);

    }

    @Override
    public List<DataCentre> getDataCentres() {
        return RestClientUtil.queryForList(apiUrl, "/dataCentres", DataCentre.class);
    }

    @Override
    public List<DataType> getDataTypes() {
        return RestClientUtil.queryForList(apiUrl, "/dataTypes", DataType.class);
    }

    @Override
    public List<Parameter> getParameters() {
        return RestClientUtil.queryForList(apiUrl, "/parameters", Parameter.class);
    }

    @Override
    public List<Region> getRegions() {
        return RestClientUtil.queryForList(apiUrl, "/regions", Region.class);
    }

    @Override
    public List<Ship> getShips() {
        return RestClientUtil.queryForList(apiUrl, "/ships", Ship.class);
    }

    @Override
    public List<SurveyDescription> getSurveys() {
        return RestClientUtil.queryForList(apiUrl, "/surveys", SurveyDescription.class);
    }
}
