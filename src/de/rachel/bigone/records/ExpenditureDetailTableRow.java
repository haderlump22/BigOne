package de.rachel.bigone.records;

import java.time.LocalDate;

public record ExpenditureDetailTableRow(Integer ExpenditureId, String Description, Double Amount, String DivideType,
        LocalDate ValidUntil, String ExpenditureHint) {
};
