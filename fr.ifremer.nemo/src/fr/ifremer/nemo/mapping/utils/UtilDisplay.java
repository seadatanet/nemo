/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ifremer.nemo.mapping.utils;


import java.util.Collection;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.util.Iterator;


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
      "Information",
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
      "Warning !",
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
      "Erreur !",
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
  public static boolean confirm (Object a_Message, JFrame a_Frame) {
    int status;
    status = JOptionPane.showConfirmDialog(
               a_Frame,
               a_Message,
               "Confirm Please !",
               JOptionPane.OK_CANCEL_OPTION);
    return status == JOptionPane.OK_OPTION;
  }
  
  public static void displayCollection(Collection c) {
  	Iterator iter = c.iterator();
  	boolean first = true;
  	System.out.print('[');
  	while (iter.hasNext()) {
  		if (!first){
  			System.out.print(", ");
  		}
  		System.out.print(iter.next());
  		first = false;
  	}
  	System.out.println(']');
  }
  
}