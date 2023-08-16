package com.lifehackaid.netincomesimulator.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

import com.lifehackaid.netincomesimulator.constant.AccountCategories;

public class AccountDetailForm {

	private AccountCategories category;
	@NotBlank
	private String summary;

	@NotNull
	@Min(value = 1)
	private Long unitValue;

	@NotNull
	@Min(value = 1)
	private Long unitAmount;
	private Boolean keepValue = false;

	public AccountDetailForm(AccountCategories category, String summary, Long unitValue, Long unitAmount,
			Boolean keepValue) {
		super();
		this.category = category;
		this.summary = summary;
		this.unitValue = unitValue;
		this.unitAmount = unitAmount;
		this.keepValue = keepValue;

	}

	public AccountCategories getCategory() {
		return category;
	}

	public Long getUnitValue() {
		return unitValue;
	}

	public Long getUnitAmount() {
		return unitAmount;
	}

	public String getSummary() {
		return summary;
	}

	public Boolean getKeepValue() {
		return keepValue;
	}

	public Long getTotalValue() {
		return (new BigDecimal(this.unitAmount)).multiply(new BigDecimal(this.unitValue)).longValue();
	}

}
