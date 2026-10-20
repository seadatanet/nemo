package fr.ifremer.nemo.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.osgi.framework.Version;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import fr.ifremer.nemo.Nemo;
import software.version.NemoSoftwareStateFactory;
import software.version.SoftwareState;
import software.version.SoftwareState.STATE;

/**
 * Checks the Nemo version against the SeaDataNet SoftwareVersionWebService.
 * <p>
 * The SOAP call is performed via a plain {@link HttpURLConnection} to avoid any
 * dependency on JAX-WS, which is not available in an OSGi environment under JDK 11+.
 * </p>
 */
public class NemoVersion {

	/** SOAP 1.1 endpoint (without ?wsdl). */
	private static final String SOAP_ENDPOINT =
			"https://www.seadatanet.org/SoftwareVersionSoap/SoftwareVersionWebService";

	/** SOAPAction header value for getSoftwareVersionList. */
	private static final String SOAP_ACTION =
			"\"http://ws/SoftwareVersionWebService/getSoftwareVersionListRequest\"";

	/** HTTP connection timeout (ms). */
	private static final int CONNECT_TIMEOUT_MS = 10_000;

	/** HTTP read timeout (ms). */
	private static final int READ_TIMEOUT_MS    = 30_000;


	public static SoftwareState check(Version version) {
		SoftwareState state;
		try {
			String xmlResult = callGetSoftwareVersionList("NEMO");
			state = parseSoftwareVersionList(xmlResult, version.toString());
		} catch (Exception e) {
			state = NemoSoftwareStateFactory.createSoapError(e.getMessage());
		}

		if (state.getState() == STATE.LAST_VERSION) {
			Nemo.getInstance().getLogger().info(state.getDescription());
		} else {
			Nemo.getInstance().getLogger().warn(getStateMessage(state));
		}
		return state;
	}

	/**
	 * Returns the user-facing message string for the given version state.
	 * <p>
	 * Returns {@link Messages#NemoVersionUnreachable} as a fallback for
	 * {@code SOAP_ERROR} or any unexpected state.
	 * Never called for {@link STATE#LAST_VERSION} (no message needed in that case).
	 * </p>
	 */
	public static String getStateMessage(SoftwareState state) {
		if (state.getState() == STATE.UNKNOWN_VERSION) {
			return MessageFormat.format(Messages.NemoVersionUnknown, state.getVersion());
		}
		try {
			String[] lastDate = state.getLastVersionDay().split("-");
			return MessageFormat.format(Messages.NemoVersionUpdateAvailable,
					state.getVersion(), state.getLastVersion(),
					lastDate[0], lastDate[1], lastDate[2]);
		} catch (Exception e) {
			return Messages.NemoVersionUnreachable;
		}
	}

	// -------------------------------------------------------------------------
	// Private helpers
	// -------------------------------------------------------------------------

	/**
	 * Sends a SOAP 1.1 {@code getSoftwareVersionList} request and returns the
	 * raw XML string contained in the {@code <return>} response element.
	 */
	private static String callGetSoftwareVersionList(String software) throws Exception {
		String soapEnvelope =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<soapenv:Envelope"
				+ " xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
				+ " xmlns:ws=\"http://ws/\">"
				+ "<soapenv:Body>"
				+ "<ws:getSoftwareVersionList>"
				+ "<software>" + escapeXml(software) + "</software>"
				+ "</ws:getSoftwareVersionList>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		byte[] bodyBytes = soapEnvelope.getBytes("UTF-8");

		HttpURLConnection conn = (HttpURLConnection) new URL(SOAP_ENDPOINT).openConnection();
		try {
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
			conn.setRequestProperty("SOAPAction", SOAP_ACTION);
			conn.setRequestProperty("Content-Length", String.valueOf(bodyBytes.length));
			conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
			conn.setReadTimeout(READ_TIMEOUT_MS);

			try (OutputStream os = conn.getOutputStream()) {
				os.write(bodyBytes);
			}

			int responseCode = conn.getResponseCode();
			InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
			String response;
			try (Scanner scanner = new Scanner(is, "UTF-8")) {
				scanner.useDelimiter("\\A");
				response = scanner.hasNext() ? scanner.next() : "";
			}

			if (responseCode != 200) {
				throw new IOException("HTTP " + responseCode + " from version-check service");
			}
			return extractReturnValue(response);
		} finally {
			conn.disconnect();
		}
	}

