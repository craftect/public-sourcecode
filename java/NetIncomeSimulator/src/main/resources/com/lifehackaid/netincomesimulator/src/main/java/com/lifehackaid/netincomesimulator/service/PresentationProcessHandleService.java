package com.lifehackaid.netincomesimulator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lifehackaid.netincomesimulator.common.DateUUIDGeneratorUtil;
import com.lifehackaid.netincomesimulator.form.AccountDetailForm;
import com.lifehackaid.netincomesimulator.repository.AccountDetailRepository;

@Service
public class PresentationProcessHandleService {

	private MessageSource messageSource;
	private NetIncomeCalcService netIncomeCalcService;
	private AccountDetailRepository accountDetailRepository;

	@Autowired
	public PresentationProcessHandleService(MessageSource messageSource, NetIncomeCalcService netIncomeCalcService,
			AccountDetailRepository accountDetailRepository) {
		this.messageSource = messageSource;
		this.netIncomeCalcService = netIncomeCalcService;
		this.accountDetailRepository = accountDetailRepository;

	}

	// Springフレームワーク内のバインディング処理でエラーがないかどうかを確認するメソッド
	public boolean hasBindingErrorWithMsgProcess(RedirectAttributes redirectAttributes, BindingResult bindingResult) {

		// 前画面から引き継がれているエラーMSGがあれば取得。（リダイレクト経由となるのでフラッシュスコープより取得）
		List<String> errorMessages = getMessagesFromFlashAttributes(redirectAttributes, "errorMessages");

		boolean errorOccuredFlag = false;

		// BindingResultの中身をチェックしてエラー発生していれば、本アプリケーションのエラーメッセージとする
		for (FieldError error : bindingResult.getFieldErrors()) {
			String errorMessage = messageSource.getMessage(error, Locale.getDefault());
			errorMessages.add(errorMessage);
			errorOccuredFlag = true;
		}

		// エラーMSGが空でなければ、フラッシュスコープにバインドして次の画面に引き渡し（本メソッドでエラーがなかった場合も考慮）
		if (!errorMessages.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
		}

		return errorOccuredFlag;
	}

	// 勘定明細リストにリスト追加してよいか評価するメソッド（削除してもよければtrue)
	public boolean hasErrorToAddAccountDetailWithMsgProcess(RedirectAttributes redirectAttributes,
			List<AccountDetailForm> accountDetailList, AccountDetailForm targetAccountDetailDto) {

		// 前画面から引き継がれているエラーMSGがあれば取得。（リダイレクト経由となるのでフラッシュスコープより取得）
		List<String> errorMessages = getMessagesFromFlashAttributes(redirectAttributes, "errorMessages");

		// 検証用に一時的な勘定明細リストを生成
		List<AccountDetailForm> tempAccountDetailList = new ArrayList<>();
		tempAccountDetailList.addAll(accountDetailList);
		tempAccountDetailList.add(targetAccountDetailDto);
		boolean errorOccuredFlag = false;

		// 一時的な勘定明細リストの合計値がマイナスにならないかチェック
		if (netIncomeCalcService.calcTaxableIncomeAmount(tempAccountDetailList) < 0) {
			String errorMessage = messageSource.getMessage("ui.NegativeAggregationCheck", null, Locale.getDefault());
			errorMessages.add(errorMessage);
			errorOccuredFlag = true;
		}

		// エラーMSGが空でなければ、フラッシュスコープにバインドして次の画面に引き渡し（本メソッド以外のエラーが存在する場合も考慮）
		if (!errorMessages.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
		}

		return errorOccuredFlag;

	}

	// 勘定明細リストの編集対象明細を編集してもよいか評価するメソッド（削除してもよければtrue)
	public boolean hasErrorToUpdateAccountDetailWithMsgProcess(RedirectAttributes redirectAttributes,
			List<AccountDetailForm> accountDetailList, AccountDetailForm targetAccountDetailDto, String index) {

		// 前画面から引き継がれているエラーMSGがあれば取得。（リダイレクト経由となるのでフラッシュスコープより取得）
		List<String> errorMessages = getMessagesFromFlashAttributes(redirectAttributes, "errorMessages");

		// 検証用に一時的な勘定明細リストを生成
		List<AccountDetailForm> tempAccountDetailList = new ArrayList<>();
		tempAccountDetailList.addAll(accountDetailList);
		tempAccountDetailList.set(Integer.parseInt(index), targetAccountDetailDto);
		boolean errorOccuredFlag = false;

		// 一時的な勘定明細リストの合計値がマイナスにならないかチェック
		if (netIncomeCalcService.calcTaxableIncomeAmount(tempAccountDetailList) < 0) {
			String errorMessage = messageSource.getMessage("ui.NegativeAggregationCheck", null, Locale.getDefault());
			errorMessages.add(errorMessage);
			errorOccuredFlag = true;
		}

		// エラーMSGが空でなければ、フラッシュスコープにバインドして次の画面に引き渡し（本メソッドでエラーがなかった場合も考慮）
		if (!errorMessages.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
		}

		return errorOccuredFlag;

	}

