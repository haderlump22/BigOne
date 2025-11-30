package de.rachel.bigone.records;

import java.time.LocalDate;

public record RacTableRow(LocalDate valueDate, String giveOrGet, Double amount, String comment,
        String dbitOrCrdt, LocalDate billingMonth, Integer eventId) {
};
