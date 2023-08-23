package com.lifehackaid.netincomesimulator.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.lifehackaid.netincomesimulator.common.AccountDetailBusinessUtil;
import com.lifehackaid.netincomesimulator.common.DateUUIDGeneratorUtil;
import com.lifehackaid.netincomesimulator.constant.AccountCategories;
import com.lifehackaid.netincomesimulator.constant.NavigationConstants;
import com.lifehackaid.netincomesimulator.form.AccountDetailForm;
import com.lifehackaid.netincomesimulator.repository.AccountDetailRepository;
import com.lifehackaid.netincomesimulator.service.NetIncomeCalcService;
import com.lifehackaid.netincomesimulator.service.PresentationProcessHandleService;

import jakarta.validation.Valid;

@Controller
@SessionAttributes(value = "accountDetailFormList")
public class AccountDetailController {
	private NetIncomeCalcService netIncomeCalcService;
	private PresentationProcessHandleService presentationProcessHandleService;
	private AccountDetailRepository accountDetailRepository;

	public AccountDetailController(NetIncomeCalcService netIncomeCalcService,
			PresentationProcessHandleService presentationProcessHandleService,
			AccountDetailRepository accountDetailRepository) {
		this.netIncomeCalcService = netIncomeCalcService;
		this.presentationProcessHandleService = presentationProcessHandleService;
		this.accountDetailRepository = accountDetailRepository;
	}

	// SessionAttributes使用時に当該リスト属性が存在することを保障ためのロジック。
	@ModelAttribute("accountDetailFormList")
	public List<AccountDetailForm> getAccountDetailFormList() {
		return AccountDetailBusinessUtil.createDefaultAccountDetailFormList();
	}

