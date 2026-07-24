package records;

import java.sql.Date;

public record TransferAmountDetailTableRow(Integer TransferAmountId, String NameOfParty, Double Amount,
        Date ValidUntil) {
};
