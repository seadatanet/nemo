package fr.ifremer.nautilus.ws;

import org.junit.jupiter.api.Test;

public class NautilusSurveyServiceTest {

	private static final String TEST_URL = "https://wsnautilus.ifremer.fr/api";

	private static final NautilusSurveyService nautilusService = new NautilusSurveyServiceClient(TEST_URL);

	@Test
	public void testGetDataTypeFor() {
		nautilusService.getDataTypeFor("18000510");
	}

	@Test
	public void testGetProjectName() {
		nautilusService.getProjectName("TEST");
	}

	@Test
	public void testGetSurvey() {
		nautilusService.getSurvey("18000510");
	}
}
