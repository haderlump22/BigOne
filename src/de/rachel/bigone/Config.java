package de.rachel.bigone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * Read some settings that the Programm need 
 * from a config file in a Subdirectory in the UserHome
 * 
 * @author Normen Rachel
 *
 */
public class Config {
	private String DbDrv, DbUrl, DbName, DbUserName, DbPw;
	Config() {
		File ConfigDir = new File(System.getProperty("user.home") + "/BigOneConfig");
		File ConfigFile = new File(ConfigDir.getPath()+"/BigOneConfig.json");
		
		try {
			// check if Dir exist, else create it
			if (!checkConfigDir(ConfigDir)) {
				
				ConfigDir.mkdir();
				ConfigFile.createNewFile();
				
				// if configfile generated than put some template settings
				writeTemplateConfig(new FileWriter(ConfigFile));
			} else {
				// check if File exist, else create it
				if (!checkConfigFile(ConfigFile)) {
					
					ConfigFile.createNewFile();
					
					// if configfile generated than put some template settings
					writeTemplateConfig(new FileWriter(ConfigFile));
				} else {
					// if file exist then try to read the necessary info
					readSettings(new FileReader(ConfigFile));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readSettings(FileReader ConfigFile) {
		JSONParser parser = new JSONParser();
		
        try {

            JSONObject jsonObject = (JSONObject) parser.parse(ConfigFile);

            DbDrv = (String) jsonObject.get("DbDrv");
            DbUrl = (String) jsonObject.get("DbUrl");
            DbName = (String) jsonObject.get("DbName");
            DbUserName = (String) jsonObject.get("DbUserName");
            DbPw = (String) jsonObject.get("DbPw");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("keine JSON Datei, oder falsches Format");
        }
	}

	private boolean checkConfigDir(File ConfigDir) {
		if (ConfigDir.isDirectory())
			return true;
		else
			return false;
	}
	
	private boolean checkConfigFile(File ConfigFile) {
		if (ConfigFile.isFile())
			return true;
		else
			return false;
	}
	
	@SuppressWarnings("unchecked")
	private void writeTemplateConfig(FileWriter ConfigFile) {
		JSONObject obj = new JSONObject();

		obj.put("DbDrv", "org.postgresql.Driver");
		obj.put("DbUrl", "jdbc:postgresql://localhost:5432/");
		obj.put("DbName", "bigone");
		obj.put("DbUser", "DbUser");
		obj.put("DbPw", "DbPw");

		try {

			ConfigFile.write(obj.toJSONString());
			ConfigFile.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getDbDrv() {
		return DbDrv;
	}
	public String getDbUrl() {
		return DbUrl;
	}
	public String getDbName() {
		return DbName;
	}
	public String getDbUserName() {
		return DbUserName;
	}
	public String getDbPw() {
		return DbPw;
	}
}