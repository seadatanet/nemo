package fr.ifremer.nemo.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.RootSAXConsumer;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.ui.actions.OptionsAction;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

/**
 * Proxy settings
 * @author IFREMER
 */
public class Proxy implements RootSAXConsumer {


    /**
	 * instance of Configuration
	 */
	private static Proxy instance;	
	
	protected boolean use;
	protected String address;
	protected String port;
	protected String login;
	protected String password;
	

	private Proxy() {
		use = false;
		address = Global.EMPTY_STRING;
		port = Global.EMPTY_STRING;
		login = Global.EMPTY_STRING;
		password = Global.EMPTY_STRING;
	}

	public static Proxy getInstance(){
		if (null == instance) { // Premier appel
			instance = new Proxy();
		}
		return instance;
	}
	
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
				File file = new File(Configuration.nemoHome + Intern.ini_name); 
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


	public static SAXConsumer fromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		Proxy tmp = new Proxy();
		tmp.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return (SAXConsumer) tmp;
	}
	
	public void buildFromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
        String tmp;
        if ((tmp = a_Attributes.getValue(Intern.proxy_use)) != null) {
            setUse(tmp);
        }

	}

	public void consumeCharacters(String arg0) throws SAXException {
		// Auto-generated method stub

	}

	public SAXConsumer consumeElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {

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
	 * 
	 * Generate XML defintion on a Stream
	 * 
	 * @param a_Writer
	 *            XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putTag(Intern.proxy_adr, getAdress());
		a_Writer.putTag(Intern.proxy_port, getPort());
		a_Writer.putTag(Intern.proxy_login, getLogin());
		a_Writer.putTag(Intern.proxy_pass, getPassword());

	}

	/**
	 * 
	 * Generate XML defintion on a XMLOutputWriter
	 * 
	 * @param a_Writer
	 *            the XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLOn(XMLOutputWriter a_Writer) {
		a_Writer.openTag(Intern.settings_proxy);
        a_Writer.putAttribute(Intern.proxy_use, isUse());
		writeXMLBodyOn(a_Writer);
		a_Writer.closeTag();
	}

	/**
	 * @return the adress
	 */
	public String getAdress() {
		return address;
	}

	/**
	 * @param adress the adress to set
	 */
	public void setAdress(String adress) {
		this.address = adress;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the pass
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param pass the pass to set
	 */
	public void setPassword(String pass) {
		this.password = pass;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the use
	 */
	public boolean isUse() {
		return use;
	}

	/**
	 * @param use the use to set
	 */
	public void setUse(String use) {
		this.use = Boolean.parseBoolean(use);
	}

	
	
	
	
}
