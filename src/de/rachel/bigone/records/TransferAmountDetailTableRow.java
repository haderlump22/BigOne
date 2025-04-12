package de.rachel.bigone.records;

import java.sql.Date;

public record TransferAmountDetailTableRow(Integer TransferAmountId, String NameOfParty, Double Amount,
        Date ValidUntil) {
};