	// 明細追加処理(Crud)
	// Tips:
	// Controllerの引数に関してBindingResultは必ずバリデーション対象データの直後の引数となるよう設定しないとうまくバリデートされない。
	@PostMapping("/createAccountDetailForm")
	public String createAccountDetailForm(
			@ModelAttribute("accountDetailFormList") List<AccountDetailForm> accountDetailFormList,
			@ModelAttribute("createAccountDetailForm") @Valid AccountDetailForm createAccountDetailForm,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {

		// 単項目チェック（Beanバリデータ、型変換）
		if (presentationProcessHandleService.hasBindingErrorWithMsgProcess(redirectAttributes, bindingResult)) {
			return NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH;
		}

		// 相関チェック（合計値がマイナスになるなど）
		if (presentationProcessHandleService.hasErrorToAddAccountDetailWithMsgProcess(redirectAttributes,
				accountDetailFormList, createAccountDetailForm)) {
			return NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH;

		}

		// ガード節を通過したチェック済の明細を追加。
		accountDetailFormList.add(createAccountDetailForm);

		return NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH;
	}

	// 明細表示処理(cRud)
	@RequestMapping("/showAccountDetailFormList")
	public String showAccountDetailFormList(Model model,
			@ModelAttribute("accountDetailFormList") List<AccountDetailForm> accountDetailFormList,
			@RequestParam(required = false) String recordId, @ModelAttribute("editIndex") String editIndex,
			RedirectAttributes redirectAttributes) {

		// 相関チェック（recordIdが存在する場合の明細存在など）
		if (presentationProcessHandleService.hasErrorToShowAccountDetailWithMsgProcess(redirectAttributes,
				accountDetailFormList, recordId)) {

			return NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH;

		}

		//画面情報の組立のためのデータバインド処理は可読性の観点から別メソッド化
		addAttributesForAccountDetailFormList(model, recordId, editIndex);

		return NavigationConstants.ACCOUNT_DETAIL_FORM_LIST_TEMPLATENAME;
	}

	// パス指定無しの場合は明細表示画面にリダイレクト
	@RequestMapping("/")
	public String handleRootPath() {

		return NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH;

	}

	// 明細更新処理(crUd) Edit→Update処理処理と遷移

	@PostMapping("/editAccountDetailForm")
	public String editAccountDetailForm(@RequestParam("index") String index, RedirectAttributes redirectAttributes) {

		redirectAttributes.addFlashAttribute("editIndex", index);

		return NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH;

	}

	@PostMapping("/updateAccountDetailForm")
	public String updateAccountDetailForm(@RequestParam("index") String index,
			@ModelAttribute("accountDetailFormList") List<AccountDetailForm> accountDetailFormList,
			@ModelAttribute("updateAccountDetailForm") @Valid AccountDetailForm updateAccountDetailForm,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {

		// 単項目チェック（Beanバリデータ、型変換）
		if (presentationProcessHandleService.hasBindingErrorWithMsgProcess(redirectAttributes, bindingResult)) {
			return NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH;
		}

		// 相関チェック（合計値がマイナスになるなど）
		if (presentationProcessHandleService.hasErrorToUpdateAccountDetailWithMsgProcess(redirectAttributes,
				accountDetailFormList, updateAccountDetailForm, index)) {
			return NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH;
		}

		// チェック対象とならなかった場合は編集された明細を対応する明細に上書き
		accountDetailFormList.set(Integer.parseInt(index), updateAccountDetailForm);

		return NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH;

	}

	// 明細削除処理(cruD)
	@PostMapping("/removeAccountDetailForm")
	public String removeAccountDetailForm(@RequestParam("index") String index,
			@ModelAttribute("accountDetailFormList") List<AccountDetailForm> accountDetailFormList,
			RedirectAttributes redirectAttributes) {

		// 相関チェック（合計値がマイナスになるなど）
		if (presentationProcessHandleService.hasErrorToRemoveAccountDetailWithMsgProcess(redirectAttributes,
				accountDetailFormList, index)) {
			return NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH;
		}

		accountDetailFormList.remove(Integer.parseInt(index));

		return NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH;
	}

	// 明細のDBへの保存処理
	@PostMapping("/saveAccountDetailFormList")
	public String saveAccountDetailFormList(Model model,
			@ModelAttribute("accountDetailFormList") List<AccountDetailForm> accountDetailFormList,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {

		String recordId = DateUUIDGeneratorUtil.generateDateUUID();

		accountDetailRepository.saveAccountDetailFormList(recordId, accountDetailFormList);

		presentationProcessHandleService.addNotificationMessage(redirectAttributes, "ui.SaveDataCompletion");

		redirectAttributes.addFlashAttribute("recordId", recordId);

		return NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_WITH_RECORDID_PATH;

	}

	// 保存後にレコードIDをURLに埋め込むための処理
	@GetMapping("/addRecordId")
	public String addRecordId(Model model, @ModelAttribute("recordId") String recordId,
			RedirectAttributes redirectAttributes) {

		// 前処理で登録したメッセージを画面描画で表示するために受け渡し用。（前処理のフラッシュスコープはここで終わりのため）
		redirectAttributes.addFlashAttribute("errorMessages", model.getAttribute("errorMessages"));
		redirectAttributes.addFlashAttribute("notificationMessages", model.getAttribute("notificationMessages"));

		// クエリストリングを付与したURLを構築
		return UriComponentsBuilder.fromPath(NavigationConstants.REDIRECT_SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH)
				.queryParam("recordId", recordId).toUriString();

	}

	// 画面出力内容の構築処理
	public void addAttributesForAccountDetailFormList(Model model, String recordId, String editIndex) {

		Map<String, String> fieldMap = new LinkedHashMap<>();
		Map<String, Long> valueMap = new LinkedHashMap<>();
		List<AccountDetailForm> accountDetailFormList;

		// URLにレコードIDがあれば、DBから読み込んで表示。
		if (StringUtils.hasText(recordId)) {
			accountDetailFormList = accountDetailRepository.loadAccountDetailFormList(recordId);
		} else {
			accountDetailFormList = (List<AccountDetailForm>) model.getAttribute("accountDetailFormList");
		}

		// 編集ボタン契機で編集対象となった勘定明細を編集対象明細とする。
		if (StringUtils.hasText(editIndex)) {
			model.addAttribute("editIndex", Integer.parseInt(editIndex));
			model.addAttribute("updateAccountDetailForm", accountDetailFormList.get(Integer.parseInt(editIndex)));
		}

		// 集計
		fieldMap.put("Revenue", "売上");
		valueMap.put("Revenue",
				netIncomeCalcService.aggregateByCategory(accountDetailFormList, AccountCategories.Revenue));

		fieldMap.put("NessesaryExpenses", "必要経費");
		valueMap.put("NessesaryExpenses",
				netIncomeCalcService.aggregateByCategory(accountDetailFormList, AccountCategories.NecessaryExpenses));

		fieldMap.put("incomeAmount", "所得総額");
		valueMap.put("incomeAmount", netIncomeCalcService.calcIncomeAmount(accountDetailFormList));

		fieldMap.put("IncomeDeduction", "所得控除総額");
		valueMap.put("IncomeDeduction",
				netIncomeCalcService.aggregateByCategory(accountDetailFormList, AccountCategories.IncomeDeduction));

		fieldMap.put("taxableIncomeAmount", "課税所得");
		valueMap.put("taxableIncomeAmount", netIncomeCalcService.calcTaxableIncomeAmount(accountDetailFormList));

		fieldMap.put("incomeTaxAmount", "(a)所得税");
		valueMap.put("incomeTaxAmount", netIncomeCalcService.calcIncomeTaxAmount(accountDetailFormList));

		fieldMap.put("residentTaxAmount", "(b)住民税");
		valueMap.put("residentTaxAmount", netIncomeCalcService.calcResidentTaxAmount(accountDetailFormList));

		fieldMap.put("specialReconstructionIncomeTaxAmount", "(c)特別復興所得税");
		valueMap.put("specialReconstructionIncomeTaxAmount",
				netIncomeCalcService.calcSpecialReconstructionIncomeTaxAmount(accountDetailFormList));

		fieldMap.put("incomeAndResidentAndSpecialIncomeTaxAmount", "税総額(a)+(b)+(c)");
		valueMap.put("incomeAndResidentAndSpecialIncomeTaxAmount",
				netIncomeCalcService.calcIncomeAndResidentAndSpecialIncomeTaxAmount(accountDetailFormList));

		fieldMap.put("TaxDeduction", "税額控除総額");
		valueMap.put("TaxDeduction",
				netIncomeCalcService.aggregateByCategory(accountDetailFormList, AccountCategories.TaxDeduction));

		fieldMap.put("reservedValueBalanceAmount", "手残り(≠可処分所得)");
		valueMap.put("reservedValueBalanceAmount",
				netIncomeCalcService.calcReservedValueBalanceAmount(accountDetailFormList)
						- netIncomeCalcService.calcIncomeAndResidentAndSpecialIncomeTaxAmount(accountDetailFormList));

		// 集計情報の設定
		model.addAttribute("fieldMap", fieldMap);
		model.addAttribute("valueMap", valueMap);

		// 一覧情報の設定
		model.addAttribute("accountDetailFormList", accountDetailFormList);
		model.addAttribute("createAccountDetailForm",
				new AccountDetailForm(AccountCategories.Revenue, "入力してください", 10000L, 12L, false));

	}
}
