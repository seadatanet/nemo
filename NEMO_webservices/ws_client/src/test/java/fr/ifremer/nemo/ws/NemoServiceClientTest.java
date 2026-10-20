package fr.ifremer.nemo.ws;

import fr.ifremer.nemo.ws.model.Country;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class NemoServiceClientTest {

    private static final String TEST_URL = "https://wsnemo.ifremer.fr/api";

    private static final NemoService nemoService = new NemoServiceClient(TEST_URL);

    @Test
    public void testGetCountries() {
        Map<String, Country> countriesByCode = new HashMap<>();
        for (Country country : nemoService.getCountries()) {
            System.out.println(country.getCode() + "\t" + country.getName());
            countriesByCode.put(country.getCode(), country);
        }
        Assertions.assertFalse(countriesByCode.isEmpty());
        Assertions.assertTrue(countriesByCode.containsKey("35"));
        Assertions.assertTrue(countriesByCode.get("35").getName().equals("FRANCE"));
    }

    @Test
    public void testGetDataCentres() {
        nemoService.getDataCentres();
    }

    @Test
    public void testGetDataTypes() {
        nemoService.getDataTypes();
    }

    @Test
    public void testGetParameters() {
        nemoService.getParameters();
    }

    @Test
    public void testGetRegions() {
        nemoService.getRegions();
    }

    @Test
    public void testGetShips() {
        nemoService.getShips();
    }

    @Test
    public void testGetSurveys() {
        nemoService.getSurveys();
    }

}
