module de.rachel.bigone {
    requires java.sql;
    requires java.desktop;
    requires jcalendar;
    requires com.google.gson;
    // Erlaubt Gson den Reflection-Zugriff auf alle Klassen in diesem Paket
    opens de.rachel.app to com.google.gson;
}
