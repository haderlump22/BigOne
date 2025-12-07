package de.rachel.bigone.records;

import java.time.LocalDate;

public record RacTableRow(LocalDate valueDate, String cdtDbtInd, String cdtDbtName, Double amount, String comment,
                LocalDate billingMonth, String bookingEvent) {
};
