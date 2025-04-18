package de.rachel.bigone.records;

public record JointAccountClosingDetailTableRow(int EventId, String NameOfExpenditure, Double ActualAmount, Double PlanAmount,
        Double Difference) {
};
