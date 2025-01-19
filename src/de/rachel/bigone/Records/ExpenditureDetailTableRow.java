package de.rachel.bigone.Records;

import java.sql.Date;

public record ExpenditureDetailTableRow(String Description, Double Amount, String DivideType, Date ValidUntil, String PartyName1, Double AmountParty1,
        String PartyName2, Double AmountParty2, String Hint) {
};
