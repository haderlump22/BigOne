package de.rachel.bigone.Records;

import java.sql.Date;

public record ExpenditureDetailTableRow(Integer ExpenditureDetailId, String Description, Double Amount, String DivideType, Date ValidUntil) {
};
