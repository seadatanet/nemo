package software.version;

import java.time.Instant;

/**
 * Factory for building {@link SoftwareState} instances from within the Nemo bundle.
 * <p>
 * {@code SoftwareState}'s constructor and setters are {@code protected}, so they are
 * only accessible from the same package. This class lives in {@code software.version}
 * precisely to bridge that access from Nemo's own HTTP-based version-check logic.
 * </p>
 */
public class NemoSoftwareStateFactory {

    private NemoSoftwareStateFactory() {
        // utility class
    }

    /**
     * Creates a state ({@code LAST_VERSION} or {@code OLD_VERSION}) with full version info.
     *
     * @param stateEnum      the determined state
     * @param software       software name (e.g. "NEMO")
     * @param version        current version string
     * @param epochMilli     epoch-millis for the current version release date
     * @param lastVersion    latest known version string
     * @param lastEpochMilli epoch-millis for the latest version release date
     * @param download       download URL for the latest version
     * @return populated SoftwareState
     */
    public static SoftwareState create(SoftwareState.STATE stateEnum,
            String software, String version, long epochMilli,
            String lastVersion, long lastEpochMilli, String download) {
        SoftwareState s = new SoftwareState();
        s.setState(stateEnum);
        s.setSoftware(software);
        s.setVersion(version);
        s.setSqlEpochMilli(epochMilli);
        s.setLastVersion(lastVersion);
        s.setLastVersionSqlEpochMilli(lastEpochMilli);
        s.setDownload(download);
        return s;
    }

    /**
     * Creates an {@code UNKNOWN_VERSION} state (version not registered in the service).
     *
     * @param software software name
     * @param version  version that was not found
     * @return populated SoftwareState
     */
    public static SoftwareState createUnknownVersion(String software, String version) {
        SoftwareState s = new SoftwareState();
        s.setState(SoftwareState.STATE.UNKNOWN_VERSION);
        s.setSoftware(software);
        s.setVersion(version);
        s.setSqlEpochMilli(Instant.now().toEpochMilli());
        return s;
    }

    /**
     * Creates a {@code SOAP_ERROR} state.
     *
     * @param errorMessage description of the error
     * @return populated SoftwareState
     */
    public static SoftwareState createSoapError(String errorMessage) {
        SoftwareState s = new SoftwareState();
        s.setState(SoftwareState.STATE.SOAP_ERROR);
        s.setSoapError(errorMessage != null ? errorMessage : "unknown error");
        s.setSqlEpochMilli(Instant.now().toEpochMilli());
        return s;
    }
}
