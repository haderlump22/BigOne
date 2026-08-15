# BigOne
Tool to manage your Financials and calculate what you can save per Month (or not...O_o)

## Buildinfos
I use Maven. So simply install maven and do:
    - mvn compile
    - mvn exec:java

You also can create an app-image with a complete runtime:
    - mvn package

Then you find under ./maven-jlink/classifiers/app-image/bin the start script "runBigone".

## Backend need's
The app needs a postgres DB. Under the HOME Folder of the User that runs that app, must exist in the Directory BigOneConfig, a json file with the followed content:

~~~
{
  "DbDrv":"org.postgresql.Driver",
  "DbPw":"<password>",
  "DbName":"<nameOfDb>",
  "DbUrl":"jdbc:postgresql:\/\/localhost:5432\/",
  "DbUserName":"<dbUser>"
}
~~~
