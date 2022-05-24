package fr.ifremer.nautilus.ws;

import fr.ifremer.nautilus.ws.model.CollectedData;
import fr.ifremer.nautilus.ws.model.Survey;
import fr.ifremer.nemo.ws.common.SQLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.*;

@RestController
@RequestMapping("/api")
public class NautilusSurveyServiceImpl implements NautilusSurveyService {

    private final DataSource dataSource;

    @Autowired
    public NautilusSurveyServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @RequestMapping(value = "/dataType/{camCrno}", method = RequestMethod.GET)
    public List<CollectedData> getDataTypeFor(@PathVariable("camCrno") String camCrno) {

        if (camCrno == null || camCrno.isEmpty()) {
            return new ArrayList<>();
        }

        final String sql = "select c.CPA_CPAR, c.CPA_NOBS, p.PAR_INSTRUMENT_E, p.PAR_LIBEL_E from CPA c, PAR p "
                + "where ((CPA_TYPTAB='C') and (CPA_NO=?) and (c.CPA_CPAR=p.PAR_CPAR))";
        return SQLUtil.queryForList(dataSource, sql, (rs) -> {
            CollectedData data = new CollectedData();
            data.setCode(rs.getString(1) + " - " + rs.getString(4));
            data.setObservationNumber(rs.getInt(2));
            String pInst = rs.getString(3);
            char c = data.getCode().charAt(0);
            data.setSensor(pInst != null && Arrays.asList('B', 'D', 'H').contains(c));
            return data;
        }, camCrno);
    }

    @Override
    @RequestMapping(value = "/projectName/{cprj}", method = RequestMethod.GET)
    public Map<String, String> getProjectName(@PathVariable("cprj") String cprj) {
        if (cprj == null) {
            return new HashMap<>();
        }
        String projectName = SQLUtil.queryForString(dataSource, "select distinct SUBSTR(PRJ_LIBEL,1,28) from PRJ where PRJ_CPRJ=?", cprj);
        Map<String, String> result = new HashMap<>();
        result.put("projectName", projectName);
        return result;
    }

    @Override
    @RequestMapping(value = "/survey/{camCrno}", method = RequestMethod.GET)
    public Survey getSurvey(@PathVariable("camCrno") String camCrno) {
        final String sql = "select CAM_CRNOM, NAV_CODNOD, NAV_NOMNAV,CAM_DATDEB,CAM_DATFIN, " +
                "REGION ,PAYS ,LABO, DATA_CENTRE , CHIEF_SCIENTIST, " +
                "LISTE_PROJET, CSR, CAMREF " +
                "from VUE_CAM_NEMO_DETAIL " +
                "where CAM_CRNO = ?";
        List<Survey> list = SQLUtil.queryForList(dataSource, sql, (rs) -> {
            Survey survey = new Survey();
            survey.setName(rs.getString(1));
            survey.setShipCode(rs.getString(2));
            survey.setShipName(rs.getString(3));
            survey.setStartDate(rs.getDate(4).getTime());
            survey.setEndDate(rs.getDate(5).getTime());
            survey.setRegionName(rs.getString(6));
            survey.setCountryCode(rs.getString(7));
            survey.setLaboratories(rs.getString(8));
            survey.setDataCentre(rs.getString(9));
            survey.setChiefScientist(rs.getString(10));
            survey.setCrno(camCrno);
            survey.setCsr(rs.getString(12));
            survey.setComment("");
            survey.setReference(rs.getString(13));
            survey.setProjectName(rs.getString(11));
            survey.setDataTypes(getDataTypeFor(camCrno));
            return survey;
        }, camCrno);
        return list.isEmpty() ? null : list.get(0);
    }
}
