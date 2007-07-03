package test.nemo;

import fr.ifremer.nemo.domain.*;
import fr.ifremer.nemo.input.*;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.ui.Application;

import javax.swing.*;

/**
 * Class TestNemo
 *
 *
 * @author	Gerard BUNEL
 */
public  class TestNemo implements Cloneable {
  public TestNemo() {
  }

  /**
   * Returns a copy of this Object
   *
   * @return  a copy of this object
   */
  public Object clone() throws CloneNotSupportedException {
    TestNemo copy = null;
    copy = (TestNemo) super.clone();
    return copy;
  }

  /**
   * Remove any associated links
   */
  public void release() {
  }

  /**
   *
   * @param  a_FileIn  
   * @param  a_FileOut  
   * @param  a_Notifier  
   */
  public static void doConvert ( String a_FileIn, String a_FileOut, ProgressNotification a_Notifier) {
    InputModel model = new InputModel();
    java.io.FileInputStream stream = null;
    try {
    	com.ago.xml.XMLBootstrapParser parser = new com.ago.xml.XMLBootstrapParser(model);
    	stream = new java.io.FileInputStream(a_FileIn);
    	parser.parse(stream, false);
    	Survey survey = model.convert(a_Notifier);
    	//survey.writeXMLOn( System.out );
    	fr.ifremer.nemo.medatlas.MedatlasSurveyWriter writer = new fr.ifremer.nemo.medatlas.MedatlasSurveyWriter();
    	a_Notifier.progress(0);
    	writer.writeSurvey ( survey, new java.io.File(a_FileOut), a_Notifier);
    } catch (Exception ex) {
    	ex.printStackTrace();
    } finally {
    	try { stream.close(); } catch (Exception e) {}
    }
  }

  /**
   *
   * @param  args  
   */
  public static void main ( String[] args) {
	    JFrame frame = new JFrame("Test NEMO");
	    final JProgressBar progress =  new JProgressBar( 0, 100);
	    frame.getContentPane().add(progress);
	    ProgressNotification notifier = new ProgressNotification() {
	    	                                public void progress(final int val) {
	    		                                try {
	    			                                SwingUtilities.invokeAndWait( new Runnable() {
	    				                                                              public void run() {
	    					                                                              progress.setValue(val);
	    				                                                              }
	    			                                                              });
	    		                                } catch (Exception e) {
	    		                                }
	    	                                }
	                                    };
	    frame.pack();
	    frame.setVisible(true);
	    doConvert( args[0], args[1], notifier );
	  }

} /* End of class: TestNemo */
