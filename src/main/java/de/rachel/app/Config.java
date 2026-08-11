package de.rachel.app;

/**
 * Read some settings that the Programm need
 * from a config file in a Subdirectory in the UserHome
 *
 * @author Normen Rachel
 *
 */
public class Config {
  private String DbDrv, DbUrl, DbName, DbUserName, DbPw;

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
