package fr.ifremer.nemo.ws;


import fr.ifremer.nemo.ws.model.*;

import java.util.List;

public interface NemoService {

    List<Country> getCountries();

    List<DataCentre> getDataCentres();

    List<DataType> getDataTypes();

    List<Parameter> getParameters();

    List<Region> getRegions();

    List<Ship> getShips();

    List<SurveyDescription> getSurveys();
}
