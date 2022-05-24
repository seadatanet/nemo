package fr.ifremer.nemo.delete;

import java.io.File;

public class InstallDirExist {

	
	public static boolean isNotExistInstallDir(String path)
	{
		File f = new File(path);
		return !(f.exists() && f.isDirectory());
	}
}
