package fr.ifremer.nemo.delete;

import java.io.File;
import java.util.Locale;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


import com.izforge.izpack.util.AbstractUIProcessHandler;

import fr.ifremer.nemo.delete.ressources.IzpackLocal;
import fr.ifremer.nemo.delete.ressources.Messages;

public class DeleteOlderInstallation {
	
	/**
	 * langage option
	 */
	public static String OPT_LANG = "l";
	/**
	 * install path option
	 */
	public static String OPT_INSTALL_PATH = "i";
	/**
	 * configuration dir name
	 */
	private static String CONFIGURATION_DIR = "configuration";
	/**
	 * plugins dir name
	 */
	private static String PLUGINS_DIR = "plugins";
	
	/**
	 * vocab dir name
	 */
	private static String VOCAB_DIR = "resources" + File.separator + "Vocab";
	
	public static void main(String[] args){
		String[] argv = {"-l","fra","-i","c:"};
		
		new DeleteOlderInstallation().run(null,argv);
	}
	
	
    /**
     * delete old folder from izpack installer
     * @param handler
     * @param args
     */
    public void run(AbstractUIProcessHandler handler, String[] args) {
       
    	Options opt = new Options();
    	//option 
    	opt.addOption(OPT_LANG, true, "installeur lang");
        opt.addOption(OPT_INSTALL_PATH, true, "install path");
        
        BasicParser p = new BasicParser();
    	try {
			CommandLine cl = p.parse(opt, args);
			if(cl.hasOption(OPT_LANG))
			{
				IzpackLocal.setLocale(cl.getOptionValue(OPT_LANG));
				
			}
			
			
	        if(cl.hasOption(OPT_INSTALL_PATH))
	        {
	        	handler.logOutput(Messages.getString("startPreinstall"), false);
	        	// delete ummary_CDI_NEMO.xml
	        	try{
	        		String cdiSummaryFilePath = (new StringBuilder(String.valueOf(cl.getOptionValue(OPT_INSTALL_PATH)))).append(File.separator).append("summary_CDI_NEMO.xml").toString();
	        		File cdiSummaryFile = new File(cdiSummaryFilePath);
	        		if (cdiSummaryFile.exists()){
	        			handler.logOutput(Messages.getString("deleteCDISummary"), false);
	        			cdiSummaryFile.delete();
	        		}
	        	} catch (Exception e) {
	    			handler.emitError(Messages.getString("error"), e.getMessage());
	    		}
	        	
	        	
	            //delete Vocab dir
	        	if(deleteDirectory(cl.getOptionValue(OPT_INSTALL_PATH), VOCAB_DIR))
	        	{
	        		handler.logOutput(Messages.getString("deleteVocab"), false);
	        	}
	        	
	            //delete old plugins dir
	        	if(deleteDirectory(cl.getOptionValue(OPT_INSTALL_PATH), PLUGINS_DIR))
	        	{
	        		handler.logOutput(Messages.getString("deletePlugins"), false);
	        	}
	        	
	            //delete old Configuration dir
	        	if(deleteDirectory(cl.getOptionValue(OPT_INSTALL_PATH), CONFIGURATION_DIR))
	        	{
	        		handler.logOutput(Messages.getString("deleteConfiguration"), false);
	        	}
	        	handler.logOutput(Messages.getString("endPreinstall"), false);
	        }
	        else
	        {
	        	handler.emitError(Messages.getString("error"), Messages.getString("inputPathOptionError"));
	        }
	        
			
			
		} catch (ParseException e) {
			handler.emitError(Messages.getString("error"), e.getMessage());
		}

      }
    
    /**
     * delete directory from installPath
     * @param rootPath string install root path
     * @param directory directory string to delere
     * @return
     */
    private boolean deleteDirectory(String rootPath, String directory)
    {

    	String deleteDir = (new StringBuilder(String.valueOf(rootPath))).append(File.separator).append(directory).toString();
    	File delDirectory = new File(deleteDir);
    	if(delDirectory.exists()){
    		return deleteDirectory(delDirectory);
    	}
    	return false;
    }
	 
	/**
     * delete the file path, can be a directory
     * @param directory
     * @return
     */
    private boolean deleteDirectory(File directory)
    {
        if(directory.exists())
        {
            File files[] = directory.listFiles();
            if(files != null)
            {
                for(int i = 0; i < files.length; i++)
                {
                    if(files[i].isDirectory())
                    {
                        deleteDirectory(files[i]);
                    }
                    else
                    {
                        files[i].delete();
                    }
                }

            }
        }
        return directory.delete();
    }

}
