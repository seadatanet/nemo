package fr.ifremer.nemo.ws;

import fr.ifremer.nemo.ws.common.SQLUtil;
import fr.ifremer.nemo.ws.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class NemoServiceImpl implements NemoService {

    private final DataSource dataSource;

    @Autowired
    public NemoServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static String convertCFormat(String a_CFormat) {
        StringBuffer buffer = new StringBuffer();
        char c;
        for (int i = 0; i < a_CFormat.length(); i++) {
            if ((c = a_CFormat.charAt(i)) != 'l') {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    @RequestMapping(value = "/countries", method = RequestMethod.GET)
    public List<Country> getCountries() {
        return SQLUtil.queryForList(dataSource, "select PAY.PAY_CPAYS, PAY.PAY_NOMPAY_E from PAY order by PAY.PAY_NOMPAY_E", (rs) -> {
            Country country = new Country();
            country.setCode(rs.getString(1));
            country.setName(rs.getString(2));
            return country;
        });
    }

    @RequestMapping(value = "/dataCentres", method = RequestMethod.GET)
    public List<DataCentre> getDataCentres() {
        final String sql = "select c.CODE_CENTRE, c.NOM_CENTRE, p.PAY_NOMPAY "
                + "from PAY p, CENTRE c "
                + "where  c.CODE_PAYS = p.PAY_CPAYS "
                + "order by p.PAY_NOMPAY, c.CODE_CENTRE";
        return SQLUtil.queryForList(dataSource, sql, (rs) -> {
            DataCentre dataCentre = new DataCentre();
            dataCentre.setCode(rs.getString(1));
            dataCentre.setName(rs.getString(2));
            dataCentre.setCountryName(rs.getString(3));
            return dataCentre;
        });
    }

    @RequestMapping(value = "/dataTypes", method = RequestMethod.GET)
    public List<DataType> getDataTypes() {
        final String sql = "select PAR.PAR_CPAR, PAR.PAR_LIBEL_E, PAR.PAR_INSTRUMENT_E from PAR where length(PAR.PAR_CPAR) = 3 order by PAR.PAR_CPAR";
        return SQLUtil.queryForList(dataSource, sql, (rs) -> {
            DataType dataType = new DataType();
            dataType.setCode(rs.getString(1));
            dataType.setLibelle(rs.getString(2));
            String pInst = rs.getString(3);
            char c = dataType.getCode().charAt(0);
            dataType.setSensor(pInst != null && Arrays.asList('B', 'D', 'H').contains(c));
            return dataType;
        });
    }

    @RequestMapping(value = "/parameters", method = RequestMethod.GET)
    public List<Parameter> getParameters() {
        final String sql = "select p.CODE_PARAMETRE, p.NOM_PARAMETRE, p.UNITE_PARAMETRE, "
                + "p.FORMAT_EDITION, p.VAL_DEFAUT "
                + "from PARAMETRE p "
                + "order by p.CODE_PARAMETRE";
        return SQLUtil.queryForList(dataSource, sql, (rs) -> {
            Parameter param = new Parameter();
            param.setCode(rs.getString(1));
            param.setName(rs.getString(2));
            param.setUnit(rs.getString(3));
            param.setFormat(convertCFormat(rs.getString(4)));
            param.setDefaultValue(rs.getString(5));
            return param;
        });
    }

    @RequestMapping(value = "/regions", method = RequestMethod.GET)
    public List<Region> getRegions() {
        final String sql = "select COD.COD_CODE, COD.COD_LIBEL "
                + "from COD "
                + "where COD.COD_NOLIST = 23 and COD.COD_LANGUE = 'E' and COD.COD_CODE is not null "
                + "order by COD.COD_CODE";

        return SQLUtil.queryForList(dataSource, sql, (rs) -> {
            Region region = new Region();
            region.setCode(rs.getString(1));
            region.setName(rs.getString(2));
            return region;
        });
    }

    @RequestMapping(value = "/ships", method = RequestMethod.GET)
    public List<Ship> getShips() {
        return SQLUtil.queryForList(dataSource, "select n.CODE_NAVIRE, n.NOM_NAV from NAVIRE n order by n.CODE_NAVIRE", (rs) -> {
            Ship ship = new Ship();
            ship.setCode(rs.getString(1));
            ship.setName(rs.getString(2));
            return ship;
        });
    }

    @RequestMapping(value = "/surveys", method = RequestMethod.GET)
    public List<SurveyDescription> getSurveys() {
        return SQLUtil.queryForList(dataSource, "select CAM_CRNO, CAM_CRNOM from VUE_CAM_NEMO", (rs) -> {
            SurveyDescription survey = new SurveyDescription();
            survey.setReference(rs.getString(1));
            survey.setName(rs.getString(2));
            return survey;
        });
    }
}
