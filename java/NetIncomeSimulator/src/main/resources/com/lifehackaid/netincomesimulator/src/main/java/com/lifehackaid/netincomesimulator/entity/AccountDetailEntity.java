package com.lifehackaid.netincomesimulator.entity;

import com.lifehackaid.netincomesimulator.constant.AccountCategories;

public class AccountDetailEntity {
	private String Id;
	private AccountCategories category;
	private String summary;
	private Long unitValue;
	private Long unitAmount;
	private Boolean keepValue;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public AccountCategories getCategory() {
		return category;
	}

	public void setCategory(AccountCategories category) {
		this.category = category;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Long getUnitValue() {
		return unitValue;
	}

	public void setUnitValue(Long unitValue) {
		this.unitValue = unitValue;
	}

	public Long getUnitAmount() {
		return unitAmount;
	}

	public void setUnitAmount(Long unitAmount) {
		this.unitAmount = unitAmount;
	}

	public Boolean getKeepValue() {
		return keepValue;
	}

	public void setKeepValue(Boolean keepValue) {
		this.keepValue = keepValue;
	}
}
