package de.rachel.app;

/**
 * Read some settings that the Programm need
 * from a config file in a Subdirectory in the UserHome
 *
 * @author Normen Rachel
 *
 */
public class Config {
  private String databaseDriver, databaseUrl, databaseName, userName, password;

  public String getDatabaseDriver() {
      return databaseDriver;
  }

  public String getDatabaseUrl() {
      return databaseUrl;
  }

  public String getDatabaseName() {
      return databaseName;
  }

  public String getUserName() {
      return userName;
  }

  public String getPassword() {
      return password;
  }

  public void setDatabaseDriver(String databaseDriver) {
    this.databaseDriver = databaseDriver;
  }

  public void setDatabaseUrl(String databaseUrl) {
    this.databaseUrl = databaseUrl;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
