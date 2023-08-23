package com.lifehackaid.netincomesimulator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.lifehackaid.netincomesimulator.constant.AccountCategories;
import com.lifehackaid.netincomesimulator.form.AccountDetailForm;

@SpringBootTest
public class NetIncomeCalcServiceTest {

	NetIncomeCalcService netIncomeCalcService;

	@Autowired
	NetIncomeCalcServiceTest(NetIncomeCalcService netIncomeCalcService) {
		this.netIncomeCalcService = netIncomeCalcService;
	}

	List<AccountDetailForm> accountDetailFormList = new ArrayList<>();

	@BeforeEach
	void setUp() {

		accountDetailFormList.add(new AccountDetailForm(AccountCategories.Revenue, "売上", 600000L, 12L, true));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "家賃", 10000L, 12L, false));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "携帯電話使用料", 3000L, 12L, false));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "固定光回線使用料", 5500L, 12L, false));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "レンタルオフィス使用料", 2000L, 12L, false));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "AWS使用料", 3000L, 12L, false));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "ChatGPT使用料", 2800L, 12L, false));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.NecessaryExpenses, "交通費", 10000L, 12L, false));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "基礎控除(住民税ベース)", 430000L, 1L, true));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "青色申告控除", 650000L, 1L, true));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "配偶者控除", 380000L, 1L, true));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "健康保険料", 75000L, 12L, false));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "国民年金保険料", 33000L, 12L, false));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "IDeCo", 67000L, 12L, false));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "小規模企業共済", 20000L, 12L, false));
		accountDetailFormList
				.add(new AccountDetailForm(AccountCategories.IncomeDeduction, "経営セーフティ共済", 20000L, 12L, false));
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.TaxDeduction, "外国税額控除", 10000L, 1L, true));

	}

	@Test
	void verifyCalcIncomeAmount() {
		assertEquals(netIncomeCalcService.calcIncomeAmount(accountDetailFormList), 6764400L);
	}

	@Test
	void verifyAggregateByCategory() {

		assertEquals(netIncomeCalcService.aggregateByCategory(accountDetailFormList, AccountCategories.Revenue),
				7200000L);
		assertEquals(
				netIncomeCalcService.aggregateByCategory(accountDetailFormList, AccountCategories.NecessaryExpenses),
				435600);
		assertEquals(netIncomeCalcService.aggregateByCategory(accountDetailFormList, AccountCategories.IncomeDeduction),
				4040000L);
		assertEquals(netIncomeCalcService.aggregateByCategory(accountDetailFormList, AccountCategories.TaxDeduction),
				10000L);

	}

	//　@formatter:off
	// https://www.nta.go.jp/taxes/shiraberu/taxanswer/shotoku/2260.htm
	/* ▼Excel貼り付け用
		所得,控除額,税率,所得税額 
		1,949,000,0,0.05,97,450
		1,950,000,97,500,0.1,97,500
		3,299,000,97,500,0.1,232,400
		3,300,000,427,500,0.2,232,500
		6,949,000,427,500,0.2,962,300
		6,950,000,636,000,0.23,962,500
		8,999,000,636,000,0.23,1,433,770
		9,000,000,1,536,000,0.33,1,434,000
		17,999,000,1,536,000,0.33,4,403,670
		18,000,000,2,796,000,0.4,4,404,000
		39,999,000,2,796,000,0.4,13,203,600
		40,000,000,4,796,000,0.45,13,204,000

	 */

	@ParameterizedTest
	@CsvSource({
		"1949000,97450",
		"1950000,97500",
		"3299000,232400",
		"3300000,232500",
		"6949000,962300",
		"6950000,962500",
		"8999000,1433770",
		"9000000,1434000",
		"17999000,4403670",
		"18000000,4404000",
		"39999000,13203600",
		"40000000,13204000",
		"50000000,17704000",
		"7000000,974000",
		"0, 0", // ゼロの場合
		"-1000, 0" // マイナスの場合
	})
	//@formatter:on 
	void verifyCalcIncomeTaxAmount(long incomeAmount, long expectedValue) {

		try {
			Method method = NetIncomeCalcService.class.getDeclaredMethod("calcIncomeTaxAmount", long.class);
			method.setAccessible(true);

			assertEquals(expectedValue, (long) method.invoke(netIncomeCalcService, incomeAmount));

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			fail("Unexpected exception occurred: " + e.getMessage());
		}

	}

	
	// https://advisors-freee.jp/article/category/cat-big-01/cat-small-01/6093/
	// https://www.nta.go.jp/taxes/shiraberu/shinkoku/tebiki2017/b/03/order4/3-4_41_1.htm
	// @formatter:off
	@ParameterizedTest
	@CsvSource({
		"196500,4126",
		"333750,7008",
		"0, 0", // ゼロの場合
		"-1000, 0" // マイナスの場合
	})
	// @formatter:on
	void verifyCalcSpecialReconstructionIncomeTaxAmount(long incomeAmount, long expectedValue) {

		try {

			Method method = NetIncomeCalcService.class.getDeclaredMethod("calcSpecialReconstructionIncomeTaxAmount",
					long.class);
			method.setAccessible(true);

			assertEquals(expectedValue, method.invoke(netIncomeCalcService, incomeAmount));

		} catch (Exception e) {
			fail("Unexpected exception occured: " + e.getMessage());
		}

	}

	// https://www.city.nerima.tokyo.jp/kurashi/zei/jyuminzei/keisan/keisanrei-26-1.html
	// 算出所得割額
	// @formatter:off
	@ParameterizedTest
	@CsvSource({
		"2823000,282300",
		"0, 0", // ゼロの場合
		"-1000, 0" // マイナスの場合
	})
	// @formatter:on
	void verifyCalcResidentTaxAmount(long incomeAmount, long expectedValue) {

		try {
			Method method = NetIncomeCalcService.class.getDeclaredMethod("calcResidentTaxAmount", long.class);
			method.setAccessible(true);
			assertEquals(expectedValue, method.invoke(netIncomeCalcService, incomeAmount));

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			fail("Unexpected exception occurred: " + e.getMessage());
		}
	}

	// https://www.city.nerima.tokyo.jp/kurashi/zei/jyuminzei/keisan/keisanrei-26-1.html
	@Test
	void verifyReservedValueBalanceAmount() {

		long calculatedTaxAmountValue = netIncomeCalcService.calcReservedValueBalanceAmount(accountDetailFormList);
		long expectedTaxAmountValue = 4194400;

		assertEquals(expectedTaxAmountValue, calculatedTaxAmountValue);

	}

	@Test
	void verifyCalcIncomeAndResidentAndSpecialIncomeTaxAmount() {

		long calculatedTaxAmountValue = netIncomeCalcService
				.calcIncomeAndResidentAndSpecialIncomeTaxAmount(accountDetailFormList);

		assertEquals(calculatedTaxAmountValue, 451053);

	}

}