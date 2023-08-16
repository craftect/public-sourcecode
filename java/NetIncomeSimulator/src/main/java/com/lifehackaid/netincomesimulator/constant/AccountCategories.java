package com.lifehackaid.netincomesimulator.constant;

public enum AccountCategories {
	Revenue("売上"), 
	NecessaryExpenses("必要経費"), 
	IncomeDeduction("所得控除"), 
	TaxDeduction("税額控除");

	private final String DESCRIPTION;

	AccountCategories(String description) {
		this.DESCRIPTION = description;
	}

	public String getDescription() {
		return DESCRIPTION;
	}
}