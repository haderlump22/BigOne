package de.rachel.bigone.records;

import java.time.LocalDate;

public record ValuesTableRow(Integer transaktionsId, String cdtDbtIndicator, LocalDate date, Double amount, String comment,
                LocalDate billingMonth, String bookingEvent) {
};
