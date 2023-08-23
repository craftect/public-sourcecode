package com.lifehackaid.netincomesimulator.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lifehackaid.netincomesimulator.constant.AccountCategories;
import com.lifehackaid.netincomesimulator.form.AccountDetailForm;

@Service
public class NetIncomeCalcService {

	@Value("${app.business.tax.residentTaxRate}")
	private String residentTaxRate;

	@Value("${app.business.tax.calcSpecialReconstructionIncomeTaxRate}")
	private String calcSpecialReconstructionIncomeTaxRate;

	@Value("${app.business.tax.incomeTaxThresholdList}")
	private List<String> incomeTaxThresholdList;

	@Value("${app.business.tax.incomeTaxRateList}")
	private List<String> incomeTaxRateList;

	@Value("${app.business.tax.incomeTaxDeductionList}")
	private List<String> incomeTaxDeductionList;

	/**
	 * 指定されたカテゴリの金額を集計します。
	 *
	 * @param accountCategory 集計するカテゴリ
	 * @return 集計された金額
	 */
	public long aggregateByCategory(List<AccountDetailForm> accountDetailFormList,
			Enum<AccountCategories> accountCategory) {

		long totalAmount = 0L;

		for (AccountDetailForm accountDetailForm : accountDetailFormList) {
			if (accountDetailForm.getCategory().equals(accountCategory)) {
				totalAmount += accountDetailForm.getTotalValue();
			}
		}

		return totalAmount;

	}

	/**
	 * 指定されたカテゴリの金額を、価値留保のあるものだけ集計します。
	 *
	 * @param accountCategory 集計するカテゴリ
	 * @return 集計された金額
	 */
	public long aggregateByCategoryWithKeepValue(List<AccountDetailForm> accountDetailFormList,
			Enum<AccountCategories> accountCategory) {

		long totalAmount = 0L;

		for (AccountDetailForm accountDetailForm : accountDetailFormList) {
			if (accountDetailForm.getCategory().equals(accountCategory) && accountDetailForm.getKeepValue()) {
				totalAmount += accountDetailForm.getTotalValue();
			}
		}

		return totalAmount;

	}

	/**
	 * 所得金額を計算します。（収入金額 - 必要経費）
	 *
	 * https://www.nta.go.jp/publication/pamph/koho/kurashi/html/01_1.htm
	 *
	 * @return 所得金額
	 */
	public long calcIncomeAmount(List<AccountDetailForm> accountDetailFormList) {
		return aggregateByCategory(accountDetailFormList, AccountCategories.Revenue)
				- aggregateByCategory(accountDetailFormList, AccountCategories.NecessaryExpenses);
	}

	/**
	 * 課税所得金額を計算します。（所得金額 - 所得控除）
	 * https://www.nta.go.jp/publication/pamph/koho/kurashi/html/01_1.htm
	 *
	 * @return 課税所得金額
	 */
	public long calcTaxableIncomeAmount(List<AccountDetailForm> accountDetailFormList) {
		return calcIncomeAmount(accountDetailFormList)
				- aggregateByCategory(accountDetailFormList, AccountCategories.IncomeDeduction);
	}

	/**ibent
	 * 国税庁掲載の速算表に基づいて所得税額を計算します。 （税率、控除額、超過累進課税時の閾値はapplication.xmlにて定義済。
	 * https://www.nta.go.jp/taxes/shiraberu/taxanswer/shotoku/2260.htm
	 * 引数有のメソッドは単体テスト用。
	 *
	 * @return 所得税額
	 */

	public long calcIncomeTaxAmount(List<AccountDetailForm> accountDetailFormList){
		return calcIncomeTaxAmount(calcTaxableIncomeAmount(accountDetailFormList));
	}
	
	private long calcIncomeTaxAmount(long ｔaxableIncomeAmount) {
		BigDecimal ｔaxableIncomeAmountBD = BigDecimal.valueOf(ｔaxableIncomeAmount);
		for (int i = incomeTaxThresholdList.size() - 1; i >= 0; i--) {
			BigDecimal threshold = new BigDecimal(incomeTaxThresholdList.get(i));
			if (ｔaxableIncomeAmountBD.compareTo(threshold) >= 0) {
				BigDecimal taxRate = new BigDecimal(incomeTaxRateList.get(i));
				BigDecimal deduction = new BigDecimal(incomeTaxDeductionList.get(i));
				return ｔaxableIncomeAmountBD.multiply(taxRate).subtract(deduction).longValue();
			}
		}
		return 0L;
	}

	/**
	 * 住民税額を計算します。（簡易計算のため課税所得の10％）厳密には基礎控除が所得税と違う等あるが、少額につき割愛
	 * https://www.nta.go.jp/taxes/shiraberu/taxanswer/shotoku/2260.htm
	 *
	 * @return 住民税額
	 */

	public long calcResidentTaxAmount(List<AccountDetailForm> accountDetailFormList){
		return calcResidentTaxAmount(calcTaxableIncomeAmount(accountDetailFormList));
	}
	
	private long calcResidentTaxAmount(long ｔaxableIncomeAmount) {

		BigDecimal incomeAmount = BigDecimal.valueOf(ｔaxableIncomeAmount);

		if (incomeAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return 0L;
		}

		return incomeAmount.multiply(new BigDecimal(residentTaxRate)).longValue();
	}

	/**
	 * 所得税から特別復興所得税額を計算します。特別復興所得税計算用メソッド（所得税額×特別復興所得税率）
	 * https://www.nta.go.jp/taxes/shiraberu/taxanswer/shotoku/2260.htm
	 *
	 * @return 特別復興所得税額
	 */

	public long calcSpecialReconstructionIncomeTaxAmount(List<AccountDetailForm> accountDetailFormList){
		return calcSpecialReconstructionIncomeTaxAmount(calcIncomeTaxAmount(accountDetailFormList));
	}
	
	private long calcSpecialReconstructionIncomeTaxAmount(long calcIncomeTaxAmount) {

		BigDecimal incomeAmount = BigDecimal.valueOf(calcIncomeTaxAmount);

		if (incomeAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return 0L;
		}

		return incomeAmount.multiply(new BigDecimal(calcSpecialReconstructionIncomeTaxRate)).longValue();
	}

	/**
	 * 所得税、住民税、特別復興所得税の総額を計算します。
	 *
	 * @return 所得税、住民税、特別復興所得税の総額
	 */
	public long calcIncomeAndResidentAndSpecialIncomeTaxAmount(List<AccountDetailForm> accountDetailFormList) {

		long incomeTaxAmount = calcIncomeTaxAmount(accountDetailFormList);
		long residentTaxAmount = calcResidentTaxAmount(accountDetailFormList);
		long specialReconstructionIncomeTaxAmount = calcSpecialReconstructionIncomeTaxAmount(accountDetailFormList);
		return incomeTaxAmount + specialReconstructionIncomeTaxAmount + residentTaxAmount;

	}

	/**
	 * 課税所得に加えて価値留保フラグが付与された明細を手残りとして計算します。
	 *
	 * @return 価値留保ベースの手残額
	 */
	public long calcReservedValueBalanceAmount(List<AccountDetailForm> accountDetailFormList) {

		return calcTaxableIncomeAmount(accountDetailFormList) // 課税所得
				+ aggregateByCategoryWithKeepValue(accountDetailFormList, AccountCategories.NecessaryExpenses)
				+ aggregateByCategoryWithKeepValue(accountDetailFormList, AccountCategories.IncomeDeduction)
				+ aggregateByCategoryWithKeepValue(accountDetailFormList, AccountCategories.TaxDeduction);

	}

}
