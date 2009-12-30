package fr.ifremer.nemo.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.input.AbstractModel;
import fr.ifremer.nemo.ui.actions.OptionsAction;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

/**
 * Proxy settings
 * @author IFREMER
 */
public class Proxy extends AbstractModel
{	
    /**
     * Indique l'utilisation d'un proxy ou non
     */
	protected boolean use;
    
    /**
     * Adresse du Proxy à utiliser
     */
	protected String address;
    
    /**
     * Port du Proxy
     */
	protected String port;
    
    /**
     * Nom utilisateur d'accès au Proxy (si protégé)
     */
	protected String login;
    
    /**
     * Mot de passe d'accès au Proxy (si protégé)
     */
	protected String password;

	public Proxy() {
        _tagName = Intern.settings_proxy;
        initValues();
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initValues(){
        use = false;
        address = Global.EMPTY_STRING;
        port = Global.EMPTY_STRING;
        login = Global.EMPTY_STRING;
        password = Global.EMPTY_STRING;
    }


    /**
     * @see AbstractModel.java#fromElement
     */
	public static SAXConsumer fromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes){
		Proxy el = new Proxy();
		el.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return el;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void buildFromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes){
        String tmp;
        if ((tmp = a_Attributes.getValue(Intern.proxy_use)) != null) {
            setUse(tmp);
        }

	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public SAXConsumer consumeElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes){

		if (a_LocalName.equals(Intern.proxy_adr)) {
			Element2Field setter = new Element2Field(this, Intern.proxy_adr_set);
			return setter;
		}
		if (a_LocalName.equals(Intern.proxy_port)) {
			Element2Field setter = new Element2Field(this, Intern.proxy_port_set);
			return setter;
		}
		if (a_LocalName.equals(Intern.proxy_login)) {
			Element2Field setter = new Element2Field(this, Intern.proxy_login_set);
			return setter;
		}
		if (a_LocalName.equals(Intern.proxy_pass)) {
			Element2Field setter = new Element2Field(this, Intern.proxy_pass_set);
			return setter;
		}
		return null;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SAXConsumer handlePathElement(String path, 
                                         String a_URI,
                                         String a_LocalName, 
                                         String a_QName, 
                                         Attributes a_Attributes){
        // non utilisé
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
        a_Writer.putAttribute(Intern.proxy_use, isUse());
		a_Writer.putTag(Intern.proxy_adr, getAdress());
		a_Writer.putTag(Intern.proxy_port, getPort());
		a_Writer.putTag(Intern.proxy_login, getLogin());
		a_Writer.putTag(Intern.proxy_pass, getPassword());

	}
    

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Proxy copy = null;
        copy = (Proxy) super.clone();
        return copy;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void release() {
        // aucun lien à supprimer
    }
    
    /**
     * Met à jour le paramétrage du Proxy
     * @param writeIni : indique si la mise à jour du fichier nemo.ini est nécessaire
     */
    public void update(boolean writeIni)
    {       
        // Backup
        boolean bkUse = use;
        String bkAddress = address;
        String bkPort = port;
        String bkLogin = login;
        String bkPassword = password;
        
        use = OptionsAction.useProxy.getSelection();
        address = OptionsAction.textAdrProxy.getText();
        port = OptionsAction.textPortProxy.getText();
        login = OptionsAction.textLoginProxy.getText();
        password = OptionsAction.textPassProxy.getText();
        
        if (writeIni){
            PrintWriter outputFileWriter = null;
            try{
                // mise à jour du .ini de l'application
                File file = new File(Intern.ini_name); 
                outputFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                outputFileWriter.println(Intern.ini_start);
                if (use){
                    outputFileWriter.println(Intern.ini_proxy_adr + address);
                    outputFileWriter.println(Intern.ini_proxy_port + port);
                    if (login != null && !login.trim().equals(Global.EMPTY_STRING)){
                        outputFileWriter.println(Intern.ini_proxy_login + login);
                    }
                    if (password != null && !password.trim().equals(Global.EMPTY_STRING)){
                        outputFileWriter.println(Intern.ini_proxy_pass + password);
                    }
                }
            }catch (IOException e){
                
                // RollBack update
                use = bkUse;
                address = bkAddress;
                port = bkPort;
                login = bkLogin;
                password = bkPassword;
                
                // inform user
                MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
                                                        Messages.error_settings_proxy_title, null, 
                                                        Messages.error_settings_proxy, 
                                                        MessageDialog.NONE, new String[] { Messages.success }, 0); 
                dialog.open();
            }finally{
                if (outputFileWriter != null)
                    outputFileWriter.close();
            }
        }
    
    }
    
    
    /**
     * Getter pour address.
     * @return String
     * @see Proxy.java#address
     */
	public String getAdress() {
		return address;
	}

    /**
     * Setter pour address
     * @param x : nouvelle valeur pour address
     * @see Proxy.java#address
     */
	public void setAdress(String x) {
		this.address = x;
	}
    
    /**
     * Getter pour login.
     * @return String
     * @see Proxy.java#login
     */
	public String getLogin() {
		return login;
	}

    /**
     * Setter pour login
     * @param x : nouvelle valeur pour login
     * @see Proxy.java#login
     */
	public void setLogin(String x) {
		this.login = x;
	}
    
    /**
     * Getter pour password.
     * @return String
     * @see Proxy.java#password
     */
	public String getPassword() {
		return password;
	}

    /**
     * Setter pour password
     * @param x : nouvelle valeur pour password
     * @see Proxy.java#password
     */
	public void setPassword(String x) {
		this.password = x;
	}
    
    /**
     * Getter pour port.
     * @return String
     * @see Proxy.java#port
     */
	public String getPort() {
		return port;
	}

    /**
     * Setter pour port
     * @param x : nouvelle valeur pour port
     * @see Proxy.java#port
     */
	public void setPort(String x) {
		this.port = x;
	}
    
    /**
     * Getter pour use.
     * @return boolean
     * @see Proxy.java#use
     */
	public boolean isUse() {
		return use;
	}

    /**
     * Setter pour use
     * @param s : nouvelle valeur pour use sous forme de String
     * @see Proxy.java#use
     */
	public void setUse(String s) {
		this.use = Boolean.parseBoolean(s);
	}
}