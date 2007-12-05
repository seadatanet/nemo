package fr.ifremer.nemo.ws;

import fr.ifremer.nemo.ws.WSCountry;
import fr.ifremer.nemo.ws.WSDataCentre;
import fr.ifremer.nemo.ws.WSDataType;
import fr.ifremer.nemo.ws.WSRegion;
import fr.ifremer.nemo.ws.WSShip;
import fr.ifremer.nemo.ws.WSSurveyDescription;
import java.sql.*;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

/**
 * Class NemoWSImpl
 * 
 * 
 * @author Gerard BUNEL
 */
public class NemoWSImpl implements Cloneable, NemoWS {

	static protected Logger _logger = null;

	static {
		if (_logger == null) {
			_logger = Logger.getLogger(NemoWSImpl.class);
		}
	}

	public NemoWSImpl() {
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	public Object clone() throws CloneNotSupportedException {
		NemoWSImpl copy = null;
		copy = (NemoWSImpl) super.clone();
		return copy;
	}

	/**
	 * Remove any link for role logger
	 */
	public static void releaseLogger() {
		setLogger(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public static Logger getLogger() {
		return _logger;
	}

	/**
	 * Create a new link with x for role logger
	 * 
	 * @param x
	 *            the object to link with
	 */
	public static void setLogger(Logger x) {
		if (_logger == x)
			return;
		_logger = x;
	}

	/**
	 * 
	 * @param a_CFormat
	 * @return String
	 */
	public String convertCFormat(String a_CFormat) {
		StringBuffer buffer = new StringBuffer();
		char c;
		for (int i = 0; i < a_CFormat.length(); i++) {
			if ((c = a_CFormat.charAt(i)) != 'l') {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	/**
	 * 
	 * @return WSCountry[]
	 */
	public WSCountry[] getCountries() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		if (_logger.isDebugEnabled()) {
			_logger.debug("NemoWSImpl.getCountries()");
		}
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("SISMER");
			conn = ds.getConnection();

			String statement = "select PAY.PAY_CPAYS, PAY.PAY_NOMPAY_E from PAY order by PAY.PAY_NOMPAY_E";
			stmt = conn.createStatement();
			result = stmt.executeQuery(statement);
			Vector<WSCountry> countries = new Vector<WSCountry>();
			while (result.next()) {
				WSCountry country = new WSCountry();
				country.setCode(result.getString(1));
				country.setName(result.getString(2));
				countries.add(country);
			}
			WSCountry[] countryArray = new WSCountry[countries.size()];
			return countries.toArray(countryArray);
		} catch (Exception e) {
			_logger.error("Erreur de récupération des Pays: " + e);
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @return WSDataCentre[]
	 */
	public WSDataCentre[] getDataCentres() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		if (_logger.isDebugEnabled()) {
			_logger.debug("NemoWSImpl.getDataCentres()");
		}
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("SISMER");
			conn = ds.getConnection();

			String statement = "select c.CODE_CENTRE, c.NOM_CENTRE, p.PAY_NOMPAY "
					+ "from PAY p, CENTRE c "
					+ "where  c.CODE_PAYS = p.PAY_CPAYS "
					+ "order by p.PAY_NOMPAY, c.CODE_CENTRE";
			stmt = conn.createStatement();
			result = stmt.executeQuery(statement);
			Vector<WSDataCentre> centres = new Vector<WSDataCentre>();
			while (result.next()) {
				WSDataCentre centre = new WSDataCentre();
				centre.setCode(result.getString(1));
				centre.setName(result.getString(2));
				centre.setCountryName(result.getString(3));
				centres.add(centre);
			}
			WSDataCentre[] centreArray = new WSDataCentre[centres.size()];
			return centres.toArray(centreArray);
		} catch (Exception e) {
			_logger
					.error("Erreur de récupération d'un Centre de Données: "
							+ e);
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @return WSDataType[]
	 */
	public WSDataType[] getDataTypes() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		if (_logger.isDebugEnabled()) {
			_logger.debug("NemoWSImpl.getDataTypes()");
		}
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("SISMER");
			conn = ds.getConnection();

			String statement = "select PAR.PAR_CPAR, PAR.PAR_LIBEL_E, PAR.PAR_INSTRUMENT_E from PAR where  length(PAR.PAR_CPAR) = 3 order by PAR.PAR_CPAR";
			stmt = conn.createStatement();
			result = stmt.executeQuery(statement);
			Vector<WSDataType> types = new Vector<WSDataType>();
			while (result.next()) {
				WSDataType dataType = new WSDataType();
				dataType.setCode(result.getString(1));
				dataType.setLibelle(result.getString(2));
				String pInst = result.getString(3);
				char c = dataType.getCode().charAt(0);
				dataType.setSensor((pInst != null)
						&& (c == 'B' || c == 'D' || c == 'H'));
				types.add(dataType);
			}
			WSDataType[] typeArray = new WSDataType[types.size()];
			return types.toArray(typeArray);
		} catch (Exception e) {
			_logger.error("Erreur de récupération des DataType: " + e);
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @return WSParameter[]
	 */
	public WSParameter[] getParameters() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		if (_logger.isDebugEnabled()) {
			_logger.debug("NemoWSImpl.getParameters()");
		}
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("SISMER");
			conn = ds.getConnection();

			String statement = "select p.CODE_PARAMETRE, p.NOM_PARAMETRE, p.UNITE_PARAMETRE, "
					+ "p.FORMAT_EDITION, p.VAL_DEFAUT "
					+ "from PARAMETRE p "
					+ "order by p.CODE_PARAMETRE";
			stmt = conn.createStatement();
			result = stmt.executeQuery(statement);
			Vector<WSParameter> parameters = new Vector<WSParameter>();
			while (result.next()) {
				WSParameter param = new WSParameter();
				param.setCode(result.getString(1));
				param.setName(result.getString(2));
				param.setUnit(result.getString(3));
				param.setFormat(convertCFormat(result.getString(4)));
				param.setDefaultValue(result.getString(5));
				parameters.add(param);
			}
			WSParameter[] parameterArray = new WSParameter[parameters.size()];
			return parameters.toArray(parameterArray);
		} catch (Exception e) {
			_logger.error("Erreur de récupération des Parametres: " + e);
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @return WSRegion[]
	 */
	public WSRegion[] getRegions() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		if (_logger.isDebugEnabled()) {
			_logger.debug("NemoWSImpl.getRegions()");
		}
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("SISMER");
			conn = ds.getConnection();

			String statement = "select COD.COD_CODE, COD.COD_LIBEL "
					+ "from COD "
					+ "where COD.COD_NOLIST = 23 and COD.COD_LANGUE = 'E' and COD.COD_CODE is not null "
					+ "order by COD.COD_CODE";
			stmt = conn.createStatement();
			result = stmt.executeQuery(statement);
			Vector<WSRegion> regions = new Vector<WSRegion>();
			while (result.next()) {
				WSRegion region = new WSRegion();
				region.setCode(result.getString(1));
				region.setName(result.getString(2));
				regions.add(region);
			}
			WSRegion[] regionArray = new WSRegion[regions.size()];
			return regions.toArray(regionArray);
		} catch (Exception e) {
			_logger.error("Erreur de récupération des Régions: " + e);
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @return WSShip[]
	 */
	public WSShip[] getShips() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		if (_logger.isDebugEnabled()) {
			_logger.debug("NemoWSImpl.getShips()");
		}
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("SISMER");
			conn = ds.getConnection();

			String statement = "select n.CODE_NAVIRE, n.NOM_NAV "
					+ "from NAVIRE n " + "order by n.CODE_NAVIRE";
			stmt = conn.createStatement();
			result = stmt.executeQuery(statement);
			Vector<WSShip> ships = new Vector<WSShip>();
			while (result.next()) {
				WSShip ship = new WSShip();
				ship.setCode(result.getString(1));
				ship.setName(result.getString(2));
				ships.add(ship);
			}
			WSShip[] shipArray = new WSShip[ships.size()];
			return ships.toArray(shipArray);
		} catch (Exception e) {
			_logger.error("Erreur de récupération de des Navires: " + e);
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @return WSSurveyDescription[]
	 */
	public WSSurveyDescription[] getSurveyList() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		if (_logger.isDebugEnabled()) {
			_logger.debug("NemoWSImpl.getSurveyList()");
		}
		try {
			System.out.println("AVANT REQ");
			
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("SISMER");
			conn = ds.getConnection();

			
			
			String statement = "select CAM_CRNO, CAM_CRNOM " + "from CAM "
					+ "where CAM_CAMTYP != 'PV' " + "order by CAM_DATDEB desc ";
			
			System.out.println("APRES REQ");
			
			stmt = conn.createStatement();
			result = stmt.executeQuery(statement);
			Vector<WSSurveyDescription> surveys = new Vector<WSSurveyDescription>();
			while (result.next()) {
				WSSurveyDescription survey = new WSSurveyDescription();
				survey.setReference(result.getString(1));
				survey.setName(result.getString(2));
				surveys.add(survey);
			}
			WSSurveyDescription[] surveyArray = new WSSurveyDescription[surveys
					.size()];
			return surveys.toArray(surveyArray);
		} catch (Exception e) {

			_logger.error("Erreur de récupération de la liste des Campagnes: "
					+ e);
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

} /* End of class: NemoWSImpl */
