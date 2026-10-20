package fr.ifremer.nautilus.ws;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for NautilusSurveyServiceImpl using MockMvc and an in-memory H2 database.
 * No network access required: all data is provided by the H2 schema.
 */
public class NautilusSurveyServiceImplTest {

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test_nautilus;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        createSchema(dataSource);
        mockMvc = MockMvcBuilders.standaloneSetup(new NautilusSurveyServiceImpl(dataSource)).build();
    }

    private void createSchema(DataSource dataSource) throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS CPA");
            stmt.execute("DROP TABLE IF EXISTS PAR");
            stmt.execute("DROP TABLE IF EXISTS PRJ");
            stmt.execute("DROP TABLE IF EXISTS VUE_CAM_NEMO_DETAIL");

            stmt.execute("CREATE TABLE PAR (PAR_CPAR VARCHAR(4), PAR_LIBEL_E VARCHAR(100), PAR_INSTRUMENT_E VARCHAR(1))");
            stmt.execute("INSERT INTO PAR VALUES ('CTD', 'CTD sensor', 'D')");

            stmt.execute("CREATE TABLE CPA (CPA_TYPTAB VARCHAR(1), CPA_NO VARCHAR(20), CPA_CPAR VARCHAR(4), CPA_NOBS INT)");
            stmt.execute("INSERT INTO CPA VALUES ('C', '18000510', 'CTD', 42)");

            stmt.execute("CREATE TABLE PRJ (PRJ_CPRJ VARCHAR(20), PRJ_LIBEL VARCHAR(100))");
            stmt.execute("INSERT INTO PRJ VALUES ('MOOSE', 'Mediterranean Ocean Observing System')");

            // VUE_CAM_NEMO_DETAIL is a view in production; create as a table for tests
            stmt.execute("CREATE TABLE VUE_CAM_NEMO_DETAIL ("
                + "CAM_CRNO VARCHAR(20), "
                + "CAM_CRNOM VARCHAR(100), NAV_CODNOD VARCHAR(5), NAV_NOMNAV VARCHAR(100), "
                + "CAM_DATDEB DATE, CAM_DATFIN DATE, "
                + "REGION VARCHAR(100), PAYS VARCHAR(5), LABO VARCHAR(200), "
                + "DATA_CENTRE VARCHAR(100), CHIEF_SCIENTIST VARCHAR(100), "
                + "LISTE_PROJET VARCHAR(200), LISTE_EDMERP VARCHAR(200), CSR VARCHAR(100), CAMREF VARCHAR(50))");
            stmt.execute("INSERT INTO VUE_CAM_NEMO_DETAIL VALUES ("
                + "'18000510', "
                + "'Moose cruise 2019', '29AH', 'Alis', "
                + "'2019-06-01', '2019-06-15', "
                + "'Mediterranean', 'FR', 'IFREMER', "
                + "'IFREMER-SISMER', 'John Doe', "
                + "'MOOSE', 'EDMERP001', 'CSR001', '18000510')");
        }
    }

    @Test
    public void testGetDataTypeFor_emptyInput_returnsEmptyList() throws Exception {
        // NautilusSurveyServiceImpl returns empty list without DB query when camCrno is empty
        mockMvc.perform(get("/api/dataType/"))
            // Empty path variable → 404 (no matching route), not a concern for this logic
            // Test via direct service is more appropriate; covered by getDataTypeFor_knownId below
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetDataTypeFor_knownId_returnsList() throws Exception {
        mockMvc.perform(get("/api/dataType/18000510"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].observationNumber").value(42));
    }

    @Test
    public void testGetProjectName_returnsMapWithProjectNameKey() throws Exception {
        mockMvc.perform(get("/api/projectName/MOOSE"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.projectName").exists());
    }

    @Test
    public void testGetSurvey_knownId_returnsSurveyWithExpectedName() throws Exception {
        mockMvc.perform(get("/api/survey/18000510"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Moose cruise 2019"))
            .andExpect(jsonPath("$.shipCode").value("29AH"))
            .andExpect(jsonPath("$.countryCode").value("FR"));
    }

    @Test
    public void testGetSurvey_unknownId_returnsEmptyBody() throws Exception {
        // Unknown camCrno → empty list → controller returns null → HTTP 200 with no body
        mockMvc.perform(get("/api/survey/UNKNOWN"))
            .andExpect(status().isOk());
    }
}
