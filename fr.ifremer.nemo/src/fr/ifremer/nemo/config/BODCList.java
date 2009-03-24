/* ============================================================================
 * Nom du fichier	: BODCList.java
 * Auteur			: dboulang
 * Date				: 23 mars 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.config;

import fr.ifremer.nemo.ui.Utils;

/**
 * Description: BODCList décrit une liste BODC
 * @author IFREMER
 *
 */
public class BODCList
{

    private String id;
    private String name;
    private String localVersion;
    private String serverVersion;
    private String url;
    
    private boolean useToMapping;
    
    
    
    /**
     * @param id
     * @param localVersion
     * @param url
     */
    public BODCList(String id, String localVersion, String url, String mapping)
    {
        super();
        this.id = id;
        this.localVersion = localVersion;
        this.url = url;
        useToMapping = Boolean.valueOf(mapping);
    }
    
    
    /**
     * Getter sur localVersion.
     * @return the localVersion
     */
    public String getLocalVersion()
    {
        return localVersion;
    }
    /**
     * Setter sur localVersion.
     * @param localVersion the localVersion to set
     */
    public void setLocalVersion(String localVersion)
    {
        this.localVersion = localVersion;
    }
    /**
     * Getter sur id.
     * @return the id
     */
    public String getId()
    {
        return id;
    }
    /**
     * Setter sur id.
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }
    /**
     * Getter sur serverVersion.
     * @return the serverVersion
     */
    public String getServerVersion()
    {
        return serverVersion;
    }
    /**
     * Setter sur serverVersion.
     * @param serverVersion the serverVersion to set
     */
    public void setServerVersion(String serverVersion)
    {
        this.serverVersion = serverVersion;
    }
    /**
     * Getter sur url.
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }
    /**
     * Setter sur url.
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    public void setUrl (){
        String VocabUrl = Utils.configuration.getVocabURL();
        String ListUrl = VocabUrl.substring(0, VocabUrl.indexOf('/',7) + 1) + "list/" + id + "/current"; //$NON-NLS-1$ //$NON-NLS-2$
        this.url = ListUrl;
    }


    /**
     * Getter sur name.
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Setter sur name.
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }


    /**
     * Getter sur useToMapping.
     * @return the useToMapping
     */
    public boolean isUseToMapping()
    {
        return useToMapping;
    }


    /**
     * Setter sur useToMapping.
     * @param useToMapping the useToMapping to set
     */
    public void setUseToMapping(boolean useToMapping)
    {
        this.useToMapping = useToMapping;
    }
    
    
    
}