	/**
	 * Extracts the text content of the {@code <return>} element from a SOAP response.
	 * The content is an XML string that describes the software version list.
	 */
	private static String extractReturnValue(String soapResponse) throws Exception {
		Document doc = newDocumentBuilder().parse(
				new InputSource(new java.io.StringReader(soapResponse)));
		// Try with namespace wildcard first, then unqualified
		NodeList returns = doc.getElementsByTagNameNS("*", "return");
		if (returns.getLength() == 0) {
			returns = doc.getElementsByTagName("return");
		}
		if (returns.getLength() == 0) {
			throw new IOException("No <return> element found in SOAP response");
		}
		return returns.item(0).getTextContent();
	}

	/**
	 * Parses the {@code <softwareList>} XML returned by the service and determines
	 * the version state by comparing the list against {@code currentVersion}.
	 */
	private static SoftwareState parseSoftwareVersionList(String xmlResult,
			String currentVersion) throws Exception {
		Document doc = newDocumentBuilder().parse(
				new InputSource(new java.io.StringReader(xmlResult)));

		NodeList infos = doc.getElementsByTagName("softwareInfo");
		if (infos.getLength() == 0) {
			return NemoSoftwareStateFactory.createUnknownVersion("NEMO", currentVersion);
		}

		// The list is ordered: first entry = latest version
		String latestVersion  = null;
		long   latestEpoch    = 0L;
		String download       = "";

		String foundVersion   = null;
		long   foundEpoch     = 0L;
		STATE  foundState     = null;

		for (int i = 0; i < infos.getLength(); i++) {
			Element info  = (Element) infos.item(i);
			String  v     = text(info, "version");
			long    epoch = parseLong(text(info, "delivered"));
			String  dl    = text(info, "download");

			if (i == 0) {
				latestVersion = v;
				latestEpoch   = epoch;
				download      = dl;
			}
			if (currentVersion.equals(v)) {
				foundVersion = v;
				foundEpoch   = epoch;
				foundState   = (i == 0) ? STATE.LAST_VERSION : STATE.OLD_VERSION;
			}
		}

		if (foundState == null) {
			return NemoSoftwareStateFactory.createUnknownVersion("NEMO", currentVersion);
		}
		return NemoSoftwareStateFactory.create(
				foundState, "NEMO", foundVersion, foundEpoch,
				latestVersion, latestEpoch, download);
	}

	/** Returns a namespace-aware, XXE-safe DocumentBuilder. */
	private static DocumentBuilder newDocumentBuilder() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		// Disable XXE (OWASP A05)
		factory.setFeature("http://xml.org/sax/features/external-general-entities",   false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setXIncludeAware(false);
		factory.setExpandEntityReferences(false);
		return factory.newDocumentBuilder();
	}

	/** Returns the text content of the first child element with the given tag name, or "". */
	private static String text(Element parent, String tagName) {
		NodeList nl = parent.getElementsByTagName(tagName);
		return nl.getLength() > 0 ? nl.item(0).getTextContent() : "";
	}

	/** Parses a long safely, returning 0 on error. */
	private static long parseLong(String s) {
		try {
			return Long.parseLong(s.trim());
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	/** Escapes the five predefined XML entities in a string. */
	private static String escapeXml(String s) {
		return s.replace("&",  "&amp;")
		        .replace("<",  "&lt;")
		        .replace(">",  "&gt;")
		        .replace("\"", "&quot;")
		        .replace("'",  "&apos;");
	}
}
