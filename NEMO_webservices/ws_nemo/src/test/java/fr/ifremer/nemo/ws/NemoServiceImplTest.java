package fr.ifremer.nemo.ws;

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
 * Unit tests for NemoServiceImpl using MockMvc and an in-memory H2 database.
 * No network access required: all data is provided by the H2 schema.
 */
public class NemoServiceImplTest {

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test_nemo;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        createSchema(dataSource);
        mockMvc = MockMvcBuilders.standaloneSetup(new NemoServiceImpl(dataSource)).build();
    }

    private void createSchema(DataSource dataSource) throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS PAY");
            stmt.execute("CREATE TABLE PAY (PAY_CPAYS VARCHAR(5), PAY_NOMPAY_E VARCHAR(100))");
            stmt.execute("INSERT INTO PAY VALUES ('FR', 'France')");
            stmt.execute("INSERT INTO PAY VALUES ('GB', 'United Kingdom')");

            stmt.execute("DROP TABLE IF EXISTS NAVIRE");
            stmt.execute("CREATE TABLE NAVIRE (CODE_NAVIRE VARCHAR(5), NOM_NAV VARCHAR(100))");
            stmt.execute("INSERT INTO NAVIRE VALUES ('29AH', 'Alis')");

            stmt.execute("DROP TABLE IF EXISTS PAR");
            stmt.execute("CREATE TABLE PAR (PAR_CPAR VARCHAR(4), PAR_LIBEL_E VARCHAR(100), PAR_INSTRUMENT_E VARCHAR(1))");
            // 3-char code → included; 4-char code → excluded by WHERE length = 3
            stmt.execute("INSERT INTO PAR VALUES ('CTD', 'CTD sensor', 'D')");
            stmt.execute("INSERT INTO PAR VALUES ('TEMP', 'Temperature', null)");
        }
    }

    @Test
    public void testGetCountries_returnsTwoCountries() throws Exception {
        mockMvc.perform(get("/api/countries"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGetCountries_firstCountryIsExpected() throws Exception {
        // Countries are ordered by name: France comes before United Kingdom
        mockMvc.perform(get("/api/countries"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("FR"))
            .andExpect(jsonPath("$[0].name").value("France"));
    }

    @Test
    public void testGetShips_returnsExpectedShip() throws Exception {
        mockMvc.perform(get("/api/ships"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].code").value("29AH"))
            .andExpect(jsonPath("$[0].name").value("Alis"));
    }

    @Test
    public void testGetDataTypes_returnsOnlyThreeCharCodes() throws Exception {
        // PAR table has 'CTD' (3 chars, included) and 'TEMP' (4 chars, excluded)
        mockMvc.perform(get("/api/dataTypes"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].code").value("CTD"));
    }
}
