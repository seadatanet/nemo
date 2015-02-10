package fr.ifremer.nautilus.metadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * Class NautilusSurveyWSImpl
 * 
 * 
 * @author Gerard BUNEL
 */
public class NautilusSurveyWSImpl implements Cloneable, NautilusSurveyWS {

	static protected Logger _logger = null;

	static {

		if (_logger == null) {
			_logger = Logger.getLogger(NautilusSurveyWSImpl.class);
		}
	}

	public NautilusSurveyWSImpl() {
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		NautilusSurveyWSImpl copy = null;
		copy = (NautilusSurveyWSImpl) super.clone();
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
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
	 * @param a_Cam_CRNO
	 * @param a_Connection
	 * @return WSCollectedData[]
	 */
	public WSCollectedData[] getDataTypeFor(String a_Cam_CRNO,
			Connection a_Connection) {
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			String statement = "select c.CPA_CPAR, c.CPA_NOBS, p.PAR_INSTRUMENT_E, p.PAR_LIBEL_E from CPA c, PAR p "
					+ "where ((CPA_TYPTAB='C') and (CPA_NO=?) and (c.CPA_CPAR=p.PAR_CPAR))";
			pstmt = a_Connection.prepareStatement(statement);
			pstmt.setString(1, a_Cam_CRNO);
			result = pstmt.executeQuery();
			Vector<WSCollectedData> datas = new Vector<WSCollectedData>();
			while (result.next()) {
				WSCollectedData data = new WSCollectedData();
				String codeConcat = "";
				codeConcat = result.getString(1) + " - " + result.getString(4);
				data.setCode(codeConcat);
				data.setObservationNumber(result.getInt(2));
				String pInst = result.getString(3);
				char c = data.getCode().charAt(0);
				data.setSensor((pInst != null)
						&& (c == 'B' || c == 'D' || c == 'H'));
				datas.add(data);
			}
			WSCollectedData[] dataArray = new WSCollectedData[datas.size()];
			return datas.toArray(dataArray);
		} catch (Exception e) {
			_logger.error("Erreur de recuperation d'un DataType: " + e);
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (Exception e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param a_CPRJ
	 * @param a_Connection
	 * @return String
	 */
	public String getProjectName(String a_CPRJ, Connection a_Connection) {
		PreparedStatement pstmt = null;
		ResultSet result = null;
		String res = "";
		if (a_CPRJ != null) {
			try {
				String statement = "select distinct  SUBSTR(PRJ_LIBEL,1,28) from PRJ where PRJ_CPRJ=?";
				pstmt = a_Connection.prepareStatement(statement);
				pstmt.setString(1, a_CPRJ);
				result = pstmt.executeQuery();
				if (result.next()) {
					res = result.getString(1);
				}
			} catch (Exception e) {
				_logger
						.error("Erreur de recuperation d'un nom de Projet: "
								+ e);
			} finally {
				if (result != null) {
					try {
						result.close();
					} catch (Exception e) {
					}
				}
				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return res;
	}

	/**
	 * 
	 * @param a_SurveyRef
	 * @return WSSurvey
	 */
	public WSSurvey getSurvey(String a_SurveyRef) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		WSSurvey survey = new WSSurvey();
		if (_logger.isDebugEnabled()) {
			_logger
					.debug("NautilusSurveyWSImpl.getSurvey(" + a_SurveyRef
							+ ")");
		}
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("SISMER");
			conn = ds.getConnection();

			String statement = "select distinct cp.CAM_CRNOM, n.NAV_CODNOD,n.NAV_NOMNAV,cp.CAM_DATDEB,cp.CAM_DATFIN,"
					+ "c.COD_LIBEL,cp.CAM_CPAYS,cp.CAM_ORGPAR,SUBSTR(cp.CAM_CAMREF,1,2),r.RSP_NOM, cp.CAM_CPRJ,cp.CAM_CRNO,cp.CAM_CAMREF "
					+ "from CAM cp,NAV n, COD c, RSP r, MIS m "
					+ "where ((cp.CAM_CRNO = ?) and (cp.CAM_NAVIRE = n.NAV_NAVIRE) and (c.COD_CODE=cp.CAM_CZONE) "
					+ "and (c.COD_NOLIST=23) and (c.COD_LANGUE='E') and (r.RSP_CRSP=m.MIS_CRSP) and "
					+ "(m.MIS_CRNO=cp.CAM_CRNO) and (m.MIS_NORANG=1))";
			pstmt = conn.prepareStatement(statement);
			pstmt.setString(1, a_SurveyRef);
			result = pstmt.executeQuery();
			if (result.next()) {
//				survey.setReference(a_SurveyRef);
				survey.setName(result.getString(1));
				survey.setShipCode(result.getString(2));
				survey.setShipName(result.getString(3));
				survey.setStartDate(result.getDate(4).getTime());
				survey.setEndDate(result.getDate(5).getTime());
				survey.setRegionName(result.getString(6));
				survey.setCountryCode(result.getString(7));
				survey.setLaboratories(result.getString(8));
				survey.setDataCentre(result.getString(9));
				survey.setChiefScientist(result.getString(10));
				
				String cprj = result.getString(11);
				String crno = result.getString(12);
				String ref = result.getString(13);
				
				survey.setReference(ref);
				survey.setCrno(crno);
				survey.setComment("");
				survey.setProjectName(getProjectName(cprj, conn));
				survey.setDataTypes(getDataTypeFor(crno, conn));
				
				_logger.debug("reference >>>>>"+ref);
				_logger.error("reference >>>>" + ref);
			}
			return survey;
		} catch (Exception e) {
			_logger.error("Erreur de recuperation de Campagne: " + e);
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (Exception e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
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

} /* End of class: NautilusSurveyWSImpl */
