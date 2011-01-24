/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ifremer.nemo.mapping.utils;


import java.util.Collection;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.ifremer.nemo.util.Messages;


public class UtilDisplay {


  /**
   * Display an Input Message box
   *
   * @param	a_Message	
   * @return	String
   *
   */
  public static String input (Object a_Message) {
    return JOptionPane.showInputDialog(a_Message);
  }

  /**
   * Display a Warning message box
   *
   * @param	a_Message	The message to display in the dialog box
   * @param	a_Frame		The frame on which to center the dialog box
   *
   */
  public static void info (Object a_Message, JFrame a_Frame) {
    JOptionPane.showMessageDialog(
      a_Frame,
      a_Message,
      Messages.CouplingTable_info,
      JOptionPane.INFORMATION_MESSAGE);
  }  
  
  /**
   * Display a Warning message box
   *
   * @param	a_Message	The message to display in the dialog box
   * @param	a_Frame		The frame on which to center the dialog box
   *
   */
  public static void warn (Object a_Message, JFrame a_Frame) {
    JOptionPane.showMessageDialog(
      a_Frame,
      a_Message,
      Messages.CouplingTable_warning,
      JOptionPane.WARNING_MESSAGE);
  }
  
  /**
   * Display a Error message box
   *
   * @param	a_Message	The message to display in the dialog box
   * @param	a_Frame		The frame on which to center the dialog box
   *
   */
  public static void error (Object a_Message, JFrame a_Frame) {
    JOptionPane.showMessageDialog(
      a_Frame,
      a_Message,
      Messages.CouplingTable_error,
      JOptionPane.ERROR_MESSAGE);
  }  

  /**
   * Display a Confirmation message box
   *
   * @param	a_Message	The message to display in the dialog box
   * @param	a_Frame		The frame on which to center the dialog box
   * @return	boolean
   *
   */
  public static int confirm (Object a_Message, JFrame a_Frame) {
    int status;
    status = JOptionPane.showConfirmDialog(
               a_Frame,
               a_Message,
               Messages.CouplingTable_confirm, 
               JOptionPane.YES_NO_CANCEL_OPTION);
    return status;
  }
  
  public static void displayCollection(Collection c) {
  	Iterator iter = c.iterator();
  	boolean first = true;
  	System.out.print('[');
  	while (iter.hasNext()) {
  		if (!first){
  			System.out.print(", "); //$NON-NLS-1$
  		}
  		System.out.print(iter.next());
  		first = false;
  	}
  	System.out.println(']');
  }
  
}