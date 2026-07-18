package de.rachel.bigone.records;

import java.sql.Date;

public record SalaryBasesIncomeDetailTableRow(String NameOfParty, Double Amount, Date ValidUntil, String Type) {
};
