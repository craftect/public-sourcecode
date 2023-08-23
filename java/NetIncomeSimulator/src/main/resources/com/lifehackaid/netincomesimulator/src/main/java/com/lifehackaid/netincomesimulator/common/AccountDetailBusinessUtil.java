package com.lifehackaid.netincomesimulator.common;

import java.util.List;

import com.lifehackaid.netincomesimulator.constant.AccountCategories;
import com.lifehackaid.netincomesimulator.form.AccountDetailForm;

import java.util.ArrayList;


public class AccountDetailBusinessUtil {

	public static List<AccountDetailForm> createDefaultAccountDetailFormList() {
		List<AccountDetailForm> accountDetailFormList = new ArrayList<>();

		accountDetailFormList.add(new AccountDetailForm(AccountCategories.Revenue, "売上", 770000L,12L, true));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "家賃", 10000L,12L, false));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "携帯電話使用料", 3000L,12L, false));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "固定光回線使用料", 5500L,12L, false));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "レンタルオフィス使用料", 2000L,12L, false));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "AWS使用料", 3000L,12L, false));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "交通費", 20000L,12L, false));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "消費税納付(簡易課税50％)", 35000L,12L, false));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "基礎控除(住民税ベース)", 430000L,1L, true));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "青色申告控除", 650000L,1L, true));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "配偶者控除", 380000L,1L, true));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "健康保険料(配偶者分込)", 90000L,12L, false));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "国民年金保険料(配偶者分込)", 35000L,12L, false));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "iDeCo", 67000L,12L, false));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "小規模企業共済", 70000L,12L, false));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.TaxDeduction, "外国税額控除", 10000L,1L, true));

		return accountDetailFormList;
	}
}
