package fr.ifremer.nemo.delete.ressources;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import fr.ifremer.nemo.delete.DeleteOlderInstallation;



/**
 * messages and libelles application
 * @author IFREMER
 */
public class Messages {
	
	private static final String BUNDLE_NAME = "fr.ifremer.nemo.delete.ressources.messages"; //$NON-NLS-1$
	

	
	
	
	public static String deleteVocab ;
	public static String deleteConfiguration ;
	public static String deletePlugins ;
	
	public static String error;
	public static String inputPathOptionError;
	
	public static String startPreinstall;
	public static String endPreinstall;

	
	static {
		
		ResourceBundle rs = ResourceBundle.getBundle(BUNDLE_NAME);
		
		Class<?> clazz = Messages.class;
		Field[] fields = clazz.getFields();
		for(Field field : fields) {
			if(rs.containsKey(field.getName())) {
				try {
					field.set(null, rs.getObject(field.getName()));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private Messages() {
	}
	
	/**
	 * @param key
	 * @return message corresponding to key
	 */
	public static String getString(String key)
	{
		try
		{
			Locale locale = IzpackLocal.getLocale();
			StringBuffer bundleName = new StringBuffer(BUNDLE_NAME);
			if (locale == Locale.FRANCE || locale == Locale.UK)
			{
				bundleName.append("_");
				bundleName.append(locale.toString());
			}
			ResourceBundle resource = ResourceBundle.getBundle(bundleName.toString());
			return resource.getString(key);
		} 
		catch (MissingResourceException e) 
		{
			return '!' + key + '!';
		}
	}
}
