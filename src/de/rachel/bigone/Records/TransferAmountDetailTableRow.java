package de.rachel.bigone.Records;

import java.sql.Date;

public record TransferAmountDetailTableRow(Integer TransferAmountId, String NameOfParty, Double Amount, Date ValidUntil){};
