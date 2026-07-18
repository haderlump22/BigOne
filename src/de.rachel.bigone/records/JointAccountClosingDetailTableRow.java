package de.rachel.bigone.records;

public record JointAccountClosingDetailTableRow(int closingDetailId, String nameOfExpenditure, Double actualAmount, Double planAmount,
        Double difference) {
};
