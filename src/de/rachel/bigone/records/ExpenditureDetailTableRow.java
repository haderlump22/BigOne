package de.rachel.bigone.records;

import java.time.LocalDate;

public record ExpenditureDetailTableRow(Integer expenditureId, String description, Double amount, String divideType,
        LocalDate validUntil, String expenditureHint, Integer frequency) {
};
