package de.rachel.bigone.Records;

import java.sql.Date;

public record ExpenditureDetailTableRow(Integer ExpenditureId, String Description, Double Amount, String DivideType, Date ValidUntil, String ExpenditureHint) {
};