	// 勘定明細リストからリストを削除してよいか評価するメソッド（削除してもよければtrue)
	public boolean hasErrorToRemoveAccountDetailWithMsgProcess(RedirectAttributes redirectAttributes,
			List<AccountDetailForm> accountDetailList, String index) {

		// 前画面から引き継がれているエラーMSGがあれば取得。（リダイレクト経由となるのでフラッシュスコープより取得）
		List<String> errorMessages = getMessagesFromFlashAttributes(redirectAttributes, "errorMessages");

		// 検証用に一時的な勘定明細リストを生成
		List<AccountDetailForm> tempAccountDetailList = new ArrayList<>();
		tempAccountDetailList.addAll(accountDetailList);
		tempAccountDetailList.remove(Integer.parseInt(index));
		boolean errorOccuredFlag = false;

		// 合計が負の数にならないかチェック
		if (netIncomeCalcService.calcTaxableIncomeAmount(tempAccountDetailList) < 0) {
			String errorMessage = messageSource.getMessage("ui.NegativeAggregationCheck", null, Locale.getDefault());
			errorMessages.add(errorMessage);
			errorOccuredFlag = true;
		}

		// エラーMSGが空でなければ、フラッシュスコープにバインドして次の画面に引き渡し
		if (!errorMessages.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
		}

		return errorOccuredFlag;

	}

	public boolean hasErrorToShowAccountDetailWithMsgProcess(RedirectAttributes redirectAttributes,
			List<AccountDetailForm> accountDetailList, String recordId) {

		// 前画面から引き継がれているエラーMSGがあれば取得。（リダイレクト経由となるのでフラッシュスコープより取得）
		List<String> errorMessages = getMessagesFromFlashAttributes(redirectAttributes, "errorMessages");
		boolean errorOccuredFlag = false;

		if (StringUtils.hasText(recordId)) {

			// recordId長が仕様と異なる場合
			if (recordId.length() != DateUUIDGeneratorUtil.generateDateUUID().length()) {
				errorOccuredFlag = true;
			}

			// データベースにデータが存在しない場合(既にエラー状態となっている場合はスキップ）
			if (!errorOccuredFlag && accountDetailRepository.loadAccountDetailFormList(recordId).size() == 0) {
				errorOccuredFlag = true;
			}

			// エラーメッセージはセキュリティの観点から具体的には定義せず、ひとまとめにする。
			if (errorOccuredFlag) {
				String errorMessage = messageSource.getMessage("ui.RecordIdCheck", null, Locale.getDefault());
				errorMessages.add(errorMessage);
			}

		}

		// エラーMSGが空でなければ、フラッシュスコープにバインドして次の画面に引き渡し（本メソッドの処理以外の引継ぎも考慮）
		if (!errorMessages.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
		}

		return errorOccuredFlag;

	}

	// プロパティファイルにあるメッセージリソースキー経由で通知メッセージを登録するメソッド。（表示色の変更などエラーメッセージとは別管理）
	public void addNotificationMessage(RedirectAttributes redirectAttributes, String messagePlaceHolderName) {

		List<String> notificationMessages = getMessagesFromFlashAttributes(redirectAttributes, "notificationMessages");

		String notificationMessage = messageSource.getMessage(messagePlaceHolderName, null, Locale.getDefault());

		notificationMessages.add(notificationMessage);

		redirectAttributes.addFlashAttribute("notificationMessages", notificationMessages);

	}

	// 内部メソッド：フラッシュスコープにある属性データを解析して、あればListに追記。（リダイレクトで画面をまたぐ場合、遷移開始元のデータが削除されてしまうため）
	private List<String> getMessagesFromFlashAttributes(RedirectAttributes redirectAttributes, String attributeName) {
		List<String> messages = new ArrayList<>();

		Object existingMessages = redirectAttributes.getFlashAttributes().get(attributeName);

		if (existingMessages != null && existingMessages instanceof List) {
			messages.addAll((List<String>) existingMessages);
		}

		return messages;
	}

}
