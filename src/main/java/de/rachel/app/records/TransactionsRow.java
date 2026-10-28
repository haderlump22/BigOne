package de.rachel.app.records;

import java.time.LocalDate;

public record TransactionsRow(String creditDebitIndicator, Integer accountId, LocalDate liquiDate, LocalDate valueDate, Double amount,
    String comment, Integer eventId) {
};
