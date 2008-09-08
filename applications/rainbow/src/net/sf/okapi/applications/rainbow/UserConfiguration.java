package net.sf.okapi.applications.rainbow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class UserConfiguration extends Properties {
	
	private static final long serialVersionUID = 1L;
	
	public UserConfiguration () {
		super();
		defaults = new Properties();
		defaults.setProperty("loadLastFile", "true");
	}

	public void load () {
		try {
			InputStream input = new FileInputStream(
				new File(System.getProperty(
					"user.home")+File.separatorChar+"."+MainForm.APPNAME));
			load(input);
		}
		catch ( IOException e ) {
			// Don't care about not reading this file
		}
	}
	
	public void save () {
		try {
			OutputStream output = new FileOutputStream(
				new File(System.getProperty(
					"user.home")+File.separatorChar+"."+MainForm.APPNAME));
			store(output, MainForm.APPNAME + " - " + Res.getString("VERSION"));
		}
		catch ( IOException e ) {
			// Don't care about not writing this file
		}
	}
	
	@Override
	public Object setProperty (String key,
		String value)
	{
		if ( value == null ) return remove(key);
		else return super.setProperty(key, value);
	}
	
	public boolean getBoolean (String key) {
		return "true".equals(getProperty(key, "false"));
	}
	
	public Object setProperty (String key,
		boolean value)
	{
		return super.setProperty(key, (value ? "true" : "false"));
	}
}
