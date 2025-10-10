package de.rachel.bigone.records;

import java.sql.Date;

public record ClosingSumValueRecord(Integer abschlussSummenId, Date abschlussMonat, String summenArt, double betrag,
        String detailQuelle) {
};
